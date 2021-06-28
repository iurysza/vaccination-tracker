package com.iurysza.learn.kmmpoc.shared.cache

import android.content.Context
import com.iurysza.learn.kmmpoc.shared.cache.HackernewsDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(HackernewsDatabase.Schema, context, "hackernews.db")
    }
}