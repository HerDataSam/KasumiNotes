package com.github.malitsplus.shizurunotes.ui.today

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.CampaignSchedule
import com.github.malitsplus.shizurunotes.data.EventSchedule
import com.github.malitsplus.shizurunotes.data.EventType
import com.github.malitsplus.shizurunotes.ui.base.TodayEventVT
import com.github.malitsplus.shizurunotes.ui.base.TodayTextVT
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

            field.add(TodayTextVT(I18N.getString(R.string.title_today)))
            val today = LocalDateTime.now().minusHours(5).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val schedule = calendarVM.scheduleMap[today]?.sortedByDescending { it.type }

            schedule?.forEach {
                if (it is CampaignSchedule) {
                    it.campaignType
                }
                field.add(TodayEventVT(it))
            }

            field.add(TodayTextVT("이후"))
            val tomorrow = LocalDateTime.now().plusDays(1).minusHours(5).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val futureSchedule = calendarVM.scheduleMap[tomorrow]?.sortedByDescending { it.type }

            futureSchedule?.forEach {
                if (it is CampaignSchedule) {
                    it.campaignType
                }
                field.add(TodayEventVT(it))
            }

            return field
        }
}