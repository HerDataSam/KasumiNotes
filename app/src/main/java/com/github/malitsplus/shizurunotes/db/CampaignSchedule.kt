package com.github.malitsplus.shizurunotes.db;

class CampaignSchedule(
    val id: Int,
    @Column("campaign_category") val campaignCategory: Int,
    val value: Double,
    @Column("system_id") val systemId: Int,
    @Column("icon_image") val iconImage: Int,
    @Column("start_time") val startTime: String,
    @Column("end_time") val endTime: String,
    @Column("level_id") val levelId: Int,
)