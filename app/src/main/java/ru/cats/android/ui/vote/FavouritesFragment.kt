package ru.cats.android.ui.vote

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favorites.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import ru.cats.android.entity.ui.CatUiViewModel
import ru.cats.android.presentation.vote.FavoritesPresenter
import ru.cats.android.presentation.vote.FavoritesView
import ru.cats.android.ui.global.BaseFragment
import ru.cats.android.ui.global.CatsListAdapter

@RuntimePermissions
class FavoritesFragment : BaseFragment(), FavoritesView {


    override val layoutRes: Int = ru.cats.android.R.layout.fragment_favorites

    @InjectPresenter
    lateinit var presenter: FavoritesPresenter

    @ProvidePresenter
    fun providePresenter(): FavoritesPresenter {
        return scope.getInstance(FavoritesPresenter::class.java)
    }

    val catInteractions = object : CatsListAdapter.CatInteractionsDelegate {
        override fun loadMore() {}
        override fun clickFavorite(cat: CatUiViewModel) {
            presenter.onFavoriteClick(cat)
        }

        override fun clickDownload(cat: CatUiViewModel) {
            downloadClickWithPermissionCheck(cat)
        }

        override fun clickCat(cat: CatUiViewModel) {
            presenter.onCatClicked(cat)
        }
    }

    private val adapter by lazy {
        CatsListAdapter(catInteractions)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView?.apply {
            val linearLayout = LinearLayoutManager(context)
            layoutManager = linearLayout
            setHasFixedSize(true)
            adapter = this@FavoritesFragment.adapter
        }

    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun showFavorites(it: List<CatUiViewModel>) {
        adapter.update(it, false)
    }

    @NeedsPermission(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun downloadClick(cat: CatUiViewModel) {
        presenter.clickDownload(cat)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }
}