package com.github.malitsplus.shizurunotes.ui.mychara

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.ui.base.CharaIconOnOffVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import kotlin.math.min

class MyCharaSettingViewModel (
    val sharedChara: SharedViewModelChara
) :ViewModel() {
    val rankAll = 9999
    val levelAll = 999
    val rankDownLimit = 800
    val rarityAll = 99
    val equipmentAll = 9
    val positionAll = 9
    val typeAll = 0

    var charaList = mutableListOf<Chara>()
    var enableList = mutableListOf<Boolean>()
    var searchLevel: Int = levelAll
    var settingLevel: Int = levelAll
    var settingRank: Int = rankAll
    var settingEquipment: Int = equipmentAll
    var settingRarity: Int = rarityAll

    var chipRankMap = mutableMapOf<Int, Boolean>()
    var chipRarityMap = mutableMapOf<Int, Boolean>()
    var chipPositionMap = mutableMapOf<Int, Boolean>()
    var chipTypeMap = mutableMapOf<Int, Boolean>()

    val levelList = mutableListOf<String>()
        get() {
            field.clear()
            field.add(I18N.getString(R.string.my_chara_setting_search_all))
            for (i in sharedChara.maxCharaContentsLevel downTo 1) {
                field.add(i.toString())
            }
            return field
        }

    val raritySettingList = mutableListOf<String>()
        get() {
            field.clear()
            field.add(I18N.getString(R.string.my_chara_setting_search_no_change))
            for (i in sharedChara.maxCharaRarity downTo 1) {
                field.add(i.toString())
            }
            return field
        }

    val levelSettingList = mutableListOf<String>()
        get() {
            field.clear()
            field.add(I18N.getString(R.string.my_chara_setting_search_no_change))
            for (i in sharedChara.maxCharaContentsLevel downTo 1) {
                field.add(i.toString())
            }
            return field
        }

    val rankSettingList = mutableListOf<String>()
        get() {
            field.clear()
            field.add(I18N.getString(R.string.my_chara_setting_search_no_change))
            for (i in sharedChara.maxCharaContentsRank downTo 1) {
                field.add(i.toString())
            }
            return field
        }

    val equipmentSettingList = mutableListOf<String>()
        get() {
            field.clear()
            field.add(I18N.getString(R.string.my_chara_setting_search_no_change))
            for (i in 6 downTo 0) {
                field.add(i.toString())
            }
            return field
        }

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            charaList.forEachIndexed { index, chara ->
                field.add(CharaIconOnOffVT(Pair(chara, enableList[index])))
            }
            return field
        }

    fun applySetting(): Boolean {
        if (settingEquipment != equipmentAll && settingRank == rankAll)
            return false

        charaList.forEachIndexed { index, it ->
            if (enableList[index]) {
                if (settingLevel != levelAll)
                    it.displaySetting.level = settingLevel
                if (settingRank != rankAll)
                    it.displaySetting.changeRank(settingRank)
                if (settingEquipment != equipmentAll)
                    it.displaySetting.equipment = it.getEquipmentList(settingEquipment)
                if (settingRarity != rarityAll)
                    it.displaySetting.rarity =  min(it.maxCharaRarity, settingRarity)
                it.saveBookmarkedChara()
                sharedChara.updateChara(it)
            }
        }
        return true
    }

    private fun filterByRarity(chara: Chara): Boolean {
        return if (chipRarityMap[rarityAll] == true)
            true
        else
            chipRarityMap[chara.displaySetting.rarity] ?: false
    }

    private fun filterByLevel(chara: Chara): Boolean {
        return if (searchLevel == levelAll) {
            true
        } else {
            chara.displaySetting.level == searchLevel
        }
    }


    private fun filterByRank(chara: Chara): Boolean {
        return when {
            chipRankMap[rankAll] == true -> true
            chara.displaySetting.rank <= rankDownLimit / 100 -> chipRankMap[rankDownLimit] ?: false
            else -> chipRankMap[chara.displaySetting.rank * 100] ?: false
        }
    }

    private fun filterByPosition(chara: Chara): Boolean {
        return when {
            chipPositionMap[positionAll] == true -> true
            else -> chipPositionMap[chara.searchAreaWidth / 300 + 1] ?: false
        }
    }

    private fun filterByType(chara: Chara): Boolean {
        return when {
            chipTypeMap[typeAll] == true -> true
            else -> chipTypeMap[chara.atkType] ?: false
        }
    }

    fun refreshChara() {
        val newCharaList = mutableListOf<Chara>()
        sharedChara.charaList.value?.filter { it.isBookmarked }?.forEach {
            newCharaList.add(it)
        }
        charaList = newCharaList
        charaList = charaList
            .filter { filterByRarity(it)
                    && filterByLevel(it)
                    && filterByRank(it)
                    && filterByPosition(it)
                    && filterByType(it) }
            .toMutableList()

        enableList.clear()
        charaList.forEach { _ ->
            enableList.add(true)
        }
    }

    fun setAllEnableList(value: Boolean) {
        enableList.forEachIndexed { index, _ ->
            enableList[index] = value
        }
    }

    init {
        settingLevel = sharedChara.maxCharaContentsLevel
        refreshChara()
    }
}

interface OnMyCharaSettingCharaListener : OnItemActionListener {
    fun charaClickListener(item: Pair<Chara, Boolean>)
}