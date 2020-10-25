package ru.cats.android.presentation.vote

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.cats.android.entity.ui.CatUiViewModel

interface FavoritesView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showFavorites(it: List<CatUiViewModel>)
}