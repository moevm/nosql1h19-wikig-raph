package com.wikiparser.tools
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object Settings {
    private val config : Config = ConfigFactory.load()
    private val neo4jConfig : Config = config.getConfig("neo4j")
    private val apiServerConfig : Config = config.getConfig("api-server")
    private val siteServerConfig : Config = config.getConfig("site-server")

    init {
        config.checkValid(ConfigFactory.defaultReference(), "neo4j")
        config.checkValid(ConfigFactory.defaultReference(), "api-server")
        config.checkValid(ConfigFactory.defaultReference(), "site-server")
        println("Config was initialized!")
    }

    fun getNeo4jPort() : Int
    {
        return neo4jConfig?.getInt("port")
    }

    fun getNeo4jHost() : String
    {
        return neo4jConfig.getString("host")
    }

    fun getNeo4jLogin() : String
    {
        return neo4jConfig.getString("credentials.login")
    }

    fun getNeo4jPassword() : String
    {
        return neo4jConfig.getString("credentials.password")
    }

    fun getApiHost() : String
    {
        return apiServerConfig.getString("host")
    }

    fun getApiPort() : Int
    {
        return apiServerConfig.getInt("port")
    }

    fun getSiteHost() : String
    {
        return siteServerConfig.getString("host")
    }

    fun getSitePort() : Int
    {
        return siteServerConfig.getInt("port")
    }
}