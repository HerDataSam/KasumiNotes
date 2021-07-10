package com.github.malitsplus.shizurunotes.ui.skillsearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.data.action.ActionParameter
import com.github.malitsplus.shizurunotes.databinding.FragmentSkillSearchDetailsBinding
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory

class SkillActionClassDetailsFragment: Fragment() {
    private lateinit var binding: FragmentSkillSearchDetailsBinding
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var skillSearchDetailsVM: SkillActionClassDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        skillSearchDetailsVM = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[SkillActionClassDetailsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSkillSearchDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val skillActionClassDetailsAdapter = SkillActionClassDetailsAdapter(sharedChara)
        skillActionClassDetailsAdapter.update(skillSearchDetailsVM.charaList)

        binding.apply {
            skillDetailsToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            skillDetailsToolbar.title = ActionParameter.description(sharedChara.selectedActionType)

            with(skillDetailsRecycler) {
                layoutManager = LinearLayoutManager(context)
                adapter = skillActionClassDetailsAdapter
                setHasFixedSize(true)
            }


        }
    }
}