package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.common.Statics
import java.util.Locale

class GachaExchangeLineup(
    val id: Int,
    val exchangeId: Int,
    val unitId: Int,
    val gachaBonusId: Int
) {
    val iconUrl = String.format(Locale.US, Statics.ICON_URL, unitId + 30)
}