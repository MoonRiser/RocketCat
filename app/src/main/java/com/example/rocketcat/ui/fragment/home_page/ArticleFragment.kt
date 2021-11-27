package com.example.rocketcat.ui.fragment.home_page

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.BaseFragment
import com.example.common.ext.dp
import com.example.rocketcat.R
import com.example.rocketcat.adapter.ArticleAdapter
import com.example.rocketcat.databinding.FragmentArticleBinding
import com.example.rocketcat.ui.fragment.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


class ArticleFragment : BaseFragment<ArticleViewModel, FragmentArticleBinding>() {


    override fun layoutId() = R.layout.fragment_article

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {

        binding.rvArticle.apply {
            val linearLayoutManager: LinearLayoutManager
            adapter = ArticleAdapter()
            layoutManager =
                LinearLayoutManager(
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

    }


}