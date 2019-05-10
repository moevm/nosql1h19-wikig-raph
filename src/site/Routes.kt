package com.wikiparser.site

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.ContentType
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.sessions.get
import io.ktor.sessions.sessions

fun checkAuth(call : ApplicationCall) : Boolean
{
    val session = call.sessions.get<MySession>()
    return session != null
}

fun Routing.siteRoutes()
{
    admin()
    logout()
    login()
    index()
}

fun Route.logout()
{
    route("/logout")
    {
        get {
            call.sessions.clear("SESSION")
            call.respondRedirect("/")
        }
    }
}

fun Route.admin()
{
    route("/admin")
    {
        get{
            if(checkAuth(call))
            {
                call.respond(FreeMarkerContent("admin.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
            }
            else
            {
                call.respondRedirect("/login")
            }
        }
    }
}

fun Route.login()
{
    route("/login") {

        get{
            if (checkAuth(call)) {
                call.respondRedirect("/")
            } else {
                call.respond(FreeMarkerContent("login.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
            }
        }
        authenticate {

            post{
                println(call.receiveParameters().toString())
                val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                call.sessions.set("SESSION", MySession(principal.name))
                call.respondRedirect("/", permanent = false)
            }
        }
    }
}

fun Route.index() {

    get("/") {
        //call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        val indexPageRes = javaClass.getResourceAsStream("/html/index.html")

        call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
    }
}