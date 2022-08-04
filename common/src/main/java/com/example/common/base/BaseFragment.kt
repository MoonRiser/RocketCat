package com.example.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.common.BR
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : BaseViewModel, DB : ViewDataBinding> : Fragment() {


    //是否第一次加载
    private var isFirst: Boolean = true

    //该类负责绑定视图数据的ViewModel
    lateinit var viewModel: VM

    //该类绑定的ViewDataBinding
    private var _binding: DB? = null
    val binding: DB get() = _binding!!

    /**
     * 当前Fragment绑定的视图布局
     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createBinding(inflater, container).let {
            _binding = it
            it.lifecycleOwner = this
            viewModel = createViewModel()
            it.setVariable(BR.viewModel, viewModel)
            it.root
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
        onVisible()
        initData()
    }

    /**
     * Fragment执行onCreate后触发的方法
     */
    open fun initData() {}

    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        val vmClazz = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
        return ViewModelProvider(getViewModelOwner())[vmClazz]
    }


    private fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): DB {
        val clazz = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<DB>
        val inflateMethod = clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        return inflateMethod.run {
            isAccessible = true
            invoke(null, inflater, parent, false) as DB
        }
    }

    /**
     * 初始化view
     */
    abstract fun initView(view: View, savedInstanceState: Bundle?)

    /**
     * 懒加载
     */
    open fun lazyLoadData() {}

    /**
     * 创建观察者
     */
    open fun initObserver() {}

    open fun getViewModelOwner(): ViewModelStoreOwner = this

    override fun onResume() {
        super.onResume()
        onVisible()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.unbind()
        //防止泄漏
        _binding = null
    }

    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            lazyLoadData()
            isFirst = false
            viewModel.loading.observe(viewLifecycleOwner) { show ->
                (requireActivity() as? BaseActivity<*, *>)?.let {
                    it.viewModel.loading.value = show == true
                }
            }
            initObserver()
        }
    }


}