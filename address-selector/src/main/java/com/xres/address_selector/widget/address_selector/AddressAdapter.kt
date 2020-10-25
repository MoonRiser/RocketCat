package com.xres.address_selector.widget.address_selector

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RestrictTo
import androidx.recyclerview.widget.RecyclerView
import com.xres.address_selector.ext.ClickCallback
import com.github.promeg.pinyinhelper.Pinyin
import com.xres.address_selector.R
import com.xres.address_selector.db.entity.Division
import com.xres.address_selector.db.entity.Street


/**
 * @Author:         Xres
 * @CreateDate:     2020/8/20 11:21
 * @Description:
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class AddressAdapter : RecyclerView.Adapter<AddressAdapter.MyViewHolder>() {

    private lateinit var context: Context
    var clickListener: ClickCallback? = null
    var preparedListener: DataPreparedListener? = null
    private var currentClickPosition: Int = -1
    var selectedColor: Int = Color.BLUE

    private val capIndexMap = mutableMapOf<String, Int>()

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
        val capView: TextView = root.findViewById(R.id.tvCap)
        val divider: View = root.findViewById(R.id.divider)
        val nameView: TextView = root.findViewById(R.id.tvAddressName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater.from(parent.context).inflate(R.layout.item_address_pinyin, parent, false)
            .let {
                context = parent.context
                MyViewHolder(it)
            }.apply {
                nameView.setOnClickListener {
                    currentClickPosition = adapterPosition
                    notifyDataSetChanged()
                    clickListener?.onClick(this.nameView)
                }

            }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val firstCap: String = dataList[position].name.getPinYinFirstCap()
        holder.apply {
            if (capIndexMap[firstCap] == position) {
                capView.visibility = View.VISIBLE
                divider.visibility = View.VISIBLE
                capView.text = firstCap
            } else {
                capView.visibility = View.GONE
                divider.visibility = View.GONE
            }
            nameView.apply {
                val color = if (currentClickPosition == position) {
                    selectedColor
                } else {
                    Color.BLACK
                }
                setTextColor(color)
                text = dataList[position].name
                tag = position//给view加上标签，方便监听
            }

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

interface DataPreparedListener {
    fun onPrepared(map: Map<String, Int>)
}


/**
 * 获取汉字对应拼音的首字母
 */
fun String.getPinYinFirstCap() = Pinyin.toPinyin(this, "/").first().toString()