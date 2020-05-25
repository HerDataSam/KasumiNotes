package com.github.malitsplus.shizurunotes.ui

import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.*
import com.github.malitsplus.shizurunotes.databinding.ActivityMainBinding
import com.github.malitsplus.shizurunotes.db.DBHelper
import com.github.malitsplus.shizurunotes.ui.calendar.CalendarViewModel
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelEquipment
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelQuest
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.utils.FileUtils
import com.google.android.material.snackbar.Snackbar
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(),
    UpdateManager.IActivityCallBack,
    SharedViewModelChara.MasterCharaCallBack
{
    private lateinit var sharedEquipment: SharedViewModelEquipment
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var sharedClanBattle: SharedViewModelClanBattle
    private lateinit var sharedQuest: SharedViewModelQuest
    private lateinit var binding: ActivityMainBinding

    //private var doubleBackToExitPressedOnce = false

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(App.localeManager.setLocale(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        UpdateManager.with(this).setIActivityCallBack(this)
        setDefaultFontSizePreference()
        initSharedViewModels()
        if (checkDbFile()) {
            loadData()
        } else {
            checkUpdate()
            sharedChara.charaList.value = mutableListOf()
        }
    }

    private fun checkDbFile(): Boolean {
        return FileUtils.checkFileAndSize(
            FileUtils.getDbFilePath(), 50)
    }

    private fun loadData() {
        sharedEquipment.loadData()
        NotificationManager.get().loadData()
    }

    private fun checkUpdate() {
        UpdateManager.get().checkAppVersion(true)
    }

    private fun initSharedViewModels() {
        sharedEquipment = ViewModelProvider(this)[SharedViewModelEquipment::class.java].apply {
            equipmentMap.observe(this@MainActivity, Observer {
                if (it.isNotEmpty()) {
                    sharedChara.loadData(it)
                }
            })
        }
        sharedChara = ViewModelProvider(this)[SharedViewModelChara::class.java].apply {
            callBack = this@MainActivity
        }
        sharedClanBattle = ViewModelProvider(this)[SharedViewModelClanBattle::class.java]
        sharedQuest = ViewModelProvider(this)[SharedViewModelQuest::class.java]
    }

    override fun charaLoadFinished() {
        checkUpdate()
    }

    override fun dbDownloadFinished() {
        thread(start = true) {
            //先关闭所有连接，释放sqliteHelper类中的所有旧版本数据库缓存
            DBHelper.get().close()
            synchronized(DBHelper::class.java){
                UpdateManager.get().doDecompress()
            }
        }
    }

    override fun dbUpdateFinished() {
        clearData()
        loadData()
    }

    override fun showSnackBar(@StringRes messageRes: Int) {
        Snackbar.make(binding.activityFrame, messageRes, Snackbar.LENGTH_LONG).show()
    }

    override fun updateContentsMaxSharedChara() {
        sharedChara.loadCharaMaxData()
    }

    private fun clearData() {
        //不使用clear，直接赋空值以触发订阅者接收事件
        sharedEquipment.equipmentMap.value = mutableMapOf()
        sharedChara.charaList.value = mutableListOf()
        sharedClanBattle.periodList.value = mutableListOf()
        sharedClanBattle.dungeonList = mutableListOf()
        sharedQuest.questList.value = mutableListOf()
        sharedEquipment.selectedDrops.value = mutableListOf()
        with(ViewModelProvider(this)[CalendarViewModel::class.java]) {
            scheduleMap.clear()
            calendarMap.clear()
        }
    }

    private fun setDefaultFontSizePreference() {
        val pref = UserSettings.get().preference.getString(UserSettings.FONT_SIZE, "M") ?: "M"
        UserSettings.get().preference.edit().putString(UserSettings.FONT_SIZE, pref).apply()
    }

    /*
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce || supportFragmentManager.backStackEntryCount != 0) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        //showSnackBar(R.string.double_back_to_exit)
        Toast.makeText(this, I18N.getString(R.string.double_back_to_exit), Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
    */
}
