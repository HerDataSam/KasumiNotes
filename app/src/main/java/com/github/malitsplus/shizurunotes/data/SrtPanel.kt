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

    val startList: MutableList<String> by lazy {
        multiWords(start)
    }

    val end: String by lazy {
        when (val endPossible = reading.takeLast(1)) {
            "~" -> reading.takeLast(2).take(1)
            else -> endPossible
        }
    }

    val endList: MutableList<String> by lazy {
        multiWords(end)
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

    private fun multiWords(value: String): MutableList<String> {
        val list = mutableListOf<String>()
        when (value) {
            "나", "라", "아" -> list.addAll(listOf("나", "라", "아"))
            "란", "안" -> list.addAll(listOf("란", "안"))
            "래", "애" -> list.addAll(listOf("래", "애"))
            "량", "양" -> list.addAll(listOf("량", "양"))
            "네", "레", "에" -> list.addAll(listOf("네", "레", "에"))
            "녀", "려", "여" -> list.addAll(listOf("녀", "려", "여"))
            "녕", "령", "영" -> list.addAll(listOf("녕", "령", "영"))
            "노", "로", "오" -> list.addAll(listOf("노", "로", "오"))
            "료", "요" -> list.addAll(listOf("료", "요"))
            "루", "우" -> list.addAll(listOf("루", "우"))
            "류", "유" -> list.addAll(listOf("류", "유"))
            "름", "음" -> list.addAll(listOf("름", "음"))
            "니", "리", "이" -> list.addAll(listOf("니", "리", "이"))
            "님", "임" -> list.addAll(listOf("님", "임"))
            else -> list.add(value)
        }
        return list
    }
}