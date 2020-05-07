package com.github.herdatasam.kasuminotes.ui.sekaievent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.herdatasam.kasuminotes.R
import com.github.herdatasam.kasuminotes.databinding.FragmentSekaiEventBinding
import com.github.herdatasam.kasuminotes.ui.shared.SharedViewModelClanBattle

class SekaiEventFragment : Fragment() {

    private lateinit var sharedClanBattle: SharedViewModelClanBattle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedClanBattle = ViewModelProvider(requireActivity())[SharedViewModelClanBattle::class.java].apply {
            loadSekaiEvent()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sekaiEventAdapter = SekaiEventAdapter(sharedClanBattle)

        val binding = DataBindingUtil.inflate<FragmentSekaiEventBinding>(
            inflater, R.layout.fragment_sekai_event, container, false
        ).apply {
            toolbarSekaiFragment.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            with(recyclerSekai){
                layoutManager = LinearLayoutManager(context)
                adapter = sekaiEventAdapter
                setHasFixedSize(true)
            }
        }

        sharedClanBattle.loadingFlag.observe(viewLifecycleOwner, Observer {
            if (!it){
                sekaiEventAdapter.update(sharedClanBattle.sekaiEventList)
            }
        })

        sharedClanBattle.loadingFlag.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    binding.sekaiEventListProgressBar.visibility = View.VISIBLE
                } else {
                    binding.sekaiEventListProgressBar.visibility = View.GONE
                }
            }
        )

        return binding.root
    }

}