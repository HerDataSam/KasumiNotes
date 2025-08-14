package com.github.malitsplus.shizurunotes.db

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.CampaignSchedule
import com.github.malitsplus.shizurunotes.data.CampaignType
import com.github.malitsplus.shizurunotes.data.EventSchedule
import com.github.malitsplus.shizurunotes.data.EventType
import com.github.malitsplus.shizurunotes.data.GachaExchangeLineup
import com.github.malitsplus.shizurunotes.data.GachaSchedule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MasterSchedule {
    fun getSchedule(nowTime: LocalDateTime?): MutableList<EventSchedule> {
        val scheduleList = mutableListOf<EventSchedule>()
        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d H:mm:ss")

        val talentMap = mapOf(
            1 to I18N.getString(R.string.talent_1),
            2 to I18N.getString(R.string.talent_2),
            3 to I18N.getString(R.string.talent_3),
            4 to I18N.getString(R.string.talent_4),
            5 to I18N.getString(R.string.talent_5),
        )

        DBHelper.get().getCampaignSchedule(null)?.forEach {
            val campaignType = CampaignType.parse(it.campaignCategory)
            scheduleList.add(
                CampaignSchedule(
                    it.id, "", EventType.Campaign,
                    LocalDateTime.parse(it.startTime, formatter),
                    LocalDateTime.parse(it.endTime, formatter),
                    it.campaignCategory, campaignType, it.value, it.systemId
                )
            )
        }
        DBHelper.get().getHatsuneSchedule(null)?.forEach {
            scheduleList.add(
                EventSchedule(
                    it.eventId, it.title, EventType.Hatsune,
                    LocalDateTime.parse(it.startTime, formatter),
                    LocalDateTime.parse(it.endTime, formatter)
                )
            )
        }
        DBHelper.get().getTowerSchedule(null)?.forEach {
            scheduleList.add(
                EventSchedule(
                    it.towerScheduleId, "", EventType.Tower,
                    LocalDateTime.parse(it.startTime, formatter), LocalDateTime.parse(it.endTime, formatter)
                )
            )
        }
        DBHelper.get().getSecretDungeonSchedule(null)?.forEach {
            scheduleList.add(
                EventSchedule(
                    it.dungeonAreaId, "", EventType.SecretDungeon,
                    LocalDateTime.parse(it.startTime, formatter), LocalDateTime.parse(it.endTime, formatter)
                )
            )
        }
        DBHelper.get().getAbyssSchedule(null)?.forEach {
            scheduleList.add(
                EventSchedule(
                    it.abyssId, "${it.title} (${talentMap[it.talentId]})", EventType.Abyss,
                    LocalDateTime.parse(it.startTime, formatter), LocalDateTime.parse(it.endTime, formatter)
                )
            )
        }

        DBHelper.get().getDomeSchedule(null)?.forEach {
            scheduleList.add(
                EventSchedule(
                    it.scheduleId, "", EventType.Dome,
                    LocalDateTime.parse(it.startTime, formatter), LocalDateTime.parse(it.endTime, formatter)
                )
            )
        }

        DBHelper.get().getTDFSchedule(null)?.forEach {
            scheduleList.add(
                EventSchedule(
                    it.scheduleId, "", EventType.TDF,
                    LocalDateTime.parse(it.startTime, formatter), LocalDateTime.parse(it.endTime, formatter)
                )
            )
        }

        DBHelper.get().getGachaSchedule(null)?.forEach {
            val gachaExchangeLineup = mutableListOf<GachaExchangeLineup>()
            if (it.exchangeId != 0) {
                DBHelper.get().getGachaExchangeLineup(it.exchangeId)?.forEach { lineUp ->
                    gachaExchangeLineup.add(
                        GachaExchangeLineup(
                            lineUp.id, lineUp.exchangeId, lineUp.unitId, lineUp.gachaBonusId
                        )
                    )
                }
            }
            scheduleList.add(
                GachaSchedule(
                    it.gachaId, it.gachaName, EventType.PickUp,
                    LocalDateTime.parse(it.startTime, formatter),
                    LocalDateTime.parse(it.endTime, formatter),
                    it.description.replace("\\n", " "),
                    gachaExchangeLineup.toList(), it.prizegachaId, it.gachaBonusId
                )
            )
        }

        if (nowTime == null) {
            DBHelper.get().getFreeGachaSchedule(null)?.forEach {
                scheduleList.add(
                    EventSchedule(
                        it.campaign_id, "", EventType.Gacha,
                        LocalDateTime.parse(it.start_time, formatter),
                        LocalDateTime.parse(it.end_time, formatter)
                    )
                )
            }
            DBHelper.get().getClanBattleTrainingPeriod()?.forEach {
                scheduleList.add(
                    EventSchedule(
                        it.clan_battle_id,
                        "",
                        EventType.ClanBattleTraining,
                        LocalDateTime.parse(it.start_time, formatter),
                        LocalDateTime.parse(it.end_time, formatter)
                    )
                )
            }
            DBHelper.get().getClanBattlePeriod()?.forEach {
                scheduleList.add(
                    EventSchedule(
                        it.clan_battle_id,
                        "",
                        EventType.ClanBattle,
                        LocalDateTime.parse(it.start_time, formatter),
                        LocalDateTime.parse(it.end_time, formatter)
                    )
                )
            }
        } else {
            //你说cy这么大个会社为什么连日期格式都不用标准的还得从程序上判断
            val iterator = scheduleList.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().endTime.isBefore(nowTime)) {
                    iterator.remove()
                }
            }
        }
        return scheduleList
    }
}