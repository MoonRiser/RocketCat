package com.example.rocketcat.ui.fragment.home_page

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.common.base.BaseFragment
import com.example.common.base.BaseViewModel
import com.example.common.ext.dp
import com.example.rocketcat.R
import com.example.rocketcat.adapter.BlurTransformation
import com.example.rocketcat.customview.MyFlowLayout
import com.example.rocketcat.databinding.FragmentTab2Binding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip


class Tab2Fragment : BaseFragment<BaseViewModel, FragmentTab2Binding>() {

    private lateinit var dialog: BottomSheetDialog

    override fun layoutId() = R.layout.fragment_tab2

    override fun initView(view: View, savedInstanceState: Bundle?) {


        val lastView = TextView(requireContext()).apply {
            text = "lastOne"
            layoutParams = MyFlowLayout.MyLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setPadding(6f.dp)
            }
        }
        binding.flowLayout.apply {
            setLastView(lastView)
//            enableLastView(false)
        }
        repeat(25) { time ->
            val child = Chip(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = 4f.dp
                }
                text = arrayOf("加油", "优惠券", "车灯", "保温杯", "德玛西亚")[(0..4).random()] + "$time"
                tag = time
                setTextColor(Color.BLUE)
                setOnClickListener {
                    dialog.show()
                }
                setOnLongClickListener {
                    isCloseIconVisible = !isCloseIconVisible
                    true
                }
                setOnCloseIconClickListener {
                    (it.parent as ViewGroup).removeView(it)
                }
                setCloseIconResource(R.drawable.ic_baseline_cancel_24)
                closeIconTint = ColorStateList.valueOf(Color.RED)
            }
            binding.flowLayout.addView(child)
        }


        dialog = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.bottom_sheet_content)
        }
        dialog.setOnShowListener { dialog1 ->

            val bottomSheetDialog = dialog1 as BottomSheetDialog
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.let {
                    val behaviour = BottomSheetBehavior.from(it)
                    behaviour.peekHeight = 48f.dp
                    behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                    setupFullHeight(it)
                }
        }

        Glide.with(requireActivity())
            .load(R.drawable.cp)
            .transform(
                CenterCrop(),
                BlurTransformation(requireContext(), 15f, 0.2f to 0.8f, 0.3f to 0.8f)
            )
            .into(binding.imgGS)


    }


    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

}

