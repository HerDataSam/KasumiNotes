package com.github.malitsplus.shizurunotes.ui.drop

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.ResourceManager
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.data.Item
import com.github.malitsplus.shizurunotes.data.Quest
import com.github.malitsplus.shizurunotes.databinding.ItemDropEquipmentBinding
import com.github.malitsplus.shizurunotes.databinding.ItemDropEquipmentSimpleBinding
import com.github.malitsplus.shizurunotes.databinding.ItemHintTextBinding
import com.github.malitsplus.shizurunotes.databinding.ItemQuestDropBinding
import com.github.malitsplus.shizurunotes.ui.base.BaseHintAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelEquipment
import com.github.malitsplus.shizurunotes.user.UserSettings

class DropQuestAdapter(
    private val mContext: Context,
    private val sharedEquipment: SharedViewModelEquipment
) : BaseHintAdapter<ItemQuestDropBinding, ItemHintTextBinding>(mContext, R.layout.item_quest_drop, R.layout.item_hint_text) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is HintTextViewHolder -> {
                with(holder.binding as ItemHintTextBinding) {
                    itemModel = itemList[position] as String
                    executePendingBindings()
                }
            }
            is InstanceViewHolder -> {
                val thisQuest = itemList[position] as Quest
                with(holder.binding as ItemQuestDropBinding) {
                    quest = thisQuest
                    when(thisQuest.questType) {
                        Quest.QuestType.Hard -> this.textQuestType.setBackgroundResource(R.drawable.shape_text_tag_background_variant)
                        else -> this.textQuestType.setBackgroundResource(R.drawable.shape_text_tag_background)
                    }
                    dropIconContainer.removeAllViews()
                    thisQuest.dropList.forEach {
                        val rewardItem = if (true) {
                            DataBindingUtil.inflate<ItemDropEquipmentBinding>(
                                LayoutInflater.from(mContext),
                                R.layout.item_drop_equipment,
                                dropIconContainer,
                                false
                            ).apply {
                                reward = it
                            }
                        } else {
                            DataBindingUtil.inflate<ItemDropEquipmentSimpleBinding>(
                                LayoutInflater.from(mContext),
                                R.layout.item_drop_equipment_simple,
                                dropIconContainer,
                                false
                            ).apply {
                                reward = it
                            }
                        }
                        sharedEquipment.selectedDrops.value?.let { itemList ->
                            for (item: Item in itemList) {
                                if (it.rewardId % 10000 == item.itemId % 10000) {
                                    rewardItem.root.background = mContext.getDrawable(R.drawable.shape_text_border)
                                    break
                                }
                            }
                        }
                        dropIconContainer.addView(rewardItem.root, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
                    }
                    executePendingBindings()
                }
            }
        }
    }
}