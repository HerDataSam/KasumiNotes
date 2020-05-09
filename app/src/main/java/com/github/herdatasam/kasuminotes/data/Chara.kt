package com.github.herdatasam.kasuminotes.data

import androidx.annotation.DrawableRes
import com.github.herdatasam.kasuminotes.R
import com.github.herdatasam.kasuminotes.common.I18N
import com.github.herdatasam.kasuminotes.data.action.PassiveAction
import java.lang.StringBuilder
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
    fun setCharaProperty(rarity: Int = 0, rank: Int = maxCharaRank, hasUnique: Boolean = true, equipmentNumber: Int = 7) {
        charaProperty = Property()
            .plusEqual(rarityProperty)
            .plusEqual(getRarityGrowthProperty(rank))
            .plusEqual(storyProperty)
            .plusEqual(promotionStatus[rank])
            .plusEqual(getAllEquipmentProperty(rank, equipmentNumber))
            .plusEqual(passiveSkillProperty)
            .plusEqual(if (hasUnique) uniqueEquipmentProperty else null)
    }

    private fun getRarityGrowthProperty(rank: Int): Property{
        return rarityPropertyGrowth.multiply(maxCharaLevel.toDouble() + rank)
    }

    fun getAllEquipmentProperty(rank: Int, equipmentNumber: Int): Property {
        val property = Property()

        when (equipmentNumber) {
            0 -> null
            // Slot 5
            1 -> property.plusEqual(rankEquipments[rank]?.get(5)?.getCeiledProperty())
            // Slot 3, 5
            2 -> {
                property.plusEqual(rankEquipments[rank]?.get(5)?.getCeiledProperty())
                    .plusEqual(rankEquipments[rank]?.get(3)?.getCeiledProperty())
            }
            // Slot 1, 3, 5
            3 -> {
                property.plusEqual(rankEquipments[rank]?.get(5)?.getCeiledProperty())
                    .plusEqual(rankEquipments[rank]?.get(3)?.getCeiledProperty())
                    .plusEqual(rankEquipments[rank]?.get(1)?.getCeiledProperty())
            }
            // Slot 1, 3, 4, 5
            4 -> {
                property.plusEqual(rankEquipments[rank]?.get(5)?.getCeiledProperty())
                    .plusEqual(rankEquipments[rank]?.get(4)?.getCeiledProperty())
                    .plusEqual(rankEquipments[rank]?.get(3)?.getCeiledProperty())
                    .plusEqual(rankEquipments[rank]?.get(1)?.getCeiledProperty())
            }
            // Slot 1, 2, 3, 4, 5
            5 -> rankEquipments[rank]?.subList(1, 6)?.forEach {
                property.plusEqual(it.getCeiledProperty())
            }
            // Slot 0, 1, 2, 3, 4, 5
            6 -> rankEquipments[rank]?.subList(0, 6)?.forEach {
                property.plusEqual(it.getCeiledProperty())
            }
            else -> rankEquipments[rank]?.forEach {
                property.plusEqual(it.getCeiledProperty())
            }
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
                            property.plusEqual((it.parameter as PassiveAction).propertyItem(maxCharaLevel))
                    }
                }
            }
            return property
        }
}