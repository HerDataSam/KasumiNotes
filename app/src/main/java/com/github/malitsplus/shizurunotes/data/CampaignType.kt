package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N

enum class CampaignType {
    none(0),
    halfStaminaNormal(11),
    halfStaminaHard,
    halfStaminaBoth,
    halfStaminaShrine,
    halfStaminaTemple,
    halfStaminaVeryHard,
    dropRareNormal(21),
    dropRareHard,
    dropRareBoth,
    dropRareVeryHard,
    dropAmountNormal(31),
    dropAmountHard,
    dropAmountBoth,
    dropAmountExploration,
    dropAmountDungeon,
    dropAmountCoop,
    dropAmountShrine,
    dropAmountTemple,
    dropAmountVeryHard,
    manaNormal(41),
    manaHard,
    manaBoth,
    manaExploration,
    manaDungeon,
    manaCoop,
    manaTemple(48),
    manaVeryHard,
    coinDungeon(51),
    cooltimeArena(61),
    cooltimeGrandArena,
    playerExpAmountNormal(81),
    playerExpAmountHard,
    playerExpAmountVeryHard,
    playerExpAmountUniqueEquip,
    playerExpAmountHighRarityEquip,
    masterCoin(90),
    masterCoinNormal,
    masterCoinHard,
    masterCoinVeryHard,
    masterCoinShrine,
    masterCoinTemple,
    masterCoinEventNormal,
    masterCoinEventHard,
    masterCoinRevivalEventNormal,
    masterCoinRevivalEventHard,
    masterCoinDropShioriNormal(100),
    masterCoinDropShioriHard,
    halfStaminaEventNormal(111),
    halfStaminaEventHard,
    halfStaminaEventBoth,
    dropRareEventNormal(121),
    dropRareEventHard,
    dropRareEventBoth,
    dropAmountEventNormal(131),
    dropAmountEventHard,
    dropAmountEventBoth,
    manaEventNormal(141),
    manaEventHard,
    manaEventBoth,
    expEventNormal(151),
    expEventHard,
    expEventBoth,
    halfStaminaRevivalEventNormal(211),
    halfStaminaRevivalEventHard,
    dropRareRevivalEventNormal(221),
    dropRareRevivalEventHard,
    dropAmountRevivalEventNormal(231),
    dropAmountRevivalEventHard,
    manaRevivalEventNormal(241),
    manaRevivalEventHard,
    expRevivalEventNormal(251),
    expRevivalEventHard,
    playerExpAmountShioriNormal(351),
    playerExpAmountShioriHard;

    var value: Int = 0
    companion object {
        var nextValue: Int = 0
        fun parse(value: Int): CampaignType {
            CampaignType.values().forEach {
                if (it.value == value) {
                    return it
                }
            }
            return none
        }
    }

    //缩写版
    //隐藏掉一些无关紧要的活动，以免显示的活动日程过多
    fun shortDescription(): String = when (this) {
        manaDungeon -> I18N.getString(R.string.short_dungeon_s)
        masterCoinNormal -> I18N.getString(R.string.short_master_coin_s)
        dropAmountNormal -> I18N.getString(R.string.short_normal_drop_s)
        dropAmountHard -> I18N.getString(R.string.short_hard_s)
        dropAmountShrine -> I18N.getString(R.string.short_shrine_s)
        dropAmountTemple -> I18N.getString(R.string.short_temple_s)
        manaExploration -> I18N.getString(R.string.short_exploration_s)
        dropAmountVeryHard -> I18N.getString(R.string.short_very_hard_s)
        else -> ""
    }

    fun shortColor(): Int = when (this) {
        manaDungeon -> R.color.ModerateGreen
        masterCoinNormal -> R.color.SweetPink
        dropAmountNormal -> R.color.LightSkyBlue
        dropAmountHard -> R.color.LightBlue
        dropAmountShrine -> R.color.CottonCandy
        dropAmountTemple -> R.color.CottonCandy
        manaExploration -> R.color.Reef
        dropAmountVeryHard -> R.color.SummerSky
        else -> R.color.Sage
    }

    fun isVisible(): Boolean = shortDescription().isNotEmpty()

    fun order(): Int = when (this) {
        manaDungeon -> 4
        masterCoinNormal -> 7
        dropAmountNormal -> 1
        dropAmountHard -> 1
        dropAmountShrine -> 5
        dropAmountTemple -> 5
        manaExploration -> 3
        dropAmountVeryHard -> 1
        else -> 7
    }

    fun category(): String = when (this) {
        coinDungeon,
        manaDungeon,
        dropAmountDungeon -> I18N.getString(R.string.dungeon)
        manaNormal,
        dropRareNormal,
        masterCoinNormal,
        halfStaminaNormal,
        playerExpAmountNormal,
        dropAmountNormal -> I18N.getString(R.string.normal)
        expEventNormal,
        manaEventNormal,
        dropRareEventNormal,
        dropAmountEventNormal,
        masterCoinDropShioriNormal,
        playerExpAmountShioriNormal,
        masterCoinEventNormal -> I18N.getString(R.string.hatsune_normal)
        expRevivalEventNormal,
        manaRevivalEventNormal,
        dropRareRevivalEventNormal,
        dropAmountRevivalEventNormal,
        masterCoinRevivalEventNormal -> I18N.getString(R.string.revival_event_normal)
        manaHard,
        dropRareHard,
        masterCoinHard,
        halfStaminaHard,
        playerExpAmountHard,
        dropAmountHard -> I18N.getString(R.string.hard)
        expEventHard,
        manaEventHard,
        dropRareEventHard,
        dropAmountEventHard,
        masterCoinDropShioriHard,
        playerExpAmountShioriHard,
        masterCoinEventHard -> I18N.getString(R.string.hatsune_hard)
        expRevivalEventHard,
        manaRevivalEventHard,
        dropRareRevivalEventHard,
        dropAmountRevivalEventHard,
        masterCoinRevivalEventHard -> I18N.getString(R.string.revival_event_hard)
        dropAmountShrine,
        masterCoinShrine,
        playerExpAmountHighRarityEquip,
        halfStaminaShrine -> I18N.getString(R.string.shrine)
        manaTemple,
        dropAmountTemple,
        masterCoinTemple,
        playerExpAmountUniqueEquip,
        halfStaminaTemple -> I18N.getString(R.string.temple)
        manaExploration,
        dropAmountExploration -> I18N.getString(R.string.exploration)
        manaVeryHard,
        dropRareVeryHard,
        dropAmountVeryHard,
        masterCoinVeryHard,
        playerExpAmountVeryHard,
        halfStaminaVeryHard -> I18N.getString(R.string.very_hard)
        else -> I18N.getString(R.string.others)
    }

    private fun bonus(): String = when (this) {
        playerExpAmountNormal,
        playerExpAmountHard,
        playerExpAmountVeryHard,
        playerExpAmountUniqueEquip,
        playerExpAmountHighRarityEquip -> I18N.getString(R.string.exp_s)

        else -> when (value / 10 % 10) {
            3 -> I18N.getString(R.string.drop_s)
            4 -> I18N.getString(R.string.mana_s)
            5 -> I18N.getString(R.string.exp_s)
            9 -> I18N.getString(R.string.master_coin_s)
            else -> I18N.getString(R.string.others)
        }
    }

    //完整的活动日程字符串
    fun description(): String = category() + bonus()

    constructor() {
        this.value = nextValue
        nextValue++
    }

    constructor(value: Int) {
        this.value = value
        nextValue = value + 1
    }
}