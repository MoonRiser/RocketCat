package com.example.common.widget.address_selector

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.viewpager2.widget.ViewPager2
import com.example.common.R
import com.example.common.data.db.AppDatabase
import com.example.common.data.db.entity.*
import com.example.common.ext.ClickCallback
import com.example.common.ext.activity
import com.example.common.ext.init
import com.google.android.material.color.MaterialColors
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/20 10:36
 * @Description:
 */
class AddressSelectorView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var listener: OnSelectListener? = null
    private var customView: View =
        LayoutInflater.from(context).inflate(R.layout.address_select_view, this, true)
    private var viewPager2: ViewPager2
    private var tabLayout: TabLayout
    private var mediator: TabLayoutMediator
    private val fragments = arrayListOf<AddressFragment>()
    private val primaryColor = MaterialColors.getColor(this, R.attr.colorPrimary)
    private val accentColor = MaterialColors.getColor(this, R.attr.colorAccent)


    //分别是省级、市级、区级、街道
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
    private val provinceLiveData: LiveData<List<Province>> =
        Transformations.switchMap(_provinceRq) {
            AppDatabase.getInstance(context.applicationContext).divisionDao().getProvinceList()
        }


    //刷新市级数据
    private fun refreshCity(provinceCode: String) {
        _provinceCode.value = provinceCode
    }

    private val _provinceCode = MutableLiveData<String>()
    private val cityLiveData: LiveData<List<City>> = Transformations.switchMap(_provinceCode) {
        AppDatabase.getInstance(context.applicationContext).divisionDao().getCityList(it)
    }


    //刷新区级数据
    private fun refreshArea(cityCode: String) {
        _cityCode.value = cityCode
    }

    private val _cityCode = MutableLiveData<String>()
    private val areaLiveData: LiveData<List<Area>> = Transformations.switchMap(_cityCode) {
        AppDatabase.getInstance(context.applicationContext).divisionDao().getAreaList(it)
    }


    /**刷新街道级数据
     *
     */
    private fun refreshStreet(areaCode: String) {
        _areaCode.value = areaCode
    }

    private val _areaCode = MutableLiveData<String>()
    private val streetLiveData: LiveData<List<Street>> = Transformations.switchMap(_areaCode) {
        AppDatabase.getInstance(context.applicationContext).divisionDao().getStreetList(it)
    }

    //四个级别的liveData的监听器
    private val provinceObserver = Observer<List<Province>> { list ->
        adapterProvince.setDataList(list)
    }
    private val cityObserver = Observer<List<City>> { list ->
        adapterCity.setDataList(list)
    }
    private val areaObserver = Observer<List<Area>> { list ->
        adapterArea.setDataList(list)
    }
    private val streetObserver = Observer<List<Street>> { list ->
        adapterStreet.setDataList(list)
    }


    init {


        tabLayout = customView.findViewById(R.id.tab_as)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AddressSelectorView)
        val color = typedArray.getColor(R.styleable.AddressSelectorView_select_color, primaryColor)
        val tabIndicatorColor =
            typedArray.getColor(R.styleable.AddressSelectorView_tabIndicatorColor, accentColor)
        adapterProvince.selectedColor = color
        adapterCity.selectedColor = color
        adapterArea.selectedColor = color
        adapterStreet.selectedColor = color
        tabLayout.apply {
            setSelectedTabIndicatorColor(tabIndicatorColor)
            setTabTextColors(Color.BLACK, color)
        }
        typedArray.recycle()

        //初始时，将省的数据先加载
        initProvince()
        initCity()
        initArea()
        initStreet()
        refreshProvince()
        //初始化viewpager
        viewPager2 = customView.findViewById(R.id.vp2_as)
        viewPager2.init(context.activity()!!, fragments, false)
        mediator = TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = currentProvince?.name ?: "请选择"
                1 -> tab.text = currentCity?.name ?: "请选择"
                2 -> tab.text = currentArea?.name ?: "请选择"
                3 -> tab.text = currentStreet?.name ?: "请选择"
            }
        }
        mediator.attach()

    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //初始化地址数据信息
        initAddressData()

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        provinceLiveData.removeObserver(provinceObserver)
        cityLiveData.removeObserver(cityObserver)
        areaLiveData.removeObserver(areaObserver)
        streetLiveData.removeObserver(streetObserver)
    }

    /**
     * 这个初始化省级数据注释写得很明白，下面三个init方法也是一样的，故省略
     */
    private fun initProvince() {
        fragments.add(fragmentProvince)
        adapterProvince.clickListener = ClickCallback {
            val position = it.tag as Int
            //mediator.detach和attach用于更新tab
            mediator.detach()
            currentProvince = adapterProvince.getDataList()[position]
            currentProvince?.code?.let { it1 -> refreshCity(it1) }
            //点击省级行政区时，要把省后面已经选择的市县行政区（如果有选择的话）移除掉
            fragments.removeFromIndex(1)
            fragmentCity.mAdapter.resetClickPosition()
            //fragmentCity.mAdapter.resetClickPosition()，因为第一次加载的时候，只有省级，所以点击item的时候要加载下一级的数据
            fragments.add(1, fragmentCity)
            //当前选中城市设null，标题会变成"请选择"
            currentCity = null
            //通知viewpager2的内容发生了变化
            viewPager2.adapter?.notifyDataSetChanged()
            //tab开始以新的vp2adapter数据更新
            mediator.attach()
            //跳转到下一级选中
            viewPager2.currentItem = 1
        }

    }

    private fun initCity() {
        adapterCity.clickListener = ClickCallback {
            mediator.detach()
            currentCity = adapterCity.getDataList()[it.tag as Int]
            currentCity?.code?.let { it1 -> refreshArea(it1) }
            fragments.removeFromIndex(2)
            fragmentArea.mAdapter.resetClickPosition()
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
            currentArea?.code?.let { it1 -> refreshStreet(it1) }
            fragments.removeFromIndex(3)
            fragmentStreet.mAdapter.resetClickPosition()
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
            mediator.attach()
        }

    }

    /**
     * 最后一项、街道级地址被选中时
     */
    private fun onLastSelected() {
        listener?.invoke(
            currentProvince as Province,
            currentCity as City,
            currentArea as Area,
            currentStreet as Street
        )
    }

    /**
     *  初始化地址数据，从数据库获取
     */
    private fun initAddressData() {
        provinceLiveData.observeForever(provinceObserver)
        cityLiveData.observeForever(cityObserver)
        areaLiveData.observeForever(areaObserver)
        streetLiveData.observeForever(streetObserver)
    }

    fun setOnSelectCompletedListener(listener: OnSelectListener) {
        this.listener = listener
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

typealias OnSelectListener = (province: Province, city: City, area: Area, street: Street) -> Unit