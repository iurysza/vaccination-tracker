package com.example.sample_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iurysza.learn.kmmpoc.shared.VaccinationTracker
import com.iurysza.learn.kmmpoc.shared.cache.DatabaseDriverFactory
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    runBlocking {
      runCatching {
        val sdk = VaccinationTracker(DatabaseDriverFactory(this@MainActivity))
        val result = sdk.getVaccinationData(true)
        android.util.Log.e("=====================", "onCreate: $result")
      }.onFailure {
        android.util.Log.e("=====================", "onCreate:", it)
      }
    }
  }
}