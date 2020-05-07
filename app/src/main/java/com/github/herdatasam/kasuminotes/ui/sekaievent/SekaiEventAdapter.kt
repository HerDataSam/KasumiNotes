package com.github.herdatasam.kasuminotes.ui.sekaievent

import android.view.View
import androidx.navigation.findNavController
import com.github.herdatasam.kasuminotes.R
import com.github.herdatasam.kasuminotes.common.I18N
import com.github.herdatasam.kasuminotes.data.SekaiEvent
import com.github.herdatasam.kasuminotes.databinding.ListItemSekaiEventBinding
import com.github.herdatasam.kasuminotes.ui.base.BaseRecyclerAdapter
import com.github.herdatasam.kasuminotes.ui.shared.SharedViewModelClanBattle

class SekaiEventAdapter (
    private val sharedClanBattle: SharedViewModelClanBattle
) : BaseRecyclerAdapter<SekaiEvent, ListItemSekaiEventBinding>(R.layout.list_item_sekai_event) {

    override fun onBindViewHolder(holder: VH<ListItemSekaiEventBinding>, position: Int) {
        with(holder.binding) {
            val thisSekaiEvent = itemList[position]
            sekaiEvent = thisSekaiEvent
            textSekaiEventDate.text = I18N.getString(R.string.text_sekai_event_date).format(thisSekaiEvent.startTime)
            clickListener = View.OnClickListener {
                sharedClanBattle.mSetSelectedBoss(thisSekaiEvent.SekaiBoss)
                it.findNavController().navigate(
                    SekaiEventFragmentDirections.actionNavSekaiEventToNavClanBattleBossDetails()
                )
            }
            executePendingBindings()
        }
    }
}