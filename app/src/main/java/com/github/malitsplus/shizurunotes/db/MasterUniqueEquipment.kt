package com.github.malitsplus.shizurunotes.db

import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.data.EquipmentPiece
import com.github.malitsplus.shizurunotes.data.GeneralItem
import com.github.malitsplus.shizurunotes.data.Item
import com.github.malitsplus.shizurunotes.data.ItemType
import com.github.malitsplus.shizurunotes.utils.Utils

class MasterUniqueEquipment {
    fun getCharaUniqueEquipment(chara: Chara): List<Equipment> {
        return DBHelper.get().getUniqueEquipment(chara.unitId)?.map {
            it.getCharaUniqueEquipment(chara).apply {
                val map = mutableMapOf<Item, Int>()
                for (i in 1..10) {
                    val itemId = Utils.getValueFromObject(it, "item_id_$i") as Int
                    if (itemId in 140000..140001) {
                        DBHelper.get().getEquipmentPiece(itemId)?.let { piece ->
                            map[EquipmentPiece(
                                piece.equipment_id,
                                piece.equipment_name,
                                piece.description,
                                piece.equipment_id / 10 * 10
                            )] = Utils.getValueFromObject(it, "consume_num_$i") as Int
                        }
                    } else if (itemId in 25000..39999) {
                        DBHelper.get().getItemData(itemId)?.let { item ->
                            map[GeneralItem(
                                item.item_id,
                                item.item_name,
                                ItemType.GENERAL_ITEM,
                                item.description,
                                item.item_id
                            )] = Utils.getValueFromObject(it, "consume_num_$i") as Int
                        }
                    }
                }
                craftMap = map
            }
        } ?: listOf(Equipment.getNull)
    }
}