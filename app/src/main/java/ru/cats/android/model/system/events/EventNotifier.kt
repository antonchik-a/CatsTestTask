package ru.cats.android.model.system.events

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class EventNotifier {
    private val notifierRelay = PublishRelay.create<Event>()

    val notifier: Observable<Event> = notifierRelay.hide()
    fun send(message: Event) = notifierRelay.accept(message)
}