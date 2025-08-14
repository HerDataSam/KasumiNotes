package com.github.malitsplus.shizurunotes.db;

class DomeSchedule(
    @Column("schedule_id") val scheduleId: Int,
    @Column("start_time") val startTime: String,
    @Column("end_time") val endTime: String,
)
