package com.example.rocketcat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
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


    class MyViewHolder(private val binding: ItemRvArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bean: ArticleBean) {
            binding.apply {
                articleBean = bean
                executePendingBindings()
            }


        }
    }

    class AdViewHolder(val imageView: AdImageView) : RecyclerView.ViewHolder(imageView)


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
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 260f.dp
                    )
                }.let {
                    AdViewHolder(it)
                }
            }
            else -> {
                DataBindingUtil.inflate<ItemRvArticleBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_rv_article,
                    parent, false
                ).let {
                    MyViewHolder(it)
                }
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
                holder.imageView.setImageResource(R.drawable.cp)
            }

        }
    }


}

