package com.github.malitsplus.shizurunotes.ui.srt

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.SrtPanel
import com.github.malitsplus.shizurunotes.databinding.FragmentSrtListBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelSrt
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelSrtFactory
import com.github.malitsplus.shizurunotes.utils.Utils
import kotlin.math.floor

class SrtListFragment : Fragment(), OnSrtClickListener<SrtPanel> {

    private lateinit var binding: FragmentSrtListBinding
    private lateinit var sharedSrt: SharedViewModelSrt
    private lateinit var srtVM: SrtListViewModel
    private val maxSpan = floor(Utils.getScreenDPWidth() / 66.0).toInt()
    private val srtPanelAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedSrt = ViewModelProvider(requireActivity())[SharedViewModelSrt::class.java]
        srtVM = ViewModelProvider(this, SharedViewModelSrtFactory(sharedSrt))[SrtListViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        sharedSrt.selectedSrt = null
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
        binding = FragmentSrtListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedSrt.loadData()
        sharedSrt.srtList.observe(viewLifecycleOwner, {
            binding.srtListProgressBar.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            srtPanelAdapter.setUpdatedList(srtVM.viewList)
        })
        binding.srtListToolbar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }
        with (binding.srtListRecycler) {
            adapter = srtPanelAdapter
            layoutManager = GridLayoutManager(context, maxSpan).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (srtPanelAdapter.getItemViewType(position)) {
                            R.layout.item_srt_list -> maxSpan
                            R.layout.item_grid_icon -> 1
                            else -> 1
                        }
                    }
                }
            }
        }
    }

    override fun onSrtPanelClicked(item: SrtPanel) {
        sharedSrt.selectedSrt = item
        sharedSrt.isFromList = true
        findNavController().navigate(SrtListFragmentDirections.actionNavSrtListToNavSrtPanel())
    }

    override fun onItemClicked(position: Int) {
    }
}