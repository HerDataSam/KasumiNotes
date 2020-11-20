package com.github.malitsplus.shizurunotes.db

import com.github.malitsplus.shizurunotes.data.SrtPanel

class MasterSrt {
    fun getSrtPanels(): MutableList<SrtPanel> {
        val srtPanelList = mutableListOf<SrtPanel>()
        DBHelper.get().getSrtPanel()?.forEach {
            val srtPanel = SrtPanel(
                it.reading_id,
                it.reading,
                it.read_type,
                it.panel_id,
                it.detail_text
            )

            srtPanelList.add(srtPanel)
        }
        return srtPanelList
    }
}