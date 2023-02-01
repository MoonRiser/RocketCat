package com.example.rocketcat.ui.home.homepage

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.common.base.LoadStateFooterAdapter
import com.example.rocketcat.ui.home.homepage.tab6.BookInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/20 10:20
 * @Description:
 */
class HomeViewModel : BaseViewModel() {

    val currentItem = ObservableInt(0)

    val footerLoadState = MutableLiveData<LoadStateFooterAdapter.LoadState>(LoadStateFooterAdapter.LoadState.Loading)

    val bookList = MutableLiveData(emptyList<BookInfo>())

    fun refreshBookList(count: Int = 20) {
        viewModelScope.launch {
            delay(500)
            bookList.value = List(count) { BookInfo.newInstance(it) }
            footerLoadState.value = LoadStateFooterAdapter.LoadState.Loading
        }
    }

    fun loadMoreBook(from: Int, size: Int = 10) {
        val t = listOf(0, 1, 2).random()
        if (t > 0) {
            val old = bookList.value ?: emptyList()
            viewModelScope.launch {
                delay(500)
                bookList.value = old + List(size) { BookInfo.newInstance(it + from) }
                footerLoadState.value = if (t > 1) LoadStateFooterAdapter.LoadState.NotLoading.Complete
                else LoadStateFooterAdapter.LoadState.NotLoading.InComplete
            }
        } else {
            footerLoadState.value = LoadStateFooterAdapter.LoadState.Error
        }
    }


    init {


    }
}