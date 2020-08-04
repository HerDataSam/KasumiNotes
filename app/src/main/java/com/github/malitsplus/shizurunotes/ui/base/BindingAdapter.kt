package com.github.malitsplus.shizurunotes.ui.base

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.allen.library.SuperTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.ResourceManager
import com.github.malitsplus.shizurunotes.data.Skill
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

/*@BindingAdapter("starStatus")
fun setStarStatus(v:ImageView, style: String) {
    when (style) {
        "filled" -> setFilled(v)
        "blank" -> setBlank(v)
    }
}*/
