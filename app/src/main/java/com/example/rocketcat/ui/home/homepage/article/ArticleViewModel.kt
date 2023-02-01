package com.example.rocketcat.ui.home.homepage.article

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.common.base.BaseViewModel
import com.example.common.data.network.NetworkApi
import com.example.common.dsl.DataItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ArticleViewModel : BaseViewModel() {


    private val articleApiService by lazy { NetworkApi.service<ArticleApiService>() }


    val articleFlow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 30)
    ) {
        ArticlePagingSource(articleApiService)
    }.flow.cachedIn(viewModelScope)

    val visibleRange = MutableStateFlow(IntRange.EMPTY)

    val subscription = visibleRange
        .sample(300)
        .onEach {
            Log.d("xres", "sample is:$it")
        }.flatMapLatest {
            TemperatureSource.temperaturesOf(*it.toList().toIntArray())
        }

}


class ArticlePagingSource(private val articleApiService: ArticleApiService) : PagingSource<Int, DataItem>() {

    override fun getRefreshKey(state: PagingState<Int, DataItem>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.nextKey
//        }
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataItem> {

        val pageNo = params.key ?: 0
        return try {
            val rsp = articleApiService.getArticleList(pageNo).data
            val datas = rsp.datas + AdBean
            val next = if (rsp.hasMore) pageNo + 1 else null
            LoadResult.Page(data = datas, prevKey = null, nextKey = next)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

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