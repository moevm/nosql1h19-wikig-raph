package com.wikiparser.site

import com.wikiparser.clients.WikipediaApiClient
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.ContentType
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.coroutines.runBlocking

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "/templates")
    }



    routing {
        get("/") {
            //call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
            val indexPageRes = javaClass.getResourceAsStream("/html/index.html")

            call.respondText(indexPageRes.reader().readText(), ContentType.Text.Html)
        }

        get("/html-freemarker") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))

        }

        get("/getHitler") {
            var response = ""
            runBlocking{

                call.respond(WikipediaApiClient.getLinks(""))
            }


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