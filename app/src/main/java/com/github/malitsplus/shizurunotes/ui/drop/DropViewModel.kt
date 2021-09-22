package com.github.malitsplus.shizurunotes.ui.drop

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Equipment

class DropViewModel : ViewModel() {

    lateinit var equipmentList: List<Equipment>
    var liveItemList = MutableLiveData<MutableList<Any>>()
    var searchText: CharSequence = ""

    fun refreshList() {
        var equipList = equipmentList.toList()
        val itemList: MutableList<Any> = ArrayList()
        var currentPromotionLevel = 0
        if (searchText.isNotEmpty()) {
            val toInteger = searchText.toString().toIntOrNull() ?: 0
            if (toInteger > 0)
                equipList = equipList.filter { it.itemUseRank == toInteger }
            else
                equipList = equipList.filter { it.equipmentName.contains(searchText) || it.catalog == searchText.toString() }
        }
        equipList.sortedByDescending { it.itemUseRank }.forEach {
            if (it.itemId != 999999) {
                if (currentPromotionLevel != it.itemUseRank) {
                    currentPromotionLevel = it.itemUseRank
                    itemList.add(currentPromotionLevel.toString())
                }
                itemList.add(it)
            }
        }
        liveItemList.postValue(itemList)
    }

    val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable) {
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            searchText = s
            refreshList()
        }
    }
}