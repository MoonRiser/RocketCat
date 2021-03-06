package com.xres.address_selector.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xres.address_selector.db.entity.Area
import com.xres.address_selector.db.entity.City
import com.xres.address_selector.db.entity.Province
import com.xres.address_selector.db.entity.Street

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/21 13:00
 * @Description:
 */
@Dao
interface DivisionDao {

    @Query("SELECT * FROM province ")
    fun getProvinceList(): LiveData<List<Province>>

    @Query("SELECT * FROM city where provinceCode = :provinceCode")
    fun getCityList(provinceCode: String): LiveData<List<City>>

    @Query("SELECT * FROM area where cityCode = :cityCode")
    fun getAreaList(cityCode: String): LiveData<List<Area>>

    @Query("SELECT * FROM street where areaCode = :areaCode")
    fun getStreetList(areaCode: String): LiveData<List<Street>>

}