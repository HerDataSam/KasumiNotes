package com.github.malitsplus.shizurunotes.ui.drop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.databinding.FragmentDropQuestBinding
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelEquipment
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelQuest
import com.github.malitsplus.shizurunotes.user.UserSettings

class DropQuestFragment : Fragment() {

    private lateinit var binding: FragmentDropQuestBinding
    private lateinit var sharedEquipment: SharedViewModelEquipment
    private lateinit var sharedQuest: SharedViewModelQuest
    private lateinit var dropQuestVM: DropQuestViewModel
    private lateinit var dropQuestAdapter: DropQuestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedEquipment = ViewModelProvider(requireActivity())[SharedViewModelEquipment::class.java]
        sharedQuest = ViewModelProvider(requireActivity())[SharedViewModelQuest::class.java]
        dropQuestVM = ViewModelProvider(
            this,
            DropQuestViewModelFactory(sharedQuest, sharedEquipment.selectedDrops.value)
        )[DropQuestViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dropQuestAdapter = DropQuestAdapter(requireContext(), sharedEquipment)
        binding = FragmentDropQuestBinding.inflate(
            inflater, container, false
        ).apply {
            questToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            questRecycler.apply {
                adapter = dropQuestAdapter
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }
        }
        setOptionItemClickListener(binding.questToolbar)

        sharedQuest.apply {
            loadingFlag.observe(viewLifecycleOwner) {
                binding.questProgressBar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
            questList.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    dropQuestVM.search()
                }
            }
            if (questList.value.isNullOrEmpty()) {
                loadData()
            }
        }

        dropQuestVM.searchedQuestList.observe(viewLifecycleOwner) {
            dropQuestAdapter.update(it)
        }

        return binding.root
    }

    private fun setOptionItemClickListener(toolbar: Toolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_drop_quest_simple -> {
                    UserSettings.get().reverseDropQuestSimple()
                    dropQuestAdapter.notifyDataSetChanged()
                }

                else -> {
                }
            }
            true
        }
    }

}
