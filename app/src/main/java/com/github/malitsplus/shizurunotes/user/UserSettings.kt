package com.github.malitsplus.shizurunotes.user

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.github.malitsplus.shizurunotes.db.DBHelper
import com.github.malitsplus.shizurunotes.utils.FileUtils
import com.github.malitsplus.shizurunotes.utils.JsonUtils
import com.github.malitsplus.shizurunotes.utils.LogUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread
import kotlin.math.max

class UserSettings private constructor(
    private val application: Application
) {
    companion object {
        const val LANGUAGE_KEY = "language"
        const val SERVER_KEY = "server"
        const val EXPRESSION_STYLE = "expressionStyle"
        const val FONT_SIZE = "textSize"
        const val CONTENTS_MAX = "contentsMax"
        const val CONTENTS_MAX_LEVEL = "contentsMaxLevel"
        const val CONTENTS_MAX_RANK = "contentsMaxRank"
        const val CONTENTS_MAX_EQUIPMENT = "contentsMaxEquipment"
        const val CONTENTS_MAX_AREA = "contentsMaxArea"
        const val CONTENTS_SELECTION = "contentsSelection"
        const val CALENDAR_FILTER = "calendarFilter"
        const val DELETE_USER_DATA = "deleteUserData"
        const val ADD_PASSIVE_ABILITY = "addPassiveAbility"
        const val LOG = "log"
        const val DB_VERSION = "dbVersion_new"
        const val DB_VERSION_JP = "dbVersion_jp"
        const val DB_VERSION_CN = "dbVersion_cn"
        const val DB_VERSION_KR = "dbVersion_kr"
        const val APP_VERSION = "appVersion"
        const val BETA_TEST = "betaTest"
        const val ABOUT = "about"

        private const val userDataFileName = "userData.json"

        @Volatile
        private lateinit var instance: UserSettings

        fun with(application: Application) {
            synchronized(UserSettings::class.java) {
                instance = UserSettings(application)
            }
        }

        @JvmStatic
        fun get(): UserSettings {
            return instance
        }
    }

    private val json: String
        get() {
            val stringBuilder = StringBuilder()
            if (FileUtils.checkFile(FileUtils.getFileFilePath(userDataFileName))) {
                try {
                    application.openFileInput(userDataFileName).use { fis ->
                        val inputStreamReader = InputStreamReader(fis, StandardCharsets.UTF_8)
                        val reader = BufferedReader(inputStreamReader)
                        var line = reader.readLine()
                        while (line != null) {
                            stringBuilder.append(line).append('\n')
                            line = reader.readLine()
                        }
                    }
                } catch (e: IOException) {
                    LogUtils.file(LogUtils.E, "GetUserJson", e.message, e.stackTrace)
                }
            }
            return stringBuilder.toString()
        }

    private val userData: UserData = if (json.isNotEmpty()) {
        JsonUtils.getBeanFromJson<UserData>(json, UserData::class.java)
    } else {
        UserData()
    }

    val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(application)

    var lastEquipmentIds: List<Int>
        get() {
            return if (userData.lastEquipmentIds != null) {
                userData.lastEquipmentIds
            } else {
                emptyList()
            }
        }
        set(ids) {
            userData.lastEquipmentIds = ids
            saveJson()
        }

    private fun saveJson() {
        thread(start = true) {
            val json = JsonUtils.getJsonFromBean(userData)
            try {
                application.openFileOutput(userDataFileName, Context.MODE_PRIVATE).use { fos ->
                    fos.write(json.toByteArray())
                }
            } catch (e: IOException) {
                LogUtils.file(LogUtils.E, "SaveUserJson", e.message, e.stackTrace)
            }
        }
    }

    fun deleteUserData() {
        userData.contentsMaxArea = null
        userData.contentsMaxLevel = null
        userData.contentsMaxRank = null
        userData.contentsMaxEquipment = null
        // add more if you want
        saveJson()
    }

    fun getUserServer(): String {
        return preference.getString(SERVER_KEY, "kr") ?: "kr"
    }

    fun getDbVersion(): Long {
        return when (preference.getString(SERVER_KEY, null)) {
            "jp" -> {
                preference.getLong(DB_VERSION_JP, 0)
            }
            "cn" -> {
                preference.getLong(DB_VERSION_CN, 0)
            }
            else -> {
                preference.getLong(DB_VERSION_KR, 0)
            }
        }
    }

    fun getLanguage(): String {
        return preference.getString(LANGUAGE_KEY, null) ?: "ko"
    }

    @SuppressLint("ApplySharedPref")
    fun setDbVersion(newVersion: Long, async: Boolean = true) {
        when (preference.getString(SERVER_KEY, "kr")) {
            "jp" -> {
                if (async) {
                    preference.edit().putLong(DB_VERSION_JP, newVersion).apply()
                } else {
                    preference.edit().putLong(DB_VERSION_JP, newVersion).commit()
                }
            }
            "cn" -> {
                if (async) {
                    preference.edit().putLong(DB_VERSION_CN, newVersion).apply()
                } else {
                    preference.edit().putLong(DB_VERSION_CN, newVersion).commit()
                }
            }
            "kr" -> {
                if (async) {
                    preference.edit().putLong(DB_VERSION_KR, newVersion).apply()
                } else {
                    preference.edit().putLong(DB_VERSION_KR, newVersion).commit()
                }
            }
        }
    }

    var contentsMaxLevel: Int
        get() = if (userData.contentsMaxLevel != null && userData.contentsMaxLevel.contains(getUserServer())) {
            userData.contentsMaxLevel[getUserServer()]!!
        }
        else {
            if (userData.contentsMaxLevel == null)
                userData.contentsMaxLevel = mutableMapOf()
            userData.contentsMaxLevel[getUserServer()] = DBHelper.get().areaLevelMap[DBHelper.get().maxArea]
            saveJson()
            userData.contentsMaxLevel[getUserServer()]!!
        }
        set(level) {
            userData.contentsMaxLevel[getUserServer()] = level
            saveJson()
        }

    var contentsMaxRank: Int
        get() = if (userData.contentsMaxRank != null && userData.contentsMaxRank.contains(getUserServer())) {
            userData.contentsMaxRank[getUserServer()]!!
        }
        else {
            if (userData.contentsMaxRank == null)
                userData.contentsMaxRank = mutableMapOf()
            userData.contentsMaxRank[getUserServer()] = DBHelper.get().areaRankMap[DBHelper.get().maxArea]
            saveJson()
            userData.contentsMaxRank[getUserServer()]!!
        }
        set(rank) {
            userData.contentsMaxRank[getUserServer()] = rank
            saveJson()
        }

    var contentsMaxEquipment: Int
        get() = if (userData.contentsMaxEquipment != null && userData.contentsMaxEquipment.contains(getUserServer())) {
            userData.contentsMaxEquipment[getUserServer()]!!
        }
        else {
            if (userData.contentsMaxEquipment == null)
                userData.contentsMaxEquipment = mutableMapOf()
            userData.contentsMaxEquipment[getUserServer()] = DBHelper.get().areaEquipmentMap[DBHelper.get().maxArea]
            saveJson()
            userData.contentsMaxEquipment[getUserServer()]!!
        }
        set(equipment) {
            userData.contentsMaxEquipment[getUserServer()] = equipment
            saveJson()
        }

    var contentsMaxArea: Int
        get() = if (userData.contentsMaxArea != null && userData.contentsMaxArea.contains(getUserServer())) {
            max(DBHelper.get().maxCharaContentArea, userData.contentsMaxArea[getUserServer()]!!)
        }
        else {
            if (userData.contentsMaxArea == null)
                userData.contentsMaxArea = mutableMapOf()
            userData.contentsMaxArea[getUserServer()] = DBHelper.get().maxArea
            saveJson()
            userData.contentsMaxArea[getUserServer()]!!
        }
        set(area) {
            userData.contentsMaxArea[getUserServer()] = area
            saveJson()
        }

    var betaTest: Boolean
        get() = preference.getBoolean(BETA_TEST, false)
        set(beta) {
            preference.edit().putBoolean(BETA_TEST, beta).apply()
        }

    fun getExpression(): Boolean {
        return preference.getBoolean(EXPRESSION_STYLE, false)
    }

    fun setExpression(newValue: Boolean) {
        preference.edit().putBoolean(EXPRESSION_STYLE, newValue).apply()
    }

    fun getCalendarFilter(): Boolean {
        return preference.getBoolean(CALENDAR_FILTER, false)
    }

    fun setCalendarFilter(newValue: Boolean) {
        preference.edit().putBoolean(CALENDAR_FILTER, newValue).apply()
    }

    fun reverseCalendarFilter() {
        preference.edit().putBoolean(CALENDAR_FILTER, !getCalendarFilter()).apply()
    }
}