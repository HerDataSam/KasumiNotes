package com.github.malitsplus.shizurunotes.ui.charaprofile

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.data.Rarity6Status
import com.github.malitsplus.shizurunotes.ui.base.CharaProfileVT
import com.github.malitsplus.shizurunotes.ui.base.CharaRankEquipmentAllVT
import com.github.malitsplus.shizurunotes.ui.base.CharaRankEquipmentVT
import com.github.malitsplus.shizurunotes.ui.base.CharaRarity6StatusVT
import com.github.malitsplus.shizurunotes.ui.base.CharaUniqueEquipmentVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.PropertyVT
import com.github.malitsplus.shizurunotes.ui.base.SpaceVT
import com.github.malitsplus.shizurunotes.ui.base.TextTagAlphaVT
import com.github.malitsplus.shizurunotes.ui.base.TextTagVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.EquipmentAllKey
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class CharaProfileViewModel(
    val sharedChara: SharedViewModelChara
) : ViewModel() {

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            sharedChara.selectedChara?.let { chara ->
                field.add(CharaProfileVT(chara))
                field.add(TextTagVT(I18N.getString(R.string.chara_story_status)))
                chara.storyStatusList.forEach { story ->
                    if (chara.charaId == story.charaId) {
                        field.add(TextTagAlphaVT(story.storyParsedName))
                        story.allProperty.nonZeroPropertiesMap.forEach {
                            field.add(PropertyVT(it))
                        }
                    }
                }
                chara.promotionBonus.filter { it.key == chara.maxCharaRank }.forEach { bonus ->
                    field.add(TextTagVT(I18N.getString(R.string.chara_promotion_bonus)))
                    bonus.value.nonZeroPropertiesMap.forEach {
                        field.add(PropertyVT(it))
                    }
                }
                field.add(SpaceVT())
                field.add(CharaUniqueEquipmentVT(chara.uniqueEquipment))

                if (chara.uniqueEquipment2 != Equipment.getNull)
                    field.add(CharaUniqueEquipmentVT(chara.uniqueEquipment2))

                // rarity 6 info
                if (chara.rarity6Status.isNotEmpty())
                    field.add(CharaRarity6StatusVT(chara.rarity6Status))
                // All equipments
                field.add(
                    CharaRankEquipmentAllVT(
                        listOf(EquipmentAllKey.ToMax, EquipmentAllKey.ToContentsMax, EquipmentAllKey.ToTarget)
                    )
                )
                // each rank equipment
                chara.rankEquipments.entries.forEach {
                    field.add(CharaRankEquipmentVT(it))
                }
                chara
            }
            return field
        }
}

interface OnEquipmentClickListener<T> : OnItemActionListener {
    fun onEquipmentClicked(item: T)
    fun onRarity6Clicked(item: Rarity6Status)
    fun onEquipmentAllClicked(item: EquipmentAllKey)
}
