package com.xres.address_selector.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xres.address_selector.db.entity.Division
import kotlinx.android.parcel.Parcelize

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/21 12:41
 * @Description:
 */
@Parcelize
@Entity
data class City(
    @PrimaryKey
    @ColumnInfo(name = "code")
    override val code: String,
    @ColumnInfo(name = "name")
    override val name: String,
    @ColumnInfo(name = "provinceCode")
    val provinceCode: String
) : Parcelable, Division