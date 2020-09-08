package com.github.malitsplus.shizurunotes.data.extension

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N

class Extension (
    val type: ExtensionType,
    val title: String,
    val madeBy: String,
    val version: String
) {
    val typeString: String by lazy {
        when (type) {
            ExtensionType.Nickname -> I18N.getString(R.string.extension_nickname)
            ExtensionType.Recommend -> I18N.getString(R.string.extension_recommend)
        }
    }
}

enum class ExtensionType {
    Nickname,
    Recommend
}