package com.example.common.ext

import android.view.View
import androidx.databinding.ObservableField
import com.example.ksp.annotation.IntSummable

/**
 * @Author:         Xres
 * @CreateDate:     2020/6/19 11:17
 * @Description:
 */

typealias ClickCallback = View.OnClickListener

typealias ObservableString = ObservableField<String>

@IntSummable
data class Foo(
    val bar: Int = 234,
    val baz: Int = 123
)

fun a() {
    val foo = Foo()
    foo.sumInts()
}