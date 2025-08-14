package com.github.malitsplus.shizurunotes.db;

class AbyssSchedule(
    @Column("abyss_id") val abyssId: Int,
    val title: String,
    @Column("start_time") val startTime: String,
    @Column("end_time") val endTime: String,
    @Column("talent_id") val talentId: Int,
)