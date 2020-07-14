package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.common.Statics
import java.util.*

class CharaEquipmentLink (
    val unitId: Int,
    val prefabId: Int,
    val searchAreaWidth: Int,
    val promotionLevel: Int
) {
    val iconUrl = String.format(Locale.US, Statics.ICON_URL, prefabId + 30)
}