package com.github.malitsplus.shizurunotes.ui.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.common.Statics
import com.github.malitsplus.shizurunotes.data.*
import com.github.malitsplus.shizurunotes.db.DBHelper.Companion.get
import com.github.malitsplus.shizurunotes.db.MasterUniqueEquipment
import com.github.malitsplus.shizurunotes.db.MasterUnlockRarity6
import com.github.malitsplus.shizurunotes.user.UserData
import com.github.malitsplus.shizurunotes.user.UserSettings
import java.util.*
import kotlin.concurrent.thread

class SharedViewModelChara : ViewModel() {

    val loadingFlag = MutableLiveData<Boolean>(false)
    val charaList = MutableLiveData<MutableList<Chara>>()

    var maxCharaLevel: Int = 0
    var maxCharaContentsLevel: Int = 0
    var maxCharaRank: Int = 0
    var maxCharaRarity: Int = 0
    var maxCharaContentsRank: Int = 0
    var maxCharaContentsEquipment: Int = 0
    var maxUniqueEquipmentLevel: Int = 0
    var maxEnemyLevel: Int = 0

    var selectedChara: Chara? = null
    var selectedMinion: MutableList<Minion>? = null
    var selectedRarity6Status: Rarity6Status? = null
    var showSingleChara: Boolean = false
    var useMyChara: Boolean = false
    var backFlag = false

    var rankComparisonFrom: Int = 0
    var rankComparisonTo: Int = 0

    var equipmentComparisonFrom: Int = 0
    var equipmentComparisonTo: Int = 0

    var equipmentComparisonFromList: List<Int>? = null
    var equipmentComparisonToList: List<Int>? = null

    var nicknames: Map<Int, UserSettings.NicknameData>? = null
    var myCharaList: List<UserData.MyCharaData> ?= null
    var myCharaTargetList: List<UserData.MyCharaData> ?= null

    /***
     * 从数据库读取所有角色数据。
     * 此方法应该且仅应该在程序初始化时或数据库更新完成后使用。
     */
    fun loadData(equipmentMap: Map<Int, Equipment>) {
        if (charaList.value.isNullOrEmpty()) {
            loadingFlag.postValue(true)
            thread(start = true) {
                val innerCharaList = mutableListOf<Chara>()
                loadBasic(innerCharaList)
                innerCharaList.forEach {
                    setCharaMaxData(it)
                    setCharaRarity(it)
                    setCharaStoryStatus(it)
                    setCharaPromotionStatus(it)
                    setCharaEquipments(it, equipmentMap)
                    setUniqueEquipment(it)
                    setRarity6Status(it)
                    setUnitSkillData(it)
                    setUnitAttackPattern(it)
                    setCharaDisplay(it)
                    setUnitNickname(it)
                    //it.setCharaPropertyMax()
                }
                charaList.postValue(innerCharaList)
                loadingFlag.postValue(false)
                callBack?.charaLoadFinished()
            }
        }
    }

    private fun loadBasic(innerCharaList: MutableList<Chara>) {
        get().getCharaBase()?.forEach {
            val chara = Chara()
            it.setCharaBasic(chara)
            innerCharaList.add(chara)
        }
        nicknames = UserSettings.get().nicknames
        myCharaList = UserSettings.get().loadCharaData()
        myCharaTargetList = UserSettings.get().loadCharaData(suffix = UserSettings.TARGET)
    }

    private fun setCharaMaxData(chara: Chara) {
        this.maxCharaLevel = get().maxCharaLevel - 1
        chara.maxCharaLevel = this.maxCharaLevel
        this.maxCharaContentsLevel = UserSettings.get().contentsMaxLevel
        chara.maxCharaContentsLevel = this.maxCharaContentsLevel

        this.maxCharaRank = get().maxCharaRank
        chara.maxCharaRank = this.maxCharaRank
        this.maxCharaContentsRank = UserSettings.get().contentsMaxRank
        chara.maxCharaContentsRank = this.maxCharaContentsRank

        chara.maxCharaRarity = get().maxUnitRarity(chara.unitId)
        if (this.maxCharaRarity < chara.maxCharaRarity)
            this.maxCharaRarity = chara.maxCharaRarity

        this.maxCharaContentsEquipment = UserSettings.get().contentsMaxEquipment
        chara.maxCharaContentsEquipment = this.maxCharaContentsEquipment

        this.maxUniqueEquipmentLevel = get().maxUniqueEquipmentLevel
        chara.maxUniqueEquipmentLevel = this.maxUniqueEquipmentLevel

        maxEnemyLevel = get().maxEnemyLevel
    }

