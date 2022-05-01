package com.github.malitsplus.shizurunotes.ui.specialEvent

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.databinding.FragmentSpecialEventBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle

class SpecialEventFragment : Fragment() {

    private lateinit var sharedClanBattle: SharedViewModelClanBattle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedClanBattle = ViewModelProvider(requireActivity())[SharedViewModelClanBattle::class.java].apply {
            loadSpecialEvent()
        }
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val specialEventAdapter = SpecialEventAdapter(sharedClanBattle)

        val binding = DataBindingUtil.inflate<FragmentSpecialEventBinding>(
            inflater, R.layout.fragment_special_event, container, false
        ).apply {
            toolbarSpecialEventFragment.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            with(recyclerSpecialEvent){
                layoutManager = LinearLayoutManager(context)
                adapter = specialEventAdapter
                setHasFixedSize(true)
            }
        }

        sharedClanBattle.loadingFlag.observe(viewLifecycleOwner) {
            if (!it) {
                specialEventAdapter.update(sharedClanBattle.specialBattleList)
            }
        }

        sharedClanBattle.loadingFlag.observe(viewLifecycleOwner) {
            if (it) {
                binding.specialEventListProgressBar.visibility = View.VISIBLE
            } else {
                binding.specialEventListProgressBar.visibility = View.GONE
            }
        }

        return binding.root
    }

}