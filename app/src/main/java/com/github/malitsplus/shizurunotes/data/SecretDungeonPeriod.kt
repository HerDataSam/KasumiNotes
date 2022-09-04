package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SecretDungeonPeriod(
    val dungeonAreaId: Int,
    val dungeonName: String,
    val description: String,
    val teaserTime: LocalDateTime,
    val startTime: LocalDateTime,
    val countStartTime: LocalDateTime,
    val endTime: LocalDateTime,
    val closeTime: LocalDateTime
) {
    private val pattern = DateTimeFormatter.ofPattern(I18N.getString(R.string.text_year_month_day_format))
    val title: String by lazy {
        I18N.getString(R.string.secret_dungeon_d_s_month, dungeonAreaId % 10, dungeonName, startTime.month.value)
    }
    val isBeforeTeaser: Boolean
        get() {
            return LocalDateTime.now().isBefore(teaserTime)
        }
    val teaserTimeString: String
        get() {
            return I18N.getString(R.string.secret_dungeon_teaser_time, teaserTime.format(pattern))
        }
    val startTimeString: String
        get() {
            return I18N.getString(R.string.secret_dungeon_start_time, startTime.format(pattern))
        }
    val endTimeString: String
        get() {
            return I18N.getString(R.string.secret_dungeon_end_time, endTime.format(pattern))
        }
    val dungeonFloor = mutableListOf<SecretDungeon>()
}