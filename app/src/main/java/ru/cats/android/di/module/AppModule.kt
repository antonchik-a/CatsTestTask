package ru.cats.android.di.module

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import okhttp3.OkHttpClient
import ru.cats.android.di.provider.*
import ru.cats.android.model.data.FavoritesCatsRepository
import ru.cats.android.model.interactor.cats.CatsInteractor
import ru.cats.android.model.repository.cats.CatsRepository
import ru.cats.android.model.server.CatApi
import ru.cats.android.model.system.AppSchedulers
import ru.cats.android.model.system.ResourceManager
import ru.cats.android.model.system.SchedulersProvider
import ru.cats.android.model.system.events.EventNotifier
import ru.cats.android.model.system.message.SystemMessageNotifier
import ru.cats.android.presentation.global.ErrorHandler
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import toothpick.config.Module


class AppModule(context: Context) : Module() {
    init {
        //Global
        bind(Context::class.java).toInstance(context)
        bind(SchedulersProvider::class.java).toInstance(AppSchedulers())
        bind(ResourceManager::class.java).toProvider(ResourceManagerProvider::class.java)
            .providesSingleton()
        bind(AssetManager::class.java).toInstance(context.assets)


        //Database
        bind(FavoritesCatsRepository::class.java).toProvider(RoomStoreProvider::class.java)
            .singleton()
        // Navigation
        val cicerone = Cicerone.create()
        bind(Router::class.java).toInstance(cicerone.router)
        bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)
        // Message notification
        bind(SystemMessageNotifier::class.java).toInstance(SystemMessageNotifier())
        bind(EventNotifier::class.java).toInstance(EventNotifier())
        bind(Gson::class.java).toProvider(GsonProvider::class.java).providesSingleton()
        bind(OkHttpClient::class.java).toProvider(OkHttpClientProvider::class.java)
            .providesSingleton()
        bind(CatApi::class.java).toProvider(ApiProvider::class.java).providesSingleton()

        //Auth
        bind(CatsRepository::class.java).singleton()
        bind(CatsInteractor::class.java).singleton()

        //Error handler with logout logic
        bind(ErrorHandler::class.java).singleton()
    }
}