package io.novumd.gitseek.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * アプリDB
 */
@Database(
    entities = [RepoEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
}
