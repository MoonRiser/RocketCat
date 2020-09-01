package com.example.rocketcat.customview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.common.ext.ClickCallback
import com.example.rocketcat.R
import com.example.rocketcat.data.db.entity.Division
import com.github.promeg.pinyinhelper.Pinyin


/**
 * @Author:         Xres
 * @CreateDate:     2020/8/20 11:21
 * @Description:
 */
class AddressAdapter : RecyclerView.Adapter<AddressAdapter.MyViewHolder>() {

    private lateinit var context: Context
    var clickListener: ClickCallback? = null

    //当前的首字母
    private var currentCap: String = ""

    private var dataList = mutableListOf<Division>()

    fun getDataList(): List<Division> = dataList
    fun setDataList(list: List<Division>) {
        dataList.clear()
        dataList.addAll(list)
        sortAddressName(dataList)
        notifyDataSetChanged()
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
            nameView.text = dataList[position].name()
            root.tag = position//给view加上标签，方便监听
            root.setOnClickListener(clickListener)
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