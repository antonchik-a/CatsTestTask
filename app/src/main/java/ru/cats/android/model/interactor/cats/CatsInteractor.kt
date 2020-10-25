package ru.cats.android.model.interactor.cats

import io.reactivex.Completable
import io.reactivex.Flowable
import ru.cats.android.entity.ui.CatUiViewModel
import ru.cats.android.mapper.CatMapper
import ru.cats.android.model.repository.cats.CatsRepository
import ru.cats.android.model.system.SchedulersProvider
import javax.inject.Inject

class CatsInteractor @Inject constructor(
        private val catsRepository: CatsRepository,
        private val mapper: CatMapper,
        private val schedulersProvider: SchedulersProvider,
) {

    fun getCats(page: Int) = catsRepository.getCats(page)
            .map { mapper.toUI(it) }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.io())


    fun getFavoritesCats(): Flowable<List<CatUiViewModel>> {
        return catsRepository.getFavoritesCatsFlowable()
                .map { mapper.toUI(it, true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.io())
    }

    fun addFavorite(cat: CatUiViewModel): Completable {
        return Completable.fromCallable {
            catsRepository.addFavorite(mapper.toEntity(cat))
        }

                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.io())
    }

    fun deleteFavorite(cat: CatUiViewModel): Completable {
        return Completable.fromCallable {
            catsRepository.deleteFavorite(mapper.toEntity(cat))
        }

                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.io())
    }

    fun downloadCat(cat: CatUiViewModel) = catsRepository.downloadCat(mapper.toDomain(cat))

}