package com.github.malitsplus.shizurunotes.data


class RankComparison(
    val chara: Chara,
    val iconUrl: String,
    val rankFrom: Int,
    val rankTo: Int,
    val property: Property
) {

    val hp = getSigned(property.getHp())
    val atk = getSigned(property.getAtk())
    val magicStr = getSigned(property.getMagicStr())
    val def = getSigned(property.getDef())
    val magicDef = getSigned(property.getMagicDef())
    val physicalCritical = getSigned(property.getPhysicalCritical())
    val magicCritical = getSigned(property.getMagicCritical())
    val waveHpRecovery = getSigned(property.getWaveHpRecovery())
    val waveEnergyRecovery = getSigned(property.getWaveEnergyRecovery())
    val dodge = getSigned(property.getDodge())
    val lifeSteal = getSigned(property.getLifeSteal())
    val hpRecoveryRate = getSigned(property.getHpRecoveryRate())
    val energyRecoveryRate = getSigned(property.getEnergyRecoveryRate())
    val energyReduceRate = getSigned(property.getEnergyReduceRate())
    val accuracy = getSigned(property.getAccuracy())
    val physicalPenetrate = getSigned(property.getPhysicalPenetrate())
    val magicPenetrate = getSigned(property.getMagicPenetrate())

    private fun getSigned(num: Int): String {
        return if (num > 0) {
            "+$num"
        } else {
            num.toString()
        }
    }
    private fun getSigned(num: Long): String {
        return if (num > 0) {
            "+$num"
        } else {
            num.toString()
        }
    }
}