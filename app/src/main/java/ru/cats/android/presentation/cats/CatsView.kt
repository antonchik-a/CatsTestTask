package ru.cats.android.presentation.cats

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.terrakok.gitlabclient.presentation.global.Paginator

interface CatsView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgress(b: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun renderPaginatorState(it: Paginator.State)
}