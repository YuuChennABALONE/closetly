package com.closetly.ui.closet

import androidx.lifecycle.*
import com.closetly.data.ClothingItem
import com.closetly.data.ClothingRepository
import kotlinx.coroutines.flow.map

class ClosetViewModel(private val repo: ClothingRepository) : ViewModel() {

    private val query = MutableLiveData("")

    private val allItems = repo.observeAll().asLiveData()

    val filtered: LiveData<List<ClothingItem>> = MediatorLiveData<List<ClothingItem>>().apply {
        fun update() {
            val q = query.value.orEmpty().trim()
            val list = allItems.value.orEmpty()
            value = if (q.isBlank()) list else list.filter {
                (it.category + it.color).contains(q) || q.contains(it.category) || q.contains(it.color)
            }
        }
        addSource(query) { update() }
        addSource(allItems) { update() }
    }

    fun setQuery(q: String) {
        query.value = q
    }
}
