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
import io.ktor.auth.*
import io.ktor.routing.route
import kotlinx.coroutines.runBlocking

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "/templates")
    }

    install(Authentication) {
        basic {
            realm = "admin"
            validate {
                credentials ->
                if(credentials.name == credentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }

    }



    routing {
        authenticate {

            route("/admin") {
                get("/") {
                    call.respondText("Success, ${call.principal<UserIdPrincipal>()?.name}")
                }
            }
        }

        siteRoutes()

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