package com.wikiparser


import com.wikiparser.api.api
import com.wikiparser.site.module
import com.wikiparser.tools.Settings

import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty


fun main()
{

    println(System.getProperty("java.io.tmpdir"))
    var envApi = applicationEngineEnvironment {
        module{
            api()
        }

        connector {
            host = Settings.getApiHost()
            port = Settings.getApiPort()
        }
    }

    var envSite = applicationEngineEnvironment {
        module{
            module()
        }

        connector {
            host = Settings.getSiteHost()
            port = Settings.getSitePort()
        }
    }

    val apiServer = embeddedServer(Jetty, envApi)
    val siteServer = embeddedServer(Jetty, envSite)

    siteServer.start(wait = false)
    apiServer.start(wait = true)
}


