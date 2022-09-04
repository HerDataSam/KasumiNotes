package com.github.malitsplus.shizurunotes.ui.dungeon

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.SecretDungeonPeriodVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle

class SecretDungeonViewModel(
    private val sharedClanBattle: SharedViewModelClanBattle
) : ViewModel() {
    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            sharedClanBattle.secretDungeonList.value?.forEach { period ->
                field.add(SecretDungeonPeriodVT(period))
            }
            return field
        }
}

interface OnSecretDungeonListener<T>: OnItemActionListener {
    fun onSecretDungeonClick(item: T)
}