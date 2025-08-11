package com.github.malitsplus.shizurunotes.ui.tower

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.TowerAreaVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelTower

class TowerAreaViewModel(
    private val sharedTowerArea: SharedViewModelTower
) : ViewModel() {

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            sharedTowerArea.towerAreaList.value?.forEach {
                field.add(TowerAreaVT(it))
            }
            field.reverse()
            return field
        }
}

interface OnTowerAreaClickListener<T> : OnItemActionListener {
    fun onTowerAreaClicked(item: T)
}