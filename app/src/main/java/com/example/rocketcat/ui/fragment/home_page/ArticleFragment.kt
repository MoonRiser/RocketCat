package com.example.rocketcat.ui.fragment.home_page

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.BaseFragment
import com.example.common.ext.dp
import com.example.rocketcat.R
import com.example.rocketcat.adapter.ArticleAdapter
import com.example.rocketcat.databinding.FragmentArticleBinding
import com.example.rocketcat.ui.fragment.HomeViewModel
import kotlin.math.abs


class ArticleFragment : BaseFragment<ArticleViewModel, FragmentArticleBinding>() {

    private val originP = PointF()

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private val articleViewModel by viewModels<ArticleViewModel>()


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
        binding.rvArticle.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    originP.set(event.rawX, event.rawY)
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                    false

                }
                MotionEvent.ACTION_MOVE -> {
                    if (abs(event.rawX - originP.x) > 100f.dp)
                        v.parent?.requestDisallowInterceptTouchEvent(false)
                    false
                }
                MotionEvent.ACTION_UP -> {
                    false
                }
                else -> false
            }
        }
        binding.fab.setOnClickListener {
            viewModel.getArticle()
        }

    }


}