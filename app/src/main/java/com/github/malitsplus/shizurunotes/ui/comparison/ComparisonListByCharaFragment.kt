package com.github.malitsplus.shizurunotes.ui.comparison

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.databinding.FragmentComparisonByCharaBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.github.malitsplus.shizurunotes.utils.Utils
import kotlin.math.floor

class ComparisonListByCharaFragment : Fragment(), OnCharaClickListener<Chara> {
    private lateinit var binding: FragmentComparisonByCharaBinding
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var comparisonListByCharaVM: ComparisonListByCharaViewModel
    private val maxSpan = floor(Utils.getScreenDPWidth() / 66.0).toInt()
    private val comparisonListAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        comparisonListByCharaVM = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[ComparisonListByCharaViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentComparisonByCharaBinding.inflate(inflater, container, false)
        setObserver()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        comparisonListAdapter.setUpdatedList(comparisonListByCharaVM.viewList)
        binding.apply {
            comparisonByCharaToolbar.setNavigationOnClickListener {
                it.findNavController().navigateUp()
            }
            comparisonByCharaRecycler.apply {
                adapter = comparisonListAdapter
                layoutManager = GridLayoutManager(context, maxSpan).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (comparisonListAdapter.getItemViewType(position)) {
                                R.layout.item_hint_text -> maxSpan
                                else -> 1
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setObserver() {
        sharedChara.loadingFlag.observe(viewLifecycleOwner) {
            binding.comparisonByCharaProgressBar.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
            comparisonListAdapter.setUpdatedList(comparisonListByCharaVM.viewList)
        }
    }

    override fun onCharaClicked(item: Chara) {
        sharedChara.mSetSelectedChara(item)
        item.let {
            sharedChara.rankComparisonFrom = it.displaySetting.rank
            sharedChara.equipmentComparisonFrom = it.displaySetting.equipmentNumber
            if (it.isBookmarked) {
                sharedChara.rankComparisonTo = it.targetSetting.rank
                sharedChara.equipmentComparisonTo = it.targetSetting.equipmentNumber
            }
            else {
                sharedChara.rankComparisonTo = it.displaySetting.rank
                sharedChara.equipmentComparisonTo = it.displaySetting.equipmentNumber
            }
        }
        findNavController().navigate(
            ComparisonListByCharaFragmentDirections.actionNavComparisonListByCharaToNavComparisonDetails()
        )
    }

    override fun onItemClicked(position: Int) {

    }
}