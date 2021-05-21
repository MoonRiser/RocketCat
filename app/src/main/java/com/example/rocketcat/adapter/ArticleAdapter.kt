package com.example.rocketcat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.common.ext.dp
import com.example.rocketcat.R
import com.example.rocketcat.customview.AdImageView
import com.example.rocketcat.databinding.ItemRvArticleBinding
import com.example.rocketcat.ui.fragment.response.ArticleBean


class ArticleAdapter : ListAdapter<ArticleBean, RecyclerView.ViewHolder>(diffCallback) {


    class MyViewHolder(private val binding: ItemRvArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bean: ArticleBean) {
            binding.apply {
                articleBean = bean
                executePendingBindings()
            }


        }
    }

    class AdViewHolder(val imageView: AdImageView) : RecyclerView.ViewHolder(imageView)


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
                holder.bind(data)
            }
            is AdViewHolder -> {
                holder.imageView.setImageResource(R.drawable.cp)
            }

        }
    }

    companion object {
        private val diffCallback = object : ItemCallback<ArticleBean>() {
            override fun areItemsTheSame(
                oldItem: ArticleBean,
                newItem: ArticleBean
            ) = oldItem.id == newItem.id


            override fun areContentsTheSame(
                oldItem: ArticleBean,
                newItem: ArticleBean
            ) = oldItem == newItem

        }
        private const val TYPE_AD = 816
        private const val TYPE_NORMAL = 34

    }


}