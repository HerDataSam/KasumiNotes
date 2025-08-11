package com.github.malitsplus.shizurunotes.ui.dungeon

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.SecretDungeonFloorVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle

class SecretDungeonFloorViewModel(
    private val sharedClanBattle: SharedViewModelClanBattle
) : ViewModel() {
    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            sharedClanBattle.selectedSecretDungeon.value?.dungeonFloor?.forEach { floor ->
                field.add(SecretDungeonFloorVT(floor))
            }
            return field
        }
}

interface OnSecretDungeonFloorListener<T> : OnItemActionListener {
    fun onSecretDungeonFloorClick(item: T)
}