package com.wikiparser.clients

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.wikiparser.tools.Settings
import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.GraphDatabase
import org.neo4j.driver.v1.Values.parameters


object Neo4jClient {
    // Driver objects are thread-safe and are typically made available application-wide.

    private var driver : Driver

    init {

        val uri = "bolt://${Settings.getNeo4jHost()}:${Settings.getNeo4jPort()}"
        driver= GraphDatabase.driver(uri,
            AuthTokens.basic(
                Settings.getNeo4jLogin(), Settings.getNeo4jPassword()))
    }

    fun addTitle(title : JsonObject?) : Boolean
    {
        if(title == null)
        {
            return false
        }

        var links = listOf<JsonElement>()
        var categories = listOf<JsonElement>()
        var size = -1

        if(title.has("links"))
        {
            if(title.get("links").isJsonArray)
            {
                links = title.getAsJsonArray("links").toList()
            }
        }

        if(title.has("categories"))
        {
            if(title.get("categories").isJsonArray) {
                categories = title.getAsJsonArray("categories").toList()
            }
        }

        if(title.has("size"))
        {
            if(title.get("size").isJsonPrimitive)
            {
                size = title.getAsJsonPrimitive("size").asString.toInt()
            }
        }
        val titleList = links.map{
            it.asJsonObject.get("title")
        }
        val titleListString = titleList.joinToString(", ", "[", "]")

        val baseTitle = title.getAsJsonPrimitive("title").asString
        val id = title.getAsJsonPrimitive("id").asString

        driver.session().beginTransaction().use { tx->
            val result = tx.run("MATCH (base:Article)\n" +
                    "WHERE base.articleTitle IN $titleListString\n" +
                    "RETURN base.articleTitle as title")

            var existsArticles: Array<String> = arrayOf()
            while (result.hasNext()) {
                existsArticles = existsArticles.plus(result.next().get("title").asString())
            }

            tx.run(
                "MERGE (base:Article {articleTitle: {title}}) " +
                        "SET base.size = {size}",
                parameters("title", baseTitle, "size", size)
            )

            categories.forEach{
                val category = it.asJsonObject.getAsJsonPrimitive("title").asString
                tx.run(
                    "MERGE (category:Category{categoryTitle: {categoryTitle}})",
                    parameters("categoryTitle", category)
                )
                tx.run(
            "MATCH (baseArticle:Article { articleTitle: {baseTitle} }), " +
                    "(category:Category { categoryTitle: {categoryTitle} })" +
                    "MERGE (category)-[:CategoryLink]->(baseArticle)",
                    parameters("baseTitle", baseTitle,
                        "categoryTitle", category)
                )
            }
            repeat(links.size) {
                val currTitle = links[it].asJsonObject.getAsJsonPrimitive("title").asString
                if (currTitle !in existsArticles)
                {
                    tx.run(
                        "CREATE (child:Article {articleTitle: {title}})",
                        parameters("title", currTitle))
                }

                tx.run(
                    "MATCH (baseArticle:Article { articleTitle: {baseTitle} })," +
                    "(childArticle:Article { articleTitle: {childTitle} }) " +
                    "MERGE (baseArticle)-[r:ArticleLink]->(childArticle)",
                    parameters("baseTitle", baseTitle, "childTitle", currTitle))
            }

            tx.success()
        }

        return true
    }


    fun storeLinkedGraphToDepthFromArticle(startArticle : String, depth: Int, resultFileName : String, resultFilePath : String = "/")
    {
        driver.session().use {
            it.beginTransaction().use { tx ->

                val result = tx.run(
                    "CALL apoc.export.graphml.query('MATCH path = (:Article{articleTitle:\"$startArticle\"})-[*1..$depth]->(:Article)\n" +
                            "WITH collect(path) AS paths RETURN paths',\'${resultFilePath.replace('\\', '/') + "/" + resultFileName}\', {useTypes:true, storeNodeIds:false, caption:[\"articleTitle\"], format:\"gephi\"})"
                )

                tx.success()

            }
        }
    }

