package com.github.malitsplus.shizurunotes.ui.comparison

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.databinding.FragmentComparisonListBinding
import com.github.malitsplus.shizurunotes.ui.base.MaterialSpinnerAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.google.android.material.appbar.MaterialToolbar

class ComparisonListFragment : Fragment() {

    private lateinit var binding: FragmentComparisonListBinding
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var comparisonListVM: ComparisonListViewModel
    private lateinit var adapter: ComparisonListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        comparisonListVM = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[ComparisonListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentComparisonListBinding.inflate(inflater, container, false)
        comparisonListVM.liveComparisonList.observe(viewLifecycleOwner, Observer {
            binding.comparisonListProgressbar.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            adapter.update(it.toMutableList())
        })
        setDropdownText()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ComparisonListAdapter(sharedChara)
        binding.apply {
            comparisonListToolbar.setNavigationOnClickListener {
                it.findNavController().navigateUp()
            }
            comparisonListToolbar.title = if (sharedChara.useMyChara)
                    I18N.getString(R.string.comparison_list_my_chara)
                else
                    I18N.getString(R.string.rank_d1_equipment_d2_to_rank_d3_to_equipment_d4,
                        sharedChara.rankComparisonFrom, sharedChara.equipmentComparisonFrom,
                        sharedChara.rankComparisonTo, sharedChara.equipmentComparisonTo
                    )
            comparisonListRecycler.apply {
                layoutManager = LinearLayoutManager(this@ComparisonListFragment.context)
                adapter = this@ComparisonListFragment.adapter
            }
        }
        setOptionItemClickListener(binding.comparisonListToolbar)
        comparisonListVM.filterDefault()
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            dropdownText1Comparison.dismissDropDown()
            dropdownText2Comparison.dismissDropDown()
            dropdownText3Comparison.dismissDropDown()
        }
    }

    private fun setOptionItemClickListener(toolbar: MaterialToolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_comparison_list_share -> {
                    exportData()
                }
            }
            true
        }
    }

    private fun setDropdownText(){
        binding.apply {
            dropdownText1Comparison.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    comparisonListVM.filter(position.toString(), null, null, null)
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@ComparisonListFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        comparisonListVM.attackTypeMap.values.toTypedArray<String>()
                    )
                )
                setText(comparisonListVM.attackTypeMap[0].toString())
            }

            dropdownText2Comparison.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    comparisonListVM.filter(null, position.toString(), null, null)
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@ComparisonListFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        comparisonListVM.positionMap.values.toTypedArray<String>()
                    )
                )
                setText(comparisonListVM.positionMap[0].toString())
            }

            dropdownText3Comparison.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    comparisonListVM.filter(null, null, position.toString(), null)
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@ComparisonListFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        comparisonListVM.sortMap.values.toTypedArray<String>()
                    )
                )
                setText(comparisonListVM.sortMap[9].toString())
            }
        }
    }

    fun exportData() {
        val components = "캐릭터,HP,물공,물방,마공,마방,물크,마크,HP자동회복,TP자동회복,회피,HP흡수,회복량,TP상승,TP소비감소,명중\n"
        var data = ""
        comparisonListVM.liveComparisonList.value?.forEach{
            data += "${it.chara.unitName},"
            data += "${it.property.getHp()},"
            data += "${it.property.getAtk()},"
            data += "${it.property.getDef()},"
            data += "${it.property.getMagicStr()},"
            data += "${it.property.getMagicDef()},"
            data += "${it.property.getPhysicalCritical()},"
            data += "${it.property.getMagicCritical()},"
            data += "${it.property.getWaveHpRecovery()},"
            data += "${it.property.getWaveEnergyRecovery()},"
            data += "${it.property.getDodge()},"
            data += "${it.property.getLifeSteal()},"
            data += "${it.property.getHpRecoveryRate()},"
            data += "${it.property.getEnergyRecoveryRate()},"
            data += "${it.property.getEnergyReduceRate()},"
            data += "${it.property.getAccuracy()}\n"
        }
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, components + data)
            type = "text/plane"
        }
        val shareIntent = Intent.createChooser(sendIntent, binding.comparisonListToolbar.title)
        startActivity(shareIntent)
    }

}
