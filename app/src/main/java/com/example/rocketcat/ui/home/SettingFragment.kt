package com.example.rocketcat.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.common.base.BaseFragment
import com.example.common.dialog.customDialogOf
import com.example.common.ext.dataStore
import com.example.rocketcat.R
import com.example.rocketcat.adapter.BlurTransformation
import com.example.rocketcat.databinding.FragmentSettingBinding
import com.example.rocketcat.ui.mainpage.SplashActivity
import kotlinx.coroutines.launch


class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>() {

    companion object {
        private val nightModes = listOf(
            AppCompatDelegate.MODE_NIGHT_YES to "夜间模式",
            AppCompatDelegate.MODE_NIGHT_NO to "白天模式",
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM to "跟随系统"
        )
    }



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
        binding.dayNightShiftBtn.setOnClickListener {
            val (mode, content) = nightModes[viewModel.index]
            Log.i("xres", "mode:$mode content:$content vm:$viewModel index:${viewModel.index} fragment:$this ")
            binding.dayNightShiftBtn.text = content
            AppCompatDelegate.setDefaultNightMode(mode)
            viewModel.index = (++viewModel.index) % 3
        }
        Glide.with(requireActivity())
            .load(R.drawable.bvs)
            .transform(
                BlurTransformation(requireContext(), 15f)
            )
            .into(binding.bgIv)
    }


}