package com.wikiparser.site

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.UserPasswordCredential
import io.ktor.auth.form
import io.ktor.freemarker.FreeMarker
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.routing
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie

data class MySession(val username: String)

fun Application.module() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "/templates")
    }

    install(Sessions) {
        cookie<MySession>("SESSION")
    }

    install(Authentication) {
        form  {
            userParamName = "name"
            passwordParamName = "password"
            validate { up: UserPasswordCredential ->
                when {
                    up.password == "admin" && up.name == "admin" -> UserIdPrincipal(up.name)
                    else -> null
                }
            }
        }
    }

    routing {

        siteRoutes()

        static ("/css"){
            resources("css")
        }

        static ("/js") {
            resources("js")
        }

        static ("/html") {
            resources("html")
        }
        
        static ("/img") {
            resources("img")
        }
        
        static ("/font") {
            resources("font")
        }
        
        static ("/scss") {
            resources("scss")
        }
    }

}





data class IndexData(val items: List<Int>)
