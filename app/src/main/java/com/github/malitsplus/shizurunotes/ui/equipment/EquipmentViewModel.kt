package com.github.malitsplus.shizurunotes.ui.equipment

import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.data.Item
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelEquipment
import com.google.android.material.slider.Slider

class EquipmentViewModel(
    val sharedEquipment: SharedViewModelEquipment
) : ViewModel() {
    val equipment = sharedEquipment.selectedEquipment ?: Equipment.getNull

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            field.add(EquipmentBasicVT(equipment))
            field.addAll(getPropertyViewType(equipment.maxEnhanceLevel))
            field.add(EquipmentLevelVT(equipment))
            field.add(TextTagVT(I18N.getString(R.string.text_equipment_craft_requirement)))
            equipment.getLeafCraftMap().forEach {
                field.add(EquipmentCraftVT(it))
            }
            if (equipment.upperEquipmentList.size > 0) {
                field.add(TextTagVT(I18N.getString(R.string.text_equipment_craft_upper)))
                equipment.upperEquipmentList.forEach {
                    field.add(EquipmentVT(it))
                }
            }
            field.add(TextTagExtVT(
                Pair(I18N.getString(R.string.text_equipment_chara_equipment_link),
                    I18N.getString(R.string.d_chara, equipment.charaEquipmentLink.size))
            ))
            equipment.sortCharaEquipmentLink()
            equipment.charaEquipmentLink.forEach{
                field.add(EquipmentCharaLinkVT(it))
            }
            return field
        }

    val selectedLevel = MutableLiveData(equipment.maxEnhanceLevel)

    val onSliderChangeListener = Slider.OnChangeListener { _, value, _ ->
            selectedLevel.value = value.toInt()
    }

    fun getPropertyViewType(level: Int = 0): List<ViewType<*>> {
        val list = mutableListOf<ViewType<*>>()
        // 如果是专属装备，需要减去初始等级1
        val enhanceLevel = if (equipment.equipmentId in 130000..139999) {
            level - 1
        } else {
            level
        }
        equipment.getEnhancedProperty(enhanceLevel).nonZeroPropertiesMap.forEach {
            list.add(PropertyVT(it))
        }
        return list
    }
}

interface OnEquipmentActionListener<T>: OnItemActionListener {
    fun onItemClickedListener(item: Item)
    fun onEquipmentClickedListener(equipment: Equipment)
    val onSliderActionListener: Slider.OnChangeListener
}