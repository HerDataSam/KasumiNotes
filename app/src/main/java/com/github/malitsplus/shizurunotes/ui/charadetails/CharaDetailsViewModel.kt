package com.github.malitsplus.shizurunotes.ui.charadetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.ui.base.CharaLoveLevelVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.google.android.material.snackbar.Snackbar
import kotlin.math.min

class CharaDetailsViewModel(
    private val sharedViewModelChara: SharedViewModelChara
) : ViewModel() {

    val mutableChara = MutableLiveData<Chara>()
    val thisChara = sharedViewModelChara.selectedChara!!
    private val equipmentIds = listOf(
        R.id.rank_equipment_details_0, R.id.rank_equipment_details_1, R.id.rank_equipment_details_2,
        R.id.rank_equipment_details_3, R.id.rank_equipment_details_4, R.id.rank_equipment_details_5)
    private val uniqueEquipmentID = R.id.unique_equipment_details
    private val rarityIds = listOf(
        R.id.chara_star1, R.id.chara_star2, R.id.chara_star3,
        R.id.chara_star4, R.id.chara_star5, R.id.chara_star6
    )

    fun changeRank(rankString: String){
        val rank = rankString.toInt()
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            setCharaProperty(rank = rank)
            saveBookmarkedChara()
            skills.forEach {
                it.setActionDescriptions(chara.displaySetting.level, charaProperty)
            }
        }
        mutableChara.value = chara!!
        sharedViewModelChara.mSetSelectedChara(chara)
    }

    fun changeLevel(levelString: String) {
        val level = levelString.toInt()
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            setCharaProperty(level = level)
            saveBookmarkedChara()
            skills.forEach {
                it.setActionDescriptions(chara.displaySetting.level, charaProperty)
            }
        }
        mutableChara.value = chara!!
        sharedViewModelChara.mSetSelectedChara(chara)
    }

    fun changeRarity(rarity: Int) {
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            setCharaProperty(rarity = rarity)
            saveBookmarkedChara()
            skills.forEach {
                it.setActionDescriptions(chara.displaySetting.level, charaProperty)
            }
        }
        mutableChara.value = chara!!
        sharedViewModelChara.mSetSelectedChara(chara)
    }

    fun changeEquipment(equipment: Int) {
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            val displayEquipment = this.displaySetting.equipment
            if (displayEquipment[equipment] < 0) {
                displayEquipment[equipment] =
                    rankEquipments[this.displaySetting.rank]?.get(equipment)?.maxEnhanceLevel ?: 5 // suppose 5 is maximum equipment enhancement level
            } else {
                displayEquipment[equipment] -= 1
            }

            setCharaProperty(equipmentEnhanceList = displayEquipment)
            saveBookmarkedChara()
            skills.forEach {
                it.setActionDescriptions(chara.displaySetting.level, charaProperty)
            }
        }
        mutableChara.value = chara!!
        sharedViewModelChara.mSetSelectedChara(chara)
    }

    fun changeUniqueEquipment(level: Int) {
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            val uniqueEquipmentLevel = if (displaySetting.uniqueEquipment <= 0 && level < 0)
                1
            else if (maxUniqueEquipmentLevel <= level)
                maxUniqueEquipmentLevel
            else
                level
            setCharaProperty(uniqueEquipmentLevel = uniqueEquipmentLevel)
            saveBookmarkedChara()
            skills.forEach {
                it.setActionDescriptions(chara.displaySetting.level, charaProperty)
            }
        }
        mutableChara.value = chara!!
        sharedViewModelChara.mSetSelectedChara(chara)
    }

    fun changeLoveLevel(target: Int, up: Boolean) {
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            val targetLoveLevelList = if (charaId == target) {
                storyStatusList
            } else {
                otherStoryProperty[target] ?: mutableListOf()
            }
            val currentLoveLevel = if (charaId == target) {
                displaySetting.loveLevel
            } else {
                otherLoveLevel[target] ?: 1
            }

            val loveList = mutableListOf(1)
            loveList.addAll(targetLoveLevelList.map { it.loveLevel })
            if (!(up && loveList.last() == currentLoveLevel) && !(!up && loveList.first() == currentLoveLevel)) {
                val nextKey = if (up) 1 else -1
                val possibleLoveLevel = loveList[loveList.indexOf(currentLoveLevel) + nextKey]
                if (charaId == target) {
                    displaySetting.loveLevel = when (displaySetting.rarity) {
                        6 -> possibleLoveLevel
                        in 1..2 -> min(4, possibleLoveLevel)
                        else -> min(8, possibleLoveLevel)
                    }
                } else {
                    otherLoveLevel[target] = possibleLoveLevel
                }
            }
            setCharaProperty()
            saveBookmarkedChara()
            skills.forEach {
                it.setActionDescriptions(chara.displaySetting.level, charaProperty)
            }
        }
        mutableChara.value = chara!!
        sharedViewModelChara.mSetSelectedChara(chara)
    }

    val loveLevelViewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            mutableChara.value?.let {
                val newMap = mutableMapOf<Int, Int>()
                newMap[it.charaId] = it.displaySetting.loveLevel
                newMap.entries.forEach { entry ->
                    field.add(CharaLoveLevelVT(entry))
                }
                it.otherLoveLevel.entries.forEach { entry ->
                    field.add(CharaLoveLevelVT(entry))
                }
            }

            return field
        }

    fun checkAndChangeEquipment(id: Int?) {
        when {
            equipmentIds.contains(id) -> {
                changeEquipment(equipmentIds.indexOf(id))
            }
            uniqueEquipmentID == id -> {
                changeUniqueEquipment(-1)
            }
            rarityIds.contains(id) -> {
                changeRarity(rarityIds.indexOf(id) + 1)
            }
            /*
            R.id.chara_love_level_plus == id -> {

                changeLoveLevel(true)
            }
            R.id.chara_love_level_minus == id -> {
                changeLoveLevel(false)
            }
            */
        }
    }

    fun setChara(chara: Chara?) {
        mutableChara.value = chara!!
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

    fun setBookmark() : Boolean {
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            setBookmark(!isBookmarked)
        }
        mutableChara.value = chara!!
        return chara.isBookmarked
    }

    fun updateChara() {
        sharedViewModelChara.updateChara(mutableChara.value!!)
    }

    fun reloadChara() {
        setChara(sharedViewModelChara.selectedChara)
    }

    init {
        reloadChara()
    }
}

interface OnLoveLevelClickListener<T>: OnItemActionListener {
    fun onLoveLevelClickedListener(unitId: Int, up: Boolean)
}