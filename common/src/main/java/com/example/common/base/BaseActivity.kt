package com.example.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.example.common.BR
import com.example.common.R
import com.example.common.widget.BallsLoading
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding> : AppCompatActivity() {


    lateinit var viewModel: VM
    lateinit var binding: DB

    private lateinit var loadingPage: ViewGroup


    abstract fun layoutId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = createViewModel()
        binding = DataBindingUtil.setContentView(this, layoutId())
        binding.setVariable(BR.viewModel, viewModel)
        initView(savedInstanceState)
        initObserver()
        //初始化loading view
        loadingPage =
            LayoutInflater.from(this).inflate(R.layout.common_loading_view, null) as ViewGroup
        viewModel.loading.observe(this) {
            showLoading(it)
        }

    }


    open fun initObserver() {

    }

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

    private fun showLoading(show: Boolean) {
        val decView = window.decorView as FrameLayout
        if (show) {
            decView.addView(loadingPage)
            val loading = loadingPage.findViewById<BallsLoading>(R.id.ballLoading)
            loading.post {
                loading.start()
            }

        } else {
            decView.removeView(loadingPage)
        }
    }


}