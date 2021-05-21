package com.example.rocketcat.ui.fragment.home_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.common.data.network.NetworkApi
import com.example.common.ext.showToast
import com.example.rocketcat.ui.fragment.response.ApiService
import com.example.rocketcat.ui.fragment.response.ArticleBean
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ArticleViewModel : BaseViewModel() {


    private var pageNo = 0
    val articleList = MutableLiveData<List<ArticleBean>>()
    private val apiService by lazy {
        NetworkApi.service<ApiService>()
    }


    fun getArticle() {

        viewModelScope.launch {

//            request(apiService.getArticleList(pageNo)).collect { result ->
//                if (result.data.hasMore) {
//                    pageNo++
//                    val oldList = articleList.value ?: emptyList()
//                    articleList.value = oldList + result.data.datas
//                }
//            }

            request2 { apiService.getArticleList(pageNo) }
                ?.onSuccess { result ->
                    if (result.hasMore) {
                        pageNo++
                        val oldList = articleList.value ?: emptyList()
                        articleList.value = oldList + result.datas

                    }
                }?.onFailure { _, errorMsg ->
                    showToast(errorMsg)

                }

        }
    }


}