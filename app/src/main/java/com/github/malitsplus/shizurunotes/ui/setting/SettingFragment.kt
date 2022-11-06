package com.github.malitsplus.shizurunotes.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.github.malitsplus.shizurunotes.BuildConfig
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.App
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.NotificationManager
import com.github.malitsplus.shizurunotes.common.UpdateManager
import com.github.malitsplus.shizurunotes.db.DBHelper
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.user.UserSettings.Companion.DB_VERSION
import com.github.malitsplus.shizurunotes.user.UserSettings.Companion.FONT_SIZE
import com.jakewharton.processphoenix.ProcessPhoenix
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread
import kotlin.math.max

class SettingFragment : PreferenceFragmentCompat() {
    private lateinit var sharedChara: SharedViewModelChara

    override fun onResume() {
        super.onResume()
        findPreference<Preference>(DB_VERSION)?.summary = UserSettings.get().getDbVersion().toString()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onDetach() {
        (activity as MainActivity).showBottomNavigation()
        super.onDetach()
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        updateListPreference()
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]

        //app版本提示
        findPreference<Preference>(UserSettings.APP_VERSION)?.apply {
            summary = BuildConfig.VERSION_NAME
            isSelectable = false
        }

        //数据库版本
        findPreference<Preference>(DB_VERSION)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                it.isEnabled = false
                UpdateManager.get().checkDatabaseVersion(true)
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

        // custom db setting
        findPreference<Preference>(UserSettings.CUSTOM_DB_KEY)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val action = SettingContainerFragmentDirections.actionNavSettingContainerToNavCustomDbSetting()
                findNavController().navigate(action)
                true
            }
        }

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
                    if (!UserSettings.get().getHideServerSwitchHint()) {
                        thread(start = true) {
                            Thread.sleep(100)
                            activity?.runOnUiThread {
                                MaterialDialog(requireContext(), MaterialDialog.DEFAULT_BEHAVIOR)
                                    .title(R.string.dialog_server_switch_title)
                                    .message(R.string.dialog_server_switch_text)
                                    .show {
                                        checkBoxPrompt(R.string.text_do_not_show_again) { checked ->
                                            UserSettings.get().setHideServerSwitchHint(checked)
                                        }
                                        positiveButton(res = R.string.text_ok)                                    }
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

        findPreference<ListPreference>(UserSettings.CONTENTS_SELECTION)?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                val newArea = (newValue as String).toInt()
                val newLevel = DBHelper.get().areaLevelMap[newArea] ?: error("")
                val newRank = DBHelper.get().areaRankMap[newArea] ?: error("")
                val newEquipmentNumber = DBHelper.get().areaEquipmentMap[newArea] ?: error("")
                UserSettings.get().contentsMaxArea = newArea
                UserSettings.get().contentsMaxLevel = newLevel
                UserSettings.get().contentsMaxRank = newRank
                UserSettings.get().contentsMaxEquipment = newEquipmentNumber

                sharedChara.loadCharaMaxData()

                this.summary = if (newArea == DBHelper.get().maxCharaContentArea)
                    I18N.getString(R.string.setting_contents_now)
                else
                    I18N.getString(R.string.setting_selected_contents, newArea, newLevel, newRank, newEquipmentNumber)
                true
            }
        }

        findPreference<Preference>(UserSettings.EXTERNAL_URL_OR_DATA)?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue: Any? ->
                val data = newValue as String
                if (data.isNotEmpty()) {
                    UpdateManager.get().setInputString(data)
                    UpdateManager.get().getInputFromUrl()
                }
                true
            }
        }

        findPreference<Preference>(UserSettings.EXTERNAL_DATA)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val action = SettingContainerFragmentDirections.actionNavSettingContainerToNavExtensionList()
                findNavController().navigate(action)
                true
            }
        }

        findPreference<Preference>(UserSettings.EXPORT_IMPORT_USER_DATA)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val action = SettingContainerFragmentDirections.actionNavSettingContainerToNavUserSettings()
                findNavController().navigate(action)
                true
            }
        }

        findPreference<Preference>(UserSettings.DELETE_USER_DATA)?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                thread(start = true) {
                    Thread.sleep(100)
                    activity?.runOnUiThread {
                        MaterialDialog(requireContext(), MaterialDialog.DEFAULT_BEHAVIOR)
                            .title(R.string.setting_delete_user_data)
                            .message(R.string.setting_delete_user_data_summary)
                            .show {
                                positiveButton(res = R.string.text_delete) {
                                    UserSettings.get().deleteUserData()
                                    updateListPreference()
                                }
                                negativeButton(res = R.string.text_deny)
                            }
                    }
                }
                true
            }
        }

        findPreference<Preference>(UserSettings.BETA_TEST)?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                if ((newValue as Boolean?)!!) {
                    thread(start = true) {
                        Thread.sleep(100)
                        activity?.runOnUiThread {
                            MaterialDialog(requireContext(), MaterialDialog.DEFAULT_BEHAVIOR)
                                .title(R.string.setting_beta_test)
                                .message(R.string.setting_beta_test_alert)
                                .show {
                                    positiveButton(res = R.string.text_ok)
                                }
                        }
                    }
                }
                UserSettings.get().betaTest = newValue as Boolean
                true
            }
        }

        findPreference<Preference>(UserSettings.DETAILED_MODE)?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                if ((newValue as Boolean?)!!) {
                    thread(start = true) {
                        Thread.sleep(100)
                        activity?.runOnUiThread {
                            MaterialDialog(requireContext(), MaterialDialog.DEFAULT_BEHAVIOR)
                                .title(R.string.setting_detailed_mode)
                                .message(R.string.setting_detailed_mode_alert)
                                .show {
                                    positiveButton(res = R.string.text_ok)
                                }
                        }
                    }
                }
                UserSettings.get().betaTest = newValue as Boolean
                true
            }
        }

        findPreference<Preference>(UserSettings.UPDATE_PREFAB_TIME)?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                UserSettings.get().setUpdatePrefabTime(newValue as Boolean)
                if ((newValue as Boolean?)!!) {
                    UpdateManager.get().checkPrefab()
                }
                true
            }
        }
    }

    fun updateListPreference() {
        val dateTimeFormat = DateTimeFormatter.ofPattern(I18N.getString(R.string.db_date_format))
        var areaTime = DBHelper.get().areaTimeMap
        areaTime = areaTime.toSortedMap()

        val entryString: MutableList<String> = mutableListOf()
        val entryValueString: MutableList<String> = mutableListOf()

        areaTime.forEach {
            if (it.key == DBHelper.get().maxCharaContentArea)
                entryString.add(I18N.getString(R.string.setting_contents_now))
            else
                entryString.add(String.format(I18N.getString(R.string.setting_area_string),
                    it.key, LocalDateTime.parse(it.value, dateTimeFormat).monthValue))
            entryValueString.add(it.key.toString())
        }
        findPreference<ListPreference>(UserSettings.CONTENTS_SELECTION)?.let {
            if (UserSettings.get().getUserServer() == "kr") {
                val currentIndex =
                    max(entryValueString.indexOf(UserSettings.get().contentsMaxArea.toString()), 0)
                it.entries = entryString.toTypedArray()
                it.entryValues = entryValueString.toTypedArray()
                it.setValueIndex(currentIndex)

                it.summary = if (currentIndex == 0)
                    I18N.getString(R.string.setting_contents_now)
                else
                    I18N.getString(
                        R.string.setting_selected_contents,
                        UserSettings.get().contentsMaxArea,
                        UserSettings.get().contentsMaxLevel,
                        UserSettings.get().contentsMaxRank,
                        UserSettings.get().contentsMaxEquipment
                    )
            }
            else {
                findPreference<PreferenceCategory>("contents_display_category")?.removePreference(it)
            }
        }
    }
}