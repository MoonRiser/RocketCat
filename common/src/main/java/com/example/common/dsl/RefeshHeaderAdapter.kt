package com.example.common.base

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.common.R
import com.example.common.databinding.ItemRvCountDownHeaderLayoutBinding
import com.example.common.ext.dp
import kotlinx.coroutines.*
import kotlin.coroutines.resume


class RefreshHeaderAdapter(
    private val headerCallback: RefreshHeaderCallback = CountDownRefreshHeader
) : RecyclerView.Adapter<RefreshHeaderAdapter.HeaderViewHold>() {

    companion object {
        val HEADER_HEIGHT = 64.dp
        val HEADER_MAX_MARGIN = 100.dp
        val DRAG_MAX_DISTANCE = 300.dp
        const val TAG_HEADER = 10086
    }

    private val animatorProvider
        get() = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = headerCallback.animDuration
            interpolator = AccelerateInterpolator()
        }

    private val mainScope = MainScope()
    private var callback: (() -> Unit)? = null
    var enable: Boolean = true
        set(value) = when (value) {
            field -> {}
            true -> {
                field = value
                notifyItemInserted(0)
            }
            false -> {
                field = value
                notifyItemRemoved(0)
            }
        }

    fun doOnRefresh(action: () -> Unit) {
        callback = action
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setOnTouchListener(object : View.OnTouchListener {

            var lastY = Float.NaN
            var animJob: Job? = null
            fun topMarginBy(oldMargin: Int, dy: Float): Int = when {
                oldMargin >= HEADER_MAX_MARGIN -> HEADER_MAX_MARGIN
                else -> {
                    val d = oldMargin + HEADER_HEIGHT + dy
                    val max = HEADER_MAX_MARGIN + HEADER_HEIGHT
                    val ratio = 1 - d / max.toFloat()
                    (oldMargin + dy * ratio).toInt()
                }
            }

            fun doOnHeaderShowing(block: RecyclerView.LayoutParams.(header: View) -> Unit) {
                recyclerView.findViewHolderForAdapterPosition(0)?.takeIf {
                    it is HeaderViewHold
                }?.let { header ->
                    header.itemView.updateLayoutParams<RecyclerView.LayoutParams> {
                        block(header.itemView)
                    }
                }
            }

            suspend fun View.updateHeaderMarginWithAnim(margin: Int) = suspendCancellableCoroutine<Unit> { cont ->
                val header = this
                val old = header.marginTop
                val animator = animatorProvider
                animator.addUpdateListener {
                    val target = ((margin - old) * (it.animatedValue as Float)).toInt() + old
                    header.updateLayoutParams<RecyclerView.LayoutParams> {
                        topMargin = target
                    }
                }
                cont.invokeOnCancellation { animator.cancel() }
                animator.doOnEnd { cont.resume(Unit) }
                animator.start()
            }

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (!enable) return false
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val dy = if (lastY.isNaN()) 0f else event.y - lastY
                        doOnHeaderShowing { header ->
                            topMargin = topMarginBy(header.top, dy)
                            if (header.top == 2) headerCallback.onTotalShow(header, dy)
                            animJob?.cancel()
                        }
                    }
                    MotionEvent.ACTION_UP -> doOnHeaderShowing { header ->
                        animJob = mainScope.launch {
                            if (topMargin < 0) header.updateHeaderMarginWithAnim(-HEADER_HEIGHT) else {
                                header.updateHeaderMarginWithAnim(0)
                                callback?.invoke()
                                headerCallback.doAnim(header)
                                header.updateHeaderMarginWithAnim(-HEADER_HEIGHT)
                            }
                        }
                    }
                }
                lastY = event.y
                return false
            }
        })

    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mainScope.cancel()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHold =
        HeaderViewHold(headerCallback.headerView(parent))

    override fun onBindViewHolder(holder: HeaderViewHold, position: Int) {

    }

    override fun getItemCount(): Int = if (enable) 1 else 0

    class HeaderViewHold(root: View) : RecyclerView.ViewHolder(root) {
        init {
            root.tag = TAG_HEADER
        }
    }
}

