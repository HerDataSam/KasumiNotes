package com.github.malitsplus.shizurunotes.utils

import kotlin.math.ceil
import kotlin.math.max

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
                searchAreaWidth > 600 -> 1.0
                else -> 0.75
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
    }
}