package com.example.sample_app

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.iurysza.vaccinationtracker.VaccinationTracker
import com.github.iurysza.vaccinationtracker.cache.DatabaseDriverFactory
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
  @Test
  fun dataIsActuallyLoading() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    runBlocking {
      val sdk = VaccinationTracker(DatabaseDriverFactory(appContext))
      val result = sdk.getVaccinationData(true)
      Log.d("TEST", result.toString())
      assert(result.isNotEmpty())
    }
  }
}