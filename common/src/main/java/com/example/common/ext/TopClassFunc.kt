package com.example.common.ext

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import kotlin.math.roundToInt


fun showToast(context: Context, msg: String) =
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

fun Bitmap.blurBitmap(
    context: Context,
    blurRadius: Float,
    bitmapScale: Float = 0.8f,
): Bitmap {
    // 计算图片缩小后的长宽
    val width = (this.width * bitmapScale).roundToInt()
    val height = (this.height * bitmapScale).roundToInt()

    // 将缩小后的图片做为预渲染的图片
    val inputBitmap = Bitmap.createScaledBitmap(this, width, height, false)
    // 创建一张渲染后的输出图片
    val outputBitmap = Bitmap.createBitmap(inputBitmap)

    // 创建RenderScript内核对象
    val rs = RenderScript.create(context)
    // 创建一个模糊效果的RenderScript的工具对象
    val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

    // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间
    // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)

    // 设置渲染的模糊程度, 25f是最大模糊度
    blurScript.setRadius(blurRadius)
    // 设置blurScript对象的输入内存
    blurScript.setInput(tmpIn)
    // 将输出数据保存到输出内存中
    blurScript.forEach(tmpOut)

    // 将数据填充到Allocation中
    tmpOut.copyTo(outputBitmap)
    inputBitmap.recycle()
    rs.destroy()
    blurScript.destroy()
    return outputBitmap
}


/**
 * 以字符串中的书名号《》，匹配索引，
 * e.g. 保护《用户协议》和《隐私协议》；
 * @param color 书名号中的内容的颜色（包括书名号）
 * @param listener 点击监听器，有几对书名号，回调里的index依次对应
 */
fun string2SpannableStringBuilder(
    rawString: String,
    @ColorInt color: Int,
    showSymbol: Boolean = true,
    listener: SpanClickListener? = null
): SpannableStringBuilder {

//以书名号来匹配，从而设置Span的颜色和点击事件
    val key1 = "《"
    val key2 = "》"

    var content = rawString

    val list1 = rawString.searchAllIndex(key1)
    val list2 = rawString.searchAllIndex(key2)
    if (!showSymbol) {
        content = rawString.replace("《", "").replace("》", "")
    }

    val builder = SpannableStringBuilder(content)
    list1.forEachIndexed { index, _ ->
        if (index < list2.size) {
            val textSpan = object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }

                override fun onClick(widget: View) {
                    listener?.onSpanClick(index)
                }
            }
            val foregroundColorSpan = object : UnderlineSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.color = color//设置颜色
                    ds.isUnderlineText = false//去掉下划线
                }
            }
            builder.apply {
                //如果不显示书名号，定位需要一个偏移
                val offset1 = if (showSymbol) {
                    0
                } else {
                    -2 * index
                }
                val offset2 = if (showSymbol) {
                    1
                } else {
                    -2 * (index + 1) + 1
                }
                setSpan(
                    textSpan,
                    list1[index] + offset1,
                    list2[index] + offset2,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    foregroundColorSpan,
                    list1[index] + offset1,
                    list2[index] + offset2,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
    return builder
}

fun string2SpannableStringBuilder(rawString: String, @ColorInt color: Int): SpannableStringBuilder {

    var content = rawString
    val builder = SpannableStringBuilder(content)
    val textSpan = object : UnderlineSpan() {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun updateDrawState(ds: TextPaint) {
            ds.underlineColor = color
            ds.isUnderlineText = true
        }
    }
    builder.setSpan(textSpan, 0, content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return builder
}

fun String.searchAllIndex(key: String): List<Int> {
    val list = arrayListOf<Int>()
    var a: Int = this.indexOf(key) //*第一个出现的索引位置
    while (a != -1) {
        list.add(a)
        a = this.indexOf(key, a + 1) //*从这个索引往后开始第一个出现的位置
    }
    return list
}

fun interface SpanClickListener {
    fun onSpanClick(index: Int)
}