package com.github.malitsplus.shizurunotes.ui.comparison

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.data.Property
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class ComparisonDetailsViewModel(
    private val sharedViewModelChara: SharedViewModelChara
) : ViewModel() {
    lateinit var diffProperty: Property
    lateinit var propertyTo: Property
    lateinit var propertyFrom: Property
    lateinit var charaFrom: Chara
    lateinit var charaTo: Chara

    init {
        sharedViewModelChara.selectedChara?.let {
            charaFrom = it.shallowCopy().apply {
                setCharaProperty(
                    rarity = it.displayRarity,
                    rank = sharedViewModelChara.rankComparisonFrom,
                    equipmentNumber = sharedViewModelChara.equipmentComparisonFrom
                )
                skills.forEach { skill ->
                    skill.setActionDescriptions(this.displayLevel, this.charaProperty)
                }
            }
            propertyFrom = charaFrom.charaProperty
            charaTo = it.shallowCopy().apply {
                setCharaProperty(
                    rarity = it.displayRarity,
                    rank = sharedViewModelChara.rankComparisonTo,
                    equipmentNumber = sharedViewModelChara.equipmentComparisonTo
                )
                skills.forEach {skill ->
                    skill.setActionDescriptions(this.displayLevel, this.charaProperty)
                }
            }
            propertyTo = charaTo.charaProperty
            diffProperty = propertyTo.roundThenSubtract(propertyFrom) ?: Property()

        }
    }
}