package com.github.malitsplus.shizurunotes.ui.rarity6status

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Item
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class Rarity6StatusViewModel (
    val sharedChara: SharedViewModelChara
) : ViewModel() {
    val rarity6Status = sharedChara.selectedRarity6Status!!

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            field.add(ItemBasicVT(rarity6Status.item))
            field.addAll(getPropertyViewType)
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

interface OnItemClickListener<T>: OnItemActionListener {
    fun onItemClicked(item: T)
}