package com.github.malitsplus.shizurunotes.ui.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.data.Minion
import com.github.malitsplus.shizurunotes.data.Property
import com.github.malitsplus.shizurunotes.db.DBHelper.Companion.get
import com.github.malitsplus.shizurunotes.user.UserSettings
import kotlin.concurrent.thread

class SharedViewModelChara : ViewModel() {

    val loadingFlag = MutableLiveData<Boolean>(false)
    val charaList = MutableLiveData<MutableList<Chara>>()

    var maxCharaLevel: Int = 0
    var maxCharaContentsLevel: Int = 0
    var maxCharaRank: Int = 0
    var maxCharaContentsRank: Int = 0
    var maxCharaContentsEquipment: Int = 0
    var maxUniqueEquipmentLevel: Int = 0

    var selectedChara: Chara? = null
    var selectedMinion: MutableList<Minion>? = null
    var backFlag = false

    var rankComparisonFrom: Int = 0
    var rankComparisonTo: Int = 0

    var equipmentComparisonFrom: Int = 0
    var equipmentComparisonTo: Int = 0

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
                    setUnitSkillData(it)
                    setUnitAttackPattern(it)
                    it.setCharaPropertyMax()
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

        this.maxCharaContentsEquipment = UserSettings.get().contentsMaxEquipment
        chara.maxCharaContentsEquipment = this.maxCharaContentsEquipment

        this.maxUniqueEquipmentLevel = get().maxUniqueEquipmentLevel
        chara.maxUniqueEquipmentLevel = this.maxUniqueEquipmentLevel
    }

    private fun setCharaRarity(chara: Chara) {
        val rarityProperty = mutableMapOf<Int, Property>()
        val rarityPropertyGrowth = mutableMapOf<Int, Property>()
        get().getUnitRarityList(chara.unitId)?.forEach {
            rarityProperty[it.rarity] = it.property
            rarityPropertyGrowth[it.rarity] = it.propertyGrowth
        }
        chara.rarityProperty = rarityProperty
        chara.rarityPropertyGrowth = rarityPropertyGrowth
    }

    private fun setCharaStoryStatus(chara: Chara) {
        chara.storyProperty = Property().apply {
            get().getCharaStoryStatus(chara.charaId)?.forEach {
                this.plusEqual(it.getCharaStoryStatus(chara))
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
        get().getCharaPromotion(chara.unitId)?.forEach { slots ->
            val equipmentList = mutableListOf<Equipment>()
            slots.charaSlots.forEach { id ->
                equipmentMap[id]?.let {
                    equipmentList.add(it)
                }
            }
            rankEquipments[slots.promotion_level] = equipmentList
        }
        chara.rankEquipments = rankEquipments
    }

    private fun setUniqueEquipment(chara: Chara) {
        get().getUniqueEquipment(chara.unitId)?.setCharaUniqueEquipment(chara)
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

    fun mSetSelectedChara(chara: Chara?){
        chara?.apply {
            skills.forEach {
                it.setActionDescriptions(chara.displayLevel, chara.charaProperty)
            }
        }
        this.selectedChara = chara
    }

    fun loadCharaMaxData() {
        charaList.value?.forEach {
            setCharaMaxData(it)
        }
    }

    var callBack: MasterCharaCallBack? = null
    interface MasterCharaCallBack {
        fun charaLoadFinished()
    }
}