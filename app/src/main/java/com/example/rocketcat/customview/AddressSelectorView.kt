package com.example.rocketcat.customview

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.viewpager2.widget.ViewPager2
import com.example.common.dialog.CustomDialog
import com.example.common.ext.ClickCallback
import com.example.common.ext.init
import com.example.rocketcat.R
import com.example.rocketcat.data.db.AppDatabase
import com.example.rocketcat.data.db.entity.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/20 10:36
 * @Description:
 */
class AddressSelectorView(private val context: FragmentActivity) {

    private var listener: OnSelectListener? = null
    private val dialogBuilder = CustomDialog.Builder(context)
    private lateinit var dialog: CustomDialog
    private var customView: View =
        LayoutInflater.from(context).inflate(R.layout.address_select_view, null)
    private var viewPager2: ViewPager2
    private var tabLayout: TabLayout
    private var mediator: TabLayoutMediator
    private val fragments = arrayListOf<AddressFragment>()

    //分别是省级、市级、县级、乡级、街道
    private val adapterProvince = AddressAdapter()
    private val adapterCity = AddressAdapter()
    private val adapterArea = AddressAdapter()
    private val adapterStreet = AddressAdapter()

    //缓存fragment实例，避免重复加载
    private val fragmentProvince = AddressFragment(adapterProvince)
    private val fragmentCity = AddressFragment(adapterCity)
    private val fragmentArea = AddressFragment(adapterArea)
    private val fragmentStreet = AddressFragment(adapterStreet)


    //当前选中的省、市、县、乡
    private var currentProvince: Division? = null
    private var currentCity: Division? = null
    private var currentArea: Division? = null
    private var currentStreet: Division? = null


    //刷新省级数据
    private fun refreshProvince() {
        _provinceRq.value = _provinceRq.value
    }

    private val _provinceRq = MutableLiveData<String>()
    private var provinceLiveData: LiveData<List<Province>> =
        Transformations.switchMap(_provinceRq) {
            AppDatabase.getInstance(context.applicationContext).divisionDao().getProvinceList()
        }


    //刷新市级数据
    private fun refreshCity(provinceCode: String) {
        _provinceCode.value = provinceCode
    }

    private val _provinceCode = MutableLiveData<String>()
    private var cityLiveData: LiveData<List<City>> = Transformations.switchMap(_provinceCode) {
        AppDatabase.getInstance(context.applicationContext).divisionDao().getCityList(it)
    }


    //刷新区级数据
    private fun refreshArea(cityCode: String) {
        _cityCode.value = cityCode
    }

    private val _cityCode = MutableLiveData<String>()
    private var areaLiveData: LiveData<List<Area>> = Transformations.switchMap(_cityCode) {
        AppDatabase.getInstance(context.applicationContext).divisionDao().getAreaList(it)
    }


    //刷新街道级数据
    private fun refreshStreet(areaCode: String) {
        _areaCode.value = areaCode
    }

    private val _areaCode = MutableLiveData<String>()
    private var streetLiveData: LiveData<List<Street>> = Transformations.switchMap(_areaCode) {
        AppDatabase.getInstance(context.applicationContext).divisionDao().getStreetList(it)
    }

    init {

        //初始化地址数据信息
        initAddressData()
        //初始时，将省的数据先加载
        initProvince()
        initCity()
        initArea()
        initStreet()
        refreshProvince()
        //初始化viewpager
        viewPager2 = customView.findViewById(R.id.vp2_as)
        viewPager2.init(context, fragments, false)
        tabLayout = customView.findViewById(R.id.tab_as)
        mediator = TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = currentProvince?.name() ?: "请选择"
                1 -> tab.text = currentCity?.name() ?: "请选择"
                2 -> tab.text = currentArea?.name() ?: "请选择"
                3 -> tab.text = currentStreet?.name() ?: "请选择"
            }
        }
        mediator.attach()

        dialogBuilder.title("请选择地址")
            .customView(customView)
            .fullScreenWidth(true)
            .ratioScreenHeight(0.7f)
            .gravity(Gravity.BOTTOM)
    }

    private fun initProvince() {
        fragments.add(fragmentProvince)
        adapterProvince.clickListener = ClickCallback {
            val position = it.tag as Int
            mediator.detach()
            currentProvince = adapterProvince.getDataList()[position]
            currentProvince?.code()?.let { it1 -> refreshCity(it1) }
            fragments.removeFromIndex(1)
            fragments.add(1, fragmentCity)
            currentCity = null
            viewPager2.adapter?.notifyDataSetChanged()
            mediator.attach()
            viewPager2.currentItem = 1
        }

    }

    private fun initCity() {
        adapterCity.clickListener = ClickCallback {
            mediator.detach()
            currentCity = adapterCity.getDataList()[it.tag as Int]
            currentCity?.code()?.let { it1 -> refreshArea(it1) }
            fragments.removeFromIndex(2)
            fragments.add(2, fragmentArea)
            currentArea = null
            viewPager2.adapter?.notifyDataSetChanged()
            mediator.attach()
            viewPager2.currentItem = 2
        }

    }

    private fun initArea() {
        adapterArea.clickListener = ClickCallback {
            mediator.detach()
            currentArea = adapterArea.getDataList()[it.tag as Int]
            currentArea?.code()?.let { it1 -> refreshStreet(it1) }
            fragments.removeFromIndex(3)
            fragments.add(3, fragmentStreet)
            currentStreet = null
            viewPager2.adapter?.notifyDataSetChanged()
            mediator.attach()
            viewPager2.currentItem = 3
        }

    }

    private fun initStreet() {
        adapterStreet.clickListener = ClickCallback {
            mediator.detach()
            currentStreet = adapterStreet.getDataList()[it.tag as Int]
            onLastSelected()
            viewPager2.adapter?.notifyDataSetChanged()
            mediator.attach()
            viewPager2.currentItem = 3
        }

    }

    /**
     * 最后一项、街道级地址被选中时
     */
    private fun onLastSelected() {
        dialog.dismiss()
        listener?.onSelect(
            currentProvince as Province,
            currentCity as City,
            currentArea as Area,
            currentStreet as Street
        )
    }


    /**
     *  初始化地址数据，到时候应该改成从数据库获取
     */
    private fun initAddressData() {
        provinceLiveData.observe(context, { list ->
            adapterProvince.setDataList(list)
        })
        cityLiveData.observe(context, { list ->
            adapterCity.setDataList(list)
        })
        areaLiveData.observe(context, { list ->
            adapterArea.setDataList(list)
        })
        streetLiveData.observe(context, { list ->
            adapterStreet.setDataList(list)
        })
    }

    fun setOnSelectCompletedListener(listener: OnSelectListener) {
        this.listener = listener
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

    //从某个索引开始往后删除
    private fun ArrayList<*>.removeFromIndex(index: Int) {
        if (this.size > index) {
            while (this.size != index) {
                this.removeAt(index)
            }
        }
    }

}

interface OnSelectListener {
    fun onSelect(province: Province, city: City, area: Area, street: Street)
}


