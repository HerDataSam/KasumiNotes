package com.github.herdatasam.kasuminotes.ui.calendar

import com.github.herdatasam.kasuminotes.R
import com.github.herdatasam.kasuminotes.data.CampaignSchedule
import com.github.herdatasam.kasuminotes.data.EventSchedule
import com.github.herdatasam.kasuminotes.databinding.ItemScheduleBinding
import com.github.herdatasam.kasuminotes.ui.base.BaseRecyclerAdapter

class DayScheduleAdapter : BaseRecyclerAdapter<EventSchedule, ItemScheduleBinding>(R.layout.item_schedule) {
    override fun onBindViewHolder(holder: VH<ItemScheduleBinding>, position: Int) {
        with(holder.binding) {
            val item = itemList[position]
            schedule = item
            if (item is CampaignSchedule) {
                typeDot.setColorFilter(item.campaignType.shortColor())
            } else {
                typeDot.setColorFilter(item.type.color)
            }
            executePendingBindings()
        }
    }
}