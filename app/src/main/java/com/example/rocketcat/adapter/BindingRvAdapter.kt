package com.example.rocketcat.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rocketcat.BR


typealias ViewHolderCreator = (parent: ViewGroup, list: () -> List<DataItem>) -> BindingViewHolder<*>

class BindingRvAdapter(private val viewHolders: HashMap<Int, ViewHolderCreator>) : ListAdapter<DataItem, BindingViewHolder<*>>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean = oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean = oldItem == newItem
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<*> {
        return viewHolders[viewType]?.invoke(parent, ::getCurrentList) ?: throw RuntimeException("未找到viewType对应的ViewHolder")
    }

    override fun onBindViewHolder(holder: BindingViewHolder<*>, position: Int) {
        val dataItem = getItem(position)
        holder.bind(dataItem)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type


}

class BindingViewHolder<T : DataItem>(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {


    fun bind(dataItem: @UnsafeVariance T) = binding.apply {
        setVariable(BR.dataItem, dataItem)
        executePendingBindings()
    }


}

abstract class DataItem(val id: Any) {
    init {
        if (!this::class.isData) {
            Log.w("BindingRvAdapter", "子类请使用data class,否则可能需要重写数据类的equals方法，确保diffUtil正确判断")
        }
    }

    var type: Int = this::class.qualifiedName?.hashCode() ?: throw RuntimeException("子类请继承本类，请不要使用匿名类")

}

fun listAdapterOf(block: HashMap<Int, ViewHolderCreator>.() -> Unit): BindingRvAdapter {
    val configMap = hashMapOf<Int, ViewHolderCreator>()
    configMap.block()
    return BindingRvAdapter(configMap)
}

inline fun <reified VB : ViewDataBinding, reified D : DataItem> HashMap<Int, ViewHolderCreator>.withType(
    crossinline onViewHolderCreate: BindingViewHolder<D>.(data: () -> D) -> Unit = { _ -> },
    noinline onItemClick: ((data: D, position: Int) -> Unit)? = null
) {
    val dataId = D::class.qualifiedName?.hashCode() ?: throw RuntimeException("子类请继承本类，请不要使用匿名类")
    val creator: ViewHolderCreator = { parent: ViewGroup, list ->
        val inflater = LayoutInflater.from(parent.context)
        val binding = VB::class.java.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, inflater, parent, false) as VB
        BindingViewHolder<D>(binding).apply {
            onViewHolderCreate {
                list.invoke()[bindingAdapterPosition] as D
            }
            onItemClick?.let { clickAction ->
                itemView.setOnClickListener {
                    val dataItem = list.invoke()[bindingAdapterPosition]
                    clickAction.invoke(dataItem as D, bindingAdapterPosition)
                }
            }

        }
    }
    this[dataId] = creator
}

