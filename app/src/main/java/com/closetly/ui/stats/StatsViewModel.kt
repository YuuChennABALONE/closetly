package com.closetly.ui.stats

import androidx.lifecycle.*
import com.closetly.data.CategoryColorCount
import com.closetly.data.ClothingItem
import com.closetly.data.ClothingRepository
import kotlinx.coroutines.flow.combine

data class StatsState(
    val total: Int,
    val dups: List<CategoryColorCount>,
    val topWorn: List<ClothingItem>,
    val idle: List<ClothingItem>
)

class StatsViewModel(private val repo: ClothingRepository) : ViewModel() {

    private val idleBefore = System.currentTimeMillis() - 60L * 24 * 3600 * 1000 // 60 days

    val state: LiveData<StatsState> = repo.observeCount()
        .combine(repo.observeDuplicates()) { total, dups -> total to dups }
        .combine(repo.observeTopWorn(5)) { pair, top -> Triple(pair.first, pair.second, top) }
        .combine(repo.observeIdle(idleBefore, 5)) { triple, idle ->
            StatsState(
                total = triple.first,
                dups = triple.second,
                topWorn = triple.third,
                idle = idle
            )
        }.asLiveData()
}
