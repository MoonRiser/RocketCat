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
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.common.R
import com.example.common.ext.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sinh


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
        val BUBBLE_RADIUS = 4f.dp.toFloat()
    }

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    private val heart: Bitmap? =
        AppCompatResources.getDrawable(context, R.drawable.ic_heart)?.toBitmap()

    private val valueProducer = ValueProducer(1500) {
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
        color = COLOR_RED
    }
    private val rgbEvaluator = ArgbEvaluator()
    private var rScale = 0f
    private var rColor = COLOR_RED


    //气泡的画笔
    private val bPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.MAGENTA
    }
    private val sBubbles = MutableList(7) { Bubble(0f, 0f, BUBBLE_RADIUS) }
    private val lBubbles = MutableList(7) { Bubble(0f, 0f, BUBBLE_RADIUS) }
    private var enableBubblesS = false
    private var enableBubblesL = false


    init {
        valueProducer.valueConsume(0f, 0.3f, DecelerateInterpolator()) { t, isEnd ->
            hScale = 1f - t
        }.valueConsume(0.3f, 0.7f) { t, isEnd ->
            rScale = t
            rColor = rgbEvaluator.evaluate(t, COLOR_RED, COLOR_PURPLE) as Int
        }.valueConsume(0.6f, 1.0f, BounceInterpolator()) { t, isEnd ->
            hScale = t
        }.valueConsume(0.4f, 0.9f, FastOutSlowInInterpolator()) { t, isEnd ->

            if (t in 0.6f..1f) {
                enableBubblesS = !isEnd
                calculateBubblesPosition(t, PI.toFloat() / 3f, sBubbles)

            }

        }.valueConsume(0.4f, 1.0f, FastOutSlowInInterpolator()) { t, isEnd ->

            if (t in 0.6f..1f) {
                enableBubblesL = !isEnd
                calculateBubblesPosition(t, PI.toFloat() / 6f, lBubbles)
            }

        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = (if (w > h) h else w) / 2f


    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)


        if (!isChecked) {
            drawScaleHeart(1f, canvas, COLOR_GREY)
        } else {
            drawColorRound(canvas)
            drawScaleHeart(hScale, canvas)
            if (enableBubblesS)
                sBubbles.forEach {
                    canvas.drawCircle(it.x, it.y, it.radius, bPaint)
                }
            if (enableBubblesL)
                lBubbles.forEach {
                    canvas.drawCircle(it.x, it.y, it.radius, bPaint)
                }
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


    private fun calculateBubblesPosition(t: Float, bias: Float, target: List<Bubble>) {
        val radius = this.radius * t
        val angleUnit = 2 * PI / 7
        (1..7).map { it * angleUnit + bias }.map { sita ->
            val x: Float = centerX + radius * sin(sita).toFloat()
            val y: Float = centerY - radius * cos(sita).toFloat()
            x to y
        }.forEachIndexed { index, pair ->
            target[index].apply {
                x = pair.first
                y = pair.second
            }
        }

    }

    private data class Bubble(
        var x: Float,
        var y: Float,
        val radius: Float,
        var color: Int = Color.BLUE
    )


}