package com.example.common.base

import android.annotation.SuppressLint
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.common.BR


typealias ViewHolderCreator = (parent: ViewGroup, list: () -> List<DataItem>) -> BindingViewHolder<*>
typealias AdapterScope = SparseArray<ViewHolderCreator>

class BindingRvAdapter(private val viewHolders: SparseArray<ViewHolderCreator>) : ListAdapter<DataItem, BindingViewHolder<*>>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
                oldItem::class == newItem::class && oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean =
                oldItem::class == newItem::class && oldItem == newItem
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<*> {
        return viewHolders[viewType]?.invoke(parent, ::getCurrentList) ?: throw RuntimeException("未找到viewType对应的ViewHolder")
    }

    override fun onBindViewHolder(holder: BindingViewHolder<*>, position: Int) {
        val dataItem = getItem(position)
        holder.bind(dataItem)
    }

    override fun getItemViewType(position: Int): Int = getItem(position)._type


}

class BindingViewHolder<T : DataItem>(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {


    fun bind(dataItem: @UnsafeVariance T) = binding.apply {
        setVariable(BR.dataItem, dataItem)
        executePendingBindings()
    }


}

/**
 * 数据基类
 */
abstract class DataItem(val id: Any) {
    init {
        if (!this::class.isData) {
            Log.w("BindingRvAdapter", "子类请使用data class,否则可能需要重写数据类的equals方法，确保diffUtil正确判断")
        }
    }

    val _type: Int = this::class.qualifiedName?.hashCode() ?: throw RuntimeException("子类请继承本类，请不要使用匿名类")

    object Header : DataItem(-1) {
        operator fun plus(other: List<DataItem>): List<DataItem> {
            return if (other is MutableList) {
                other.add(0, this)
                other
            } else {
                val result = ArrayList<DataItem>(other.size + 1)
                result.add(this)
                result.addAll(other)
                result
            }
        }
    }

    object Footer : DataItem(-1)

}

/**
 * listAdapter构建器
 */
fun listAdapterOf(block: AdapterScope.() -> Unit): BindingRvAdapter {
    val configMap = SparseArray<ViewHolderCreator>()
    configMap.block()
    return BindingRvAdapter(configMap)
}

fun BindingRvAdapter.withRefreshHeader(
    enable: Boolean = true,
    onRefresh: () -> Unit
) = ConcatAdapter(LoadStateHeaderAdapter().apply {
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
) = ConcatAdapter(LoadStateHeaderAdapter().apply {
    this.enable = enableHeader
    doOnRefresh(onRefresh)
}, this, LoadStateFooterAdapter(loadState).apply {
    this.enable = enableFooter
    doOnLoadMore(onLoadMore)
})


inline fun <reified D : DataItem, reified VB : ViewDataBinding> AdapterScope.withViewHolder(
    crossinline viewHolderScope: ViewHolderScope<D, VB>.() -> Unit = { }
) {
    val dataId = D::class.qualifiedName?.hashCode() ?: throw RuntimeException("请检查泛型D的类型")
    val creator: ViewHolderCreator = { parent: ViewGroup, list ->
        val inflater = LayoutInflater.from(parent.context)
        val binding = VB::class.java.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, inflater, parent, false) as VB
        BindingViewHolder<D>(binding).apply {
            val impl = ViewHolderScopeImpl<D, VB>(this, list)
            viewHolderScope.invoke(impl)
        }
    }
    this[dataId] = creator
}

interface ViewHolderScope<D : DataItem, VB : ViewDataBinding> {

    val adapterPosition: Int

    val itemBinding: VB

    val data: D?

    fun doOnItemViewClick(action: (D) -> Unit)

    val currentList: List<DataItem>
}

class ViewHolderScopeImpl<D : DataItem, VB : ViewDataBinding>(
    private val viewHolder: BindingViewHolder<D>,
    private val listProvider: () -> List<DataItem>
) : ViewHolderScope<D, VB> {

    override val adapterPosition: Int
        get() = viewHolder.bindingAdapterPosition
    override val itemBinding: VB
        get() = viewHolder.binding as VB
    override val data: D?
        get() = currentList.getOrNull(adapterPosition) as? D

    override fun doOnItemViewClick(action: (D) -> Unit) {
        viewHolder.itemView.setOnClickListener {
            data?.let(action)
        }
    }

    override val currentList: List<DataItem>
        get() = listProvider.invoke()
}

