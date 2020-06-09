package com.github.malitsplus.shizurunotes.ui.charadetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class CharaDetailsViewModel(private val sharedViewModelChara: SharedViewModelChara) : ViewModel() {

    val mutableChara = MutableLiveData<Chara>()
    lateinit var displayEquipment: MutableList<Int>
    val itemIds = listOf(
        R.id.rank_equipment_details_0, R.id.rank_equipment_details_1, R.id.rank_equipment_details_2,
        R.id.rank_equipment_details_3, R.id.rank_equipment_details_4, R.id.rank_equipment_details_5)
    val uniqueID = R.id.unique_equipment_details

    fun changeRank(rankString: String){
        val rank = rankString.toInt()
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            setCharaProperty(rank = rank)
            skills.forEach {
                it.setActionDescriptions(chara.displayLevel, charaProperty)
            }
        }
        mutableChara.value = chara
        setDisplayEquipment()
    }

    fun changeLevel(levelString: String) {
        val level = levelString.toInt()
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            setCharaProperty(level = level)
            skills.forEach {
                it.setActionDescriptions(chara.displayLevel, charaProperty)
            }
        }
        mutableChara.value = chara
    }

    fun changeRarity(rarity: Int) {
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            setCharaProperty(rarity = rarity)
            skills.forEach {
                it.setActionDescriptions(chara.displayLevel, charaProperty)
            }
        }
        mutableChara.value = chara
    }

    fun changeEquipment(equipment: Int) {
        val chara = mutableChara.value?.shallowCopy()
        if (displayEquipment[equipment] < 0) {
            displayEquipment[equipment] = 5 // suppose 5 is maximum equipment enhancement level
        } else {
            displayEquipment[equipment] = -5
        }
        chara?.apply {
            setCharaProperty(equipmentEnhanceList = displayEquipment)
            skills.forEach {
                it.setActionDescriptions(chara.displayLevel, charaProperty)
            }
        }
        mutableChara.value = chara
    }

    fun changeUniqueEquipment(level: Int) {
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            val uniqueEquipmentLevel = if (level < 0)
                -displayUniqueEquipmentLevel
            else
                level
            setCharaProperty(uniqueEquipmentLevel = uniqueEquipmentLevel)
            skills.forEach {
                it.setActionDescriptions(chara.displayLevel, charaProperty)
            }
        }
        mutableChara.value = chara
    }

    fun checkAndChangeEquipment(id: Int?) {
        if (itemIds.contains(id)){
            changeEquipment(itemIds.indexOf(id))
        }
        else if (uniqueID == id) {
            changeUniqueEquipment(-1)
        }
    }

    fun setChara(chara: Chara?) {
        mutableChara.value = chara
        setDisplayEquipment()
    }

    fun setDisplayEquipment() {
        if (mutableChara.value != null) {
            displayEquipment = mutableChara.value!!.displayEquipments[mutableChara.value!!.displayRank]!!
        }
    }

    fun getEquipment(equipment: Int): Int {
        return displayEquipment[equipment]
    }

    fun getChara(): Chara?{
        return mutableChara.value
    }

    fun getAttackPatternList(): List<Any> {
        val list = mutableListOf<Any>()
        mutableChara.value?.let { chara ->
            for (i in 1..chara.attackPatternList.size) {
                list.add(i)
                chara.attackPatternList[i - 1].items.forEach {
                    list.add(it)
                }
            }
        }
        return list
    }

    init {
        setChara(sharedViewModelChara.selectedChara)
    }
}