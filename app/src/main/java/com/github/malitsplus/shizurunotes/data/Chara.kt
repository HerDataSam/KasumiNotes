package com.github.malitsplus.shizurunotes.data

import android.text.format.DateFormat
import androidx.annotation.DrawableRes
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.Statics
import com.github.malitsplus.shizurunotes.data.action.PassiveAction
import com.github.malitsplus.shizurunotes.db.DBHelper
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.utils.CalcUtils.Companion.calcCombatPower
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
    var unitConversionId: Int? = null
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
    var maxCharaLoveLevel: Int = 8
    var maxCharaSetting = PropertySetting()
    var maxContentsSetting = PropertySetting()
    var displaySetting = PropertySetting()
    var targetSetting = PropertySetting()
    var isBookmarked: Boolean = false
    var isBookmarkLocked: Boolean = false

    lateinit var actualName: String
    lateinit var age: String
    lateinit var unitName: String
    var shortestName = ""
    var shortName = ""
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
    lateinit var guessProperty: Property
    val rarityProperty = mutableMapOf<Int, Property>()
    val rarityPropertyGrowth = mutableMapOf<Int, Property>()
    val otherStoryProperty: MutableMap<Int, MutableList<OneStoryStatus>> = hashMapOf()
    val otherLoveLevel = mutableMapOf<Int, Int>()
    lateinit var promotionStatus: Map<Int, Property>
    lateinit var promotionBonus: Map<Int, Property>
    var displayPromotionBonus: Boolean = false
    lateinit var rankEquipments: Map<Int, List<Equipment>>
    lateinit var displayEquipments: MutableMap<Int, MutableList<Int>>
    var uniqueEquipment: Equipment = Equipment.getNull
    var uniqueEquipment2: Equipment = Equipment.getNull
    lateinit var rarity6Status: List<Rarity6Status>

    var attackPatternList = mutableListOf<AttackPattern>()
    var skills = mutableListOf<Skill>()
    //var attackPatternConversionList = mutableListOf<AttackPattern>()
    //var conversionSkills = mutableListOf<Skill>()
    val storyStatusList = mutableListOf<OneStoryStatus>()

    fun storyProperty(loveLevel: Int): Property {
        val property = Property()
        property.apply {
            storyStatusList.filter { it.loveLevel <= loveLevel }.forEach {
                this.plusEqual(it.allProperty)
            }
        }
        return property
    }

    val birthDate: String by lazy {
        try {
            val calendar = Calendar.getInstance()
            calendar.set(calendar.get(Calendar.YEAR), birthMonth.toInt() - 1, birthDay.toInt())
            val locale =  Locale(UserSettings.get().getLanguage())
            val format = DateFormat.getBestDateTimePattern(locale, "d MMM")
            SimpleDateFormat(format, locale).format(calendar.time)
        } catch (e: Exception) {
            //LogUtils.file(LogUtils.E, "Failed to format ${unitName}'s birthDate string. Details: ${e.message}")
            birthMonth + I18N.getString(R.string.text_month) + birthDay + I18N.getString(R.string.text_day)
        }
    }

    // for comparison
    @Suppress("UNUSED_PARAMETER")
    fun setCharaPropertyByEquipmentNumber(rarity: Int = displaySetting.rarity,
                         level: Int = displaySetting.level,
                         rank: Int = displaySetting.rank,
                         uniqueEquipmentLevel: Int = displaySetting.uniqueEquipment,
                         uniqueEquipment2Level: Int = displaySetting.uniqueEquipment2,
                         equipmentNumber: Int = 6,
                         save: Boolean = true) {

        // check whether equipmentNumber exceeds EquipmentNum of contentsMax
        var equipmentNumberByContentsMax = equipmentNumber
        if (rank == maxCharaContentsRank)
            equipmentNumberByContentsMax = min(equipmentNumber, maxCharaContentsEquipment)
        else if (rank > maxCharaContentsRank)
            equipmentNumberByContentsMax = 0

        setCharaProperty(rarity, level, rank, uniqueEquipmentLevel, uniqueEquipment2Level,
            getEquipmentList(equipmentNumberByContentsMax), save)
    }

    fun setCharaProperty(setting: PropertySetting, save: Boolean = false) {
        val settingRarity = if (setting.rarity != 0)
                setting.rarity
            else
                displaySetting.rarity
        val settingLevel = if (setting.level != 0)
                setting.level
            else
                displaySetting.level
        val settingRank = if (setting.rank != 0)
                setting.rank
            else
                displaySetting.rank
        val settingUniqueEquipment = if (setting.uniqueEquipment >= 0)
            setting.uniqueEquipment
        else
            displaySetting.uniqueEquipment
        val settingUniqueEquipment2 = if (setting.uniqueEquipment2 >= 0)
            setting.uniqueEquipment2
        else
            displaySetting.uniqueEquipment2
        val settingEquipment = if (setting.equipment.size != 0)
            setting.equipment
        else
            displaySetting.equipment

        setCharaProperty(settingRarity, settingLevel, settingRank, settingUniqueEquipment,
            settingUniqueEquipment2, settingEquipment.toList(), save)
    }

    @Suppress("UNUSED_PARAMETER")
    fun setCharaProperty(rarity: Int = displaySetting.rarity,
                         level: Int = displaySetting.level,
                         rank: Int = displaySetting.rank,
                         uniqueEquipmentLevel: Int = displaySetting.uniqueEquipment,
                         uniqueEquipment2Level: Int = displaySetting.uniqueEquipment2,
                         equipmentEnhanceList: List<Int> = displaySetting.equipment,
                         save: Boolean = true) {
        if (save) {
            displaySetting.rarity = rarity
            displaySetting.level = level
            displaySetting.rank = rank
            displaySetting.uniqueEquipment = uniqueEquipmentLevel
            displaySetting.uniqueEquipment2 = uniqueEquipment2Level
            displaySetting.equipment = equipmentEnhanceList.toMutableList()
        }

        val prefabSetting: Int = when (displaySetting.rarity) {
            in 1..2 -> 10
            6 -> 60
            else -> 30
        }

        displaySetting.loveLevel = when (displaySetting.rarity) {
            in 1..2 -> min(4, displaySetting.loveLevel)
            6 -> displaySetting.loveLevel
            else -> min(8, displaySetting.loveLevel)
        }

        iconUrl = String.format(Locale.US, Statics.ICON_URL, prefabId + prefabSetting)
        imageUrl = String.format(Locale.US, Statics.FULL_IMAGE_URL, prefabId + max(30, prefabSetting))

        charaProperty = Property()
            .plusEqual(rarityProperty[rarity])
            .plusEqual(rarityGrowthProperty(rarity, level, rank))
            .plusEqual(storyProperty(displaySetting.loveLevel))
            .plusEqual(otherStoryProperty())
            .plusEqual(promotionStatus[rank])
            .plusEqual(equipmentProperty(equipmentEnhanceList, rank))
            .plusEqual(if (UserSettings.get().getExpressPassiveAbility()) passiveSkillProperty(rarity, level) else null)
            .plusEqual(uniqueEquipmentProperty(uniqueEquipmentLevel))
            .plusEqual(uniqueEquipment2Property(uniqueEquipment2Level))
            .plusEqual(promotionBonus[rank])

        guessProperty = Property()
            .plusEqual(rarityProperty[displaySetting.rarity])
            .plusEqual(rarityGrowthProperty(displaySetting.rarity, displaySetting.level, displaySetting.rank))
            .plusEqual(storyProperty(displaySetting.loveLevel))
            .plusEqual(otherStoryProperty())
            .plusEqual(promotionStatus[displaySetting.rank])
            .plusEqual(if (UserSettings.get().getExpressPassiveAbility()) passiveSkillProperty(displaySetting.rarity, displaySetting.level) else null)
            .plusEqual(uniqueEquipmentProperty(displaySetting.uniqueEquipment))
            .plusEqual(uniqueEquipment2Property(displaySetting.uniqueEquipment2))
            .plusEqual(promotionBonus[rank])

        displayPromotionBonus = promotionBonus[rank] != null
    }

    fun combatGuess(equipmentEnhanceList: List<Int> = displaySetting.equipment): Int {
        val newProperty = guessProperty.plus(equipmentProperty(equipmentEnhanceList, displaySetting.rank))
        val combat = calcCombatPower(newProperty, displaySetting.rarity, displaySetting.level,
            passiveSkillProperty(displaySetting.rarity, displaySetting.level),
            displaySetting.uniqueEquipment, displaySetting.uniqueEquipment2)
        return combat
    }

    private fun rarityGrowthProperty(rarity: Int, level: Int, rank: Int): Property {
        val property = rarityPropertyGrowth[rarity] ?: Property()
        return property.multiply(level.toDouble() + rank)
    }

    private fun otherStoryProperty(): Property {
        val property = Property()
        otherLoveLevel.entries.forEach {
            otherStoryProperty[it.key]?.filter { storyStatus ->
                storyStatus.loveLevel <= it.value
            }?.forEach { status ->
                property.plusEqual(status.allProperty)
            }
        }
        return property
    }

    private fun equipmentProperty(equipments: List<Int>, rank: Int): Property {
        val property = Property()

        for ((i, v) in equipments.withIndex()) {
            if (v >= 0)
                property.plusEqual(rankEquipments[rank]?.get(i)?.getEnhancedProperty(v))
        }

        return property
    }

    private fun uniqueEquipmentProperty(uniqueEquipmentLevel: Int): Property {
        // level 1: base property, level 2 ~: base + enhancement
        return uniqueEquipment.getEnhancedProperty(uniqueEquipmentLevel - 1)
    }

    private fun uniqueEquipment2Property(uniqueEquipment2Level: Int): Property {
        return uniqueEquipment2.getEnhancedProperty(uniqueEquipment2Level)
    }

    private val rarity6Property: Property
        get() {
            val property = Property()

            if (displaySetting.rarity >= 6)
                rarity6Status.forEach {
                    property.plusEqual(it.property)
                }

            return property
        }

    private fun passiveSkillProperty(rarity: Int, level: Int): Property {
        val property = Property()
        skills.forEach { skill ->
            if (rarity >= 5 && skill.skillClass == Skill.SkillClass.EX1_EVO) {
                skill.actions.forEach {
                    if (it.parameter is PassiveAction)
                        property.plusEqual((it.parameter as PassiveAction).propertyItem(level))
                }
            } else if (rarity < 5 && skill.skillClass == Skill.SkillClass.EX1) {
                skill.actions.forEach {
                    if (it.parameter is PassiveAction)
                        property.plusEqual((it.parameter as PassiveAction).propertyItem(level))
                }
            }
        }
        return property
    }

    /* Implementation is followed by the in-game function CalcOverall */
    val combatPower: Int
        get() {
            return calcCombatPower(charaProperty, displaySetting.rarity, displaySetting.level,
                passiveSkillProperty(displaySetting.rarity, displaySetting.level),
                displaySetting.uniqueEquipment, displaySetting.uniqueEquipment2)
        }

    fun getEquipmentList(equipmentNumber: Int): MutableList<Int> {
        val equipLists = mutableListOf(5, 5, 5, 5, 5, 5)

        // equipment 0 1 2 3 4 5 -> 0 2 4 / 1 3 5
        val convertMap = mapOf(0 to 0, 1 to 2, 2 to 4, 3 to 1, 4 to 3, 5 to 5)
        for (i in 0..(5 - equipmentNumber)) {
            equipLists[convertMap[i] ?: error("")] = -1
        }
        for (i in (5 - equipmentNumber + 1)..5) {
            equipLists[convertMap[i] ?: error("")] = 5
        }

        return equipLists
    }

    fun registerMyChara(value: Boolean = true) {
        if (value) {
            //val area = DBHelper.get().maxCharaContentArea
            val level = DBHelper.get().maxCharaContentsLevel
            displaySetting.level = level
            displaySetting.rank = DBHelper.get().maxCharaContentsRank
            val equipmentNumber = DBHelper.get().maxCharaContentsEquipment
            displaySetting.equipment = getEquipmentList(equipmentNumber)
            displaySetting.skillLevel = mutableListOf(level, level, level, level)

            targetSetting.rank = maxCharaContentsRank
            targetSetting.equipment = getEquipmentList(maxCharaContentsEquipment)
            //targetEquipments = getEquipmentList(maxCharaContentsEquipment)
        }
        setBookmark(value)
    }

    fun setBookmark(value: Boolean) {
        if (!isBookmarkLocked) {
            isBookmarked = value
            saveBookmarkedChara()
        }
    }

    fun reverseBookmark() {
        if (!isBookmarkLocked) {
            isBookmarked = !isBookmarked
            saveBookmarkedChara()
        }
    }

    fun saveBookmarkedChara() {
        if (isBookmarked) {
            UserSettings.get().saveCharaData(
                charaId, // TODO: send setting
                displaySetting.rarity,
                displaySetting.level,
                displaySetting.rank,
                displaySetting.equipment,
                displaySetting.uniqueEquipment,
                displaySetting.uniqueEquipment2,
                displaySetting.loveLevel,
                displaySetting.skillLevel,
                isBookmarkLocked
            )
            saveTargetChara()
        }
        else {
            UserSettings.get().removeCharaData(charaId)
            UserSettings.get().removeCharaData(charaId, UserSettings.TARGET)
        }
    }

    fun setIsBookmarkLocked(value: Boolean) {
        isBookmarkLocked = value
        saveBookmarkedChara()
    }

    fun saveTargetChara() {
        if (isBookmarked) {
            UserSettings.get().saveCharaData(
                charaId,
                displaySetting.rarity,
                displaySetting.level,
                targetSetting.rank,
                targetSetting.equipment,
                displaySetting.uniqueEquipment,
                displaySetting.uniqueEquipment2,
                displaySetting.loveLevel,
                displaySetting.skillLevel,
                isBookmarkLocked,
                UserSettings.TARGET
            )
        }
    }

    val levelList: MutableList<String>
        get() {
            val list: MutableList<String> = mutableListOf()
            for (i in maxCharaContentsLevel.downTo(1))
                list.add(i.toString())
            return list
        }

    val levelListInt: MutableList<Int>
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

    //val displayEquipmentNumber: Int
    //    get() {
    //        return displayEquipments[displayRank]?.count { it > 0 } ?: 0
    //    }

    val targetRankList = mutableListOf<Int>()
        get() {
            field.clear()
            var rank = maxCharaContentsRank
            var equip = maxCharaContentsEquipment
            repeat(5) {
                field.add(rank * 100 + equip)
                equip -= 1
                if (equip == 2) {
                    equip += 4
                    rank -= 1
                }
            }
            if (equip != 6) {
                rank -= 1
                equip = 6
            }

            repeat(4) {
                field.add(rank * 100 + equip)
                rank -= 1
            }
            field.add(1 * 100 + 0)

            return field
        }

    fun setTargetRankEquipment(value: Int) {
        if (isBookmarkLocked)
            return

        targetSetting.rank = value / 100
        //targetEquipmentNumber = value % 100
        targetSetting.equipment = getEquipmentList(value % 100)

        saveTargetChara()
    }

    val isConvertible: Boolean
        get() = unitConversionId != 0 && unitConversionId != null

    val unitIdDetail: String
        get() = if (UserSettings.get().detailedMode)
                " - $unitId"
            else
                ""

    fun getMaxUniqueEquipmentLevel(equipSlot: Int): Int {
        return if (equipSlot == 2)
            5
        else
            maxUniqueEquipmentLevel
    }

    val isUniqueEquipment2Available: Boolean
        get() = uniqueEquipment2 != Equipment.getNull
}