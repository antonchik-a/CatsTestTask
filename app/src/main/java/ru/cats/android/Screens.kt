package ru.cats.android

import androidx.fragment.app.Fragment
import ru.cats.android.entity.ui.CatUiViewModel
import ru.cats.android.ui.cats.CatsFlowFragment
import ru.cats.android.ui.cats.CatsListFragment
import ru.cats.android.ui.flow.MainFlowFragment
import ru.cats.android.ui.photo.PhotoDetailBundle
import ru.cats.android.ui.photo.PhotoDetailFragment
import ru.cats.android.ui.vote.FavoritesFlowFragment
import ru.cats.android.ui.vote.FavoritesFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {
    const val CATS_FLOW = "schedule_flow"
    const val FAVORITES_FLOW = "vote_flow"

    object MainFlowScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = MainFlowFragment()
    }

    object CatsFlow : SupportAppScreen() {
        override fun getFragment(): Fragment = CatsFlowFragment()

        override fun getScreenKey(): String = CATS_FLOW
    }

    object CatsScreen : SupportAppScreen() {
        override fun getFragment(): Fragment = CatsListFragment()
    }


    object FavoritesFlow : SupportAppScreen() {
        override fun getFragment(): Fragment = FavoritesFlowFragment()

        override fun getScreenKey(): String = FAVORITES_FLOW
    }

    class FavoritesScreen(val id: Int = 0) : SupportAppScreen() {
        override fun getFragment(): Fragment = FavoritesFragment()
    }

    class PhotoDettailScreen(val cat: CatUiViewModel) : SupportAppScreen() {
        override fun getFragment(): Fragment =
            PhotoDetailFragment.newInstance(PhotoDetailBundle(cat))
    }

}