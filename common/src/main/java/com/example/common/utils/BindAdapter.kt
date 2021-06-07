package com.example.common.utils

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.common.ext.ClickCallback
import com.jakewharton.rxbinding2.view.RxView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import java.util.concurrent.TimeUnit

@BindingAdapter(value = ["CornerRadius"])
fun ImageView.roundedCorners(roundingRadius: Int) {
    Glide.with(this.context)
        .load(this.drawable)
        .apply(
            RequestOptions.bitmapTransform(
                RoundedCorners(
                    dp2px(
                        roundingRadius.toFloat()
                    )
                )
            )
        )
        .into(this)
}


@BindingAdapter(value = ["circleAvatar"])
fun circleImageUrl(view: ImageView, circle: Boolean) {
    if (circle) {
        Glide.with(view.context.applicationContext)
            .load(view.drawable)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(view)
    }

}

typealias OnClickCallback = (view: View) -> Unit

const val FAST_CLICK_DELAY_TIME = 1000L

@BindingAdapter(value = ["android:onClick", "allowFastClick"], requireAll = false)
fun View.click(listener: ClickCallback?, allowFastClick: Boolean?) {

    if (allowFastClick == true) {
        listener?.let {
            setOnClickListener(it)
        }
    } else RxView.clicks(this)
        .throttleFirst(FAST_CLICK_DELAY_TIME, TimeUnit.MILLISECONDS)
        .subscribe {
            listener?.onClick(this)
        }
}

@BindingAdapter(value = ["passwordVisible"])
fun EditText.setPasswordVisibility(visible: Boolean) {
    transformationMethod =
        if (visible) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
}


@BindingAdapter(value = ["dataList", "toEnd"])
fun <T> RecyclerView.submit(dataList: List<T>?, toEnd: Boolean?) {
    (adapter as? ListAdapter<*, *>)?.let {
        val needCopy = it.currentList === dataList
        val list = if (needCopy) ArrayList<T>(dataList) else dataList
        it.submitList(list as? List<Nothing>) {
            //滑动到底部，其实可以加个属性控制滑到顶部还是底部
            it.itemCount.takeIf { count ->
                count > 0
            }?.let { count ->
                val position = if (toEnd != false) count - 1 else 0
                smoothScrollToPosition(position)
            }

        }
    }
}


/***********************双向绑定*********************************/
@InverseBindingAdapter(attribute = "currentItem", event = "currentItemAttrChanged")
fun ViewPager2.getMyCurrentItem(): Int = currentItem

@BindingAdapter(value = ["currentItem", "smoothScroll"], requireAll = false)
fun ViewPager2.setMyCurrentItem(position: Int?, smoothScroll: Boolean?) {
    if (currentItem != position) {
        setCurrentItem(position ?: 0, smoothScroll == true)
    }
}

@BindingAdapter(value = ["currentItemAttrChanged"])
fun ViewPager2.setListeners(
    attrChange: InverseBindingListener
) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            attrChange.onChange()
        }
    })

}

/**
 * 刷新布局的状态 true：启动刷新 false：完成刷新
 */
@BindingAdapter(value = ["refresh"])
fun SmartRefreshLayout.performRefresh(refresh: Boolean?) {
    refresh?.let {
        if (it && !isRefreshing) autoRefresh()
        if (!it) finishRefresh()
    }

}


@BindingAdapter(value = ["onRefresh"])
fun SmartRefreshLayout._setRefreshListener(refresh: OnRefreshListener) {
    setOnRefreshListener(refresh)
}

@BindingAdapter(value = ["onLoadMore"])
fun SmartRefreshLayout._setLoadMoreListener(listener: OnLoadMoreListener) {
    setOnLoadMoreListener(listener)
}

@BindingAdapter(value = ["noMoreData"])
fun SmartRefreshLayout.performLoadMore(noMoreData: Boolean?) {
    if (noMoreData == true) finishLoadMoreWithNoMoreData() else finishLoadMore()

}