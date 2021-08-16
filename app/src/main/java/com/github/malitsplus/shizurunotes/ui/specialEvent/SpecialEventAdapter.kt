package com.github.malitsplus.shizurunotes.ui.specialEvent

import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.Statics
import com.github.malitsplus.shizurunotes.data.SpecialBattle
import com.github.malitsplus.shizurunotes.databinding.ListItemSpecialEventBinding
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle
import java.util.*

class SpecialEventAdapter (
    private val sharedClanBattle: SharedViewModelClanBattle
) : BaseRecyclerAdapter<SpecialBattle, ListItemSpecialEventBinding>(R.layout.list_item_special_event) {

    override fun onBindViewHolder(holder: VH<ListItemSpecialEventBinding>, position: Int) {
        with(holder.binding) {
            val thisBoss = itemList[position]
            event = thisBoss

            val participantAdapter = ViewTypeAdapter<ViewType<*>>()
            val viewList = mutableListOf<ViewType<*>>()
            thisBoss.charaList.forEach {
                viewList.add(CharaIconUrlVT(
                    String.format(Locale.US, Statics.ICON_URL, it + 30)
                ))
            }
            if (viewList.isEmpty()) {
                viewList.add(DescriptionVT(I18N.getString(R.string.text_boss_none)))
            }
            participantAdapter.setUpdatedList(viewList)

            with(specialEventRecycler) {
                layoutManager = GridLayoutManager(context, 5).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return 1
                        }
                    }
                }
                adapter = participantAdapter
            }
            clickListener = View.OnClickListener {
                sharedClanBattle.mSetSelectedBoss(thisBoss.enemy)
                it.findNavController().navigate(
                    SpecialEventFragmentDirections.actionNavSpecialEventToNavEnemy()
                )
            }
            executePendingBindings()
        }
    }
}