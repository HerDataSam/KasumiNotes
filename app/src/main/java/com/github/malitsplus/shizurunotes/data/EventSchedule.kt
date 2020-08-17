package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.ResourceManager
import com.github.malitsplus.shizurunotes.utils.Utils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class EventSchedule(
    val id: Int,
    val name: String,
    val type: EventType,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
) {
    open val title: String = when (type) {
        EventType.Hatsune -> type.description + ": " + name
        else -> type.description
    }

    val durationString: String
        get() {
            val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            return startTime.format(pattern) + "  ~  " + endTime.format(pattern)
        }
    val durationStringForToday: String
        get() {
            val pattern = DateTimeFormatter.ofPattern(I18N.getString(R.string.simple_date_format))
            var string = ""
            if (!startTime.isBefore(LocalDateTime.now()))
                string += startTime.format(pattern)
            string += " ~ "
            return string + endTime.format(pattern)
        }

    val colorResource: Int
        get() {
            return ResourceManager.get().getColor(color)
        }

    open val color: Int
        get() {
            return type.color
        }

    open val importance: Int
        get () {
            return type.value
        }
}

class CampaignSchedule(
    id: Int,
    name: String,
    type: EventType,
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    val category: Int,
    val campaignType: CampaignType,
    val value: Double,
    val systemId: Int
) : EventSchedule(id, name, type, startTime, endTime) {
    override val title: String by lazy {
        campaignType.description().format(Utils.roundIfNeed(value / 1000.0))
    }
    override val color: Int by lazy {
        campaignType.shortColor()
    }
    override val importance: Int by lazy {
        campaignType.value
    }
    val shortTitle: String = campaignType.shortDescription().format(Utils.roundIfNeed(value / 1000.0))
}

enum class EventType(var value: Int) {
    Campaign(4),
    Hatsune(3),
    ClanBattle(1),
    Tower(2),
    Gacha(0);

    val description: String
        get() = when (this) {
            Campaign -> I18N.getString(R.string.campaign)
            Hatsune -> I18N.getString(R.string.hatsune)
            ClanBattle -> I18N.getString(R.string.clanBattle)
            Tower -> I18N.getString(R.string.tower)
            Gacha -> I18N.getString(R.string.gacha)
//            else -> I18N.getString(R.string.unknown)
        }

    val color: Int
        get() = when (this) {
            Campaign -> R.color.Sage
            Hatsune -> R.color.Tangerine
            ClanBattle -> R.color.Peacock
            Tower -> R.color.Grape
            Gacha -> R.color.Flamingo
//            else -> R.color.Graphite
        }

    val order: Int
        get() = when (this) {
            Campaign -> 1
            Hatsune -> 0
            ClanBattle -> 2
            Tower -> 2
            Gacha -> 6
        }

}