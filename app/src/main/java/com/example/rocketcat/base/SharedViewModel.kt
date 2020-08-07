package com.example.rocketcat.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.rocketcat.data.db.AppDatabase

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    val database = AppDatabase.getInstance(application)



}