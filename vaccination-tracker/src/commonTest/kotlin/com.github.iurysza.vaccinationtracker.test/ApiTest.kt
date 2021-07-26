package com.github.iurysza.vaccinationtracker.test

import com.github.iurysza.vaccinationtracker.network.VaccinationTrackerApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.test.Test

class ApiTest {

  private val api: VaccinationTrackerApi = VaccinationTrackerApi()

  @Test
  fun test(): Unit {
    val scope = GlobalScope.launch {
      api.getCovidDataByState()
    }
  }
}