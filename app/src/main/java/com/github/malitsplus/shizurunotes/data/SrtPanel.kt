package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.Statics

class SrtPanel (
    val readingId: Int,
    val reading: String,
    val readType: Int,
    val panelId: Int,
    val detailText: String
) {
    val start: String by lazy {
        reading.take(1)
    }

    val end: String by lazy {
        reading.takeLast(1)
    }

    val iconUrl: String by lazy {
        Statics.SRT_PANEL_URL.format(panelId)
    }

    val srtType: SrtType by lazy {
        when (readType) {
            2 -> SrtType.Ura
            3 -> SrtType.Priconne
            else -> SrtType.Normal
        }
    }

    enum class SrtType {
        Normal,
        Ura,
        Priconne;

        fun description(): String {
            return when(this) {
                Ura -> I18N.getStringWithSpace(R.string.text_srt_ura)
                Priconne -> I18N.getStringWithSpace(R.string.text_srt_priconne)
                else -> I18N.getStringWithSpace(R.string.text_srt_normal)
            }
        }
        fun backgroundColor(): Int {
            return when(this) {
                Ura -> R.drawable.shape_text_tag_background_cornflower
                Priconne -> R.drawable.shape_text_tag_background_variant
                else -> R.drawable.shape_text_tag_background_moderategreen
            }
        }
    }
}