package com.github.malitsplus.shizurunotes.db;

class HatsuneSchedule (
    @Column("event_id") val eventId: Int,
    @Column("title") val title: String,
    @Column("teaser_time") val teaserTime: String,
    @Column("start_time") val startTime: String,
    @Column("end_time") val endTime: String,
)
