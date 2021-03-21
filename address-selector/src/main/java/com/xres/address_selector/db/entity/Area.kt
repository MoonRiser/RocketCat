package com.xres.address_selector.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/21 12:41
 * @Description:
 */
@Parcelize
@Entity
class Area(
    @PrimaryKey
    @ColumnInfo(name = "code")
    override val code: String,
    @ColumnInfo(name = "name")
    override val name: String,
    @ColumnInfo(name = "cityCode")
    val cityCode: String
) : Parcelable, Division