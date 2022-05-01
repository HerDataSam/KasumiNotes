package com.github.malitsplus.shizurunotes.db

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.HatsuneStage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MasterHatsune {
    fun getHatsune(): MutableList<HatsuneStage> {
        val hatsuneStageList = mutableListOf<HatsuneStage>()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        DBHelper.get().getHatsuneSchedule(null)?.forEach { schedule ->
            val hatsuneStage = HatsuneStage(
                schedule.event_id,
                LocalDateTime.parse(schedule.teaser_time, formatter),
                LocalDateTime.parse(schedule.start_time, formatter),
                LocalDateTime.parse(schedule.end_time, formatter),
                schedule.title
            )
            hatsuneStageList.add(hatsuneStage)
        }
        return hatsuneStageList
    }

    fun getHatsuneBattleWave(hatsune: HatsuneStage): HatsuneStage {
        DBHelper.get().getHatsuneBattle(hatsune.eventId)?.forEach { battle ->
            DBHelper.get().getWaveGroupData(battle.wave_group_id_1)?.let {
                hatsune.battleWaveGroupMap[battle.quest_name] = it.getWaveGroup(true)
            }
        }
        DBHelper.get().getHatsuneSP(hatsune.eventId)?.forEach { sp ->
            DBHelper.get().getWaveGroupData(sp.wave_group_id)?.let {
                hatsune.battleWaveGroupMap[I18N.getString(R.string.sp_mode_d, sp.mode)] = it.getWaveGroup(true)
            }
        }
        return hatsune
    }
}