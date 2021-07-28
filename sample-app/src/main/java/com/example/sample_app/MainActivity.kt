package com.example.sample_app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.iurysza.vaccinationtracker.VaccinationTracker
import com.github.iurysza.vaccinationtracker.cache.DatabaseDriverFactory
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    runBlocking {
      runCatching {
        val sdk = VaccinationTracker(DatabaseDriverFactory(applicationContext))
        val result = sdk.getVaccinationData(true)
        Log.d("SAMPLE-APP", "getVaccinationData: $result")
      }.onFailure {
        Log.e("SAMPLE-APP", "getVaccinationData:", it)
      }
    }
  }
}