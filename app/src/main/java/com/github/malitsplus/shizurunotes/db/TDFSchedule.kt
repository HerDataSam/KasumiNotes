package com.github.malitsplus.shizurunotes.db;

class TDFSchedule(
    @Column("schedule_id") val scheduleId: Int,
    @Column("ex_quest_id") val exQuestId: Int,
    @Column("start_time") val startTime: String,
    @Column("end_time") val endTime: String,
)
