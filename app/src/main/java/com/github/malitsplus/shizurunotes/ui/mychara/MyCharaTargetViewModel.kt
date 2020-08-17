package com.github.malitsplus.shizurunotes.ui.mychara
/* MOST OF THE CODE IN THIS CLASS IS INHERITED FROM CHARALISTVIEWMODEL WRITTEN BY MalitsPlus */
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.ui.base.CharaTargetVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class MyCharaTargetViewModel(
    val sharedChara: SharedViewModelChara
) : ViewModel() {

    var selectedAttackType: String = "0"
    var selectedPosition: String = "0"
    var selectedSort: String = "1"
    var isAsc: Boolean = false
    var currentCharaList = MutableLiveData<List<Chara>>()

    fun updateCharaList() {
        currentCharaList.postValue(charaList)
    }

    val charaList = mutableListOf<Chara>()
    get() {
        field.clear()
        sharedChara.charaList.value?.filter { it.isBookmarked }?.forEach { chara ->
            if (checkAttackType(chara, selectedAttackType) && checkPosition(chara, selectedPosition)) {
                field.add(chara)
            }
        }
        field.sortWith(kotlin.Comparator { a: Chara, b: Chara ->
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
                    valueA = a.charaProperty.getEnergyRecoveryRate()
                    valueB = b.charaProperty.getEnergyRecoveryRate()
                }
                "11" -> {
                    valueA = a.charaProperty.getEnergyReduceRate()
                    valueB = b.charaProperty.getEnergyReduceRate()
                }
                else -> {
                    valueA = a.unitId
                    valueB = b.unitId
                }
            }
            (if (isAsc) -1 else 1) * valueB.compareTo(valueA)
        })
        return field
    }

    val viewList = mutableListOf<ViewType<*>>()
    get() {
        field.clear()
        currentCharaList.value?.forEach {
            field.add(CharaTargetVT(it))
        }
        return field
    }

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
        10 to I18N.getString(R.string.ui_chip_sort_tp_up),
        11 to I18N.getString(R.string.ui_chip_sort_tp_reduce)
    )

    val dropDownValuesMap = mapOf<Int, Array<String>>(
        1 to attackTypeMap.values.toTypedArray(),
        2 to positionMap.values.toTypedArray(),
        3 to sortMap.values.toTypedArray()
    )

    private fun checkPosition(chara: Chara, position: String): Boolean {
        return position == "0" || position == chara.position
    }

    private fun checkAttackType(chara: Chara, type: String): Boolean {
        return type == "0" || type.toInt() == chara.atkType
    }
}

interface OnCharaTargetClickListener<T>: OnItemActionListener {
    fun onCharaTargetClickedListener(chara: Chara, value: Int)
}