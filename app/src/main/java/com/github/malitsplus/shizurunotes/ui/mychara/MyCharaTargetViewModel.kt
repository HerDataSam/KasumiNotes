package com.github.malitsplus.shizurunotes.ui.mychara

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.ui.base.CharaTargetVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class MyCharaTargetViewModel(
    val sharedChara: SharedViewModelChara
) : ViewModel() {
    val viewList = mutableListOf<ViewType<*>>()
    get() {
        field.clear()

        sharedChara.charaList.value?.let{ charaList ->
            // TODO: order
            charaList.filter { it.isBookmarked }.forEach {
                field.add(CharaTargetVT(it))
            }
        }
        return field
    }
}

interface OnCharaTargetClickListener<T>: OnItemActionListener {
    fun onCharaTargetClickedListener(chara: Chara, value: Int)
}