package ru.cats.android.model.server

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.cats.android.entity.Cat

interface CatApi {
    @GET("/v1/images/search")
    fun loadCats(@Query("page") page: Int, @Query("limit") limit: Int): Single<List<Cat>>
}