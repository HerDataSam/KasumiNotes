package com.github.malitsplus.shizurunotes.ui.kaiserbattle

import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.Statics
import com.github.malitsplus.shizurunotes.data.KaiserBattle
import com.github.malitsplus.shizurunotes.databinding.ListItemKaiserBattleBinding
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle
import java.util.*

class KaiserBattleAdapter (
    private val sharedClanBattle: SharedViewModelClanBattle
) : BaseRecyclerAdapter<KaiserBattle, ListItemKaiserBattleBinding>(R.layout.list_item_kaiser_battle) {

    override fun onBindViewHolder(holder: VH<ListItemKaiserBattleBinding>, position: Int) {
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
                viewList.add(DescriptionVT(I18N.getString(R.string.text_kaiser_same)))
            }
            participantAdapter.setUpdatedList(viewList)

            with(kaiserBattleRecycler) {
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
                sharedClanBattle.mSetSelectedBoss(thisBoss.boss)
                it.findNavController().navigate(
                    KaiserBattleFragmentDirections.actionNavKaiserBattleToNavEnemy()
                )
            }
            executePendingBindings()
        }
    }
}