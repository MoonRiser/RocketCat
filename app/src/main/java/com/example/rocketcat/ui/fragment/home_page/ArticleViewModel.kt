package com.example.rocketcat.ui.fragment.home_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.common.data.network.NetworkApi
import com.example.common.ext.ClickCallback
import com.example.common.ext.showToast
import com.example.rocketcat.ui.fragment.response.AdBean
import com.example.rocketcat.ui.fragment.response.ApiService
import com.example.rocketcat.ui.fragment.response.ArticleBean
import com.example.rocketcat.ui.fragment.response.ContentBean
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArticleViewModel : BaseViewModel() {


    private var _pageNo = 0
    val articleList = MutableLiveData<List<ContentBean>>()
    private val apiService by lazy { NetworkApi.service<ApiService>() }

    val noMoreData = MutableLiveData(false)
    val toEnd = MutableLiveData(true)

    val onFabClick = ClickCallback {
        getArticle(0)
    }

    val refreshAction = OnRefreshListener {
        getArticle(0)
    }
    val loadMoreAction = OnLoadMoreListener {
        getArticle(_pageNo)
    }
    val performRefresh = MutableLiveData(false)


    private fun getArticle(pageNo: Int) {

        apiService.getArticleList(pageNo)
            .filter {
                it.isSuccess
            }.map {
                it.data
            }
            .onEach { data ->
                if (data.hasMore) {
                    _pageNo++
                    val (toEnd, oldList) =
                        if (pageNo == 0) (false to emptyList()) else true to (articleList.value ?: emptyList())
                    this@ArticleViewModel.toEnd.value = toEnd
                    val newList = mutableListOf<ContentBean>().apply {
                        addAll(data.datas)
                        if (size > 1) add(size / 2, AdBean)//在数据中间插广告
                    }
                    articleList.value = oldList + newList
                }
                noMoreData.value = !data.hasMore
            }
            .onCompletion {
                performRefresh.value = false
            }
            .launchIn(viewModelScope)
    }


}