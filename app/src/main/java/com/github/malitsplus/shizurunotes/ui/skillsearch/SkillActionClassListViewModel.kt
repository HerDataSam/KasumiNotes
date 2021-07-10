package com.github.malitsplus.shizurunotes.ui.skillsearch

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.action.ActionParameter
import com.github.malitsplus.shizurunotes.db.DBHelper
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.SkillClassVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType

class SkillActionClassListViewModel : ViewModel() {

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            val skillClasses = DBHelper.get().getSkillActionClasses()

            skillClasses?.forEach {
                field.add(SkillClassVT(Pair(it.action_type, ActionParameter.description(it.action_type))))
            }
            return field
        }
}

interface OnSkillActionClickListener<T>: OnItemActionListener {
    fun onSkillActionClicked(item: T)
}