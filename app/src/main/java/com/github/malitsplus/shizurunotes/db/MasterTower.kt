package com.github.malitsplus.shizurunotes.db

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.TowerArea
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MasterTower {
    fun getTower(): MutableList<TowerArea> {
        var lastMaxFloorNum = 0
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        val towerList = mutableListOf<TowerArea>()
        DBHelper.get().getTowerArea()?.forEach {
            val towerArea = TowerArea(
                it.tower_area_id,
                lastMaxFloorNum + 1,
                it.max_floor_num,
                it.cloister_quest_id,
                LocalDateTime.parse(it.start_time, formatter)
            )
            lastMaxFloorNum = it.max_floor_num

            towerArea.bossId = DBHelper.get().getTowerEnemyBoss(towerArea.minWaveId, towerArea.maxWaveId)

            towerList.add(towerArea)
        }
        return towerList
    }

    fun getTowerWave(tower: TowerArea): TowerArea {
        DBHelper.get().getTowerWave((tower.minFloorId..tower.maxFloorId step 10).toList())?.forEach { wave ->
            tower.towerWaveGroupMap[I18N.getString(R.string.tower_d_floor, wave.id % 10000)] = wave.getWaveGroup(true)
        }

        DBHelper.get().getTowerWave(listOf(tower.exQuestId))?.forEach { wave ->
            tower.towerWaveGroupMap[I18N.getString(R.string.tower_d_floor_ex, tower.maxFloorNum)] = wave.getWaveGroup(true)
        }

        DBHelper.get().getTowerCloister(tower.cloisterId)?.let { cloister ->
            var waveNumber = 1
            DBHelper.get().getTowerWave(cloister.waveGroupId)?.forEach { wave ->
                tower.towerWaveGroupMap[I18N.getString(R.string.tower_cloister_d_wave, waveNumber)] = wave.getWaveGroup(true)
                waveNumber++
            }
        }

        return tower
    }
}