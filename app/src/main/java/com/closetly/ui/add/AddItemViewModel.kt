package com.closetly.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.closetly.data.ClothingItem
import com.closetly.data.ClothingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddItemViewModel(private val repo: ClothingRepository) : ViewModel() {

    fun add(item: ClothingItem, onDone: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.add(item)
            kotlinx.coroutines.withContext(Dispatchers.Main) { onDone() }
        }
    }
}
