package ru.cats.android.presentation.main

import moxy.InjectViewState
import ru.cats.android.model.system.message.SystemMessageNotifier
import ru.cats.android.presentation.global.BasePresenter
import javax.inject.Inject


@InjectViewState
class MainFlowPresenter @Inject constructor(
    private val systemMessageNotifier: SystemMessageNotifier
) : BasePresenter<MainFlowView>() {

}