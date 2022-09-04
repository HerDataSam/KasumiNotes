package com.github.malitsplus.shizurunotes.ui.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.*
import com.github.malitsplus.shizurunotes.db.DBHelper
import kotlin.concurrent.thread

class SharedViewModelClanBattle : ViewModel() {

    val periodList = MutableLiveData<MutableList<ClanBattlePeriod>>()
    val loadingFlag = MutableLiveData(false)
    var selectedPeriod = MutableLiveData<ClanBattlePeriod>(null)
    var selectedEnemyList: List<Enemy>? = null
    var selectedMinion: MutableList<Enemy>? = null
    var selectedEnemyTitle: String = ""

    var dungeonList = mutableListOf<Dungeon>()
    var sekaiEventList = mutableListOf<SekaiEvent>()
    var specialBattleList = mutableListOf<SpecialBattle>()

    val secretDungeonList = MutableLiveData<List<SecretDungeonPeriod>>()
    var selectedSecretDungeon = MutableLiveData<SecretDungeonPeriod>(null)

    /***
     * 从数据库读取所有会战数据。
     * 此方法应该且仅应该在程序初始化时或数据库更新完成后使用。
     */
    fun loadData(){
        if (periodList.value.isNullOrEmpty()) {
            loadingFlag.value = true
            thread(start = true){
                val innerPeriodList = mutableListOf<ClanBattlePeriod>()
                DBHelper.get().getClanBattlePeriod()?.forEach {
                    innerPeriodList.add(it.transToClanBattlePeriod())
                }
                periodList.postValue(innerPeriodList)
                loadingFlag.postValue(false)
            }
        }
    }

    fun loadBossData() {
        selectedPeriod.value?.let{ period ->
            period.phaseList.let { phaseList ->
                if (phaseList[0].bossList.isEmpty()) {
                    loadingFlag.value = true
                    thread(start = true) {
                        period.phaseList.forEach {
                            ;
                        }
                        selectedPeriod.postValue(period)
                        loadingFlag.postValue(false)
                    }
                }
            }
        }
    }

    fun loadDungeon(){
        if (dungeonList.isEmpty()){
            thread(start = true){
                loadingFlag.postValue(true)
                DBHelper.get().getDungeons()?.forEach {
                    dungeonList.add(it.dungeon)
                }
                loadingFlag.postValue(false)
            }
        }
    }

    fun loadSecretDungeon(){
        if (secretDungeonList.value.isNullOrEmpty()){
            thread(start = true){
                loadingFlag.postValue(true)
                val dungeonList = mutableListOf<SecretDungeonPeriod>()
                DBHelper.get().getSecretDungeonPeriods()?.forEach {
                    dungeonList.add(it.secretDungeonPeriod)
                }
                secretDungeonList.postValue(dungeonList)
                loadingFlag.postValue(false)
            }
        }
    }

    fun loadSecretDungeonWaves(){
        if (selectedSecretDungeon.value != null && selectedSecretDungeon.value!!.dungeonFloor.isEmpty()){
            thread(start = true){
                loadingFlag.postValue(true)
                val secretDungeon = selectedSecretDungeon.value!!
                DBHelper.get().getSecretDungeons(secretDungeon.dungeonAreaId)?.forEach {
                    secretDungeon.dungeonFloor.add(it.secretDungeon)
                }
                selectedSecretDungeon.postValue(secretDungeon)
                loadingFlag.postValue(false)
            }
        }
    }

    fun loadSekaiEvent() {
        if (sekaiEventList.isEmpty()) {
            thread(start = true) {
                loadingFlag.postValue(true)
                DBHelper.get().getSekaiEvents()?.forEach {
                    sekaiEventList.add(it.sekaiEvent)
                }
                loadingFlag.postValue(false)
            }
        }
    }

    fun loadSpecialEvent() {
        if (specialBattleList.isNullOrEmpty()) {
            thread(start = true) {
                loadingFlag.postValue(true)
                val count = DBHelper.get().getSpecialEventCount()
                if (count >= 1) {
                    DBHelper.get().getKaiserEvent()?.forEach {
                        specialBattleList.add(it.kaiserBattle)
                    }
                    DBHelper.get().getKaiserSpecial()?.forEach {
                        specialBattleList.add(it.kaiserBattle)
                    }
                }
                if (count >= 2) {
                    DBHelper.get().getLegionEvent()?.forEach {
                        specialBattleList.add(it.legionBattle)
                    }
                    DBHelper.get().getLegionSpecial()?.forEach {
                        specialBattleList.add(it.legionBattle)
                    }
                }
                loadingFlag.postValue(false)
            }
        }
    }

    fun mSetSelectedBoss(enemy: Enemy){
        if (enemy.isMultiTarget) {
            enemy.skills.forEach {
                //多目标Boss技能值暂时仅供参考，非准确值
                it.setActionDescriptions(it.enemySkillLevel, enemy.children[0].property)
            }
        } else {
            enemy.skills.forEach {
                it.setActionDescriptions(it.enemySkillLevel, enemy.property)
            }
        }
        this.selectedEnemyList = listOf(enemy)
    }

    fun mSetSelectedBoss(enemyList: List<Enemy>, specifiedTitle: String = ""){
        enemyList.forEach { enemy ->
            if (enemy.isMultiTarget) {
                enemy.skills.forEach {
                    //多目标Boss技能值暂时仅供参考，非准确值
                    it.setActionDescriptions(it.enemySkillLevel, enemy.children[0].property)
                }
            } else {
                enemy.skills.forEach {
                    it.setActionDescriptions(it.enemySkillLevel, enemy.property)
                }
            }
        }
        this.selectedEnemyList = enemyList
        this.selectedEnemyTitle = specifiedTitle
    }
}
