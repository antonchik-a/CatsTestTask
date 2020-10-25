package ru.cats.android.presentation.photo

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.cats.android.entity.ui.CatUiViewModel


interface DetailPhotoView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun bind(cat: CatUiViewModel)
}