package ru.cats.android.model.data

import io.reactivex.Flowable
import io.reactivex.Single
import ru.cats.android.model.data.entity.CatEntity

interface FavoritesCatsRepository {

    fun addCat(cat: CatEntity)

    fun deleteCat(cat: CatEntity)

    fun getAllCatsFlowable(): Flowable<List<CatEntity>>

    fun getAllCatsSingle(): Single<List<CatEntity>>

    fun clear()
}