package com.iurysza.learn.kmmpoc.test

import com.iurysza.learn.kmmpoc.shared.network.VaccinationTrackerApi
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