package com.github.malitsplus.shizurunotes.ui.comparison

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.*
import com.github.malitsplus.shizurunotes.ui.base.DescriptionVT
import com.github.malitsplus.shizurunotes.ui.base.InGameStatComparisonVT
import com.github.malitsplus.shizurunotes.ui.base.DividerVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.utils.CalcUtils
import kotlin.math.ceil

class ComparisonDetailsViewModel(
    private val sharedViewModelChara: SharedViewModelChara
) : ViewModel() {
    lateinit var diffProperty: Property
    lateinit var propertyTo: Property
    lateinit var propertyFrom: Property
    val charaTo = MutableLiveData<Chara?>()
    val charaFrom = MutableLiveData<Chara?>()
    lateinit var propertySettingTo: PropertySetting
    lateinit var propertySettingFrom: PropertySetting
    var diffCombatPower = 0
    var diffCombatPowerString = ""

    var showTP: MutableLiveData<Boolean> = MutableLiveData(UserSettings.get().getShowTP())
    var showDef: MutableLiveData<Boolean> = MutableLiveData(UserSettings.get().getShowDef())
    var showDmg: MutableLiveData<Boolean> = MutableLiveData(UserSettings.get().getShowDmg())

    var showSetting = false
    var showComp: MutableLiveData<Boolean> = MutableLiveData(true)

    init {
        sharedViewModelChara.selectedChara?.let {
            // property setting to
            propertySettingTo = PropertySetting().copyFrom(it.displaySetting)
            propertySettingTo.rank = sharedViewModelChara.rankComparisonTo

            sharedViewModelChara.equipmentComparisonToList?.let { equipmentList ->
                propertySettingTo.equipment = equipmentList.toMutableList()
            } ?: run {
                propertySettingTo.equipmentNumber = sharedViewModelChara.equipmentComparisonTo
            }
            // chara and property to
            charaTo.value = it.shallowCopy().apply {
                setCharaProperty(propertySettingTo, false)
                skills.forEach {skill ->
                    skill.setActionDescriptions(this.displaySetting.level, this.charaProperty)
                }
            }
            propertyTo = charaTo.value?.charaProperty!!
            val combatPowerTo = charaTo.value?.combatPower!!

            // property setting from
            propertySettingFrom = PropertySetting().copyFrom(it.displaySetting)
            propertySettingFrom.rank = sharedViewModelChara.rankComparisonFrom

            sharedViewModelChara.equipmentComparisonFromList?.let { equipmentList ->
                propertySettingFrom.equipment = equipmentList.toMutableList()
            } ?: run {
                propertySettingFrom.equipmentNumber = sharedViewModelChara.equipmentComparisonFrom
            }
            // chara and property from
            charaFrom.value = it.shallowCopy().apply {
                setCharaProperty(propertySettingFrom, false)
            }
            propertyFrom = charaFrom.value?.charaProperty!!

            val combatPowerFrom = charaFrom.value?.combatPower!!
            diffCombatPower = combatPowerTo - combatPowerFrom
            diffCombatPowerString = if (diffCombatPower > 0)
                "+$diffCombatPower"
            else
                "$diffCombatPower"

            diffProperty = propertyTo.roundThenSubtract(propertyFrom) ?: Property()
        }
    }

    fun update() {
        val copyCharaTo = charaTo.value?.shallowCopy()
        copyCharaTo?.apply {
            setCharaProperty(propertySettingTo, false)
            skills.forEach {skill ->
                skill.setActionDescriptions(propertySettingTo.level, this.charaProperty)
            }
            propertyTo = charaProperty
        }
        val combatPowerTo = copyCharaTo?.combatPower!!
        charaTo.value = copyCharaTo

        val copyCharaFrom = charaFrom.value?.shallowCopy()
        copyCharaFrom?.apply {
            setCharaProperty(propertySettingFrom, false)
            propertyFrom = charaProperty
        }
        val combatPowerFrom = copyCharaFrom?.combatPower!!
        charaFrom.value = copyCharaFrom

        diffCombatPower = combatPowerTo - combatPowerFrom
        diffCombatPowerString = if (diffCombatPower > 0)
            "+$diffCombatPower"
        else
            "$diffCombatPower"

        diffProperty = propertyTo.roundThenSubtract(propertyFrom) ?: Property()
    }

    fun changeRarity(rarity: Int, from: Boolean) {
        if (from) {
            propertySettingFrom.rarity = rarity
        }
        else {
            propertySettingTo.rarity = rarity
        }
        update()
    }

    fun changeLevel(level: Int, from: Boolean) {
        if (from) {
            propertySettingFrom.level = level
        }
        else {
            propertySettingTo.level = level
        }
        update()
    }

    fun changeRank(rank: Int, from: Boolean) {
        if (from) {
            propertySettingFrom.rank = rank
        }
        else {
            propertySettingTo.rank = rank
        }
        update()
    }

    fun changeEquipment(equipment: Int, from: Boolean) {
        if (from) {
            propertySettingFrom.changeEquipment(equipment)
        }
        else {
            propertySettingTo.changeEquipment(equipment)
        }
        update()
    }

    fun changeEquipmentLong(equipment: Int, from: Boolean) {
        if (from) {
            propertySettingFrom.changeEquipmentLong(equipment)
        }
        else {
            propertySettingTo.changeEquipmentLong(equipment)
        }
        update()
    }

    fun changeUniqueEquipment(level: Int, from: Boolean) {
        if (from) {
            propertySettingFrom.uniqueEquipment = level
        }
        else {
            propertySettingTo.uniqueEquipment = level
        }
        update()
    }

    val viewList: MutableList<ViewType<*>>
        get() {
            val list = mutableListOf<ViewType<*>>()
            if (showTP.value!! || showDef.value!! || showDmg.value!!) {
                // TITLE
                list.add(DividerVT())
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_title),
                            (propertySettingFrom.rank * 100 + propertySettingFrom.equipmentNumber).toDouble(),
                            (propertySettingTo.rank * 100 + propertySettingTo.equipmentNumber).toDouble(),
                            DisplayCategory.TITLE, DisplayStyle.RANK_ITEM
                        )
                    )
                )
            }
            if (showTP.value!!) {
                // ABOUT TPs
                list.add(DividerVT())
                // tp 1 action
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_tp_1_action),
                            CalcUtils.get1ActionTPRecovery(propertyFrom.getEnergyRecoveryRate()),
                            CalcUtils.get1ActionTPRecovery(propertyTo.getEnergyRecoveryRate()),
                            DisplayCategory.TP, DisplayStyle.ROUND2
                        )
                    )
                )
                // 1st UB
                val ub1stFrom =
                    CalcUtils.getActionNumberToUB(propertyFrom.getEnergyRecoveryRate(), 0)
                val ub1stTo = CalcUtils.getActionNumberToUB(propertyTo.getEnergyRecoveryRate(), 0)
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_tp_1st_UB),
                            ceil(ub1stFrom) * 100.0 + ub1stFrom,
                            ceil(ub1stTo) * 100.0 + ub1stTo,
                            DisplayCategory.TP, DisplayStyle.ROUND0_2
                        )
                    )
                )
                // 2nd UB
                val ub2ndFrom = CalcUtils.getActionNumberToUB(
                    propertyFrom.getEnergyRecoveryRate(),
                    propertyFrom.getEnergyReduceRate()
                )
                val ub2ndTo = CalcUtils.getActionNumberToUB(
                    propertyTo.getEnergyRecoveryRate(),
                    propertyTo.getEnergyReduceRate()
                )
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_tp_2nd_UB),
                            ceil(ub2ndFrom) * 100.0 + ub2ndFrom,
                            ceil(ub2ndTo) * 100.0 + ub2ndTo,
                            DisplayCategory.TP, DisplayStyle.ROUND0_2
                        )
                    )
                )
                // tp recovery by damage
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_tp_recovery_by_damage),
                            CalcUtils.getTPRecoveryFromDamage(
                                propertyFrom.getEnergyRecoveryRate(), charaFrom.value?.searchAreaWidth ?: 300,
                                10000, propertyFrom.getHp().toInt()
                            ),
                            CalcUtils.getTPRecoveryFromDamage(
                                propertyTo.getEnergyRecoveryRate(), charaTo.value?.searchAreaWidth ?: 300,
                                10000, propertyTo.getHp().toInt()
                            ),
                            DisplayCategory.TP, DisplayStyle.ROUND2
                        )
                    )
                )
            }
            if (showDef.value!!) {
                // ABOUT DEFs
                list.add(DividerVT())
                // def ratio
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_def_physical),
                            CalcUtils.getDefReducePercentage(propertyFrom.getDef()),
                            CalcUtils.getDefReducePercentage(propertyTo.getDef()),
                            DisplayCategory.DEF,
                            DisplayStyle.PERCENTAGE2
                        )
                    )
                )
                // magicDef ratio
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_def_magical),
                            CalcUtils.getDefReducePercentage(propertyFrom.getMagicDef()),
                            CalcUtils.getDefReducePercentage(propertyTo.getMagicDef()),
                            DisplayCategory.DEF,
                            DisplayStyle.PERCENTAGE2
                        )
                    )
                )
                // 10k physical damage to chara by def
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_def_10k_physical_damage),
                            CalcUtils.getDamageByDefRatio(propertyFrom.getDef(), 10000.0),
                            CalcUtils.getDamageByDefRatio(propertyTo.getDef(), 10000.0),
                            DisplayCategory.DEF
                        )
                    )
                )
                // 10k magical damage to chara by def
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_def_10k_magical_damage),
                            CalcUtils.getDamageByDefRatio(propertyFrom.getMagicDef(), 10000.0),
                            CalcUtils.getDamageByDefRatio(propertyTo.getMagicDef(), 10000.0),
                            DisplayCategory.DEF
                        )
                    )
                )
                // 10k physical damage to chara by def
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_def_damage_to_10k_physical),
                            10000.0 / CalcUtils.getDefRatio(propertyFrom.getDef()),
                            10000.0 / CalcUtils.getDefRatio(propertyTo.getDef()),
                            DisplayCategory.DEF
                        )
                    )
                )
                // 10k magical damage to chara by def
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_def_damage_to_10k_magical),
                            10000.0 / CalcUtils.getDefRatio(propertyFrom.getMagicDef()),
                            10000.0 / CalcUtils.getDefRatio(propertyTo.getMagicDef()),
                            DisplayCategory.DEF
                        )
                    )
                )
                // effective physical HP of chara by def
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_def_effective_hp_physical),
                            propertyFrom.hp / CalcUtils.getDefRatio(propertyFrom.getDef()),
                            propertyTo.hp / CalcUtils.getDefRatio(propertyTo.getDef()),
                            DisplayCategory.DEF
                        )
                    )
                )
                // effective magical HP of chara by def
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_def_effective_hp_magical),
                            propertyFrom.hp / CalcUtils.getDefRatio(propertyFrom.getMagicDef()),
                            propertyTo.hp / CalcUtils.getDefRatio(propertyTo.getMagicDef()),
                            DisplayCategory.DEF
                        )
                    )
                )
            }
            if (showDmg.value!!) {
                // ABOUT DMGs
                list.add(DividerVT())
                // dodge ratio
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_dmg_dodge),
                            CalcUtils.getDodgeRatio(propertyFrom.getDodge()),
                            CalcUtils.getDodgeRatio(propertyTo.getDodge()),
                            DisplayCategory.DMG,
                            DisplayStyle.PERCENTAGE2
                        )
                    )
                )
                // dodge ratio 30
                list.add(
                    InGameStatComparisonVT(
                        InGameStatComparison(
                            I18N.getString(R.string.comparison_dmg_dodge_30),
                            CalcUtils.getDodgeRatio(propertyFrom.getDodge(), 30),
                            CalcUtils.getDodgeRatio(propertyTo.getDodge(), 30),
                            DisplayCategory.DMG,
                            DisplayStyle.PERCENTAGE2
                        )
                    )
                )

                val isPhysical = propertyFrom.getAtk() > propertyFrom.getMagicStr()
                if (isPhysical) {
                    // physical critical ratio
                    list.add(
                        InGameStatComparisonVT(
                            InGameStatComparison(
                                I18N.getString(R.string.comparison_dmg_physical_critical),
                                CalcUtils.getCriticalRatio(propertyFrom.getPhysicalCritical()),
                                CalcUtils.getCriticalRatio(propertyTo.getPhysicalCritical()),
                                DisplayCategory.DMG,
                                DisplayStyle.PERCENTAGE2
                            )
                        )
                    )
                    // physical attack * critical
                    list.add(
                        InGameStatComparisonVT(
                            InGameStatComparison(
                                I18N.getString(R.string.comparison_dmg_expect_physical_damage),
                                CalcUtils.getExpectDamage(
                                    propertyFrom.getPhysicalCritical(),
                                    propertyFrom.getAtk(),
                                    1,
                                    1
                                ),
                                CalcUtils.getExpectDamage(
                                    propertyTo.getPhysicalCritical(),
                                    propertyTo.getAtk(),
                                    1,
                                    1
                                ),
                                DisplayCategory.DMG,
                                DisplayStyle.ROUND0
                            )
                        )
                    )
                } else {
                    // magical critical ratio
                    list.add(
                        InGameStatComparisonVT(
                            InGameStatComparison(
                                I18N.getString(R.string.comparison_dmg_magical_critical),
                                CalcUtils.getCriticalRatio(propertyFrom.getMagicCritical()),
                                CalcUtils.getCriticalRatio(propertyTo.getMagicCritical()),
                                DisplayCategory.DMG,
                                DisplayStyle.PERCENTAGE2
                            )
                        )
                    )
                    // magical attack * critical
                    list.add(
                        InGameStatComparisonVT(
                            InGameStatComparison(
                                I18N.getString(R.string.comparison_dmg_expect_magical_damage),
                                CalcUtils.getExpectDamage(
                                    propertyFrom.getMagicCritical(),
                                    propertyFrom.getMagicStr(),
                                    1,
                                    1
                                ),
                                CalcUtils.getExpectDamage(
                                    propertyTo.getMagicCritical(),
                                    propertyTo.getMagicStr(),
                                    1,
                                    1
                                ),
                                DisplayCategory.DMG,
                                DisplayStyle.ROUND0
                            )
                        )
                    )
                }
            }
            if (showDef.value!! || showDmg.value!!) {
                // Descriptions
                list.add(DividerVT())
            }
            if (showDef.value!!) {
                list.add(DescriptionVT(I18N.getString(R.string.comparison_def_description)))
            }
            if (showDmg.value!!) {
                list.add(DescriptionVT(I18N.getString(R.string.comparison_dmg_description)))
            }
            return list
        }

}

interface OnSettingClickListener {
    fun onRarityClicked(rarity: Int, from: Boolean)
    fun onEquipmentClicked(number: Int, from: Boolean)
    fun onEquipmentLongClicked(number: Int, from: Boolean)
    fun onUniqueEquipmentClicked(number: Int, from: Boolean)
}