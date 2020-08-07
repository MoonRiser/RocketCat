package com.example.common.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.LifecycleOwner
import com.example.common.ext.dpValue
import com.example.common.utils.dp2px


/**
 * @Author:         Xres
 * @CreateDate:     2020/5/18 10:44
 * @Description:
 */
typealias Callback = () -> Unit

class CustomDialog(private val builder: Builder, context: Context) : AppCompatDialog(context) {


    private var root: LinearLayout

    init {


        builder.lifecycleOwner?.lifecycle?.addObserver(
            DialogLifecycleObserver(
                ::dismiss
            )
        )

        val linearLayout = LinearLayout(getContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                dp2px(
                    300F
                ), ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(builder.bgColor)
                cornerRadius = builder.roundCorner.dpValue().toFloat()
            }
        }

        builder.title?.let {
            TextView(getContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp2px(25f)
                }
                text = builder.title
                gravity = Gravity.CENTER_HORIZONTAL
                setTextColor(Color.BLACK)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17f)
            }
        }?.also { linearLayout.addView(it) }

        val middleContent: View = builder.customView?.apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        } ?: TextView(getContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(20f.dpValue(), 25f.dpValue(), 20f.dpValue(), 25f.dpValue())
                gravity = Gravity.CENTER
            }
            text = builder.content
            setLineSpacing(10f.dpValue().toFloat(), 1f)
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)

        }

        linearLayout.addView(middleContent)

        val divider1 = View(getContext()).apply {
            layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1f.dpValue())
            setBackgroundColor(Color.parseColor("#dedfe0"))
        }

        val cornerRadius = builder.roundCorner.dpValue().toFloat()
        val linearLayout2 = LinearLayout(getContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50f.dpValue())
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadii = floatArrayOf(
                    0f,
                    0f,
                    0f,
                    0f,
                    cornerRadius,
                    cornerRadius,
                    cornerRadius,
                    cornerRadius
                )
            }
        }

        val tvLeft = builder.textLeft?.let {
            TextView(getContext()).apply {
                text = builder.textLeft
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                setTextColor(builder.textLeftColor)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1F
                )
                setOnClickListener {
                    cancel()
                    builder.leftOnClickListener?.invoke()
                }
            }
        }

        val tvRight = builder.textRight?.let {

            TextView(getContext()).apply {
                text = it
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                setTextColor(builder.textRightColor)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1F
                )
                setOnClickListener {
                    builder.rightOnClickListener?.invoke()
                    dismiss()
                }

            }
        }

        val divider2 = View(getContext()).apply {
            layoutParams = LinearLayout.LayoutParams(1f.dpValue(), 24f.dpValue())
                .apply { gravity = Gravity.CENTER }
            setBackgroundColor(Color.parseColor("#dedfe0"))
        }


        val button = tvLeft?.also {
            linearLayout.addView(divider1)
            linearLayout2.addView(tvLeft)
            tvRight?.also {
                linearLayout2.addView(divider2)
                linearLayout2.addView(tvRight)
            }
        } ?: tvRight?.also {
            linearLayout.addView(divider1)
            linearLayout2.addView(tvRight)
        }

        root = linearLayout.apply {
            button?.let {
                addView(linearLayout2)
            }
        }
    }

    override fun onAttachedToWindow() {

        setCanceledOnTouchOutside(builder.canBeCancledOutside)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(root)
    }

    class Builder(val context: Context) {


        var title: String? = null
            private set
        var content: String = "assert content not be empty"
            private set
        var textLeft: String? = null
            private set
        var textRight: String? = null
            private set
        var leftOnClickListener: Callback? = null
            private set
        var rightOnClickListener: Callback? = null
            private set
        var customView: View? = null
            private set

        var canBeCancledOutside: Boolean = true
            private set


        @ColorInt
        var bgColor: Int = Color.WHITE
            private set

        @ColorInt
        var textLeftColor: Int = Color.parseColor("#FF333333")
            private set

        @ColorInt
        var textRightColor: Int = Color.parseColor("#FF00D863")
            private set

        var roundCorner: Float = 16f
            private set

        var lifecycleOwner: LifecycleOwner? = null
            private set

        fun title(title: String) = apply { this.title = title }
        fun content(content: String) = apply { this.content = content }
        fun textLeft(textLeft: String) = apply { this.textLeft = textLeft }
        fun textRight(textRight: String) = apply { this.textRight = textRight }
        fun leftOnClickListener(callback: Callback) = apply { this.leftOnClickListener = callback }
        fun rightOnClickListener(callback: Callback) =
            apply { this.rightOnClickListener = callback }

        fun customView(customView: View) = apply { this.customView = customView }
        fun bgColor(@ColorInt bgColor: Int) = apply { this.bgColor = bgColor }
        fun roundCorner(roundCorner: Float) = apply { this.roundCorner = roundCorner }
        fun lifecycleOwner(lifecycleOwner: LifecycleOwner) =
            apply { this.lifecycleOwner = lifecycleOwner }

        fun canBeCancledOutside(canBeCancledOutside: Boolean) =
            apply { this.canBeCancledOutside = canBeCancledOutside }

        fun textLeftColor(@ColorInt textLeftColor: Int) =
            apply { this.textLeftColor = textLeftColor }

        fun textRightColor(@ColorInt textRightColor: Int) =
            apply { this.textRightColor = textRightColor }

        fun build() = CustomDialog(this, context)

        fun show() {
            build().show()
        }


    }


}