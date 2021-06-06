package com.xres.address_selector.widget.address_selector

import android.graphics.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RestrictTo
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.promeg.pinyinhelper.Pinyin
import com.xres.address_selector.R
import com.xres.address_selector.db.entity.Division
import com.xres.address_selector.db.entity.Street
import com.xres.address_selector.ext.ClickCallback
import com.xres.address_selector.ext.color
import com.xres.address_selector.ext.dp
import kotlin.math.min


/**
 * @Author:         Xres
 * @CreateDate:     2020/8/20 11:21
 * @Description:
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class AddressAdapter : RecyclerView.Adapter<AddressAdapter.MyViewHolder>() {


    var clickListener: ClickCallback? = null
    var preparedListener: DataPreparedListener? = null
    var visibleListener: HostVisibleListener? = null
    private var currentClickPosition: Int = -1
    var selectedColor: Int = Color.BLUE

    val capIndexMap = mutableMapOf<String, Int>()

    private var dataList = mutableListOf<Division>()
    fun getDataList(): List<Division> = dataList
    fun setDataList(list: List<Division>) {
        dataList.clear()
        dataList.addAll(list)
        //最后一级，街道级别需要显示“暂不选择”，
        if (list.isNotEmpty()) {
            if (list.first() is Street) {
                dataList.add(Street("0", "暂不选择", "0"))
            }
        }
        sortAddressName(dataList)
        computeCapIndex()
        if (capIndexMap.isNotEmpty()) {
            preparedListener?.onPrepared(capIndexMap)
        }

        notifyDataSetChanged()
    }

    fun resetClickPosition() {
        currentClickPosition = -1
    }


    class MyViewHolder(val root: View) : RecyclerView.ViewHolder(root) {

        val nameView: TextView = root.findViewById(R.id.tvAddressName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.division_name_item_layout, parent, false)
            .let {
                MyViewHolder(it)
            }.apply {
                nameView.setOnClickListener {
                    currentClickPosition = adapterPosition
                    notifyDataSetChanged()
                    clickListener?.onClick(this.nameView)
                }

            }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.nameView.apply {
            val color = if (currentClickPosition == position) selectedColor else Color.BLACK
            setTextColor(color)
            text = dataList[position].name
            tag = position//给view加上标签，方便监听
        }


    }


    override fun getItemCount() = dataList.size


    /**
     * 对传入的省市县街道名称按照拼音进行排序
     */
    private fun sortAddressName(list: MutableList<Division>) {
        list.sortWith { str0, str1 ->
            str0.name.getPinYinFirstCap().compareTo(str1.name.getPinYinFirstCap())
        }
    }

    /**
     * 计算需要显示的首字母索引
     */
    private fun computeCapIndex() {
        capIndexMap.clear()
        var current = ""
        dataList.map { it.name.getPinYinFirstCap() }.forEachIndexed { index, s ->
            if (s != current) {
                capIndexMap[s] = index
                current = s
            }
        }

    }

}

typealias HostVisibleListener = () -> Unit

interface DataPreparedListener {
    fun onPrepared(map: Map<String, Int>)
}

/**
 * 获取汉字对应拼音的首字母
 */
fun String.getPinYinFirstCap() = Pinyin.toPinyin(this, "/").first().toString()

class MyDecoration(private val capMap: Map<String, Int>) : RecyclerView.ItemDecoration() {

    //    val firstCap: String = dataList[position].name.getPinYinFirstCap()
    private val mBounds = Rect()
    private var floatText = ""

    companion object {
        private val PADDING = 32f.dp
        private val CAP_HEIGHT = 36f.dp
        private val BOTTOM_LINE_HEIGHT = 1f.dp
        private val CAP_TEXT_POSITION = 24f.dp
        private const val CAP_BG_COLOR = "#F4F4F4"
        private const val TEXT_COLOR = "#607D8B"

    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = CAP_BG_COLOR.color
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = TEXT_COLOR.color
        textSize = 18f.dp.toFloat()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        c.save()
        parent.children.forEach { item ->
            parent.getDecoratedBoundsWithMargins(item, mBounds)
            val rect = mBounds.run {
                Rect(left + PADDING, bottom, right - PADDING, bottom + BOTTOM_LINE_HEIGHT)
            }
            //画分割线
            c.drawRect(rect, paint)
            //画文字
            val position = parent.getChildLayoutPosition(item)
            val text = getCapByPosition(position)
            if (text != null) {
                val rect1 = mBounds.run {
                    Rect(left, top, right, top + CAP_HEIGHT)
                }
                c.drawRect(rect1, paint)
                c.drawText(
                    text,
                    (mBounds.left + PADDING).toFloat(),
                    mBounds.top.toFloat() + CAP_TEXT_POSITION,
                    textPaint
                )
            }
        }
        c.restore()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        c.save()
        val firstVisible =
            (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val firstCompleteVisible =
            (parent.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        floatText = getCapByPosition(firstVisible) ?: floatText

        parent.findViewHolderForAdapterPosition(firstCompleteVisible)?.itemView?.let {
            val top = Rect().apply { parent.getDecoratedBoundsWithMargins(it, this) }.top
            val bott =
                if (!capMap.containsValue(firstCompleteVisible)) CAP_HEIGHT else min(
                    top,
                    CAP_HEIGHT
                )
            val rect = Rect(0, bott - CAP_HEIGHT, parent.width, bott)
            //画首字母背景
            c.drawRect(rect, paint)
            //画首字母
            c.drawText(
                floatText,
                PADDING.toFloat(),
                bott - CAP_HEIGHT + CAP_TEXT_POSITION.toFloat(),
                textPaint
            )
        }
        c.restore()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
        //预留首字母空间
        if (capMap.containsValue(position)) {
            outRect.top = CAP_HEIGHT
        }
        //预留下划线
        outRect.bottom = BOTTOM_LINE_HEIGHT
    }

    private fun getCapByPosition(firstVisible: Int): String? =
        capMap.filter { map -> map.value == firstVisible }.keys.firstOrNull()


}