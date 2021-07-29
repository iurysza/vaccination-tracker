package com.github.iurysza.vaccinationtracker.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.iurysza.vaccinationtracker.VaccinationTracker
import com.github.iurysza.vaccinationtracker.cache.DatabaseDriverFactory
import kotlinx.coroutines.runBlocking

class PrintVaccinationData : CliktCommand() {
  private val sdk = VaccinationTracker(databaseDriverFactory = DatabaseDriverFactory())

  override fun run() {
    runBlocking {
      echo(sdk.getVaccinationData(true))
    }
  }
}

fun main(args: Array<String>) = PrintVaccinationData().main(args)
