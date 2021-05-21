package com.example.rocketcat.ui.fragment.home_page

import androidx.databinding.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.common.base.BaseViewModel
import com.example.common.ext.ClickCallback
import com.example.common.ext.ObservableString
import com.example.common.ext.showToast
import com.example.rocketcat.BR
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.internal.notifyAll

class MyBindingViewModel : BaseViewModel() {

    val name = ObservableString("姓名")

    val user = User()
    var person = ObservableField(Person())

    //输入框相关
    val account = ObservableString()
    val pwd = ObservableString()
    val pwdVisible = ObservableBoolean(false)
    val enableNext = ObservableBoolean(false)

    //绑定函数
    val age = MutableLiveData(0)
    fun handleAge(age: Int?): String = age?.let { "年龄：$it" } ?: "--"

    val clear = ObservableBoolean(false)

    //点击事件绑定
    val onTestClick = ClickCallback {
        showToast("I am OK")
    }

    init {

        val observer = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val enable = account.get()?.length == 11 && pwd.get()?.isNotEmpty() == true
                enableNext.set(enable)
            }
        }
        account.addOnPropertyChangedCallback(observer)
        pwd.addOnPropertyChangedCallback(observer)

        //test
        viewModelScope.launch {
            delay(3000)
            listOf(18, 19, 20, null).asFlow()
                .map {
                    delay(800)
                    it
                }
                .collect {
                    age.value = it
                }
            clear.set(true)

        }
    }
}


data class Person(var name: String = "pp", var age: String = "18")

class User : BaseObservable() {

    @get:Bindable
    var firstName: String = "xiao"
        set(value) {
            field = value
            notifyChange()
        }

    @get:Bindable
    var lastName: String = "Ming"
        set(value) {
            field = value
            notifyPropertyChanged(BR.lastName)
        }
}
