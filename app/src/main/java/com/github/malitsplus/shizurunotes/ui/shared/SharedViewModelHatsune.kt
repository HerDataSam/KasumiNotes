package com.github.malitsplus.shizurunotes.ui.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.HatsuneStage
import com.github.malitsplus.shizurunotes.db.MasterHatsune
import kotlin.concurrent.thread

class SharedViewModelHatsune : ViewModel() {

    val hatsuneStageList = MutableLiveData<List<HatsuneStage>>()
    var selectedHatsune = MutableLiveData<HatsuneStage>(null)

    fun loadData() {
        if (hatsuneStageList.value.isNullOrEmpty()) {
            thread(start = true) {
                hatsuneStageList.postValue(MasterHatsune().getHatsune())
            }
        }
    }

    fun loadHatsuneWaveData() {
        if (selectedHatsune.value?.battleWaveGroupMap.isNullOrEmpty()) {
            thread(start = true) {
                selectedHatsune.postValue(MasterHatsune().getHatsuneBattleWave(selectedHatsune.value!!))
            }
        }
    }
}