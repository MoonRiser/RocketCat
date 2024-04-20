package com.example.rocketcat.ui.home.homepage.tab6

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.BaseFragment
import com.example.common.dsl.DataItemUnique
import com.example.common.dsl.adapter.listAdapterOf
import com.example.common.dsl.adapter.withLoadStateHeaderAndFooter
import com.example.common.dsl.adapter.withViewHolder
import com.example.rocketcat.databinding.FragmentTab6LayoutBinding
import com.example.rocketcat.databinding.ItemRvTestLayoutBinding
import com.example.rocketcat.ui.home.homepage.HomeViewModel
import com.xres.address_selector.ext.showToast
import java.text.SimpleDateFormat
import java.util.Date

class Tab6Fragment : BaseFragment<HomeViewModel, FragmentTab6LayoutBinding>() {

    companion object {
    }

    private val listAdapter = listAdapterOf {
        withViewHolder<BookInfo, ItemRvTestLayoutBinding>()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initView(view: View, savedInstanceState: Bundle?) {

        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = listAdapter.withLoadStateHeaderAndFooter(
                loadState = viewModel.footerLoadState,
                onRefresh = {
                    viewModel.refreshBookList()
                },
                onLoadMore = {
                    viewModel.loadMoreBook(listAdapter.itemCount)
                    showToast("load more !!!")
                }
            )
        }
        viewModel.refreshBookList()
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.bookList.observe(viewLifecycleOwner) {
            listAdapter.submitList(it)
        }
    }


}

data class BookInfo(
    val bookId: Int,
    val content: String,
    @ColorInt val color: Int
) : DataItemUnique(bookId) {
    companion object {
        fun newInstance(id: Int) = BookInfo(
            bookId = id,
            content = "this moment in time is ${now()}",
            color = listOf(Color.BLACK, Color.BLUE, Color.YELLOW, Color.CYAN, Color.RED, Color.GREEN, Color.MAGENTA).random()
        )

        private fun now(): String {
            val sdf = SimpleDateFormat("hh:mm:ss")
            return sdf.format(Date())
        }
    }
}