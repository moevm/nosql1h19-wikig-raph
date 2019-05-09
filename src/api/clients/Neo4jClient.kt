package com.wikiparser.clients

import com.wikiparser.tools.Settings

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.neo4j.driver.v1.*

import org.neo4j.driver.v1.Values.parameters
import java.net.URI

object Neo4jClient {
    // Driver objects are thread-safe and are typically made available application-wide.

    private var driver : Driver

    init {

        val uri = "bolt://${Settings.getNeo4jHost()}:${Settings.getNeo4jPort()}"
        driver= GraphDatabase.driver(uri,
            AuthTokens.basic(
                Settings.getNeo4jLogin(), Settings.getNeo4jPassword()))
    }

    fun addTitle(title : JsonObject?)
    {
        if(title == null)
        {
            return
        }


        val links = title.getAsJsonArray("links").toList()
        val categories = title.getAsJsonArray("categories").toList()
        val baseTitle = title.getAsJsonPrimitive("title").asString
        val id = title.getAsJsonPrimitive("id").asString



        driver.session().beginTransaction().use{ tx->

            tx.run("MERGE (base:Article {articleTitle: {title}})", parameters("title", baseTitle))

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
    }

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