package ru.cats.android.di.provider

import android.content.Context
import ru.cats.android.model.system.ResourceManager
import javax.inject.Inject
import javax.inject.Provider


class ResourceManagerProvider @Inject constructor(val context: Context) :
    Provider<ResourceManager> {

    override fun get(): ResourceManager {
        return ResourceManager(context)
    }
}