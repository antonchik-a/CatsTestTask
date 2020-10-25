package ru.cats.android.ui.global

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer
import kotlinx.android.synthetic.main.item_cat.view.*
import ru.cats.android.GlideApp
import ru.cats.android.R
import ru.cats.android.entity.ui.BaseUIViewModel
import ru.cats.android.entity.ui.CatUiViewModel
import ru.cats.android.entity.ui.ProgressUiViewModel
import ru.cats.android.extension.screenWidth
import ru.cats.android.extension.visible

class CatsListAdapter(val interactionsDelegate: CatInteractionsDelegate) : AsyncListDifferDelegationAdapter<BaseUIViewModel>(
        object : DiffUtil.ItemCallback<BaseUIViewModel>() {
            override fun areItemsTheSame(oldItem: BaseUIViewModel, newItem: BaseUIViewModel): Boolean {
                if (oldItem === newItem) return true

                if (oldItem is CatUiViewModel && newItem is CatUiViewModel) {
                    return oldItem.id == newItem.id
                }
                return false
            }

            override fun getChangePayload(oldItem: BaseUIViewModel, newItem: BaseUIViewModel) = Any() //disable default blink animation

            override fun areContentsTheSame(oldItem: BaseUIViewModel, newItem: BaseUIViewModel) = oldItem == newItem
        }
) {
    var fullData = false

    init {
        items = mutableListOf()
        delegatesManager.addDelegate(catAdapterDelegate())
        delegatesManager.addDelegate(progressAdapterDelegate())
    }

    fun update(data: List<BaseUIViewModel>, isPageProgress: Boolean) {
        items = mutableListOf<BaseUIViewModel>().apply {
            addAll(data)
            if (isPageProgress) add(ProgressUiViewModel)
        }
    }

    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: MutableList<Any?>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        if (!fullData && position >= items.size - 10) interactionsDelegate.loadMore()
    }


    fun catAdapterDelegate() = adapterDelegateLayoutContainer<CatUiViewModel, BaseUIViewModel>(R.layout.item_cat) {
        bind { diffPayloads ->
            displayImage(itemView.image, itemView.progressBar, item)
            setFavoriteState(item.isFavorite, itemView.favorite)

            itemView.image.setOnClickListener {
                interactionsDelegate.clickCat(item)
            }

            itemView.favorite.setOnClickListener {
                interactionsDelegate.clickFavorite(item)
            }

            itemView.download.setOnClickListener {
                interactionsDelegate.clickDownload(item)
            }
        }
    }

    fun setFavoriteState(isFavorite: Boolean, button: FloatingActionButton) {
        if (isFavorite) {
            button.setImageResource(R.drawable.ic_favorite_24)
            button.setColorFilter(ContextCompat.getColor(button.context, R.color.white))
            button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(button.context, R.color.colorAccent))
        } else {
            button.setImageResource(R.drawable.ic_favorite_border)
            button.setColorFilter(ContextCompat.getColor(button.context, R.color.black))
            button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(button.context, R.color.white))
        }

    }

    fun displayImage(imageView: ImageView, progressBar: ProgressBar, item: CatUiViewModel) {
        val viewWidth = imageView.context.screenWidth
        val newHeight = (viewWidth * 9).toFloat() / 16.0f
        imageView.layoutParams.height = newHeight.toInt()

        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .centerCrop()

        if (item.width < item.height) {
            requestOptions.centerInside()
        }

        progressBar.visible(true)
        GlideApp.with(imageView)
                .applyDefaultRequestOptions(requestOptions)
                .load(item.url)
                .dontAnimate()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        progressBar.visible(false)
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        progressBar.visible(false)
                        return false
                    }
                })
                .into(DrawableImageViewTarget(imageView))
    }

    fun progressAdapterDelegate() = adapterDelegateLayoutContainer<ProgressUiViewModel, BaseUIViewModel>(R.layout.item_progress) {}


    interface CatInteractionsDelegate {
        fun loadMore()
        fun clickCat(cat: CatUiViewModel)
        fun clickFavorite(cat: CatUiViewModel)
        fun clickDownload(cat: CatUiViewModel)
    }

}