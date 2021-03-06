package com.github.malitsplus.shizurunotes.ui.skillsearch

import android.view.MotionEvent
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.databinding.ListItemSkillSearchDetailsBinding
import com.github.malitsplus.shizurunotes.ui.base.BaseRecyclerAdapter
import com.github.malitsplus.shizurunotes.ui.base.SkillSimpleVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class SkillActionClassDetailsAdapter (
    val sharedChara: SharedViewModelChara
): BaseRecyclerAdapter<Chara, ListItemSkillSearchDetailsBinding>(
    R.layout.list_item_skill_search_details) {
    override fun onBindViewHolder(holder: VH<ListItemSkillSearchDetailsBinding>, position: Int) {
        with(holder.binding) {
            val thisChara = itemList[position]
            chara = thisChara

            val skillAdapter = ViewTypeAdapter<ViewType<*>>()
            val skillList = mutableListOf<ViewType<*>>()
            thisChara.skills.forEach {
                if (it.actions.fold(false, {
                            acc, action -> acc || action.actionType == sharedChara.selectedActionType
                }))
                    skillList.add(SkillSimpleVT(it))
            }
            skillAdapter.setUpdatedList(skillList)

            with(skillSearchRecycler) {
                layoutManager = LinearLayoutManager(context)
                adapter = skillAdapter

                //val divider = DividerItemDecoration(context, LinearLayoutManager(context).orientation)
                //divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
                //addItemDecoration(divider)
            }
            clickListener = View.OnClickListener {
                sharedChara.mSetSelectedChara(thisChara)
                it.findNavController().navigate(
                    SkillActionClassDetailsFragmentDirections.actionNavSkillSearchDetailsToNavCharaDetails()
                )
            }
        }
    }
}