package com.example.rocketcat.ui.fragment.tab

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rocketcat.R
import com.example.rocketcat.adapter.ArticleAdapter
import com.example.rocketcat.base.BaseFragment
import com.example.rocketcat.databinding.FragmentArticleBinding
import kotlinx.android.synthetic.main.fragment_article.*


class ArticleFragment : BaseFragment<ArticleViewModel, FragmentArticleBinding>() {


    private val mAdapter by lazy { ArticleAdapter() }

    override fun layoutId() = R.layout.fragment_article

    override fun initView(savedInstanceState: Bundle?) {

        rvArticle.apply {
            val linearLayoutManager: LinearLayoutManager
            adapter = mAdapter
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
        fab.setOnClickListener {
            viewModel.getAriticle()
        }

    }

    override fun initObserver() {
        viewModel.articleList.observe(this, Observer {
            mAdapter.dataList = it
        })
    }
}