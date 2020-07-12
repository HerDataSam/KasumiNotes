package com.github.malitsplus.shizurunotes.ui.tower

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.WaveGroup
import com.github.malitsplus.shizurunotes.ui.base.TowerWaveVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelTower

class TowerWaveViewModel(
    private val sharedTowerArea: SharedViewModelTower
) : ViewModel() {

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            sharedTowerArea.selectedTowerArea.value?.let { area ->
                area.towerWaveGroupMap.forEach {
                    field.add(TowerWaveVT(it))
                }
            }
            field.reverse()
            return field
        }
}

interface OnWaveClickListener: OnItemActionListener {
    fun onWaveClick(waveGroup: Map.Entry<String, WaveGroup>)
}