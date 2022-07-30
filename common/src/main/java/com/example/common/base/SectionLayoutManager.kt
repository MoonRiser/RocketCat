package com.example.common.base

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Field
import java.util.*

/**
 * @author Rango on 2020/11/5
 */
class SectionLayoutManager @JvmOverloads constructor(
    context: Context,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {

    companion object {
        const val tag = "SectionLayoutManager"
    }


    /**
     * 最多吸顶个数
     */
    private val maxSectionCount = 1

    /**
     * 存储所有 section position
     * 在滚动的过程中进行更新这个已经实现，记录所有的Section的Position，但是有以下情况需要考虑
     * 1. adapter.notifyDataSetChanged()一系列方法调用的时候 更新问题
     * 2. scrollToPosition -- 更新问题
     * 3. 快速滚动的时候有些ViewHolder的绘制过程是省略的
     */
    private val sectionPositions: SortedSet<Int> = TreeSet()

    /**
     * position < firstVisibleItemPosition的SectionViewHolder
     * 存储已经吸顶的Section（或已经滚动过去的SectionViewHolder）
     */
    private val sectionCache: SectionCache = SectionCache()
//    fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
//        super.onLayoutChildren(recycler, state)
//        //        for (int i = 0; i < getChildCount(); i++) {
////            RecyclerView.ViewHolder vh = getViewHolderByView(getChildAt(i));
////            int position = vh.getLayoutPosition();
////            if (vh instanceof Section) {
////                sectionPositions.add(position);
////            } else {
////                sectionPositions.remove(position);
////            }
////        }
//    }

    private fun removeAllSections() {
        for (viewHolder in sectionCache) {
            removeView(viewHolder.itemView)
        }
    }

    /**
     * 整体思路，
     * 在手指上滑（向上滚动 dy > 0）的时候,SectionViewHolder不能进入Holder缓存，需要独立缓存
     * 在手指下滑（向下滚动 dy < 0）的时候，SectionViewHolder在离开吸顶位置的时候需要将该ViewHolder重新放入缓存池，
     * 正常显示在列表中
     *
     * @param dy       >0 是手指向上滑动
     * @param recycler
     * @param state
     * @return
     */
    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        for (i in 0 until childCount) {
            val itemView: View? = getChildAt(i)
            val vh: RecyclerView.ViewHolder? = getViewHolderByView(itemView)

            if (vh !is Section || sectionCache.peek() === vh) {
                continue
            }
            if (!vh.isStickyHeader()) {
                continue
            }
            if (dy > 0 && vh.itemView.top < dy) {
                sectionCache.push(vh)
                Log.i(tag, "搜集：dy=$dy")
            } else {
                break
            }
        }
        //拒绝进入系统的回收复用策略
        removeAllSections()
        val result: Int = super.scrollVerticallyBy(dy, recycler, state)
        val vh: RecyclerView.ViewHolder? = getViewHolderByView(getChildAt(0))
        val attachedSection: RecyclerView.ViewHolder? = sectionCache.peek()
        if (vh is Section && attachedSection?.layoutPosition == vh.layoutPosition && vh.isStickyHeader()
        ) {
            removeViewAt(0)
        }

        //检查栈顶 -- 同步状态
        for (removedViewHolder in sectionCache.clearTop(findFirstVisibleItemPosition())) {
            Log.i(tag, "移除ViewHolder:$removedViewHolder")
            for (i in 0 until childCount) {
                val attachedViewHolder: RecyclerView.ViewHolder? = getViewHolderByView(getChildAt(i))
                if (removedViewHolder.layoutPosition == attachedViewHolder?.layoutPosition) {
                    val attachedItemView: View = attachedViewHolder.itemView
                    val left: Int = attachedItemView.left
                    val top: Int = attachedItemView.top
                    val bottom: Int = attachedItemView.bottom
                    val right: Int = attachedItemView.right
                    removeView(attachedItemView)
                    addView(removedViewHolder.itemView, i)
                    removedViewHolder.itemView.layout(left, top, right, bottom)
                    break
                }
            }
        }
        val section: RecyclerView.ViewHolder? = sectionCache.peek()
        if (section != null) {
            val itemView: View = section.itemView
            if (!itemView.isAttachedToWindow) {
                addView(itemView)
                Log.i(tag, "添加ViewHolder:$section")
            }
            val subItem: View = getChildAt(1)!!
            val subVh = getViewHolderByView(subItem)
            if (subVh is Section && subVh.isStickyHeader()) {
                val h: Int = itemView.measuredHeight
                val top = (subItem.top - h).coerceAtMost(0)
                val bottom = subItem.top.coerceAtMost(h)
                itemView.layout(getDecoratedLeft(itemView), top, getDecoratedRight(itemView), bottom)
//                itemView.layout(0, top, itemView.measuredWidth, bottom)
                Log.i(tag, "layout1 : h:$h")
            } else {
                itemView.layout(
                    getDecoratedLeft(itemView),
                    itemView.marginTop,
                    getDecoratedRight(itemView),
                    itemView.marginTop + getDecoratedMeasuredHeight(itemView)
                )
//                itemView.layout(0, 0, itemView.measuredWidth, itemView.measuredHeight)
                Log.i(tag, "layout2: bounds${itemView.clipBounds}")
            }
        }
        return result
    }

//    override fun scrollToPosition(position: Int) {
//        super.scrollToPosition(position)
//        //TODO: 搜集ViewHolderSection
//    }
//
//    override fun smoothScrollToPosition(
//        recyclerView: RecyclerView?, state: RecyclerView.State?,
//        position: Int
//    ) {
//        super.smoothScrollToPosition(recyclerView, state, position)
//    }

    private fun getViewHolderByView(view: View?): RecyclerView.ViewHolder? {
        if (view == null) return null
        return try {
            val lp: RecyclerView.LayoutParams = view.layoutParams as RecyclerView.LayoutParams
            val viewHolderField: Field = lp.javaClass.getDeclaredField("mViewHolder")
            viewHolderField.isAccessible = true
            viewHolderField.get(lp) as RecyclerView.ViewHolder
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


/**
 * 吸顶ViewHolder的缓存
 *
 * @author Rango on 2020/11/17
 */
class SectionCache : Stack<RecyclerView.ViewHolder>() {
    private val filterMap: MutableMap<Int, RecyclerView.ViewHolder> = HashMap(16, 64f)
    override fun push(item: RecyclerView.ViewHolder): RecyclerView.ViewHolder {

        val position: Int = item.layoutPosition
        //避免存在重复的Value
        if (filterMap.containsKey(position)) {
            //返回null说明没有添加成功
            return filterMap[position]!!
        }
        filterMap[position] = item
        return super.push(item)
    }

    @Synchronized
    override fun peek(): RecyclerView.ViewHolder? {
        return if (size == 0) {
            null
        } else super.peek()
    }

    /**
     * 栈顶清理
     * 根据LayoutPosition清理
     *
     * @param layoutPosition 大于position的内容会被清理
     */
    fun clearTop(layoutPosition: Int): List<RecyclerView.ViewHolder> {
        val removedViewHolders: MutableList<RecyclerView.ViewHolder> = LinkedList()
        val it: MutableIterator<RecyclerView.ViewHolder> = iterator()
        while (it.hasNext()) {
            val top: RecyclerView.ViewHolder = it.next()
            if (top.layoutPosition > layoutPosition) {
                it.remove()
                filterMap.remove(top.layoutPosition)
                removedViewHolders.add(top)
            }
        }
        return removedViewHolders
    }
}

interface Section {
    fun isStickyHeader() = true
}