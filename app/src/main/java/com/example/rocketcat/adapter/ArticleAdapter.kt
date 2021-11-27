package com.example.rocketcat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.common.ext.dp
import com.example.rocketcat.R
import com.example.rocketcat.customview.AdImageView
import com.example.rocketcat.databinding.ItemRvArticleBinding
import com.example.rocketcat.ui.fragment.response.AdBean
import com.example.rocketcat.ui.fragment.response.ArticleBean
import com.example.rocketcat.ui.fragment.response.ContentBean
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ArticleAdapter : ListAdapter<ContentBean, RecyclerView.ViewHolder>(diffCallback) {


    companion object {
        private val diffCallback = object : ItemCallback<ContentBean>() {
            override fun areItemsTheSame(oldItem: ContentBean, newItem: ContentBean): Boolean =
                if (oldItem is ArticleBean && newItem is ArticleBean) oldItem.id == newItem.id else false


            override fun areContentsTheSame(oldItem: ContentBean, newItem: ContentBean): Boolean =
                if (oldItem is ArticleBean && newItem is ArticleBean) oldItem == newItem else false


        }
        private const val TYPE_AD = 816
        private const val TYPE_NORMAL = 34
        private const val TYPE_TEST = 12

    }


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

    class TestViewHolder(val root: TextView) : RecyclerView.ViewHolder(root) {
        init {
            root.text = "test"
            MainScope().launch {
                Log.i("xres", "TestViewHolder is to init")
                delay(8000)
                root.text = "health"
                Log.i("xres", "text is  set")

            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_TEST
            getItem(position) is AdBean -> TYPE_AD
            else -> TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TEST -> {
                TestViewHolder(TextView(parent.context).apply {
                    layoutParams = RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    )
                })
            }
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

