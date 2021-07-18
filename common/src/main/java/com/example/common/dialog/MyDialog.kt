package com.example.common.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * @author WayneXie
 * @date 2021/7/14
 */

typealias DialogClick = (DialogInterface) -> Unit

@MainThread
fun dialog(
    context: Context,
    modifier: MyDialog.Modifier? = null,
    builderAction: MyDialog.Builder.() -> Unit
): Dialog {
    val builder = MyDialog.Builder(context)
    builderAction.invoke(builder)
    return builder.build(modifier)

}

@MainThread
fun dialog(
    fragment: Fragment,
    modifier: MyDialog.Modifier? = null,
    builderAction: MyDialog.Builder.() -> Unit
): Dialog {
    if (!fragment.isAdded && fragment.isDetached) throw RuntimeException("fragment must has already attached to activity before build dialog")
    return dialog(fragment.requireActivity(), modifier, builderAction)
}


class MyDialog private constructor(private val context: Context) {

    val dialogBuilder
        get() = AlertDialog.Builder(context)


    class Builder(private val context: Context) {

        private var _title: CharSequence? = null

        private var _message: CharSequence? = null

        private var _messageLivedata: LiveData<out CharSequence>? = null

        private var _negativeButtonText: CharSequence? = null

        private var _negativeAction: DialogClick? = null

        private var _positiveButtonText: CharSequence? = null

        private var _positiveAction: DialogClick? = null


        fun title(title: CharSequence) {
            _title = title
        }


        fun message(message: CharSequence) {
            _message = message
        }


        fun <T : CharSequence> message(message: LiveData<T>) {
            _messageLivedata = message
        }


        fun negativeButton(buttonText: CharSequence, clickAction: DialogClick = { it.dismiss() }) {
            _negativeButtonText = buttonText
            _negativeAction = clickAction
        }


        fun positiveButton(buttonText: CharSequence, clickAction: DialogClick? = null) {
            _positiveButtonText = buttonText
            _positiveAction = clickAction
        }


        fun build(modifier: Modifier? = null): Dialog {
            val dialogBuilder = MyDialog(context).dialogBuilder
            with(dialogBuilder) {
                _title?.let { setTitle(it) }
                _message?.let { setMessage(it) }
                _messageLivedata?.let {
                    setMessage(it.value ?: "")
                }
                _negativeButtonText?.let {
                    setNegativeButton(it) { dialog, _ ->
                        _negativeAction?.invoke(dialog)
                    }
                }
                _positiveButtonText?.let {
                    setPositiveButton(it) { dialog, _ ->
                        _positiveAction?.invoke(dialog)
                    }
                }
            }
            return dialogBuilder.create().also { dialog ->
                modifier?.let {
                    dialog.setCanceledOnTouchOutside(it.canceledOnTouchOutside)
                    dialog.window?.setGravity(it.gravity)
                }
                var messageObserver: Observer<CharSequence>? = null
                dialog.setOnShowListener {
                    messageObserver = Observer<CharSequence> {
                        val msgView = dialog.findViewById<TextView>(android.R.id.message)
                        msgView?.text = it
                    }
                    _messageLivedata?.observeForever(messageObserver!!)
                }
                dialog.setOnDismissListener {
                    _messageLivedata?.removeObserver(messageObserver!!)
                }
            }
        }
    }

    data class Modifier(
        val canceledOnTouchOutside: Boolean = true,
        val gravity: Int = Gravity.CENTER
    )

}