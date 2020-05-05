package com.github.herdatasam.kasuminotes.ui.dungeon

import android.view.View
import androidx.navigation.findNavController
import com.github.herdatasam.kasuminotes.R
import com.github.herdatasam.kasuminotes.data.Dungeon
import com.github.herdatasam.kasuminotes.databinding.ListItemDungeonBinding
import com.github.herdatasam.kasuminotes.ui.shared.SharedViewModelClanBattle
import com.github.herdatasam.kasuminotes.ui.base.BaseRecyclerAdapter

class DungeonAdapter(
    private val sharedClanBattle: SharedViewModelClanBattle
) : BaseRecyclerAdapter<Dungeon, ListItemDungeonBinding>(R.layout.list_item_dungeon) {

    override fun onBindViewHolder(holder: VH<ListItemDungeonBinding>, position: Int) {
        with(holder.binding){
            val thisDungeon = itemList[position]
            dungeon = thisDungeon
            textDungeonDescription.text = thisDungeon.description
            clickListener = View.OnClickListener {
                sharedClanBattle.mSetSelectedBoss(thisDungeon.dungeonBoss)
                it.findNavController().navigate(
                    DungeonFragmentDirections.actionNavDungeonToNavClanBattleBossDetails()
                )
            }
            executePendingBindings()
        }
    }
}