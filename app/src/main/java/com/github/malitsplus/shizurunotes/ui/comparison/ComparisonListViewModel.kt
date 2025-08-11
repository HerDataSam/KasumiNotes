package com.github.malitsplus.shizurunotes.ui.comparison

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.data.RankComparison
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import kotlin.concurrent.thread

class ComparisonListViewModel(
    private val sharedViewModelChara: SharedViewModelChara
) : ViewModel() {

    val comparisonList = mutableListOf<RankComparison>()
    val liveComparisonList = MutableLiveData<List<RankComparison>>()

    var selectedAttackType: String = "0"
    var selectedPosition: String = "0"
    var selectedSort: String = "9"
    var isAsc: Boolean = false

    val attackTypeMap = mapOf(
        0 to I18N.getString(R.string.ui_chip_any),
        1 to I18N.getString(R.string.ui_chip_atk_type_physical),
        2 to I18N.getString(R.string.ui_chip_atk_type_magical)
    )

    val positionMap = mapOf(
        0 to I18N.getString(R.string.ui_chip_any),
        1 to I18N.getString(R.string.ui_chip_position_forward),
        2 to I18N.getString(R.string.ui_chip_position_middle),
        3 to I18N.getString(R.string.ui_chip_position_rear)
    )

    val sortMap = mapOf(
        0 to I18N.getString(R.string.ui_chip_sort_tp_up),
        1 to I18N.getString(R.string.ui_chip_sort_tp_reduce),
        2 to I18N.getString(R.string.ui_chip_sort_physical_atk),
        3 to I18N.getString(R.string.ui_chip_sort_magical_atk),
        4 to I18N.getString(R.string.ui_chip_sort_physical_def),
        5 to I18N.getString(R.string.ui_chip_sort_magical_def),
        6 to I18N.getString(R.string.ui_chip_sort_physical_critical),
        7 to I18N.getString(R.string.ui_chip_sort_magical_critical),
        8 to I18N.getString(R.string.ui_chip_sort_hp),
        9 to I18N.getString(R.string.ui_chip_sort_position)
    )

    fun filter(
        attackType: String?,
        position: String?,
        sortValue: String?,
        asc: Boolean?
    ) {
        selectedAttackType = attackType ?: selectedAttackType
        selectedPosition = position ?: selectedPosition
        sortValue?.apply {
            isAsc = if (this == selectedSort) !isAsc else false
            selectedSort = this
        }

        asc?.let { isAsc = it }

        val comparisonToShow: MutableList<RankComparison> = ArrayList()
        comparisonList.forEach { comparison ->
            if (checkAttackType(comparison.chara, selectedAttackType) && checkPosition(
                    comparison.chara,
                    selectedPosition
                )
            ) {
                comparisonToShow.add(comparison)
            }
            //comparison.chara.startTime.isBefore(
            //    LocalDateTime.parse(
            //        DBHelper.get().areaTimeMap[UserSettings.get().contentsMaxArea],
            //        DateTimeFormatter.ofPattern(I18N.getString(R.string.db_date_format))
            //    ).plusDays(7)) || (comparison.chara.isBookmarked
            //  || (comparison.chara.maxCharaLevel == DBHelper.get().areaLevelMap[UserSettings.get().contentsMaxArea])
        }

        comparisonToShow.sortWith(Comparator { a: RankComparison, b: RankComparison ->
            val valueA: Int
            val valueB: Int
            when (selectedSort) {
                "0" -> {
                    valueA = a.property.getEnergyRecoveryRate()
                    valueB = b.property.getEnergyRecoveryRate()
                }

                "1" -> {
                    valueA = a.property.getEnergyReduceRate()
                    valueB = b.property.getEnergyReduceRate()
                }

                "2" -> {
                    valueA = a.property.getAtk()
                    valueB = b.property.getAtk()
                }

                "3" -> {
                    valueA = a.property.getMagicStr()
                    valueB = b.property.getMagicStr()
                }

                "4" -> {
                    valueA = a.property.getDef()
                    valueB = b.property.getDef()
                }

                "5" -> {
                    valueA = a.property.getMagicDef()
                    valueB = b.property.getMagicDef()
                }

                "6" -> {
                    valueA = a.property.getPhysicalCritical()
                    valueB = b.property.getPhysicalCritical()
                }

                "7" -> {
                    valueA = a.property.getMagicCritical()
                    valueB = b.property.getMagicCritical()
                }

                "8" -> {
                    valueA = a.property.getHp().toInt()
                    valueB = b.property.getHp().toInt()
                }

                "9" -> { // intentionally reversed
                    valueA = b.chara.searchAreaWidth
                    valueB = a.chara.searchAreaWidth
                }

                else -> {
                    valueA = a.chara.unitId
                    valueB = b.chara.unitId
                }
            }
            (if (isAsc) -1 else 1) * valueB.compareTo(valueA)
        })
        liveComparisonList.postValue(comparisonToShow)
    }

    fun filterDefault() {
        thread(start = true) {
            if (sharedViewModelChara.useMyChara)
                refreshMyCharaList()
            else
                refreshList()
            filter(selectedAttackType, selectedPosition, selectedSort, isAsc)
        }
    }

    private fun checkPosition(chara: Chara, position: String): Boolean {
        return position == "0" || position == chara.position
    }

    private fun checkAttackType(chara: Chara, type: String): Boolean {
        return type == "0" || type.toInt() == chara.atkType
    }

    fun refreshList() {
        comparisonList.clear()
        val rankFrom = sharedViewModelChara.rankComparisonFrom
        val rankTo = sharedViewModelChara.rankComparisonTo
        val equipmentFrom = sharedViewModelChara.equipmentComparisonFrom
        val equipmentTo = sharedViewModelChara.equipmentComparisonTo
        //考虑用户手快，charaList可能还在loading的情况
        for (i in 1..25) {
            if (sharedViewModelChara.loadingFlag.value == false) {
                break
            } else {
                Thread.sleep(200)
            }
        }
        sharedViewModelChara.charaList.value?.forEach {
            val propertyTo = it.shallowCopy().apply {
                setCharaPropertyByEquipmentNumber(rank = rankTo, equipmentNumber = equipmentTo, save = false)
            }.charaProperty
            val propertyFrom = it.shallowCopy().apply {
                setCharaPropertyByEquipmentNumber(rank = rankFrom, equipmentNumber = equipmentFrom, save = false)
            }.charaProperty
            comparisonList.add(
                RankComparison(
                    it,
                    it.iconUrl,
                    rankFrom,
                    rankTo,
                    propertyTo.roundThenSubtract(propertyFrom)
                )
            )
        }
    }

    fun refreshMyCharaList() {
        comparisonList.clear()
        //TODO: Do I need this?
        for (i in 1..25) {
            if (sharedViewModelChara.loadingFlag.value == false) {
                break
            } else {
                Thread.sleep(200)
            }
        }
        sharedViewModelChara.charaList.value?.let { charaList ->
            charaList.filter { it.isBookmarked && it.targetSetting.rank > 1 }.forEach {
                val rankFrom = it.displaySetting.rank
                val equipmentFrom = it.displaySetting.equipment.toList()
                val rankTo = it.targetSetting.rank
                val equipmentTo = it.targetSetting.equipment

                val propertyTo = it.shallowCopy().apply {
                    setCharaProperty(rank = rankTo, equipmentEnhanceList = equipmentTo)
                }.charaProperty

                val propertyFrom = it.shallowCopy().apply {
                    setCharaProperty(rank = rankFrom, equipmentEnhanceList = equipmentFrom)
                }.charaProperty
                comparisonList.add(
                    RankComparison(
                        it,
                        it.iconUrl,
                        rankFrom,
                        rankTo,
                        propertyTo.roundThenSubtract(propertyFrom),
                        true
                    )
                )
            }
        }
    }
}