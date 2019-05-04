package com.wikiparser

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import freemarker.cache.*
import io.ktor.freemarker.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resolveResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.jetty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "/templates")
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    val client = HttpClient(Apache) {
    }



    routing {
        get("/") {
            //call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
            val indexStr = File(javaClass.getResource("/html/index.html").toURI()).readText()
            call.respondText(indexStr, ContentType.Text.Html)
        }

        get("/html-freemarker") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }

        static ("/css"){
            // This marks index.html from the 'web' folder in resources as the default file to serve.
            defaultResource("index.html", "html")
            // This serves files from the 'web' folder in the application resources.
            resources("css")
        }

        static ("/js") {
            resources("js")
        }

        static ("/html") {
            resources("html")
        }
    }

}

data class IndexData(val items: List<Int>)

