package com.closetly.ui.today

import androidx.lifecycle.*
import com.closetly.data.ClothingItem
import com.closetly.data.ClothingRepository
import com.closetly.ui.common.Categories
import com.closetly.ui.common.Colors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

data class OutfitSuggestion(
    val top: ClothingItem,
    val bottom: ClothingItem,
    val shoes: ClothingItem,
    val outer: ClothingItem? = null
)

class TodayViewModel(private val repo: ClothingRepository) : ViewModel() {

    private val allItems = repo.observeAll().asLiveData()

    private val _suggestion = MutableLiveData<OutfitSuggestion?>()
    val suggestion: LiveData<OutfitSuggestion?> = _suggestion

    fun generate(temp: String) {
        val list = allItems.value.orEmpty()
        val tops = list.filter { it.category == Categories.TOP }
        val bottoms = list.filter { it.category == Categories.BOTTOM }
        val shoes = list.filter { it.category == Categories.SHOES }
        val outers = list.filter { it.category == Categories.OUTER }

        if (tops.isEmpty() || bottoms.isEmpty() || shoes.isEmpty()) {
            _suggestion.value = null
            return
        }

        // Simple heuristic:
        // - pick a random top
        // - pick bottom that "matches" by a simple color rule
        val top = tops.random()
        val bottom = pickMatch(top, bottoms) ?: bottoms.random()
        val shoe = pickMatch(bottom, shoes) ?: shoes.random()

        val outer = if (temp == "冷" && outers.isNotEmpty()) pickMatch(top, outers) ?: outers.random() else null

        _suggestion.value = OutfitSuggestion(top = top, bottom = bottom, shoes = shoe, outer = outer)
    }

    private fun pickMatch(base: ClothingItem, candidates: List<ClothingItem>): ClothingItem? {
        val baseNeutral = Colors.isNeutral(base.color)
        val good = candidates.filter { c ->
            val cNeutral = Colors.isNeutral(c.color)
            baseNeutral || cNeutral || base.color != c.color
        }
        return if (good.isEmpty()) null else good[Random.nextInt(good.size)]
    }

    fun markWorn() {
        val s = _suggestion.value ?: return
        val now = System.currentTimeMillis()
        val items = listOfNotNull(s.top, s.bottom, s.shoes, s.outer)
        viewModelScope.launch(Dispatchers.IO) {
            items.forEach { i ->
                repo.update(
                    i.copy(
                        wornCount = i.wornCount + 1,
                        lastWornAt = now
                    )
                )
            }
        }
    }
}
