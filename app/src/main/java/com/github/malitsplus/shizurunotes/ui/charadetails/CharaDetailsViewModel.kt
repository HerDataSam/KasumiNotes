package com.github.malitsplus.shizurunotes.ui.charadetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class CharaDetailsViewModel(private val sharedViewModelChara: SharedViewModelChara) : ViewModel() {

    val mutableChara = MutableLiveData<Chara>()
    lateinit var displayEquipment: MutableList<Int>
    val equipmentIds = listOf(
        R.id.rank_equipment_details_0, R.id.rank_equipment_details_1, R.id.rank_equipment_details_2,
        R.id.rank_equipment_details_3, R.id.rank_equipment_details_4, R.id.rank_equipment_details_5)
    val uniqueEquipmentID = R.id.unique_equipment_details
    val rarityIds = listOf(
        R.id.chara_star1, R.id.chara_star2, R.id.chara_star3,
        R.id.chara_star4, R.id.chara_star5, R.id.chara_star6
    )

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
            val uniqueEquipmentLevel = if (displayUniqueEquipmentLevel < 0 && level < 0)
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
        if (equipmentIds.contains(id)){
            changeEquipment(equipmentIds.indexOf(id))
        }
        else if (uniqueEquipmentID == id) {
            changeUniqueEquipment(-1)
        }
        else if (rarityIds.contains(id)) {
            changeRarity(rarityIds.indexOf(id) + 1)
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

    val levelUndRankString: String
        get() {
            return mutableChara.value?.let {
                 I18N.getString(R.string.level_d1_rank_d2, it.maxCharaLevel, it.maxCharaRank)
            } ?: ""
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