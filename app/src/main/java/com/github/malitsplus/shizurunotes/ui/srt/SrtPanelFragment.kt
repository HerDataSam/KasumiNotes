package com.github.malitsplus.shizurunotes.ui.srt

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
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.SrtPanel
import com.github.malitsplus.shizurunotes.databinding.FragmentSrtPanelBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelSrt
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelSrtFactory
import com.github.malitsplus.shizurunotes.utils.Utils
import kotlin.math.floor

class SrtPanelFragment : Fragment(), OnSrtClickListener<SrtPanel> {
    private lateinit var binding: FragmentSrtPanelBinding
    private lateinit var sharedSrt: SharedViewModelSrt
    private lateinit var srtPanelVM: SrtPanelViewModel
    private val maxSpan = floor(Utils.getScreenDPWidth() / 66.0).toInt()
    private val srtPanelAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }
    private var srtPanel: SrtPanel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedSrt = ViewModelProvider(requireActivity())[SharedViewModelSrt::class.java]
        srtPanelVM = ViewModelProvider(this, SharedViewModelSrtFactory(sharedSrt))[SrtPanelViewModel::class.java]
        srtPanel = sharedSrt.selectedSrt
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSrtPanelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (binding) {
            srtPanelToolbar.setNavigationOnClickListener {
                it.findNavController().navigateUp()
            }

            srtPanelToolbar.title = srtPanel?.reading ?: I18N.getString(R.string.text_srt_blank)

            srtPanelAdapter.setUpdatedList(srtPanelVM.viewList)
            with (srtPanelRecycler) {
                adapter = srtPanelAdapter
                layoutManager = GridLayoutManager(context, maxSpan).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (srtPanelAdapter.getItemViewType(position)) {
                                R.layout.item_grid_icon_srt -> 1
                                else -> maxSpan
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onSrtPanelClicked(item: SrtPanel) {
        sharedSrt.selectedSrt = item
        sharedSrt.isFromList = false
        findNavController().navigate(SrtPanelFragmentDirections.actionNavSrtPanelToNavSrtPanel())
    }

    override fun onItemClicked(position: Int) {
    }
}