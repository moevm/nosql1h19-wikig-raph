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


        title.has("title")
        title.has("id")
        title.has("size")

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
        val baseTitle = title.getAsJsonPrimitive("title").asString
        val id = title.getAsJsonPrimitive("id").asString



        var categoriesList = ArrayList<String>()
        categories.forEach {
            categoriesList.add(it.asJsonObject.getAsJsonPrimitive("title").asString)
        }

        driver.session().beginTransaction().use{ tx->

            tx.run("MERGE (base:Article {articleTitle: {title}, categories: {categories}, size: {size}})",
                parameters("title", baseTitle, "categories", categoriesList, "size", size))

            if(links == null)
            {
                tx.success()
                return true
            }

            repeat(links.size) {
                val currTitle = links[it].asJsonObject.getAsJsonPrimitive("title").asString
                tx.run("MERGE (child:Article {articleTitle: {title}})", parameters("title", currTitle))
                tx.run("MATCH (baseArticle:Article { articleTitle: {baseTitle} })," +
                        "(childArticle:Article { articleTitle: {childTitle} }) " +
                        "MERGE (baseArticle)-[r:ArticleLink]->(childArticle)" +
                        "RETURN baseArticle.articleTitle, type(r), childArticle.articleTitle",
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
                    "CALL apoc.export.graphml.query('MATCH path = (:Article{articleTitle:\"${startArticle}\"})-[*1..$depth]->(:Article)\n" +
                            "WITH collect(path) AS paths RETURN paths',\'${resultFilePath.replace('\\', '/') + "/" + resultFileName}\', {useTypes:true, storeNodeIds:false, caption:[\"articleTitle\"], format:\"gephi\"})"
                )

                tx.success()

            }
        }
    }

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