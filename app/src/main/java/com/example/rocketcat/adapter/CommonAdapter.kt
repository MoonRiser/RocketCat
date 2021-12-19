package com.example.rocketcat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rocketcat.databinding.ItemViewGalleryBinding


class CommonAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(DIFF_UTIL) {

    companion object {

        private const val TYPE_NOTHING = -1
        private const val TYPE_GALLERY = 1

        val DIFF_UTIL = object : DiffUtil.ItemCallback<Any>() {

            override fun areItemsTheSame(oldItem: Any, newItem: Any) = when {
                oldItem is ImageItem && newItem is ImageItem -> oldItem.imageId == newItem.imageId
                else -> false
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any) = when {
                else -> false
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_GALLERY -> GalleryViewHolder(
                ItemViewGalleryBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            else -> throw RuntimeException("wrong viewType")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is GalleryViewHolder -> holder.bind(item as ImageItem)
        }
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is ImageItem -> TYPE_GALLERY
        else -> TYPE_NOTHING
    }

}

class GalleryViewHolder(private val binding: ItemViewGalleryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ImageItem) {
        Glide.with(itemView.context)
            .load(item.imageId)
            .into(binding.ivContent)
    }
}


@JvmInline
value class ImageItem(val imageId: Int)