/**
 * RefreshHeader
 */
interface RefreshHeaderCallback {

    val animDuration: Long

    fun headerView(parent: ViewGroup): View

    fun onTotalShow(view: View, dy: Float)

    suspend fun doAnim(view: View) = delay(animDuration)

}

object CountDownRefreshHeader : RefreshHeaderCallback {


    override fun headerView(parent: ViewGroup): View =
        ItemRvCountDownHeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false).root

    override val animDuration: Long = 500

    override fun onTotalShow(view: View, dy: Float) {
        view.findViewById<TextView>(R.id.tv_tips).text = if (dy > 0) "释放立即刷新" else "下拉即可刷新"
    }

    override suspend fun doAnim(view: View) {
        (40 downTo 0).onEach {
            view.findViewById<TextView>(R.id.tv_anim).text = "count down : $it"
            delay(50)
        }

    }


}

class PagingHeaderAdapter : LoadStateAdapter<RefreshHeaderAdapter.HeaderViewHold>() {

    private val animatorProvider
        get() = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            interpolator = AccelerateInterpolator()
        }

    private val mainScope = MainScope()

    override fun onBindViewHolder(holder: RefreshHeaderAdapter.HeaderViewHold, loadState: LoadState) {
        when {
            loadState is LoadState.Loading -> holder.itemView
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): RefreshHeaderAdapter.HeaderViewHold {
        return RefreshHeaderAdapter.HeaderViewHold(
            ItemRvCountDownHeaderLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setOnTouchListener(object : View.OnTouchListener {

            var lastY = Float.NaN
            var animJob: Job? = null
            fun topMarginBy(oldMargin: Int, dy: Float): Int = when {
                oldMargin >= RefreshHeaderAdapter.HEADER_MAX_MARGIN -> RefreshHeaderAdapter.HEADER_MAX_MARGIN
                else -> {
                    val d = oldMargin + RefreshHeaderAdapter.HEADER_HEIGHT + dy
                    val max = RefreshHeaderAdapter.HEADER_MAX_MARGIN + RefreshHeaderAdapter.HEADER_HEIGHT
                    val ratio = 1 - d / max.toFloat()
                    (oldMargin + dy * ratio).toInt()
                }
            }

            fun doOnHeaderShowing(block: RecyclerView.LayoutParams.(header: View) -> Unit) {
                recyclerView.findViewHolderForAdapterPosition(0)?.takeIf {
                    it is RefreshHeaderAdapter.HeaderViewHold
                }?.let { header ->
                    header.itemView.updateLayoutParams<RecyclerView.LayoutParams> {
                        block(header.itemView)
                    }
                }
            }

            suspend fun View.updateHeaderMarginWithAnim(margin: Int) = suspendCancellableCoroutine<Unit> { cont ->
                val header = this
                val old = header.marginTop
                val animator = animatorProvider
                animator.addUpdateListener {
                    val target = ((margin - old) * (it.animatedValue as Float)).toInt() + old
                    header.updateLayoutParams<RecyclerView.LayoutParams> {
                        topMargin = target
                    }
                }
                cont.invokeOnCancellation { animator.cancel() }
                animator.doOnEnd { cont.resume(Unit) }
                animator.start()
            }

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val dy = if (lastY.isNaN()) 0f else event.y - lastY
                        doOnHeaderShowing { header ->
                            topMargin = topMarginBy(header.top, dy)
                            animJob?.cancel()
                        }
                    }
                    MotionEvent.ACTION_UP -> doOnHeaderShowing { header ->
                        animJob = mainScope.launch {
                            if (topMargin < 0) header.updateHeaderMarginWithAnim(-RefreshHeaderAdapter.HEADER_HEIGHT) else {
                                header.updateHeaderMarginWithAnim(0)
                                header.updateHeaderMarginWithAnim(-RefreshHeaderAdapter.HEADER_HEIGHT)
                            }
                        }
                    }
                }
                lastY = event.y
                return false
            }
        })

    }


}