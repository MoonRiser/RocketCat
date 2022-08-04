package com.example.rocketcat.ui.fragment.home_page

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common.base.BaseFragment
import com.example.common.base.DataItem
import com.example.common.base.listAdapterOf
import com.example.common.base.withViewHolder
import com.example.common.ext.awaitEnd
import com.example.common.ext.dp
import com.example.rocketcat.R
import com.example.rocketcat.databinding.FragmentTab6LayoutBinding
import com.example.rocketcat.databinding.ItemRvHeaderLayoutBinding
import com.example.rocketcat.databinding.ItemRvTestLayoutBinding
import com.example.rocketcat.ui.fragment.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.PI
import kotlin.math.sin

class Tab6Fragment : BaseFragment<HomeViewModel, FragmentTab6LayoutBinding>() {

    companion object {
        val HEADER_HEIGHT = 64.dp
        val HEADER_MAX_MARGIN = 100.dp
        val DRAG_MAX_DISTANCE = 300.dp
    }

    private val listAdapter = listAdapterOf {
        withViewHolder<ItemRvHeaderLayoutBinding, DataItem.Header>()
        withViewHolder<ItemRvTestLayoutBinding, BookInfo>()
    }

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 300
        interpolator = AccelerateInterpolator()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(view: View, savedInstanceState: Bundle?) {

        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = listAdapter
        }
        lifecycleScope.launch {
            while (false) {
                delay(2500)
                val newList = listAdapter.currentList + BookInfo.newInstance()
                listAdapter.submitList(newList) {
                    binding.rv.scrollToPosition(listAdapter.itemCount - 1)
                }
            }
        }
        listAdapter.submitList(DataItem.Header + List(20) { BookInfo.newInstance() })
        binding.rv.setOnTouchListener(object : View.OnTouchListener {
            var startY = 0f
            fun topMarginBy(scrollY: Float): Int = when {
                scrollY < 0 -> -HEADER_HEIGHT
                scrollY > DRAG_MAX_DISTANCE -> HEADER_MAX_MARGIN
                else -> ((HEADER_HEIGHT + HEADER_MAX_MARGIN) * sin(scrollY * PI / DRAG_MAX_DISTANCE / 2)).toInt() - HEADER_HEIGHT
            }

            fun updateHeaderMargin(block: RecyclerView.LayoutParams.(header: View) -> Unit) {
                val lm = binding.rv.layoutManager as LinearLayoutManager
                val header = lm.findViewByPosition(0) as? FrameLayout ?: return
                header.updateLayoutParams<RecyclerView.LayoutParams> {
                    block(header)
                }
            }

            fun updateHeaderMarginWithAnim(margin: Int): View? {
                val lm = binding.rv.layoutManager as LinearLayoutManager
                val header = lm.findViewByPosition(0) as? FrameLayout ?: return null
                val old = header.marginTop
                animator.addUpdateListener {
                    val target = ((margin - old) * (it.animatedValue as Float)).toInt() + old
                    header.updateLayoutParams<RecyclerView.LayoutParams> {
                        topMargin = target
                    }
                }
                animator.start()
                return header
            }


            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startY = event.y
                    }
                    MotionEvent.ACTION_MOVE -> updateHeaderMargin {
                        topMargin = topMarginBy(event.y - startY)
                    }
                    MotionEvent.ACTION_UP -> {
                        val header = updateHeaderMarginWithAnim(0)
                        lifecycleScope.launch {
                            animator.awaitEnd()
                            (50 downTo 0).onEach {
                                delay(50)
                                header?.findViewById<TextView>(R.id.tv_anim)?.text = "倒计时：$it"
                            }
                            updateHeaderMarginWithAnim(-HEADER_HEIGHT)
                        }
                    }
                }

                return false
            }
        })
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            }
        })

    }


}

data class BookInfo(val content: String, @ColorInt val color: Int) : DataItem(1) {
    companion object {
        fun newInstance() = BookInfo(
            content = "this moment in time is ${now()}",
            color = listOf(Color.BLACK, Color.BLUE, Color.YELLOW, Color.CYAN, Color.RED, Color.GREEN, Color.MAGENTA).random()
        )

        private fun now(): String {
            val sdf = SimpleDateFormat("hh:mm:ss")
            return sdf.format(Date())
        }
    }
}