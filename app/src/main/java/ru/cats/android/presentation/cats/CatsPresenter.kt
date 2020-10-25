package ru.cats.android.presentation.cats

import io.reactivex.disposables.Disposable
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
import ru.terrakok.gitlabclient.presentation.global.Paginator
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class CatsPresenter @Inject constructor(
        private val router: Router,
        private val flowRouter: FlowRouter,
        private val catsInteractor: CatsInteractor,
        private val errorHandler: ErrorHandler,
        private val systemMessageNotifier: SystemMessageNotifier,
        private val paginator: Paginator.Store<CatUiViewModel>,
        private val resourceManager: ResourceManager
) : BasePresenter<CatsView>() {
    private var pageDisposable: Disposable? = null
    private var fullData = false

    val favorites: HashSet<String> = HashSet()

    init {
        paginator.render = {
            viewState.renderPaginatorState(it)
            fullData = it is Paginator.State.Empty || it is Paginator.State.FullData<*>
        }
        paginator.sideEffects.subscribe { effect ->
            when (effect) {
                is Paginator.SideEffect.LoadPage -> loadNewPage(effect.currentPage)
                is Paginator.SideEffect.ErrorEvent -> {
                    errorHandler.proceed(effect.error) { systemMessageNotifier.send(it) }
                }
            }
        }.connect()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        observeFavorites()
        refresh()
    }

    private fun observeFavorites() {
        catsInteractor.getFavoritesCats()
                .subscribe({
                    favorites.clear()
                    favorites.addAll(it.map { it.id })
                    paginator.proceed(Paginator.Action.LikeChanges(favorites))
                }, {
                    Timber.e(it)
                })
                .connect()
    }

    private fun loadNewPage(page: Int) {
        pageDisposable?.dispose()
        pageDisposable =
                catsInteractor.getCats(page)
                        .subscribe(
                                { data ->
                                    paginator.proceed(Paginator.Action.NewPage(page, data))
                                },
                                { e ->
                                    errorHandler.proceed(e)
                                    paginator.proceed(Paginator.Action.PageError(e))
                                }
                        )
        pageDisposable?.connect()
    }

    fun refresh() = paginator.proceed(Paginator.Action.Refresh)
    fun loadNextPage() = paginator.proceed(Paginator.Action.LoadMore)
    fun onCatClicked(cat: CatUiViewModel) = router.navigateTo(Screens.PhotoDettailScreen(cat))
    fun onBackPressed() = flowRouter.exit()
    fun clickFavorite(cat: CatUiViewModel) {
        if (favorites.contains(cat.id)) {
            deleteFavorite(cat)
        } else {
            addFavorite(cat)
        }
    }

    fun clickDownload(cat: CatUiViewModel) {
        catsInteractor.downloadCat(cat)
        systemMessageNotifier.send(resourceManager.getString(R.string.file_download_start))
    }

    private fun addFavorite(cat: CatUiViewModel) {
        catsInteractor.addFavorite(cat)
                .subscribe({
                    Timber.d("add favorite")
                }, {
                    Timber.e(it)
                })
                .connect()
    }

    private fun deleteFavorite(cat: CatUiViewModel) {
        catsInteractor.deleteFavorite(cat)
                .subscribe({
                    Timber.d("add favorite")
                }, {
                    Timber.e(it)
                })
                .connect()
    }
}