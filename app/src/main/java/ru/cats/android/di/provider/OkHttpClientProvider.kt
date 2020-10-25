package ru.cats.android.di.provider

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import ru.cats.android.BuildConfig
import ru.cats.android.model.server.interceptor.CurlLoggingInterceptor
import ru.cats.android.model.server.interceptor.ErrorResponseInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider


class OkHttpClientProvider @Inject constructor() : Provider<OkHttpClient> {

    override fun get() = with(OkHttpClient.Builder()) {
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)
        addNetworkInterceptor(ErrorResponseInterceptor())
        if (BuildConfig.DEBUG) {
            addNetworkInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
            addNetworkInterceptor(CurlLoggingInterceptor())
        }
        build()
    }
}