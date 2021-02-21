package com.github.malitsplus.shizurunotes.ui.comparison

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import androidx.core.widget.doAfterTextChanged
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
import com.github.malitsplus.shizurunotes.ui.base.MaterialSpinnerAdapter
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.charadetails.SkillAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ComparisonDetailsFragment : Fragment(), OnSettingClickListener {
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
        //binding.toolbarComparisonDetails.menu.findItem(R.id.comparison_expression_style).isChecked = UserSettings.get().getExpression()
        binding.toolbarComparisonDetails.menu.findItem(R.id.comparison_details_tp).isChecked = UserSettings.get().getShowTP()
        binding.toolbarComparisonDetails.menu.findItem(R.id.comparison_details_def).isChecked = UserSettings.get().getShowDef()
        binding.toolbarComparisonDetails.menu.findItem(R.id.comparison_details_dmg).isChecked = UserSettings.get().getShowDmg()
        binding.toolbarComparisonDetails.menu.findItem(R.id.comparison_details_comparison_table).isChecked = comparisonDetailsVM.showComp.value ?: true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                skillAdapter.update(comparisonDetailsVM.charaTo.value?.skills!!)
            }
            // tool bar action
            toolbarComparisonDetails.setNavigationOnClickListener {
                it.findNavController().navigateUp()
            }
            // tool bar title
            toolbarComparisonDetails.title =
                I18N.getString(R.string.rank_d1_equipment_d2_to_rank_d3_to_equipment_d4,
                    comparisonDetailsVM.propertySettingFrom.rank, comparisonDetailsVM.propertySettingFrom.equipmentNumber,
                    comparisonDetailsVM.propertySettingTo.rank, comparisonDetailsVM.propertySettingTo.equipmentNumber)

            // register all data
            comparisonVM = comparisonDetailsVM
            clickListener = this@ComparisonDetailsFragment

            // human-readable stats
            comparisonDetailsAdapter.setList(comparisonDetailsVM.viewList)
            comparisonDetailsHumanReadableStatsRecycler.apply {
                adapter = comparisonDetailsAdapter
                layoutManager = LinearLayoutManager(context)
            }

            // display or setting
            displayOrSetting(comparisonDetailsVM.showSetting)

            statusComparisonSettingButton.setOnClickListener {
                comparisonDetailsVM.showSetting = !comparisonDetailsVM.showSetting
                displayOrSetting(comparisonDetailsVM.showSetting)
            }

            // property settings
            var levelList: List<Int> = listOf()
            var rankList: List<Int> = listOf()
            comparisonDetailsVM.charaTo.value?.let {
                levelList = it.levelList.toList()
                rankList = it.rankList.toList()

                // rarity from and to
                if (it.maxCharaRarity < 6) {
                    charaStar6From.visibility = View.GONE
                    charaStar6To.visibility = View.GONE
                }
            }

            // level from
            comparisonDetailsLevelSpinnerFrom.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    comparisonDetailsVM.changeLevel(adapter.getItem(position).toString().toInt(), from = true)
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@ComparisonDetailsFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        levelList.toTypedArray()
                    )
                )
                setText(comparisonDetailsVM.propertySettingFrom.level.toString())
            }
            // level to
            comparisonDetailsLevelSpinnerTo.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    comparisonDetailsVM.changeLevel(adapter.getItem(position).toString().toInt(), from = false)
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@ComparisonDetailsFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        levelList.toTypedArray()
                    )
                )
                setText(comparisonDetailsVM.propertySettingTo.level.toString())
            }

            // rank from
            comparisonDetailsRankSpinnerFrom.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    comparisonDetailsVM.changeRank(adapter.getItem(position).toString().toInt(), from = true)
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@ComparisonDetailsFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        rankList.toTypedArray()
                    )
                )
                setText(comparisonDetailsVM.propertySettingFrom.rank.toString())
            }
            // rank to
            comparisonDetailsRankSpinnerTo.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    comparisonDetailsVM.changeRank(adapter.getItem(position).toString().toInt(), from = false)
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@ComparisonDetailsFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        rankList.toTypedArray()
                    )
                )
                setText(comparisonDetailsVM.propertySettingTo.rank.toString())
            }

            // unique equipment level from
            comparisonDetailsCharaFromUniqueEquipment.uniqueEquipmentDetailsLevel.addOnChangeListener  { _, value, _ ->
                comparisonDetailsVM.changeUniqueEquipment(value.toInt(), true)
            }
            comparisonDetailsCharaFromUniqueEquipment.uniqueEquipmentDetailsDisplay.doAfterTextChanged {
                if (it?.isNotBlank() == true) {
                    val level = try {
                        it.toString().toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    comparisonDetailsVM.changeUniqueEquipment(level, true)
                    if (comparisonDetailsVM.propertySettingFrom.uniqueEquipment != comparisonDetailsCharaFromUniqueEquipment.uniqueEquipmentDetailsLevel.value.toInt()) {
                        comparisonDetailsCharaFromUniqueEquipment.uniqueEquipmentDetailsLevel.value =
                            comparisonDetailsVM.propertySettingFrom.uniqueEquipment.toFloat()
                    }
                }
            }

            // unique equipment level to
            comparisonDetailsCharaToUniqueEquipment.uniqueEquipmentDetailsLevel.addOnChangeListener  { _, value, _ ->
                comparisonDetailsVM.changeUniqueEquipment(value.toInt(), false)
            }
            comparisonDetailsCharaToUniqueEquipment.uniqueEquipmentDetailsDisplay.doAfterTextChanged {
                if (it?.isNotBlank() == true) {
                    val level = try {
                        it.toString().toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    comparisonDetailsVM.changeUniqueEquipment(level, false)
                    if (comparisonDetailsVM.propertySettingTo.uniqueEquipment != comparisonDetailsCharaToUniqueEquipment.uniqueEquipmentDetailsLevel.value.toInt()) {
                        comparisonDetailsCharaToUniqueEquipment.uniqueEquipmentDetailsLevel.value =
                            comparisonDetailsVM.propertySettingTo.uniqueEquipment.toFloat()
                    }
                }
            }

            setOptionItemClickListener(toolbarComparisonDetails)
        }

        setObserver()

        return binding.run {
            root
        }
    }

    private fun setOptionItemClickListener(toolbar: MaterialToolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.comparison_expression_style -> {
                    val singleItems = I18N.getStringArray(R.array.setting_skill_expression_options)
                    val checkedItem = UserSettings.get().getExpression()
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(I18N.getString(R.string.setting_skill_expression_title))
                        .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                            if (UserSettings.get().getExpression() != which) {
                                UserSettings.get().setExpression(which)
                                skillAdapter.update(comparisonDetailsVM.charaTo.value?.skills!!)
                                skillAdapter.notifyDataSetChanged()
                            }
                            dialog.dismiss()
                        }.show()
                    true
                }
                R.id.comparison_details_comparison_table -> {
                    it.isChecked = !it.isChecked
                    comparisonDetailsVM.showComp.postValue(it.isChecked)
                    true
                }
                R.id.comparison_details_tp -> {
                    it.isChecked = !it.isChecked
                    comparisonDetailsVM.showTP.postValue(it.isChecked)
                    UserSettings.get().setShowTP(it.isChecked)
                    true
                }
                R.id.comparison_details_def -> {
                    it.isChecked = !it.isChecked
                    comparisonDetailsVM.showDef.postValue(it.isChecked)
                    UserSettings.get().setShowDef(it.isChecked)
                    true
                }
                R.id.comparison_details_dmg -> {
                    it.isChecked = !it.isChecked
                    comparisonDetailsVM.showDmg.postValue(it.isChecked)
                    UserSettings.get().setShowDmg(it.isChecked)
                    true
                }
                else -> true
            }
        }
    }

    private fun setObserver() {
        comparisonDetailsVM.showTP.observe(viewLifecycleOwner, {
            comparisonDetailsAdapter.setUpdatedList(comparisonDetailsVM.viewList)
        })

        comparisonDetailsVM.showDef.observe(viewLifecycleOwner, {
            comparisonDetailsAdapter.setUpdatedList(comparisonDetailsVM.viewList)
        })

        comparisonDetailsVM.showDmg.observe(viewLifecycleOwner, {
            comparisonDetailsAdapter.setUpdatedList(comparisonDetailsVM.viewList)
        })
        comparisonDetailsVM.showComp.observe(viewLifecycleOwner, {
            binding.comparisonDetailsStatusDetails.visibility = if (comparisonDetailsVM.showComp.value != false)
                View.VISIBLE
            else
                View.GONE
        })
        comparisonDetailsVM.charaTo.observe(viewLifecycleOwner, {
            binding.comparisonVM = comparisonDetailsVM
            skillAdapter.update(comparisonDetailsVM.charaTo.value?.skills!!)
            skillAdapter.notifyDataSetChanged()
            comparisonDetailsAdapter.setUpdatedList(comparisonDetailsVM.viewList)
            changeTitle()
        })
        comparisonDetailsVM.charaFrom.observe(viewLifecycleOwner, {
            binding.comparisonVM = comparisonDetailsVM
            comparisonDetailsAdapter.setUpdatedList(comparisonDetailsVM.viewList)
            changeTitle()
        })
    }

    private fun changeTitle() {
        binding.toolbarComparisonDetails.title =
            I18N.getString(R.string.rank_d1_equipment_d2_to_rank_d3_to_equipment_d4,
                comparisonDetailsVM.propertySettingFrom.rank, comparisonDetailsVM.propertySettingFrom.equipmentNumber,
                comparisonDetailsVM.propertySettingTo.rank, comparisonDetailsVM.propertySettingTo.equipmentNumber)
    }

    private fun displayOrSetting(isOpen: Boolean) {
        binding.apply {
            if (isOpen) {
                comparisonDetailsCharaDisplay.visibility = View.GONE
                comparisonDetailsCharaSettings.visibility = View.VISIBLE
                statusComparisonSettingButton.text = I18N.getString(R.string.status_comparison_button_close)
            } else {
                comparisonDetailsCharaDisplay.visibility = View.VISIBLE
                comparisonDetailsCharaSettings.visibility = View.GONE
                statusComparisonSettingButton.text = I18N.getString(R.string.status_comparison_button)
            }
        }
    }

    override fun onRarityClicked(rarity: Int, from: Boolean) {
        comparisonDetailsVM.changeRarity(rarity, from)
    }

    override fun onEquipmentClicked(number: Int, from: Boolean) {
        comparisonDetailsVM.changeEquipment(number, from)
    }

    override fun onUniqueEquipmentClicked(number: Int, from: Boolean) {
        TODO("Not yet implemented")
    }

}