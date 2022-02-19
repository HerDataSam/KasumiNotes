package com.github.malitsplus.shizurunotes.ui.setting.customDB

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

interface OnCopyButtonListener: View.OnClickListener {
    fun onCopyButtonClickedListener(server: String): Boolean
}

class CustomDBFragment : Fragment(), OnCopyButtonListener {
    lateinit var binding: FragmentCustomDbSettingBinding

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
                    memo.substring(0 until kotlin.math.min(memo.length, 20)))
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

        MaterialDialog(requireContext(), MaterialDialog.DEFAULT_BEHAVIOR)
            .title(text = I18N.getString(R.string.custom_db_copy))
            .message(text = I18N.getString(R.string.custom_db_copy_text, serverText))
            .cancelOnTouchOutside(true)
            .show {
                positiveButton(res = R.string.text_ok) {
                    if (FileUtils.copyDBFile(from, to))
                        Snackbar.make(binding.root, R.string.custom_db_copy_success, Snackbar.LENGTH_LONG).show()
                    else
                        Snackbar.make(binding.root, R.string.custom_db_copy_failed, Snackbar.LENGTH_LONG).show()
                }
                negativeButton(res = R.string.text_copy)
            }
        return true
    }

    override fun onClick(p0: View?) {
        //
    }


}