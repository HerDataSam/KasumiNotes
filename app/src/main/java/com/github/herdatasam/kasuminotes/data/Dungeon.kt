package com.github.herdatasam.kasuminotes.data

class Dungeon(
    val dungeonAreaId: Int,
    val waveGroupId: Int,
    val enemyId: Int,
    val dungeonName: String,
    val description: String,
    val dungeonBoss: Enemy
)