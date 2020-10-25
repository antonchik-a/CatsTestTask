package ru.cats.android.presentation.vote

import moxy.InjectViewState
import ru.cats.android.R
import ru.cats.android.Screens
import ru.cats.android.entity.ui.CatUiViewModel
import ru.cats.android.model.interactor.cats.CatsInteractor
import ru.cats.android.model.system.ResourceManager
import ru.cats.android.model.system.flow.FlowRouter
import ru.cats.android.model.system.message.SystemMessageNotifier
import ru.cats.android.presentation.global.BasePresenter
import ru.cats.android.presentation.global.ErrorHandler
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class FavoritesPresenter @Inject constructor(
        private val catsInteractor: CatsInteractor,
        private val router: Router,
        private val systemMessageNotifier: SystemMessageNotifier,
        private val errorHandler: ErrorHandler,
        private val flowRouter: FlowRouter,
        private val resourceManager: ResourceManager
) : BasePresenter<FavoritesView>() {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showFavorites()
    }

    private fun showFavorites() {
        catsInteractor.getFavoritesCats()
                .subscribe({
                    viewState.showFavorites(it)
                }, {
                    errorHandler.proceed(it, {
                        Timber.e(it)
                    })
                })
                .connect()
    }


    fun onBackPressed() {
        flowRouter.exit()
    }

    fun onCatClicked(cat: CatUiViewModel) {
        router.navigateTo(Screens.PhotoDettailScreen(cat))
    }

    fun onFavoriteClick(cat: CatUiViewModel) {
        catsInteractor.deleteFavorite(cat)
                .subscribe({
                    Timber.d("removed ${cat.id}")
                }, {
                    Timber.e(it)
                })
                .connect()
    }

    fun clickDownload(cat: CatUiViewModel) {
        catsInteractor.downloadCat(cat)
        systemMessageNotifier.send(resourceManager.getString(R.string.file_download_start))
    }
}