package com.github.malitsplus.shizurunotes.ui.setting

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.github.malitsplus.shizurunotes.BuildConfig
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.App
import com.github.malitsplus.shizurunotes.common.NotificationManager
import com.github.malitsplus.shizurunotes.common.UpdateManager
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.user.UserSettings.Companion.DB_VERSION
import com.github.malitsplus.shizurunotes.user.UserSettings.Companion.FONT_SIZE
import com.jakewharton.processphoenix.ProcessPhoenix
import kotlin.concurrent.thread

class SettingFragment : PreferenceFragmentCompat() {

    override fun onResume() {
        super.onResume()
        findPreference<Preference>(DB_VERSION)?.summary = UserSettings.get().getDbVersion().toString()
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        //app版本提示
        findPreference<Preference>(UserSettings.APP_VERSION)?.apply {
            summary = BuildConfig.VERSION_NAME
            isSelectable = false
        }

        //数据库版本
        findPreference<Preference>(DB_VERSION)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                it.isEnabled = false
                UpdateManager.get().checkDatabaseVersion()
                thread(start = true){
                    Thread.sleep(5000)
                    activity?.runOnUiThread {
                        it.isEnabled = true
                    }
                }
                true
            }
        }

        //重下数据库，暂时停用
//        findPreference<Preference>("reDownloadDb")?.apply {
//            onPreferenceClickListener = Preference.OnPreferenceClickListener {
//                UpdateManager.get().forceDownloadDb()
//                true
//            }
//        }

        //日志
        findPreference<Preference>(UserSettings.LOG)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val action = SettingContainerFragmentDirections.actionNavSettingContainerToNavLog()
                findNavController().navigate(action)
                true
            }
        }

        //关于
        findPreference<Preference>(UserSettings.ABOUT)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val action = SettingContainerFragmentDirections.actionNavSettingContainerToNavSettingAbout()
                findNavController().navigate(action)
                true
            }
        }

        //语言选择框
        val languagePreference = findPreference<ListPreference>(UserSettings.LANGUAGE_KEY)
        if (languagePreference != null) {
            languagePreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                    App.localeManager.setNewLocale(
                        requireActivity().application,
                        newValue as String?
                    )
                    thread(start = true){
                        Thread.sleep(100)
                        ProcessPhoenix.triggerRebirth(activity)
                    }
                    true
                }
        }

        //服务器选择
        val serverPreference = findPreference<ListPreference>(UserSettings.SERVER_KEY)
        if (serverPreference != null) {
            serverPreference.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    thread(start = true) {
                        Thread.sleep(100)
                        activity?.runOnUiThread {
                            MaterialDialog(requireContext(), MaterialDialog.DEFAULT_BEHAVIOR)
                                .title(R.string.dialog_server_switch_title)
                                .message(R.string.dialog_server_switch_text)
                                .show {
                                    positiveButton(res = R.string.text_ok)
                                }
                        }
                    }
                    true
                }
            serverPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, _ ->
                    NotificationManager.get().cancelAllAlarm()
                    thread(start = true){
                        Thread.sleep(100)
                        ProcessPhoenix.triggerRebirth(activity)
                    }
                    true
                }
        }

        val fontSizePreference = findPreference<ListPreference>(FONT_SIZE)
        if (fontSizePreference != null) {
            fontSizePreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                    UserSettings.get().preference.edit().putString(
                        FONT_SIZE, newValue as String?).apply()
                    true
                }
        }

        // appearance
        findPreference<Preference>(UserSettings.CONTENTS_MAX_LEVEL)?.isEnabled = false
        findPreference<Preference>(UserSettings.CONTENTS_MAX_RANK)?.isEnabled = false
        findPreference<Preference>(UserSettings.CONTENTS_MAX_EQUIPMENT)?.isEnabled = false


        // register onclick event
        findPreference<Preference>(UserSettings.CONTENTS_MAX)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if (UserSettings.get().preference.getBoolean(it.key, false)) {
                    findPreference<Preference>(UserSettings.CONTENTS_MAX_LEVEL)?.isEnabled = false
                    findPreference<Preference>(UserSettings.CONTENTS_MAX_RANK)?.isEnabled = false
                    findPreference<Preference>(UserSettings.CONTENTS_MAX_EQUIPMENT)?.isEnabled = false
                    UpdateManager.get().checkContentsMax()
                }
                else {
                    UserSettings.get().contentsMaxLevel = 0
                    UserSettings.get().contentsMaxRank = 0
                    UserSettings.get().contentsMaxEquipment = 0
                    UserSettings.get().contentsMaxArea = 0

                    UpdateManager.get().checkContentsMax(true)
                }
                true
            }
        }
    }

    fun updateAppearance() {

        /*val setMax = UserSettings.get().preference.getBoolean(CONTENTS_MAX, false)

        findPreference<Preference>(CONTENTS_MAX_LEVEL)?.isEnabled = setMax
        findPreference<Preference>(CONTENTS_MAX_RANK)?.isEnabled = setMax
        findPreference<Preference>(CONTENTS_MAX_EQUIPMENT)?.isEnabled = setMax*/
    }
}