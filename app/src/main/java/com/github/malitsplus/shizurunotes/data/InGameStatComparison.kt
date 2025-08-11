package com.github.malitsplus.shizurunotes.data

import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.ResourceManager
import kotlin.math.round

class InGameStatComparison(
    val text: String,
    var statFrom: Double,
    var statTo: Double,
    val displayCategory: DisplayCategory = DisplayCategory.TP,
    val displayStyle: DisplayStyle = DisplayStyle.ROUND0
) {
    var statDiff = statFrom - statTo
    var displayStringFrom: String = String.format(I18N.getString(R.string.round_0), 0)
    var displayStringTo: String = String.format(I18N.getString(R.string.round_0), 0)
    var displayBackground = ResourceManager.get().getColor(R.color.Pigeon)
    var displayTextColor = ResourceManager.get().getColor(R.color.colorOnCustom)

    init {
        when (displayStyle) {
            DisplayStyle.ROUND2 -> {
                displayStringFrom = String.format(I18N.getString(R.string.round_2), statFrom)
                displayStringTo = String.format(I18N.getString(R.string.round_2), statTo)
            }

            DisplayStyle.PERCENTAGE2 -> {
                statFrom *= 100.0
                statTo *= 100.0
                displayStringFrom = String.format(I18N.getString(R.string.percentage_2), statFrom)
                displayStringTo = String.format(I18N.getString(R.string.percentage_2), statTo)
            }

            DisplayStyle.ROUND0_2 -> {
                statDiff = -statDiff
                displayStringFrom =
                    String.format(I18N.getString(R.string.round_0_2), statFrom.toInt() / 100, statFrom % 100.0)
                displayStringTo =
                    String.format(I18N.getString(R.string.round_0_2), statTo.toInt() / 100, statTo % 100.0)
            }

            DisplayStyle.RANK_ITEM -> {
                statDiff = 0.0
                displayStringFrom =
                    String.format(I18N.getString(R.string.rank_item), statFrom.toInt() / 100, statFrom.toInt() % 100)
                displayStringTo =
                    String.format(I18N.getString(R.string.rank_item), statTo.toInt() / 100, statTo.toInt() % 100)
            }

            else -> {
                displayStringFrom = String.format(I18N.getString(R.string.round_0), round(statFrom).toInt())
                displayStringTo = String.format(I18N.getString(R.string.round_0), round(statTo).toInt())
            }
        }

        displayBackground = ResourceManager.get().getColor(
            when (displayCategory) {
                DisplayCategory.TITLE -> R.color.yellow_800
                DisplayCategory.DEF -> R.color.Independence
                DisplayCategory.DMG -> R.color.Prussian
                else -> R.color.Pigeon
            }
        )
        displayTextColor = ResourceManager.get().getColor(
            when (displayCategory) {
                DisplayCategory.TITLE -> R.color.black_800
                else -> R.color.colorOnCustom
            }
        )
    }

}

enum class DisplayStyle {
    ROUND0, ROUND2, ROUND0_2, PERCENTAGE2, RANK_ITEM
}

enum class DisplayCategory {
    TP, DEF, DMG, TITLE
}