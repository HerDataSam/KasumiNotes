package com.github.malitsplus.shizurunotes.ui.comparison

import android.view.View
import android.widget.TextView
import androidx.navigation.findNavController
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.ResourceManager
import com.github.malitsplus.shizurunotes.data.RankComparison
import com.github.malitsplus.shizurunotes.databinding.ItemComparisonBinding
import com.github.malitsplus.shizurunotes.ui.base.BaseRecyclerAdapter
import com.github.malitsplus.shizurunotes.ui.setting.SettingFragment
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.user.UserSettings

class ComparisonListAdapter (
    private val sharedViewModelChara: SharedViewModelChara
): BaseRecyclerAdapter<RankComparison, ItemComparisonBinding>(R.layout.item_comparison) {
    override fun onBindViewHolder(holder: VH<ItemComparisonBinding>, position: Int) {
        with(holder.binding) {
            val item = itemList[position]
            comparison = item

            // click listener
            clickListener = View.OnClickListener {
                sharedViewModelChara.mSetSelectedChara(comparison?.chara)
                it.findNavController().navigate(
                    ComparisonListFragmentDirections.actionNavComparisonListToNavComparisonDetails()
                )
            }

            // set views
            setTextColor(item.property.getAtk(), cmpAtk)
            setTextColor(item.property.getDef(), cmpDef)
            setTextColor(item.property.getPhysicalCritical(), cmpPhysicalCritical)
            setTextColor(item.property.getMagicStr(), cmpMagicStr)
            setTextColor(item.property.getMagicDef(), cmpMagicDef)
            setTextColor(item.property.getMagicCritical(), cmpMagicCritical)
            setTextColor(item.property.getHp(), cmpHp)
            setTextColor(item.property.getEnergyRecoveryRate(), cmpEnergyRecoveryRate)
            setTextColor(item.property.getEnergyReduceRate(), cmpEnergyReduceRate)
            setTextColor(item.property.getWaveEnergyRecovery(), cmpWaveEnergyRecovery)
            setTextColor(item.property.getLifeSteal(), cmpLifeSteal)
            setTextColor(item.property.getHpRecoveryRate(), cmpHpRecoveryRate)
            setTextColor(item.property.getWaveHpRecovery(), cmpWaveHpRecovery)
            setTextColor(item.property.getAccuracy(), cmpAccuracy)
            setTextColor(item.property.getDodge(), cmpDodge)

            val setting = UserSettings.get().preference.getString(UserSettings.FONT_SIZE, "M") ?: "M"
            setTextSize(setting, cmpAtk)
            setTextSize(setting, cmpDef)
            setTextSize(setting, cmpPhysicalCritical)
            setTextSize(setting, cmpMagicStr)
            setTextSize(setting, cmpMagicDef)
            setTextSize(setting, cmpMagicCritical)
            setTextSize(setting, cmpHp)
            setTextSize(setting, cmpEnergyRecoveryRate)
            setTextSize(setting, cmpEnergyReduceRate)
            setTextSize(setting, cmpWaveEnergyRecovery)
            setTextSize(setting, cmpLifeSteal)
            setTextSize(setting, cmpHpRecoveryRate)
            setTextSize(setting, cmpWaveHpRecovery)
            setTextSize(setting, cmpAccuracy)
            setTextSize(setting, cmpDodge)
            executePendingBindings()
        }
    }

    private fun setTextColor(num: Int, textView: TextView) {
        when {
            num > 0 -> {
                textView.setTextColor(ResourceManager.get().getColor(R.color.green_350))
            }
            num < 0 -> {
                textView.setTextColor(ResourceManager.get().getColor(R.color.red_500))
            }
            else -> {
                textView.setTextColor(ResourceManager.get().getColor(R.color.textPrimary))
            }
        }
    }
    private fun setTextColor(num: Long, textView: TextView) {
        when {
            num > 0 -> {
                textView.setTextColor(ResourceManager.get().getColor(R.color.green_350))
            }
            num < 0 -> {
                textView.setTextColor(ResourceManager.get().getColor(R.color.red_500))
            }
            else -> {
                textView.setTextColor(ResourceManager.get().getColor(R.color.textPrimary))
            }
        }
    }

    private fun setTextSize(setting: String, textView: TextView) {
        when (setting) {
            "L" -> textView.setTextAppearance(R.style.myComparisonTextLarge)
            "S" -> textView.setTextAppearance(R.style.myComparisonTextSmall)
            "XS" -> textView.setTextAppearance(R.style.myComparisonTextXSmall)
            else -> textView.setTextAppearance(R.style.myComparisonText)
        }
    }

}