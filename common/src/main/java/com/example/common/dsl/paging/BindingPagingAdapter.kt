package com.example.common.dsl.paging

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.common.dsl.IListItemUnique
import com.example.common.dsl.StickyHeaderCallbacks
import com.example.common.dsl.adapter.ConfigMap
import com.example.common.dsl.adapter.ListAdapterScope
import com.example.common.dsl.viewholder.BindingViewHolder

/**
 * @author xres
 * @date 2022/9/3 17:10
 */

open class BindingPagingAdapter internal constructor(
    override val configMap: ConfigMap
) : PagingDataAdapter<IListItemUnique, BindingViewHolder<*, *>>(DIFF_CALLBACK), ListAdapterScope, StickyHeaderCallbacks {


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
        return configMap[getItemViewType(position)]?.isSticky ?: false
    }

    override fun getItemViewType(position: Int): Int = peek(position)!!.qualifiedType()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<*, *> {
        return configMap[viewType]?.creator?.invoke(parent, ::currentList)
            ?: throw RuntimeException("未找到viewType对应的ViewHolder")
    }


    override fun onBindViewHolder(holder: BindingViewHolder<*, *>, position: Int) {
        val dataItem = getItem(position)!!
        holder.bind(dataItem)
    }

    override fun currentList(): List<IListItemUnique> = snapshot().items

    /**
     * @param block 函数类型返回值决定要不要执行更新操作
     */
    fun updateItem(position: Int, block: (dataItem: IListItemUnique?) -> Boolean) {
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

