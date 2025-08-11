package com.github.malitsplus.shizurunotes.ui.gacha

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.data.GachaSchedule
import com.github.malitsplus.shizurunotes.databinding.FragmentGachaListBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.calendar.CalendarViewModel
import com.github.malitsplus.shizurunotes.ui.calendar.CalendarViewModelFactory

class GachaListFragment : Fragment(), OnGachaClickListener<GachaSchedule> {
    lateinit var binding: FragmentGachaListBinding
    lateinit var sharedCalendar: CalendarViewModel
    lateinit var gachaListVM: GachaListViewModel
    private val gachaListAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onDetach() {
        (activity as MainActivity).showBottomNavigation()
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedCalendar = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        gachaListVM = ViewModelProvider(this, CalendarViewModelFactory(sharedCalendar))[GachaListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGachaListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            gachaListToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }
        gachaListAdapter.setList(gachaListVM.viewList)
        with(binding.gachaListRecycler) {
            adapter = gachaListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onGachaClickedListener(item: GachaSchedule) {
        sharedCalendar.selectedGacha = item
        findNavController().navigate(GachaListFragmentDirections.actionNavGachaListToGachaExchangeList())
    }

    override fun onItemClicked(position: Int) {
    }
}