    fun getCategoryArticles(category : String, resultFileName : String, resultFilePath : String = "/")
    {
        driver.session().use {
            it.beginTransaction().use { tx ->

                tx.run(
                    "CALL apoc.export.graphml.query(" +
                            "'MATCH (n:Category{categoryTitle:\"$category\"})-->(a), " +
                            "(n:Category{categoryTitle:\"$category\"})-[*0..1]->(b)\n" +
                            "OPTIONAL MATCH (a)-[r]->(b)\n" +
                            "RETURN a,b,r;', \'${resultFilePath.replace('\\', '/') + "/" + resultFileName}\', " +
                            "{useTypes:true, storeNodeIds:false, caption:[\"articleTitle\"], format:\"gephi\"})"
                )
//                tx.run(
//                    "CALL apoc.export.graphml.query('MATCH (:Category{categoryTitle:\"$category\"})-->(n:Article)\n" +
//                            "return n', \'${resultFilePath.replace('\\', '/') + "/" + resultFileName}\', {useTypes:true, storeNodeIds:false, caption:[\"categoryTitle\"], format:\"gephi\"})"
//                )

                tx.success()

            }
        }
    }

    fun getAllShortestPaths(startArticle: String, finishArticle: String, depth: Int, resultFileName : String, resultFilePath : String = "/")
    {
        val filePath = resultFilePath.replace('\\', '/') + "/" + resultFileName
        println(filePath)
        println()
        driver.session().use {
            it.beginTransaction().use { tx ->
                tx.run(
                    "CALL apoc.export.graphml.query('MATCH (start:Article{articleTitle:\"$startArticle\"})," +
                            "(finish:Article{articleTitle:\"$finishArticle\"})," +
                            "path = allShortestPaths( (start)-[*..$depth]-(finish) )\n" +
                            "RETURN path', \'${filePath}', " +
                            "{useTypes:true, storeNodeIds:false, caption:[\"articleTitle\"], format:\"gephi\"})"
                )

                tx.success()

            }
        }
    }

    // TODO: Store category
    // w/api.php?action=query&list=categorymembers&cmtitle=Category:{categoryTitle}&cmlimit=max&cmnamespace=0&format=json

    // TODO: There is no reason anymore to be JSON Array as return value for this one
    fun getLinks(title : String) : JsonArray
    {

        val resultArray = JsonArray()
        val result = driver.session().use {
            it.beginTransaction().use{ tx->

                val result = tx.run("MATCH (:Article {articleTitle: {title}})-->(link)" +
                       "RETURN id(link), link.articleTitle", parameters("title", title))

                tx.success()

                while (result.hasNext()) {

                    val record = result.next()
                    // Values can be extracted from a record by index or name.
                    val tmpJsonObj = JsonObject()
                    tmpJsonObj.addProperty("id", record.get("id(link)").toString())
                    tmpJsonObj.addProperty("link", record.get("link.articleTitle").asString())
                    resultArray.add(tmpJsonObj)
                }
            }
        }

        return resultArray
    }

    fun getTitleId(title : String) : Int ?
    {
        driver.session().use {
            it.beginTransaction().use { tx ->

                val result = tx.run(
                    "MATCH (article:Article {articleTitle: {title}})" +
                            "RETURN id(article)", parameters("title", title)
                )

                tx.success()
                if (result.hasNext())
                    return result.next().get("id(article)").asInt()
                else
                    return null
            }
        }
    }

    fun getCountOfArticles() : Int
    {
        driver.session().use {
            it.beginTransaction().use { tx ->

                val result = tx.run(
                    "MATCH (article:Article)" +
                            "RETURN COUNT(article)")

                tx.success()
                if (result.hasNext())
                    return result.next().get("COUNT(article)").asInt()
                else
                    return 0
            }
        }
    }

