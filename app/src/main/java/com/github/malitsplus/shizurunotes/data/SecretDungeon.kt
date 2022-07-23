package com.github.malitsplus.shizurunotes.data

import java.time.LocalDateTime

class SecretDungeon(
    val dungeonAreaId: Int,
    val waveGroupId: Int,
    val difficulty: Int,
    val floor: Int,
    val dungeonName: String,
    val description: String,
    val dungeonBoss: List<Enemy>,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
) {
    val difficultyText: String by lazy {
        if (difficulty == 0) {
            ""
        } else {
            "Difficulty: $difficulty"
        }
    }
    val floorText: String by lazy {
        if (floor == 0) {
            ""
        } else {
            "Floor #$floor"
        }
    }
}