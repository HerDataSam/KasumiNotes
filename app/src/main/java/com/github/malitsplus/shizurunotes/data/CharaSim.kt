package com.github.malitsplus.shizurunotes.data

class CharaSim (
    val chara: Chara
    ) {
    var HP = chara.charaProperty.hp
    var TP = 0

    var rarity: Int
        get() = chara.displaySetting.rarity
        set(value) {
            chara.displaySetting.rarity = value
        }

    var rank: Int
        get() = chara.displaySetting.rank
        set(value) {
            chara.displaySetting.changeRank(value)
        }

    var level: Int
        get() = chara.displaySetting.level
        set(value) {
            chara.displaySetting.level = value
        }

    var uniqueEquipmentLevel: Int
        get() = chara.displaySetting.uniqueEquipment
        set(value) {
            chara.displaySetting.uniqueEquipment = value
        }
}