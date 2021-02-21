package com.github.malitsplus.shizurunotes.utils

import com.github.malitsplus.shizurunotes.data.Property
import com.github.malitsplus.shizurunotes.data.UnitCoefficient
import com.github.malitsplus.shizurunotes.db.DBHelper
import com.github.malitsplus.shizurunotes.user.UserSettings
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

class CalcUtils {
    companion object {
        fun getTPRecovery(tpRecoveryRate: Int, actionTP: Double): Double {
            return actionTP * (1.0 + tpRecoveryRate.toDouble() / 100.0)
        }

        fun get1ActionTPRecovery(tpRecoveryRate: Int): Double {
            return getTPRecovery(tpRecoveryRate, 90.0)
        }

        fun getActionNumberToUB(tpRecoveryRate: Int, tpReduceRate: Int): Double {
            return 10.0 * (100.0 - tpReduceRate.toDouble()) / get1ActionTPRecovery(tpRecoveryRate)
        }

        fun getTPStackRatioDamage(searchAreaWidth: Int): Double {
            return when {
                searchAreaWidth <= 300 -> 0.5
                searchAreaWidth > 600 -> 0.5
                else -> 0.5
            }
        }

        fun getTPRecoveryFromDamage(tpRecoveryRate: Int, searchAreaWidth: Int,
                                    damage: Int, maxHP: Int): Double {
            return getTPRecovery(tpRecoveryRate,
                1000.0 / maxHP.toDouble() * getTPStackRatioDamage(searchAreaWidth) * damage.toDouble())
        }

        fun getDefReducePercentage(def: Int): Double {
            return 1.0 - getDefRatio(def)
        }

        fun getDefRatio(def: Int): Double {
            return 1.0 - (def.toDouble() / (def.toDouble() + 100.0))
        }

        fun getDamageByDefRatio(def: Int, damage: Double): Double {
            return damage * getDefRatio(def)
        }

        fun getDodgeRatio(dodge: Int): Double {
            return getDodgeRatio(dodge, 0)
        }

        fun getDodgeRatio(dodge: Int, accuracy: Int): Double {
            val dodgeByAccuracy = max(0.0, (dodge - accuracy).toDouble())
            return dodgeByAccuracy / (dodgeByAccuracy + 100.0)
        }

        fun getCriticalRatio(critical: Int): Double {
            return getCriticalRatio(critical, 1, 1)
        }

        fun getCriticalRatio(critical: Int, charaLevel: Int, enemyLevel: Int): Double {
            return critical.toDouble() / 20.0 / 100.0 * (charaLevel.toDouble() / enemyLevel.toDouble())
        }

        fun getExpectDamage(critical: Int, attack: Int, charaLevel: Int, enemyLevel: Int): Double {
            val criticalRatio = getCriticalRatio(critical, charaLevel, enemyLevel)
            return attack.toDouble() * 2.0 * criticalRatio + attack.toDouble() * (1 - criticalRatio)
        }

        fun calcCombatPower(charaProperty: Property, displayRarity: Int, displayLevel: Int,
                            passiveSkillProperty: Property, displayUniqueEquipmentLevel: Int): Int {
            var property = charaProperty
            // if passive ability is applied to stat, minus it
            if (UserSettings.get().preference.getBoolean(UserSettings.ADD_PASSIVE_ABILITY, true)) {
                property = property.plus(passiveSkillProperty.reverse())
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
            if (displayUniqueEquipmentLevel > 0) {
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
    }
}