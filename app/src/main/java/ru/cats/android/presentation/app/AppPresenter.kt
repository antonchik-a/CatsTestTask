package ru.rosnet.charging.presentation.app

import moxy.InjectViewState
import moxy.MvpView
import ru.cats.android.Screens
import ru.cats.android.model.system.flow.FlowRouter
import ru.cats.android.presentation.global.BasePresenter
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class AppPresenter @Inject constructor(
    private val flowRouter: FlowRouter,
    private val router: Router
) : BasePresenter<MvpView>() {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        flowRouter.newRootFlow(Screens.MainFlowScreen)
    }

    fun onBackPressed() = router.finishChain()


}