package com.iurysza.learn.kmmpoc.shared.network

import com.iurysza.learn.kmmpoc.shared.entity.ArticlesResponse
import com.iurysza.learn.kmmpoc.shared.entity.Hit
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.serialization.json.Json

class HackernewsApi {
    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = Json {
                isLenient = true
                ignoreUnknownKeys = true
            }
            serializer = KotlinxSerializer(json)
        }
    }

    suspend fun getAllHits(): List<Hit> {
        val response = httpClient.get<ArticlesResponse>(hitsEndpoint)
        return response.hits
    }

    companion object {
        private const val hitsEndpoint = "https://hn.algolia.com/api/v1/search?tags=story"
    }
}

