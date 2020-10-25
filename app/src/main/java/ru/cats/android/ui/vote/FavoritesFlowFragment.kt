package ru.cats.android.ui.vote

import android.os.Bundle
import ru.cats.android.Screens
import ru.cats.android.di.module.FlowNavigationModule
import ru.cats.android.ui.global.FlowFragment
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppScreen
import toothpick.Scope

class FavoritesFlowFragment : FlowFragment() {

    companion object {
        fun newInstance(bundle: Bundle? = null) =
            FavoritesFlowFragment().apply { arguments = bundle }
    }

    override fun getLaunchScreen(): SupportAppScreen {
        return Screens.FavoritesScreen()
    }

    override fun installModules(scope: Scope) {
        scope.installModules(
            FlowNavigationModule(scope.getInstance(Router::class.java))
        )
    }


}