package com.github.malitsplus.shizurunotes.data

interface Item {
    val itemId: Int
    val itemName: String
    val iconUrl: String
    val itemDescription: String
    val itemType: ItemType
    val itemCraftedId: Int
    var itemUseRank: Int
}

enum class ItemType {
    GENERAL_ITEM,
    EQUIPMENT,
    EQUIPMENT_PIECE
}