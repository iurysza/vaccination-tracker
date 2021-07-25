package com.iurysza.learn.kmmpoc.shared.network

import com.iurysza.learn.kmmpoc.shared.entity.CovidStatsSummary
import com.iurysza.learn.kmmpoc.shared.entity.VaccinationDataResponseItem
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.serialization.json.Json

internal class VaccinationTrackerApi {
  private val httpClient = HttpClient {
    install(JsonFeature) {
      serializer = KotlinxSerializer(
        Json {
          isLenient = true
          ignoreUnknownKeys = true
        }
      )
    }
  }

  suspend fun getCovidVaccinationByState(): List<VaccinationDataResponseItem> {
    return httpClient.get("$HOST/coronavirusbra1/tables/vaccination.json")
  }

  suspend fun getCovidDataByState(): List<CovidStatsSummary> {
    return httpClient.get("$HOST/coronavirusbra1/tables/global.json")
  }

  companion object {
    // private const val HOST = "http://10.0.2.2:1080"
    private const val HOST = "https://api.vacinacao-covid19.com"
  }
}

