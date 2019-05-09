package com.wikiparser.api

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.routing.routing

fun Application.api()
{
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }


    routing {
        apiRoutes()
    }
}