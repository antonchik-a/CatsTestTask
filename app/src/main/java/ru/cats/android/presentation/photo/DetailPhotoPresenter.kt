package ru.cats.android.presentation.photo

import moxy.InjectViewState
import ru.cats.android.presentation.global.BasePresenter
import ru.cats.android.ui.photo.PhotoDetailBundle
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@InjectViewState
class DetailPhotoPresenter @Inject constructor(
        private val router: Router,
        private val bundle: PhotoDetailBundle
) : BasePresenter<DetailPhotoView>() {


    override fun onFirstViewAttach() {
        viewState.bind(bundle.img)
    }

    fun onBackPressed() {
        router.exit()
    }
}