package com.github.malitsplus.shizurunotes.db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DBExtensionViewModel(private val repository: DBExtensionRepository) : ViewModel() {
    fun getActionPrefab(actionId: Int): List<ActionPrefab> {
        return repository.getActionPrefab(actionId)
    }

    fun insertActionPrefab(actionPrefab: ActionPrefab)  {
        repository.insertActionPrefab(actionPrefab)
    } //= viewModelScope.launch

    fun insertActionPrefabs(actionPrefabs: List<ActionPrefab>) {
        repository.insertActionPrefabs(actionPrefabs)
    }
}

class DBExtensionViewModelFactory(private val repository: DBExtensionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DBExtensionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DBExtensionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}