    // personalization function
    fun setCharaDisplay(chara: Chara) {
        myCharaList?.find { it.charaId == chara.charaId }?.let { myChara ->
            chara.displayLevel = myChara.level
            chara.displayRank = myChara.rank
            chara.displayRarity = myChara.rarity
            chara.displayEquipments[chara.displayRank] = myChara.equipment
            chara.displayUniqueEquipmentLevel = myChara.uniqueEquipment
            chara.isBookmarked = true
            chara.isBookmarkLocked = myChara.isBookmarkLocked

            myCharaTargetList?.find { it.charaId == chara.charaId }?.let { target ->
                chara.targetRank = target.rank
                chara.targetEquipments = target.equipment
                chara.targetEquipmentNumber = target.equipment.count { it > 0 }
                // Target rarity? target unique equipments?
            }
        } ?: run {
            chara.displayLevel = chara.maxCharaContentsLevel
            chara.displayRank = chara.maxCharaContentsRank
            chara.displayRarity = chara.maxCharaRarity
            chara.displayEquipments[chara.displayRank] = chara.getEquipmentList(chara.maxCharaContentsEquipment)

            if (chara.uniqueEquipment?.equals(Equipment.getNull)!!) {
                chara.displayUniqueEquipmentLevel = 0
            } else {
                chara.displayUniqueEquipmentLevel = chara.maxUniqueEquipmentLevel
            }
        }

        chara.setCharaProperty()
    }

    private fun setCharaRarity(chara: Chara) {
        get().getUnitRarityList(chara.unitId)?.forEach {
            if (it.rarity == 6) {
                chara.maxCharaRarity = 6
                chara.rarity = 6
                chara.maxCharaLoveLevel = 12
                chara.iconUrl = Statics.ICON_URL.format(chara.prefabId + 60)
                chara.imageUrl = Statics.IMAGE_URL.format(chara.prefabId + 60)
            }
            chara.rarityProperty[it.rarity] = it.property
            chara.rarityPropertyGrowth[it.rarity] = it.propertyGrowth
        }
    }

    private fun setCharaStoryStatus(chara: Chara) {
        var property = Property()
        var charaId = 0
        //var loveLevel = 1
        chara.storyProperty[1] = property
        get().getCharaStoryStatus(chara.charaId)?.forEach {
            if (charaId == it.chara_id_1) {
                /* //fill the love_level gap, but maybe it does not need?
                for (i in loveLevel..it.love_level) {
                    if (charaId == chara.charaId)
                        chara.storyProperty[it.love_level] = property
                    else {
                        if (chara.otherStoryProperty[charaId].isNullOrEmpty()) {
                            chara.otherStoryProperty[charaId] = mutableMapOf()
                        }
                        chara.otherStoryProperty[charaId]?.set(it.love_level, property)
                    }
                }*/
                // current love_level property
                property = property.plus(it.getCharaStoryStatus(chara))
            }
            else {
                // reset for new chara
                charaId = it.chara_id_1
                property = Property()
                property = property.plus(it.getCharaStoryStatus(chara))
            }
            //loveLevel = it.love_level

            // add love_level
            if (charaId == chara.charaId)
                chara.storyProperty[it.love_level] = property
            else {
                if (chara.otherStoryProperty[charaId].isNullOrEmpty()) {
                    chara.otherStoryProperty[charaId] = mutableMapOf()
                    chara.otherStoryProperty[charaId]?.set(1, Property())
                }
                chara.otherStoryProperty[charaId]?.set(it.love_level, property)
            }
        }
        chara.displayLoveLevel = when (chara.rarity) {
            6 -> 12
            in 1..2 -> 4
            else -> 8
        }
        chara.otherStoryProperty.entries.forEach { entry ->
            val possibleChara = myCharaList?.find { it.charaId == entry.key }
            if (possibleChara != null) {
                chara.otherLoveLevel[entry.key] = when (possibleChara.rarity) {
                    6 -> 12
                    in 1..2 -> 4
                    else -> 8
                }
            }
            else {
                if (myCharaList.isNullOrEmpty()) {
                    val rarity = charaList.value?.let { list ->
                        list.find { it.charaId == entry.key }?.displayRarity
                    } ?: 5
                    chara.otherLoveLevel[entry.key] = when (rarity) {
                        6 -> 12
                        in 1..2 -> 4
                        else -> 8
                    }
                }
                else
                    chara.otherLoveLevel[entry.key] = 1
            }
        }
    }

