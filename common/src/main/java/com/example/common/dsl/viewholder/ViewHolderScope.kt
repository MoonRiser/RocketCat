package com.example.common.dsl.viewholder

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.common.BR
import com.example.common.dsl.IListItem

interface ViewHolderScope<D : IListItem, VB : ViewBinding> {

    val itemBinding: VB

    val data: D?

    fun doOnItemViewClick(action: (D) -> Unit)

}

class BindingViewHolder<D : IListItem, VB : ViewBinding>(
    override val itemBinding: VB,
    private val listProvider: () -> List<IListItem>
) : RecyclerView.ViewHolder(itemBinding.root), ViewHolderScope<D, VB> {

    var onBind: (D) -> Unit = {}
    fun bind(dataItem: IListItem) {
        onBind.invoke(dataItem as D)
        if (itemBinding is ViewDataBinding) itemBinding.apply {
            setVariable(BR.dataItem, dataItem)
            executePendingBindings()
        }
    }

    override val data: D? get() = listProvider.invoke().getOrNull(bindingAdapterPosition) as? D

    override fun doOnItemViewClick(action: (D) -> Unit) {
        itemBinding.root.setOnClickListener {
            data?.let(action)
        }
    }

}