package ru.cats.android.presentation.global

import ru.cats.android.extension.userMessage
import ru.cats.android.model.server.error.ServerError
import ru.cats.android.model.system.ResourceManager
import timber.log.Timber
import javax.inject.Inject


class ErrorHandler @Inject constructor(
    private val resourceManager: ResourceManager
) {
    fun proceed(error: Throwable, messageListener: (String) -> Unit = {}) {
        Timber.e(error)
        if (error is ServerError) {
            messageListener(error.userMessage(resourceManager))
        } else {
            messageListener(error.userMessage(resourceManager))
        }
    }

}