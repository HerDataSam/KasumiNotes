package com.github.malitsplus.shizurunotes.ui.mychara

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.common.Statics
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.ui.base.CharaTargetVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class MyCharaTargetSettingViewModel (
    val sharedChara: SharedViewModelChara
) : ViewModel() {
    var sampleChara = Chara()

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            field.add(CharaTargetVT(sampleChara))
            return field
        }

    init {
        sampleChara.apply {
            targetSetting.rank = 1
            targetSetting.equipmentNumber = 0
            targetSetting.equipment = mutableListOf(0, 0, 0, 0, 0, 0)
            displaySetting.rank = 99
            displaySetting.equipment = mutableListOf(5, 5, 5, 5, 5, 5)
            maxCharaContentsRank = sharedChara.maxCharaContentsRank
            maxCharaContentsEquipment = sharedChara.maxCharaContentsEquipment
            isBookmarked = false
            isBookmarkLocked = false
            iconUrl = Statics.ITEM_ICON_URL.format(31000)
        }
    }
}