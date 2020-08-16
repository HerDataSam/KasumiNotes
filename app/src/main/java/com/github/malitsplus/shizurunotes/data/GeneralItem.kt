package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.common.Statics

class GeneralItem(
    override val itemId: Int,
    override val itemName: String,
    override val itemType: ItemType,
    override val itemDescription: String,
    override val itemCraftedId: Int,
    override var itemUseRank: Int = 99
) : Item {
    override val iconUrl: String = Statics.ITEM_ICON_URL.format(itemId)
}