package com.example.rocketcat.ui.fragment.tab

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rocketcat.R
import com.example.rocketcat.adapter.ArticlrAdapter
import com.example.rocketcat.base.BaseFragment
import com.example.rocketcat.databinding.FragmentArticleBinding
import kotlinx.android.synthetic.main.fragment_article.*


class ArticleFragment : BaseFragment<ArticleViewModel, FragmentArticleBinding>() {


    private val mAdapter by lazy { ArticlrAdapter() }

    override fun layoutId() = R.layout.fragment_article

    override fun initView(savedInstanceState: Bundle?) {

        rvArticle.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
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