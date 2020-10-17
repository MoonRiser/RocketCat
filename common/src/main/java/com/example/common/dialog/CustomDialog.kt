package com.example.common.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.LifecycleOwner
import com.example.common.ext.DialogCallback
import com.example.common.ext.dp
import com.example.common.utils.getScreenSize


/**
 * @Author:         Xres
 * @CreateDate:     2020/5/18 10:44
 * @Description:
 */


open class CustomDialog(private val builder: Builder, context: Context) : AppCompatDialog(context) {


    private val observer = DialogLifecycleObserver(::dismiss)
    private val gradientDrawable = GradientDrawable().apply {
        setColor(Color.WHITE)
    }

    init {
        apply {
            setCanceledOnTouchOutside(builder.canBeCancelOutside)
            builder.windowsAnimation?.let { window?.setWindowAnimations(it) }
        }
        builder.lifecycleOwner?.lifecycle?.addObserver(observer)

    }

    override fun onAttachedToWindow() {
        window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setGravity(builder.gravity)
            attributes = attributes.apply {
                width = builder.fullScreenWidth.let {
                    if (it) {
                        ViewGroup.LayoutParams.MATCH_PARENT
                    } else {
                        (getScreenSize(context).x * 0.8f).toInt()
                    }
                }
                height = builder.ratioScreenHeight.let {
                    if (it == 0f) {
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    } else {
                        (getScreenSize(context).y * it).toInt()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val radius = builder.roundCorner.dp.toFloat()
        //设置圆角属性
        if (builder.bottomWithRoundCorner) {
            gradientDrawable.cornerRadius = radius
        } else {
            gradientDrawable.cornerRadii = floatArrayOf(
                radius,
                radius,
                radius,
                radius,
                0f,
                0f,
                0f,
                0f
            )
        }
        //根线性布局
        val linearLayout = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER_HORIZONTAL
            orientation = LinearLayout.VERTICAL
            background = gradientDrawable
        }

        val titleView = builder.title?.let {
            TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 25f.dp
                }
                text = builder.title
                gravity = Gravity.CENTER_HORIZONTAL
                setTextColor(Color.BLACK)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17f)
            }
        } ?: TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                10f.dp
            )
        }
        //中间布局、文字内容或者自定义布局二选一
        val middleContent: View = onCreateCustomView(context) ?: builder.inflater?.inflate(
            LayoutInflater.from(context), linearLayout
        ) ?: TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(30f.dp, 10f.dp, 30f.dp, 20f.dp)
            }
            text = builder.content
            minHeight = 56f.dp
            gravity = Gravity.CENTER_VERTICAL
            setLineSpacing(6f.dp.toFloat(), 1f)
            setTextColor(Color.parseColor("#353535"))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)

        }
        linearLayout.apply {
            addView(titleView)
            addView(middleContent)
        }
        //横向的分割线
        val divider1 = View(context).apply {
            layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1f.dp)
            setBackgroundColor(Color.parseColor("#dedfe0"))
        }
        //按钮布局
        val bottomLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 45f.dp)
            if (builder.bottomWithRoundCorner) {
                background = GradientDrawable().apply {
                    setColor(Color.WHITE)
                    cornerRadii = floatArrayOf(
                        0f,
                        0f,
                        0f,
                        0f,
                        radius,
                        radius,
                        radius,
                        radius
                    )
                }
            }
        }
        val tvLeft = builder.textLeft?.let {
            TextView(context).apply {
                text = builder.textLeft
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                setTextColor(builder.textLeftColor)
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1F
                )
                setOnClickListener {
                    builder.leftOnClickListener?.onClick(this@CustomDialog) ?: dismiss()
                }
            }
        }
        val tvRight = builder.textRight?.let {

            TextView(context).apply {
                text = it
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                setTextColor(builder.textRightColor)
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1F
                )
                setOnClickListener {
                    builder.rightOnClickListener?.onClick(this@CustomDialog)
                }

            }
        }
        val divider2 = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(1f.dp, 24f.dp)
                .apply { gravity = Gravity.CENTER }
            setBackgroundColor(Color.parseColor("#dedfe0"))
        }
        val button = tvLeft?.also {
            linearLayout.addView(divider1)
            bottomLayout.addView(tvLeft)
            tvRight?.also {
                bottomLayout.addView(divider2)
                bottomLayout.addView(tvRight)
            }
        } ?: tvRight?.also {
            linearLayout.addView(divider1)
            bottomLayout.addView(tvRight)
        }
        val root = linearLayout.apply {
            button?.let {
                addView(bottomLayout)
            }
        }
        setContentView(root)
    }

    override fun onStop() {
        super.onStop()
        builder.lifecycleOwner?.lifecycle?.removeObserver(observer)

    }

    open fun onCreateCustomView(context: Context): View? = null

    open class Builder(val context: Context) {


        var title: String? = null
            private set
        var subTitle: String? = null
            private set
        var content: String = "assert content not be empty"
            private set
        var textLeft: String? = null
            private set
        var textRight: String? = null
            private set
        var leftOnClickListener: DialogCallback? = null
            private set
        var rightOnClickListener: DialogCallback? = null
            private set
        var inflater: CustomViewInflater? = null
            private set

        var canBeCancelOutside: Boolean = true
            private set

        var windowsAnimation: Int? = null
            private set

        var gravity: Int = Gravity.CENTER
            private set

        var bottomWithRoundCorner: Boolean = true
            private set

        var fullScreenWidth: Boolean = false
            private set

        /**
         * 写死dialog高度占屏幕高度的百分比，当为0时，表示WRAP_CONTENT
         */
        var ratioScreenHeight: Float = 0f
            private set

        @ColorInt
        var titleColor: Int = Color.parseColor("#FF353535")
            private set


        @ColorInt
        var bgColor: Int = Color.parseColor("#EEFFFFFF")
            private set

        @ColorInt
        var textLeftColor: Int = Color.parseColor("#FF353535")
            private set

        @ColorInt
        var textRightColor: Int = Color.parseColor("#FF0096DF")
            private set

        var roundCorner: Float = 16f
            private set

        var lifecycleOwner: LifecycleOwner? = null
            private set

        fun subTitle(subTitle: String) = apply { this.subTitle = subTitle }
        fun title(title: String) = apply { this.title = title }
        fun content(content: String) = apply { this.content = content }
        fun textLeft(textLeft: String) = apply { this.textLeft = textLeft }
        fun textRight(textRight: String) = apply { this.textRight = textRight }
        fun leftOnClickListener(callback: DialogCallback) =
            apply { this.leftOnClickListener = callback }

        fun rightOnClickListener(callback: DialogCallback) =
            apply { this.rightOnClickListener = callback }

        fun customView(inflater: CustomViewInflater) = apply { this.inflater = inflater }
        fun bgColor(@ColorInt bgColor: Int) = apply { this.bgColor = bgColor }
        fun roundCorner(roundCorner: Float) = apply { this.roundCorner = roundCorner }
        fun lifecycleOwner(lifecycleOwner: LifecycleOwner) =
            apply { this.lifecycleOwner = lifecycleOwner }

        fun canBeCancledOutside(canBeCancledOutside: Boolean) =
            apply { this.canBeCancelOutside = canBeCancledOutside }

        fun textLeftColor(@ColorInt textLeftColor: Int) =
            apply { this.textLeftColor = textLeftColor }

        fun textRightColor(@ColorInt textRightColor: Int) =
            apply { this.textRightColor = textRightColor }

        fun windowsAnimation(windowsAnimation: Int) =
            apply { this.windowsAnimation = windowsAnimation }

        fun gravity(gravity: Int) = apply { this.gravity = gravity }
        fun fullScreenWidth(fullScreenWidth: Boolean) =
            apply { this.fullScreenWidth = fullScreenWidth }

        fun bottomWithRoundCorner(bottomWithRoundCorner: Boolean) =
            apply { this.bottomWithRoundCorner = bottomWithRoundCorner }

        fun ratioScreenHeight(ratioScreenHeight: Float) =
            apply { this.ratioScreenHeight = ratioScreenHeight }

        open fun build() = CustomDialog(this, context).apply {
            create()
            if (context is ComponentActivity) {
                lifecycleOwner = context as ComponentActivity
            }
        }

        open fun show() {
            build().show()
        }


    }


}

interface CustomViewInflater {
    fun inflate(inflater: LayoutInflater, parent: ViewGroup): View
}