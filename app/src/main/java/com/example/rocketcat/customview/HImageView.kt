package com.example.rocketcat.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * @author xres
 * @createDate 12/15/20
 */

class HImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {


    //视觉所能看到的实际宽度
    private var visibleWidth: Int = 0

    //图片在内存中的宽度
    private var realWidth: Int = 0


    //页面的索引和偏移量、页数
    private var pageIndex = 0
    private var pageOffset = 0f
    var pageCount = 1
        set(value) {
            field = if (value > 0) value else 1
        }

    //供绘制的drawable
    private lateinit var myDrawable: Drawable


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        visibleWidth = w
        //获取图片的真实比例，然后按照设定的高，获得宽度
        realWidth = (h * 1.0f / drawable.intrinsicHeight * drawable.intrinsicWidth).toInt()
        myDrawable = drawable.apply {
            setBounds(0, 0, realWidth, h)
        }

    }

    override fun onDraw(canvas: Canvas) {

        canvas.save()
        canvas.translate(((pageOffset + pageIndex) / pageCount) * (visibleWidth - realWidth), 0f)
        super.onDraw(canvas)
        canvas.restore()

    }


    /**
     * @param pageIndex vp的页面索引
     * @param pageOffset vp的页面偏移量
     */
    fun setPageOffset(pageIndex: Int, pageOffset: Float) {

        this.pageIndex = pageIndex
        this.pageOffset = pageOffset
        invalidate()
    }


}