package com.github.malitsplus.shizurunotes.ui.gacha

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.EventType
import com.github.malitsplus.shizurunotes.data.GachaSchedule
import com.github.malitsplus.shizurunotes.ui.base.GachaListVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.calendar.CalendarViewModel
import java.time.LocalDateTime

class GachaListViewModel (
    private val sharedCalendar: CalendarViewModel
) : ViewModel() {
    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            sharedCalendar.allSchedules
                .filter { it.type == EventType.PickUp && it.endTime.isAfter(LocalDateTime.now()) }
                .sortedBy { it.startTime }
                .forEach {
                    field.add(GachaListVT(it as GachaSchedule))
                }
            return field
        }
}

interface OnGachaClickListener<T>: OnItemActionListener {
    fun onGachaClickedListener(item: GachaSchedule)
}