package com.github.malitsplus.shizurunotes.ui.dungeon

import android.view.View
import androidx.navigation.findNavController
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.Dungeon
import com.github.malitsplus.shizurunotes.databinding.ListItemDungeonBinding
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle
import com.github.malitsplus.shizurunotes.ui.base.BaseRecyclerAdapter

class DungeonAdapter(
    private val sharedClanBattle: SharedViewModelClanBattle
) : BaseRecyclerAdapter<Dungeon, ListItemDungeonBinding>(R.layout.list_item_dungeon) {

    override fun onBindViewHolder(holder: VH<ListItemDungeonBinding>, position: Int) {
        with(holder.binding){
            val thisDungeon = itemList[position]
            dungeon = thisDungeon
            textDungeonDescription.text = thisDungeon.description
            mode.text = thisDungeon.modeText
            if (thisDungeon.dungeonBoss.size > 1) {
                clanB2Icon.visibility = View.VISIBLE
            }
            clickListener = View.OnClickListener {
                sharedClanBattle.mSetSelectedBoss(thisDungeon.dungeonBoss)
                it.findNavController().navigate(
                    DungeonFragmentDirections.actionNavDungeonToNavEnemy()
                )
            }
            executePendingBindings()
        }
    }
}