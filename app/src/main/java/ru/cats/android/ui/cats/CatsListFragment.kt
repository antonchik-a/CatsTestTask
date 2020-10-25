package ru.cats.android.ui.cats

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_cats.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import ru.cats.android.entity.ui.BaseUIViewModel
import ru.cats.android.entity.ui.CatUiViewModel
import ru.cats.android.presentation.cats.CatsPresenter
import ru.cats.android.presentation.cats.CatsView
import ru.cats.android.ui.global.BaseFragment
import ru.cats.android.ui.global.CatsListAdapter
import ru.terrakok.gitlabclient.presentation.global.Paginator


@RuntimePermissions
class CatsListFragment : BaseFragment(), CatsView {

    override val layoutRes: Int = ru.cats.android.R.layout.fragment_cats

    @InjectPresenter
    lateinit var presenter: CatsPresenter

    @ProvidePresenter
    fun providePresenter(): CatsPresenter {
        return scope.getInstance(CatsPresenter::class.java)
    }

    val catInteractions = object : CatsListAdapter.CatInteractionsDelegate {
        override fun loadMore() {
            presenter.loadNextPage()
        }

        override fun clickCat(cat: CatUiViewModel) {
            presenter.onCatClicked(cat)
        }

        override fun clickFavorite(cat: CatUiViewModel) {
            presenter.clickFavorite(cat)
        }

        override fun clickDownload(cat: CatUiViewModel) {
            downloadClickWithPermissionCheck(cat)
        }
    }

    private val adapter by lazy {
        CatsListAdapter(catInteractions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout?.setOnRefreshListener {
            presenter.refresh()
        }

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = this@CatsListFragment.adapter
        }
    }

    override fun renderPaginatorState(state: Paginator.State) {
        postViewAction {
            when (state) {
                is Paginator.State.Empty -> {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.update(emptyList(), false)
                }
                is Paginator.State.EmptyProgress -> {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.update(emptyList(), false)
                }
                is Paginator.State.EmptyError -> {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.update(emptyList(), false)
                }
                is Paginator.State.Data<*> -> {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.update(state.data as List<BaseUIViewModel>, false)
                }
                is Paginator.State.Refresh<*> -> {
                    swipeRefreshLayout.isRefreshing = true
                    adapter.update(state.data as List<BaseUIViewModel>, false)
                }
                is Paginator.State.NewPageProgress<*> -> {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.update(state.data as List<BaseUIViewModel>, true)
                }
                is Paginator.State.FullData<*> -> {
                    swipeRefreshLayout.isRefreshing = false
                    adapter.update(state.data as List<BaseUIViewModel>, false)
                }
            }
        }
    }


    override fun showProgress(b: Boolean) {
        swipeRefreshLayout?.isRefreshing = b
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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