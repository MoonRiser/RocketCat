package com.example.rocketcat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.rocketcat.BR
import com.example.rocketcat.R
import com.example.rocketcat.data.network.response.ArticleResponse
import com.example.rocketcat.databinding.ItemRvArticleBinding

class ArticlrAdapter : RecyclerView.Adapter<ArticlrAdapter.MyViewHolder>() {


    var dataList = arrayListOf<ArticleResponse>()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }


    class MyViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataBindingUtil.inflate<ItemRvArticleBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_rv_article,
            parent, false
        ).let {
            MyViewHolder(it)
        }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.setVariable(BR.articleViewModel, dataList[position])
    }
}