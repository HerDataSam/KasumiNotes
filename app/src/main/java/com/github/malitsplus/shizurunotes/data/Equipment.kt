package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.Statics
import java.lang.Integer.min

class Equipment(
    val equipmentId: Int,
    val equipmentName: String,
    val description: String,
    val promotionLevel: Int,
    val craftFlg: Int,
    val equipmentEnhancePoint: Int,
    val salePrice: Int,
    val requireLevel: Int,
    val maxEnhanceLevel: Int,
    val equipmentProperty: Property,
    var equipmentEnhanceRates: List<Property>,
    val catalog: String,
    val rarity: Int
) : Item {
    override val itemId: Int = equipmentId
    override val iconUrl = Statics.EQUIPMENT_ICON_URL.format(equipmentId)
    override val itemName: String = equipmentName
    override val itemDescription: String = description
    override val itemType = ItemType.EQUIPMENT
    override val itemCraftedId: Int = equipmentId
    override var itemUseRank: Int = 99
    var craftMap: Map<Item, Int>? = null
    var charaEquipmentLink = mutableListOf<CharaEquipmentLink>()
    var upperEquipmentList = mutableSetOf<Equipment>()

    fun getCeiledProperty(): Property {
        return getEnhancedProperty(maxEnhanceLevel)
    }

    fun getEnhancedProperty(level: Int): Property {
        if (equipmentEnhanceRates.isEmpty()) {
            return Property()
        }
        return if (equipmentId in uniqueEquipmentIdRange) {
            if (level <= 260 || equipmentEnhanceRates.size == 1) {
                equipmentProperty.plus(equipmentEnhanceRates[0].multiply(min(maxEnhanceLevel, level).toDouble())).ceiled
            } else {
                equipmentProperty
                    .plus(equipmentEnhanceRates[0].multiply(259.toDouble())).ceiled
                    .plus(equipmentEnhanceRates[1].multiply((level - 260).toDouble())).ceiled
            }
        } else {
            equipmentProperty.plus(equipmentEnhanceRates[0].multiply(min(maxEnhanceLevel, level).toDouble())).ceiled
        }
    }

    fun getLeafCraftMap(): Map<Item, Int> {
        val leafMap = mutableMapOf<Item, Int>()
        craftMap?.forEach {
            addOrCreateLeaf(leafMap, it.key, it.value)
        }
        return leafMap
    }

    private fun addOrCreateLeaf(map: MutableMap<Item, Int>, item: Item, value: Int) {
        if (item is EquipmentPiece || item is GeneralItem || (item is Equipment && item.craftFlg == 0)) {
            if (map.containsKey(item)) {
                map[item]?.plus(value)
            } else {
                map[item] = value
            }
        } else if (item is Equipment && item.craftFlg == 1) {
            item.craftMap?.forEach {
                addOrCreateLeaf(map, it.key, it.value)
            }
        }
    }

    fun addCharaEquipmentLink(unitId: Int, prefabId: Int, searchAreaWidth: Int, promotionLevel: Int) {
        charaEquipmentLink.add(CharaEquipmentLink(unitId, prefabId, searchAreaWidth, promotionLevel))
        itemUseRank = min(promotionLevel, itemUseRank)
        craftMap?.forEach {
            addCharaEquipmentLinkLeaf(it.key, unitId, prefabId, searchAreaWidth, promotionLevel)
        }
    }

    private fun addCharaEquipmentLinkLeaf(
        item: Item,
        unitId: Int,
        prefabId: Int,
        searchAreaWidth: Int,
        promotionLevel: Int
    ) {
        if (item is Equipment && item.craftFlg == 1) {
            item.charaEquipmentLink.add(CharaEquipmentLink(unitId, prefabId, searchAreaWidth, promotionLevel))
            item.craftMap?.forEach {
                addCharaEquipmentLinkLeaf(it.key, unitId, prefabId, searchAreaWidth, promotionLevel)
            }
        }
    }

    fun sortCharaEquipmentLink() {
        charaEquipmentLink.sortWith(compareByDescending<CharaEquipmentLink> { it.promotionLevel }.thenBy { it.searchAreaWidth })
    }

    companion object {
        val uniqueEquipmentIdRange = 130000..139999
        val getNull = Equipment(
            999999,
            I18N.getString(R.string.unimplemented),
            "",
            0,
            0,
            0,
            0,
            0,
            0,
            Property(),
            listOf(Property(), Property()),
            "",
            0
        )
    }
}

class EquipmentPiece(
    private val id: Int,
    private val name: String,
    private val description: String,
    private val crafted: Int
) : Item {
    override val itemId: Int = id
    override val itemName: String = name
    override val itemDescription: String = description
    override val itemType: ItemType = ItemType.EQUIPMENT_PIECE
    override val iconUrl: String = Statics.EQUIPMENT_ICON_URL.format(itemId)
    override val itemCraftedId: Int = crafted
    override var itemUseRank: Int = 99
}