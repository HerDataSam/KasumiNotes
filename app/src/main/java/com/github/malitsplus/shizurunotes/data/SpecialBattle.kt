package com.github.malitsplus.shizurunotes.data

class SpecialBattle(
    val eventId: Int,
    val name: String,
    val enemy: List<Enemy>,
    val maxRaidHP: Long,
    val charaList: List<Int>
)