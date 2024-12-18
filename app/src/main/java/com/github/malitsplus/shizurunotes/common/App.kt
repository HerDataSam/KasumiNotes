package com.github.malitsplus.shizurunotes.common

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.github.malitsplus.shizurunotes.db.DBExtensionRepository
import com.github.malitsplus.shizurunotes.db.DBHelper
import com.github.malitsplus.shizurunotes.db.ExtensionDB
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.utils.Utils
import java.lang.ClassCastException

class App : Application() {
    companion object {
        lateinit var localeManager: LocaleManager

        lateinit var dbExtension: ExtensionDB
        lateinit var dbExtensionRepository: DBExtensionRepository

    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        createNotificationChannel()
        initSingleton()
    }

    override fun attachBaseContext(base: Context) {
        localeManager =
            LocaleManager(base)
        //dbExtension = ExtensionDB.getDB(base)
        //dbExtensionRepository = DBExtensionRepository(dbExtension.actionPrefabDao())
        super.attachBaseContext(localeManager.setLocale(base))
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelDefault = NotificationChannel(NOTIFICATION_CHANNEL_DEFAULT, "important", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "important notification"
            }
            val channelLow = NotificationChannel(NOTIFICATION_CHANNEL_LOW, "regular", NotificationManager.IMPORTANCE_LOW).apply {
                description = "regular notification"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelDefault)
            notificationManager.createNotificationChannel(channelLow)
        }
    }

    private fun initSingleton() {
        Utils.setApp(this)
        UserSettings.with(this)
        initUserServer()
        DBHelper.with(this)
        ResourceManager.with(this)
        I18N.application = this
        com.github.malitsplus.shizurunotes.common.NotificationManager.with(this)
    }

    private fun initUserServer() {
        when (UserSettings.get().preference.getString(UserSettings.SERVER_KEY, "kr")) {
            "jp" -> {
                Statics.DB_FILE_NAME = Statics.DB_FILE_NAME_JP
                Statics.DB_FILE_NAME_COMPRESSED = Statics.DB_FILE_NAME_COMPRESSED_JP
                Statics.LATEST_VERSION_URL = Statics.LATEST_VERSION_URL_JP
                Statics.DB_FILE_URL = Statics.DB_FILE_URL_JP
            }
            "custom" -> {
                Statics.DB_FILE_NAME = Statics.DB_FILE_NAME_CUSTOM
                Statics.DB_FILE_NAME_COMPRESSED = Statics.DB_FILE_NAME_COMPRESSED_CUSTOM
                Statics.LATEST_VERSION_URL = Statics.LATEST_VERSION_URL_CUSTOM
                Statics.DB_FILE_URL = Statics.DB_FILE_URL_CUSTOM
            }
            else -> {
                Statics.DB_FILE_NAME = Statics.DB_FILE_NAME_KR
                Statics.DB_FILE_NAME_COMPRESSED = Statics.DB_FILE_NAME_COMPRESSED_KR
                Statics.LATEST_VERSION_URL = Statics.LATEST_VERSION_URL_KR
                Statics.DB_FILE_URL = Statics.DB_FILE_URL_KR
                //Statics.CONTENTS_MAX_URL = Statics.CONTENTS_MAX_URL_KR
            }
        }
    }
}