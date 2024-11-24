package com.example.rocketcat.ui.home.homepage.article

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.common.base.BaseViewModel
import com.example.common.data.network.NetworkApi
import com.example.common.dsl.paging.PagingResponse
import com.example.common.dsl.paging.pagingDataOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ArticleViewModel : BaseViewModel() {


    private val articleApiService by lazy { NetworkApi.service<ArticleApiService>() }


    val articleFlow = pagingDataOf(30) {
        val rsp = articleApiService.getArticleList(it).data
        val page = if (it == 0) rsp.datas + StickyBean else rsp.datas + AdBean
        PagingResponse(page, rsp.hasMore)
    }.cachedIn(viewModelScope)

    val visibleRange = MutableStateFlow(IntRange.EMPTY)

    val subscription = visibleRange
        .sample(300)
        .onEach {
            Log.d("xres", "sample is:$it")
        }.flatMapLatest {
            TemperatureSource.temperaturesOf(*it.toList().toIntArray())
        }

}

/**
 * - 温度源；模拟订阅场景
 */
object TemperatureSource {

    private const val PERIOD = 1500L
    private const val TEMP_PERIOD = 800L

    private val DELAY get() = (TEMP_PERIOD..PERIOD).random()

    private val nowFormatted: String
        get() = DateTimeFormatter
//        .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
            .ofPattern("HH:mm:ss")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())

    val now = flow {
        while (true) {
            delay(DELAY)
            emit(Unit)
        }
    }

    val temperature = flow {
        while (true) {
            val n = (-27..47).random()
            delay(DELAY)
            emit(n)
        }
    }

    val content = now.combine(temperature) { _, t ->
        "$nowFormatted 气温: $t °C"
    }

    fun temperatureOf(index: Int) = content.map { "$index # $it" }

    /**
     * 模拟订阅场景
     */
    fun temperaturesOf(vararg indexs: Int): Flow<Pair<Int, String>> =
        indexs.map { index ->
            temperatureOf(index).map { index to it }
        }.merge()


}