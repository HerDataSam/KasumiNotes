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
        const val ADD_PASSIVE_ABILITY = "addPassiveAbility"
        const val LOG = "log"
        const val DB_VERSION = "dbVersion_new"
        const val DB_VERSION_JP = "dbVersion_jp"
        const val DB_VERSION_CN = "dbVersion_cn"
        const val DB_VERSION_KR = "dbVersion_kr"
        const val APP_VERSION = "appVersion"
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
        get() = userData.lastEquipmentIds
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

    fun getUserServer(): String {
        return preference.getString(SERVER_KEY, "jp") ?: "jp"
    }

    fun getDbVersion(): Long {
        return if (preference.getString(SERVER_KEY, null) == "cn") {
            preference.getLong(DB_VERSION_CN, 0)
        } else {
            preference.getLong(DB_VERSION_JP, 0)
        }
    }

    fun getLanguage(): String {
        return preference.getString(LANGUAGE_KEY, null) ?: "ja"
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

    //fun setContentsMaxLevel(level: Int) {
    //
    //}

    var contentsMaxLevelString: String
        get() = preference.getString(CONTENTS_MAX_LEVEL, "0") ?: (DBHelper.get().maxCharaLevel- 1).toString()
        set(level) {
            if (level == "0") {
                preference.edit().putString(CONTENTS_MAX_LEVEL, (DBHelper.get().maxCharaLevel - 1).toString()).apply()
            } else {
                preference.edit().putString(CONTENTS_MAX_LEVEL, level).apply()
            }
        }

    var contentsMaxRankString: String
        get() = preference.getString(CONTENTS_MAX_RANK, "0") ?: DBHelper.get().maxCharaRank.toString()
        set(rank) {
            if (rank == "0"){
                preference.edit().putString(CONTENTS_MAX_RANK, DBHelper.get().maxCharaRank.toString()).apply()
            } else {
                preference.edit().putString(CONTENTS_MAX_RANK, rank).apply()
            }
        }

    var contentsMaxEquipmentString: String
        get() = preference.getString(CONTENTS_MAX_EQUIPMENT, "0") ?: "3"
        set(equipment) {
            if (equipment == "0") {
                preference.edit().putString(CONTENTS_MAX_EQUIPMENT, "3").apply()
            } else {
                preference.edit().putString(CONTENTS_MAX_EQUIPMENT, equipment).apply()
            }
        }

    var contentsMaxAreaString: String
        get() = preference.getString(CONTENTS_MAX_AREA, "0") ?: DBHelper.get().maxArea.toString()
        set(area) {
            if (area == "0") {
                preference.edit().putString(CONTENTS_MAX_AREA, DBHelper.get().maxArea.toString()).apply()
            } else {
                preference.edit().putString(CONTENTS_MAX_AREA, area).apply()
            }
        }

    var contentsMaxLevel: Int
        get() = contentsMaxLevelString.toInt()
        set(level) {
            contentsMaxLevelString = level.toString()
        }

    var contentsMaxRank: Int
        get() = contentsMaxRankString.toInt()
        set(rank) {
            contentsMaxRankString = rank.toString()
        }

    var contentsMaxEquipment: Int
        get() = contentsMaxEquipmentString.toInt()
        set(equipment) {
            contentsMaxEquipmentString = equipment.toString()
        }

    var contentsMaxArea: Int
        get() = contentsMaxAreaString.toInt()
        set(area) {
            contentsMaxAreaString = area.toString()
        }
}