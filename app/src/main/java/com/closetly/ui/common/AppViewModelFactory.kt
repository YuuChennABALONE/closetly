package com.closetly.ui.common

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.closetly.ClosetlyApp
import com.closetly.data.ClothingRepository
import com.closetly.ui.closet.ClosetViewModel
import com.closetly.ui.stats.StatsViewModel
import com.closetly.ui.today.TodayViewModel
import com.closetly.ui.add.AddItemViewModel

class AppViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {

    private val repo: ClothingRepository
        get() = (app as ClosetlyApp).repository

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ClosetViewModel::class.java) ->
                ClosetViewModel(repo) as T
            modelClass.isAssignableFrom(TodayViewModel::class.java) ->
                TodayViewModel(repo) as T
            modelClass.isAssignableFrom(StatsViewModel::class.java) ->
                StatsViewModel(repo) as T
            modelClass.isAssignableFrom(AddItemViewModel::class.java) ->
                AddItemViewModel(repo) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
