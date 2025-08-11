package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.Statics
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TowerArea(
    val areaId: Int,
    val minFloorNum: Int,
    val maxFloorNum: Int,
    val cloisterId: Int,
    val startTime: LocalDateTime
) {
    val minFloorId = 710000000 + areaId * 100000 + minFloorNum * 10 + 1
    val maxFloorId = 710000000 + areaId * 100000 + maxFloorNum * 10 + 1
    val exQuestId = 720000000 + areaId * 100000 + maxFloorNum * 10 + 1
    val minWaveId = 710000000 + minFloorNum * 100 + 1
    val maxWaveId = 710000000 + maxFloorNum * 100 + 100

    val towerWaveGroupMap = mutableMapOf<String, WaveGroup>()
    val towerString = I18N.getString(R.string.tower_floor_from_d1_to_d2, minFloorNum, maxFloorNum)
    var bossId = 0

    val bossUrl: String
        get() = Statics.ICON_URL.format(bossId)

    val startTimeStr: String
        get() {
            val formatter = DateTimeFormatter.ofPattern(I18N.getString(R.string.text_year_month_day_format))
            if (startTime.isAfter(LocalDateTime.now())) {
                return I18N.getString(R.string.text_open, startTime.format(formatter))
            }
            return ""
        }
}