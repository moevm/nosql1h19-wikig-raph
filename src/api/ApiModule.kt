package com.wikiparser.api

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson

import io.ktor.routing.routing
import java.time.Duration

fun Application.api()
{
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    install(CORS)
    {
        //header(HttpHeaders.XForwardedProto)
        anyHost()
        allowCredentials = true
        maxAge = Duration.ofDays(1)

    }


    routing {
        apiRoutes()
    }
}