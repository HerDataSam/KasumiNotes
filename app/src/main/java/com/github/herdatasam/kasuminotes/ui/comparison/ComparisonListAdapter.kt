package com.github.herdatasam.kasuminotes.ui.comparison

import android.graphics.Color
import android.widget.TextView
import com.github.herdatasam.kasuminotes.R
import com.github.herdatasam.kasuminotes.common.ResourceManager
import com.github.herdatasam.kasuminotes.data.RankComparison
import com.github.herdatasam.kasuminotes.databinding.ItemComparisonBinding
import com.github.herdatasam.kasuminotes.ui.base.BaseRecyclerAdapter

class ComparisonListAdapter : BaseRecyclerAdapter<RankComparison, ItemComparisonBinding>(R.layout.item_comparison) {
    override fun onBindViewHolder(holder: VH<ItemComparisonBinding>, position: Int) {
        with(holder.binding) {
            val item = itemList[position]
            comparison = item
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
            executePendingBindings()
        }
    }

    private fun setTextColor(num: Int, textView: TextView) {
        if (num > 0) {
            textView.setTextColor(ResourceManager.get().getColor(R.color.green_350))
        } else if (num < 0) {
            textView.setTextColor(ResourceManager.get().getColor(R.color.red_500))
        } else {
            textView.setTextColor(ResourceManager.get().getColor(R.color.textPrimary))
        }
    }
    private fun setTextColor(num: Long, textView: TextView) {
        if (num > 0) {
            textView.setTextColor(ResourceManager.get().getColor(R.color.green_350))
        } else if (num < 0) {
            textView.setTextColor(ResourceManager.get().getColor(R.color.red_500))
        } else {
            textView.setTextColor(ResourceManager.get().getColor(R.color.textPrimary))
        }
    }
}