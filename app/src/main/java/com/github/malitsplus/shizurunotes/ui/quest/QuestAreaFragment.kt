package com.github.malitsplus.shizurunotes.ui.quest

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.data.QuestArea
import com.github.malitsplus.shizurunotes.databinding.FragmentQuestAreaBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelEquipment
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelQuest
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelQuestFactory

class QuestAreaFragment : Fragment(), OnQuestAreaClickListener<QuestArea> {
    private lateinit var binding: FragmentQuestAreaBinding
    private lateinit var sharedQuest: SharedViewModelQuest
    private lateinit var sharedEquipment: SharedViewModelEquipment
    private lateinit var questAreaVM: QuestAreaViewModel
    private val questAreaAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedQuest = ViewModelProvider(requireActivity())[SharedViewModelQuest::class.java]
        sharedEquipment = ViewModelProvider(requireActivity())[SharedViewModelEquipment::class.java]
        questAreaVM = ViewModelProvider(this, SharedViewModelQuestFactory(sharedQuest))[QuestAreaViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestAreaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onDetach() {
        (activity as MainActivity).showBottomNavigation()
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        sharedQuest.selectedQuestArea = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedQuest.loadAreaData()
        sharedQuest.questAreaList.observe(viewLifecycleOwner) {
            binding.questAreaProgressBar.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            questAreaAdapter.setUpdatedList(questAreaVM.viewList)
        }
        binding.questAreaToolbar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }
        with (binding.questAreaRecycler) {
            adapter = questAreaAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onQuestAreaClicked(item: QuestArea) {
        sharedQuest.selectedQuestArea = item
        sharedEquipment.selectedDrops.value?.clear()
        findNavController().navigate(QuestAreaFragmentDirections.actionNavQuestAreaToNavDropQuest())
    }

    override fun onItemClicked(position: Int) {
    }
}