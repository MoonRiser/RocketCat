package com.example.rocketcat.customview

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import com.example.common.ext.dpValue
import com.example.rocketcat.R
import com.example.rocketcat.adapter.CustomInterpolator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class BallsLoading
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    //小球颜色、数量（默认11个）、半径
    private var ballColor: Int
    private var ballNum: Int

    //单位：dp
    private var ballRadius: Int

    private var max_dis: Float = 0f
    private val paint: Paint
    private var centerX: Int = 0
    private var centerY: Int = 0

//    //0-1代表[0,2*PI]弧度
//    private var angle: Float = 0f

//    //值动画
//    private var animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)

    //颜色估值器
    private val evaluator = ArgbEvaluator()
    private val angles = arrayListOf<Float>()
    private val animators = arrayListOf<ValueAnimator>()
    private val balls = arrayListOf<Ball>()


    init {

        val array = context.obtainStyledAttributes(attrs, R.styleable.BallsLoading, defStyleAttr, 0)
        ballRadius = array.getInteger(R.styleable.BallsLoading_ball_radius, 4).toFloat().dpValue()
        ballColor = array.getColor(R.styleable.BallsLoading_ball_color, Color.BLUE)
        ballNum = array.getInteger(R.styleable.BallsLoading_ball_num, 11)
        array.recycle()

        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ballColor
            style = Paint.Style.FILL
        }


    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        centerX = w / 2
        centerY = w / 2
        max_dis = min(centerX, centerY).toFloat()
        repeat(ballNum) {
            balls.add(Ball(PointF(centerX.toFloat(), ballRadius.toFloat()), ballRadius))
            animators.add(ValueAnimator.ofFloat(0f, 1f))
            angles.add(0f)
        }
        animators.forEachIndexed { index, animator ->
            animator.apply {
                duration = 5000
                interpolator = CustomInterpolator(1 - 0.02f * index)
                repeatCount = Animation.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener {
                    angles[index] = (it.animatedValue as Float)
                    invalidate()
                }
            }
        }


    }

    override fun onDraw(canvas: Canvas) {

        angles.forEachIndexed { index, angle ->
            val position = getPositionOfCircleByAngle(
                angle,
                centerX.toFloat(),
                centerY.toFloat(),
                max_dis - ballRadius
            )
            val ball = balls[index].apply { center = position }
            paint.color = evaluator.evaluate(angle, Color.WHITE, ballColor) as Int
            canvas.drawCircle(ball.center.x, ball.center.y, ball.radius.toFloat(), paint)

        }


    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animators.forEach {
            it.cancel()
        }
    }


    fun switchState() {

        if (!animators[0].isStarted) {
            animators.forEach { it.start() }
            return
        }
        if (animators[0].isPaused) {
            animators.forEach { it.resume() }
        } else if (animators[0].isRunning) {
            animators.forEach { it.pause() }
        }
    }

    class Ball(var center: PointF, var radius: Int)

    //获取角度对应的圆上任意一点的位置/极坐标系
    private fun getPositionOfCircleByAngle(
        angle: Float,
        cx: Float,
        cy: Float,
        radius: Float
    ): PointF {

        val x = cx + radius * sin(2 * PI * angle)
        val y = cy - radius * cos(2 * PI * angle)
        return PointF(x.toFloat(), y.toFloat())

    }


}