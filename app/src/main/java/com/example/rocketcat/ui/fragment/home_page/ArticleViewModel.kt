package com.example.rocketcat.ui.fragment.home_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.common.base.BaseViewModel
import com.example.common.data.network.NetworkApi
import com.example.rocketcat.ui.fragment.response.AdBean
import com.example.rocketcat.ui.fragment.response.ApiService
import com.example.rocketcat.ui.fragment.response.ContentBean
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class ArticleViewModel : BaseViewModel() {


    private val apiService by lazy { NetworkApi.service<ApiService>() }


    val articleFlow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 10)
    ) {
        ArticlePagingSource(apiService)
    }.flow.cachedIn(viewModelScope)

    private val _logger = MutableLiveData<Long>()

    val logger = _logger.switchMap<Long, String> {
        liveData {
            source(it).collect {
                emit(it)
            }
        }
    }

    private var i = 0L
    fun change() {
        i++
        _logger.value = i * 1000
    }

    private fun source(delay: Long) = flow {
        while (true) {
            delay(delay)
            emit("current timeStamp with delay $delay : ${System.currentTimeMillis()}")
        }
    }


}


class ArticlePagingSource(private val apiService: ApiService) : PagingSource<Int, ContentBean>() {

    override fun getRefreshKey(state: PagingState<Int, ContentBean>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentBean> {

        val pageNo = params.key ?: 0
        return try {
            val rsp = apiService.getArticleList(pageNo).data
            val datas = rsp.datas + listOf(AdBean)
            val next = if (rsp.hasMore) pageNo + 1 else null
            LoadResult.Page(data = datas, prevKey = null, nextKey = next)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

}