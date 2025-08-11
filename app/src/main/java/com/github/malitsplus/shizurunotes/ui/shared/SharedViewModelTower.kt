package com.github.malitsplus.shizurunotes.ui.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.TowerArea
import com.github.malitsplus.shizurunotes.db.MasterTower
import kotlin.concurrent.thread

class SharedViewModelTower : ViewModel() {
    val towerAreaList = MutableLiveData<List<TowerArea>>()
    val selectedTowerArea = MutableLiveData<TowerArea>(null)

    fun loadData() {
        if (towerAreaList.value.isNullOrEmpty()) {
            thread(start = true) {
                towerAreaList.postValue(MasterTower().getTower())
            }
        }
    }

    fun loadTowerWaveData() {
        if (selectedTowerArea.value?.towerWaveGroupMap.isNullOrEmpty()) {
            thread(start = true) {
                selectedTowerArea.postValue(MasterTower().getTowerWave(selectedTowerArea.value!!))
            }
        }
    }
}