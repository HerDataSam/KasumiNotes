package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class QuestArea(
    val areaId: Int,
    val areaName: String,
    val startTime: LocalDateTime
) {
    val areaNum: Int = areaId.rem(1000)

    val startTimeStr: String
        get() {
            val formatter = DateTimeFormatter.ofPattern(I18N.getString(R.string.text_year_month_day_format))
            if (startTime.isAfter(LocalDateTime.now())) {
                return I18N.getString(R.string.text_open, startTime.format(formatter))
            }
            return ""
        }

    val questType: Quest.QuestType by lazy {
        when (areaId) {
            in 11000..11999 -> Quest.QuestType.Normal
            in 12000..12999 -> Quest.QuestType.Hard
            in 13000..13999 -> Quest.QuestType.VeryHard
            else -> Quest.QuestType.Others
        }
    }
}