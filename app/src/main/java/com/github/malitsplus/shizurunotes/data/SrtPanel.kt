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
            "나" -> list.addAll(listOf("나", "아"))
            "라" -> list.addAll(listOf("라", "아"))
            "란" -> list.addAll(listOf("란", "안"))
            "래" -> list.addAll(listOf("래", "애"))
            "량" -> list.addAll(listOf("량", "양"))
            "네" -> list.addAll(listOf("네", "에"))
            "레" -> list.addAll(listOf("레", "에"))
            "녀" -> list.addAll(listOf("녀", "여"))
            "려" -> list.addAll(listOf("려", "여"))
            "녕", "령" -> list.addAll(listOf("녕", "령", "영"))
            "노" -> list.addAll(listOf("노", "오"))
            "로" -> list.addAll(listOf("로", "오"))
            "료" -> list.addAll(listOf("료", "요"))
            "루" -> list.addAll(listOf("루", "우"))
            "류" -> list.addAll(listOf("류", "유"))
            "름" -> list.addAll(listOf("름", "음"))
            "니" -> list.addAll(listOf("니", "이"))
            "리" -> list.addAll(listOf("리", "이"))
            "님" -> list.addAll(listOf("님", "임"))
            in "\u3041".."\u309e" -> list.addAll(listOf(value, (value[0] + 0x60).toString()))
            in "\u30a1".."\u30fe" -> list.addAll(listOf(value, (value[0] - 0x60).toString()))
            in "\uff66".."\uff9d" -> list.addAll(listOf(value, (value[0] - 0xcf25).toString()))
            else -> list.add(value)
        }
        return list
    }
}