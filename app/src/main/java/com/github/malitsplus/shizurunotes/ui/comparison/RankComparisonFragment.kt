package com.github.malitsplus.shizurunotes.ui.comparison

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.databinding.FragmentRankComparisonBinding
import com.github.malitsplus.shizurunotes.ui.base.MaterialSpinnerAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory

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
            dropdownRankFrom.addTextChangedListener(afterTextChanged = { rank: CharSequence? ->
                val sizeFrom = sharedChara.charaList.value?.get(0)?.rankEquipments?.get(rank.toString().toInt())?.size
                val equipmentListFrom = mutableListOf<Int>()

                if (sizeFrom != null) {
                    for (i in sizeFrom downTo 0) {
                        equipmentListFrom.add(i)
                    }
                }
                else
                    equipmentListFrom.add(0)

                dropdownEquipmentFrom.apply {
                    setAdapter(
                        MaterialSpinnerAdapter(
                            this@RankComparisonFragment.requireContext(),
                            R.layout.dropdown_item_chara_list,
                            equipmentListFrom.toTypedArray()
                        )
                    )
                    setText(equipmentListFrom[0].toString())
                }
            })
            val sizeFrom = sharedChara.charaList.value?.get(0)?.rankEquipments?.get(comparisonViewModel.rankList[1].toString().toInt())?.size
            val equipmentListFrom = mutableListOf<Int>()

            if (sizeFrom != null) {
                for (i in sizeFrom downTo 0) {
                    equipmentListFrom.add(i)
                }
            }
            else
                equipmentListFrom.add(0)

            dropdownEquipmentFrom.apply {
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@RankComparisonFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        equipmentListFrom.toTypedArray()
                    )
                )
                setText(equipmentListFrom[0].toString())
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
            dropdownRankTo.addTextChangedListener(afterTextChanged = { rank: CharSequence? ->
                val sizeTo = sharedChara.charaList.value?.get(0)?.rankEquipments?.get(rank.toString().toInt())?.size
                val equipmentListTo = mutableListOf<Int>()

                if (sizeTo != null) {
                    for (i in sizeTo downTo 0) {
                        equipmentListTo.add(i)
                    }
                }
                else
                    equipmentListTo.add(0)

                dropdownEquipmentTo.apply {
                    setAdapter(
                        MaterialSpinnerAdapter(
                            this@RankComparisonFragment.requireContext(),
                            R.layout.dropdown_item_chara_list,
                            equipmentListTo.toTypedArray()
                        )
                    )
                    setText(equipmentListTo[0].toString())
                }
            })
            val sizeTo = sharedChara.charaList.value?.get(0)?.rankEquipments?.get(comparisonViewModel.rankList[0].toString().toInt())?.size
            val equipmentListTo = mutableListOf<Int>()

            if (sizeTo != null) {
                for (i in sizeTo downTo 0) {
                    equipmentListTo.add(i)
                }
            }
            else
                equipmentListTo.add(0)
            dropdownEquipmentTo.apply {
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@RankComparisonFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        equipmentListTo.toTypedArray()
                    )
                )
                setText(equipmentListTo[0].toString())
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
            dropdownEquipmentFrom.dismissDropDown()
            dropdownEquipmentTo.dismissDropDown()
        }
    }

}
