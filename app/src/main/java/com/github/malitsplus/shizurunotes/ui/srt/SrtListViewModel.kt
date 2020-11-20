package com.github.malitsplus.shizurunotes.ui.srt

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelSrt

class SrtListViewModel (
    private val sharedSrtPanel: SharedViewModelSrt
) : ViewModel() {
    var showGrid = true

    val viewList = mutableListOf<ViewType<*>>()
    get() {
        field.clear()
        var currentPanelId = 0
        sharedSrtPanel.srtList.value?.sortedBy { it.readingId }?.forEach {
            if (showGrid) {
                if (currentPanelId != it.panelId) {
                    field.add(SrtGridVT(it))
                    currentPanelId = it.panelId
                }
            }
            else {
                field.add(SrtListVT(it))
            }
        }
        return field
    }
}

interface OnSrtClickListener<T>: OnItemActionListener {
    fun onSrtPanelClicked(item: T)
}