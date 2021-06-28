package com.iurysza.learn.kmmpoc.shared

import com.iurysza.learn.kmmpoc.shared.cache.DatabaseDriverFactory
import com.iurysza.learn.kmmpoc.shared.cache.DatabaseNew
import com.iurysza.learn.kmmpoc.shared.cache.HitStore
import com.iurysza.learn.kmmpoc.shared.entity.Hit
import com.iurysza.learn.kmmpoc.shared.network.HackernewsApi

class HackerNewsSDK(databaseDriverFactory: DatabaseDriverFactory) {
  private val hackerNewsApi: HackernewsApi = HackernewsApi()
  private val database = DatabaseNew(databaseDriverFactory)

  @Throws(Exception::class)
  suspend fun getArticles(forceReload: Boolean): List<HitStore> {
    val cachedLaunches = database.getAllLaunches()
    return if (cachedLaunches.isNotEmpty() && !forceReload) {
      cachedLaunches
    } else {
      val allHits = hackerNewsApi.getAllHits()
      return mapData(allHits).also {
        database.clearDatabase()
        database.persistHits(it)
      }
    }
  }

  private fun mapData(allHits: List<Hit>) = allHits.map {
    HitStore(
      url = it.url ?: "",
      title = it.title ?: "",
      storyUrl = it.storyUrl ?: "",
      storyTitle = it.storyTitle ?: "",
      storyId = it.createdAt,
      relevancyScore = it.relevancyScore?.toLong() ?: 0,
      storyText = ""
    )
  }
}
