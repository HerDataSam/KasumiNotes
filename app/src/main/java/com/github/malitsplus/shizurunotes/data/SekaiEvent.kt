package com.github.malitsplus.shizurunotes.data

import java.time.LocalDateTime

class SekaiEvent(
    val eventId: Int,
    val sekaiEventName: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val SekaiBossId: Int,
    val sekaiBossName: String,
    val sekaiEventText: String,
    val SekaiBoss: Enemy
)
