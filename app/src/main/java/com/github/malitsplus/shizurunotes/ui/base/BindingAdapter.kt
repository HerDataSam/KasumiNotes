package com.github.malitsplus.shizurunotes.ui.base

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.allen.library.SuperTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.ResourceManager
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.data.Quest
import com.github.malitsplus.shizurunotes.data.Skill
import com.github.malitsplus.shizurunotes.data.SrtPanel
import com.github.malitsplus.shizurunotes.ui.mychara.OnCharaTargetClickListener
import com.google.android.material.card.MaterialCardView

@BindingAdapter(value = ["imageUrl", "placeHolder", "errorHolder"], requireAll = false)
fun loadImage(view: ImageView, imageUrl: String?, placeHolder: Int?, errorHolder: Int?) {
    when{
        !imageUrl.isNullOrEmpty() && placeHolder != null && errorHolder != null ->
            Glide.with(view.context)
                .load(imageUrl)
                .placeholder(placeHolder)
                .error(errorHolder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
        !imageUrl.isNullOrEmpty() && placeHolder != null ->
            Glide.with(view.context)
                .load(imageUrl)
                .placeholder(placeHolder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
        !imageUrl.isNullOrEmpty() && errorHolder != null ->
            Glide.with(view.context)
                .load(imageUrl)
                .error(errorHolder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
        !imageUrl.isNullOrEmpty() ->
            Glide.with(view.context)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }
}

@BindingAdapter("src")
fun loadSrc(view: ImageView, src: Int) {
    view.setImageResource(src)
}

@BindingAdapter("sRightText")
fun setRightString(view: SuperTextView, src: String) {
    view.setRightString(src)
}

@BindingAdapter("sLeftText")
fun setLeftString(view: SuperTextView, src: String) {
    view.setLeftString(src)
}

@BindingAdapter("checkSummonSkill")
fun setVisibility(view: View, skill: Skill?) {
    view.visibility = if (skill != null && skill.enemyMinionList.isNotEmpty()) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("itemStatus")
fun setItemStatus(v: ImageView, style: String) {
    when (style) {
        "gray" -> setGray(v)
        "original" -> setOriginal(v)
    }
}

private fun setGray(v: ImageView) {
    val matrix = ColorMatrix()
    matrix.setSaturation(0f)
    v.colorFilter = ColorMatrixColorFilter(matrix)
    v.imageAlpha = 216
}
private fun setOriginal(v: ImageView) {
    v.colorFilter = null
    v.imageAlpha = 255
}

@BindingAdapter("comparisonColor")
fun setComparisonColor(v: TextView, value: Double) {
    when {
        value > 0 -> v.setTextColor(ResourceManager.get().getColor(R.color.green_350))
        value < 0 -> v.setTextColor(ResourceManager.get().getColor(R.color.red_500))
        else -> v.setTextColor(ResourceManager.get().getColor(R.color.textPrimary))
    }
}

@BindingAdapter("sRightComparisonColor")
fun setSComparisonColor(v: SuperTextView, value: Double) {
    when {
        value > 0 -> v.setRightTextColor(ResourceManager.get().getColor(R.color.green_350))
        value < 0 -> v.setRightTextColor(ResourceManager.get().getColor(R.color.red_500))
        else -> v.setRightTextColor(ResourceManager.get().getColor(R.color.textPrimary))
    }
}

@BindingAdapter("backgroundColorTint")
fun setBackgroundColorTint(v: MaterialCardView, value: Int) {
    v.backgroundTintList = ResourceManager.get().getColorStateList(value)
}

@BindingAdapter("questType")
fun setQuestTypeTag(v:TextView, value: Quest.QuestType) {
    v.text = I18N.getString(R.string.space_modifier, value.description())
    v.setBackgroundResource(value.backgroundColor())

}

@BindingAdapter("chara", "targetNumber")
fun setCharaTarget(v: TextView, chara: Chara, target: Int) {
    val targetValue = chara.targetRankList[target]

    v.text = I18N.getString(R.string.rank_d_equipment_two_line, targetValue / 100, targetValue % 100)
    if (targetValue == 100) {
        v.text = I18N.getString(R.string.my_chara_no_target)
    }
    if (targetValue == chara.targetSetting.rank * 100 + chara.targetSetting.equipmentNumber) {
        v.setBackgroundResource(R.drawable.shape_text_tag_background_variant)
    }
    else {
        v.setBackgroundResource(0)
    }
}

@BindingAdapter("setLocked")
fun setTargetLocked(v: ConstraintLayout, isLocked: Boolean) {
    if (isLocked) {
        v.setBackgroundColor(ResourceManager.get().getColor(R.color.colorCustom))
    } else {
        v.setBackgroundColor(0x00000000)
    }
}

@BindingAdapter("setLocked")
fun setTargetLocked(v: ImageView, isLocked: Boolean) {
    if (isLocked) {
        v.setBackgroundResource(R.drawable.mic_lock)
    } else {
        v.setBackgroundResource(R.drawable.mic_lock_open)
    }
}

@BindingAdapter("srtType")
fun setSrtTypeTag(v:TextView, value: SrtPanel.SrtType) {
    v.text = I18N.getString(R.string.space_modifier, value.description())
    v.setBackgroundResource(value.backgroundColor())
}

@BindingAdapter("srtStringButton")
fun setSrtStringButton(v:Button, value: Boolean) {
    if (value) {
        v.setBackgroundResource(R.drawable.shape_text_tag_background_variant)
    }
    else {
        v.setBackgroundResource(0)
    }
}

@BindingAdapter("onLongClick")
fun setLongClickFunction(v: View, func: () -> Unit) {
    v.setOnLongClickListener {
        func()
        return@setOnLongClickListener true
    }
}

/*@BindingAdapter("starStatus")
fun setStarStatus(v:ImageView, style: String) {
    when (style) {
        "filled" -> setFilled(v)
        "blank" -> setBlank(v)
    }
}*/
