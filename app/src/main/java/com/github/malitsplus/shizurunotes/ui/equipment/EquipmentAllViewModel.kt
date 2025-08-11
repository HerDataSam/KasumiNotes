package com.github.malitsplus.shizurunotes.ui.equipment

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.data.Item
import com.github.malitsplus.shizurunotes.ui.base.EquipmentCraftVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.EquipmentAllKey
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelEquipment

class EquipmentAllViewModel(
    val sharedChara: SharedViewModelChara,
    val sharedEquipment: SharedViewModelEquipment
) : ViewModel() {
    var sortMethod: SortMethod = SortMethod.ByCraftId

    val charaList = mutableListOf<Chara>()
        get() {
            field.clear()
            if (sharedChara.showSingleChara) {
                field.add(sharedChara.selectedChara!!)
            } else {
                sharedChara.charaList.value?.filter { it.isBookmarked }?.forEach {
                    field.add(it)
                }
            }
            return field
        }

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()

            val itemMap = when (sharedEquipment.equipmentAllKey) {
                EquipmentAllKey.ToMax -> allEquipments
                EquipmentAllKey.ToContentsMax -> contentsMaxEquipments
                EquipmentAllKey.ToTarget -> targetEquipments
                else -> allEquipments
            }

            getSortedEntry(itemMap).forEach {
                field.add(EquipmentCraftVT(it))
            }
            return field
        }

    private val allEquipments = mutableMapOf<Item, Int>()
        get() {
            field.clear()
            charaList.forEach { chara ->
                for (rank in (chara.maxCharaRank) downTo 1) {
                    chara.rankEquipments[rank]?.forEach {
                        if (it.itemId != 999999)
                            it.getLeafCraftMap().entries.forEach { entry ->
                                field.merge(entry.key, entry.value) { t, u -> t + u }
                            }
                    }
                }
            }
            return field
        }

    private val contentsMaxEquipments = mutableMapOf<Item, Int>()
        get() {
            field.clear()
            charaList.forEach { chara ->
                for ((i, v) in chara.getEquipmentList(chara.maxCharaContentsEquipment)
                    .withIndex()) {
                    if (v >= 0)
                        chara.rankEquipments[chara.maxCharaContentsRank]?.let {
                            it[i].getLeafCraftMap().entries.forEach { entry ->
                                field.merge(entry.key, entry.value) { t, u -> t + u }
                            }
                        }
                }
                for (rank in (chara.maxCharaContentsRank - 1) downTo 1) {
                    chara.rankEquipments[rank]?.forEach {
                        it.getLeafCraftMap().entries.forEach { entry ->
                            field.merge(entry.key, entry.value) { t, u -> t + u }
                        }
                    }
                }
            }
            return field
        }

    private val targetEquipments = mutableMapOf<Item, Int>()
        get() {
            field.clear()
            charaList.forEach { chara ->
                if (chara.targetSetting.rank > chara.displaySetting.rank) {
                    // add items of target rank equips
                    for ((i, v) in chara.targetSetting.equipment.withIndex()) {
                        if (v >= 0)
                            chara.rankEquipments[chara.targetSetting.rank]?.let {
                                it[i].getLeafCraftMap().entries.forEach { entry ->
                                    field.merge(entry.key, entry.value) { t, u -> t + u }
                                }
                            }
                    }
                    // add items which lack in the display rank
                    for ((i, v) in chara.displaySetting.equipment.withIndex()) {
                        if (v < 0)
                            chara.rankEquipments[chara.displaySetting.rank]?.let {
                                it[i].getLeafCraftMap().entries.forEach { entry ->
                                    field.merge(entry.key, entry.value) { t, u -> t + u }
                                }
                            }
                    }
                } else if (chara.targetSetting.rank == chara.displaySetting.rank) {
                    // if two rank is the same, add items which target rank only have
                    for ((i, v) in chara.targetSetting.equipment.withIndex()) {
                        if (v >= 0 && chara.displaySetting.equipment[i] < 0)
                            chara.rankEquipments[chara.targetSetting.rank]?.let {
                                it[i].getLeafCraftMap().entries.forEach { entry ->
                                    field.merge(entry.key, entry.value) { t, u -> t + u }
                                }
                            }
                    }
                }
                // add items between two ranks
                for (rank in (chara.targetSetting.rank - 1) downTo (chara.displaySetting.rank + 1)) {
                    chara.rankEquipments[rank]?.forEach {
                        it.getLeafCraftMap().entries.forEach { entry ->
                            field.merge(entry.key, entry.value) { t, u -> t + u }
                        }
                    }
                }
            }
            return field
        }

    private val displayEquipments = mutableMapOf<Item, Int>()
        get() {
            field.clear()
            charaList.forEach { chara ->
                for ((i, v) in chara.displaySetting.equipment.withIndex()) {
                    if (v >= 0)
                        chara.rankEquipments[chara.displaySetting.rank]?.let {
                            it[i].getLeafCraftMap().entries.forEach { entry ->
                                field.merge(entry.key, entry.value) { t, u -> t + u }
                            }
                        }
                }
                for (rank in (chara.displaySetting.rank - 1) downTo 1) {
                    chara.rankEquipments[rank]?.forEach {
                        it.getLeafCraftMap().entries.forEach { entry ->
                            field.merge(entry.key, entry.value) { t, u -> t + u }
                        }
                    }
                }
            }
            return field
        }

    fun getSortedEntry(map: MutableMap<Item, Int>): List<Map.Entry<Item, Int>> {
        val entries = map.toMap().entries
        return when (sortMethod) {
            SortMethod.ByNum -> entries.sortedByDescending { it.value }
            else -> entries.sortedByDescending { it.key.itemCraftedId }
        }
    }
}

enum class SortMethod {
    ByNum,
    ByCraftId
}