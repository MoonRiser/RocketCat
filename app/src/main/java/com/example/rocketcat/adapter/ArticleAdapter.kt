package com.example.rocketcat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.common.data.network.response.ArticleResponse
import com.example.common.ext.dp
import com.example.rocketcat.BR
import com.example.rocketcat.R
import com.example.rocketcat.customview.AdImageView
import com.example.rocketcat.databinding.ItemRvArticleBinding


class ArticleAdapter : ListAdapter<ArticleResponse, RecyclerView.ViewHolder>(diffCallback) {


    class MyViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

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
                holder.binding.setVariable(BR.articleViewModel, data)
            }
            is AdViewHolder -> {
                holder.imageView.setImageResource(R.drawable.cp)
            }

        }
    }

    companion object {
        private val diffCallback = object : ItemCallback<ArticleResponse>() {
            override fun areItemsTheSame(
                oldItem: ArticleResponse,
                newItem: ArticleResponse
            ) = oldItem.id == newItem.id


            override fun areContentsTheSame(
                oldItem: ArticleResponse,
                newItem: ArticleResponse
            ) = oldItem == newItem

        }
        private const val TYPE_AD = 816
        private const val TYPE_NORMAL = 34

    }


}