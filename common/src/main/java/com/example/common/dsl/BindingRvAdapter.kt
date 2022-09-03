package com.example.common.dsl

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.common.base.LoadStateFooterAdapter
import com.example.common.base.RefreshHeaderAdapter


class BindingRvAdapter(
    private val viewHolders: SparseArray<ViewHolderCreator<*, *>>
) : ListAdapter<DataItem, BindingViewHolder<*, *>>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
                oldItem::class == newItem::class && oldItem._id == newItem._id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
                oldItem::class == newItem::class && oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<*, *> =
        viewHolders[viewType]?.invoke(parent, ::getCurrentList) ?: throw RuntimeException("未找到viewType对应的ViewHolder")

    override fun onBindViewHolder(holder: BindingViewHolder<*, *>, position: Int) {
        val dataItem = getItem(position)
        holder.bind(dataItem)
    }

    override fun getItemViewType(position: Int): Int = getItem(position)._type


}


/**
 * listAdapter构建器
 */
fun listAdapterOf(
    block: AdapterScope.() -> Unit
): BindingRvAdapter {
    val configMap = SparseArray<ViewHolderCreator<*, *>>()
    configMap.block()
    return BindingRvAdapter(configMap)
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




