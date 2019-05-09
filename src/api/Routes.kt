package com.wikiparser.api

import com.google.gson.JsonObject
import com.wikiparser.clients.Neo4jClient
import com.wikiparser.clients.WikipediaApiClient
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlinx.coroutines.runBlocking

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

    get("/title")
    {
        val titleName = call.parameters["titleName"]
        val doStore = call.parameters["doStore"]
        if (titleName != null) {
            var response = JsonObject()

            runBlocking {
                response = WikipediaApiClient.getLinks(titleName)
            }

            if (doStore != null) {
                if (doStore == "true") {
                    Neo4jClient.addTitle(response)

                }
            }

            call.respond(response)
        }
    }
}