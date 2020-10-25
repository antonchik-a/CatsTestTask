package ru.cats.android.presentation.main

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleTagStrategy
import moxy.viewstate.strategy.StateStrategyType

interface MainFlowView : MvpView {
    companion object {
        const val TAG_PROGRESS_STATE = "progress_state"
    }
}