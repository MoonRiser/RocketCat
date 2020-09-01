package com.example.rocketcat.data.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * @Author:         Xres
 * @CreateDate:     2020/8/21 12:41
 * @Description:
 */

@Parcelize
@Entity
data class Province(
    @PrimaryKey
    @ColumnInfo(name = "code")
    val code: String,
    @ColumnInfo(name = "name")
    val name: String
) : Parcelable, Division {
    @Ignore
    override fun code(): String {
        return code
    }
    @Ignore
    override fun name(): String {
        return name
    }


}