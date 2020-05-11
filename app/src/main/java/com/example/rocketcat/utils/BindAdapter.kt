package com.example.rocketcat.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

@BindingAdapter(value = ["RoundedCorners"])
fun ImageView.roundedCorners(roundingRadius: Int) {
    Glide.with(this.context)
        .load(this.drawable)
        .apply(RequestOptions.bitmapTransform(RoundedCorners(dp2px(roundingRadius.toFloat()))))
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

@BindingAdapter(value = ["onClick"])
fun View.click(onclick: OnClickCallback) {
    this.setOnClickListener {
        RxView.clicks(this)
            .throttleFirst(FAST_CLICK_DELAY_TIME, TimeUnit.MILLISECONDS)
            .subscribe { onclick(this) }

    }

}
