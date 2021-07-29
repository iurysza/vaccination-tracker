package com.github.iurysza.vaccinationtracker.cache

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

actual class DatabaseDriverFactory {

  actual fun createDriver(): SqlDriver {
    val jdbcSqliteDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    CovidVaccinationDatabase.Schema.create(jdbcSqliteDriver)
    return jdbcSqliteDriver
  }

}