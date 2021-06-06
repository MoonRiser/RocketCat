package com.xres.address_selector.widget.address_selector

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.xres.address_selector.db.entity.Area
import com.xres.address_selector.db.entity.City
import com.xres.address_selector.db.entity.Province
import com.xres.address_selector.db.entity.Street
import com.xres.address_selector.dialog.CustomDialog
import com.xres.address_selector.dialog.CustomViewInflater

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/24 9:25
 * @Description:
 */
class AddressSelector(context: FragmentActivity) {

    private val dialogBuilder = CustomDialog.Builder(context)
    private lateinit var dialog: CustomDialog
    private val customView = AddressSelectorView(context)

    init {
        dialogBuilder.title("请选择地址")
            .customView { _, _ -> customView }
            .fullScreenWidth(true)
            .ratioScreenHeight(0.7f)
            .bottomWithRoundCorner(false)
            .gravity(Gravity.BOTTOM)
    }

    fun setOnSelectCompletedListener(listener: OnSelectedListener) =
        customView.setOnSelectCompletedListener { province, city, area, street ->
            listener.onSelect(this, province, city, area, street)
        }

    fun show() {
        if (::dialog.isInitialized) {
            dialog.show()
        } else {
            dialogBuilder.build().also {
                dialog = it
            }.show()
        }
    }

    fun dismiss() {
        dialog.dismiss()
    }
}

interface OnSelectedListener {
    fun onSelect(
        selector: AddressSelector,
        province: Province,
        city: City,
        area: Area,
        street: Street
    )
}