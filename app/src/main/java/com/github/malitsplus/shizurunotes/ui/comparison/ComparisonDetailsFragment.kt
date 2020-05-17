package com.github.malitsplus.shizurunotes.ui.comparison

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.allen.library.SuperTextView
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.ResourceManager
import com.github.malitsplus.shizurunotes.databinding.FragmentComparisonDetailsBinding
import com.github.malitsplus.shizurunotes.ui.charadetails.SkillAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import kotlin.math.ceil
import kotlin.math.roundToInt

class ComparisonDetailsFragment : Fragment() {
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var comparisonDetailsVM: ComparisonDetailsViewModel
    private lateinit var binding: FragmentComparisonDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        comparisonDetailsVM = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[ComparisonDetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_comparison_details,
            container,
            false
        )

        binding.apply {
            val layoutManagerSkill = LinearLayoutManager(context)
            val adapterSkill = SkillAdapter(sharedChara)
            binding.comparisonDetailSkillRecycler.apply {
                layoutManager = layoutManagerSkill
                adapter = adapterSkill
                adapterSkill.update(comparisonDetailsVM.charaTo.skills)
            }
            // tool bar action
            toolbarComparisonDetails.setNavigationOnClickListener {
                it.findNavController().navigateUp()
            }
            // tool bar title
            toolbarComparisonDetails.title = "R" + sharedChara.rankComparisonFrom + "/" + sharedChara.equipmentComparisonFrom +
                    " → " + "R" + sharedChara.rankComparisonTo + "/" + sharedChara.equipmentComparisonTo

            // diff and property
            diff = comparisonDetailsVM.diffProperty
            property = comparisonDetailsVM.propertyTo
            val details = statusComparisonDetails

            diff?.let {
                setTextColor(it.getAtk(), details.txtPhyAtkComparisonDetails)
                setTextColor(it.getMagicStr(), details.txtMagAtkComparisonDetails)
                setTextColor(it.getPhysicalCritical(), details.txtPhyCrtComparisonDetails)
                setTextColor(it.getMagicCritical(), details.txtMagCrtComparisonDetails)
                setTextColor(it.getDef(), details.txtPhyDefComparisonDetails)
                setTextColor(it.getMagicDef(), details.txtMagDefComparisonDetails)
                setTextColor(it.getHp(), details.txtHpComparisonDetails)
                setTextColor(it.getLifeSteal(), details.txtLifeStealComparisonDetails)
                setTextColor(it.getEnergyRecoveryRate(), details.txtEngRcvComparisonDetails)
                setTextColor(it.getEnergyReduceRate(), details.txtEngRdcComparisonDetails)
                setTextColor(it.getAccuracy(), details.txtAccComparisonDetails)
                setTextColor(it.getWaveHpRecovery(), details.txtWavHComparisonDetails)
                setTextColor(it.getWaveEnergyRecovery(), details.txtWavEngComparisonDetails)
                setTextColor(it.getPhysicalPenetrate(), details.txtPhyPntComparisonDetails)
                setTextColor(it.getMagicPenetrate(), details.txtMagPntComparisonDetails)
                setTextColor(it.getHpRecoveryRate(), details.txtHpRcvComparisonDetails)
            }
            chara = comparisonDetailsVM.charaTo

            property?.let {
                // 1st ub
                val ub = 1000.0 / (90 * (1 + it.energyRecoveryRate / 100.0))
                val ubCeil = ceil(ub)
                val tpRecover = 90 * (1 + it.energyRecoveryRate / 100.0)
                TPUpInfo1stUBDetailTextTo.text =
                    String.format(getString(R.string.comparison_tp_1st_ub_description),
                        tpRecover.roundToInt(), ub, ubCeil.roundToInt())
                // 2nd ub
                val ub2 = (1000 - it.energyReduceRate * 10) / (90 * (1 + it.energyRecoveryRate / 100.0))
                val ub2Ceil = ceil(ub2)
                TPUpInfo2ndUBDetailTextTo.text =
                    String.format(getString(R.string.comparison_tp_2nd_ub_description),
                        (it.energyReduceRate * 10).roundToInt(), ub2, ub2Ceil.roundToInt())
            }

            comparisonDetailsVM.propertyFrom?.let {
                // 1st ub
                val ub = 1000.0 / (90 * (1 + it.energyRecoveryRate / 100.0))
                val ubCeil = ceil(ub)
                val tpRecover = 90 * (1 + it.energyRecoveryRate / 100.0)
                TPUpInfo1stUBDetailTextFrom.text =
                    String.format(getString(R.string.comparison_tp_1st_ub_description),
                        tpRecover.roundToInt(), ub, ubCeil.roundToInt())
                // 2nd ub
                val ub2 = (1000 - it.energyReduceRate * 10) / (90 * (1 + it.energyRecoveryRate / 100.0))
                val ub2Ceil = ceil(ub2)
                TPUpInfo2ndUBDetailTextFrom.text =
                    String.format(getString(R.string.comparison_tp_2nd_ub_description),
                        (it.energyReduceRate * 10).roundToInt(), ub2, ub2Ceil.roundToInt())
            }
        }

        return binding.run {
            root
        }
    }

    private fun setTextColor(num: Int, textView: SuperTextView) {
        if (num > 0) {
            textView.setRightTextColor(ResourceManager.get().getColor(R.color.green_350))
        } else if (num < 0) {
            textView.setRightTextColor(ResourceManager.get().getColor(R.color.red_500))
        } else {
            textView.setRightTextColor(ResourceManager.get().getColor(R.color.textPrimary))
        }
    }
    private fun setTextColor(num: Long, textView: SuperTextView) {
        if (num > 0) {
            textView.setRightTextColor(ResourceManager.get().getColor(R.color.green_350))
        } else if (num < 0) {
            textView.setRightTextColor(ResourceManager.get().getColor(R.color.red_500))
        } else {
            textView.setRightTextColor(ResourceManager.get().getColor(R.color.textPrimary))
        }
    }
}