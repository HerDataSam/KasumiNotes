package com.github.malitsplus.shizurunotes.ui.charalist

import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.db.DBHelper
import com.github.malitsplus.shizurunotes.ui.base.CharaListVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.user.UserSettings
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CharaListViewModel(
    private val sharedViewModelChara: SharedViewModelChara
) : ViewModel() {

    fun getViewList(charaList: List<Chara>? = null): MutableList<ViewType<*>> {
        return if (charaList.isNullOrEmpty()) {
            mutableListOf<ViewType<*>>().apply {
                liveCharaList.value?.forEach {
                    add(CharaListVT(it))
                }
            }
        } else {
            mutableListOf<ViewType<*>>().apply {
                charaList.forEach {
                    add(CharaListVT(it))
                }
            }
        }
    }

    val liveCharaList = MutableLiveData<List<Chara>>()

    var selectedAttackType: String = "0"
    var selectedPosition: String = "0"
    var selectedSort: String = "0"
    var isAsc: Boolean = false
    var searchText: CharSequence = ""

    private val attackTypeMap = mapOf(
        0 to I18N.getString(R.string.ui_chip_any),
        1 to I18N.getString(R.string.ui_chip_atk_type_physical),
        2 to I18N.getString(R.string.ui_chip_atk_type_magical)
    )

    private val positionMap = mapOf(
        0 to I18N.getString(R.string.ui_chip_any),
        1 to I18N.getString(R.string.ui_chip_position_forward),
        2 to I18N.getString(R.string.ui_chip_position_middle),
        3 to I18N.getString(R.string.ui_chip_position_rear)
    )

    private val sortMap = mapOf(
        0 to I18N.getString(R.string.ui_chip_sort_new),
        1 to I18N.getString(R.string.ui_chip_sort_position),
        2 to I18N.getString(R.string.ui_chip_sort_name),
        3 to I18N.getString(R.string.ui_chip_sort_physical_atk),
        4 to I18N.getString(R.string.ui_chip_sort_magical_atk),
        5 to I18N.getString(R.string.ui_chip_sort_physical_critical),
        6 to I18N.getString(R.string.ui_chip_sort_magical_critical),
        7 to I18N.getString(R.string.ui_chip_sort_physical_def),
        8 to I18N.getString(R.string.ui_chip_sort_magical_def),
        9 to I18N.getString(R.string.ui_chip_sort_hp),
        10 to I18N.getString(R.string.ui_chip_sort_effective_physical_hp),
        11 to I18N.getString(R.string.ui_chip_sort_effective_magical_hp),
        12 to I18N.getString(R.string.ui_chip_sort_tp_up),
        13 to I18N.getString(R.string.ui_chip_sort_tp_reduce),
        14 to I18N.getString(R.string.ui_chip_sort_age),
        15 to I18N.getString(R.string.ui_chip_sort_height),
        16 to I18N.getString(R.string.ui_chip_sort_weight),
        17 to I18N.getString(R.string.ui_chip_sort_rarity_6)
    )

    val dropDownValuesMap = mapOf<Int, Array<String>>(
        1 to attackTypeMap.values.toTypedArray(),
        2 to positionMap.values.toTypedArray(),
        3 to sortMap.values.toTypedArray()
    )

    private fun filter(
        attackType: String?,
        position: String?,
        sortValue: String?,
        asc: Boolean?,
        searchText: CharSequence?
    ) {
        selectedAttackType = attackType?: selectedAttackType
        selectedPosition = position?: selectedPosition
        sortValue?.apply {
            isAsc = if (this == selectedSort) !isAsc else false
            selectedSort = this
        }

        asc?.let { isAsc = it }

        val charaToShow: MutableList<Chara> = ArrayList()
        sharedViewModelChara.charaList.value?.forEach { chara ->
            if (searchText?.isNotEmpty() == true) {
                if (chara.unitName.contains(searchText)
                    || chara.kana.contains(searchText)
                    || (chara.shortestName.isNotBlank() && searchText.contains(chara.shortestName))
                    || chara.shortestName.startsWith(searchText)
                ) {
                    setSortValue(chara, selectedSort)
                    charaToShow.add(chara)
                }
            } else if ((chara.startTime.isBefore(
                    LocalDateTime.parse(
                        DBHelper.get().areaTimeMap[UserSettings.get().contentsMaxArea],
                        DateTimeFormatter.ofPattern(I18N.getString(R.string.db_date_format))
                    ).plusDays(10)) || chara.isBookmarked
                        || (chara.maxCharaLevel == DBHelper.get().areaLevelMap[UserSettings.get().contentsMaxArea]))
                && checkAttackType(chara, selectedAttackType) && checkPosition(chara, selectedPosition)) {
                setSortValue(chara, selectedSort)
                charaToShow.add(chara)
            }
        }

        charaToShow.sortWith(kotlin.Comparator { a: Chara, b: Chara ->
            val valueA : Int
            val valueB : Int
            when (selectedSort) {
                "0" -> {
                    return@Comparator if (b.startTime.isEqual(a.startTime)) 0 else if (b.startTime.isAfter(a.startTime) == isAsc) -1 else 1
                }
                "1" -> { // intentionally reversed
                    valueA = b.searchAreaWidth
                    valueB = a.searchAreaWidth
                }
                "2" -> {
                    return@Comparator (if (isAsc) 1 else -1) * b.unitName.compareTo(a.unitName)
                }
                "3" -> {
                    valueA = a.charaProperty.getAtk()
                    valueB = b.charaProperty.getAtk()
                }
                "4" -> {
                    valueA = a.charaProperty.getMagicStr()
                    valueB = b.charaProperty.getMagicStr()
                }
                "5" -> {
                    valueA = a.charaProperty.getPhysicalCritical()
                    valueB = b.charaProperty.getPhysicalCritical()
                }
                "6" -> {
                    valueA = a.charaProperty.getMagicCritical()
                    valueB = b.charaProperty.getMagicCritical()
                }
                "7" -> {
                    valueA = a.charaProperty.getDef()
                    valueB = b.charaProperty.getDef()
                }
                "8" -> {
                    valueA = a.charaProperty.getMagicDef()
                    valueB = b.charaProperty.getMagicDef()
                }
                "9" -> {
                    valueA = a.charaProperty.getHp().toInt()
                    valueB = b.charaProperty.getHp().toInt()
                }
                "10" -> {
                    valueA = a.charaProperty.effectivePhysicalHP
                    valueB = b.charaProperty.effectivePhysicalHP
                }
                "11" -> {
                    valueA = a.charaProperty.effectiveMagicalHP
                    valueB = b.charaProperty.effectiveMagicalHP
                }
                "12" -> {
                    valueA = a.charaProperty.getEnergyRecoveryRate()
                    valueB = b.charaProperty.getEnergyRecoveryRate()
                }
                "13" -> {
                    valueA = a.charaProperty.getEnergyReduceRate()
                    valueB = b.charaProperty.getEnergyReduceRate()
                }
                "14" -> {
                    valueA = if (a.age.contains("?")) 9999 else a.age.toInt()
                    valueB = if (b.age.contains("?")) 9999 else b.age.toInt()
                }
                "15" -> {
                    valueA = if (a.height.contains("?")) 9999 else a.height.toInt()
                    valueB = if (b.height.contains("?")) 9999 else b.height.toInt()
                }
                "16" -> {
                    valueA = if (a.weight.contains("?")) 9999 else a.weight.toInt()
                    valueB = if (b.weight.contains("?")) 9999 else b.weight.toInt()
                }
                "17" -> {
                    valueA = if (a.maxCharaRarity > 5) a.charaId + 10000 else a.charaId
                    valueB = if (b.maxCharaRarity > 5) b.charaId + 10000 else b.charaId
                }
                else -> {
                    valueA = a.unitId
                    valueB = b.unitId
                }
            }
            (if (isAsc) -1 else 1) * valueB.compareTo(valueA)
        })
        liveCharaList.postValue(charaToShow)
    }

    fun filterDefault() {
        filter(selectedAttackType, selectedPosition, selectedSort, isAsc, searchText)
    }

    private fun checkPosition(chara: Chara, position: String): Boolean {
        return position == "0" || position == chara.position
    }

    private fun checkAttackType(chara: Chara, type: String): Boolean {
        return type == "0" || type.toInt() == chara.atkType
    }

    private fun setSortValue(chara: Chara, sortValue: String) {
        when (sortValue) {
            "1" -> chara.sortValue = chara.searchAreaWidth.toString()
            "3" -> chara.sortValue = chara.charaProperty.getAtk().toString()
            "4" -> chara.sortValue = chara.charaProperty.getMagicStr().toString()
            "5" -> chara.sortValue = chara.charaProperty.getPhysicalCritical().toString()
            "6" -> chara.sortValue = chara.charaProperty.getMagicCritical().toString()
            "7" -> chara.sortValue = chara.charaProperty.getDef().toString()
            "8" -> chara.sortValue = chara.charaProperty.getMagicDef().toString()
            "9" -> chara.sortValue = chara.charaProperty.getHp().toString()
            "10" -> chara.sortValue = chara.charaProperty.effectivePhysicalHP.toString()
            "11" -> chara.sortValue = chara.charaProperty.effectiveMagicalHP.toString()
            "12" -> chara.sortValue = chara.charaProperty.getEnergyRecoveryRate().toString()
            "13" -> chara.sortValue = chara.charaProperty.getEnergyReduceRate().toString()
            "14" -> {
                if (chara.actualName == "出雲 宮子") {
                    chara.sortValue = I18N.getString(R.string.aged_s, chara.age)
                } else {
                    chara.sortValue = chara.age
                }
            }
            "15" -> chara.sortValue = chara.height
            "16" -> chara.sortValue = chara.weight
            else -> chara.sortValue = ""
        }
    }

    val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            searchText = s
            filterDefault()
        }
    }

    fun getSpinnerClickListener(type: Int): AdapterView.OnItemClickListener {
        return when (type) {
            1 -> AdapterView.OnItemClickListener { _, _, position, _ ->
                filter(position.toString(), null, null, null, searchText)
            }
            2 -> AdapterView.OnItemClickListener { _, _, position, _ ->
                filter(null, position.toString(), null, null, searchText)
            }
            3 -> AdapterView.OnItemClickListener { _, _, position, _ ->
                filter(null, null, position.toString(), null, searchText)
            }
            else -> throw IllegalStateException("Illegal spinner adapter type $type.")
        }
    }
}

interface OnCharaActionListener : OnItemActionListener {
    fun onCharaClickedListener(chara: Chara, position: Int)
}
