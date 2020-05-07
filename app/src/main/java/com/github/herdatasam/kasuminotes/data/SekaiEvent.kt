package com.github.herdatasam.kasuminotes.data;

class SekaiEvent(
    val eventId: Int,
    val sekaiEventName: String,
    val description: String,
    val startTime: String,
    val SekaiBossId: Int,
    val sekaiBossName: String,
    val sekaiEventText: String,
    val SekaiBoss: Enemy
)
