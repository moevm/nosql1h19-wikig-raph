package com.wikiparser

import com.google.gson.Gson
import com.google.gson.JsonObject
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
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import kotlinx.coroutines.runBlocking
import com.wikiparser.clients.Neo4jClient
import com.wikiparser.clients.WikipediaApiClient


fun main(args: Array<String>)
{
    val testJson = "{\n" +
            "    \"continue\": {\n" +
            "        \"plcontinue\": \"2731583|0|Garrison_Church_(Potsdam)\",\n" +
            "        \"continue\": \"||categories\"\n" +
            "    },\n" +
            "    \"limits\": {\n" +
            "        \"categories\": 500\n" +
            "    },\n" +
            "    \"query\": {\n" +
            "        \"pages\": {\n" +
            "            \"2731583\": {\n" +
            "                \"pageid\": 2731583,\n" +
            "                \"ns\": 0,\n" +
            "                \"title\": \"Adolf Hitler\",\n" +
            "                \"links\": [\n" +
            "                    {\n" +
            "                        \"ns\": 0,\n" +
            "                        \"title\": \"F\\u00fchrersonderzug\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"ns\": 0,\n" +
            "                        \"title\": \"Gabriele D'Annunzio\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"ns\": 0,\n" +
            "                        \"title\": \"Galeazzo Ciano\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"ns\": 0,\n" +
            "                        \"title\": \"Ganap Party\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"ns\": 0,\n" +
            "                        \"title\": \"Garmisch-Partenkirchen\"\n" +
            "                    }\n" +
            "                ]\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}"

    var testJsonStr = "        {\"links\": [\n" +
            "          {\n" +
            "            \"ns\": 0,\n" +
            "            \"title\": \"1934 Montreux Fascist conference\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"ns\": 0,\n" +
            "            \"title\": \"1936 Summer Olympics\"\n" +
            "          }]}"

    var testJsonArray = JsonParser().parse(testJsonStr).asJsonObject.getAsJsonArray("links")

    val test = JsonParser().parse(testJson).asJsonObject.get("continue").asJsonObject.get("continue").asString

    var testJsonObject = JsonParser().parse(testJson)
    val wtf = testJsonObject.asJsonObject
        .getAsJsonObject("query").getAsJsonObject("pages").keySet().first()
        //.getAsJsonArray("pages")
    val linksList = testJsonObject
        .asJsonObject
        .getAsJsonObject("query")
        .getAsJsonObject("pages")
        .getAsJsonObject(wtf)
        .getAsJsonArray("links")
        .addAll(testJsonArray)




     var env = applicationEngineEnvironment {
        module{
            module()
            api()
        }

        connector {
            host = "0.0.0.0"
            port = 8080
        }
    }
    val server = embeddedServer(Jetty, env)
    server.start(wait = true)
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



fun Route.getTitle() {

    get("/title")
    {
        val titleName = call.parameters["titleName"]
        val doStore = call.parameters["doStore"]
        if (titleName != null) {
            val client = HttpClient(Apache)
            var response = JsonObject()

            runBlocking {
                response = WikipediaApiClient.getLinks(titleName)
            }

            if (doStore != null) {
                if (doStore == "true") {
                    Neo4jClient.getInstance("bolt://localhost.ml:7687", "neo4j", "h1tlerTRACE")
                        .addTitle(response)

                }
            }

            call.respond(response)
        }
    }

    get("/getLinks")
    {
        val titleName = call.parameters["titleName"].toString()


        val result = Neo4jClient.getInstance("bolt://localhost.ml:7687", "neo4j", "h1tlerTRACE")
            .getLinks(titleName)

        call.respond(result)
    }
}

fun Routing.api()
{

    route("/api")
    {
        getTitle()
    }
}

fun Application.api()
{



    routing {
        api()
    }
}

data class IndexData(val items: List<Int>)

