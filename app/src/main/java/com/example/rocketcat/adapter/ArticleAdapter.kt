package com.example.rocketcat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.PagingFooterAdapter
import com.example.common.base.RefreshHeaderAdapter
import com.example.common.base.Section
import com.example.common.ext.dp
import com.example.rocketcat.R
import com.example.rocketcat.customview.AdImageView
import com.example.rocketcat.databinding.ItemRvArticleBinding
import com.example.rocketcat.ui.fragment.response.AdBean
import com.example.rocketcat.ui.fragment.response.ArticleBean
import com.example.rocketcat.ui.fragment.response.ContentBean


class ArticleAdapter : PagingDataAdapter<ContentBean, RecyclerView.ViewHolder>(diffCallback) {


    companion object {
        private val diffCallback = object : ItemCallback<ContentBean>() {
            override fun areItemsTheSame(oldItem: ContentBean, newItem: ContentBean): Boolean =
                if (oldItem is ArticleBean && newItem is ArticleBean) oldItem.id == newItem.id else false


            override fun areContentsTheSame(oldItem: ContentBean, newItem: ContentBean): Boolean =
                if (oldItem is ArticleBean && newItem is ArticleBean) oldItem == newItem else false


        }
        private const val TYPE_AD = 816
        private const val TYPE_NORMAL = 34
    }


    class MyViewHolder(private val binding: ItemRvArticleBinding) : RecyclerView.ViewHolder(binding.root), Section {
        fun bind(bean: ArticleBean) {
            binding.apply {
                dataItem = bean
                executePendingBindings()
            }


        }

        override fun isStickyHeader(): Boolean = bindingAdapterPosition % 3 == 0
    }

    class AdViewHolder(val imageView: AdImageView) : RecyclerView.ViewHolder(imageView) {
        fun bind() {
            imageView.setImageResource(R.drawable.cp)
            imageView.reset()
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            getItem(position) is AdBean -> TYPE_AD
            else -> TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_AD -> {
                AdImageView(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 260f.dp
                    )
                }.let {
                    AdViewHolder(it)
                }
            }
            else -> {
                MyViewHolder(ItemRvArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyViewHolder -> {
                val data = getItem(position)
                holder.bind(data as ArticleBean)
            }
            is AdViewHolder -> {
                holder.bind()
            }

        }
    }

    fun withRefreshHeaderAndLoadStateFooter(
        header: RefreshHeaderAdapter = RefreshHeaderAdapter(),
        footer: PagingFooterAdapter = PagingFooterAdapter()
    ): ConcatAdapter {
        header.doOnRefresh {
            refresh()
        }
        footer.doOnRetry {
            retry()
        }
        addLoadStateListener { loadStates ->
            footer.loadState = loadStates.append
            loadStates.refresh.takeIf { it is LoadState.Error }?.let {
                footer.loadState = it
            }
        }
        return ConcatAdapter(header, this, footer)
    }


}

