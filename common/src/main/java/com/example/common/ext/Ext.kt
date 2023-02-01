package com.example.common.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Base64
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.fragment.app.FragmentActivity
import com.example.common.R
import com.example.common.utils.dp2px
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.coroutines.resume
import kotlin.math.hypot

/**
 * @Author:         Xres
 * @CreateDate:     2020/5/19 16:53
 * @Description:
 */

/**
 * 比如函数view.margin()需要的传入的参数是18dp对应的px值，那么直接view.margin(18f.dpValue),表明传入的18是dp为单位
 */
@Deprecated("推荐用顶级属性", ReplaceWith("dp2px(this)", "com.example.common.utils.dp2px"))
fun Float.dpValue(): Int = dp2px(this)

val Float.dp
    get() = dp2px(this)

inline val Int.dp
    get() = dp2px(this.toFloat())

val String.color
    @ColorInt
    get() = Color.parseColor(this)

/**
 * 用于富文本
 */
@Suppress("DEPRECATION")
val String.richText: Spanned
    get() = if (Build.VERSION.SDK_INT >= 24) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY) // for 24 api and more
    } else {
        Html.fromHtml(this) // or for older api
    }


inline fun <reified T : Activity> Activity.jumpTo(extras: Bundle? = null) {
    val intent = Intent(this, T::class.java).apply {
        if (extras != null) {
            putExtras(extras)
        }
    }
    this.startActivity(intent)
}

inline val View.primaryColor
    get() = MaterialColors.getColor(this, R.attr.colorPrimary)

/**
 * 尾递归函数，其实就是编译的时候优化了一下，用while循环取代递归，减小开销
 * 获取context的activity
 */
tailrec fun Context?.activity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}


/**
 * 将文件转化为Base64，同时压缩60%
 */
fun File.convertToBase64(): String {
    val bm = BitmapFactory.decodeFile(this.path)
    val baos = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, 60, baos) //bm is the bitmap object
    val b = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

/**
 * 想了想，上面的拷贝缓存文件的方法有点憨憨
 * 试试这个方法,
 * 算了，这个方法费内存，主要是文件需要完整读取成字节数组,非内存,搞不好OOM就要命了
 */
fun Uri.toRequestBody(context: Context, contentType: MediaType? = null): RequestBody? {
    var requestBody: RequestBody? = null
    try {
        val inputStream = context.contentResolver.openInputStream(this)
        val byteArray: ByteArray? = inputStream?.readBytes()
        requestBody = byteArray?.toRequestBody(contentType = contentType)
        inputStream?.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return requestBody

}

/**
 * @param sizeLimit 如果【getExternalCacheDir外部缓存文件夹】缓存超过sizeLimit MB就给爷爬
 */
suspend fun Context.clearCacheDir(sizeLimit: Int) {

    withContext(Dispatchers.IO) {
        if (externalCacheDir?.size ?: 1 > sizeLimit * 1024 * 1024 * 8) {
            externalCacheDir?.deleteRecursively()
        }
    }
}

/**
 *你这File (如果对应的是文件目录,则是递归遍历整个目录的大小)多少byte（位）
 */
val File.size: Long
    get() {
        if (!exists()) return 0
        if (isFile) return length()
        return walkBottomUp().fold(length(), { res, it -> it.length() + res })
    }

/**
 * 相同圆心,等比例缩放
 */
fun RectF.scale(factor: Float): RectF = RectF(
    centerX() * (1 - factor),
    centerY() * (1 - factor),
    centerX() * (1 + factor),
    centerY() * (1 + factor),
)

fun Canvas.drawRing(cx: Float, cy: Float, radius: Float, ringWidth: Float, paint: Paint) {

    if (ringWidth > radius) throw RuntimeException("环的宽度不能比环的半径大！")
    if (ringWidth < 1f) return
    val mRadius = radius - ringWidth / 2
    paint.apply {
        style = Paint.Style.STROKE
        strokeWidth = ringWidth
    }
    drawCircle(cx, cy, mRadius, paint)
}

infix fun PointF.distanceTo(destination: PointF): Float =
    hypot(x - destination.x, y - destination.y)

inline fun <T, R> Iterable<T>.zipWithNextInLoop(transform: (a: T, b: T) -> R): List<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()
    val result = mutableListOf<R>()
    var current = iterator.next()
    while (iterator.hasNext()) {
        val next = iterator.next()
        result.add(transform(current, next))
        current = next
    }
    if (result.isNotEmpty()) result.add(transform(last(), first()))
    return result
}

