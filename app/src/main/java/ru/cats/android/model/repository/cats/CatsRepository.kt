package ru.cats.android.model.repository.cats

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import io.reactivex.Flowable
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import ru.cats.android.entity.Cat
import ru.cats.android.mapper.CatMapper
import ru.cats.android.model.data.FavoritesCatsRepository
import ru.cats.android.model.data.entity.CatEntity
import ru.cats.android.model.server.CatApi
import java.io.File
import javax.inject.Inject


class CatsRepository @Inject constructor(private val catsApi: CatApi,
                                         private val favoritesCatsRepository: FavoritesCatsRepository,
                                         private val mapper: CatMapper,
                                         private val context: Context) {

    val PAGE_SIZE = 20

    fun getCats(page: Int) = catsApi.loadCats(page, PAGE_SIZE)

    fun getFavoritesCatsFlowable(): Flowable<List<Cat>> {
        return favoritesCatsRepository.getAllCatsFlowable()
                .map {
                    mapper.toDomain(it)
                }
    }

    fun addFavorite(cat: CatEntity) {
        favoritesCatsRepository.addCat(cat)
    }

    fun deleteFavorite(cat: CatEntity) {
        favoritesCatsRepository.deleteCat(cat)
    }

    fun downloadCat(cat: Cat) {
        var f = File(context.getExternalFilesDir(null), cat.url.split("/").last())
        val reg = Regex("^(.*?)(?:\\(\\s?(\\d+)\\))?(\\.[^.]*)?\$")

        var newFileName: String = cat.url.split("/").last()
        var i = 0
        while (f.exists()) {
            val matchRes = reg.find(f.name)
            val nameGroup = matchRes?.groups?.get(1)
            val versionGroup = matchRes?.groups?.get(2)
            val extGroup = matchRes?.groups?.get(3)
            if (versionGroup != null) {
                i = versionGroup.value.toInt()
            }
            i++
            newFileName = "${nameGroup?.value ?: ""}($i)${extGroup?.value ?: ""}"
            f = File(context.getExternalFilesDir(null), newFileName)
        }

        val url = cat.url
        val httpurl = url.toHttpUrlOrNull()
        var downloadId: Long? = null
        httpurl?.let {
            val request = DownloadManager.Request(Uri.parse(url))
                    .setTitle(newFileName)// Title of the Download Notification
                    .setDescription("Загрузка котика")// Description of the Download Notification
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                    .setDestinationUri(Uri.fromFile(f))// Uri of the destination file
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setAllowedOverRoaming(true)// Set if download is allowed on roaming network.
                    .setVisibleInDownloadsUi(true)

            val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadId = downloadManager.enqueue(request)
        }
    }

}