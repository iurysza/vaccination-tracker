package com.github.iurysza.vaccinationtracker.entity

import com.github.iurysza.vaccinationtracker.cache.CovidVaccinationData

internal fun List<CovidStatsSummary>.toVaccinationData(): List<CovidVaccinationData> = map { responseItem ->
  val vaccinations = responseItem.vaccinations
  CovidVaccinationData(
    state = responseItem.state,
    isoCode = responseItem.iso_code,
    singleDose = vaccinations.singleDose.toLong(),
    singleDosePercentage = vaccinations.singleDosePercentage,
    firstDose = vaccinations.firstDose.toLong(),
    firstDosePercentage = vaccinations.firstDosePercentage,
    secondDose = vaccinations.secondDose.toLong(),
    secondDosePercentage = vaccinations.secondDosePercentage,
    fullyVaccinated = vaccinations.fullyVaccinated.toLong(),
    fullyVaccinatedPercentage = vaccinations.fullyVaccinatedPercentage,
    lastUpdate = vaccinations.lastUpdate,
    total = vaccinations.total.toLong()
  )
}
