package com.github.malitsplus.shizurunotes.ui.setting.extension

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.extension.Extension
import com.github.malitsplus.shizurunotes.ui.base.ExtensionVT
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.user.UserSettings

class ExtensionViewModel() : ViewModel() {
    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            UserSettings.get().loadExtensionInfo().forEach {
                field.add(ExtensionVT(it))
            }
            return field
        }
}

interface OnExtensionClickListener<T> : OnItemActionListener {
    fun onExtensionClickedListener(extension: Extension)
}