package com.github.malitsplus.shizurunotes.ui.mychara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.databinding.FragmentMyCharaSettingBinding
import com.github.malitsplus.shizurunotes.ui.base.MaterialSpinnerAdapter
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.github.malitsplus.shizurunotes.utils.Utils
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlin.math.floor

class MyCharaSettingFragment : Fragment(), OnMyCharaSettingCharaListener {
    lateinit var binding: FragmentMyCharaSettingBinding
    lateinit var sharedChara: SharedViewModelChara
    lateinit var myCharaSettingVM: MyCharaSettingViewModel
    private val maxSpan = floor(Utils.getScreenDPWidth() / 66.0).toInt()
    private val charaListAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        myCharaSettingVM =
            ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[MyCharaSettingViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCharaSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            // search level
            charaSettingToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            levelSpinnerCharaSetting.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    myCharaSettingVM.searchLevel = when (position) {
                        0 -> myCharaSettingVM.levelAll
                        else -> adapter.getItem(position).toString().toInt()
                    }
                    refreshList()
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@MyCharaSettingFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        myCharaSettingVM.levelList.toTypedArray()
                    )
                )
                setText(myCharaSettingVM.levelList[0])
            }

            // search rarity
            chipGroupRarity.children.forEachIndexed { index, view ->
                if (index == 0) {
                    view.tag = myCharaSettingVM.rarityAll
                    myCharaSettingVM.chipRarityMap[myCharaSettingVM.rarityAll] = true
                } else {
                    view.tag = index
                    myCharaSettingVM.chipRarityMap[index] = false
                }
                (view as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                    val tag = buttonView.tag.toString().toInt()
                    chipGroupRarity.children.forEach { child ->
                        if (child.tag != myCharaSettingVM.rarityAll) {
                            if (tag == myCharaSettingVM.rarityAll && isChecked) {
                                (child as Chip).isChecked = false
                                myCharaSettingVM.chipRarityMap[tag] = isChecked
                            }
                        } else {
                            (child as Chip).let {
                                it.isChecked = !isChecked && (chipGroupRarity.checkedChipIds.size == 0)
                                myCharaSettingVM.chipRarityMap[tag] = isChecked
                                myCharaSettingVM.chipRarityMap[it.tag.toString().toInt()] = it.isChecked
                            }
                        }
                    }
                    refreshList()
                }
            }

            // search rank
            myCharaSettingVM.chipRankMap[myCharaSettingVM.rankAll] = true

            (sharedChara.maxCharaContentsRank downTo (myCharaSettingVM.rankDownLimit / 100)).forEach {
                val chip = layoutInflater.inflate(R.layout.layout_chip_choice, chipGroupRankEquipment, false) as Chip
                chip.text = if (it == 8) {
                    I18N.getString(R.string.text_below, it)
                } else {
                    I18N.getString(R.string.rank_d, it)
                }
                chip.tag = it * 100

                myCharaSettingVM.chipRankMap[chip.tag.toString().toInt()] = false
                chipGroupRankEquipment.addView(chip)
            }
            chipGroupRankEquipment.children.forEachIndexed { index, view ->
                if (index == 0) {
                    view.tag = myCharaSettingVM.rankAll
                    myCharaSettingVM.chipRankMap[myCharaSettingVM.rankAll] = true
                } else {
                    myCharaSettingVM.chipRankMap[index] = false
                }
                (view as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                    val tag = buttonView.tag.toString().toInt()

                    chipGroupRankEquipment.children.forEach { child ->
                        if (child.tag != myCharaSettingVM.rankAll) {
                            if (tag == myCharaSettingVM.rankAll && isChecked) {
                                (child as Chip).isChecked = false
                                myCharaSettingVM.chipRankMap[tag] = isChecked
                            }
                        } else {
                            (child as Chip).let {
                                it.isChecked = !isChecked && (chipGroupRankEquipment.checkedChipIds.size == 0)
                                myCharaSettingVM.chipRankMap[tag] = isChecked
                                myCharaSettingVM.chipRankMap[it.tag.toString().toInt()] = it.isChecked
                            }
                        }
                    }
                    refreshList()
                }
            }

            // search position
            chipGroupPosition.children.forEachIndexed { index, view ->
                if (index == 0) {
                    view.tag = myCharaSettingVM.positionAll
                    myCharaSettingVM.chipPositionMap[myCharaSettingVM.positionAll] = true
                } else {
                    view.tag = index
                    myCharaSettingVM.chipPositionMap[index] = false
                }
                (view as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                    val tag = buttonView.tag.toString().toInt()
                    chipGroupPosition.children.forEach { child ->
                        if (child.tag != myCharaSettingVM.positionAll) {
                            if (tag == myCharaSettingVM.positionAll && isChecked) {
                                (child as Chip).isChecked = false
                                myCharaSettingVM.chipPositionMap[tag] = isChecked
                            }
                        } else {
                            (child as Chip).let {
                                it.isChecked = !isChecked && (chipGroupPosition.checkedChipIds.size == 0)
                                myCharaSettingVM.chipPositionMap[tag] = isChecked
                                myCharaSettingVM.chipPositionMap[it.tag.toString().toInt()] = it.isChecked
                            }
                        }
                    }
                    refreshList()
                }
            }

            // search attack type
            chipGroupType.children.forEachIndexed { index, view ->
                if (index == 0) {
                    view.tag = myCharaSettingVM.typeAll
                    myCharaSettingVM.chipTypeMap[myCharaSettingVM.typeAll] = true
                } else {
                    view.tag = index
                    myCharaSettingVM.chipTypeMap[index] = false
                }
                (view as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                    val tag = buttonView.tag.toString().toInt()
                    chipGroupType.children.forEach { child ->
                        if (child.tag != myCharaSettingVM.typeAll) {
                            if (tag == myCharaSettingVM.typeAll && isChecked) {
                                (child as Chip).isChecked = false
                                myCharaSettingVM.chipTypeMap[tag] = isChecked
                            }
                        } else {
                            (child as Chip).let {
                                it.isChecked = !isChecked && (chipGroupType.checkedChipIds.size == 0)
                                myCharaSettingVM.chipTypeMap[tag] = isChecked
                                myCharaSettingVM.chipTypeMap[it.tag.toString().toInt()] = it.isChecked
                            }
                        }
                    }
                    refreshList()
                }
            }

            settingApplyButton.setOnClickListener {
                if (myCharaSettingVM.applySetting())
                    Snackbar.make(binding.root, R.string.text_applied, Snackbar.LENGTH_SHORT).show()
                else
                    Snackbar.make(binding.root, R.string.text_equipment_all_wrong, Snackbar.LENGTH_SHORT).show()
                refreshList()
            }

            refreshList()

            charaSettingCharaRecycler.apply {
                adapter = charaListAdapter
                layoutManager = GridLayoutManager(context, maxSpan).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (charaListAdapter.getItemViewType(position)) {
                                R.layout.item_hint_text -> maxSpan
                                else -> 1
                            }
                        }
                    }
                }
            }

            // setting rarity
            raritySpinnerChangeTo.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    myCharaSettingVM.settingRarity = when (position) {
                        0 -> myCharaSettingVM.rarityAll
                        else -> adapter.getItem(position).toString().toInt()
                    }
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@MyCharaSettingFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        myCharaSettingVM.raritySettingList.toTypedArray()
                    )
                )
                setText(myCharaSettingVM.raritySettingList[0])
            }

            // setting level
            levelSpinnerChangeTo.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    myCharaSettingVM.settingLevel = when (position) {
                        0 -> myCharaSettingVM.levelAll
                        else -> adapter.getItem(position).toString().toInt()
                    }
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@MyCharaSettingFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        myCharaSettingVM.levelSettingList.toTypedArray()
                    )
                )
                setText(myCharaSettingVM.levelSettingList[0])
            }

            // setting rank
            rankSpinnerChangeTo.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    myCharaSettingVM.settingRank = when (position) {
                        0 -> myCharaSettingVM.rankAll
                        else -> adapter.getItem(position).toString().toInt()
                    }
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@MyCharaSettingFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        myCharaSettingVM.rankSettingList.toTypedArray()
                    )
                )
                setText(myCharaSettingVM.rankSettingList[0])
            }

            // setting equipment
            equipmentSpinnerChangeTo.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    myCharaSettingVM.settingEquipment = when (position) {
                        0 -> myCharaSettingVM.equipmentAll
                        else -> adapter.getItem(position).toString().toInt()
                    }
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@MyCharaSettingFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        myCharaSettingVM.equipmentSettingList.toTypedArray()
                    )
                )
                setText(myCharaSettingVM.equipmentSettingList[0])
            }

            charaSettingCharaListsOnOff.setOnCheckedChangeListener { _, isChecked ->
                myCharaSettingVM.setAllEnableList(isChecked)
                refreshList(false)
            }
        }
    }

    private fun refreshList(refresh: Boolean = true) {
        if (refresh) {
            myCharaSettingVM.refreshChara()
            binding.charaSettingCharaListsOnOff.isChecked = true
        }
        charaListAdapter.setList(myCharaSettingVM.viewList)
        charaListAdapter.notifyDataSetChanged()
    }

    override fun charaClickListener(item: Pair<Chara, Boolean>) {
    }

    override fun onItemClicked(position: Int) {
        myCharaSettingVM.enableList[position] = !myCharaSettingVM.enableList[position]
        refreshList(false)
    }

}