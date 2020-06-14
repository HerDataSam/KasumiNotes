package com.github.malitsplus.shizurunotes.db

import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.data.GeneralItem
import com.github.malitsplus.shizurunotes.data.ItemType
import com.github.malitsplus.shizurunotes.data.Rarity6Status

class MasterUnlockRarity6 {
    fun getCharaUnlockRarity6(chara: Chara): List<Rarity6Status> {
        val rarity6StatusList = mutableListOf<Rarity6Status>()
        var countItem = 0
        DBHelper.get().getUnlockRarity6(chara.unitId)?.forEach {
            if (it.unlock_flag == 1) { // unlocked
                DBHelper.get().getItemData(it.material_id)?.let { item ->
                    rarity6StatusList.add(
                        Rarity6Status(
                            GeneralItem(item.item_id, item.item_name, ItemType.GENERAL_ITEM, item.description),
                            countItem + it.material_count,
                            it.property))
                }
                countItem = 0
            } else {
                countItem += it.material_count
            }
        }
        return rarity6StatusList
    }
}