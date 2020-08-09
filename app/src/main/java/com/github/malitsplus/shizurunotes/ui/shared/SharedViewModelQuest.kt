package com.github.malitsplus.shizurunotes.ui.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Quest
import com.github.malitsplus.shizurunotes.data.QuestArea
import com.github.malitsplus.shizurunotes.db.MasterQuest
import kotlin.concurrent.thread

class SharedViewModelQuest : ViewModel() {
    val questAreaList = MutableLiveData<List<QuestArea>>()
    val questList = MutableLiveData<List<Quest>>()
    val loadingFlag = MutableLiveData<Boolean>(false)
    var selectedQuestArea: QuestArea? = null
    var includeNormal = false
    var includeHard = false

    /***
     * 从数据库读取所有任务数据。
     */
    fun loadData() {
        if (questList.value.isNullOrEmpty()) {
            loadingFlag.value = true
            thread(start = true) {
                questList.postValue(MasterQuest().quest)
                loadingFlag.postValue(false)
            }
        }
    }

    fun loadAreaData() {
        if (questAreaList.value.isNullOrEmpty()) {
            loadingFlag.value = true
            thread(start = true) {
                questAreaList.postValue(MasterQuest().questArea)
                loadingFlag.postValue(false)
            }
        }
    }
}