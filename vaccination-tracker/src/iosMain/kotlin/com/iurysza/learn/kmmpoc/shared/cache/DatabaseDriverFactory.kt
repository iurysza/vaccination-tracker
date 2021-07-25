package com.iurysza.learn.kmmpoc.shared.cache

import com.iurysza.learn.kmmpoc.shared.cache.CovidVaccinationDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(CovidVaccinationDatabase.Schema, "vaccination.db")
    }
}