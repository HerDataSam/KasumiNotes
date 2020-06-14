package com.github.malitsplus.shizurunotes.data

import android.text.format.DateFormat
import androidx.annotation.DrawableRes
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.Statics
import com.github.malitsplus.shizurunotes.data.action.PassiveAction
import com.github.malitsplus.shizurunotes.db.DBHelper
import com.github.malitsplus.shizurunotes.user.UserSettings
import java.lang.Integer.max
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt

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
    var displayCharaStory: Int = 8

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
    lateinit var rarity6Status: List<Rarity6Status>

    var attackPatternList = mutableListOf<AttackPattern>()
    var skills = mutableListOf<Skill>()

    val birthDate: String by lazy {
        if (birthMonth.contains("?") || birthDay.contains("?")) {
            birthMonth + I18N.getString(R.string.text_month) + birthDay + I18N.getString(R.string.text_day)
        } else {
            val calendar = Calendar.getInstance()
            calendar.set(calendar.get(Calendar.YEAR), birthMonth.toInt() - 1, birthDay.toInt())
            val locale =  Locale(UserSettings.get().getLanguage())
            val format = DateFormat.getBestDateTimePattern(locale, "d MMM")
            SimpleDateFormat(format, locale).format(calendar.time)
        }
    }

    // for comparison
    @Suppress("UNUSED_PARAMETER")
    fun setCharaPropertyByEquipmentNumber(rarity: Int = displayRarity,
                         level: Int = displayLevel,
                         rank: Int = displayRank,
                         uniqueEquipmentLevel: Int = displayUniqueEquipmentLevel, equipmentNumber: Int = 6) {

        // check whether equipmentNumber exceeds EquipmentNum of contentsMax
        var equipmentNumberByContentsMax = equipmentNumber
        if (rank == maxCharaContentsRank)
            equipmentNumberByContentsMax = min(equipmentNumber, maxCharaContentsEquipment)
        else if (rank > maxCharaContentsRank)
            equipmentNumberByContentsMax = 0

        val equipLists: MutableList<Int> = displayEquipments[rank] ?: mutableListOf(5, 5, 5, 5, 5, 5)

        // equipment 0 1 2 3 4 5 -> 0 2 4 / 1 3 5
        val convertMap = mapOf(0 to 0, 1 to 2, 2 to 4, 3 to 1, 4 to 3, 5 to 5)
        for (i in 0..(5 - equipmentNumberByContentsMax)) {
            equipLists[convertMap[i] ?: error("")] = -1
        }
        for (i in (5 - equipmentNumberByContentsMax + 1)..5) {
            equipLists[convertMap[i] ?: error("")] = 5
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

        val prefabSetting: Int = when (displayRarity) {
            1, 2 -> 10
            6 -> 60
            else -> 30
        }

        iconUrl = String.format(Locale.US, Statics.ICON_URL, prefabId + prefabSetting)
        imageUrl = String.format(Locale.US, Statics.IMAGE_URL, prefabId + max(30, prefabSetting))

        charaProperty = Property()
            .plusEqual(rarityProperty[displayRarity])
            .plusEqual(rarityGrowthProperty)
            .plusEqual(storyProperty) // TODO: apply story 4 / 8 / 12 by rarity
            .plusEqual(promotionStatus[displayRank])
            .plusEqual(equipmentProperty)
            .plusEqual(if (UserSettings.get().preference.getBoolean(UserSettings.ADD_PASSIVE_ABILITY, true)) passiveSkillProperty else null)
            .plusEqual(uniqueEquipmentProperty)
            .plusEqual(rarity6Property)
    }

    // TODO: load initial status from file of my character
    fun setCharaPropertyMax() {
        setCharaPropertyByEquipmentNumber(this.maxCharaRarity, this.maxCharaContentsLevel, this.maxCharaContentsRank,
            this.maxUniqueEquipmentLevel, this.maxCharaContentsEquipment)
    }

    private val rarityGrowthProperty: Property
        get() {
            val property = rarityPropertyGrowth[displayRarity] ?: Property()
            return property.multiply(displayLevel.toDouble() + displayRank)
        }

    private val equipmentProperty: Property
        get() {
            val property = Property()

            for ((i, v) in displayEquipments[displayRank]?.withIndex()!!) {
                if (v >= 0)
                    property.plusEqual(rankEquipments[displayRank]?.get(i)?.getEnhancedProperty(v))
            }

            return property
        }

    private val uniqueEquipmentProperty: Property
        get() {
            // level 1: base property, level 2 ~: base + enhancement
            return uniqueEquipment?.getEnhancedProperty(displayUniqueEquipmentLevel - 1) ?: Property()
        }

    private val rarity6Property: Property
        get() {
            val property = Property()

            if (displayRarity >= 6)
                rarity6Status.forEach {
                    property.plusEqual(it.property)
                }

            return property
        }

    private val passiveSkillProperty: Property
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

    /* Implementation is followed by the in-game function CalcOverall */
    val combatPower: Int
        get() {
            val property = charaProperty
            // if passive ability is applied to stat, minus it
            if (UserSettings.get().preference.getBoolean(UserSettings.ADD_PASSIVE_ABILITY, true)) {
                property.plusEqual(passiveSkillProperty.reverse())
            }

            val unitCoefficient = DBHelper.get().getUnitCoefficient()?.coefficient ?: UnitCoefficient()
            // calculate combat power of charaProperty
            var powerSum = property.multiplyElementWise(unitCoefficient.Property)

            var skillSum = 0.0
            // consider all skill levels are the same with chara level
            // UB
            if (displayRarity >= 6) {
                skillSum += unitCoefficient.ub_evolution_slv_coefficient.times(displayLevel)
                skillSum += unitCoefficient.ub_evolution_coefficient
            }
            else {
                skillSum += displayLevel * 1.0
            }
            // skill 1
            if (uniqueEquipment?.maxEnhanceLevel!! > 0) {
                skillSum += unitCoefficient.skill1_evolution_slv_coefficient.times(displayLevel)
                skillSum += unitCoefficient.skill1_evolution_coefficient
            }
            else {
                skillSum += displayLevel * 1.0
            }
            // skill 2 (no evolution yet)
            skillSum += displayLevel * 1.0
            // EX skill
            if (displayRarity >= 5) {
                skillSum += unitCoefficient.exskill_evolution_coefficient
            }
            skillSum += displayLevel * 1.0

            powerSum += skillSum.times(unitCoefficient.skill_lv_coefficient)
            return Math.pow(powerSum, unitCoefficient.overall_coefficient).roundToInt()
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