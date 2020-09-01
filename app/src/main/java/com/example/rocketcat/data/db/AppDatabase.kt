package com.example.rocketcat.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rocketcat.data.db.dao.DivisionDao
import com.example.rocketcat.data.db.entity.Area
import com.example.rocketcat.data.db.entity.City
import com.example.rocketcat.data.db.entity.Province
import com.example.rocketcat.data.db.entity.Street


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
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .createFromAsset("databases/$DATABASE_NAME.db")
                .build()

    }


}