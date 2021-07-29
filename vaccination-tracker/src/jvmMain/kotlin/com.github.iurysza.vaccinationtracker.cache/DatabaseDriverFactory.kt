package com.github.iurysza.vaccinationtracker.cache

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

actual class DatabaseDriverFactory {

  actual fun createDriver(): SqlDriver {
    val connectionUrl = "jdbc:sqlite:///Users/iury/development/projects/personal-projects/kmmpoc/cli-client/build/install/cli-client/bin/vaccinationtracker.db"
    val jdbcSqliteDriver = JdbcSqliteDriver(connectionUrl)
    runCatching { CovidVaccinationDatabase.Schema.create(jdbcSqliteDriver) }
    return jdbcSqliteDriver
  }

}