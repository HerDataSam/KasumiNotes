package com.github.malitsplus.shizurunotes.ui.setting.customDB

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.Statics
import com.github.malitsplus.shizurunotes.databinding.FragmentCustomDbSettingBinding
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.utils.FileUtils
import com.google.android.material.snackbar.Snackbar

interface OnCopyButtonListener : View.OnClickListener {
    fun onCopyButtonClickedListener(server: String): Boolean
}

class CustomDBFragment : Fragment(), OnCopyButtonListener {
    lateinit var binding: FragmentCustomDbSettingBinding
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        //
    }
    private val fileReader = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        if (it != null) {
            handleExternalDBFile(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomDbSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            customDbToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            saveCustomDbName.setOnClickListener {
                val memo = customDbName.text.toString()
                UserSettings.get().setCustomDBMemo(
                    memo.substring(0 until kotlin.math.min(memo.length, 20))
                )
                Snackbar.make(binding.root, R.string.custom_db_memo_save, Snackbar.LENGTH_SHORT).show()
            }
            customDbName.setText(UserSettings.get().getCustomDBMemo())
            onClickListener = this@CustomDBFragment
        }
    }

    override fun onCopyButtonClickedListener(server: String): Boolean {
        val from = when (server) {
            "jp" -> Statics.DB_FILE_NAME_JP
            "kr" -> Statics.DB_FILE_NAME_KR
            else -> ""
        }
        val to = Statics.DB_FILE_NAME_CUSTOM

        val serverText = when (server) {
            "jp" -> I18N.getString(R.string.custom_db_jp)
            "kr" -> I18N.getString(R.string.custom_db_kr)
            else -> ""
        }

        if (server == "external") {
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            fileReader.launch("*/*")
        } else {
            MaterialDialog(requireContext(), MaterialDialog.DEFAULT_BEHAVIOR)
                .title(text = I18N.getString(R.string.custom_db_copy))
                .message(text = I18N.getString(R.string.custom_db_copy_text, serverText))
                .cancelOnTouchOutside(true)
                .show {
                    positiveButton(res = R.string.text_ok) {
                        if (FileUtils.copyDBFile(from, to))
                            Snackbar.make(
                                binding.root,
                                R.string.custom_db_copy_success,
                                Snackbar.LENGTH_LONG
                            ).show()
                        else
                            Snackbar.make(
                                binding.root,
                                R.string.custom_db_copy_failed,
                                Snackbar.LENGTH_LONG
                            ).show()
                    }
                    negativeButton(res = R.string.text_copy)
                }
        }
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleExternalDBFile(file: Uri) {
        var fileName = ""
        var fileSize = 0
        requireContext().contentResolver.query(
            file, null, null, null, null
        )?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            it.moveToFirst()
            fileName = it.getString(nameIndex)
            fileSize = it.getInt(sizeIndex)
        }
        requireContext().contentResolver.openInputStream(file).use { stream ->
            if (!FileUtils.checkValidDBFile(stream)) {
                Snackbar.make(
                    binding.root,
                    I18N.getString(R.string.custom_db_copy_invalid),
                    Snackbar.LENGTH_LONG
                ).show()
                return
            }
        }
        requireContext().contentResolver.openInputStream(file).use { stream ->
            val success = FileUtils.copyInputStreamToFile(stream, Statics.DB_FILE_NAME_CUSTOM)
            if (success) {
                Snackbar.make(
                    binding.root,
                    I18N.getString(R.string.custom_db_copy_success_external, fileName),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                Snackbar.make(
                    binding.root,
                    I18N.getString(R.string.custom_db_copy_failed_external, fileName),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            stream?.close()
            FileUtils.checkFileSize(FileUtils.getDbFilePath(), fileSize)
        }
    }

    override fun onClick(p0: View?) {
        //
    }


}