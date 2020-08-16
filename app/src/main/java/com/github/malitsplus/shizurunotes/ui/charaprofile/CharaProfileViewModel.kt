package com.github.malitsplus.shizurunotes.ui.charaprofile

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.data.Rarity6Status
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.EquipmentAllKey
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class CharaProfileViewModel(
    val sharedChara: SharedViewModelChara
) : ViewModel() {

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            sharedChara.selectedChara?.let { chara ->
                field.add(CharaProfileVT(chara))
                field.add(CharaUniqueEquipmentVT(chara.uniqueEquipment ?: Equipment.getNull))
                // rarity 6 info
                if (chara.rarity6Status.isNotEmpty())
                    field.add(CharaRarity6StatusVT(chara.rarity6Status))
                // All equipments
                field.add(CharaRankEquipmentAllVT(
                    listOf(EquipmentAllKey.ToMax, EquipmentAllKey.ToContentsMax, EquipmentAllKey.ToTarget)
                ))
                // each rank equipment
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
    fun onRarity6Clicked(item: Rarity6Status)
    fun onEquipmentAllClicked(item: EquipmentAllKey)
}
