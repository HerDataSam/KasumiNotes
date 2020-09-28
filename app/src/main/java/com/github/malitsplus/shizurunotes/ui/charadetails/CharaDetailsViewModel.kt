package com.github.malitsplus.shizurunotes.ui.charadetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.ui.base.CharaLoveLevelVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.google.android.material.snackbar.Snackbar
import kotlin.math.min

class CharaDetailsViewModel(
    private val sharedViewModelChara: SharedViewModelChara
) : ViewModel() {

    val mutableChara = MutableLiveData<Chara>()
    lateinit var displayEquipment: MutableList<Int>
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
            saveBookmarkedChara()
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
            saveBookmarkedChara()
            skills.forEach {
                it.setActionDescriptions(chara.displayLevel, charaProperty)
            }
        }
        mutableChara.value = chara
    }

    fun changeEquipment(equipment: Int) {
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            val displayEquipment = this.displayEquipments[this.displayRank]!!
            if (displayEquipment[equipment] < 0) {
                displayEquipment[equipment] =
                    rankEquipments[this.displayRank]?.get(equipment)?.maxEnhanceLevel ?: 5 // suppose 5 is maximum equipment enhancement level
            } else {
                displayEquipment[equipment] -= 1
            }

            setCharaProperty(equipmentEnhanceList = displayEquipment)
            saveBookmarkedChara()
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
            saveBookmarkedChara()
            skills.forEach {
                it.setActionDescriptions(chara.displayLevel, charaProperty)
            }
        }
        mutableChara.value = chara
    }

    fun changeLoveLevel(up: Boolean) {
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            val loveList = storyProperty.keys.toList()
            if (!(up && loveList.last() == displayLoveLevel) && !(!up && loveList.first() == displayLoveLevel)) {
                val nextKey = if (up) 1 else -1
                val possibleLoveLevel = loveList[loveList.indexOf(displayLoveLevel) + nextKey]
                displayLoveLevel = when (displayRarity) {
                    6 -> possibleLoveLevel
                    in 1..2 -> min(4, possibleLoveLevel)
                    else -> min(8, possibleLoveLevel)
                }
            }
            setCharaProperty()
            skills.forEach {
                it.setActionDescriptions(chara.displayLevel, charaProperty)
            }
        }
        mutableChara.value = chara
    }

    val loveLevelViewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()

            mutableChara.value?.otherLoveLevel?.let {
                it.entries.forEach { entry ->
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
            R.id.chara_love_level_plus == id -> {
                changeLoveLevel(true)
            }
            R.id.chara_love_level_minus == id -> {
                changeLoveLevel(false)
            }
        }
    }

    fun setChara(chara: Chara?) {
        mutableChara.value = chara
        setDisplayEquipment()
    }

    fun setDisplayEquipment() {
        mutableChara.value?.let {
            displayEquipment = it.displayEquipments[it.displayRank]!!
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

    fun setBookmark() : Boolean {
        val chara = mutableChara.value?.shallowCopy()
        chara?.apply {
            this.isBookmarked = !this.isBookmarked
        }
        mutableChara.value = chara
        return chara?.isBookmarked ?: false
    }

    fun updateChara() {
        sharedViewModelChara.updateChara(mutableChara.value!!)
    }

    init {
        setChara(sharedViewModelChara.selectedChara)
    }
}