package com.example.rocketcat.ui.fragment.home_page

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.BaseFragment
import com.example.common.dsl.pagingAdapterOf
import com.example.common.dsl.withViewHolder
import com.example.rocketcat.customview.AdImageView
import com.example.rocketcat.databinding.FragmentArticleBinding
import com.example.rocketcat.databinding.ItemRvAdBinding
import com.example.rocketcat.databinding.ItemRvArticleBinding
import com.example.rocketcat.ui.fragment.response.AdBean
import com.example.rocketcat.ui.fragment.response.ArticleBean
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ArticleFragment : BaseFragment<ArticleViewModel, FragmentArticleBinding>() {

    companion object {
        private const val TAG = "ArticleFragment"
    }

    private val articleAdapter = pagingAdapterOf {
        withViewHolder<ArticleBean, ItemRvArticleBinding> { }
        withViewHolder<AdBean, ItemRvAdBinding>()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initView(view: View, savedInstanceState: Bundle?) {

        binding.horizontalRv.apply {
            adapter = articleAdapter
            layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false).apply {
                spanSizeLookup = GridLayoutManager.DefaultSpanSizeLookup()
            }
            PagerSnapHelper().attachToRecyclerView(this)
        }

        binding.rvArticle.apply {
            val linearLayoutManager: LinearLayoutManager
            adapter = articleAdapter.withRefreshHeaderAndLoadStateFooter()
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
                        if (viewHolder?.itemViewType == 2) {
                            (viewHolder.itemView as AdImageView).setOffset(
                                recyclerView.height,
                                viewHolder.itemView.top
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

        viewModel.logger.observe(viewLifecycleOwner) {
            Log.i(TAG, it)
        }

        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                Log.i(TAG, "${event.name} Event happens")
            }
        })

    }


}