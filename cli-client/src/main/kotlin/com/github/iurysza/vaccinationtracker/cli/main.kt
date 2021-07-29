package com.github.iurysza.vaccinationtracker.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.pair
import com.github.iurysza.vaccinationtracker.VaccinationTracker
import com.github.iurysza.vaccinationtracker.cache.CovidVaccinationData
import com.github.iurysza.vaccinationtracker.cache.DatabaseDriverFactory
import kotlinx.coroutines.runBlocking

class PrintVaccinationData : CliktCommand() {

  private val forceUpdate by option("-f", "--force", help = "Get latest data (updated once a day)").flag()
  private val state by option(help = "data for state")
  private val data: Pair<String, String>? by option(help = "data point").pair()

  private val sdk = VaccinationTracker(databaseDriverFactory = DatabaseDriverFactory())

  override fun run() = runBlocking {
    val vaccinationData: List<CovidVaccinationData> = sdk.getVaccinationData(latest = forceUpdate)
    val stateData = vaccinationData.find { it.isoCode == state }

    if (stateData != null) {
      echo(stateData.state)
      val (value, matchReason) = getDataValue(stateData)
      echo("$matchReason: $value")
    } else {
      echo("Data by state: ")
      vaccinationData.forEach { println(it) }
    }
  }

  private fun getDataValue(vaccinationData: CovidVaccinationData): Pair<String, String> {
    val type = data?.second
    val data = data?.first
    return when {
      data.matches("firstDose") -> {
        when (type) {
          "%" -> vaccinationData.firstDosePercentage.toString() to "First Dose percentage"
          else -> vaccinationData.firstDose.toString() to "First Dose"
        }
      }
      data.matches("secondDose") -> {
        when (type) {
          "%" -> vaccinationData.secondDosePercentage.toString() to "Second Dose percentage"
          else -> vaccinationData.secondDose.toString() to "Second Dose"
        }
        }
        data.matches("single") -> {
        when (type) {
          "%" -> vaccinationData.secondDosePercentage.toString() to "Single Dose percentage"
          else -> vaccinationData.secondDose.toString() to "Single Dose"
        }
      }
      data.matches("full") -> {
        when (type) {
          "%" -> vaccinationData.fullyVaccinatedPercentage.toString() to "Fully vaccinated percentage"
          else -> vaccinationData.fullyVaccinated.toString() to "Fully vaccinated"
        }
      }
      else -> "invalid option" to ""
    }
  }

  private fun String?.matches(dataPointName: String) = this.toString() in dataPointName
}



fun main(args: Array<String>) = PrintVaccinationData().main(args)
