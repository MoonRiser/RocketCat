package com.example.rocketcat.ui.fragment.tab

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.lifecycleScope
import com.example.common.base.BaseFragment
import com.example.rocketcat.R
import com.example.rocketcat.databinding.FragmentTab3Binding
import com.example.rocketcat.ui.fragment.HomeViewModel
import com.xres.address_selector.dialog.CustomDialog
import com.xres.address_selector.ext.DialogCallback
import com.xres.address_selector.ext.showToast
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author xres
 * @createDate 10/28/20
 */

class Tab3Fragment : BaseFragment<HomeViewModel, FragmentTab3Binding>() {

    override fun layoutId() = R.layout.fragment_tab3

    override fun initView(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            async {  }
            val msg = awaitDialog(requireActivity())
            showToast(msg)
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

