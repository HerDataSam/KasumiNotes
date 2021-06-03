package com.github.malitsplus.shizurunotes.ui.drop

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.data.Item
import com.github.malitsplus.shizurunotes.data.Quest
import com.github.malitsplus.shizurunotes.db.RawQuest
import com.github.malitsplus.shizurunotes.db.DBHelper
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelQuest
import com.github.malitsplus.shizurunotes.user.UserSettings
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.measureNanoTime

class DropQuestViewModel(
    private val sharedQuest: SharedViewModelQuest,
    private val itemList: List<Item>?
) : ViewModel() {
    val searchedQuestList = MutableLiveData<MutableList<Any>>(mutableListOf())

    @Suppress("UNCHECKED_CAST")
    fun search() {
        if (itemList.isNullOrEmpty()) {
            questTypeFilter(sharedQuest.questList.value)
            searchedQuestList.value = questTypeFilter(sharedQuest.questList.value) as MutableList<Any>?
        } else {
            thread(start = true) {
                val resultList = mutableListOf<Any>()
                var rawList = mutableListOf<Quest>()
                //var rawMap = mutableMapOf<Quest, Double>()
                val andList = mutableListOf<Quest>()
                val orList = mutableListOf<Quest>()
                val middleList = mutableListOf<Quest>()
                //val questTime = mutableListOf<Double>()

                sharedQuest.questList.value!!.forEach { quest -> //questTypeFilter()
                    itemList.forEach { item ->
                        if (UserSettings.get().contentsMaxArea >= quest.areaId.rem(100) &&
                            quest.contains(item.itemId)) {
                            rawList.add(quest)
                        }
                    }
                    //if (UserSettings.get().contentsMaxArea >= quest.areaId.rem(100)) {
                    //    val elapsed: Long = measureNanoTime {
                    //        val point = quest.calcPoints(itemList)
                    //        if (point > 0)
                    //            rawMap.merge(quest, point, Double::plus)
                    //    }
                    //    questTime.add(elapsed / 1000000000.0)
                    //}
                }
                // TODO: Upgrade quest search by pre-searched list and multiplier
                //for((index, value) in questTime.iterator().withIndex()) {
                //    println("Quest-$index: $value")
                //}
                //val sortedMap: List<Pair<Quest, Double>>
                //val sorting: Long = measureNanoTime {
                //    sortedMap = rawMap.toList().sortedByDescending { (_, value) -> value }
                //}
                //println("Sort: " + sorting / 1000000000.0)

                //var number = 0.0
                //sortedMap.forEach {
                //    if (it.second != number) {
                //        number = it.second
                //        resultList.add(number.toString())
                //    }
                //    resultList.add(it.first)
                //}
                //searchedQuestList.postValue(resultList)

                when(val num = itemList.size) {
                    1 -> {
                        rawList.sortByDescending { it.getOdds(itemList) }
                        questTypeFilter(rawList)
                        searchedQuestList.postValue(questTypeFilter(rawList) as MutableList<Any>)
                    }
                    else -> {
                        rawList = questTypeFilter(rawList).toMutableList()
                        rawList.forEach {
                            if (!andList.contains(it) && !middleList.contains(it)) {
                                when(Collections.frequency(rawList, it)) {
                                    num -> andList.add(it)
                                    1 -> orList.add(it)
                                    else -> middleList.add(it)
                                }
                            }
                        }
                        if (andList.isNotEmpty()) {
                            resultList.add(I18N.getString(R.string.text_condition_and))
                            resultList.addAll(andList.sortedByDescending { it.getOdds(itemList) })
                        }
                        if (middleList.isNotEmpty()) {
                            resultList.add(I18N.getString(R.string.text_condition_andor))
                            resultList.addAll(middleList.sortedByDescending { it.getOdds(itemList) })
                        }
                        if (orList.isNotEmpty()) {
                            resultList.add(I18N.getString(R.string.text_condition_or))
                            resultList.addAll(orList.sortedByDescending { it.getOdds(itemList) })
                        }
                        searchedQuestList.postValue(resultList)
                    }
                }
            }
        }
    }

    private fun questTypeFilter(list: List<Quest>?): List<Quest> {
        if (list == null) {
            return listOf()
        }
        return when {
            sharedQuest.selectedQuestArea != null -> {
                list.filter { it.areaId == sharedQuest.selectedQuestArea?.areaId }
            }
            sharedQuest.includeNormal && !sharedQuest.includeHard -> {
                list.filter { it.questType == Quest.QuestType.Normal }
            }
            !sharedQuest.includeNormal && sharedQuest.includeHard -> {
                list.filter { it.questType == Quest.QuestType.Hard }
            }
            else -> {
                list
            }
        }
    }
}