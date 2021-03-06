package com.example.common.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


const val DATABASE_NAME = "divisions_data_cn"

//@Database(
//    entities = [Province::class, City::class, Area::class, Street::class],
//    version = 1,
//    exportSchema = false
//)
abstract class AppDatabase : RoomDatabase() {

//    abstract fun divisionDao(): DivisionDao

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