package com.example.rocketcat.ui.fragment.home_page

import android.content.Context
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.common.base.BaseFragment
import com.example.rocketcat.R
import com.example.rocketcat.adapter.MyGalleryAdapter
import com.example.rocketcat.databinding.FragmentTab3Binding
import com.example.rocketcat.ui.fragment.HomeViewModel
import com.xres.address_selector.dialog.CustomDialog
import com.xres.address_selector.ext.DialogCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * @author xres
 * @createDate 10/28/20
 */

class Tab3Fragment : BaseFragment<HomeViewModel, FragmentTab3Binding>() {

    private val imgs = arrayListOf(R.drawable.jump, R.drawable.paint, R.drawable.sit)

    override fun layoutId() = R.layout.fragment_tab3

    override fun initView(savedInstanceState: Bundle?) {
        binding.ivAbove.pageCount = imgs.size
        binding.vp2below.apply {
            adapter = MyGalleryAdapter(imgs)
            offscreenPageLimit = 2
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                   binding.ivAbove.setPageOffset(position,positionOffset)
                }
            })

        }
    }


    private suspend fun awaitDialog(context: Context) =
        suspendCancellableCoroutine<String> { cont ->
            CustomDialog.Builder(context)
                .title("这是标题")
                .content("这是内容")
                .textLeft("cancel")
                .textRight("OK")
                .leftOnClickListener(object : DialogCallback {
                    override fun onClick(dialog: CustomDialog) {
                        cont.resume("已取消")
                    }
                })
                .rightOnClickListener(object : DialogCallback {
                    override fun onClick(dialog: CustomDialog) {
                        cont.resume("已确定")
                        dialog.dismiss()
                    }
                }).show()
        }


}

