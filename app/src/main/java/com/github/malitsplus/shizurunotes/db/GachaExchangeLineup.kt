package com.github.malitsplus.shizurunotes.db;

class GachaExchangeLineup(
    val id: Int,
    @Column("exchange_id") val exchangeId: Int,
    @Column("unit_id") val unitId: Int,
    val rarity: Int,
    @Column("gacha_bonus_id") val gachaBonusId: Int,
)
