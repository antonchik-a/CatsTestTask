package ru.cats.android.mapper

import ru.cats.android.entity.Cat
import ru.cats.android.entity.ui.CatUiViewModel
import ru.cats.android.model.data.entity.CatEntity
import javax.inject.Inject

class CatMapper @Inject constructor() {


    fun toUI(cats: List<Cat>, isFavorite: Boolean = false) = cats.map { toUI(it, isFavorite) }

    fun toUI(cat: Cat, isFavorite: Boolean = false): CatUiViewModel {
        return CatUiViewModel(id = cat.id,
                url = cat.url,
                width = cat.width,
                height = cat.height,
                isFavorite = isFavorite)
    }

    fun toDomain(cats: List<CatEntity>): List<Cat> {
        return cats.map { toDomain(it) }
    }

    fun toDomain(cat: CatEntity): Cat {
        return Cat(id = cat.id,
                url = cat.url,
                width = cat.width,
                height = cat.height)
    }

    fun toEntity(cat: CatUiViewModel): CatEntity {
        return CatEntity(id = cat.id,
                url = cat.url,
                width = cat.width,
                height = cat.height)
    }

    fun toEntity(cat: Cat): CatEntity {
        return CatEntity(id = cat.id,
                url = cat.url,
                width = cat.width,
                height = cat.height)
    }

    fun toDomain(cat: CatUiViewModel) = Cat(id = cat.id,
            url = cat.url,
            width = cat.width,
            height = cat.height)
}