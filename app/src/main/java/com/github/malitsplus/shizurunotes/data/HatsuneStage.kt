package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.Statics
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HatsuneStage(
    val eventId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val title: String
) {
    val battleWaveGroupMap = mutableMapOf<String, WaveGroup>()
//    val spBattleWaveGroupMap = mutableMapOf<String, WaveGroup>()
    val pattern = DateTimeFormatter.ofPattern(I18N.getString(R.string.text_year_month_day_format))
    val durationString: String
        get() {
            return startTime.format(pattern) + "  ~  " + endTime.format(pattern)
        }
    val startTimeString: String
        get() {
            return I18N.getString(R.string.hatsune_start_time, startTime.format(pattern))
        }
    val endTimeString: String
        get() {
            return I18N.getString(R.string.hatsune_end_time, endTime.format(pattern))
        }
    val imageUrl = Statics.EVENT_BANNER_URL.format(eventId)
}