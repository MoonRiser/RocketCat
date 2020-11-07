package com.xres.address_selector.ext

import android.view.View
import com.xres.address_selector.dialog.CustomDialog

/**
 * @Author:         Xres
 * @CreateDate:     2020/6/19 11:17
 * @Description:
 */

typealias ClickCallback = View.OnClickListener

typealias DialogCallback = OnDialogClickListener

interface OnDialogClickListener {
    fun onClick(dialog: CustomDialog)
}
