package com.example.common.widget

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.common.R
import com.example.common.data.db.entity.Division
import com.example.common.data.db.entity.Street
import com.example.common.ext.ClickCallback
import com.github.promeg.pinyinhelper.Pinyin


/**
 * @Author:         Xres
 * @CreateDate:     2020/8/20 11:21
 * @Description:
 */
class AddressAdapter : RecyclerView.Adapter<AddressAdapter.MyViewHolder>() {

    private lateinit var context: Context
    var clickListener: ClickCallback? = null
    private var currentClickPosition: Int = -1
    var selectedColor: Int = Color.BLUE

    //当前的首字母
    private var currentCap: String = ""

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
        notifyDataSetChanged()
    }

    fun resetClickPosition() {
        currentClickPosition = -1
    }


    class MyViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
        val capView: TextView = root.findViewById(R.id.tvCap)
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
        val firstCap: String = dataList[position].name().getPinYinFirstCap()
        holder.apply {
            if (currentCap != firstCap) {
                capView.text = firstCap
                currentCap = firstCap
            } else {
                capView.text = ""
            }
            nameView.apply {
                val color = if (currentClickPosition == position) {
                    selectedColor
                } else {
                    Color.BLACK
                }
                setTextColor(color)
                text = dataList[position].name()
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
            str0.name().getPinYinFirstCap().compareTo(str1.name().getPinYinFirstCap())
        }
    }


}


/**
 * 获取汉字对应拼音的首字母
 */
fun String.getPinYinFirstCap() = Pinyin.toPinyin(this, "/").first().toString()