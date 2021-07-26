package com.github.iurysza.vaccinationtracker

import com.github.iurysza.vaccinationtracker.cache.CovidVaccinationData
import com.github.iurysza.vaccinationtracker.cache.DatabaseDriverFactory
import com.github.iurysza.vaccinationtracker.cache.VaccinationTrackerDatabase
import com.github.iurysza.vaccinationtracker.entity.VaccinationDataResponseItem
import com.github.iurysza.vaccinationtracker.entity.toVaccinationData
import com.github.iurysza.vaccinationtracker.network.VaccinationTrackerApi

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
