package com.github.malitsplus.shizurunotes.data

import android.text.format.DateFormat
import androidx.annotation.DrawableRes
import com.github.malitsplus.shizurunotes.common.Statics
import com.github.malitsplus.shizurunotes.data.action.PassiveAction
import com.github.malitsplus.shizurunotes.user.UserSettings
import java.lang.Integer.max
import java.lang.Integer.min
import java.text.SimpleDateFormat
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
    var maxCharaRarity: Int = 5
    var maxCharaContentsRank: Int = 0
    var maxCharaContentsEquipment: Int = 0
    var maxUniqueEquipmentLevel: Int = 0
    var rarity: Int = 5
    var displayLevel: Int = 1
    var displayRank: Int = 1
    var displayRarity: Int = 5
    var displayUniqueEquipmentLevel: Int = 0

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
    lateinit var displayEquipments: MutableMap<Int, MutableList<Int>>
    var uniqueEquipment: Equipment? = Equipment.getNull

    var attackPatternList = mutableListOf<AttackPattern>()
    var skills = mutableListOf<Skill>()

    val birthDate: String by lazy {
        val calendar = Calendar.getInstance()
        calendar.set(calendar.get(Calendar.YEAR), birthMonth.toInt(), birthDay.toInt())
        val locale =  Locale(UserSettings.get().getLanguage())
        val format = DateFormat.getBestDateTimePattern(locale, "d MMM")
        SimpleDateFormat(format, locale).format(calendar.time)
    }

    @Suppress("UNUSED_PARAMETER")
    fun setCharaPropertyByEquipmentNumber(rarity: Int = displayRarity,
                         level: Int = displayLevel,
                         rank: Int = displayRank,
                         uniqueEquipmentLevel: Int = displayUniqueEquipmentLevel, equipmentNumber: Int = 6) {

        // check whether equipmentNumber exceeds EquipmentNum of contentsMax
        var fixedEquipmentNumber = equipmentNumber
        if (rank == maxCharaContentsRank)
            fixedEquipmentNumber = min(equipmentNumber, maxCharaContentsEquipment)
        else if (rank > maxCharaContentsRank)
            fixedEquipmentNumber = 0

        val equipLists: MutableList<Int> = displayEquipments[rank] ?: mutableListOf(5, 5, 5, 5, 5, 5)

        val convertMap = mapOf(0 to 0, 1 to 2, 2 to 4, 3 to 1, 4 to 3, 5 to 5)
        for (i in 0..(5 - fixedEquipmentNumber)) {
            equipLists[convertMap[i] ?: error("")] = -5
        }

        setCharaProperty(rarity, level, rank, uniqueEquipmentLevel, equipLists)
    }

    @Suppress("UNUSED_PARAMETER")
    fun setCharaProperty(rarity: Int = displayRarity,
                         level: Int = displayLevel,
                         rank: Int = displayRank,
                         uniqueEquipmentLevel: Int = displayUniqueEquipmentLevel,
                         equipmentEnhanceList: List<Int> = displayEquipments[rank] ?: listOf(5, 5, 5, 5, 5, 5)) {
        displayRarity = rarity
        displayLevel = level
        displayRank = rank
        displayUniqueEquipmentLevel = uniqueEquipmentLevel
        displayEquipments[rank] = equipmentEnhanceList.toMutableList()

        charaProperty = Property()
            .plusEqual(rarityProperty[rarity])
            .plusEqual(getRarityGrowthProperty(rarity, level, rank))
            .plusEqual(storyProperty)
            .plusEqual(promotionStatus[rank])
            .plusEqual(getAllEquipmentProperty(rank, equipmentEnhanceList))
            .plusEqual(if (UserSettings.get().preference.getBoolean(UserSettings.ADD_PASSIVE_ABILITY, true)) passiveSkillProperty else null)
            .plusEqual(uniqueEquipmentProperty)

        val prefabSetting: Int = when (displayRarity) {
            1, 2 -> 10
            6 -> 60
            else -> 30
        }

        iconUrl = String.format(Locale.US, Statics.ICON_URL, prefabId + prefabSetting)
        imageUrl = String.format(Locale.US, Statics.IMAGE_URL, prefabId + max(30, prefabSetting))
    }

    // TODO: load from initial status file of my character
    fun setCharaPropertyMax() {
        setCharaPropertyByEquipmentNumber(this.maxCharaRarity, this.maxCharaContentsLevel, this.maxCharaContentsRank,
            this.maxUniqueEquipmentLevel, this.maxCharaContentsEquipment)
    }

    private fun getRarityGrowthProperty(rarity: Int, level: Int, rank: Int): Property {
        val property = rarityPropertyGrowth[rarity] ?: Property()
        return property.multiply(level.toDouble() + rank)
    }

    fun getAllEquipmentProperty(rank: Int, equipmentEnhanceList: List<Int>): Property {
        val property = Property()

        for ((i, v) in equipmentEnhanceList.withIndex()) {
            if (v >= 0)
                property.plusEqual(rankEquipments[rank]?.get(i)?.getEnhancedProperty(v))
        }

        return property
    }

    val uniqueEquipmentProperty: Property
        get() {
            return uniqueEquipment?.getEnhancedProperty(displayUniqueEquipmentLevel) ?: Property()
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