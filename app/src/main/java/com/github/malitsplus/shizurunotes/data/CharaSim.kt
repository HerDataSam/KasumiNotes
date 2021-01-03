package com.github.malitsplus.shizurunotes.data

class CharaSim (
    val chara: Chara
    ) {
    var HP = chara.charaProperty.hp
    var TP = 0

    var rarity: Int
        get() = chara.displayRarity
        set(value) {
            chara.displayRarity = value
        }

    var rank: Int
        get() = chara.displayRank
        set(value) {
            chara.displayRank = value
        }

    var level: Int
        get() = chara.displayLevel
        set(value) {
            chara.displayLevel = value
        }

    var uniqueEquipmentLevel: Int
        get() = chara.displayUniqueEquipmentLevel
        set(value) {
            chara.displayUniqueEquipmentLevel = value
        }
}