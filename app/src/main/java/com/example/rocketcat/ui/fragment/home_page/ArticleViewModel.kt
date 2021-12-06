package com.example.rocketcat.ui.fragment.home_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.common.base.BaseViewModel
import com.example.common.data.network.NetworkApi
import com.example.common.ext.ClickCallback
import com.example.rocketcat.ui.fragment.response.AdBean
import com.example.rocketcat.ui.fragment.response.ApiService
import com.example.rocketcat.ui.fragment.response.ContentBean
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class ArticleViewModel : BaseViewModel() {


    private var _pageNo = 0
    val articleList = MutableLiveData<List<ContentBean>>()
    private val apiService by lazy { NetworkApi.service<ApiService>() }

    val noMoreData = MutableLiveData(false)
    val toEnd = MutableLiveData(true)

    val onFabClick = ClickCallback {
//        getArticle(0)
    }

    val refreshAction = OnRefreshListener {
//        getArticle(0)
    }
    val loadMoreAction = OnLoadMoreListener {
//        getArticle(_pageNo)
    }
    val performRefresh = MutableLiveData(false)


//    private fun getArticle(pageNo: Int) {
//
//        apiService.getArticleList(pageNo)
//            .filter {
//                it.isSuccess
//            }.map {
//                it.data
//            }
//            .onEach { data ->
//                if (data.hasMore) {
//                    _pageNo++
//                    val (toEnd, oldList) =
//                        if (pageNo == 0) (false to emptyList()) else true to (articleList.value ?: emptyList())
//                    this@ArticleViewModel.toEnd.value = toEnd
//                    val newList = mutableListOf<ContentBean>().apply {
//                        addAll(data.datas)
//                        if (size > 1) add(size / 2, AdBean)//在数据中间插广告
//                    }
//                    articleList.value = oldList + newList
//                }
//                noMoreData.value = !data.hasMore
//            }
//            .onCompletion {
//                performRefresh.value = false
//            }
//            .launchIn(viewModelScope)
//    }

    val articleFlow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 10)
    ) {
        ArticlePagingSource(apiService)
    }.flow.cachedIn(viewModelScope)


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