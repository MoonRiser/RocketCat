package com.example.common.dsl

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.common.base.PagingFooterAdapter
import com.example.common.base.RefreshHeaderAdapter

/**
 * @author xres
 * @date 2022/9/3 17:10
 */

class BindingPagingAdapter(
    private val viewHolders: SparseArray<ViewHolderCreator<*, *>>
) : PagingDataAdapter<DataItem, BindingViewHolder<*, *>>(DIFF_CALLBACK) {


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
                oldItem::class == newItem::class && oldItem._id == newItem._id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
                oldItem::class == newItem::class && oldItem == newItem
        }

    }


    override fun getItemViewType(position: Int): Int =
        peek(position)!!._type


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<*, *> {
        return viewHolders[viewType]?.invoke(parent) { snapshot().toList().filterNotNull() }
            ?: throw RuntimeException("未找到viewType对应的ViewHolder")
    }


    override fun onBindViewHolder(holder: BindingViewHolder<*, *>, position: Int) {
        val dataItem = getItem(position)!!
        holder.bind(dataItem)
    }

    /**
     * @param block 函数类型返回值决定要不要执行更新操作
     */
    fun updateItem(position: Int, block: (dataItem: DataItem?) -> Boolean) {
        if (block(peek(position))) notifyItemChanged(position)
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

fun pagingAdapterOf(
    block: AdapterScope.() -> Unit
): BindingPagingAdapter {
    val configMap = SparseArray<ViewHolderCreator<*, *>>()
    configMap.block()
    return BindingPagingAdapter(configMap)
}