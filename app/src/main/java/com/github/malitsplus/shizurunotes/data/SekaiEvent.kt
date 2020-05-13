package com.github.malitsplus.shizurunotes.data

import java.time.LocalDate

class SekaiEvent(
    val eventId: Int,
    val sekaiEventName: String,
    val description: String,
    val startTime: LocalDate,
    val SekaiBossId: Int,
    val sekaiBossName: String,
    val sekaiEventText: String,
    val SekaiBoss: Enemy
)
