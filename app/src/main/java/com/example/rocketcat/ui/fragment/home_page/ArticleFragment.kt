package com.example.rocketcat.ui.fragment.home_page

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.BaseFragment
import com.example.rocketcat.R
import com.example.rocketcat.adapter.ArticleAdapter
import com.example.rocketcat.databinding.FragmentArticleBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ArticleFragment : BaseFragment<ArticleViewModel, FragmentArticleBinding>() {

    private val articleAdapter = ArticleAdapter()

    override fun layoutId() = R.layout.fragment_article

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {

        binding.rvArticle.apply {
            val linearLayoutManager: LinearLayoutManager
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(
                requireActivity(),
                RecyclerView.VERTICAL,
                false
            ).also { linearLayoutManager = it }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val firstPosition = linearLayoutManager.findFirstVisibleItemPosition()
                    val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
                    for (i in firstPosition..lastPosition) {
                        val viewHolder = findViewHolderForAdapterPosition(i)
                        if (viewHolder is ArticleAdapter.AdViewHolder) {
                            viewHolder.imageView.setOffset(
                                linearLayoutManager.height,
                                viewHolder.imageView.top
                            )
                        }

                    }
                }
            })
        }

        lifecycleScope.launch {
            viewModel.articleFlow.collectLatest {
                articleAdapter.submitData(it)
            }
        }

    }


}