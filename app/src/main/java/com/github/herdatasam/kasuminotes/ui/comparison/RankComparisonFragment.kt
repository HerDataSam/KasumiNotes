package com.github.herdatasam.kasuminotes.ui.comparison

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.github.herdatasam.kasuminotes.R
import com.github.herdatasam.kasuminotes.databinding.FragmentRankComparisonBinding
import com.github.herdatasam.kasuminotes.ui.base.MaterialSpinnerAdapter
import com.github.herdatasam.kasuminotes.ui.shared.SharedViewModelChara
import com.github.herdatasam.kasuminotes.ui.shared.SharedViewModelCharaFactory

class RankComparisonFragment : Fragment() {

    lateinit var binding: FragmentRankComparisonBinding
    lateinit var sharedChara: SharedViewModelChara
    lateinit var comparisonViewModel: ComparisonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        comparisonViewModel = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[ComparisonViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRankComparisonBinding.inflate(layoutInflater, container, false).apply {
            rankComparisonToolbar.setNavigationOnClickListener {
                it.findNavController().navigateUp()
            }
            // from
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
            dropdownEquipmentFrom.apply {
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@RankComparisonFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        comparisonViewModel.equipmentList.toTypedArray()
                    )
                )
                setText(comparisonViewModel.equipmentList[0].toString())
            }
            // to
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
            dropdownEquipmentTo.apply {
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@RankComparisonFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        comparisonViewModel.equipmentList.toTypedArray()
                    )
                )
                setText(comparisonViewModel.equipmentList[0].toString())
            }
            // button
            calculateButton.setOnClickListener {
                sharedChara.rankComparisonFrom = dropdownRankFrom.text.toString().toInt()
                sharedChara.rankComparisonTo = dropdownRankTo.text.toString().toInt()
                sharedChara.equipmentComparisonFrom = dropdownEquipmentFrom.text.toString().toInt()
                sharedChara.equipmentComparisonTo = dropdownEquipmentTo.text.toString().toInt()
                it.findNavController().navigate(RankComparisonFragmentDirections.actionNavRankCompareToNavCompareList())
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            dropdownRankFrom.dismissDropDown()
            dropdownRankTo.dismissDropDown()
        }
    }

}
