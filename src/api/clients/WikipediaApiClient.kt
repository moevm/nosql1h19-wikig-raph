package com.wikiparser.clients

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import kotlinx.coroutines.runBlocking

object WikipediaApiClient : AutoCloseable {
    private val client = HttpClient(Apache)
    private val request = HttpRequestBuilder()
    private val url = URLBuilder(
        URLProtocol.HTTP,
        "en.wikipedia.com",
        80
    )


    init{
        url.path("w", "api.php")

        // /w/api.php?action=query&format=json&prop=links%7Ccategories&
        // titles=MyTitle&plnamespace=0&pllimit=max&cllimit=max

        url.parameters.append("action", "query")
        url.parameters.append("format", "json")
        url.parameters.append("prop", "links|categories")
        url.parameters.append("plnamespace", "0")
        url.parameters.append("pllimit", "max")
        url.parameters.append("cllimit", "max")

        request.url(url.build())
    }

    suspend fun getLinks(baseArticleName: String = "Adolf Hitler") : JsonObject
    {
        request.url.parameters.remove("plcontinue")
        request.url.parameters.remove("continue")
        request.url.parameters.remove("titles")
        request.url.parameters.append("titles", baseArticleName)
        var responseStr =  client.get<String>(request)


        var responseJson = JsonParser().parse(responseStr).asJsonObject

        if(responseJson.get("query") == null)
        {
            val errorJson = JsonObject()
            errorJson.addProperty("error", "wrong title")
            return errorJson
        }

        var pageId = extractPageIdFromResponse(responseJson)


        var links = extractLinksFromResponse(responseJson, pageId)
        var categories = extractCategoriesFromResponse(responseJson, pageId)
        var continueObject = extractContinueObject(responseJson)

        while (continueObject != null) {
            val plContinueElement : JsonElement? = continueObject.get("plcontinue")
            if (plContinueElement != null) {
                request.url.parameters.append("plcontinue", plContinueElement.asString)
            }

            val clContinueElement : JsonElement? = continueObject.get("continue")
            if (clContinueElement != null) {
                request.url.parameters.append("continue", clContinueElement.asString)
            }

            runBlocking {
                responseStr = client.get<String>(request)
            }

            responseJson = JsonParser().parse(responseStr).asJsonObject

            if (links != null) {
                val tmpLinks = extractLinksFromResponse(responseJson, pageId)
                if(tmpLinks != null)
                    links.addAll(tmpLinks)
            }

            if (categories != null) {
                val tmpCategories = extractCategoriesFromResponse(responseJson, pageId)
                if(tmpCategories != null)
                    categories.addAll(tmpCategories)
            }

            continueObject = extractContinueObject(responseJson)
        }

        val resultObject : JsonObject = JsonObject()
        resultObject.addProperty("title", baseArticleName)
        resultObject.addProperty("id", pageId)
        resultObject.add("links", links)
        resultObject.add("categories", categories)

        return resultObject
    }



    private fun extractPageIdFromResponse(responseJson : JsonObject) : String
    {
        return responseJson
            .getAsJsonObject("query")
            .getAsJsonObject("pages")
            .keySet()
            .first()
    }

    private fun extractLinksFromResponse(responseJson : JsonObject, pageId : String = "") : JsonArray?
    {
        var currPageId = pageId
        if(pageId == "")
            currPageId = extractPageIdFromResponse(responseJson)

        return responseJson
            .getAsJsonObject("query")
            .getAsJsonObject("pages")
            .getAsJsonObject(pageId)
            .getAsJsonArray("links")
    }

    private fun extractCategoriesFromResponse(responseJson : JsonObject, pageId : String = "") : JsonArray?
    {
        var currPageId = pageId
        if(pageId == "")
            currPageId = extractPageIdFromResponse(responseJson)

        return responseJson
            .getAsJsonObject("query")
            .getAsJsonObject("pages")
            .getAsJsonObject(pageId)
            .getAsJsonArray("categories")
    }

    private fun extractContinueObject(responseJson : JsonObject) : JsonObject?
    {
        return responseJson.getAsJsonObject("continue")

    }

    override fun close() {
        client.close()
    }

}