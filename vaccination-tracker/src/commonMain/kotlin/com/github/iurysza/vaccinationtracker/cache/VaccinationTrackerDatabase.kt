package com.github.iurysza.vaccinationtracker.cache

internal class VaccinationTrackerDatabase(databaseDriverFactory: DatabaseDriverFactory) {

  private val database by lazy {
    CovidVaccinationDatabase(databaseDriverFactory.createDriver()).covidVaccinationDatabaseQueries
  }

  fun clearDatabase() = database.transaction { database.clear() }

  fun addVaccinationData(vaccinationData: List<CovidVaccinationData>) = database.transaction {
    vaccinationData.forEach {
      database.insertVaccinationData(
        state = it.state,
        isoCode = it.isoCode,
        singleDose = it.singleDose,
        singleDosePercentage = it.singleDosePercentage,
        firstDose = it.firstDose,
        firstDosePercentage = it.firstDosePercentage,
        secondDose = it.secondDose,
        secondDosePercentage = it.secondDosePercentage,
        fullyVaccinated = it.fullyVaccinated,
        fullyVaccinatedPercentage = it.fullyVaccinatedPercentage,
        lastUpdate = it.lastUpdate,
        total = it.total
      )
    }
  }

  fun getVaccinationData(): List<CovidVaccinationData> = database.getVaccinationData().executeAsList()
}