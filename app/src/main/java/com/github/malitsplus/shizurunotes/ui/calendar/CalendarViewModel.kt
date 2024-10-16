package com.github.malitsplus.shizurunotes.ui.calendar

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.CampaignSchedule
import com.github.malitsplus.shizurunotes.data.EventSchedule
import com.github.malitsplus.shizurunotes.data.EventType
import com.github.malitsplus.shizurunotes.data.GachaSchedule
import com.github.malitsplus.shizurunotes.db.MasterSchedule
import com.github.malitsplus.shizurunotes.utils.Utils
import com.haibin.calendarview.Calendar
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CalendarViewModel : ViewModel() {
    var allSchedules = mutableListOf<EventSchedule>()
    val scheduleMap = mutableMapOf<String, MutableList<EventSchedule>>()
    val calendarMap = mutableMapOf<String, Calendar>()
    var selectedDay: String? = null
    var selectedGacha: GachaSchedule? = null

    private val maxDisplayNum: Int = when (Utils.getScreenRatio()) {
        in 0.0..1.7776 -> 5
        in 1.7776..1.9072 -> 6
        in 1.9072..2.0368 -> 7
        in 2.0368..2.1666 -> 8
        else -> 99
    }

    fun initData() {
        if (calendarMap.isNotEmpty()) return

        allSchedules = MasterSchedule().getSchedule(null)
        allSchedules.forEach {
            val thisStartDate = LocalDate.of(it.startTime.year, it.startTime.month, it.startTime.dayOfMonth)
            var thisEndDate = LocalDate.of(it.endTime.year, it.endTime.month, it.endTime.dayOfMonth)
            if (it.endTime.hour < 5) {
                thisEndDate = thisEndDate.plusDays(-1)
            }
            addToMap(thisStartDate, thisEndDate, it)
        }
    }

    private fun addToMap(startDate: LocalDate, endDate: LocalDate, it: EventSchedule) {
        val duration = ChronoUnit.DAYS.between(startDate, endDate)
        for (diff in 0..duration) {
            val thisDate = startDate.plusDays(diff)
            if (!thisDate.isAfter(endDate)) {
                val datePattern = thisDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                if (it is CampaignSchedule) {
                    if (it.campaignType.isVisible()) {
                        addSchemeCalendar(datePattern, thisDate.year, thisDate.monthValue, thisDate.dayOfMonth, it.colorResource, it.shortTitle, it.campaignType.order(), it.startTime, it.endTime)
                    }
                } else {
                    if (it.type != EventType.PickUp) // do not add pickup schedule yet
                        addSchemeCalendar(datePattern, thisDate.year, thisDate.monthValue, thisDate.dayOfMonth, it.colorResource, it.type.description, it.type.order, it.startTime, it.endTime)
                }
                if (scheduleMap[datePattern] == null) {
                    scheduleMap[datePattern] = mutableListOf()
                }
                scheduleMap[datePattern]?.add(it)
            } else {
                break
            }
        }
    }

    private fun addSchemeCalendar(
        datePattern: String,
        year: Int,
        month: Int,
        day: Int,
        color: Int,
        text: String,
        order: Int,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        if (calendarMap[datePattern] == null) {
            calendarMap[datePattern] = Calendar().apply {
                this.year = year
                this.month = month
                this.day = day
                schemeColor = color
                scheme = text
                addScheme(order, color, text, startTime, endTime)
            }
        } else {
            calendarMap[datePattern]?.let {
                if (it.schemes.size < maxDisplayNum) {
                    it.addScheme(order, color, text, startTime, endTime)
                }
            }
        }
    }
}