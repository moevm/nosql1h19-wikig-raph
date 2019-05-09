package com.wikiparser.site

import com.google.gson.JsonObject
import com.wikiparser.clients.Neo4jClient
import com.wikiparser.clients.WikipediaApiClient
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import kotlinx.coroutines.runBlocking

fun Routing.siteRoutes()
{

    login()
    index()
}

fun Route.login()
{
    get("/login") {
        call.respond(FreeMarkerContent("login.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
    }
}

fun Route.index() {

    get("/") {
        //call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        val indexPageRes = javaClass.getResourceAsStream("/html/index.html")

        call.respondText(indexPageRes.reader().readText(), ContentType.Text.Html)
    }
}