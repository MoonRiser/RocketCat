package com.example.common.widget

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.animation.*
import android.widget.CompoundButton
import androidx.annotation.CallSuper
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import androidx.core.graphics.drawable.toBitmap
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.common.R
import com.example.common.ext.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class LikeButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CompoundButton(context, attrs, defStyleAttr) {


    companion object {
        const val TAG = "LikeButton"
        val COLOR_RED by lazy { "#ee0290".color }
        val COLOR_PURPLE by lazy { "#cd00ea".color }
        val COLOR_GREY by lazy { "#BDBDBD".color }
    }

    private var centerX = 0
    private var centerY = 0
    private var radius = 0f

    private val heart: Bitmap? =
        AppCompatResources.getDrawable(context, R.drawable.ic_heart)?.toBitmap()

    private val valueProducer = ValueProducer(2000) {
        invalidate()
    }

    /**
     * ❤️的画笔
     */
    private val hPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private var hScale = 1.0f


    //圆的画笔
    private val rPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val rgbEvaluator = ArgbEvaluator()
    private var rScale = 0f
    private var rColor = COLOR_RED


    //气泡的画笔
    private val bPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    init {
        valueProducer.valueConsume(0f, 0.3f) { t, isEnd ->
            val rt = DecelerateInterpolator().getInterpolation(t)
            hScale = 1f - rt
        }.valueConsume(0.3f, 0.7f) { t, isEnd ->
            rScale = t
            rColor = rgbEvaluator.evaluate(t, COLOR_RED, COLOR_PURPLE) as Int
        }.valueConsume(0.5f, 0.9f) { t, isEnd ->
            hScale = BounceInterpolator().getInterpolation(t)
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2
        centerY = h / 2
        radius = (if (w > h) h else w) / 2f
    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)


        if (!isChecked) {
            drawScaleHeart(1f, canvas, COLOR_GREY)
        } else {
            drawColorRound(canvas)
            drawScaleHeart(hScale, canvas)
        }

    }

    @CallSuper
    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        if (isPressed)
            if (isChecked) {
                valueProducer.start()
            } else {
                valueProducer.cancel()
            }
    }


    private fun drawScaleHeart(scale: Float, canvas: Canvas, color: Int = COLOR_RED) {
        heart?.let {
            Log.i("xres1", scale.toString())
            val old = RectF(0f, 0f, it.width.toFloat(), heart.height.toFloat())
            hPaint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(heart, null, old.scale(scale), hPaint)
        }
    }

    private fun drawColorRound(canvas: Canvas) {
        val mRadius = rScale * radius
        val t = if (rScale < 0.6f) 1f else +2.5f * (1 - rScale)
        rPaint.color = rColor
        canvas.drawRing(centerX.toFloat(), centerY.toFloat(), mRadius, mRadius * t, rPaint)

    }


}