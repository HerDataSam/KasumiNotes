package com.github.malitsplus.shizurunotes.ui.setting.userSettings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.databinding.FragmentUserSettingsBinding
import com.github.malitsplus.shizurunotes.user.UserData
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.utils.JsonUtils
import com.google.android.material.snackbar.Snackbar

interface OnExportImportButtonListener : View.OnClickListener {
    fun onCopyButtonClickedListener(command: String): Boolean
}

class ExportImportUserSettingsFragment : Fragment(), OnExportImportButtonListener {
    lateinit var binding: FragmentUserSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            userSettingToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            /*
                        saveCustomDbName.setOnClickListener {
                            val memo = customDbName.text.toString()
                            UserSettings.get().setCustomDBMemo(
                                memo.substring(0 until kotlin.math.min(memo.length, 20)))
                            Snackbar.make(binding.root, R.string.custom_db_memo_save, Snackbar.LENGTH_SHORT).show()
                        }
                        customDbName.setText(UserSettings.get().getCustomDBMemo())*/
            onClickListener = this@ExportImportUserSettingsFragment
        }
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onCopyButtonClickedListener(command: String): Boolean {
        if (command == "export") {
            Thread.sleep(100)
            activity?.runOnUiThread {
                MaterialDialog(requireContext(), MaterialDialog.DEFAULT_BEHAVIOR)
                    .title(R.string.setting_export_user_data_text)
                    .message(R.string.setting_export_user_data_summary)
                    .cancelOnTouchOutside(true)
                    .show {
                        positiveButton(res = R.string.text_export) {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, UserSettings.get().getUserData())
                                type = "text/plane"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            startActivity(shareIntent)
                        }
                        negativeButton(res = R.string.text_cancel)
                    }
            }
        } else if (command == "import") {
            try {
                val value = binding.userSettingImportContent.text.toString()
                val testJson: UserData = JsonUtils.getBeanFromJson(value, UserData::class.java)
                MaterialDialog(requireContext(), MaterialDialog.DEFAULT_BEHAVIOR)
                    .title(text = I18N.getString(R.string.setting_import_user_data_text))
                    .message(text = I18N.getString(R.string.custom_db_copy_text))
                    .cancelOnTouchOutside(true)
                    .show {
                        positiveButton(res = R.string.text_import) {
                            UserSettings.get().setUserData(value)
                            Snackbar.make(binding.root, R.string.user_data_imported, Snackbar.LENGTH_SHORT).show()
                        }
                        negativeButton(res = R.string.text_cancel)
                    }
            } catch (e: Exception) {
                Snackbar.make(binding.root, R.string.user_data_import_invalid, Snackbar.LENGTH_SHORT).show()
            }
        }

        return true
    }

    override fun onClick(p0: View?) {
        //
    }


}