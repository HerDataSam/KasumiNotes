package com.github.malitsplus.shizurunotes.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.*
import com.github.malitsplus.shizurunotes.databinding.ActivityMainBinding
import com.github.malitsplus.shizurunotes.db.DBHelper
import com.github.malitsplus.shizurunotes.service.FloatingService
import com.github.malitsplus.shizurunotes.ui.calendar.CalendarViewModel
import com.github.malitsplus.shizurunotes.ui.shared.*
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

    private lateinit var floatingService: FloatingService
    var isServiceActive = false

    val onServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceActive = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as FloatingService.LocalBinder
            floatingService = binder.getService()
            isServiceActive = true
        }
    }

    override fun onPause() {
        super.onPause()
        val floating = Intent(applicationContext, FloatingService::class.java)
        bindService(floating, onServiceConnection, Context.BIND_AUTO_CREATE)

        //floatingService.init()
    }

    override fun onResume() {
        super.onResume()
        if (isServiceActive) {
            unbindService(onServiceConnection)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(App.localeManager.setLocale(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupViews()
        checkPermission()

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

    override fun onDestroy() {
        super.onDestroy()
        unbindService(onServiceConnection)
        isServiceActive = false
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

    private fun clearData() {
        //不使用clear，直接赋空值以触发订阅者接收事件
        sharedEquipment.equipmentMap.value = mutableMapOf()
        sharedChara.charaList.value = mutableListOf()
        sharedClanBattle.periodList.value = mutableListOf()
        sharedClanBattle.dungeonList = mutableListOf()
        sharedQuest.questList.value = mutableListOf()
        sharedEquipment.selectedDrops.value = mutableListOf()
        ViewModelProvider(this)[SharedViewModelHatsune::class.java].hatsuneStageList.value = listOf()
        with(ViewModelProvider(this)[CalendarViewModel::class.java]) {
            scheduleMap.clear()
            calendarMap.clear()
        }
    }

    private fun setDefaultFontSizePreference() {
        val pref = UserSettings.get().preference.getString(UserSettings.FONT_SIZE, "M") ?: "M"
        UserSettings.get().preference.edit().putString(UserSettings.FONT_SIZE, pref).apply()
    }

    // views
    fun setupViews() {
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavView.setupWithNavController(navController)
    }

    fun showBottomNavigation()
    {
        binding.bottomNavView.visibility = View.VISIBLE
    }

    fun hideBottomNavigation()
    {
        binding.bottomNavView.visibility = View.GONE
    }

    fun checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 1)
        } else {
            //Log.e("myLog", "startService")
            //val floating = Intent(applicationContext, FloatingService::class.java)
            //bindService(floating, onServiceConnection, Context.BIND_AUTO_CREATE)
            //startService(floating)
        }
    }

    fun startService() {
        val floatingService = Intent(applicationContext, FloatingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(floatingService)
        }
    }

    fun stopService() {
        stopService(Intent(applicationContext, FloatingService::class.java))
    }

}
