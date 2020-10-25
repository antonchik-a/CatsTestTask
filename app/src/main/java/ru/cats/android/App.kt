package ru.cats.android

import androidx.multidex.MultiDexApplication
import ru.cats.android.di.DI
import ru.cats.android.di.module.AppModule
import timber.log.Timber
import toothpick.configuration.Configuration
import toothpick.ktp.KTP

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initToothpick()
        initAppScope()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initToothpick() {
        if (BuildConfig.DEBUG) {
            KTP.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes())
        } else {
            KTP.setConfiguration(Configuration.forProduction())
        }
    }

    private fun initAppScope() {
        KTP.openScope(DI.APP_SCOPE)
            .installModules(AppModule(this))
    }

}