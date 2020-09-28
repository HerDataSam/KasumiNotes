package com.github.malitsplus.shizurunotes.ui.today

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.CampaignSchedule
import com.github.malitsplus.shizurunotes.data.EventSchedule
import com.github.malitsplus.shizurunotes.data.EventType
import com.github.malitsplus.shizurunotes.ui.base.TodayEventVT
import com.github.malitsplus.shizurunotes.ui.base.HintTextVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.calendar.CalendarViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TodayViewModel (
    val calendarVM: CalendarViewModel
) : ViewModel() {

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()

            field.add(HintTextVT(I18N.getString(R.string.today_ongoing)))
            val todayStart =  LocalDateTime.now().minusHours(5).toLocalDate().atStartOfDay().plusHours(5)
            val tomorrowStart = todayStart.plusDays(1)
            val weekAfterStart = todayStart.plusDays(7)

            val todaySchedule = calendarVM.allSchedules.filter {
                it.startTime.isBefore(tomorrowStart)
                        && it.endTime.isAfter(todayStart)
            }.sortedWith (compareBy { it.importance })
            //val schedule = calendarVM.scheduleMap[today]?.sortedBy { it.importance }

            todaySchedule.forEach {
                if (it is CampaignSchedule) {
                    if (it.campaignType.isVisible()) {
                        field.add(TodayEventVT(it))
                    }
                }
                else
                    field.add(TodayEventVT(it))
            }

            field.add(HintTextVT(I18N.getString(R.string.today_upcoming)))
            val futureSchedule = calendarVM.allSchedules.filter {
                it.startTime.isAfter(tomorrowStart.minusMinutes(1))
                    && it.startTime.isBefore(weekAfterStart)
            }.sortedWith (compareBy({ it.startTime }, { it.importance}))

            futureSchedule.forEach {
                if (it is CampaignSchedule) {
                    if (it.campaignType.isVisible()) {
                        field.add(TodayEventVT(it))
                    }
                }
                else
                    field.add(TodayEventVT(it))
            }

            return field
        }
}

interface OnTodayActionListener<T>: OnItemActionListener {
    fun onScheduleClickedListener(item: EventSchedule)
}