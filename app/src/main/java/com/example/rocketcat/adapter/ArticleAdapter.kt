package com.example.rocketcat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.common.ext.dpValue
import com.example.rocketcat.BR
import com.example.rocketcat.R
import com.example.rocketcat.customview.AdImageView
import com.example.rocketcat.data.network.response.ArticleResponse
import com.example.rocketcat.databinding.ItemRvArticleBinding

private const val AD_TYPE = 816
private const val NORMAL_TYPE = 34

class ArticleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mContext: Context


    var dataList = arrayListOf<ArticleResponse>()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }


    class MyViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    class AdViewHolder(val imageView: AdImageView) : RecyclerView.ViewHolder(imageView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context
        return when (viewType) {
            AD_TYPE -> {
                AdImageView(parent.context).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 260f.dpValue()
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


    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyViewHolder -> {
                holder.binding.setVariable(BR.articleViewModel, dataList[position])
            }
            is AdViewHolder -> {
                holder.imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        mContext,
                        R.drawable.cp
                    )
                )

            }

        }
    }


    override fun getItemViewType(position: Int) = when (position) {
        6 -> AD_TYPE
        else -> NORMAL_TYPE
    }
}