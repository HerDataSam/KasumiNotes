package com.github.malitsplus.shizurunotes.ui.gacha

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.GachaSchedule
import com.github.malitsplus.shizurunotes.ui.base.CharaIconUrlVT
import com.github.malitsplus.shizurunotes.ui.base.GachaDetailsVT
import com.github.malitsplus.shizurunotes.ui.base.TextTagVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.calendar.CalendarViewModel

class GachaExchangeListViewModel (
    sharedCalendar: CalendarViewModel
) : ViewModel() {
    var gachaEvent: GachaSchedule = sharedCalendar.selectedGacha!!

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            field.add(GachaDetailsVT(gachaEvent))
            field.add(TextTagVT(I18N.getString(R.string.gacha_exchange_list)))
            gachaEvent.exchangeLineup.forEach { lineup ->
                field.add(CharaIconUrlVT(lineup.iconUrl))
            }
            return field
        }

}