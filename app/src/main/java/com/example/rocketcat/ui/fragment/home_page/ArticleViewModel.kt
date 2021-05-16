package com.example.rocketcat.ui.fragment.home_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.common.data.network.RequestManager
import com.example.common.data.network.response.ArticleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleViewModel : BaseViewModel() {


    private var pageNo = 0
    val articleList = MutableLiveData<List<ArticleResponse>>()


    fun getArticle() {

        viewModelScope.launch {
            val result = RequestManager.getHomeData(pageNo)
            if (result.data.hasMore()) {
                pageNo++
                val oldList = articleList.value ?: emptyList()
                articleList.value = oldList + result.data.datas
            }
        }
    }


}