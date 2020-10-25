package ru.cats.android.model.data

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.cats.android.model.data.entity.CatEntity
import ru.cats.android.model.data.room.CatDao

@Database(
    entities = [CatEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catDao(): CatDao
}
