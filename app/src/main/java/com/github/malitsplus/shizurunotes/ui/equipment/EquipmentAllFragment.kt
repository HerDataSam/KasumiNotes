package com.github.malitsplus.shizurunotes.ui.equipment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.common.ResourceManager
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.data.Item
import com.github.malitsplus.shizurunotes.databinding.FragmentEquipmentAllBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.*
import com.github.malitsplus.shizurunotes.utils.Utils
import com.google.android.material.slider.Slider
import kotlin.math.floor

class EquipmentAllFragment : Fragment(), OnEquipmentActionListener<Equipment>, View.OnClickListener {
    lateinit var binding: FragmentEquipmentAllBinding
    lateinit var sharedChara: SharedViewModelChara
    lateinit var sharedEquipment: SharedViewModelEquipment
    lateinit var equipmentAllVM: EquipmentAllViewModel

    private val maxSpan = floor(Utils.getScreenDPWidth() / 66.0).toInt()
    val equipmentAllAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        sharedEquipment = ViewModelProvider(requireActivity())[SharedViewModelEquipment::class.java]
        equipmentAllVM = ViewModelProvider(this, SharedViewModelCharaEquipmentFactory(sharedChara, sharedEquipment))[EquipmentAllViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        sharedEquipment.selectedDrops.value?.clear()
        when (equipmentAllVM.sortMethod) {
            SortMethod.ByNum -> {
                binding.equipmentAllToolbar.menu.findItem(R.id.menu_equipment_sort_by_num).isChecked = true
                binding.equipmentAllToolbar.menu.findItem(R.id.menu_equipment_sort_by_id).isChecked = false
            }
            else -> {
                binding.equipmentAllToolbar.menu.findItem(R.id.menu_equipment_sort_by_id).isChecked = true
                binding.equipmentAllToolbar.menu.findItem(R.id.menu_equipment_sort_by_num).isChecked = false
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEquipmentAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            equipmentAllToolbar.apply {
                title = if (sharedChara.showSingleChara)
                    I18N.getString(R.string.s_equipment_all, equipmentAllVM.charaList[0].unitName)
                else
                    I18N.getString(R.string.s_equipment_all, I18N.getString(R.string.text_all))
                setNavigationOnClickListener { view ->
                    view.findNavController().navigateUp()
                }
            }
            setOptionItemClickListener(equipmentAllToolbar)

            clickListener = this@EquipmentAllFragment

            equipmentAllRecycler.apply {
                equipmentAllAdapter.setList(equipmentAllVM.viewList)
                adapter = equipmentAllAdapter
                layoutManager = GridLayoutManager(requireContext(), maxSpan).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (equipmentAllAdapter.getItemViewType(position)) {
                                R.layout.item_equipment_craft_num -> 1
                                else -> maxSpan
                            }
                        }
                    }
                }
            }
            //setPressedState()
            this
        }
    }

    private fun setOptionItemClickListener(toolbar: Toolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_equipment_sort_by_num -> {
                    equipmentAllVM.sortMethod = SortMethod.ByNum
                    it.isChecked = true
                    binding.equipmentAllToolbar.menu.findItem(R.id.menu_equipment_sort_by_id).isChecked = false
                }
                else -> {
                    equipmentAllVM.sortMethod = SortMethod.ByCraftId
                    it.isChecked = true
                    binding.equipmentAllToolbar.menu.findItem(R.id.menu_equipment_sort_by_num).isChecked = false
                }
            }
            equipmentAllAdapter.setList(equipmentAllVM.viewList)
            equipmentAllAdapter.notifyDataSetChanged()
            true
        }
    }

    override fun onClick(v: View?) {
        sharedEquipment.equipmentAllKey = when (v?.id) {
            R.id.equipment_all_button_to_max -> EquipmentAllKey.ToMax
            R.id.equipment_all_button_to_contents_max -> EquipmentAllKey.ToContentsMax
            R.id.equipment_all_button_to_target -> EquipmentAllKey.ToTarget
            else -> EquipmentAllKey.ToMax
        }
        //setPressedState()
        equipmentAllAdapter.setList(equipmentAllVM.viewList)
        equipmentAllAdapter.notifyDataSetChanged()
    }

    fun setPressedState() {
        binding.equipmentAllButtonToMax.isPressed = false
        binding.equipmentAllButtonToContentsMax.isPressed = false
        binding.equipmentAllButtonToTarget.isPressed = false
        when (sharedEquipment.equipmentAllKey) {
            EquipmentAllKey.ToMax -> binding.equipmentAllButtonToMax.isPressed = true
            EquipmentAllKey.ToContentsMax -> binding.equipmentAllButtonToContentsMax.isPressed = true
            EquipmentAllKey.ToTarget -> binding.equipmentAllButtonToTarget.isPressed = true
            else -> binding.equipmentAllButtonToMax.isPressed = true
        }
    }

    override fun onItemClickedListener(item: Item) {
        if (item.itemId in 101000..139999) {
            sharedEquipment.setDrop(item)
            findNavController().navigate(EquipmentAllFragmentDirections.actionNavEquipmentAllToNavDropQuest())
        }
    }

    override fun onItemClicked(position: Int) {
    }

    override val onSliderActionListener: Slider.OnChangeListener
        get() = TODO("Not yet implemented")
}