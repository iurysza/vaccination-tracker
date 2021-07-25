package com.iurysza.learn.kmmpoc.shared

import com.iurysza.learn.kmmpoc.shared.cache.CovidVaccinationData
import com.iurysza.learn.kmmpoc.shared.cache.DatabaseDriverFactory
import com.iurysza.learn.kmmpoc.shared.cache.VaccinationTrackerDatabase
import com.iurysza.learn.kmmpoc.shared.entity.VaccinationDataResponseItem
import com.iurysza.learn.kmmpoc.shared.entity.toVaccinationData
import com.iurysza.learn.kmmpoc.shared.network.VaccinationTrackerApi

class VaccinationTracker(databaseDriverFactory: DatabaseDriverFactory) {

  private val api: VaccinationTrackerApi = VaccinationTrackerApi()
  private val database = VaccinationTrackerDatabase(databaseDriverFactory)

  suspend fun getFullVaccinationData(): List<VaccinationDataResponseItem> {
    return api.getCovidVaccinationByState()
  }

  suspend fun getVaccinationData(latest: Boolean): List<CovidVaccinationData> {
    val lastLoadedData = database.getVaccinationData()
    return if (lastLoadedData.isNotEmpty() && !latest) {
      lastLoadedData
    } else {
      val covidDataByState = api.getCovidDataByState()
      return covidDataByState.toVaccinationData().also {
        database.clearDatabase()
        database.addVaccinationData(it)
      }
    }
  }
}
