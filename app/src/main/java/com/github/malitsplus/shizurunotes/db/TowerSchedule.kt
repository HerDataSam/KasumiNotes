package com.github.malitsplus.shizurunotes.db;

class TowerSchedule(
    @Column("tower_schedule_id") val towerScheduleId: Int,
    @Column("start_time") val startTime: String,
    @Column("end_time") val endTime: String,
)
