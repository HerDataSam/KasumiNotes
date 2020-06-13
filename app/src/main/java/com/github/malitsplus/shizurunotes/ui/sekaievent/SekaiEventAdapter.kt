package com.github.malitsplus.shizurunotes.ui.sekaievent

import android.view.View
import androidx.navigation.findNavController
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.SekaiEvent
import com.github.malitsplus.shizurunotes.databinding.ListItemSekaiEventBinding
import com.github.malitsplus.shizurunotes.ui.base.BaseRecyclerAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle

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