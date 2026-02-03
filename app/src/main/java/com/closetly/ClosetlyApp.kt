package com.closetly

import android.app.Application
import com.closetly.data.AppDatabase
import com.closetly.data.ClothingRepository

class ClosetlyApp : Application() {

    lateinit var repository: ClothingRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = ClothingRepository(db.clothingDao())
    }
}
