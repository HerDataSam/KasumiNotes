package com.github.malitsplus.shizurunotes.ui.rarity6status

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Item
import com.github.malitsplus.shizurunotes.ui.base.EquipmentCraftVT
import com.github.malitsplus.shizurunotes.ui.base.ItemBasicVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.PropertyVT
import com.github.malitsplus.shizurunotes.ui.base.TextTagVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class Rarity6StatusViewModel(
    val sharedChara: SharedViewModelChara
) : ViewModel() {
    val rarity6Status = sharedChara.selectedRarity6Status!!

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            field.add(ItemBasicVT(rarity6Status.item))
            field.addAll(getPropertyViewType)
            field.add(TextTagVT(I18N.getString(R.string.text_equipment_craft_requirement)))
            field.addAll(getItemCountViewType)
            return field
        }

    val getPropertyViewType: List<ViewType<*>>
        get() {
            val list = mutableListOf<ViewType<*>>()
            rarity6Status.property.nonZeroPropertiesMap.forEach {
                list.add(PropertyVT(it))
            }
            return list
        }

    val getItemCountViewType: List<ViewType<*>>
        get() {
            val list = mutableListOf<ViewType<*>>()
            val map = mutableMapOf<Item, Int>()
            map[rarity6Status.item] = rarity6Status.itemCount
            map.forEach {
                list.add(EquipmentCraftVT(it))
            }
            return list
        }
}

interface OnItemClickListener<T> : OnItemActionListener {
    fun onItemClicked(item: T)
}