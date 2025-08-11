package com.github.malitsplus.shizurunotes.ui.charaprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.data.Rarity6Status
import com.github.malitsplus.shizurunotes.databinding.FragmentCharaProfileBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.EquipmentAllKey
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelEquipment

class CharaProfileFragment : Fragment(), OnEquipmentClickListener<Equipment> {

    private lateinit var binding: FragmentCharaProfileBinding

    private val maxSpan = 6

    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var sharedEquipment: SharedViewModelEquipment
    private lateinit var charaProfileVM: CharaProfileViewModel
    private val charaProfileAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java].apply {
            backFlag = false
        }
        sharedEquipment = ViewModelProvider(requireActivity())[SharedViewModelEquipment::class.java]
        charaProfileVM =
            ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[CharaProfileViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        sharedChara.showSingleChara = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharaProfileBinding.inflate(inflater, container, false).apply {
            toolbarCharaProfile.apply {
                title = sharedChara.selectedChara?.unitName
                setNavigationOnClickListener { view ->
                    view.findNavController().navigateUp()
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.charaProfileRecycler.apply {
            charaProfileAdapter.setList(charaProfileVM.viewList)
            adapter = charaProfileAdapter
            layoutManager = GridLayoutManager(requireContext(), maxSpan).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (charaProfileAdapter.getItemViewType(position)) {
                            R.layout.item_chara_profile -> maxSpan
                            R.layout.item_property -> maxSpan / 2
                            else -> maxSpan
                        }
                    }
                }
            }
        }
    }

    override fun onEquipmentClicked(item: Equipment) {
        if (item.equipmentId != 999999) {
            sharedEquipment.selectedEquipment = item
            findNavController().navigate(CharaProfileFragmentDirections.actionNavCharaProfileToNavEquipment())
        }
    }

    override fun onRarity6Clicked(item: Rarity6Status) {
        sharedChara.selectedRarity6Status = item
        findNavController().navigate(CharaProfileFragmentDirections.actionNavCharaProfileToNavRarity6Status())
    }

    override fun onEquipmentAllClicked(item: EquipmentAllKey) {
        sharedEquipment.equipmentAllKey = item
        sharedChara.showSingleChara = true
        findNavController().navigate(CharaProfileFragmentDirections.actionNavCharaProfileToNavEquipmentAll())
    }

    override fun onItemClicked(position: Int) {
    }
}