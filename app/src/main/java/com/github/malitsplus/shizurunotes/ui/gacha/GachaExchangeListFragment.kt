package com.github.malitsplus.shizurunotes.ui.gacha

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.databinding.FragmentGachaExchangeListBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.calendar.CalendarViewModel
import com.github.malitsplus.shizurunotes.ui.calendar.CalendarViewModelFactory

class GachaExchangeListFragment : Fragment() {
    lateinit var binding: FragmentGachaExchangeListBinding
    lateinit var sharedCalendar: CalendarViewModel
    private lateinit var gachaExchangeListVM: GachaExchangeListViewModel
    private val gachaExchangeListAdapter by lazy { ViewTypeAdapter<ViewType<*>>() }
    val maxSpan = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedCalendar = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        gachaExchangeListVM =
            ViewModelProvider(this, CalendarViewModelFactory(sharedCalendar))[GachaExchangeListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGachaExchangeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            gachaExchangeListToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }
        gachaExchangeListAdapter.setList(gachaExchangeListVM.viewList)
        with(binding.gachaExchangeListRecycler) {
            adapter = gachaExchangeListAdapter
            layoutManager = GridLayoutManager(requireContext(), maxSpan).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (gachaExchangeListAdapter.getItemViewType(position)) {
                            R.layout.item_grid_icon_chara_url -> 1
                            else -> maxSpan
                        }
                    }
                }
            }
        }
    }
}