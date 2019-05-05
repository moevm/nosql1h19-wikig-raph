package com.wikiparser

import com.google.gson.JsonParser
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import freemarker.cache.*
import io.ktor.freemarker.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import kotlinx.coroutines.runBlocking


fun main(args: Array<String>)
{
    val server = embeddedServer(
        Jetty,
        port = 8080,
        module = Application::module
    ).apply {
        start(wait = false)
    }
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "/templates")
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }



    val client = HttpClient(Apache)




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


                val wikipediaApiRequest = HttpRequestBuilder()
                wikipediaApiRequest.host = "wikipedia.com"
                wikipediaApiRequest.port = 80
                wikipediaApiRequest.method = HttpMethod("http")
                wikipediaApiRequest.url.path("w", "api.php")
                wikipediaApiRequest.parameter("action", "query")
                wikipediaApiRequest.parameter("format", "json")
                wikipediaApiRequest.parameter("prop", "categories")
                wikipediaApiRequest.parameter("titles", "Adolf Hitler")
                wikipediaApiRequest.parameter("cllimit", "max")
                println(wikipediaApiRequest.build().url)
                response = client.get<String>(wikipediaApiRequest)
            }

            call.respond(JsonParser().parse(response))
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

