package com.example.common.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.R

private typealias OnLoadMore = () -> Unit

class LoadStateFooterAdapter(
    private val state: LiveData<LoadState>,
    private val footerCallback: LoadStateFooterCallback = ProgressLoadStateFooter
) : RecyclerView.Adapter<LoadStateFooterAdapter.FooterViewHolder>() {

    sealed class LoadState {
        class NotLoading private constructor(loadComplete: Boolean) : LoadState() {
            companion object {
                val Complete = NotLoading(loadComplete = true)
                val InComplete = NotLoading(loadComplete = false)
            }
        }

        object Loading : LoadState()
        object Error : LoadState()
    }

    private var loadState: LoadState = LoadState.Loading
    private var callback: OnLoadMore? = null

    var enable: Boolean = true
        set(value) = when (value) {
            field -> {}
            true -> {
                field = value
                notifyItemInserted(0)
            }
            false -> {
                field = value
                notifyItemRemoved(0)
            }
        }

    fun doOnLoadMore(action: OnLoadMore) {
        callback = action
    }

    private val observer = Observer<LoadState> { newState ->
        when {
            newState == loadState -> {}
            else -> {
                loadState = newState
                notifyDataSetChanged()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterViewHolder {
        return FooterViewHolder(footerCallback.footerView(parent))
    }

    override fun onBindViewHolder(holder: FooterViewHolder, position: Int) {
        when (loadState) {
            LoadState.Loading -> footerCallback.onLoading(holder.itemView)
            LoadState.NotLoading.Complete -> footerCallback.onComplete(holder.itemView)
            LoadState.Error -> footerCallback.onError(holder.itemView)
            else -> {}
        }
    }

    override fun getItemCount(): Int = if (enable) 1 else 0

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val lastVisible = recyclerView.findViewHolderForAdapterPosition(lastVisiblePosition)
                lastVisible?.takeIf { it is FooterViewHolder }?.let {
                    if (loadState != LoadState.NotLoading.Complete) callback?.invoke()
                }

            }
        })
        state.observeForever(observer)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        state.removeObserver(observer)
    }


    class FooterViewHolder(val root: View) : RecyclerView.ViewHolder(root)

}

interface LoadStateFooterCallback {

    fun footerView(parent: ViewGroup): View

    fun onLoading(view: View)

    fun onError(view: View)

    fun onComplete(view: View)
}

object ProgressLoadStateFooter : LoadStateFooterCallback {

    override fun footerView(parent: ViewGroup): View =
        LayoutInflater.from(parent.context).inflate(R.layout.item_rv_progress_footer_layout, parent, false)


    override fun onLoading(view: View) {

        view.isVisible = true
        view.findViewById<ProgressBar>(R.id.progress_circular).isVisible = true
        view.findViewById<TextView>(R.id.tv_done).isVisible = false
        view.findViewById<TextView>(R.id.tv_failed).isVisible = false
    }

    override fun onError(view: View) {
        view.isVisible = true
        view.findViewById<ProgressBar>(R.id.progress_circular).isVisible = false
        view.findViewById<TextView>(R.id.tv_done).isVisible = false
        view.findViewById<TextView>(R.id.tv_failed).isVisible = true

    }

    override fun onComplete(view: View) {
        view.isVisible = true
        view.findViewById<ProgressBar>(R.id.progress_circular).isVisible = false
        view.findViewById<TextView>(R.id.tv_done).isVisible = true
        view.findViewById<TextView>(R.id.tv_failed).isVisible = false
    }

}

class PagingFooterAdapter : LoadStateAdapter<LoadStateFooterAdapter.FooterViewHolder>() {

    private var _onRetry: (() -> Unit)? = null

    fun doOnRetry(action: () -> Unit) {
        _onRetry = action
    }

    override fun onBindViewHolder(holder: LoadStateFooterAdapter.FooterViewHolder, loadState: LoadState) {
        when {
            loadState is LoadState.NotLoading && loadState.endOfPaginationReached -> ProgressLoadStateFooter.onComplete(holder.itemView)
            loadState is LoadState.NotLoading && !loadState.endOfPaginationReached -> holder.itemView.isVisible = false
            loadState is LoadState.Error -> ProgressLoadStateFooter.onError(holder.itemView)
            loadState is LoadState.Loading -> ProgressLoadStateFooter.onLoading(holder.itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateFooterAdapter.FooterViewHolder {
        return LoadStateFooterAdapter.FooterViewHolder(ProgressLoadStateFooter.footerView(parent)).apply {
            itemView.setOnClickListener { _onRetry?.invoke() }
        }
    }

}