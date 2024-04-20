package com.example.rocketcat.ui.home.homepage.article

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.BaseFragment
import com.example.common.dsl.StickyHeaderLinearLayoutManager
import com.example.common.dsl.adapter.pagingAdapterOf
import com.example.common.dsl.adapter.withViewHolder
import com.example.common.dsl.viewholder.BindingViewHolder
import com.example.common.ext.isInstance
import com.example.rocketcat.customview.AdImageView
import com.example.rocketcat.databinding.FragmentArticleBinding
import com.example.rocketcat.databinding.ItemRvAdBinding
import com.example.rocketcat.databinding.ItemRvArticleBinding
import com.example.rocketcat.databinding.ItemRvStickyBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ArticleFragment : BaseFragment<ArticleViewModel, FragmentArticleBinding>() {

    companion object {
        private const val TAG = "ArticleFragment"
    }

    private val articleAdapter = pagingAdapterOf {
        withViewHolder<ArticleBean, ItemRvArticleBinding>()
        withViewHolder<AdBean, ItemRvAdBinding>()
        withViewHolder<StickyBean, ItemRvStickyBinding>(isSticky = true)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initView(view: View, savedInstanceState: Bundle?) {

        binding.horizontalRv.apply {
            adapter = articleAdapter
            itemAnimator = null
            layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false).apply {
                spanSizeLookup = GridLayoutManager.DefaultSpanSizeLookup()
            }
            PagerSnapHelper().attachToRecyclerView(this)
        }

        binding.rvArticle.apply {
            val linearLayoutManager: LinearLayoutManager
            adapter = articleAdapter.withRefreshHeaderAndLoadStateFooter()
            itemAnimator = null
            layoutManager = StickyHeaderLinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                false
            ).also { linearLayoutManager = it }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                fun globalToLocal(p: Int) = findViewHolderForAdapterPosition(p)?.bindingAdapterPosition ?: 0

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val firstPosition = linearLayoutManager.findFirstVisibleItemPosition()
                    val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
                    viewModel.visibleRange.value = globalToLocal(firstPosition)..globalToLocal(lastPosition)
                    for (i in firstPosition..lastPosition) {
                        val viewHolder = findViewHolderForAdapterPosition(i) as? BindingViewHolder<*, *>
                        if (viewHolder?.itemBinding is ItemRvAdBinding) {
                            (viewHolder.itemView as AdImageView).setOffset(
                                recyclerView.height,
                                viewHolder.itemView.top
                            )
                        }

                    }
                }
            })
        }

        binding.fab.setOnClickListener {
            binding.rvArticle.smoothScrollToPosition(0)
        }

        lifecycleScope.launch {
            viewModel.articleFlow.collectLatest {
                articleAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.subscription
                    .collect { (position, content) ->
                        articleAdapter.updateItem(position) {
                            it?.isInstance<ArticleBean> { bean -> bean.desc = content } != null
                        }
                    }
            }
        }

        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                Log.i(TAG, "${event.name} Event happens")
            }
        })

    }


}