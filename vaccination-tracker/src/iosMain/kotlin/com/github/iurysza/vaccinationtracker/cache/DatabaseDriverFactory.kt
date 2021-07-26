package com.github.iurysza.vaccinationtracker.cache

import com.github.iurysza.vaccinationtracker.cache.CovidVaccinationDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(CovidVaccinationDatabase.Schema, "vaccination.db")
    }
}