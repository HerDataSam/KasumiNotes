package com.github.malitsplus.shizurunotes.ui.setting.extension

import androidx.lifecycle.ViewModel
import com.github.malitsplus.shizurunotes.data.extension.Extension
import com.github.malitsplus.shizurunotes.data.extension.ExtensionType
import com.github.malitsplus.shizurunotes.ui.base.DescriptionVT
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.user.UserSettings

class ExtensionItemViewModel() : ViewModel() {
    var selectedExtension: Extension = UserSettings.get().selectedExtension!!

    val viewList = mutableListOf<ViewType<*>>()
        get() {
            field.clear()
            if (selectedExtension.type == ExtensionType.Nickname)
                field.add(DescriptionVT(UserSettings.get().nicknameBuilder))
            return field
        }
}