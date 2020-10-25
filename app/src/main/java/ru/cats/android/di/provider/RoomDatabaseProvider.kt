package ru.cats.android.di.provider

import android.content.Context
import ru.cats.android.model.data.FavoritesCatsRepositoryImpl
import javax.inject.Inject
import javax.inject.Provider

class RoomStoreProvider @Inject constructor(
    private val context: Context
) : Provider<FavoritesCatsRepositoryImpl> {

    override fun get(): FavoritesCatsRepositoryImpl {
        return FavoritesCatsRepositoryImpl(context)
    }
}