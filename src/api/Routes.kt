package com.wikiparser.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
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
import kotlinx.coroutines.runBlocking


fun parseGraphToDepth(startArticle : String, depth : Int) : JsonArray
{
    var currDepth = depth

    var resultJson = JsonArray()


    var nodesForIteration = ArrayList<String>()
    nodesForIteration.add(startArticle)

    while(currDepth > 0)
    {
        var nodesFromIteration = ArrayList<String>()

        nodesForIteration.forEach {
            currNode->

            val titleId = Neo4jClient.getTitleId(currNode)
            var links = Neo4jClient.getLinks(currNode)
            if((titleId != null) && (links.size() > 0))
            {
                links = Neo4jClient.getLinks(currNode)
            }
            else
            {
                runBlocking{
                    Neo4jClient.addTitle(
                        WikipediaApiClient.getLinks(currNode)
                    )
                }

                links = Neo4jClient.getLinks(currNode)

            }
            val resultNode = JsonObject()

            resultNode.addProperty("id", titleId)
            resultNode.add("point_to", links)

            resultJson.add(resultNode)

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

    return resultJson
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
                Neo4jClient.addTitle(response)


            call.respond(response)
        }
    }

    get("/parse/")
    {
        val depth = call.parameters["depth"] ?: "none"
        val startArticle = call.parameters["startArticle"] ?: "none"
        call.respond(parseGraphToDepth(startArticle, depth.toInt()))
    }
}