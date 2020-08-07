package com.example.rocketcat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.rocketcat.R

/**
 * @Author:         Xres
 * @CreateDate:     2020/5/25 17:27
 * @Description:
 */
class ChoiceDialogAdapter(val viewPager2: ViewPager2) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DIALOG_ONE = 1
    private val DIALOG_TWO = 2


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == DIALOG_ONE) {
            MyViewHolder(inflater.inflate(R.layout.dialog_one, parent, false))
        } else {
            MyViewHolder(inflater.inflate(R.layout.dialog_two, parent, false))
        }
    }

    override fun getItemCount() = 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemViewType(position: Int) = if (position == 0) {
        DIALOG_ONE
    } else {
        DIALOG_TWO
    }


}