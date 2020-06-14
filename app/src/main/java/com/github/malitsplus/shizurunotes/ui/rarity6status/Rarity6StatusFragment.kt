package com.github.malitsplus.shizurunotes.ui.rarity6status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.Rarity6Status
import com.github.malitsplus.shizurunotes.databinding.FragmentRarity6StatusBinding
import com.github.malitsplus.shizurunotes.ui.base.OnItemActionListener
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory

class Rarity6StatusFragment : Fragment(), OnItemClickListener<Rarity6Status> {
    lateinit var binding: FragmentRarity6StatusBinding
    lateinit var sharedChara: SharedViewModelChara
    lateinit var rarity6StatusVM: Rarity6StatusViewModel

    private val maxSpan = 4
    private val rarity6StatusAdapter by lazy { ViewTypeAdapter<ViewType<*>>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java].apply {
            backFlag = false
        }
        rarity6StatusVM = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[Rarity6StatusViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRarity6StatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            rarity6StatusToolbar.apply {
                title = sharedChara.selectedRarity6Status?.item?.itemName
                setNavigationOnClickListener { view ->
                    view.findNavController().navigateUp()
                }
            }
            rarity6StatusRecycler.apply {
                itemAnimator = null
                rarity6StatusAdapter.setList(rarity6StatusVM.viewList)
                adapter = rarity6StatusAdapter
                layoutManager = GridLayoutManager(requireContext(), maxSpan).apply {
                    spanSizeLookup = spanSize
                }
            }
            this
        }
    }

    private val spanSize = object: GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (rarity6StatusAdapter.getItemViewType(position)) {
                R.layout.item_property -> maxSpan / 2
                R.layout.item_equipment_craft_num -> maxSpan / 4
                else -> maxSpan
            }
        }
    }

    override fun onItemClicked(item: Rarity6Status) {
        TODO("Not yet implemented")
    }

    override fun onItemClicked(position: Int) {
        TODO("Not yet implemented")
    }
}