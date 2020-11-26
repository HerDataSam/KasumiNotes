package com.github.malitsplus.shizurunotes.ui.comparison

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
import com.github.malitsplus.shizurunotes.utils.CalcUtils
import kotlin.math.ceil

class ComparisonDetailsViewModel(
    private val sharedViewModelChara: SharedViewModelChara
) : ViewModel() {
    lateinit var diffProperty: Property
    lateinit var propertyTo: Property
    lateinit var propertyFrom: Property
    lateinit var charaFrom: Chara
    lateinit var charaTo: Chara

    var showTP: MutableLiveData<Boolean> = MutableLiveData(true)
    var showDef: MutableLiveData<Boolean> = MutableLiveData(false)
    var showDmg: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        sharedViewModelChara.selectedChara?.let {
            charaTo = it.shallowCopy().apply {
                sharedViewModelChara.equipmentComparisonToList?.let { equipmentList ->
                    setCharaProperty(
                        rarity = it.displayRarity,
                        rank = sharedViewModelChara.rankComparisonTo,
                        equipmentEnhanceList = equipmentList,
                        save = false
                    )
                } ?: run {
                    setCharaPropertyByEquipmentNumber(
                        rarity = it.displayRarity,
                        rank = sharedViewModelChara.rankComparisonTo,
                        equipmentNumber = sharedViewModelChara.equipmentComparisonTo,
                        save = false
                    )
                }
                skills.forEach {skill ->
                    skill.setActionDescriptions(this.displayLevel, this.charaProperty)
                }
            }
            propertyTo = charaTo.charaProperty
            charaFrom = it.shallowCopy().apply {
                sharedViewModelChara.equipmentComparisonFromList?.let { equipmentList ->
                    setCharaProperty(
                        rarity = it.displayRarity,
                        rank = sharedViewModelChara.rankComparisonFrom,
                        equipmentEnhanceList = equipmentList,
                        save = false
                    )
                } ?: run {
                    setCharaPropertyByEquipmentNumber(
                        rarity = it.displayRarity,
                        rank = sharedViewModelChara.rankComparisonFrom,
                        equipmentNumber = sharedViewModelChara.equipmentComparisonFrom,
                        save = false
                    )
                }
            }
            propertyFrom = charaFrom.charaProperty
            diffProperty = propertyTo.roundThenSubtract(propertyFrom) ?: Property()

        }
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
                            (sharedViewModelChara.rankComparisonFrom * 100 + sharedViewModelChara.equipmentComparisonFrom).toDouble(),
                            (sharedViewModelChara.rankComparisonTo * 100 + sharedViewModelChara.equipmentComparisonTo).toDouble(),
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
                                propertyFrom.getEnergyRecoveryRate(), charaFrom.searchAreaWidth,
                                10000, propertyFrom.getHp().toInt()
                            ),
                            CalcUtils.getTPRecoveryFromDamage(
                                propertyTo.getEnergyRecoveryRate(), charaTo.searchAreaWidth,
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