package com.github.malitsplus.shizurunotes.ui.today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.CampaignSchedule
import com.github.malitsplus.shizurunotes.data.EventSchedule
import com.github.malitsplus.shizurunotes.data.EventType
import com.github.malitsplus.shizurunotes.databinding.FragmentTodayBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.calendar.CalendarViewModel
import com.github.malitsplus.shizurunotes.ui.calendar.CalendarViewModelFactory
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.google.android.material.appbar.MaterialToolbar

class TodayFragment : Fragment(), OnTodayActionListener<EventSchedule> {
    private lateinit var binding: FragmentTodayBinding
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var sharedCalendar: CalendarViewModel
    private lateinit var todayVM: TodayViewModel
    private val todayAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedChara = ViewModelProvider(requireActivity()).get(SharedViewModelChara::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedCalendar = ViewModelProvider(requireActivity()).get(CalendarViewModel::class.java)
        sharedCalendar.initData()
        todayVM = ViewModelProvider(requireActivity(), CalendarViewModelFactory(sharedCalendar))[TodayViewModel::class.java]

        binding = FragmentTodayBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            todayRecycler.apply {
                todayAdapter.setList(todayVM.viewList)
                adapter = todayAdapter
                layoutManager = LinearLayoutManager(this.context)
            }
            setOptionItemClickListener(todayToolbar)
            this
        }
    }

    private fun setOptionItemClickListener(toolbar: MaterialToolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_today_calendar -> {
                    findNavController().navigate(TodayFragmentDirections.actionTodayToNavCalendar())
                }
            }
            true
        }
    }

    override fun onScheduleClickedListener(item: EventSchedule) {
        when {
            item is CampaignSchedule -> {

            }
            item.type == EventType.Hatsune -> {
                findNavController().navigate(TodayFragmentDirections.actionTodayToNavHatsuneStage())
            }
            item.type == EventType.ClanBattle -> {
                findNavController().navigate(TodayFragmentDirections.actionTodayToNavClanBattle())
            }
            item.type == EventType.Tower -> {
                findNavController().navigate(TodayFragmentDirections.actionTodayToNavTower())
            }
            item.type == EventType.PickUp -> {
                findNavController().navigate(TodayFragmentDirections.actionTodayToNavGachaList())
            }
        }
    }

    override fun onItemClicked(position: Int) {
    }
}