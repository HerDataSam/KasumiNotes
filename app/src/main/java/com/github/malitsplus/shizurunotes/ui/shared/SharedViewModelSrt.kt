package com.github.malitsplus.shizurunotes.ui.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.SrtPanel
import com.github.malitsplus.shizurunotes.db.MasterSrt
import kotlin.concurrent.thread

class SharedViewModelSrt : ViewModel() {
    val srtList = MutableLiveData<List<SrtPanel>>()
    var selectedSrt: SrtPanel? = null
    var isFromList = true

    fun loadData() {
        if (srtList.value.isNullOrEmpty()) {
            thread(start = true) {
                srtList.postValue(MasterSrt().getSrtPanels())
            }
        }
    }
}