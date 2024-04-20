package com.example.common.dsl.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.common.dsl.IListItem
import com.example.common.dsl.IListItemUnique
import com.example.common.dsl.paging.BindingPagingAdapter
import com.example.common.dsl.viewholder.BindingViewHolder

/**
 * @author xres
 * @date 2022/9/3 17:09
 */

typealias ViewHolderCreator<D, VB> = (parent: ViewGroup, list: () -> List<IListItem>) -> BindingViewHolder<D, VB>
internal typealias ConfigMap = SparseArray<ViewHolderInfo>

data class ViewHolderInfo(
    val creator: ViewHolderCreator<*, *>,
    val isSticky: Boolean
)

/**
 * listAdapter构建器
 */
fun listAdapterOf(
    block: ListAdapterScope.() -> Unit
): BindingRvAdapter {
    val configMap = ConfigMap()
    return object : BindingRvAdapter(configMap) {
        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            block.invoke(this)
        }
    }
}

fun pagingAdapterOf(
    block: ListAdapterScope.() -> Unit
): BindingPagingAdapter {
    val configMap = ConfigMap()
    return object : BindingPagingAdapter(configMap) {
        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            block.invoke(this)
        }
    }
}

inline fun <reified D : IListItemUnique, reified VB : ViewBinding> ListAdapterScope.withViewHolder(
    isSticky: Boolean = false,
    crossinline viewHolderScope: BindingViewHolder<D, VB>.() -> Unit = { }
) {
    val dataId = IListItem.qualifiedTypeOf<D>()
    val creator: ViewHolderCreator<D, VB> = { parent: ViewGroup, list ->
        val inflater = LayoutInflater.from(parent.context)
        val binding = VB::class.java.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, inflater, parent, false) as VB
        BindingViewHolder<D, VB>(binding, this::currentList).apply(viewHolderScope)
    }
    this.configMap[dataId] = ViewHolderInfo(creator, isSticky)
}