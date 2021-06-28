package com.example.common.ext

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.common.widget.ViewPager2Container
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

fun ViewPager2.init(
    fragment: Fragment,
    fragments: List<Fragment>,
    isUserInputEnabled: Boolean = true
): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    offscreenPageLimit = fragments.size
    //设置适配器
    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
        override fun getItemId(position: Int) = fragments[position].hashCode().toLong()
        override fun containsItem(itemId: Long) =
            fragments.map { it.hashCode().toLong() }.contains(itemId)
    }
    return this
}


fun ViewPager2.init(
    activity: FragmentActivity,
    fragments: List<Fragment>,
    isUserInputEnabled: Boolean = true,
    offscreenPageLimit: Int = 1
): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    this.offscreenPageLimit = offscreenPageLimit
    //设置适配器
    adapter = object : FragmentStateAdapter(activity) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
        override fun getItemId(position: Int) = fragments[position].hashCode().toLong()
        override fun containsItem(itemId: Long) =
            fragments.map { it.hashCode().toLong() }.contains(itemId)
    }
    return this
}

/**
 * 处理viewpager2的滑动冲突
 */
fun ViewPager2.enableNestedScroll() {
    val vp2 = this
    val p = parent as ViewGroup
    val index = p.indexOfChild(this)
    p.removeViewAt(index)
    val container = ViewPager2Container(context).apply {
        layoutParams = vp2.layoutParams
        addView(vp2.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        })
    }
    p.addView(container, index)

}

fun AppCompatActivity.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(msg: String) {
    Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
}

@ColorInt
fun View.getColorById(@ColorRes colorId: Int) = ContextCompat.getColor(this.context, colorId)

@ColorInt
fun FragmentActivity.getColorById(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

@ColorInt
fun Fragment.getColorById(@ColorRes colorId: Int) = requireActivity().getColorById(colorId)

fun AppCompatActivity.getDrawableById(@DrawableRes resourceID: Int): Drawable? =
    ResourcesCompat.getDrawable(resources, resourceID, theme)

fun Fragment.getDrawableById(@DrawableRes resourceID: Int): Drawable? =
    (requireActivity() as? AppCompatActivity)?.getDrawableById(resourceID)

fun View.getDrawableById(@DrawableRes resourceID: Int): Drawable? =
    ContextCompat.getDrawable(context, resourceID)

/**
 * @param rawString 初始字符串，中间的协议名称必须带书名号《》
 * @param color 书名号中的内容包括书名号的颜色
 * @param listener 点击监听器，被点击的第index个协议
 */
@JvmOverloads
fun TextView.setClickableSpan(
    rawString: String,
    @ColorInt color: Int = primaryColor,
    showSymbol: Boolean = true,
    listener: SpanClickListener? = null
) {
    this.apply {
        text = string2SpannableStringBuilder(rawString, color, showSymbol, listener)
        highlightColor = ContextCompat.getColor(this.context, android.R.color.transparent)
        movementMethod = LinkMovementMethod.getInstance()
    }
}

fun View.createSpringAnimation(
    property: DynamicAnimation.ViewProperty,
    finalPosition: Float = 0f,
    stiffness: Float = SpringForce.STIFFNESS_LOW,
    dampingRatio: Float = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
): SpringAnimation {
    return SpringForce(finalPosition).also {
        it.stiffness = stiffness
        it.dampingRatio = dampingRatio
    }.let { force ->
        SpringAnimation(this, property).also {
            it.spring = force
        }
    }
}


/**
 * 切换软键盘的弹出和隐藏的状态
 *
 * @param view 存在于当前布局 ViewTree 中，随意一个 View
 */
fun EditText.toggleSoftInput() {
    val imm: InputMethodManager? =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm?.toggleSoftInput(0, 0)
}

/**
 * 利用协程进行滚动布局的非阻塞式挂起，如果你不理解，请先去学习kotlin coroutine
 * 参考Google官方文章 https://zhuanlan.zhihu.com/p/270002338
 * @param direction 滚动方向 ScrollView.FOCUS_DOWN，ScrollView.FOCUS_UP
 */

suspend fun NestedScrollView.awaitScroll(direction: Int) =
    suspendCancellableCoroutine<Unit> { cont ->
        //先滚动
        fullScroll(direction)
        //判断滚动布局是否滚到头,因为如果布局本身位置就到头的话,不会触发OnScrollChangeListener
        if (!(canScrollVertically(1) && canScrollVertically(-1))) {
            cont.resume(Unit)
            return@suspendCancellableCoroutine
        }
        // 这里的 lambda 表达式会被立即调用，允许我们创建一个监听器
        val listener = NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
            if (!(canScrollVertically(1) && canScrollVertically(-1))) {
                //区分调用自身还是父类的重载方法
                v.setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?)
                cont.resume(Unit)
            }
        }

        // 如果协程被取消，移除该监听
        cont.invokeOnCancellation {
            setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?)
        }
        // 最终，将监听添加到 view 上
        setOnScrollChangeListener(listener)

        // 这样协程就被挂起了，除非监听器中的 cont.resume() 方法被调用

    }
