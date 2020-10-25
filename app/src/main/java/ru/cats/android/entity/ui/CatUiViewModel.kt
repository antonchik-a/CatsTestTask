package ru.cats.android.entity.ui


sealed class BaseUIViewModel
data class CatUiViewModel(
    val height: Int,
    val id: String,
    val url: String,
    val width: Int,
    val isFavorite: Boolean = false
) : BaseUIViewModel()

object ProgressUiViewModel : BaseUIViewModel()