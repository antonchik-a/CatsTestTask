package ru.cats.android.model.data.room

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single
import ru.cats.android.model.data.entity.CatEntity

@Dao
interface CatDao {

    @Insert
    fun insert(cat: CatEntity)

    @Update
    fun update(cat: CatEntity)

    @Delete
    fun delete(cat: CatEntity)

    @Query("SELECT * FROM cats")
    fun getAllSingle(): Single<List<CatEntity>>

    @Query("SELECT * FROM cats")
    fun getAllFlowable(): Flowable<List<CatEntity>>

}