package com.github.herdatasam.kasuminotes.data

class WaveGroup(
    val id: Int,
    val waveGroupId: Int
) {
    var enemyList: List<Enemy>? = null
    var dropGoldList: List<Int>? = null
    var dropRewardList: List<EnemyRewardData>? = null

}