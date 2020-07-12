package com.github.malitsplus.shizurunotes.ui.comparison

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.allen.library.SuperTextView
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.ResourceManager
import com.github.malitsplus.shizurunotes.databinding.FragmentComparisonDetailsBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.charadetails.SkillAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.google.android.material.appbar.MaterialToolbar

class ComparisonDetailsFragment : Fragment() {
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var comparisonDetailsVM: ComparisonDetailsViewModel
    private lateinit var binding: FragmentComparisonDetailsBinding
    private val comparisonDetailsAdapter by lazy { ViewTypeAdapter<ViewType<*>>() }
    private lateinit var skillAdapter: SkillAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        comparisonDetailsVM = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[ComparisonDetailsViewModel::class.java]
        skillAdapter = SkillAdapter(sharedChara, SkillAdapter.FROM.COMPARISON_DETAILS)
    }

    override fun onResume() {
        super.onResume()
        binding.toolbarComparisonDetails.menu.findItem(R.id.comparison_expression_style).isChecked = UserSettings.get().getExpression()
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
            binding.comparisonDetailsSkillRecycler.apply {
                layoutManager = layoutManagerSkill
                adapter = skillAdapter
                skillAdapter.update(comparisonDetailsVM.charaTo.skills)
            }
            // tool bar action
            toolbarComparisonDetails.setNavigationOnClickListener {
                it.findNavController().navigateUp()
            }
            // tool bar title
            toolbarComparisonDetails.title =
                I18N.getString(R.string.rank_d_equipment, sharedChara.rankComparisonFrom, sharedChara.equipmentComparisonFrom) +
                " → " + I18N.getString(R.string.rank_d_equipment, sharedChara.rankComparisonTo, sharedChara.equipmentComparisonTo)

            // diff and property
            diff = comparisonDetailsVM.diffProperty
            property = comparisonDetailsVM.propertyTo
            val details = statusComparisonDetails

            chara = comparisonDetailsVM.charaTo
            val diffCP = comparisonDetailsVM.charaTo.combatPower - comparisonDetailsVM.charaFrom.combatPower
            diffCombatPower = if (diffCP > 0)
                "+$diffCP"
            else
                "$diffCP"

            diff?.let {
                setTextColor(diffCP, details.txtCombatPowerComparisonDetails)
            }

            // human-readable stats
            comparisonDetailsAdapter.setList(comparisonDetailsVM.viewList)
            comparisonDetailsHumanReadableStatsRecycler.apply {
                adapter = comparisonDetailsAdapter
                layoutManager = LinearLayoutManager(context)
            }

            setOptionItemClickListener(toolbarComparisonDetails)
        }

        setObserver()

        return binding.run {
            root
        }
    }

    private fun setTextColor(num: Int, textView: SuperTextView) {
        val color = when {
            num > 0 -> R.color.green_350
            num < 0 -> R.color.red_500
            else -> R.color.textPrimary
        }
        textView.setRightTextColor(ResourceManager.get().getColor(color))
    }

    private fun setOptionItemClickListener(toolbar: MaterialToolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.comparison_expression_style -> {
                    it.isChecked = !it.isChecked
                    UserSettings.get().setExpression(it.isChecked)
                    comparisonDetailsVM.charaTo.apply {
                        skills.forEach { skill ->
                            skill.setActionDescriptions(this.displayLevel, this.charaProperty)
                        }
                    }
                    skillAdapter.update(comparisonDetailsVM.charaTo.skills)
                    skillAdapter.notifyDataSetChanged()
                    true
                }
                R.id.comparison_details_tp -> {
                    it.isChecked = !it.isChecked
                    comparisonDetailsVM.showTP.postValue(it.isChecked)
                    true
                }
                R.id.comparison_details_def -> {
                    it.isChecked = !it.isChecked
                    comparisonDetailsVM.showDef.postValue(it.isChecked)
                    true
                }
                R.id.comparison_details_dmg -> {
                    it.isChecked = !it.isChecked
                    comparisonDetailsVM.showDmg.postValue(it.isChecked)
                    true
                }
                else -> true
            }
        }
    }

    private fun setObserver() {
        comparisonDetailsVM.showTP.observe(viewLifecycleOwner, Observer {
            comparisonDetailsAdapter.setUpdatedList(comparisonDetailsVM.viewList)
        })

        comparisonDetailsVM.showDef.observe(viewLifecycleOwner, Observer {
            comparisonDetailsAdapter.setUpdatedList(comparisonDetailsVM.viewList)
        })

        comparisonDetailsVM.showDmg.observe(viewLifecycleOwner, Observer {
            comparisonDetailsAdapter.setUpdatedList(comparisonDetailsVM.viewList)
        })
    }
}