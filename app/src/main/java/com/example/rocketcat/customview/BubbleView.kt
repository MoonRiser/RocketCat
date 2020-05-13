package com.example.rocketcat.customview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.example.rocketcat.R
import java.util.*

class BubbleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var size: PointF? = null
    private val maxDist: Float
    private val bbPaint: Paint
    private val bandPaint: Paint
    private val bubbles: MutableList<Bubble> = ArrayList()
    lateinit var states: BooleanArray //false远离；true接近
    private val bubbleNum: Int
    private var anim: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private var tg = 0f

    internal inner class Bubble(var center: PointF, var radius: Int) {


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initView(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in bubbles.indices) {
            for (j in i + 1 until bubbles.size) {
                if (isApproach(bubbles[i], bubbles[j])) {
//                    states[i] = true;
//                    states[j] = true;
                    val path =
                        getBezPathBetween2dots(bubbles[i], bubbles[j])
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
            //        anim.setRepeatMode(ValueAnimator.RESTART);
//        anim.setRepeatCount(-1);
            addUpdateListener(AnimatorUpdateListener { animation -> //设置当前绘制的爆炸图片index
                //mCurDrawableIndex = (int) animation.getAnimatedValue();
                tg = animation.animatedValue as Float
                invalidate()
            })
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
        val bx =
            (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x
        val by =
            (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y
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
        val h = Math.abs(f2.x - f1.x) //水平边
        val v = Math.abs(f2.y - f1.y) //竖边
        val x =
            Math.sqrt(h * h + v * v.toDouble()).toFloat() //斜边。勾股定理
        val cos0 = h / x
        val sin0 = v / x
        val Ax = f1.x - b1.radius * sin0
        val Ay = f1.y - b1.radius * cos0
        val Dx = f1.x + b1.radius * sin0
        val Dy = f1.y + b1.radius * cos0
        val Bx = f2.x - b2.radius * sin0
        val By = f2.y - b2.radius * cos0
        val Cx = f2.x + b2.radius * sin0
        val Cy = f2.y + b2.radius * cos0
        val oval1 =
            RectF(f1.x - b1.radius, f1.y - b1.radius, f1.x + b1.radius, f1.y + b1.radius)
        val oval2 =
            RectF(f2.x - b1.radius, f2.y - b1.radius, f2.x + b1.radius, f2.y + b1.radius)
        val sita1 =
            (Math.PI - Math.asin(sin0.toDouble())).toFloat()
        val path = Path()
        path.reset()
        path.moveTo(Ax, Ay)
        path.quadTo(anchorX, anchorY, Bx, By)
        //        path.addArc(oval2, sita1 + 180, 180);
        path.lineTo(Cx, Cy)
        path.quadTo(anchorX, anchorY, Dx, Dy)
        //        path.addArc(oval1, 90 - sita1, 180);
        path.close()
        return path
    }

    companion object {
        private const val MAX_RADIUS = 120
    }

    init {
        val array =
            context.obtainStyledAttributes(attrs, R.styleable.BubbleView, defStyleAttr, 0)
        maxDist = 4 * array.getDimension(R.styleable.BubbleView_bubble_radius, 20f)
        val bubbleColor =
            array.getColor(R.styleable.BubbleView_bubble_color, Color.BLUE)
        bubbleNum = array.getInteger(R.styleable.BubbleView_bubble_num, 4)
        array.recycle()
        bandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bandPaint.color = bubbleColor
        bandPaint.alpha = 128
        bandPaint.style = Paint.Style.FILL
        bbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bbPaint.color = bubbleColor
        bbPaint.style = Paint.Style.FILL
    }
}