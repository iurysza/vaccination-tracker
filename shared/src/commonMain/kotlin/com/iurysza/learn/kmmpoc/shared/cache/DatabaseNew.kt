package com.iurysza.learn.kmmpoc.shared.cache

internal class DatabaseNew(databaseDriverFactory: DatabaseDriverFactory) {
  private val database = HackernewsDatabase(databaseDriverFactory.createDriver())
  private val dbQuery = database.hackernewsDatabaseQueries

  fun clearDatabase() {
    dbQuery.transaction {
      dbQuery.removeAllHits()
    }
  }

  fun getAllLaunches(): List<HitStore> {
    return dbQuery.selectAllHits(::mapHits).executeAsList()
  }

  private fun mapHits(
    url: String,
    title: String,
    storyUrl: String,
    storyId: String,
    storyTitle: String,
    relevancyScore: Long,
    storyText: String,
  ): HitStore = HitStore(
    url = url,
    title = title,
    storyUrl = storyUrl,
    storyTitle = storyTitle,
    storyId = storyId,
    relevancyScore = relevancyScore,
    storyText = storyText
  )

  fun persistHits(launches: List<HitStore>) {
    dbQuery.transaction {
      launches.forEach {
        dbQuery.insertHit(
          url = it.url,
          title = it.title,
          storyId = it.storyId,
          storyText = it.storyText,
          storyTitle = it.storyTitle,
          storyUrl = it.storyUrl,
          relevancyScore = it.relevancyScore
        )
      }
    }
  }
}