package com.github.malitsplus.shizurunotes.data

import androidx.annotation.DrawableRes
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.action.PassiveAction
import java.time.LocalDateTime

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
    var maxUniqueEquipmentLevel: Int = 0
    var rarity: Int = 0

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
    lateinit var rarityProperty: Property
    lateinit var rarityPropertyGrowth: Property
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
    fun setCharaProperty(rarity: Int = 0, rank: Int = maxCharaRank, hasUnique: Boolean = true, equipmentNumber: Int = 6) {
        charaProperty = Property()
            .plusEqual(rarityProperty)
            .plusEqual(getRarityGrowthProperty(rank))
            .plusEqual(storyProperty)
            .plusEqual(promotionStatus[rank])
            .plusEqual(getAllEquipmentProperty(rank, equipmentNumber))
            .plusEqual(passiveSkillProperty)
            .plusEqual(if (hasUnique) uniqueEquipmentProperty else null)
    }

    private fun getRarityGrowthProperty(rank: Int): Property {
        return rarityPropertyGrowth.multiply(maxCharaContentsLevel.toDouble() + rank)
    }

    fun getAllEquipmentProperty(rank: Int, equipmentNumber: Int): Property {
        var property = Property()
        var size = rankEquipments[rank]?.size
        var equipLists: List<Int>

        if (size == null)
            size = 0;
        when (size * 10 + equipmentNumber) {
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
        }

    val passiveSkillProperty: Property
        get() {
            val property = Property()
            skills.forEach { skill ->
                if (skill.skillClass == Skill.SkillClass.EX1_EVO) {
                    skill.actions.forEach {
                        if (it.parameter is PassiveAction)
                            property.plusEqual((it.parameter as PassiveAction).propertyItem(maxCharaContentsLevel))
                    }
                }
            }
            return property
        }
}