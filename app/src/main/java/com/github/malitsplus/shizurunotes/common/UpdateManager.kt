package com.github.malitsplus.shizurunotes.common

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.github.malitsplus.shizurunotes.BuildConfig
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N.application
import com.github.malitsplus.shizurunotes.db.ActionPrefab
import com.github.malitsplus.shizurunotes.db.DBExtensionRepository
import com.github.malitsplus.shizurunotes.db.ExtensionDB
import com.github.malitsplus.shizurunotes.db.RawSkillPrefab
import com.github.malitsplus.shizurunotes.user.UserData
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.utils.BrotliUtils
import com.github.malitsplus.shizurunotes.utils.FileUtils
import com.github.malitsplus.shizurunotes.utils.JsonUtils
import com.github.malitsplus.shizurunotes.utils.LogUtils
import com.google.gson.JsonSyntaxException
import okhttp3.*
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

class UpdateManager private constructor(
    private val mContext: Context)
{
    companion object {
        private const val UPDATE_CHECK_COMPLETED = 1
        private const val UPDATE_DOWNLOADING = 2
        private const val UPDATE_DOWNLOAD_ERROR = 3
        private const val UPDATE_DOWNLOAD_COMPLETED = 4
        private const val UPDATE_COMPLETED = 5
        private const val UPDATE_DOWNLOAD_CANCELED = 6
        private const val UPDATE_URL_LOAD_ERROR = 7
        private const val UPDATE_URL_LOAD_COMPLETED = 8
        private const val UPDATE_PREFAB_CHECK = 9
        private const val UPDATE_PREFAB_DOWNLOAD_ERROR = 10
        private const val UPDATE_PREFAB_DOWNLOAD_COMPLETED = 11
        private const val UPDATE_PREFAB_COMPLETE = 12
        private const val APP_UPDATE_CHECK_COMPLETED = 15
        private lateinit var updateManager: UpdateManager

        fun with(context: Context): UpdateManager{
            updateManager = UpdateManager(context)
            return updateManager
        }

        fun get(): UpdateManager{
            return updateManager
        }
    }

    private var appHasNewVersion = false
    private var appVersionJsonInstance: AppVersionJson? = null
    private var serverVersion: Long = 0
    private var prefabVersion: Long = 0
    private var hasNewPrefab = false
    private var progress = 0
    private var hasNewVersion = false
    private var forceUpdate = false
    private val canceled = false
    val callBack: UpdateCallBack
    private val versionInfo: String? = null
    private var progressDialog: MaterialDialog? = null
    private var maxLength = 0

    init {
        callBack = object: UpdateCallBack {
            /***
             * APP更新检查完成，弹出更新确认对话框
             */
            override fun appCheckUpdateCompleted() {
                if (appHasNewVersion) {
                    val log = when (UserSettings.get().preference.getString(UserSettings.LANGUAGE_KEY, "kr")){
                        "zh" -> appVersionJsonInstance?.messageZh
                        "ja" -> appVersionJsonInstance?.messageJa
                        else -> appVersionJsonInstance?.messageKr
                    }
                    MaterialDialog(mContext, MaterialDialog.DEFAULT_BEHAVIOR)
                        .title(text = I18N.getString(R.string.app_full_name) + "v" + appVersionJsonInstance?.versionName)
                        .message(text = log)
                        .cancelOnTouchOutside(false)
                        .show {
                            positiveButton(res = R.string.dialog_confirm) {
                                downloadApp()
                            }
                            negativeButton(res = R.string.dialog_cancel) {
                                checkDatabaseVersion()
                            }
                        }
                } else {
                    checkDatabaseVersion()
                }

                val info = when (UserSettings.get().preference.getString(UserSettings.LANGUAGE_KEY, "kr")){
                    "zh" -> appVersionJsonInstance?.infoZh
                    "ja" -> appVersionJsonInstance?.infoJa
                    else -> appVersionJsonInstance?.infoKr
                }
                if (!info.isNullOrEmpty() && info != UserSettings.get().lastInfoMessage) {
                    MaterialDialog(mContext, MaterialDialog.DEFAULT_BEHAVIOR)
                        .title(text = I18N.getString(R.string.external_data))
                        .message(text = info)
                        .show {
                            negativeButton(res = R.string.text_do_not_show_again) {
                                UserSettings.get().lastInfoMessage = info
                            }
                            positiveButton(res = R.string.text_ok)
                        }
                }
            }

            /***
             * 数据库检查更新完成，弹出更新确认对话框
             */
            override fun dbCheckUpdateCompleted(hasUpdate: Boolean, updateInfo: CharSequence?) {
                if (hasUpdate) {
                    LogUtils.file(LogUtils.I, "New db version $serverVersion determined.")
                    MaterialDialog(mContext, MaterialDialog.DEFAULT_BEHAVIOR)
                        .title(res = R.string.db_update_dialog_title)
                        .message(res = R.string.db_update_dialog_text)
                        .cancelOnTouchOutside(false)
                        .show {
                            positiveButton(res = R.string.dialog_confirm) {
                                downloadDB(false)
                            }
                            negativeButton(res = R.string.dialog_cancel) {
                                LogUtils.file(LogUtils.I, "Canceled download db version$serverVersion.")
                            }
                        }
                }
                else {
                    checkPrefab()
                }
                getInputFromUrl()
            }

            /***
             * 更新数据库时进度条变化
             */
            override fun dbDownloadProgressChanged(progress: Int, maxLength: Int) {
                if (progressDialog?.isShowing == true) {
                    progressDialog?.message(
                        null,
                        "%d / %d KB download.".format((progress / 1024), maxLength / 1024),
                        null
                    )
                }
            }

            /***
             * 取消数据库更新
             */
            override fun dbDownloadCanceled() {}

            /***
             * 数据库下载完成
             */
            override fun dbDownloadCompleted(success: Boolean, errorMsg: CharSequence?) {
                LogUtils.file(LogUtils.I, "DB download finished.")
                progressDialog?.message(R.string.update_download_finished_text, null, null)
            }

            /***
             * 数据库更新整个流程结束
             */
            override fun dbUpdateCompleted() {
                LogUtils.file(LogUtils.I, "DB update finished.")
                val newFileHash = FileUtils.getFileMD5ToString(FileUtils.getDbFilePath())
                if (!forceUpdate && UserSettings.get().getDBHash() == newFileHash) {
                    LogUtils.file(LogUtils.W, "duplicate DB file.")
                    iActivityCallBack?.showSnackBar(R.string.db_update_duplicate)
                } else {
                    UserSettings.get().setDBHash(newFileHash)
                    UserSettings.get().setDbVersion(serverVersion)
                    iActivityCallBack?.showSnackBar(R.string.db_update_finished_text)
                    iActivityCallBack?.dbUpdateFinished()
                }
                progressDialog?.cancel()
                checkPrefab()
            }

            override fun prefabUpdateCheck() {
                if (hasNewPrefab) {
                    LogUtils.file(LogUtils.I, "New prefab version $prefabVersion determined.")
                    MaterialDialog(mContext, MaterialDialog.DEFAULT_BEHAVIOR)
                        .title(res = R.string.prefab_update_dialog_title)
                        .message(res = R.string.prefab_update_dialog_text)
                        .cancelOnTouchOutside(false)
                        .show {
                            positiveButton(res = R.string.dialog_confirm) {
                                downloadPrefab(false)
                            }
                            negativeButton(res = R.string.dialog_cancel) {
                                LogUtils.file(LogUtils.I, "Canceled download prefab version$prefabVersion.")
                            }
                        }
                }
            }

            override fun prefabDownloadCompleted(success: Boolean, errorMsg: CharSequence?) {
                LogUtils.file(LogUtils.I, "Prefab download finished.")
                progressDialog?.message(R.string.update_download_finished_text, null, null)
            }

            override fun prefabUpdateCompleted() {
                LogUtils.file(LogUtils.I, "Prefab update finished.")
                progressDialog?.cancel()
                UserSettings.get().setPrefabVersion(prefabVersion)
                iActivityCallBack?.prefabUpdateFinished()
            }

            override fun urlLoadError() {
                progressDialog?.cancel()
                iActivityCallBack?.showSnackBar(R.string.url_update_failed)
            }

            override fun urlDataParse() {
                inputUrlValue?.let {
                    MaterialDialog(mContext, MaterialDialog.DEFAULT_BEHAVIOR)
                        .title(text = I18N.getString(R.string.message))
                        .message(text = it)
                        .show {
                            positiveButton(res = R.string.text_ok)
                        }
                    if (UserSettings.get().saveExternalData(it))
                        iActivityCallBack?.externalDataApplied()
                }
            }

            /***
             * 更新失败
             */
            override fun dbUpdateError() {
                progressDialog?.cancel()
                iActivityCallBack?.showSnackBar(R.string.db_update_failed)
            }
        }
    }

    class AppVersionJson{
        var versionCode: Int? = null
        var versionName: String? = null
        var recommend: Boolean? = null
        var messageJa: String? = null
        var messageZh: String? = null
        var messageKr: String? = null
        var infoJa: String? = null
        var infoZh: String? = null
        var infoKr: String? = null
    }

    fun checkAppVersion(checkDb: Boolean) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(Statics.APP_UPDATE_LOG)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                LogUtils.file(LogUtils.E, "checkAppVersion", e.message)
                if (checkDb) checkDatabaseVersion()
            }

            override fun onResponse(call: Call, response: Response) {
                val lastVersionJson = response.body?.string()
                try {
                    if (lastVersionJson.isNullOrEmpty())
                        throw Exception("No response from server.")
                    if (response.code != 200)
                        throw Exception("Abnormal connection state code: ${response.code}")

                    appVersionJsonInstance = JsonUtils.getBeanFromJson<AppVersionJson>(lastVersionJson, AppVersionJson::class.java)
                    appVersionJsonInstance?.versionCode?.let {
                        if (it > getAppVersionCode() &&
                            (appVersionJsonInstance?.recommend == true || UserSettings.get().betaTest)) { // it is recommended or you signed the beta test
                            appHasNewVersion = true
                        }
                    }
                } catch (e: Exception) {
                    LogUtils.file(LogUtils.E, "checkAppVersion", e.message)
                    iActivityCallBack?.showSnackBar(R.string.app_update_check_failed)
                } finally {
                    updateHandler.sendEmptyMessage(APP_UPDATE_CHECK_COMPLETED)
                }
            }
        })
    }

    fun checkDatabaseVersion(forceDownload: Boolean = false) {
        if (UserSettings.get().isCustomDB()) {
            return
        }
        forceUpdate = forceDownload
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(Statics.LATEST_VERSION_URL)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_ERROR)
                LogUtils.file(LogUtils.E, "checkDatabaseVersion", e.message)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val lastVersionJson = response.body?.string()
                try {
                    if (lastVersionJson.isNullOrEmpty())
                        throw Exception("No response from server.")
                    val obj = JSONObject(lastVersionJson)
                    serverVersion = obj.getLong("TruthVersion")
                    hasNewVersion = serverVersion != UserSettings.get().getDbVersion()
                    if (forceUpdate)
                        hasNewVersion = true
                    updateHandler.sendEmptyMessage(UPDATE_CHECK_COMPLETED)
                } catch (e: Exception) {
                    LogUtils.file(LogUtils.E, "checkDatabaseVersion", e.message)
                    updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_ERROR)
                }
            }
        })
    }

    var downloadId: Long? = null
    fun downloadApp(){
        val downloadManager = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(Statics.APP_PACKAGE)).apply {
            setMimeType("application/vnd.android.package-archive")
            setTitle(I18N.getString(R.string.app_full_name))
            setDestinationInExternalFilesDir(mContext, null, Statics.APK_NAME)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }
        FileUtils.checkFileAndDeleteIfExists(File(mContext.getExternalFilesDir(null), Statics.APK_NAME))
        downloadId = downloadManager.enqueue(request)
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        mContext.registerReceiver(broadcastReceiver, intentFilter)
    }

    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE){
                if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == downloadId){
                    installApp()
                }
            }
        }
    }

    fun installApp() {
        val contentUri = FileProvider.getUriForFile(
            mContext,
            BuildConfig.APPLICATION_ID + ".provider",
            File(mContext.getExternalFilesDir(null), Statics.APK_NAME)
        )
        val install = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            data = contentUri
        }
        mContext.unregisterReceiver(broadcastReceiver)
        mContext.startActivity(install)
    }

    fun downloadDB(forceDownload: Boolean) {
        LogUtils.file(LogUtils.I, "Start download DB ver$serverVersion.")
        progressDialog = MaterialDialog(mContext, MaterialDialog.DEFAULT_BEHAVIOR).apply {
            this.title(R.string.db_update_progress_title, null)
            .message(R.string.db_update_progress_text, null, null)
            .cancelable(false)
            .show()
        }
        thread(start = true){
            try {
                if (forceDownload) {
                    FileUtils.deleteDirectory(File(FileUtils.getDbDirectoryPath()))
                }
                val conn = URL(Statics.DB_FILE_URL).openConnection() as HttpURLConnection
                maxLength = conn.contentLength
                val inputStream = conn.inputStream
                if (!File(FileUtils.getDbDirectoryPath()).exists()) {
                    if (!File(FileUtils.getDbDirectoryPath()).mkdirs()) throw Exception("Cannot create DB path.")
                }
                val compressedFile = File(FileUtils.getCompressedDbFilePath())
                if (compressedFile.exists()) {
                    FileUtils.deleteFile(compressedFile)
                }
                val fileOutputStream = FileOutputStream(compressedFile)
                var totalDownload = 0
                val buf = ByteArray(1024 * 1024)
                var numRead: Int
                while (true) {
                    numRead = inputStream.read(buf)
                    totalDownload += numRead
                    progress = totalDownload
                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING))
                    if (numRead <= 0) {
                        updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED)
                        break
                    }
                    fileOutputStream.write(buf, 0, numRead)
                }
                inputStream.close()
                fileOutputStream.close()
                iActivityCallBack?.dbDownloadFinished()
            } catch (e: Exception) {
                LogUtils.file(LogUtils.E, "downloadDB", e.message)
                updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_ERROR)
            }
        }
    }

    fun doDecompress(){
        FileUtils.deleteFile(FileUtils.getDbFilePath())
        LogUtils.file(LogUtils.I, "Start decompress DB.")
        BrotliUtils.deCompress(FileUtils.getCompressedDbFilePath(), true)
        updateHandler.sendEmptyMessage(UPDATE_COMPLETED)
    }

    fun forceDownloadDb() {
        MaterialDialog(mContext, MaterialDialog.DEFAULT_BEHAVIOR)
            .title(res = R.string.db_update_dialog_title)
            .message(res = R.string.db_update_dialog_text)
            .cancelOnTouchOutside(false)
            .show {
                positiveButton(res = R.string.dialog_confirm) {
                    downloadDB(true)
                }
                negativeButton(res = R.string.dialog_cancel) {
                    LogUtils.file(LogUtils.I, "Canceled download db version$serverVersion.")
                }
            }
    }

    fun checkPrefab() {
        // check prefab expression
        if (!(UserSettings.get().getExpressPrefabTime()
                && UserSettings.get().getUpdatePrefabTime()))
            return

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(Statics.LATEST_VERSION_URL_JP) // JP prefab only
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_ERROR)
                LogUtils.file(LogUtils.E, "checkPrefab", e.message)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val lastVersionJson = response.body?.string()
                try {
                    if (lastVersionJson.isNullOrEmpty())
                        throw Exception("No response from server.")
                    val obj = JSONObject(lastVersionJson)
                    prefabVersion = obj.getLong("PrefabVer")
                    hasNewPrefab = prefabVersion != UserSettings.get().getPrefabVersion()
                    updateHandler.sendEmptyMessage(UPDATE_PREFAB_CHECK)
                } catch (e: Exception) {
                    LogUtils.file(LogUtils.E, "checkPrefab", e.message)
                    updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_ERROR)
                }
            }
        })
        //updateHandler.sendEmptyMessage(UPDATE_PREFAB_CHECK)
    }

    fun downloadPrefab(forceDownload: Boolean) {
        LogUtils.file(LogUtils.I, "Start download prefab ver$prefabVersion.")
        progressDialog = MaterialDialog(mContext, MaterialDialog.DEFAULT_BEHAVIOR).apply {
            this.title(R.string.prefab_update_progress_title, null)
                .message(R.string.update_progress_text, null, null)
                .cancelable(false)
                .show()
        }
        thread(start = true){
            try {
                if (forceDownload) {
                    FileUtils.deleteDirectory(File(FileUtils.getPrefabDirectoryPath()))
                }
                val conn = URL(Statics.PREFAB_FILE_URL).openConnection() as HttpURLConnection
                maxLength = conn.contentLength
                val inputStream = conn.inputStream
                if (!File(FileUtils.getPrefabDirectoryPath()).exists()) {
                    if (!File(FileUtils.getPrefabDirectoryPath()).mkdirs()) throw Exception("Cannot create prefabs path.")
                }
                val compressedFile = File(FileUtils.getPrefabFilePath())
                if (compressedFile.exists()) {
                    FileUtils.deleteFile(compressedFile)
                }
                val fileOutputStream = FileOutputStream(compressedFile)
                var totalDownload = 0
                val buf = ByteArray(1024 * 1024)
                var numRead: Int
                while (true) {
                    numRead = inputStream.read(buf)
                    totalDownload += numRead
                    progress = totalDownload
                    updateHandler.sendMessage(updateHandler.obtainMessage(UPDATE_DOWNLOADING))
                    if (numRead <= 0) {
                        updateHandler.sendEmptyMessage(UPDATE_PREFAB_DOWNLOAD_COMPLETED)
                        break
                    }
                    fileOutputStream.write(buf, 0, numRead)
                }
                inputStream.close()
                fileOutputStream.close()
                iActivityCallBack?.prefabDownloadFinished()
            } catch (e: Exception) {
                LogUtils.file(LogUtils.E, "downloadDB", e.message)
                updateHandler.sendEmptyMessage(UPDATE_PREFAB_DOWNLOAD_ERROR)
            }
        }
    }

    fun doUnzip(){
        FileUtils.deleteFilesExtension(FileUtils.getPrefabFilePath(), "json")
        LogUtils.file(LogUtils.I, "Start unzip prefabs.")
        FileUtils.unzip(FileUtils.getPrefabDirectoryPath() + "/", Statics.PREFAB_FILE_NAME)
        updateHandler.sendEmptyMessage(UPDATE_PREFAB_COMPLETE)
    }

    fun analyzeAndUpdatePrefab() {
        iActivityCallBack?.showSnackBar(R.string.prefab_update_extract)
        val path = FileUtils.getPrefabDirectoryPath()

        App.dbExtension = ExtensionDB.getDB(application.baseContext)
        App.dbExtensionRepository = DBExtensionRepository(App.dbExtension.actionPrefabDao())
        val extensionDB = App.dbExtensionRepository

        val fileList = FileUtils.getFileListsExtension(path, "json")
        for (file in fileList) {
            val stringBuilder = StringBuilder()
            if (FileUtils.checkFile(file)) {
                try {
                    FileInputStream(file).use { fis ->
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
            val fileContents = stringBuilder.toString()
            val prefab = JsonUtils.getBeanFromJson<RawSkillPrefab>(fileContents, RawSkillPrefab::class.java)
            // TODO: attack
            val skillInfoData = mutableListOf<RawSkillPrefab.SkillInfoData>()
            skillInfoData.addAll(prefab.UnionBurstList)
            skillInfoData.addAll(prefab.MainSkillList)
            skillInfoData.addAll(prefab.SpecialSkillList)
            skillInfoData.addAll(prefab.SpecialSkillEvolutionList)
            skillInfoData.addAll(prefab.UnionBurstEvolutionList)
            skillInfoData.addAll(prefab.MainSkillEvolutionList)
            skillInfoData.addAll(prefab.SubUnionBurstList)
            //skillInfoData.add(RawSkillPrefab.SkillInfoData(prefab.Attack))

            val actionPrefabList = mutableListOf<ActionPrefab>()
            skillInfoData.forEach { skillData ->
                skillData.data.ActionParametersOnPrefab.forEach { actionParameter ->
                    if (actionParameter.data.Visible == 1) {
                        actionParameter.data.Details.forEach { details ->
                            if (details.data.Visible == 1) {
                                details.data.ExecTimeForPrefab.forEachIndexed { i, execTime ->
                                    actionPrefabList.add(
                                        ActionPrefab(
                                            details.data.ActionId,
                                            i,
                                            execTime.data.Time,
                                            execTime.data.DamageNumType,
                                            execTime.data.Weight,
                                            execTime.data.DamageNumScale
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            extensionDB.insertActionPrefabs(actionPrefabList.toList())
            FileUtils.deleteFile(file)
        }
        iActivityCallBack?.showSnackBar(R.string.prefab_update_extract_complete)
    }

    fun updateFailed(){
        updateHandler.sendEmptyMessage(UPDATE_DOWNLOAD_ERROR)
    }

    fun getAppVersionCode(): Int{
        return BuildConfig.VERSION_CODE
    }

    @Suppress("UNUSED_PARAMETER")
    fun getInputFromUrl() {
        inputUrl?.let {
            val preProcessedUrl = when {
                it.contains("//cafe.daum") -> {
                    it.replace("//cafe.daum", "//m.cafe.daum")
                }
                it.contains("//gall.dcinside.com/m/") -> {
                    it.replace("//gall.dcinside.com/m/", "//gall.dcinside.com/")
                }
                else -> {
                    it
                }
            }

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(preProcessedUrl)
                .build()
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    updateHandler.sendEmptyMessage(UPDATE_URL_LOAD_ERROR)
                    LogUtils.file(LogUtils.E, "intentUrlGetError", e.message)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val html = response.body?.string()
                    try {
                        val document = Jsoup.parse(html)
                        document.outputSettings(Document.OutputSettings().prettyPrint(false))

                        val url = response.request.url.toString()
                        val element = when {
                            url.contains("m.dcinside.com") -> {
                                document.select("div.thum-txtin")
                            }
                            url.contains("dcinside.com") -> {
                                document.select("div.writing_view_box")
                            }
                            url.contains("arca.live") -> {
                                document.select("div.article-content")
                            }
                            url.contains("kyaruberos") -> {
                                document.select("div.ppatc_body div.xe_content")
                            }
                            else -> {
                                document.select("div.view_info")
                            }
                        }
                        element.select("br").append("\n")
                        element.select("p").prepend("\n")
                        inputUrlValue = Jsoup.clean(element.first().wholeText().replace("\t", "").replace("\r\n", ""),
                        "", Whitelist.none(), Document.OutputSettings().prettyPrint(false))
                        updateHandler.sendEmptyMessage(UPDATE_URL_LOAD_COMPLETED)
                    } catch (e: Exception) {
                        LogUtils.file(LogUtils.E, "intentUrlGetError", e.message)
                        updateHandler.sendEmptyMessage(UPDATE_URL_LOAD_ERROR)
                    }
                }
            })
        } ?: run {
            inputUrlValue?.let { value ->
                try {
                    val testJson = JsonUtils.getBeanFromJson<UserData>(value, UserData::class.java)
                    MaterialDialog(mContext, MaterialDialog.DEFAULT_BEHAVIOR)
                        .title(res = R.string.user_data_import)
                        .message(res = R.string.user_data_import_valid)
                        .cancelOnTouchOutside(false)
                        .show {
                            positiveButton(res = R.string.dialog_confirm) {
                                UserSettings.get().setUserData(value)
                                iActivityCallBack?.showSnackBar(R.string.user_data_imported)
                            }
                            negativeButton(res = R.string.dialog_cancel) {
                                LogUtils.file(LogUtils.I, "Canceled import user data.")
                            }
                        }
                } catch (e: Exception) {
                    iActivityCallBack?.showSnackBar(R.string.user_data_import_invalid)
                }
            }
        }

    }

    val updateHandler = Handler(Looper.getMainLooper()) { msg: Message ->
        when (msg.what) {
            APP_UPDATE_CHECK_COMPLETED ->
                callBack.appCheckUpdateCompleted()
            UPDATE_CHECK_COMPLETED ->
                callBack.dbCheckUpdateCompleted(hasNewVersion, versionInfo)
            UPDATE_DOWNLOADING ->
                callBack.dbDownloadProgressChanged(progress, maxLength)
            UPDATE_DOWNLOAD_ERROR ->
                callBack.dbUpdateError()
            UPDATE_DOWNLOAD_COMPLETED ->
                callBack.dbDownloadCompleted(true, "")
            UPDATE_COMPLETED ->
                callBack.dbUpdateCompleted()
            UPDATE_DOWNLOAD_CANCELED ->
                TODO()
            UPDATE_URL_LOAD_ERROR ->
                callBack.urlLoadError()
            UPDATE_URL_LOAD_COMPLETED ->
                callBack.urlDataParse()
            UPDATE_PREFAB_CHECK ->
                callBack.prefabUpdateCheck()
            UPDATE_PREFAB_DOWNLOAD_ERROR ->
                callBack.dbUpdateError()
            UPDATE_PREFAB_DOWNLOAD_COMPLETED ->
                callBack.dbDownloadCompleted(true, "")
            UPDATE_PREFAB_COMPLETE ->
                callBack.prefabUpdateCompleted()
            else -> {
            }
        }
        true
    }

    interface UpdateCallBack {
        fun appCheckUpdateCompleted()
        fun dbCheckUpdateCompleted(hasUpdate: Boolean, updateInfo: CharSequence?)
        fun dbDownloadProgressChanged(progress: Int, maxLength: Int)
        fun dbDownloadCanceled()
        fun dbUpdateError()
        fun dbDownloadCompleted(success: Boolean, errorMsg: CharSequence?)
        fun dbUpdateCompleted()
        fun prefabUpdateCheck()
        fun prefabDownloadCompleted(success: Boolean, errorMsg: CharSequence?)
        fun prefabUpdateCompleted()
        fun urlLoadError()
        fun urlDataParse()
    }

    interface IActivityCallBack {
        fun showSnackBar(@StringRes messageRes: Int)
        fun dbDownloadFinished()
        fun dbUpdateFinished()
        fun prefabDownloadFinished()
        fun prefabUpdateFinished()
        fun externalDataApplied()
    }

    private var iActivityCallBack: IActivityCallBack? = null
    fun setIActivityCallBack(callBack: IActivityCallBack) {
        iActivityCallBack = callBack
    }

    private var inputUrl: String? = null
    private var inputUrlValue: String? = null

    fun setInputString(input: String) {
        val matcher = Patterns.WEB_URL.matcher(input)
        if (matcher.find()) {
            // do something
            val url = input.substring(matcher.start(0), matcher.end(0))
            inputUrl = url
        }
        else {
            inputUrlValue = input
        }
    }
}
