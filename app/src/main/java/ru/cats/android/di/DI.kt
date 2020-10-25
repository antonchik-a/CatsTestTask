package ru.cats.android.di

import toothpick.Scope
import toothpick.config.Module
import toothpick.ktp.binding.CanBeBound
import toothpick.ktp.binding.CanBeNamed
import kotlin.reflect.KClass


object DI {
    const val APP_SCOPE = "app scope"
}

inline fun Scope.installModule(bindings: Module.() -> Unit) {
    installModules(Module().apply { bindings() })
}

inline fun <reified T : Any> Module.bind(annotation: KClass<out Annotation>, obj: T) {
    CanBeNamed<T>(bind(T::class.java)).withName(annotation).toInstance(obj)
}

inline fun <reified T : Any, reified Q : Annotation> Module.bindNamed(): CanBeBound<T> =
    CanBeNamed(bind(T::class.java)).withName(Q::class)