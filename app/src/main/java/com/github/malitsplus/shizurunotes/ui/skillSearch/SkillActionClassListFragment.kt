package com.github.malitsplus.shizurunotes.ui.skillSearch

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
import com.github.malitsplus.shizurunotes.databinding.FragmentSkillSearchListBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara

class SkillActionClassListFragment : Fragment(), OnSkillActionClickListener<Int> {

    private lateinit var binding: FragmentSkillSearchListBinding
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var skillActionListVM: SkillActionClassListViewModel
    private val skillActionListAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        skillActionListVM = ViewModelProvider(requireActivity())[SkillActionClassListViewModel::class.java]
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
    ): View {
        binding = FragmentSkillSearchListBinding.inflate(inflater, container, false)
        setObserver()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.skillListToolbar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }

        skillActionListAdapter.setList(skillActionListVM.viewList)

        with (binding.skillListRecycler) {
            adapter = skillActionListAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)

        }
    }

    private fun setObserver() {
        sharedChara.loadingFlag.observe(viewLifecycleOwner) {
            binding.skillListProgressBar.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        sharedChara.charaList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                skillActionListAdapter.setList(skillActionListVM.viewList)
                skillActionListAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onSkillActionClicked(item: Int) {
        sharedChara.selectedActionType = item
        sharedChara.selectedActionDetails = -1
        findNavController().navigate(
            SkillActionClassListFragmentDirections.actionNavSkillSearchListToNavSkillSearchDetails()
        )
    }

    override fun onItemClicked(position: Int) {
    }

}