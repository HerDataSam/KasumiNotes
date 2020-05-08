package com.github.herdatasam.kasuminotes.ui.comparison

import androidx.lifecycle.ViewModel
import com.github.herdatasam.kasuminotes.ui.shared.SharedViewModelChara

class ComparisonViewModel(
    private val sharedViewModelChara: SharedViewModelChara
) : ViewModel() {
    val rankList = mutableListOf<Int>()
    val equipmentList = mutableListOf<Int>()

    init {
        for (i in this.sharedViewModelChara.maxCharaRank downTo 2) {
            rankList.add(i)
        }
        for (i in 6 downTo 0) {
            equipmentList.add(i)
        }
    }

}