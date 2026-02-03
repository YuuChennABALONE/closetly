package com.closetly.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closetly.data.ClothingItem
import com.closetly.data.ClothingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddItemViewModel(private val repo: ClothingRepository) : ViewModel() {

    fun load(id: Long, onLoaded: (ClothingItem?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = repo.getById(id)
            withContext(Dispatchers.Main) { onLoaded(item) }
        }
    }

    fun add(item: ClothingItem, onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.add(item)
            withContext(Dispatchers.Main) { onDone() }
        }
    }

    fun update(item: ClothingItem, onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.update(item)
            withContext(Dispatchers.Main) { onDone() }
        }
    }

    fun delete(item: ClothingItem, onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.delete(item)
            withContext(Dispatchers.Main) { onDone() }
        }
    }
}