fun <T> Iterable<T>.zipWithNextInLoop() = zipWithNextInLoop { a, b -> a to b }


//作者：谷歌开发者
//链接：https://zhuanlan.zhihu.com/p/270002338
//来源：知乎
//著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

suspend fun Animator.awaitEnd() = suspendCancellableCoroutine<Unit> { cont ->

    // 增加一个处理协程取消的监听器，如果协程被取消，
    // 同时执行动画监听器的 onAnimationCancel() 方法，取消动画
    cont.invokeOnCancellation { cancel() }

    addListener(object : AnimatorListenerAdapter() {
        private var endedSuccessfully = true

        override fun onAnimationCancel(animation: Animator) {
            // 动画已经被取消，修改是否成功结束的标志
            endedSuccessfully = false
        }

        override fun onAnimationEnd(animation: Animator) {

            // 为了在协程恢复后的不发生泄漏，需要确保移除监听
            animation.removeListener(this)
            if (cont.isActive) {

                // 如果协程仍处于活跃状态
                if (endedSuccessfully) {
                    // 并且动画正常结束，恢复协程
                    cont.resume(Unit)
                } else {
                    // 否则动画被取消，同时取消协程
                    cont.cancel()
                }
            }
        }
    })
}


suspend fun ValueAnimator.await(progress: Float = 1.0f) =
    suspendCancellableCoroutine<Unit> { cont ->
        var firstTime = true
        // 增加一个处理协程取消的监听器，如果协程被取消，
        // 同时执行动画监听器的 onAnimationCancel() 方法，取消动画
        cont.invokeOnCancellation { cancel() }
        addUpdateListener {
            val value = it.animatedValue as Float
            if (firstTime && value > progress) {
                firstTime = false
                cont.resume(Unit)
            }

        }
        addListener(object : AnimatorListenerAdapter() {
            private var endedSuccessfully = true

            override fun onAnimationCancel(animation: Animator) {
                // 动画已经被取消，修改是否成功结束的标志
                endedSuccessfully = false
            }

            override fun onAnimationEnd(animation: Animator) {

                // 为了在协程恢复后的不发生泄漏，需要确保移除监听
                animation.removeListener(this)
                if (cont.isActive) {

                    // 如果协程仍处于活跃状态
                    if (!endedSuccessfully) {
                        // 否则动画被取消，同时取消协程
                        cont.cancel()
                    }
                }
            }
        })
    }


fun Activity.enableFullScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.apply {
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsets.Type.systemBars())
            //侵入刘海屏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
    } else {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}

inline fun <reified T> Any.isInstance(block: (T) -> Unit = {}): T? {
    return if (this is T) {
        block.invoke(this)
        this
    } else null
}

suspend inline fun retryBy(
    controller: SharedFlow<Any>,
    crossinline block: suspend CoroutineScope.() -> Unit
) {

    // Instance of the running repeating coroutine

    coroutineScope {

        var launchedJob: Job? = null

        try {
            if (controller.replayCache.isEmpty()) {
                launchedJob = this@coroutineScope.launch {
                    coroutineScope {
                        block()
                    }
                }
            }
            controller.collect {
                launchedJob?.cancel()
                launchedJob = this@coroutineScope.launch {
                    coroutineScope {
                        block()
                    }
                }

            }
        } finally {
            launchedJob?.cancel()
        }


    }

}