package com.github.malitsplus.shizurunotes.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.github.malitsplus.shizurunotes.data.CampaignSchedule
import com.github.malitsplus.shizurunotes.data.CampaignType
import com.github.malitsplus.shizurunotes.data.EventSchedule
import com.github.malitsplus.shizurunotes.data.EventType
import com.github.malitsplus.shizurunotes.db.MasterSchedule
import com.github.malitsplus.shizurunotes.user.UserSettings
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.concurrent.thread

class NotificationManager private constructor(
    private val mContext: Context
) {
    companion object {
        private lateinit var instance: NotificationManager

        fun with (context: Context): NotificationManager {
            instance = NotificationManager(context)
            return instance
        }

        fun get(): NotificationManager {
            return instance
        }
    }

    var futureSchedule: MutableList<EventSchedule> = mutableListOf()

    private val notificationTypeMap = mapOf(
        NORMAL_BEFORE to CampaignType.dropAmountNormal,
        DUNGEON_BEFORE_2 to CampaignType.manaDungeon,
        DUNGEON_BEFORE to CampaignType.manaDungeon,
        HATSUNE_LAST to EventType.Hatsune,
        HATSUNE_LAST_HOUR to EventType.Hatsune,
        TOWER_LAST_HOUR to EventType.Tower
    )

    fun loadData() {
        thread(start = true) {
            futureSchedule = MasterSchedule().getSchedule(LocalDateTime.now())
            refreshNotification()
        }
    }

    fun refreshNotification() {
        futureSchedule.forEach {
            prepareAlarm(it, false)
        }
    }

    fun cancelAllAlarm() {
        futureSchedule.forEach {
            prepareAlarm(it, true)
        }
    }

    fun refreshSpecificNotification(typeString: String, newValue: Boolean) {
        futureSchedule.forEach {
            var t: Enum<*> = it.type
            if (it is CampaignSchedule) {
                t = it.campaignType
            }
            if (t == notificationTypeMap[typeString]) {
                if (newValue) {
                    setAlarm(it, typeString)
                } else {
                    cancelAlarm(getIntent(typeString), getSpecificId(it, typeString))
                }
            }
        }
    }

    private fun prepareAlarm(eventSchedule: EventSchedule, cancel: Boolean) {
        if (eventSchedule is CampaignSchedule) {
            when (eventSchedule.campaignType) {
                CampaignType.dropAmountNormal -> {
                    setOrCancelAlarm(eventSchedule, NORMAL_BEFORE, cancel)
                }
                CampaignType.manaDungeon -> {
                    setOrCancelAlarm(eventSchedule, DUNGEON_BEFORE_2, cancel)
                    setOrCancelAlarm(eventSchedule, DUNGEON_BEFORE, cancel)
                }
                else -> {  }
            }
        } else {
            when (eventSchedule.type) {
                EventType.Hatsune -> {
                    setOrCancelAlarm(eventSchedule, HATSUNE_LAST, cancel)
                    setOrCancelAlarm(eventSchedule, HATSUNE_LAST_HOUR, cancel)
                }
                EventType.Tower -> {
                    setOrCancelAlarm(eventSchedule, TOWER_LAST_HOUR, cancel)
                }
                else -> {  }
            }
        }
    }

    private fun setOrCancelAlarm(eventSchedule: EventSchedule, typeString: String, cancel: Boolean) {
        if (UserSettings.get().preference.getBoolean(typeString, false) && !cancel) {
            setAlarm(eventSchedule, typeString)
        } else {
            cancelAlarm(getIntent(typeString), getSpecificId(eventSchedule, typeString))
        }
    }

    private fun setAlarm(eventSchedule: EventSchedule, typeString: String) {
        if (!eventSchedule.startTime.isEqual(eventSchedule.endTime)) {
            val triggerTime = getTriggerTime(eventSchedule, typeString)
            if (triggerTime.isAfter(LocalDateTime.now())) {
                val alarmMgr = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = getIntent(typeString)
                val pendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    getSpecificId(eventSchedule, typeString),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
//                val zoneOffset = TimeZone.getDefault().toZoneId().rules.getOffset(LocalDateTime.now())

                alarmMgr.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime.toInstant(ZoneOffset.of("+9")).toEpochMilli(),
                    pendingIntent
                )
//            alarmMgr.set(AlarmManager.RTC_WAKEUP, LocalDateTime.now().plusSeconds(8).toInstant(zoneOffset).toEpochMilli(), pendingIntent)
            }
        }
    }

    private fun cancelAlarm(intent: Intent, id: Int) {
        val alarmMgr = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmMgr.cancel(pendingIntent)
    }

    private fun getTriggerTime(eventSchedule: EventSchedule, type: String): LocalDateTime {
        return when (type) {
            DUNGEON_BEFORE_2 -> eventSchedule.startTime.plusDays(-2).withHour(5).withMinute(0).withSecond(0)
            NORMAL_BEFORE,
            DUNGEON_BEFORE -> eventSchedule.startTime.plusDays(-1).withHour(5).withMinute(0).withSecond(0)
            HATSUNE_LAST -> eventSchedule.endTime.withHour(5).withMinute(0).withSecond(0)
            HATSUNE_LAST_HOUR,
            TOWER_LAST_HOUR -> eventSchedule.endTime.plusHours(-2)
            else -> LocalDateTime.MIN
        }
    }

    private fun getIntent(type: String): Intent {
        return Intent().apply {
            setClass(mContext, AlarmReceiver::class.java)
            action =
                NOTIFICATION_ACTION
            putExtra(NOTIFICATION_EXTRA_TYPE, type)
        }
    }

    private fun getSpecificId(eventSchedule: EventSchedule, typeString: String): Int {
        return when (typeString) {
            NORMAL_BEFORE -> eventSchedule.id + 100000
            DUNGEON_BEFORE_2 -> eventSchedule.id + 100000
            DUNGEON_BEFORE -> eventSchedule.id + 200000
            HATSUNE_LAST -> eventSchedule.id + 100000
            HATSUNE_LAST_HOUR -> eventSchedule.id + 200000
            TOWER_LAST_HOUR -> eventSchedule.id + 100000
            else -> 0
        }
    }
}