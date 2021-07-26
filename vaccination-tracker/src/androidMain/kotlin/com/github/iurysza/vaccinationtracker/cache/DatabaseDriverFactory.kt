package com.github.iurysza.vaccinationtracker.cache

import android.content.Context
import com.github.iurysza.vaccinationtracker.cache.CovidVaccinationDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(CovidVaccinationDatabase.Schema, context, "vaccination.db")
    }
}