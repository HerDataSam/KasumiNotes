package com.github.malitsplus.shizurunotes.db

import com.github.malitsplus.shizurunotes.data.Quest
import com.github.malitsplus.shizurunotes.data.QuestArea

class MasterQuest {
    val quest: MutableList<Quest>
        get() {
            val questList: MutableList<Quest> = ArrayList()
            DBHelper.get().getQuests()?.forEach {
                questList.add(it.quest)
            }
            return questList
        }

    val questArea: MutableList<QuestArea>
        get() {
            val questAreaList: MutableList<QuestArea> = ArrayList()
            DBHelper.get().getQuestAreas()?.forEach { raw ->
                val questArea = raw.questArea
                //if (questAreaList.find { it.areaNum == questArea.areaNum } == null) {
                questAreaList.add(questArea)
                //}
            }
            return questAreaList
        }
}