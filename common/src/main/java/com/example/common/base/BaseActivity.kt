package com.example.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.common.BR
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM : ViewModel, DB : ViewDataBinding> : AppCompatActivity() {


    lateinit var viewModel: VM
    lateinit var binding: DB


    abstract fun layoutId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = createViewModel()
        binding = DataBindingUtil.setContentView(this, layoutId())
        binding.setVariable(getViewModelBR(), viewModel)
        initView(savedInstanceState)
    }


    open fun getViewModelBR(): Int = BR.viewModel


    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }


    @Suppress("UNCHECKED_CAST")
    private fun <VM> getVmClazz(obj: Any): Class<VM> {
        return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
    }


}