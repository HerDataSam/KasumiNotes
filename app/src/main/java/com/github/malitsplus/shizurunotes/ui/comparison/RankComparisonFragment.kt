package com.github.malitsplus.shizurunotes.ui.comparison

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.databinding.FragmentRankComparisonBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.base.MaterialSpinnerAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.github.malitsplus.shizurunotes.user.UserSettings

class RankComparisonFragment : Fragment() {

    lateinit var binding: FragmentRankComparisonBinding
    lateinit var sharedChara: SharedViewModelChara
    lateinit var comparisonViewModel: ComparisonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        comparisonViewModel = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[ComparisonViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onDetach() {
        (activity as MainActivity).showBottomNavigation()
        super.onDetach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankComparisonBinding.inflate(layoutInflater, container, false).apply {
            rankComparisonToolbar.setNavigationOnClickListener {
                it.findNavController().navigateUp()
            }

            // from
            // add rankList
            dropdownRankFrom.apply {
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@RankComparisonFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        comparisonViewModel.rankList.toTypedArray()
                    )
                )
                setText(comparisonViewModel.rankList[1].toString())
            }
            // add text change listener
            dropdownRankFrom.addTextChangedListener(afterTextChanged = { rank: CharSequence? ->
                buildEquipmentList(dropdownEquipmentFrom, rank.toString().toInt())
            })
            // add equipmentList
            buildEquipmentList(dropdownEquipmentFrom, UserSettings.get().contentsMaxRank - 1)

            // to
            // add rankList
            dropdownRankTo.apply {
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@RankComparisonFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        comparisonViewModel.rankList.toTypedArray()
                    )
                )
                setText(comparisonViewModel.rankList[0].toString())
            }
            // add text change listener
            dropdownRankTo.addTextChangedListener(afterTextChanged = { rank: CharSequence? ->
                buildEquipmentList(dropdownEquipmentTo, rank.toString().toInt())
            })
            // add equipmentList
            buildEquipmentList(dropdownEquipmentTo, UserSettings.get().contentsMaxRank)

            // button
            calculateButton.setOnClickListener {
                sharedChara.rankComparisonFrom = dropdownRankFrom.text.toString().toInt()
                sharedChara.rankComparisonTo = dropdownRankTo.text.toString().toInt()
                sharedChara.equipmentComparisonFrom = dropdownEquipmentFrom.text.toString().toInt()
                sharedChara.equipmentComparisonTo = dropdownEquipmentTo.text.toString().toInt()
                it.findNavController().navigate(RankComparisonFragmentDirections.actionNavRankCompareToNavCompareList())
            }

            calculateMyCharaButton.setOnClickListener {
                sharedChara.useMyChara = true
                it.findNavController().navigate(RankComparisonFragmentDirections.actionNavRankCompareToNavCompareList())
            }

            calculateByCharaButton.setOnClickListener {
                it.findNavController().navigate(RankComparisonFragmentDirections.actionNavRankCompareToNavCompareListByChara())
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            dropdownRankFrom.dismissDropDown()
            dropdownRankTo.dismissDropDown()
            dropdownEquipmentFrom.dismissDropDown()
            dropdownEquipmentTo.dismissDropDown()
        }
        sharedChara.apply {
            useMyChara = false
            rankComparisonFrom = 0
            rankComparisonTo = 0
            equipmentComparisonFrom = 0
            equipmentComparisonTo = 0
        }
    }

    private fun buildEquipmentList(view: AutoCompleteTextView, rank: Int) {
        val size = if (UserSettings.get().contentsMaxRank == rank)
                UserSettings.get().contentsMaxEquipment
            else
                6
        val equipmentList = mutableListOf<Int>()

        for (i in size downTo 0) {
            equipmentList.add(i)
        }

        view.apply {
            setAdapter(
                MaterialSpinnerAdapter(
                    this@RankComparisonFragment.requireContext(),
                    R.layout.dropdown_item_chara_list,
                    equipmentList.toTypedArray()
                )
            )
            setText(equipmentList[0].toString())
        }
    }
}
