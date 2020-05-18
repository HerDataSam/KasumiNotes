package com.github.malitsplus.shizurunotes.data

import androidx.annotation.DrawableRes
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.Statics
import com.github.malitsplus.shizurunotes.data.action.PassiveAction
import com.github.malitsplus.shizurunotes.ui.setting.SettingFragment
import com.github.malitsplus.shizurunotes.user.UserSettings
import java.lang.Integer.min
import java.time.LocalDateTime
import java.util.*

class Chara: Cloneable {

    @Throws(CloneNotSupportedException::class)
    override fun clone(): Chara {
        return super.clone() as Chara
    }

    fun shallowCopy(): Chara {
        return clone()
    }

    var unitId: Int = 0
    var charaId: Int = 0
    var prefabId: Int = 0
    var searchAreaWidth: Int = 0
    var atkType: Int = 0
    var moveSpeed: Int = 0
    var guildId: Int = 0
    var normalAtkCastTime: Double = 0.0
    @DrawableRes var positionIcon: Int = 0
    var maxCharaLevel: Int = 0
    var maxCharaContentsLevel: Int = 0
    var maxCharaRank: Int = 0
    var maxCharaContentsRank: Int = 0
    var maxCharaContentsEquipment: Int = 0
    var maxUniqueEquipmentLevel: Int = 0
    var rarity: Int = 5
    var displayLevel: Int = 1
    var displayRank: Int = 1
    var displayRarity: Int = 5

    lateinit var actualName: String
    lateinit var age: String
    lateinit var unitName: String
    lateinit var guild: String
    lateinit var race: String
    lateinit var height: String
    lateinit var weight: String
    lateinit var birthMonth: String
    lateinit var birthDay: String
    lateinit var bloodType: String
    lateinit var favorite: String
    lateinit var voice: String
    lateinit var kana: String
    lateinit var catchCopy: String
    lateinit var iconUrl: String
    lateinit var imageUrl: String
    lateinit var position: String
    var comment: String? = null
    var selfText: String? = null
    var sortValue: String? = null

    lateinit var startTime: LocalDateTime
    lateinit var startTimeStr: String

    lateinit var charaProperty: Property
    lateinit var rarityProperty: Map<Int, Property>
    lateinit var rarityPropertyGrowth: Map<Int, Property>
    lateinit var storyProperty: Property
    lateinit var promotionStatus: Map<Int, Property>
    lateinit var rankEquipments: Map<Int, List<Equipment>>
    var uniqueEquipment: Equipment? = null

    var attackPatternList = mutableListOf<AttackPattern>()
    var skills = mutableListOf<Skill>()

    val birthDate: String
        get() = StringBuilder()
            .append(birthMonth)
            .append(I18N.getString(R.string.text_month))
            .append(birthDay)
            .append(I18N.getString(R.string.text_day))
            .toString()

    @Suppress("UNUSED_PARAMETER")
    fun setCharaProperty(rarity: Int = displayRarity,
                         level: Int = maxCharaContentsLevel,
                         rank: Int = maxCharaContentsRank,
                         hasUnique: Boolean = true, equipmentNumber: Int = 6) {
        displayRarity = rarity
        displayLevel = level
        displayRank = rank
        charaProperty = Property()
            .plusEqual(rarityProperty[rarity])
            .plusEqual(getRarityGrowthProperty(rarity, level, rank))
            .plusEqual(storyProperty)
            .plusEqual(promotionStatus[rank])
            .plusEqual(getAllEquipmentProperty(rank, equipmentNumber))
            .plusEqual(if (UserSettings.get().preference.getBoolean(SettingFragment.ADD_PASSIVE_ABILITY, true)) passiveSkillProperty else null)
            .plusEqual(if (hasUnique) uniqueEquipmentProperty else null)

        if (displayRarity == 6) {
            iconUrl = String.format(Locale.US, Statics.ICON_URL, prefabId + 60)
            imageUrl = String.format(Locale.US, Statics.IMAGE_URL, prefabId + 60)
        }
    }

    // TODO: load from initial status file of my character
    fun setCharaPropertyMax() {
        setCharaProperty(this.rarity, this.maxCharaContentsLevel, this.maxCharaContentsRank, true, this.maxCharaContentsEquipment)
    }

    private fun getRarityGrowthProperty(rarity: Int, level: Int, rank: Int): Property {
        val property = rarityPropertyGrowth[rarity] ?: Property()
        return property.multiply(level.toDouble() + rank)
    }

    fun getAllEquipmentProperty(rank: Int, equipmentNumber: Int): Property {
        val property = Property()
        var size = rankEquipments[rank]?.size
        val equipLists: List<Int>

        var fixedEquipmentNumber = equipmentNumber
        if (rank == maxCharaContentsRank)
            fixedEquipmentNumber = min(equipmentNumber, maxCharaContentsEquipment)
        else if (rank > maxCharaContentsRank)
            fixedEquipmentNumber = 0

        if (size == null)
            size = 0
        when (size * 10 + fixedEquipmentNumber) {
            30, 40, 50, 60 -> equipLists = listOf()
            31 -> equipLists = listOf(2)
            32 -> equipLists = listOf(1, 2)
            33 -> equipLists = listOf(0, 1, 2)
            41 -> equipLists = listOf(3)
            42 -> equipLists = listOf(1, 3)
            43 -> equipLists = listOf(0, 1, 3)
            44 -> equipLists = listOf(0, 1, 2, 3)
            51 -> equipLists = listOf(4)
            52 -> equipLists = listOf(2, 4)
            53 -> equipLists = listOf(0, 2, 4)
            54 -> equipLists = listOf(0, 2, 3, 4)
            55 -> equipLists = listOf(0, 1, 2, 3, 4)
            61 -> equipLists = listOf(5)
            62 -> equipLists = listOf(3, 5)
            63 -> equipLists = listOf(1, 3, 5)
            64 -> equipLists = listOf(1, 3, 4, 5)
            65 -> equipLists = listOf(1, 2, 3, 4, 5)
            66 -> equipLists = listOf(0, 1, 2, 3, 4, 5)

            else -> equipLists = listOf()
        }

        equipLists.forEach { t: Int ->
            property.plusEqual(rankEquipments[rank]?.get(t)?.getCeiledProperty())
        }

        return property
    }

    val uniqueEquipmentProperty: Property
        get() {
            return Property()
                    .plusEqual(uniqueEquipment?.equipmentProperty)
                    .plusEqual(uniqueEquipment?.equipmentEnhanceRate?.multiply(maxUniqueEquipmentLevel - 1.toDouble()))
                    .ceiled
        }

    val passiveSkillProperty: Property
        get() {
            val property = Property()
            skills.forEach { skill ->
                if (skill.skillClass == Skill.SkillClass.EX1_EVO) {
                    skill.actions.forEach {
                        if (it.parameter is PassiveAction)
                            property.plusEqual((it.parameter as PassiveAction).propertyItem(displayLevel))
                    }
                }
            }
            return property
        }

    val levelList: MutableList<Int>
        get() {
            val list: MutableList<Int> = mutableListOf()
            for (i in maxCharaContentsLevel.downTo(1))
                list.add(i)
            return list
        }

    val rankList: MutableList<Int>
        get() {
            val list: MutableList<Int> = mutableListOf()
            for (i in maxCharaContentsRank.downTo(1))
                list.add(i)
            return list
        }
}