package com.github.malitsplus.shizurunotes.ui.srt

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.ui.base.DividerVT
import com.github.malitsplus.shizurunotes.ui.base.SrtGridVT
import com.github.malitsplus.shizurunotes.ui.base.SrtPanelDescriptionVT
import com.github.malitsplus.shizurunotes.ui.base.SrtPanelVT
import com.github.malitsplus.shizurunotes.ui.base.TextTagExtVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelSrt
import com.github.malitsplus.shizurunotes.user.UserSettings

class SrtPanelViewModel(
    private val sharedViewModelSrt: SharedViewModelSrt
) : ViewModel() {
    private val selectedPanel = sharedViewModelSrt.selectedSrt
    private val isFromList = sharedViewModelSrt.isFromList
    var isReadingVisible = UserSettings.get().getShowSrtReading()

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            selectedPanel?.let { panel ->
                field.add(SrtPanelVT(panel))

                sharedViewModelSrt.srtList.value?.let { list ->
                    list.filter { it.readingId == panel.readingId }
                        .sortedBy { it.readingId }.forEach { cPanel ->
                            field.add(SrtPanelDescriptionVT(cPanel))
                            list.filter { cPanel.endList.contains(it.start) }.let { endList ->
                                if (endList.isNotEmpty()) {
                                    field.add(
                                        TextTagExtVT(
                                            Pair(
                                                I18N.getString(R.string.text_srt_next_panel),
                                                endList.size.toString()
                                            )
                                        )
                                    )
                                    endList.forEach {
                                        field.add(SrtGridVT(Pair(it, isReadingVisible)))
                                    }
                                }
                            }
                            field.add(DividerVT())
                        }

                    list.filter { (it.readingId != panel.readingId) && (it.panelId == panel.panelId) }
                        .sortedBy { it.readingId }.forEach { cPanel ->
                            field.add(SrtPanelDescriptionVT(cPanel))
                            list.filter { cPanel.endList.contains(it.start) }.let { endList ->
                                if (endList.isNotEmpty()) {
                                    field.add(
                                        TextTagExtVT(
                                            Pair(
                                                I18N.getString(R.string.text_srt_next_panel),
                                                endList.size.toString()
                                            )
                                        )
                                    )
                                    endList.forEach {
                                        field.add(SrtGridVT(Pair(it, isReadingVisible)))
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