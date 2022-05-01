package com.github.malitsplus.shizurunotes.ui.mychara

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.databinding.FragmentMyCharaTargetBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.base.MaterialSpinnerAdapter
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory

class MyCharaTargetFragment : Fragment(), OnCharaTargetClickListener<Pair<Chara, Int>> {
    private lateinit var binding: FragmentMyCharaTargetBinding
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var myCharaTargetVM: MyCharaTargetViewModel
    private val myCharaTargetAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        myCharaTargetVM = ViewModelProvider(
            this,
            SharedViewModelCharaFactory(sharedChara)
        )[MyCharaTargetViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCharaTargetBinding.inflate(inflater, container, false)
        setObserver()
        setDropdownText()
        myCharaTargetVM.updateCharaList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            myCharaTargetToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            setOptionItemClickListener(myCharaTargetToolbar)
            myCharaTargetAdapter.setList(myCharaTargetVM.viewList)
            with(myCharaTargetRecycler) {
                adapter = myCharaTargetAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onDetach() {
        (activity as MainActivity).showBottomNavigation()
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            dropdownTextType.dismissDropDown()
            dropdownTextPosition.dismissDropDown()
            dropdownTextSort.dismissDropDown()
        }
        sharedChara.apply {
            rankComparisonFrom = 0
            rankComparisonTo = 0
            equipmentComparisonFrom = 0
            equipmentComparisonTo = 0
            equipmentComparisonFromList = null
            equipmentComparisonToList = null
        }
    }

    private fun setObserver() {
        sharedChara.loadingFlag.observe(viewLifecycleOwner) {
            binding.myCharaTargetProgressBar.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        sharedChara.charaList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                myCharaTargetVM.updateCharaList()
            }
        }
        myCharaTargetVM.currentCharaList.observe(viewLifecycleOwner) {
            myCharaTargetAdapter.setList(myCharaTargetVM.viewList)
            myCharaTargetAdapter.notifyDataSetChanged()

            binding.myCharaTargetNoResult.visibility = if (it.isNullOrEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun setOptionItemClickListener(toolbar: Toolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_my_chara_target_setting -> {
                    findNavController().navigate(MyCharaTargetFragmentDirections.actionNavMyCharaTargetToNavCharaTargetSettings())
                }
            }
            true
        }
    }

    private fun setDropdownText() {
        binding.apply {
            dropdownTextType.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    myCharaTargetVM.selectedAttackType = position.toString()
                    myCharaTargetVM.updateCharaList()
                }
                setAdapter(getSpinnerAdapter(1))
            }
            dropdownTextPosition.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    myCharaTargetVM.selectedPosition = position.toString()
                    myCharaTargetVM.updateCharaList()
                }
                setAdapter(getSpinnerAdapter(2))
            }
            dropdownTextSort.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    if (position.toString() == myCharaTargetVM.selectedSort)
                        myCharaTargetVM.isAsc = !myCharaTargetVM.isAsc
                    else
                        myCharaTargetVM.selectedSort = position.toString()
                    myCharaTargetVM.updateCharaList()
                }
                setAdapter(getSpinnerAdapter(3))
            }
        }
    }

    private fun getSpinnerAdapter(type: Int): MaterialSpinnerAdapter<String> {
        return MaterialSpinnerAdapter(
            requireContext(),
            R.layout.dropdown_item_chara_list,
            myCharaTargetVM.dropDownValuesMap[type] ?: arrayOf()
        )
    }

    override fun onCharaTargetClickedListener(chara: Chara, value: Int) {
        when (value) {
            10 -> {
                sharedChara.mSetSelectedChara(chara)
                sharedChara.backFlag = true
                findNavController().navigate(MyCharaTargetFragmentDirections.actionNavMyCharaTargetToNavCharaDetails())
                myCharaTargetAdapter.notifyDataSetChanged()
            }
            20 -> {
                sharedChara.apply {
                    mSetSelectedChara(chara)
                    rankComparisonFrom = chara.displaySetting.rank
                    rankComparisonTo = chara.targetSetting.rank
                    equipmentComparisonFrom = chara.displaySetting.equipmentNumber
                    equipmentComparisonTo = chara.targetSetting.equipmentNumber
                    equipmentComparisonFromList = chara.displaySetting.equipment
                    equipmentComparisonToList = chara.targetSetting.equipment
                }
                findNavController().navigate(MyCharaTargetFragmentDirections.actionNavMyCharaTargetToNavComparisonDetails())
            }
            30 -> {
                chara.setIsBookmarkLocked(!chara.isBookmarkLocked)
                myCharaTargetAdapter.notifyDataSetChanged()
            }
            else -> {
                chara.setTargetRankEquipment(value)
                myCharaTargetAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onItemClicked(position: Int) {
    }
}