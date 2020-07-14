package com.github.malitsplus.shizurunotes.ui.drop

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.Equipment

class DropViewModel : ViewModel() {

    var itemList = mutableListOf<Any>()

    fun refreshList(equipList: List<Equipment>) {
        itemList.clear()
        var currentPromotionLevel = 0
        equipList.sortedByDescending { it.minPromotionLevel }.forEach {
            if (it.itemId != 999999) {
                if (currentPromotionLevel != it.minPromotionLevel) {
                    currentPromotionLevel = it.minPromotionLevel
                    itemList.add(currentPromotionLevel.toString())
                }
                itemList.add(it)
            }
        }
    }
}