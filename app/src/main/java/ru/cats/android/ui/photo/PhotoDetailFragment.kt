package ru.cats.android.ui.photo

import android.os.Bundle
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_photo_detail.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.cats.android.GlideApp
import ru.cats.android.di.installModule
import ru.cats.android.entity.ui.CatUiViewModel
import ru.cats.android.extension.argument
import ru.cats.android.presentation.photo.DetailPhotoPresenter
import ru.cats.android.presentation.photo.DetailPhotoView
import ru.cats.android.ui.global.BaseFragment
import toothpick.Scope
import java.io.Serializable

class PhotoDetailFragment : BaseFragment(), DetailPhotoView {

    companion object {
        fun newInstance(photoBundle: PhotoDetailBundle): PhotoDetailFragment =
                PhotoDetailFragment().apply {
                    val bundle = Bundle()
                    bundle.putSerializable(ARG_BUNDLE, photoBundle)
                    this.arguments = bundle
                }
    }

    override val layoutRes: Int = ru.cats.android.R.layout.fragment_photo_detail

    @InjectPresenter
    lateinit var presenter: DetailPhotoPresenter

    @ProvidePresenter
    fun providePresenter(): DetailPhotoPresenter {
        return scope.getInstance(DetailPhotoPresenter::class.java)
    }

    override fun installModules(scope: Scope) {
        super.installModules(scope)
        scope.installModule {
            bind(PhotoDetailBundle::class.java).toInstance(bundle)
        }
    }

    val bundle: PhotoDetailBundle by argument(ARG_BUNDLE)

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun bind(cat: CatUiViewModel) {
        val sharedOptions: RequestOptions = RequestOptions()
                .fitCenter()

        GlideApp.with(context!!)
                .load(cat.url)
                .apply(sharedOptions)
                .into(photo_view)
    }
}

class PhotoDetailBundle(val img: CatUiViewModel) : Serializable

