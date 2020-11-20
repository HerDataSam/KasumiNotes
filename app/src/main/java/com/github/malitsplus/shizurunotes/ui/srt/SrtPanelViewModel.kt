package com.github.malitsplus.shizurunotes.ui.srt

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelSrt

class SrtPanelViewModel(
    private val sharedViewModelSrt: SharedViewModelSrt
) : ViewModel() {
    private val selectedPanel = sharedViewModelSrt.selectedSrt
    private val isFromList = sharedViewModelSrt.isFromList

    val viewList = mutableListOf<ViewType<*>>()
    get() {
        field.clear()
        selectedPanel?.let { panel ->
            field.add(SrtPanelVT(panel))

            sharedViewModelSrt.srtList.value?.let { list ->
                list.filter { it.readingId == panel.readingId }
                    .sortedBy { it.readingId }.forEach { cPanel ->
                    field.add(SrtPanelDescriptionVT(cPanel))
                    list.filter { it.start == cPanel.end }.let { endList ->
                        if (endList.isNotEmpty()) {
                            field.add(TextTagExtVT(Pair(I18N.getString(R.string.text_srt_next_panel), endList.size.toString())))
                            endList.forEach {
                                field.add(SrtGridVT(it))
                            }
                        }
                    }
                    field.add(DividerVT())
                }

                list.filter { (it.readingId != panel.readingId) && (it.panelId == panel.panelId) }
                    .sortedBy { it.readingId }.forEach { cPanel ->
                    field.add(SrtPanelDescriptionVT(cPanel))
                    list.filter { it.start == cPanel.end }.let { endList ->
                        if (endList.isNotEmpty()) {
                            field.add(TextTagExtVT(Pair(I18N.getString(R.string.text_srt_next_panel), endList.size.toString())))
                            endList.forEach {
                                field.add(SrtGridVT(it))
                            }
                        }
                    }
                    field.add(DividerVT())
                }
            }
        }

        return field
    }
}

interface OnSrtPanelClickListener<T>: OnItemActionListener {
    fun onSrtPanelClicked(item: T)
}