    private fun setCharaPromotionStatus(chara: Chara) {
        val promotionStatus = mutableMapOf<Int, Property>()
        get().getCharaPromotionStatus(chara.unitId)?.forEach {
            promotionStatus[it.promotion_level] = it.promotionStatus
        }
        chara.promotionStatus = promotionStatus
    }

    private fun setCharaEquipments(chara: Chara, equipmentMap: Map<Int, Equipment>) {
        val rankEquipments = mutableMapOf<Int, List<Equipment>>()
        val displayEquipments = mutableMapOf<Int, MutableList<Int>>()
        get().getCharaPromotion(chara.unitId)?.forEach { slots ->
            val equipmentList = mutableListOf<Equipment>()
            val equipmentLevel = mutableListOf<Int>()
            slots.charaSlots.forEach { id ->
                equipmentMap[id]?.let {
                    equipmentList.add(it)
                    equipmentLevel.add(it.maxEnhanceLevel)
                    it.addCharaEquipmentLink(chara.unitId, chara.prefabId, chara.searchAreaWidth, slots.promotion_level)
                }
            }
            rankEquipments[slots.promotion_level] = equipmentList
            displayEquipments[slots.promotion_level] = equipmentLevel
        }
        chara.rankEquipments = rankEquipments
        chara.displayEquipments = displayEquipments
    }

    private fun setUniqueEquipment(chara: Chara) {
        chara.uniqueEquipment = MasterUniqueEquipment().getCharaUniqueEquipment(chara)
    }

    private fun setRarity6Status(chara: Chara) {
        chara.rarity6Status = MasterUnlockRarity6().getCharaUnlockRarity6(chara)
    }

    private fun setUnitSkillData(chara: Chara) {
        get().getUnitSkillData(chara.unitId)?.setCharaSkillList(chara)
    }

    private fun setUnitAttackPattern(chara: Chara) {
        get().getUnitAttackPattern(chara.unitId)?.forEach {
            chara.attackPatternList.add(
                it.attackPattern.setItems(
                    chara.skills,
                    chara.atkType
                )
            )
        }
    }

    fun setUnitNickname(chara: Chara) {
        nicknames!![chara.charaId]?.let { nickname ->
            chara.shortName = nickname.shortNickname
            chara.shortestName = nickname.shortestNickname
        }
    }

    fun mSetSelectedChara(chara: Chara?){
        chara?.apply {
            skills.forEach {
                it.setActionDescriptions(chara.displayLevel, chara.charaProperty)
            }
        }
        this.selectedChara = chara
    }

    fun loadExternalData() {
        nicknames = UserSettings.get().nicknames
        charaList.value?.forEach {
            setUnitNickname(it)
        }
    }

    fun loadCharaMaxData() {
        charaList.value?.forEach {
            setCharaMaxData(it)
            setCharaDisplay(it)
        }
    }

    fun updateChara(chara: Chara) {
        charaList.value?.let { list ->
            val index = list.indexOfFirst { it.charaId == chara.charaId }
            list.set(index, chara)
        }
    }

    var callBack: MasterCharaCallBack? = null
    interface MasterCharaCallBack {
        fun charaLoadFinished()
    }
}