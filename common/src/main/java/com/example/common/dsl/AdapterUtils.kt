package com.example.common.dsl

import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.common.BR

/**
 * @author xres
 * @date 2022/9/3 17:09
 */

typealias ViewHolderCreator<D, VB> = (parent: ViewGroup, list: () -> List<DataItem>) -> BindingViewHolder<D, VB>
typealias AdapterScope = SparseArray<ViewHolderCreator<*, *>>

class BindingViewHolder<D : DataItem, VB : ViewDataBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root) {

    fun bind(dataItem: @UnsafeVariance D) = binding.apply {
        setVariable(BR.dataItem, dataItem)
        executePendingBindings()
    }

}

abstract class DataItem(val _id: Any) {
    init {
        if (!this::class.isData) {
            Log.w("BindingRvAdapter", "子类请使用data class,否则可能需要重写数据类的equals方法，确保diffUtil正确判断")
        }
    }

    val _type: Int = this::class.qualifiedName?.hashCode() ?: throw RuntimeException("子类请继承本类，请不要使用匿名类")

}

interface ViewHolderScope<D : DataItem, VB : ViewDataBinding> {

    val adapterPosition: Int

    val itemBinding: VB

    val data: D?

    fun doOnItemViewClick(action: (D) -> Unit)

    val currentList: List<DataItem>
}

class ViewHolderScopeImpl<D : DataItem, VB : ViewDataBinding>(
    private val viewHolder: BindingViewHolder<D, VB>,
    private val listProvider: () -> List<DataItem>
) : ViewHolderScope<D, VB> {

    override val adapterPosition: Int
        get() = viewHolder.bindingAdapterPosition
    override val itemBinding: VB
        get() = viewHolder.binding
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

inline fun <reified D : DataItem, reified VB : ViewDataBinding> AdapterScope.withViewHolder(
    crossinline viewHolderScope: ViewHolderScope<D, VB>.() -> Unit = { }
) {
    val dataId = D::class.qualifiedName?.hashCode() ?: throw RuntimeException("请检查泛型D的类型")
    val creator: ViewHolderCreator<D, VB> = { parent: ViewGroup, list ->
        val inflater = LayoutInflater.from(parent.context)
        val binding = VB::class.java.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, inflater, parent, false) as VB

        BindingViewHolder<D, VB>(binding).apply {
            val impl = ViewHolderScopeImpl(this, list)
            viewHolderScope.invoke(impl)
        }
    }
    this[dataId] = creator
}