package com.github.malitsplus.shizurunotes.ui.drop

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.data.Equipment
import com.github.malitsplus.shizurunotes.databinding.FragmentDropBinding
import com.github.malitsplus.shizurunotes.ui.base.BaseHintAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelEquipment
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelQuest
import com.github.malitsplus.shizurunotes.utils.Utils

class DropFragment : Fragment() {

    private lateinit var sharedEquipment: SharedViewModelEquipment
    private lateinit var sharedQuest: SharedViewModelQuest
    private lateinit var dropVM: DropViewModel
    private lateinit var binding: FragmentDropBinding
    private lateinit var mAdapter: GridSelectAdapter
    private var maxSpanNum = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedEquipment = ViewModelProvider(requireActivity())[SharedViewModelEquipment::class.java]
        sharedQuest = ViewModelProvider(requireActivity())[SharedViewModelQuest::class.java]
        dropVM = ViewModelProvider(this)[DropViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDropBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = GridSelectAdapter(requireContext(), sharedEquipment)

        //根据屏幕大小调整每行显示图标数
        if (Utils.getScreenRatio() < 2.0) {
            maxSpanNum = 5
        }

        val mLayoutManager = GridLayoutManager(context, maxSpanNum).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when(mAdapter.getItemViewType(position)) {
                        BaseHintAdapter.HINT_TEXT -> maxSpanNum
                        else -> 1
                    }
                }
            }
        }
        binding.apply {
            equipmentSearchBar.addTextChangeListener(dropVM.textWatcher)
            setOptionItemClickListener(dropToolbar)
            dropRecycler.apply {
                layoutManager = mLayoutManager
                adapter = mAdapter
                setHasFixedSize(true)
            }
        }
        setFloatingBarClickListener()
        setObservers()
    }

    override fun onResume() {
        super.onResume()
        binding.dropToolbar.menu.findItem(R.id.menu_drop_normal).isChecked = sharedQuest.includeNormal
        binding.dropToolbar.menu.findItem(R.id.menu_drop_hard).isChecked = sharedQuest.includeHard
    }

    private fun setFloatingBarClickListener() {
        binding.clickListener = View.OnClickListener { view ->
            if (view.id == R.id.drop_floating_button) {
                if (sharedEquipment.selectedDrops.value?.isNotEmpty() == true) {
                    val idList = mutableListOf<Int>()
                    sharedEquipment.selectedDrops.value?.forEach {
                        idList.add(it.itemId)
                    }
                    UserSettings.get().lastEquipmentIds = idList
                }
                view.findNavController().navigate(DropFragmentDirections.actionNavDropToNavDropQuest())
            }
        }
    }

    private fun setObservers() {
        sharedEquipment.equipmentMap.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                dropVM.equipmentList = it.values.toList()
                dropVM.refreshList()
            }
        }
        sharedEquipment.loadingFlag.observe(viewLifecycleOwner) {
            binding.dropProgressBar.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        dropVM.liveItemList.observe(viewLifecycleOwner) {
            mAdapter.update(it)
        }
    }

    private fun setOptionItemClickListener(toolbar: Toolbar) {
        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_drop_before -> {
                    val ids = UserSettings.get().lastEquipmentIds
                    if (!ids.isNullOrEmpty()) {
                        clearRecyclerView()
                        ids.forEach { id ->
                            for (item in mAdapter.itemList) {
                                if (item is Equipment && item.equipmentId == id) {
                                    sharedEquipment.selectedDrops.value?.add(item)
                                    val vh = binding.dropRecycler.findViewHolderForAdapterPosition(mAdapter.itemList.indexOf(item))
                                    vh?.let {
                                        (vh as BaseHintAdapter.InstanceViewHolder).binding.root.background = requireContext().getDrawable(R.drawable.shape_selected_background)
                                    }
                                    break
                                }
                            }
                        }
                    }
                    true
                }
                R.id.menu_drop_cancel -> {
                    clearRecyclerView()
                    true
                }
                R.id.menu_drop_normal -> {
                    it.isChecked = !it.isChecked
                    sharedQuest.includeNormal = it.isChecked
                    true
                }
                R.id.menu_drop_hard -> {
                    it.isChecked = !it.isChecked
                    sharedQuest.includeHard = it.isChecked
                    true
                }
                else -> true
            }
        }
    }

    private fun clearRecyclerView() {
        binding.dropRecycler.children.forEach {
            it.background = requireContext().getDrawable(R.drawable.shape_unselected_background)
        }
        sharedEquipment.selectedDrops.value?.clear()
    }
}