package com.github.malitsplus.shizurunotes.ui.mychara

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.ui.base.CharaIconVT
import com.github.malitsplus.shizurunotes.ui.base.HintTextVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class MyCharaViewModel(
    val sharedChara: SharedViewModelChara
) : ViewModel() {
    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            val charaList = sharedChara.charaList.value
            charaList?.sortedByDescending { it.unitId }?.let { sortedList ->
                var currentRankEquipment = 0
                sortedList.filter { it.isBookmarked }
                    .sortedWith(compareBy({ -it.displaySetting.rank }, { -it.displaySetting.equipmentNumber }))
                    .forEach {
                        if (currentRankEquipment != it.displaySetting.rank * 100 + it.displaySetting.equipmentNumber) {
                            currentRankEquipment = it.displaySetting.rank * 100 + it.displaySetting.equipmentNumber
                            field.add(
                                HintTextVT(
                                    I18N.getString(
                                        R.string.rank_d_equipment,
                                        it.displaySetting.rank,
                                        it.displaySetting.equipmentNumber
                                    )
                                )
                            )
                        }
                        field.add(CharaIconVT(it))
                    }

                field.add(HintTextVT(I18N.getString(R.string.my_chara_not_owned)))
                sortedList.filter { !it.isBookmarked }.forEach {
                    field.add(CharaIconVT(it))
                }
            }
            return field
        }
}

interface OnCharaClickListener<T> : OnItemActionListener {
    fun onCharaClickedListener(chara: Chara)
}