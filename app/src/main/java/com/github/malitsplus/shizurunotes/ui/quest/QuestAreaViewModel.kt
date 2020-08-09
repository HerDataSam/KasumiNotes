package com.github.malitsplus.shizurunotes.ui.quest

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.QuestAreaVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelQuest

class QuestAreaViewModel(
    val sharedQuest: SharedViewModelQuest
) : ViewModel() {
    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            sharedQuest.questAreaList.value?.sortedWith(compareBy({it.startTime}, { it.areaNum }))?.asReversed()?.forEach {
                field.add(QuestAreaVT(it))
            }
            return field
        }
}

interface OnQuestAreaClickListener<T>: OnItemActionListener {
    fun onQuestAreaClicked(item: T)
}