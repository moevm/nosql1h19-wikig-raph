package com.wikiparser.tools
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object Settings {
    private val config : Config = ConfigFactory.load()

    init {
        config.checkValid(ConfigFactory.defaultReference(), "neo4j")
        println("Config was initialized!")
    }

    fun getNeo4jPort() : Int
    {
        return config?.getInt("neo4j.port")
    }

    fun getNeo4jHost() : String
    {
        return config?.getString("neo4j.host")
    }

    fun getNeo4jLogin() : String
    {
        return config?.getString("neo4j.credentials.login")
    }

    fun getNeo4jPassword() : String
    {
        return config?.getString("neo4j.credentials.password")
    }
}