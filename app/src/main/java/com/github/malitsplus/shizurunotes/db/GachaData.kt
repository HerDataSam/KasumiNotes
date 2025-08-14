package com.github.malitsplus.shizurunotes.db;

class GachaData(
    @Column("gacha_id") val gachaId: Int,
    @Column("gacha_name") val gachaName: String,
    val description: String,
    @Column("description_2") val description2: String,
    @Column("description_sp") val descriptionSp: String,
    @Column("start_time") val startTime: String,
    @Column("end_time") val endTime: String,
    @Column("special_id") val specialId: Int,
    @Column("movie_id") val movieId: Int,
    @Column("exchange_id") val exchangeId: Int,
    @Column("prizegacha_id") val prizegachaId: Int,
    @Column("gacha_bonus_id") val gachaBonusId: Int,
)
