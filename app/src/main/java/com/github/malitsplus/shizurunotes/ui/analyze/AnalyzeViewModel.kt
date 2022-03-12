package com.github.malitsplus.shizurunotes.ui.analyze

import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.data.CharaSim
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.data.Property
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.utils.UnitUtils
import com.github.malitsplus.shizurunotes.utils.Utils

class AnalyzeViewModel(
    val sharedChara: SharedViewModelChara
) : ViewModel(), OnAnalyzeActionListener {

    var chara = MutableLiveData<Chara>()
    lateinit var charaSim: CharaSim
    //var property4Analyze = MutableLiveData<Property>()
    var rarity = 1
    var rank = 1
    var level = 1
    //var loveLevel = 8
    //var combatPower = 0
    var enemyProperty = Property()
    var enemyLevel = 1
    var enemyAccuracy = 50
    var enemyDodge = 0
    var rankEquipments = mapOf<Int, List<Equipment>>()
    //var displayEquipment = mutableListOf<Int>()//mutableMapOf<Int, MutableList<Int>>()
    var uniqueEquipment = Equipment.getNull
    val rankList = mutableListOf<Int>()
    val levelList = mutableListOf<Int>()

    private val equipmentIds = listOf(
        R.id.rank_equipment_details_0, R.id.rank_equipment_details_1, R.id.rank_equipment_details_2,
        R.id.rank_equipment_details_3, R.id.rank_equipment_details_4, R.id.rank_equipment_details_5)
    private val uniqueEquipmentID = R.id.unique_equipment_details

    init {
        chara.value = sharedChara.selectedChara
        chara.value?.let {
            //charaSim = CharaSim(it)
            //property4Analyze.value = Property().plusEqual(it.charaProperty)
            rarity = it.displaySetting.rarity
            rank = it.displaySetting.rank
            for (i in it.maxCharaContentsRank downTo 2) {
                rankList.add(i)
            }
            level = it.displaySetting.level
            enemyLevel = it.displaySetting.level
            for (i in it.maxCharaContentsLevel downTo 1) {
                levelList.add(i)
            }
            rankEquipments = it.rankEquipments
            //displayEquipment = it.displayEquipments[it.displayRank]!!
            uniqueEquipment = it.uniqueEquipment ?: Equipment.getNull
        }
    }

    val enemyLevelText: String
        get() {
            return I18N.getString(R.string.enemy_level_d, enemyLevel)
        }

    val enemyAccuracyText: String
        get() {
            return I18N.getString(R.string.enemy_accuracy_d, enemyAccuracy)
        }

    val enemyDodgeText: String
        get() {
            return I18N.getString(R.string.enemy_dodge_d, enemyDodge)
        }

    val criticalRateText: String
        get() {
            val criticalRate = chara.value?.let {
                val critical = if (it.atkType == 1) {8//
                    it.charaProperty.getPhysicalCritical()
                } else {
                    it.charaProperty.getMagicCritical()
                }
                UnitUtils.getCriticalRate(critical, it.displaySetting.level, enemyLevel) * 100.0
            } ?: 0.0
            return I18N.getString(R.string.percent_modifier_s, Utils.getOneDecimalPlaces(criticalRate))
        }

    val hpAbsorbRateText: String
        get() {
            val hpAbsorbRate = chara.value?.let {
                UnitUtils.getHpAbsorbRate(it.charaProperty.getLifeSteal(), enemyLevel) * 100.0
            } ?: 0.0
            return I18N.getString(R.string.percent_modifier_s, Utils.getOneDecimalPlaces(hpAbsorbRate))
        }

    val physicalDamageCutText: String
        get() {
            return chara.value?.let {
                I18N.getString(R.string.percent_modifier_s, Utils.getOneDecimalPlaces(it.charaProperty.physicalDamageCut * 100.0))
            } ?: "0%"
        }

    val magicalDamageCutText: String
        get() {
            return chara.value?.let {
                I18N.getString(R.string.percent_modifier_s, Utils.getOneDecimalPlaces(it.charaProperty.magicalDamageCut * 100.0))
            } ?: "0%"
        }

    val hpRecoveryRateText: String
        get() {
            return chara.value?.let {
                I18N.getString(R.string.percent_modifier_s, Utils.getOneDecimalPlaces(it.charaProperty.hpRecovery * 100.0))
            } ?: "100%"
        }

    val tpUpRateText: String
        get() {
            return chara.value?.let {
                I18N.getString(R.string.percent_modifier_s, Utils.getOneDecimalPlaces(it.charaProperty.tpUpRate * 100.0))
            } ?: "100%"
        }

    val accuracyRateText: String
        get() {
            return if (chara.value?.atkType == 2) {
                "100%"
            } else {
                chara.value?.let {
                    val rate = UnitUtils.getAccuracyRate(it.charaProperty.getAccuracy(), enemyDodge) * 100.0
                    I18N.getString(R.string.percent_modifier_s, Utils.getOneDecimalPlaces(rate))
                } ?: "100%"
            }
        }

    val dodgeRateText: String
        get() {
            return chara.value?.let {
                val rate = UnitUtils.getDodgeRate(enemyAccuracy, it.charaProperty.getDodge()) * 100.0
                I18N.getString(R.string.percent_modifier_s, Utils.getOneDecimalPlaces(rate))
            } ?: "0%"
        }

    val tpPerActionText: String
        get() {
            return chara.value?.let {
                val tp = UnitUtils.getActualTpRecoveryValue(UnitUtils.TpBonusType.Action, it.charaProperty.tpUpRate)
                Utils.getTwoDecimalPlaces(tp)
            } ?: UnitUtils.TpBonusType.Action.getBaseValue().toString()
        }

    val tpRemainText: String
        get() {
            return chara.value?.charaProperty?.tpRemain.toString()
        }

    fun changeRank(rank: Int){
        val tempChara = chara.value?.shallowCopy()
        tempChara?.apply {
            setCharaProperty(rank = rank)
            skills.forEach {
                it.setActionDescriptions(tempChara.displaySetting.level, charaProperty)
            }
        }
        chara.value = tempChara!!
    }

    fun changeLevel(level: Int) {
        val tempChara = chara.value?.shallowCopy()
        tempChara?.apply {
            setCharaProperty(level = level)
            saveBookmarkedChara()
            skills.forEach {
                it.setActionDescriptions(tempChara.displaySetting.level, charaProperty, enemyProperty)
            }
        }
        chara.value = tempChara!!
    }

    fun changeRarity(rarity: Int) {
        val tempChara = chara.value?.shallowCopy()
        tempChara?.apply {
            setCharaProperty(rarity = rarity)
            saveBookmarkedChara()
            skills.forEach {
                it.setActionDescriptions(tempChara.displaySetting.level, charaProperty)
            }
        }
        chara.value = tempChara!!
    }

    fun changeEquipment(equipment: Int) {
        val tempChara = chara.value?.shallowCopy()
        tempChara?.apply {
            val displayEquipment = this.displaySetting.equipment
            if (displayEquipment[equipment] < 0) {
                displayEquipment[equipment] =
                    rankEquipments[rank]?.get(equipment)?.maxEnhanceLevel ?: 5
            } else {
                displayEquipment[equipment] -= 1
            }

            setCharaProperty(equipmentEnhanceList = displayEquipment)
            skills.forEach {
                it.setActionDescriptions(this.displaySetting.level, charaProperty)
            }
        }
        chara.value = tempChara!!
    }

    fun changeUniqueEquipment(level: Int) {
        val tempChara = chara.value?.shallowCopy()
        tempChara?.apply {
            val uniqueEquipmentLevel = if (displaySetting.uniqueEquipment < 0 && level < 0)
                -displaySetting.uniqueEquipment
            else
                level
            setCharaProperty(uniqueEquipmentLevel = uniqueEquipmentLevel)
            saveBookmarkedChara()
            skills.forEach {
                it.setActionDescriptions(tempChara.displaySetting.level, charaProperty)
            }
        }
        chara.value = tempChara!!
    }

    override fun onClick(view: View?) {
        if (view != null) {
            if (equipmentIds.contains(view.id)) {
                changeEquipment(equipmentIds.indexOf(view.id))
            }
        }
    }

    override fun onItemClicked(position: Int) {
    }

    fun updateChara() {
        sharedChara.updateChara(chara.value!!)
        sharedChara.mSetSelectedChara(chara.value!!)
    }

    /*
    fun findCharaSim(guessCP: Int) {
        val tempChara = chara.value?.shallowCopy()
        var number = 0
        var check = false
        val simpleList = listOf(-1, 0, 3, 5)
        tempChara?.apply {
            for (e1 in simpleList) {
                for (e2 in simpleList) {
                    for (e3 in simpleList) {
                        for (e4 in simpleList) {
                            for (e5 in simpleList) {
                                for (e6 in simpleList) {
                                    if (e1 == 5 && e2 == 5 && e3 == 5 && e4 == 5 && e5 == 5 && e6 == 5)
                                        check = true
                                    if (guessCP != combatGuess(equipmentEnhanceList = listOf(e1, e4, e2, e5, e3, e6)))
                                        continue
                                    else {
                                        number += 1
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        number
    }
     */
}

interface OnAnalyzeActionListener : OnItemActionListener,
    View.OnClickListener {
}