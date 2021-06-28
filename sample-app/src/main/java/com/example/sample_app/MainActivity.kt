package com.example.sample_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iurysza.learn.kmmpoc.shared.HackerNewsSDK
import com.iurysza.learn.kmmpoc.shared.cache.DatabaseDriverFactory
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    runBlocking {
      kotlin.runCatching {
        val sdk = HackerNewsSDK(databaseDriverFactory = DatabaseDriverFactory(this@MainActivity))
        val result = sdk.getArticles(true)
        android.util.Log.e("TAG", "onCreate: $result")
      }.onFailure {
        android.util.Log.e("TAG", "onCreate:", it)
      }
    }
  }
}