package com.example.rocketcat.customview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import com.example.common.ext.dp
import com.example.common.ext.zipWithNextInLoop
import com.example.rocketcat.R
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt


class BubbleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val MAX_RADIUS = 120
    }

    private var size: PointF? = null
    private val maxDist: Float
    private val bbPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val bandPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        alpha = 128
        style = Paint.Style.FILL
    }

    private val bubbles: MutableList<Bubble> = ArrayList()
    lateinit var states: BooleanArray //false远离；true接近
    private val bubbleNum: Int
    var anim: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)

    //贝塞尔曲线的控制变量
    private var tg = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4.dp.toFloat()
    }
    var pathToDraw: Path = Path()
        set(value) {
            field = value
            invalidate()
        }

    @ColorInt
    var paintColor: Int = Color.BLUE
        set(value) {
            field = value
            paint.color = field
            invalidate()
        }

    /**
     * 图表模拟
     */
    private val chartColors = listOf("#55ff0000", "#5500ff00", "#550000ff").map { Color.parseColor(it) }
    private val chartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 2f.dp.toFloat()
        color = chartColors.first()
    }

    private val t1 = (1..9).map { (200..500).random() }
    private val t2 = t1.map { it + 80 * (0..3).random() }
    private val t3 = t2.map { it + 80 * (0..3).random() }

    private fun List<Int>.toPath(isLine: Boolean = true): Path {
        val itemWidth = width.toFloat() / (this.size + 1)
        val path = Path()
        val xs = (1..this.size).map { itemWidth * it }
        xs.zip(this).forEachIndexed { index, (x, y) ->
            if (index == 0) path.moveTo(x, y.toFloat())
            else path.lineTo(x, y.toFloat())
        }
        if (isLine) return path
        val b = bottom.toFloat()
        val fx = xs.first()
        val ex = xs.last()
        path.lineTo(ex, b)
        path.lineTo(fx, b)
        path.close()
        return path
    }

    private operator fun Path.minus(other: Path): Path {
        val result = Path()
        result.op(this, other, Path.Op.DIFFERENCE)
        return result
    }

    private val lines by lazy { listOf(t1.toPath(), t2.toPath(), t3.toPath()) }
    private val areas by lazy { listOf(t1.toPath(isLine = false), t2.toPath(isLine = false), t3.toPath(isLine = false)) }
    private val areas2 by lazy { areas.zipWithNextInLoop().mapIndexed { index, (c, n) -> if (index == areas.lastIndex) c else c - n } }


    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.BubbleView, defStyleAttr, 0)
        maxDist = 4 * array.getDimension(R.styleable.BubbleView_bubble_radius, 20f)
        val bubbleColor = array.getColor(R.styleable.BubbleView_bubble_color, Color.BLUE)
        bubbleNum = array.getInteger(R.styleable.BubbleView_bubble_num, 4)
        array.recycle()

        bandPaint.color = bubbleColor
        bbPaint.color = bubbleColor
    }

    class Bubble(var center: PointF, var radius: Int)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initView(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //小球动画
        for (i in bubbles.indices) {
            for (j in i + 1 until bubbles.size) {
                if (isApproach(bubbles[i], bubbles[j])) {
                    val path = getBezPathBetween2dots(bubbles[i], bubbles[j])
                    canvas.drawPath(path, bandPaint)
                }
            }
            if (!states[i]) {
                val b = getBezPathDot(tg, bubbles[i])
                canvas.drawCircle(b.x, b.y, bubbles[i].radius.toFloat(), bbPaint)
            } else {
                states[i] = false
            }
        }
        //图表模拟
        chartPaint.style = Paint.Style.FILL
        areas2.onEachIndexed { index, path ->
            chartPaint.color = chartColors[index]
            canvas.drawPath(path, chartPaint)
        }
        chartPaint.color = Color.YELLOW
        chartPaint.style = Paint.Style.STROKE
        lines.onEach { canvas.drawPath(it, chartPaint) }
        //手写痕迹
        canvas.drawPath(pathToDraw, paint)

    }

    private fun initView(w: Int, h: Int) {
        if (size == null) {
            size = PointF(w.toFloat(), h.toFloat())
        } else {
            size!![w.toFloat()] = h.toFloat()
        }
        bubbles.clear()
        states = BooleanArray(bubbleNum)
        for (i in 0 until bubbleNum) {
            states[i] = false
            val center = getRandomDot(i)
            val bubble = Bubble(center, 20 + 8 * i)
            bubbles.add(bubble)
        }
        anim.apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 15000
            addUpdateListener { animation -> //设置当前绘制的爆炸图片index
                //mCurDrawableIndex = (int) animation.getAnimatedValue();
                tg = animation.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    //修改动画执行标志
                    for (bubble in bubbles) {
                        bubble.center = (getP2(bubble))
                    }
                    anim.start()
                }
            })
            start()
        }

    }

    private fun getBezPathDot(t: Float, bubble: Bubble): PointF {
        val p0 = bubble.center
        val r = bubble.radius
        val p1 = getRandomDot((p0.x * p0.y).toInt())
        val p2 = getRandomDot((p0.x * p0.y * r).toInt())
        val bx = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x
        val by = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y
        return PointF(bx, by)
    }

    private fun getP2(bubble: Bubble): PointF {
        val p0 = bubble.center
        val r = bubble.radius
        return getRandomDot((p0.x * p0.y * r).toInt())
    }

    private fun getRandomDot(seed: Int): PointF {
        val random = Random(seed.toLong())
        val width =
            random.nextInt(size!!.x.toInt() - MAX_RADIUS) + MAX_RADIUS.toFloat()
        val height =
            random.nextInt(size!!.y.toInt() - MAX_RADIUS) + MAX_RADIUS.toFloat()
        return PointF(width, height)
    }

    private fun isApproach(b1: Bubble, b2: Bubble): Boolean {
        val f1 = getBezPathDot(tg, b1)
        val f2 = getBezPathDot(tg, b2)
        val distance = (f1.x - f2.x) * (f1.x - f2.x) + (f1.y - f2.y) * (f1.y - f2.y)
        return distance < maxDist * maxDist
    }

    private fun getBezPathBetween2dots(b1: Bubble, b2: Bubble): Path {
        val f1 = getBezPathDot(tg, b1) //圆心o1坐标
        val f2 = getBezPathDot(tg, b2) //圆心o2坐标
        val anchorX = (f1.x + f2.x) / 2
        val anchorY = (f1.y + f2.y) / 2
        val h = abs(f2.x - f1.x) //水平边
        val v = abs(f2.y - f1.y) //竖边
        val x = sqrt(h * h + v * v.toDouble()).toFloat() //斜边。勾股定理
        val cos0 = h / x
        val sin0 = v / x
        val ax = f1.x - b1.radius * sin0
        val ay = f1.y - b1.radius * cos0
        val dx = f1.x + b1.radius * sin0
        val dy = f1.y + b1.radius * cos0
        val bx = f2.x - b2.radius * sin0
        val by = f2.y - b2.radius * cos0
        val cx = f2.x + b2.radius * sin0
        val cy = f2.y + b2.radius * cos0

        val path = Path()
        path.reset()
        path.moveTo(ax, ay)
        path.quadTo(anchorX, anchorY, bx, by)
        //        path.addArc(oval2, sita1 + 180, 180);
        path.lineTo(cx, cy)
        path.quadTo(anchorX, anchorY, dx, dy)
        //        path.addArc(oval1, 90 - sita1, 180);
        path.close()
        return path
    }


}