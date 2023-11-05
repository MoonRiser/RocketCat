package com.example.common.dsl

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.TouchDelegate
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.ext.isInstance

class StickyDecoration(val viewType: Int) : RecyclerView.ItemDecoration() {


    private var stickyView: View? = null
    private var isFirst = true
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val realAdapter = (parent.adapter as ConcatAdapter).adapters.find { a -> a is BindingPagingAdapter }
        val target = parent.children.find {
            val pos = parent.findContainingViewHolder(it)?.bindingAdapterPosition ?: -1
            if (pos.toLong() == RecyclerView.NO_ID) return
            if ((realAdapter?.itemCount ?: 0) > 0) realAdapter?.getItemViewType(pos) == viewType else false
        }
        target?.let {
            stickyView = it
        }
        parent.layoutManager?.isInstance<LinearLayoutManager> {
            stickyView?.let {
                if (isFirst) {
                    isFirst = false
                    val rect = Rect()
                    it.getHitRect(rect)
                    val delegate = TouchDelegate(Rect(rect.left, 0, rect.right, rect.bottom - rect.top), it)
                    Log.i("kkp", "rect is $rect")
                    parent.touchDelegate = delegate
                }
                val fcv = findFirstCompletelyVisibleItemPosition()
                val pos = parent.getChildAdapterPosition(it)
                if (pos <= fcv) {
                    Log.i("kkp", "layout ${it.left}, ${it.top}, ${it.right}, ${it.bottom}")
                    it.draw(c)
                }
            }


        }
    }


}