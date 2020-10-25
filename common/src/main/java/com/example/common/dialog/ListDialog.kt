package com.example.common.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.cardview.widget.CardView
import androidx.core.view.setPadding
import com.example.common.R
import com.example.common.ext.ClickCallback
import com.example.common.ext.dp

/**
 * @author xres
 * @createDate 10/25/20
 */


class ListDialog<T> private constructor(private val builder: Builder<T>, context: Context) :
    AppCompatDialog(context) {


    override fun onWindowAttributesChanged(params: WindowManager.LayoutParams) {
        params.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
        }
        super.onWindowAttributesChanged(params)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.run {
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        val linearLayout =
            LayoutInflater.from(context).inflate(R.layout.list_dialog_content, null).apply {
                findViewById<TextView>(R.id.tvTitle).text = builder.title
                findViewById<CardView>(R.id.btnCancel).setOnClickListener {
                    builder.btnListener?.onClick(it)
                    dismiss()
                }
                val parent = findViewById<LinearLayout>(R.id.llContent)
                builder.dataList.map {
                    TextView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        setPadding(0, 8f.dp, 0, 8f.dp)
                        gravity = Gravity.CENTER
                        text = it.toString()
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
                    }
                }.forEachIndexed { index, tv ->
                    if (index != 0) {
                        View(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                1f.dp
                            )
                            setBackgroundColor(Color.BLACK)
                        }.also { parent.addView(it) }
                    }
                    tv.setOnClickListener {
                        builder.itemListener?.onSelect(
                            index,
                            builder.dataList[index]
                        )
                    }
                    parent.addView(tv)
                }
            }

        setContentView(linearLayout)

    }


    class Builder<K>(private val context: Context) {

        var btnListener: ClickCallback? = null
            private set
        var itemListener: OnItemSelectListener<K>? = null
            private set
        var title = "请设置标题"
            private set
        val dataList = arrayListOf<K>()


        fun title(title: String) = apply {
            this.title = title
        }

        fun btnListener(listener: View.OnClickListener) = apply {
            this.btnListener = listener
        }

        fun itemListener(listener: OnItemSelectListener<K>) = apply {
            this.itemListener = listener
        }

        fun dataList(list: List<K>) = apply {
            dataList.clear()
            dataList.addAll(list)
        }


        fun build() = ListDialog<K>(this, context).apply {
            create()
        }

        fun show() {
            build().show()
        }

        interface OnItemSelectListener<K> {
            fun onSelect(index: Int, item: K)
        }

    }


}