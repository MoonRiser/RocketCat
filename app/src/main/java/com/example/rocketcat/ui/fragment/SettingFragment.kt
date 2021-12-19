package com.example.rocketcat.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.common.base.BaseFragment
import com.example.common.dialog.customDialogOf
import com.example.common.ext.dataStore
import com.example.rocketcat.R
import com.example.rocketcat.adapter.BlurTransformation
import com.example.rocketcat.databinding.FragmentSettingBinding
import com.example.rocketcat.ui.activity.SplashActivity
import kotlinx.coroutines.launch


class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>() {


    override fun layoutId() = R.layout.fragment_setting

    override fun initView(view: View, savedInstanceState: Bundle?) {

        binding.resetGuideBtn.setOnClickListener {
            customDialogOf(requireContext()) {
                title("提示")
                message("app重新启动时，将再次展示引导页面")
                positiveButton("ok") { dialog ->
                    lifecycleScope.launch {
                        requireContext().dataStore.edit {
                            it[SplashActivity.keyHasSkip] = false
                        }
                    }
                    dialog.dismiss()
                }
                negativeButton("cancel")
            }.show()
        }
        Glide.with(requireActivity())
            .load(R.drawable.bvs)
            .transform(
                BlurTransformation(requireContext(), 15f)
            )
            .into(binding.bgIv)
    }


}