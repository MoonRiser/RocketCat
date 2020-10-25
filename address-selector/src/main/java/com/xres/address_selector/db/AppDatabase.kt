package com.xres.address_selector.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.pinyinhelper.PinyinMapDict
import com.xres.address_selector.db.dao.DivisionDao
import com.xres.address_selector.db.entity.Area
import com.xres.address_selector.db.entity.City
import com.xres.address_selector.db.entity.Province
import com.xres.address_selector.db.entity.Street


const val DATABASE_NAME = "divisions_data_cn"

@Database(
    entities = [Province::class, City::class, Area::class, Street::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun divisionDao(): DivisionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also {
                initPinYin()
                INSTANCE = it
            }
        }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .createFromAsset("databases/$DATABASE_NAME.db")
                .build()


        private fun initPinYin() {
            Pinyin.init(
                Pinyin.newConfig()
                    .with(object : PinyinMapDict() {
                        override fun mapping(): Map<String, Array<String>> {
                            val map = HashMap<String, Array<String>>()
                            map["重"] = arrayOf("CHONG")
                            return map
                        }
                    })
            )
        }

    }


}