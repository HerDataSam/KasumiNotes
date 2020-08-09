package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.Statics

class RewardData(
    val rewardType: Int,
    val rewardId: Int,
    val rewardNum: Int,
    val odds: Int
) {
    val rewardIcon: String = when (rewardType) {
        2 -> Statics.ITEM_ICON_URL.format(rewardId)
        else -> Statics.EQUIPMENT_ICON_URL.format(rewardId)
    }

    val oddsString: String = I18N.getString(R.string.percent_modifier).format(odds)

}