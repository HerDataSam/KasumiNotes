package com.github.malitsplus.shizurunotes.user

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.github.malitsplus.shizurunotes.data.extension.Extension
import com.github.malitsplus.shizurunotes.data.extension.ExtensionType
import com.github.malitsplus.shizurunotes.db.*
import com.github.malitsplus.shizurunotes.utils.FileUtils
import com.github.malitsplus.shizurunotes.utils.JsonUtils
import com.github.malitsplus.shizurunotes.utils.LogUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.system.measureNanoTime

class UserSettings private constructor(
    private val application: Application
) {
    companion object {
        const val LANGUAGE_KEY = "language"
        const val SERVER_KEY = "server"
        const val CUSTOM_DB_KEY = "customDBSetting"
        const val CUSTOM_DB_MEMO_KEY = "customDBMemo"
        const val HIDE_SERVER_SWITCH_HINT_KEY = "hideServerSwitchHint"
        const val EXPRESSION_STYLE = "expressionStyle2"
        const val EXPRESS_PREFAB_TIME = "expressPrefabTime"
        const val FONT_SIZE = "textSize"
        const val CONTENTS_MAX = "contentsMax"
        const val CONTENTS_MAX_LEVEL = "contentsMaxLevel"
        const val CONTENTS_MAX_RANK = "contentsMaxRank"
        const val CONTENTS_MAX_EQUIPMENT = "contentsMaxEquipment"
        const val CONTENTS_MAX_AREA = "contentsMaxArea"
        const val CONTENTS_SELECTION = "contentsSelection"
        const val EXTERNAL_URL_OR_DATA = "externalUrlOrData"
        const val EXTERNAL_DATA = "externalData"
        const val CALENDAR_FILTER = "calendarFilter"
        const val DROP_QUEST_SIMPLE = "dropQuestSimple"
        const val EXPORT_USER_DATA = "exportUserData"
        const val DELETE_USER_DATA = "deleteUserData"
        const val ADD_PASSIVE_ABILITY = "addPassiveAbility"
        const val COMPARISON_SHOW_TP = "comparisonShowTP"
        const val COMPARISON_SHOW_DEF = "comparisonShowDef"
        const val COMPARISON_SHOW_DMG = "comparisonShowDmg"
        const val SHOW_SRT_READING = "showSrtReading"
        const val LOG = "log"
        const val DB_VERSION = "dbVersion_new"
        const val DB_VERSION_JP = "dbVersion_jp"
        const val DB_VERSION_CN = "dbVersion_cn"
        const val DB_VERSION_KR = "dbVersion_kr"
        const val APP_VERSION = "appVersion"
        const val PREFAB_VERSION = "prefabVersion"
        const val BETA_TEST = "betaTest"
        const val ABOUT = "about"
        const val EXPRESSION_VALUE = 0
        const val EXPRESSION_EXPRESSION = 1
        const val EXPRESSION_ORIGINAL = 2
        const val LAST_DB_HASH = "last_db_hash"
        const val ABNORMAL_EXIT = "abnormal_exit"

        const val TARGET = "target"
        const val RECOMMEND = "recommend"

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

    private var json: String
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
        set(value) {
            try {
                application.openFileOutput(userDataFileName, Context.MODE_PRIVATE).use { fos ->
                    fos.write(value.toByteArray())
                }
                LogUtils.file(LogUtils.W, "Save $userDataFileName from another app")
            } catch (e: IOException) {
                LogUtils.file(LogUtils.E, "SaveUserJson", e.message, e.stackTrace)
            }
        }

    private val userData: UserData = if (json.isNotEmpty()) {
        JsonUtils.getBeanFromJson(json, UserData::class.java)
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

    private fun saveJsonMain() {
        val json = JsonUtils.getJsonFromBean(userData)
        try {
            application.openFileOutput(userDataFileName, Context.MODE_PRIVATE).use { fos ->
                fos.write(json.toByteArray())
            }
        } catch (e: IOException) {
            LogUtils.file(LogUtils.E, "SaveUserJson", e.message, e.stackTrace)
        }
    }

    private fun overwriteJson(newJson: String) : Boolean {
        json = newJson
        return true
    }

    fun deleteUserData() {
        userData.contentsMaxArea = null
        userData.contentsMaxLevel = null
        userData.contentsMaxRank = null
        userData.contentsMaxEquipment = null
        userData.nicknames = null
        userData.extensionMap = null
        // add more if you want
        saveJson()
    }

    fun setUserData(userDataString: String):Boolean {
        return overwriteJson(userDataString)
    }

    fun getUserData(): String {
        return json
    }

    fun getUserServer(): String {
        return preference.getString(SERVER_KEY, "kr") ?: "kr"
    }

    fun isCustomDB(): Boolean {
        return getUserServer().contains("custom")
    }

    fun getCustomDBMemo(): String {
        return preference.getString(CUSTOM_DB_MEMO_KEY, "") ?: ""
    }

    fun setCustomDBMemo(memo: String) {
        preference.edit().putString(CUSTOM_DB_MEMO_KEY, memo).apply()
    }

    fun getHideServerSwitchHint(): Boolean {
        return preference.getBoolean(HIDE_SERVER_SWITCH_HINT_KEY, false)
    }
    fun setHideServerSwitchHint(isHide: Boolean) {
        preference.edit().putBoolean(HIDE_SERVER_SWITCH_HINT_KEY, isHide).apply()
    }

    fun getDbVersion(): Long {
        return when (preference.getString(SERVER_KEY, null)) {
            "jp" -> {
                preference.getLong(DB_VERSION_JP, 0)
            }/*
            "cn" -> {
                preference.getLong(DB_VERSION_CN, 0)
            }*/
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
            /*
            "cn" -> {
                if (async) {
                    preference.edit().putLong(DB_VERSION_CN, newVersion).apply()
                } else {
                    preference.edit().putLong(DB_VERSION_CN, newVersion).commit()
                }
            }*/
            "kr" -> {
                if (async) {
                    preference.edit().putLong(DB_VERSION_KR, newVersion).apply()
                } else {
                    preference.edit().putLong(DB_VERSION_KR, newVersion).commit()
                }
            }
        }
    }

    fun getPrefabVersion(): Long {
        return preference.getLong(PREFAB_VERSION, 0)
    }

    @SuppressLint("ApplySharedPref")
    fun setPrefabVersion(prefabVersion: Long, async: Boolean = true) {
        if (async)
            preference.edit().putLong(PREFAB_VERSION, prefabVersion).apply()
        else
            preference.edit().putLong(PREFAB_VERSION, prefabVersion).commit()
    }

    fun getDBHash(): String {
        return preference.getString(LAST_DB_HASH, "0") ?: "0"
    }
    fun setDBHash(newValue: String) {
        preference.edit().putString(LAST_DB_HASH, newValue).apply()
    }

    fun checkContentsMax() {
        if (contentsMaxLevel < DBHelper.get().maxCharaContentsLevel ||
            contentsMaxRank < DBHelper.get().maxCharaContentsRank ||
            (contentsMaxRank == DBHelper.get().maxCharaContentsRank && contentsMaxEquipment < DBHelper.get().maxCharaContentsEquipment)) {
            contentsMaxArea = DBHelper.get().maxCharaContentArea
            contentsMaxLevel = DBHelper.get().maxCharaContentsLevel
            contentsMaxRank = DBHelper.get().maxCharaContentsRank
            contentsMaxEquipment = DBHelper.get().maxCharaContentsEquipment
        }
    }

    var contentsMaxLevel: Int
        get() = if (userData.contentsMaxLevel != null && userData.contentsMaxLevel.contains(getUserServer()) && getUserServer() == "kr") {
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
        get() = if (userData.contentsMaxRank != null && userData.contentsMaxRank.contains(getUserServer()) && getUserServer() == "kr") {
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
        get() = if (userData.contentsMaxEquipment != null && userData.contentsMaxEquipment.contains(getUserServer()) && getUserServer() == "kr") {
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
        get() = if (userData.contentsMaxArea != null && userData.contentsMaxArea.contains(getUserServer()) && getUserServer() == "kr") {
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

    fun getExpression(): Int {
        return preference.getString(EXPRESSION_STYLE, "0")?.toInt() ?: 0
    }

    fun setExpression(newValue: Int) {
        preference.edit().putString(EXPRESSION_STYLE, newValue.toString()).apply()
    }

    fun getShowTP(): Boolean {
        return preference.getBoolean(COMPARISON_SHOW_TP, true)
    }

    fun setShowTP(newValue: Boolean) {
        preference.edit().putBoolean(COMPARISON_SHOW_TP, newValue).apply()
    }

    fun getShowDef(): Boolean {
        return preference.getBoolean(COMPARISON_SHOW_DEF, false)
    }

    fun setShowDef(newValue: Boolean) {
        preference.edit().putBoolean(COMPARISON_SHOW_DEF, newValue).apply()
    }

    fun getShowDmg(): Boolean {
        return preference.getBoolean(COMPARISON_SHOW_DMG, false)
    }

    fun setShowDmg(newValue: Boolean) {
        preference.edit().putBoolean(COMPARISON_SHOW_DMG, newValue).apply()
    }

    fun getShowSrtReading(): Boolean {
        return preference.getBoolean(SHOW_SRT_READING, false)
    }

    fun setShowSrtReading(newValue: Boolean) {
        preference.edit().putBoolean(SHOW_SRT_READING, newValue).apply()
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

    fun getAbnormalExit(): Boolean {
        return preference.getBoolean(ABNORMAL_EXIT, false)
    }

    fun setAbnormalExit(value: Boolean) {
        preference.edit().putBoolean(ABNORMAL_EXIT, value).apply()
    }

    fun getExpressPassiveAbility(): Boolean {
        return preference.getBoolean(ADD_PASSIVE_ABILITY, false)
    }

    fun setExpressPassiveAbility(value: Boolean) {
        preference.edit().putBoolean(ADD_PASSIVE_ABILITY, value).apply()
    }

    fun getExpressPrefabTime(): Boolean {
        return preference.getBoolean(EXPRESS_PREFAB_TIME, true)
    }

    fun setExpressPrefabTime(value: Boolean) {
        preference.edit().putBoolean(EXPRESS_PREFAB_TIME, value).apply()
    }

    /* My Chara
    ** load and save
     */
    fun loadCharaData(suffix: String = ""): MutableList<UserData.MyCharaData> {
        if (userData.myCharaData.isNullOrEmpty()) {
            userData.myCharaData = mutableMapOf()
            saveJson()
        }
        if (userData.myCharaData[getUserServer() + suffix].isNullOrEmpty()) {
            userData.myCharaData[getUserServer() + suffix] = mutableListOf()
            saveJson()
        }
        return userData.myCharaData[getUserServer() + suffix] ?: mutableListOf()
    }

    fun loadCharaData(charaId: Int, suffix: String = ""): UserData.MyCharaData? {
        return loadCharaData(suffix).find {
            it.charaId == charaId
        }
    }

    fun saveCharaData(charaId: Int, rarity: Int, level: Int, rank: Int,
                      equipment: MutableList<Int>, uniqueEquipment: Int,
                      loveLevel: Int, skillLevels: MutableList<Int>,
                      isBookmarkLocked: Boolean, suffix: String = "") {
        val list = loadCharaData(suffix)
        list.find {
            it.charaId == charaId
        }?.apply {
            this.rarity = rarity
            this.level = level
            this.rank = rank
            this.equipment = equipment
            this.uniqueEquipment = uniqueEquipment
            this.loveLevel = loveLevel
            this.skillLevels = skillLevels
            this.isBookmarkLocked = isBookmarkLocked
        } ?: run {
            list.add(UserData.MyCharaData(charaId, rarity, level, rank, equipment, uniqueEquipment, loveLevel, skillLevels, isBookmarkLocked))
        }

        userData.myCharaData[getUserServer() + suffix] = list
        saveJsonMain()
    }

    fun removeCharaData(charaId: Int, suffix: String = "") {
        val list = loadCharaData(suffix)
        val data = list.find {
            it.charaId == charaId
        }
        if (data != null) {
            list.remove(data)
            userData.myCharaData[getUserServer() + suffix] = list
            saveJsonMain()
        }
    }

    fun getDropQuestSimple(): Boolean {
        return preference.getBoolean(DROP_QUEST_SIMPLE, false)
    }

    fun setDropQuestSimple(newValue: Boolean) {
        preference.edit().putBoolean(DROP_QUEST_SIMPLE, newValue).apply()
    }

    fun reverseDropQuestSimple() {
        preference.edit().putBoolean(DROP_QUEST_SIMPLE, !getDropQuestSimple()).apply()
    }

    var lastInfoMessage: String
        get() {
            return if (userData.lastInfoMessage.isNullOrEmpty())
                ""
            else
                userData.lastInfoMessage
        }
        set(value) {
            userData.lastInfoMessage = value
            saveJson()
        }

    class NicknameData (
        val shortestNickname: String,
        val shortNickname: String
    )

    fun saveExternalData(data: String): Boolean {
        val dataTypeRegex = """\[(\D+)]""".toRegex()
        val dataType = dataTypeRegex.find(data)
        var valid = true

        // nickname part
        if (dataType!!.destructured.component1() == "닉네임") {
            val values = data.split("[닉네임]").last()
            val titleRegex = """-Title: *(\S.+)""".toRegex()
            val madeByRegex = """-MadeBy: *(\S.+)""".toRegex()
            val versionRegex = """-Version: *(\S.+)""".toRegex()

            val extension = UserData.Extension(
                titleRegex.find(values)?.destructured?.component1() ?: "",
                madeByRegex.find(values)?.destructured?.component1() ?: "",
                versionRegex.find(values)?.destructured?.component1() ?: ""
            )

            val nicknameData = mutableMapOf<Int, NicknameData>()

            values.split("\n").forEach {
                if (valid) {
                    val nicknameRegex = """#(\d+):.\((\S+)\).(\S+)""".toRegex()
                    nicknameRegex.find(it)?.destructured?.let { matchResult ->
                        val unitId = matchResult.component1().toInt()
                        valid = valid && (unitId in 1001..1999)

                        val shortest = matchResult.component2()
                        valid = valid && (shortest.length in 1..5)

                        val short = matchResult.component3()
                        valid = valid && (short.isNotEmpty())

                        if (valid) {
                            nicknameData[unitId] = NicknameData(shortest, short)
                        }
                    }
                }
            }

            if (valid) {
                // save extension info
                saveExtensionInfo("Nickname", extension)
                // save nickname
                nicknames = nicknameData
            }
        }/*
        else if (dataType.destructured.component1() == "추천랭크") {
            val values = data.split("[추천랭크]").last()
            val titleRegex = """-Title: *(\S.+)""".toRegex()
            val madeByRegex = """-MadeBy: *(\S.+)""".toRegex()
            val versionRegex = """-Version: *(\S.+)""".toRegex()

            val extension = UserData.Extension(
                titleRegex.find(values)?.destructured?.component1() ?: "",
                madeByRegex.find(values)?.destructured?.component1() ?: "",
                versionRegex.find(values)?.destructured?.component1() ?: ""
            )
        }*/

        return valid
    }

    fun saveExtensionInfo(extension: String, info: UserData.Extension) {
        if (userData.extensionMap.isNullOrEmpty()) {
            userData.extensionMap = mutableMapOf()
            saveJsonMain()
        }
        userData.extensionMap[extension] = info
        saveJsonMain()
    }

    fun loadExtensionInfo(extension: String = ""): List<Extension> {
        val extensionList = mutableListOf<Extension>()
        if (extension.isEmpty()) {
            if (!userData.extensionMap.isNullOrEmpty()) {
                userData.extensionMap.forEach {
                    extensionList.add(
                        Extension(
                            ExtensionType.valueOf(it.key),
                            it.value.title, it.value.madeBy, it.value.version
                        )
                    )
                }
            }
        } else {
            userData.extensionMap[extension]?.let {
                extensionList.add(Extension(ExtensionType.valueOf(extension),
                    it.title, it.madeBy, it.version))
            }
        }
        return extensionList
    }

    var nicknames: Map<Int, NicknameData>
        get() {
            return if (userData.nicknames.isNullOrEmpty()) {
                emptyMap()
            } else {
                val nicknameList = mutableMapOf<Int, NicknameData>()
                userData.nicknames.forEach {
                    nicknameList[it.key] = NicknameData(it.value.shortestNickname, it.value.shortNickname)
                }
                nicknameList
            }
        }
        set(value) {
            if (userData.nicknames.isNullOrEmpty()) {
                userData.nicknames = mutableMapOf()
                saveJsonMain()
            }
            userData.nicknames.clear()
            value.forEach {
                userData.nicknames[it.key] = UserData.Nickname(it.value.shortestNickname, it.value.shortNickname)
            }
            saveJsonMain()
        }

    val nicknameBuilder: String
        get() {
            var string = ""
            val extension = userData.extensionMap["Nickname"]!!
            string += "[닉네임]\n"
            string += "-Title: ${extension.title}\n"
            string += "-MadeBy: ${extension.madeBy}\n"
            string += "-Version: ${extension.version}\n"

            userData.nicknames.forEach {
                string += "#${it.key}: (${it.value.shortestNickname}) ${it.value.shortNickname}\n"
            }
            return string
        }

    var selectedExtension: Extension? = null

    fun test() {
        val elapsed: Long = measureNanoTime {

        }
        println(elapsed / 1000000000.0)
    }
}