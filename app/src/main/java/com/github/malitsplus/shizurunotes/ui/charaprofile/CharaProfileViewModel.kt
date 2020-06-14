package com.github.malitsplus.shizurunotes.ui.charaprofile

import android.view.View
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import kotlin.collections.LinkedHashMap

class CharaProfileViewModel(
    val sharedChara: SharedViewModelChara
) : ViewModel() {

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            sharedChara.selectedChara?.let { chara ->
                field.add(CharaProfileVT(chara))
                field.add(CharaUniqueEquipmentVT(chara.uniqueEquipment ?: Equipment.getNull))
                if (chara.rarity6Status.isNotEmpty())
                    field.add(CharaRarity6StatusVT(chara.rarity6Status))
                chara.rankEquipments.entries.forEach {
                    field.add(CharaRankEquipmentVT(it))
                }
                chara
            }
            return field
        }
}

interface OnEquipmentClickListener<T>: OnItemActionListener {
    fun onEquipmentClicked(item: T)
}