package com.github.malitsplus.shizurunotes.ui.mychara

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class MyCharaSettingViewModel (
    val sharedChara: SharedViewModelChara
) :ViewModel() {
    var charaList = mutableListOf<Chara>()
    var settingLevel: Int = 0

    val levelList = mutableListOf(0)
        get() {
            field.clear()
            for (i in sharedChara.maxCharaContentsLevel downTo 1) {
                field.add(i)
            }
            return field
        }

    fun applySetting() {
        charaList.forEach {
            it.displayLevel = settingLevel
            // TODO: rarity, rank, equipment
            sharedChara.updateChara(it)
        }
    }

    fun refreshChara() {
        val newCharaList = mutableListOf<Chara>()
        sharedChara.charaList.value?.filter { it.isBookmarked }?.forEach {
            newCharaList.add(it)
        }
        charaList = newCharaList
    }

    init {
        settingLevel = sharedChara.maxCharaContentsLevel
        refreshChara()
    }
}