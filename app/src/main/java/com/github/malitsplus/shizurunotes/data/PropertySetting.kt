package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N

class PropertySetting() {
    var rarity: Int = 0
    var rank: Int = 0
    var level: Int = 0
    var equipment: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0, 0)
    var uniqueEquipment: Int = 0
    var uniqueEquipment2: Int = 0
    var loveLevel: Int = 0
    var skillLevel: MutableList<Int> = mutableListOf(0, 0, 0, 0)

    fun setBasic(
        rarity: Int, rank: Int, level: Int,
        equipment: MutableList<Int>, uniqueEquipment: Int, uniqueEquipment2: Int,
        loveLevel: Int = 8, skillLevel: MutableList<Int> = mutableListOf(0, 0, 0, 0)
    ) {
        this.rarity = rarity
        this.rank = rank
        this.level = level
        this.equipment = equipment
        this.uniqueEquipment = uniqueEquipment
        this.uniqueEquipment2 = uniqueEquipment2
        this.loveLevel = loveLevel

        if (skillLevel[0] != 0) {
            this.skillLevel = skillLevel
        } else {
            this.skillLevel = mutableListOf(level, level, level, level)
        }
    }

    fun copyFrom(setting: PropertySetting): PropertySetting {
        rarity = setting.rarity
        rank = setting.rank
        level = setting.level
        equipment = setting.equipment
        uniqueEquipment = setting.uniqueEquipment
        uniqueEquipment2 = setting.uniqueEquipment2
        loveLevel = setting.loveLevel
        skillLevel = setting.skillLevel

        return this
    }

    var equipmentNumber: Int
        get() {
            return equipment.count { it >= 0 }
        }
        set(value) {
            val equipLists = mutableListOf(5, 5, 5, 5, 5, 5)

            // equipment 0 1 2 3 4 5 -> 0 2 4 / 1 3 5
            val convertMap = mapOf(0 to 0, 1 to 2, 2 to 4, 3 to 1, 4 to 3, 5 to 5)
            for (i in 0..(5 - value)) {
                equipLists[convertMap[i] ?: error("")] = -1
            }
            for (i in (5 - value + 1)..5) {
                equipLists[convertMap[i] ?: error("")] = 5
            }

            equipment = equipLists
        }

    fun changeRank(rank: Int) {
        if (rank != this.rank) {
            equipment = mutableListOf(5, 5, 5, 5, 5, 5)
        }
        this.rank = rank
    }

    fun changeEquipment(position: Int) {
        if (equipment.size > position) {
            val value = equipment[position]
            if (value < 0) {
                equipment[position] = 5
            } else {
                equipment[position] = value - 1
            }
        }
    }

    fun changeEquipmentLong(position: Int) {
        if (equipment.size > position) {
            val value = equipment[position]
            if (value < 0) {
                equipment[position] = 5
            } else {
                equipment[position] = -1
            }
        }
    }

    val displayStatus: String
        get() {
            return I18N.getString(
                R.string.rarity_d1_rank_d2_equipment_d3_level_d4_unique_d5,
                rarity, rank, equipmentNumber, level, uniqueEquipment
            )
        }
}