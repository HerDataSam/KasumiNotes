package com.github.malitsplus.shizurunotes.ui.skillsearch

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.data.action.AuraAction
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class SkillActionClassDetailsViewModel (
    val sharedChara: SharedViewModelChara
): ViewModel() {

    val charaList = mutableListOf<Chara>()
        get() {
            field.clear()

            if (sharedChara.selectedActionType == 0)
                return field

            sharedChara.charaList.value?.forEach {
                if (it.skills.fold(false, { accSkill, skill ->
                    accSkill || skill.actions.fold( false, { accAction, action ->
                        accAction ||
                                (action.actionType == sharedChara.selectedActionType
                                        && filteredByDetails(action.actionDetail1))
                    })
                }))
                    field.add(it)
            }

            return field
        }

    private fun filteredByDetails(details: Int): Boolean {
        if (sharedChara.selectedActionDetails == -1)
            return true

        return when (sharedChara.selectedActionType) {
            10 -> sharedChara.selectedActionDetails == details
            else -> true
        }
    }

    val detailedFilter = mutableListOf<Int>()
        get() {
            field.clear()

            when (sharedChara.selectedActionType) {
                10 -> return field
                else -> return field
            }
        }
}