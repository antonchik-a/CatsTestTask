package ru.cats.android.model.data

import android.content.Context
import androidx.room.Room
import io.reactivex.Flowable
import io.reactivex.Single
import ru.cats.android.model.data.entity.CatEntity
import javax.inject.Inject

class FavoritesCatsRepositoryImpl @Inject constructor(private val context: Context) :
    FavoritesCatsRepository {
    override fun addCat(cat: CatEntity) {
        database.catDao().insert(cat)
    }

    override fun deleteCat(cat: CatEntity) {
        database.catDao().delete(cat)
    }

    override fun getAllCatsFlowable(): Flowable<List<CatEntity>> {
        return database.catDao().getAllFlowable()
    }

    override fun getAllCatsSingle(): Single<List<CatEntity>> {
        return database.catDao().getAllSingle()
    }

    override fun clear() {
        database.clearAllTables()
    }

    val database: AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "cats_database")
            .fallbackToDestructiveMigration()
            .build()
}