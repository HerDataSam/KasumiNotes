package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import java.time.LocalDateTime

class SecretDungeon(
    val dungeonAreaId: Int,
    val waveGroupId: Int,
    val difficulty: Int,
    val floor: Int,
    val dungeonBoss: List<Enemy>,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
) {
    val difficultyText: String by lazy {
        I18N.getString(R.string.secret_dungeon_difficulty, difficulty)
    }
    val floorText: String by lazy {
        I18N.getString(R.string.secret_dungeon_floor, floor)
    }
}