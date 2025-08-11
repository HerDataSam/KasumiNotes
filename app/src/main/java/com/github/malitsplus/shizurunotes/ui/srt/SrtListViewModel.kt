package com.github.malitsplus.shizurunotes.ui.srt

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.SrtPanel
import com.github.malitsplus.shizurunotes.ui.base.DividerVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.SrtGridVT
import com.github.malitsplus.shizurunotes.ui.base.SrtListVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelSrt
import com.github.malitsplus.shizurunotes.user.UserSettings

class SrtListViewModel(
    private val sharedSrtPanel: SharedViewModelSrt
) : ViewModel() {
    var showGrid = true
    var sortABC = false
    var isReadingVisible = UserSettings.get().getShowSrtReading()
    var panels = mutableListOf<SrtPanel>()
    var startStringList = listOf<String>()
    var endStringList = listOf<String>()
    var searchStartText = ""

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            var currentPanelId = 0
            setPanels()

            /*
            endStringList.forEach {
                field.add(SrtSearchStringVT(Pair(it, it == searchStartText)))
            }
            field.add(DividerVT())
            */
            val searchedPanels =
                if (searchStartText.isEmpty())
                    panels
                else
                    panels.filter { it.startList.contains(searchStartText) }

            if (showGrid) {
                val notDuplicatedList = mutableListOf<SrtPanel>()
                searchedPanels.sortedBy { it.readingId }.forEach {
                    if (currentPanelId != it.panelId) {
                        notDuplicatedList.add(it)
                        currentPanelId = it.panelId
                    }
                }
                if (sortABC) {
                    notDuplicatedList.sortBy { it.start }
                }
                notDuplicatedList.forEach {
                    field.add(SrtGridVT(Pair(it, isReadingVisible)))
                }
            } else {
                if (sortABC) {
                    searchedPanels.sortedBy { it.start }.forEach {
                        field.add(SrtListVT(it))
                    }
                } else {
                    searchedPanels.sortedBy { it.readingId }.forEach {
                        if (currentPanelId != it.panelId) {
                            if (currentPanelId != 0)
                                field.add(DividerVT())
                            currentPanelId = it.panelId
                        }
                        field.add(SrtListVT(it))
                    }
                }
            }

            return field
        }

    fun setPanels() {
        panels = sharedSrtPanel.srtList.value?.toMutableList() ?: mutableListOf()

        val startSet = mutableSetOf<String>()
        val endSet = mutableSetOf<String>()

        panels.forEach {
            startSet.add(it.start)
            endSet.add(it.end)
        }

        startStringList = startSet.sorted().toList()
        endStringList = endSet.sorted().toList()
    }
}

interface OnSrtClickListener<T> : OnItemActionListener {
    fun onSrtPanelClicked(item: T)
    fun onSrtStringClicked(item: String)
}