    fun exportToFile(resultFileName : String, resultFilePath : String = "/") {
        val filePath = resultFilePath.replace('\\', '/') + resultFileName
        driver.session().use {
            it.beginTransaction().use { tx ->
                tx.run (
                    "CALL apoc.export.graphml.all('$filePath', " +
                            "{useTypes:true})"
                )
                tx.success()

            }
        }
    }

    fun importFromFile(resultFileName : String, resultFilePath : String = "/") {
//        val filePath = resultFilePath.replace('\\', '/') + resultFileName
//        println(filePath)
        driver.session().use {
            it.beginTransaction().use { tx ->
                tx.run (
                    "CALL apoc.import.graphml('$resultFileName', " +
                            "{batchSize: 10000, readLabels: true, storeNodeIds: false})"
                )
                tx.success()

            }
        }
    }

    fun outgoingRelations() : JsonArray
    {
        val resultArray = JsonArray()
        driver.session().use {
            it.beginTransaction().use{ tx->

                val result = tx.run("MATCH (n:Article)\n" +
                        "WITH size((n)-->()) AS count_of_outgoing_relations\n" +
                        "RETURN count_of_outgoing_relations AS outgoing_vertexes, " +
                        "COUNT(count_of_outgoing_relations) AS count_\n" +
                        "ORDER BY outgoing_vertexes;")

                tx.success()

                while (result.hasNext()) {

                    val record = result.next()
                    // Values can be extracted from a record by index or name.
                    val tmpJsonObj = JsonObject()
                    tmpJsonObj.addProperty("Count of outgoing relations", record.get("outgoing_vertexes").toString())
                    tmpJsonObj.addProperty("Count of vertexes", record.get("count_").toString())
                    resultArray.add(tmpJsonObj)
                }
            }
        }

        return resultArray
    }

    fun incomingRelations() : JsonArray
    {
        val resultArray = JsonArray()
        driver.session().use {
            it.beginTransaction().use{ tx->

                val result = tx.run("MATCH (n:Article)\n" +
                        "WITH size(()-[:ArticleLink]->(n)) AS count_of_incoming_relations\n" +
                        "RETURN count_of_incoming_relations AS incoming_vertexes, " +
                        "COUNT(count_of_incoming_relations) AS count_\n" +
                        "ORDER BY count_of_incoming_relations;")

                tx.success()

                while (result.hasNext()) {

                    val record = result.next()
                    // Values can be extracted from a record by index or name.
                    val tmpJsonObj = JsonObject()
                    tmpJsonObj.addProperty("Count of incoming relations", record.get("incoming_vertexes").toString())
                    tmpJsonObj.addProperty("Count of vertexes", record.get("count_").toString())
                    resultArray.add(tmpJsonObj)
                }
            }
        }

        return resultArray
    }

    fun isTitleExist(title : String) : Boolean
    {
        return getTitleId(title) != null
    }

    private fun addPerson(name: String) {
        // Sessions are lightweight and disposable connection wrappers.
        driver.session().use { session ->
            // Wrapping Cypher in an explicit transaction provides atomicity
            // and makes handling errors much easier.
            session.beginTransaction().use { tx ->
                tx.run("MERGE (a:Person {name: {x}})", parameters("x", name))
                tx.success()  // Mark this write as successful.
            }
        }
    }

    private fun printPeople(initial: String) {
        driver.session().use { session ->
            // Auto-commit transactions are a quick and easy way to wrap a read.
            val result = session.run(
                "MATCH (a:Person) WHERE a.name STARTS WITH {x} RETURN a.name AS name",
                parameters("x", initial)
            )
            // Each Cypher execution returns a stream of records.
            while (result.hasNext()) {
                val record = result.next()
                // Values can be extracted from a record by index or name.
                println(record.get("name").asString())
            }
        }
    }

    fun close() {
        // Closing a driver immediately shuts down all open connections.
        driver.close()
    }

}