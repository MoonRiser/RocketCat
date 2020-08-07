package com.example.rocketcat.ui.fragment.tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rocketcat.base.BaseViewModel
import com.example.rocketcat.data.network.RequestManager
import com.example.rocketcat.data.network.response.ArticleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleViewModel : BaseViewModel() {


    val articleList: LiveData<ArrayList<ArticleResponse>>
        get() = _articleList

    private val _articleList = MutableLiveData(arrayListOf<ArticleResponse>())


    fun getAriticle() {
        viewModelScope.launch {
            val dataList = withContext(Dispatchers.IO) {
                RequestManager.getHomeData(0)
            }
            _articleList.value = dataList.data.datas

        }
    }


}