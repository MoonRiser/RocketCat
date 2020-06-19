package com.example.rocketcat.ext

import android.view.View
import com.example.rocketcat.dialog.CustomDialog

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