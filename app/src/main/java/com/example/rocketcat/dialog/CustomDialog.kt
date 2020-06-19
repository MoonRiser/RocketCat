package com.szlanyou.iov.common.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
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
import com.szlanyou.iov.common.ext.DialogCallback
import com.szlanyou.iov.common.ext.dpValue
import com.szlanyou.iov.common.utils.AndroidUtil
import com.szlanyou.iov.common.utils.dp2px
import com.szlanyou.nissaniov.dialog.DialogLifecycleObserver

/**
 * @Author:         Xres
 * @CreateDate:     2020/5/18 10:44
 * @Description:
 */


open class CustomDialog(val builder: Builder, context: Context) : AppCompatDialog(context) {




    init {
        apply {
            setCanceledOnTouchOutside(builder.canBeCancledOutside)
            builder.windowsAnimation?.let { window?.setWindowAnimations(it) }
        }
        builder.lifecycleOwner?.lifecycle?.addObserver(DialogLifecycleObserver(::dismiss))

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
                        (AndroidUtil.getScreenSize().x * 0.8f).toInt()
                    }
                }
                height = builder.ratioScreenHeight.let {
                    if (it == 0f) {
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    } else {
                        (AndroidUtil.getScreenSize().y * it).toInt()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //根线性布局
        val linearLayout = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            gravity = Gravity.CENTER_HORIZONTAL
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(builder.bgColor)
                cornerRadius = builder.roundCorner.dpValue().toFloat()
            }
        }
        //标题布局、 包括标题和副标题
        val titleView: View = builder.title?.let {
            val title = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                text = builder.title
                gravity = Gravity.CENTER_HORIZONTAL
                setTextColor(builder.titleColor)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
            }
            val subTitle = builder.subTitle?.let {
                TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                        topMargin = dp2px(10f)
                    }
                    gravity = Gravity.CENTER_HORIZONTAL
                    text = it
                    setTextColor(builder.titleColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
                }
            }
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = dp2px(20f)
                }
            }.apply {
                addView(title)
                subTitle?.also { addView(it) }
            }

        } ?: TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                10f.dpValue())
        }
        //中间布局、文字内容或者自定义布局二选一
        val middleContent: View = onCreateCustomView(context) ?: builder.customView?.apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
        } ?: TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(30f.dpValue(), 10f.dpValue(), 30f.dpValue(), 20f.dpValue())
            }
            text = builder.content
            minHeight = 56f.dpValue()
            gravity = Gravity.CENTER_VERTICAL
            setLineSpacing(6f.dpValue().toFloat(), 1f)
            setTextColor(Color.parseColor("#353535"))
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)

        }
        linearLayout.apply {
            addView(titleView)
            addView(middleContent)
        }
        //横向的分割线
        val divider1 = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1f.dpValue())
            setBackgroundColor(Color.parseColor("#dedfe0"))
        }
        val cornerRadius = builder.roundCorner.dpValue().toFloat()
        //按钮布局
        val bottomLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 45f.dpValue())
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, cornerRadius, cornerRadius, cornerRadius, cornerRadius)
            }
        }
        val tvLeft = builder.textLeft?.let {
            TextView(context).apply {
                text = builder.textLeft
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                setTextColor(builder.textLeftColor)
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1F)
                setOnClickListener {
                    builder.leftOnClickListener?.onClick(this@CustomDialog) ?: dismiss()
                }
            }
        }
        val tvRight = builder.textRight?.let {

            TextView(getContext()).apply {
                text = it
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                setTextColor(builder.textRightColor)
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1F)
                setOnClickListener {
                    builder.rightOnClickListener?.onClick(this@CustomDialog)
                }

            }
        }
        val divider2 = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(1f.dpValue(), 24f.dpValue()).apply { gravity = Gravity.CENTER }
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

    open fun onCreateCustomView(context: Context): View? = null

    open class Builder(val context: Context) {

//        private var dialog: CustomDialog? = null

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
        var customView: View? = null
            private set

        var canBeCancledOutside: Boolean = true
            private set

        var windowsAnimation: Int? = null
            private set

        var gravity: Int = Gravity.CENTER
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
        var bgColor: Int = Color.WHITE
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
        fun leftOnClickListener(callback: DialogCallback) = apply { this.leftOnClickListener = callback }
        fun rightOnClickListener(callback: DialogCallback) = apply { this.rightOnClickListener = callback }
        fun customView(customView: View) = apply { this.customView = customView }
        fun bgColor(@ColorInt bgColor: Int) = apply { this.bgColor = bgColor }
        fun roundCorner(roundCorner: Float) = apply { this.roundCorner = roundCorner }
        fun lifecycleOwner(lifecycleOwner: LifecycleOwner) = apply { this.lifecycleOwner = lifecycleOwner }
        fun canBeCancledOutside(canBeCancledOutside: Boolean) = apply { this.canBeCancledOutside = canBeCancledOutside }
        fun textLeftColor(@ColorInt textLeftColor: Int) = apply { this.textLeftColor = textLeftColor }
        fun textRightColor(@ColorInt textRightColor: Int) = apply { this.textRightColor = textRightColor }
        fun windowsAnimation(windowsAnimation: Int) = apply { this.windowsAnimation = windowsAnimation }
        fun gravity(gravity: Int) = apply { this.gravity = gravity }
        fun fullScreenWidth(fullScreenWidth: Boolean) = apply { this.fullScreenWidth = fullScreenWidth }
        fun ratioScreenHeight(ratioScreenHeight: Float) = apply { this.ratioScreenHeight = ratioScreenHeight }

        open fun build() = CustomDialog(this, context)

        open fun show() {
            build().show()
        }


    }


}