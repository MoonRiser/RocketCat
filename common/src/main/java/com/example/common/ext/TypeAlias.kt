package com.example.common.ext

import android.view.View
import androidx.databinding.ObservableField
import com.example.common.dialog.CustomDialog

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

typealias ObservableString = ObservableField<String>