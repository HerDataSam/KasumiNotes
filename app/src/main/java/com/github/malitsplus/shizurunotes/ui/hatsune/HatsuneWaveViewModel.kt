package com.github.malitsplus.shizurunotes.ui.hatsune

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.WaveGroup
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelHatsune

class HatsuneWaveViewModel(
    private val sharedHatsune: SharedViewModelHatsune
) : ViewModel() {

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            sharedHatsune.selectedHatsune.value?.let { stage ->
                field.add(HintTextVT("하드 드롭"))
                stage.hatsuneItem.forEach { item ->
                    field.add(ItemIconVT(item))
                }
                field.add(HintTextVT("보스"))
                stage.battleWaveGroupMap.forEach {
                    field.add(HatsuneWaveVT(it))
                }
            }
            return field
        }
}

interface OnWaveClickListener : OnItemActionListener {
    fun onWaveClick(waveGroup: WaveGroup)
}