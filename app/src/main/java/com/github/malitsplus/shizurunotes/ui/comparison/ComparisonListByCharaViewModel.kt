package com.github.malitsplus.shizurunotes.ui.comparison

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class ComparisonListByCharaViewModel(
    private val sharedViewModelChara: SharedViewModelChara
) : ViewModel() {
    val viewList = mutableListOf<ViewType<*>>()
    get() {
        field.clear()
        var currentSearchAreaWidth = 0
        sharedViewModelChara.charaList.value?.sortedBy { it.searchAreaWidth }?.forEach {
            when {
                currentSearchAreaWidth < 100 && it.searchAreaWidth > 100 ->
                    field.add(HintTextVT(I18N.getString(R.string.ui_chip_position_forward)))
                currentSearchAreaWidth < 300 && it.searchAreaWidth > 300 ->
                    field.add(HintTextVT(I18N.getString(R.string.ui_chip_position_middle)))
                currentSearchAreaWidth < 600 && it.searchAreaWidth > 600 ->
                    field.add(HintTextVT(I18N.getString(R.string.ui_chip_position_middle)))
            }
            currentSearchAreaWidth = it.searchAreaWidth
            field.add(CharaIconComparisonVT(it))
        }

        return field
    }
}

interface OnCharaClickListener<T>: OnItemActionListener {
    fun onCharaClicked(item: T)
}
