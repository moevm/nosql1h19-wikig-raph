package com.wikiparser.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.wikiparser.api.clients.GephiClient
import com.wikiparser.clients.Neo4jClient
import com.wikiparser.clients.WikipediaApiClient
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.util.flattenForEach
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.system.measureNanoTime


fun checkGraphToDepth(startArticle : String, depth : Int)
{
    var currDepth = depth


    var nodesForIteration = ArrayList<String>()
    nodesForIteration.add(startArticle)

    while(currDepth > 0)
    {
        var nodesFromIteration = ArrayList<String>()

        nodesForIteration.forEach {
            currNode ->

            val titleId = Neo4jClient.getTitleId(currNode)
            var links = Neo4jClient.getLinks(currNode)

            // TODO: There is potential error where article have no any links but we trying to parse it over and over
            if((titleId == null) || (links.size() == 0))
            {

                println("Article: $currNode :: Not in database")

                runBlocking{
                    println("--Trying to get links from wikipedia API...")

                    val links = WikipediaApiClient.getLinks(currNode)

                    println("--Trying to store links into NEO4J...")
                    val time = measureNanoTime{
                        Neo4jClient.addTitle(links)
                    }
                    println("--Done! time: $time")
                }

                links = Neo4jClient.getLinks(currNode)
            }
            else
            {
                println("Article: $currNode :: Already in database")
            }



            links.forEach {
                nodesFromIteration.add(
                    it.asJsonObject
                        .getAsJsonPrimitive("link")
                        .asString
                )
            }

        }

        nodesForIteration = nodesFromIteration

        currDepth--
    }
}

fun parametersChecker(parameters : Parameters)
{

    parameters.forEach { s, list ->
        parameterHandler(s, list)
    }

}

fun parameterHandler(parameter : String, value : List<String>)
{
    when (parameter)
    {
        "article" -> "hello"
    }
}

fun Routing.apiRoutes()
{
    get("/"){
        call.respondText("Yes I AM #_$")
    }
    getLinks()
    getTitle()
    getLinkedArticles()
    getOutgoingRelations()
    getIncomingRelations()
    getCountOfArticles()
}

fun Route.getLinks()
{
    get("/getLinks")
    {
        val titleName = call.parameters["titleName"].toString()


        val result = Neo4jClient.getLinks(titleName)


        call.respond(result)
    }
}

fun Route.getTitle() {

    get("/title/")
    {
        val titleName = call.parameters["article"]
        val parameters = call.parameters

        println(call.request.headers.forEach { s, list -> println(s); println(list)  })
        if (titleName != null) {
            var response = JsonObject()

            runBlocking {
                response = WikipediaApiClient.getLinks(titleName)
            }


            //TODO: Check if should parse node
            if(!Neo4jClient.isTitleExist(titleName))
                //Neo4jClient.addTitle(response)


            call.respond(response)
        }
    }


}

fun Route.getLinkedArticles()
{
    get("/linkedarticles")
    {
        val depth = call.parameters["depth"]?.toInt() ?: 1
        val startArticle = call.parameters["startArticle"] ?: "none"
        val secondsForProcessing = call.parameters["processfor"]?.toLong() ?: 60


        call.application.environment.log.info("Trying to get $startArticle linked articles to depth $depth")
        call.application.environment.log.info("Start graph checking...")
        val time = measureNanoTime{
            checkGraphToDepth(startArticle, depth)
        } / 1000000
        call.application.environment.log.info("------store time: $time")
        call.application.environment.log.info("Storing result graph to file for further read by GEPHI...")
        Neo4jClient.storeLinkedGraphToDepthFromArticle(
            startArticle,
            depth,
            "tmpgraph.graphml",
            System.getProperty("java.io.tmpdir")
        )

        call.application.environment.log.info("GEPHI trying to load file...")
        call.respond(GephiClient.processGraphToSigmaJsonString(
            "tmpgraph.graphml",
            System.getProperty("java.io.tmpdir"),
            secondsForProcessing
            ))

    }
}

fun Route.getOutgoingRelations(){
    get("/outgoingRelations"){
        val result = Neo4jClient.outgoingRelations()
        call.respond(result)
    }
}

fun Route.getIncomingRelations(){
    get("/incomingRelations"){
        val result = Neo4jClient.incomingRelations()
        call.respond(result)
    }
}

fun Route.getCountOfArticles(){
    get("/countOfArticles"){
        val result = Neo4jClient.getCountOfArticles()
        call.respond(result)
    }
}