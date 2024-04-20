package com.example.common.dsl.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.common.dsl.IListItem
import com.example.common.dsl.IListItemUnique
import com.example.common.dsl.StickyHeaderCallbacks
import com.example.common.dsl.paging.LoadStateFooterAdapter
import com.example.common.dsl.paging.RefreshHeaderAdapter
import com.example.common.dsl.viewholder.BindingViewHolder


open class BindingRvAdapter internal constructor(
    override val configMap: ConfigMap
) : ListAdapter<IListItemUnique, BindingViewHolder<*, *>>(DIFF_CALLBACK), ListAdapterScope, StickyHeaderCallbacks {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<IListItemUnique>() {
            override fun areItemsTheSame(oldItem: IListItemUnique, newItem: IListItemUnique): Boolean =
                oldItem::class == newItem::class && oldItem.uniqueId == newItem.uniqueId

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: IListItemUnique, newItem: IListItemUnique): Boolean =
                oldItem::class == newItem::class && oldItem == newItem
        }

    }

    override fun isStickyHeader(position: Int): Boolean {
        return configMap[getItem(position).qualifiedType()]?.isSticky ?: false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<*, *> =
        configMap[viewType]?.creator?.invoke(parent, ::getCurrentList) ?: throw RuntimeException("未找到viewType对应的ViewHolder")

    override fun onBindViewHolder(holder: BindingViewHolder<*, *>, position: Int) {
        val dataItem = getItem(position)
        holder.bind(dataItem)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).qualifiedType()

    override fun currentList(): List<IListItem> = currentList

}


fun BindingRvAdapter.withRefreshHeader(
    enable: Boolean = true,
    onRefresh: () -> Unit
) = ConcatAdapter(RefreshHeaderAdapter().apply {
    this.enable = enable
    doOnRefresh(onRefresh)
}, this)

fun BindingRvAdapter.withLoadStateFooter(
    enable: Boolean = true,
    loadState: LiveData<LoadStateFooterAdapter.LoadState>,
    onLoadMore: () -> Unit
) = ConcatAdapter(this, LoadStateFooterAdapter(loadState).apply {
    this.enable = enable
    doOnLoadMore(onLoadMore)
})

fun BindingRvAdapter.withLoadStateHeaderAndFooter(
    enableHeader: Boolean = true,
    enableFooter: Boolean = true,
    loadState: LiveData<LoadStateFooterAdapter.LoadState>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit
) = ConcatAdapter(RefreshHeaderAdapter().apply {
    this.enable = enableHeader
    doOnRefresh(onRefresh)
}, this, LoadStateFooterAdapter(loadState).apply {
    this.enable = enableFooter
    doOnLoadMore(onLoadMore)
})




