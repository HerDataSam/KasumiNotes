package com.github.malitsplus.shizurunotes.ui.kaiserbattle

import android.content.Context
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
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.databinding.FragmentKaiserBattleBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle

class KaiserBattleFragment : Fragment() {

    private lateinit var sharedClanBattle: SharedViewModelClanBattle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedClanBattle = ViewModelProvider(requireActivity())[SharedViewModelClanBattle::class.java].apply {
            loadKaiserBattle()
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

        val kaiserBattleAdapter = KaiserBattleAdapter(sharedClanBattle)

        val binding = DataBindingUtil.inflate<FragmentKaiserBattleBinding>(
            inflater, R.layout.fragment_kaiser_battle, container, false
        ).apply {
            toolbarKaiserFragment.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            with(recyclerKaiser){
                layoutManager = LinearLayoutManager(context)
                adapter = kaiserBattleAdapter
                setHasFixedSize(true)
            }
        }

        sharedClanBattle.loadingFlag.observe(viewLifecycleOwner, Observer {
            if (!it){
                kaiserBattleAdapter.update(sharedClanBattle.kaiserBattleList)
            }
        })

        sharedClanBattle.loadingFlag.observe(viewLifecycleOwner,
            Observer {
                if (it) {
                    binding.kaiserEventListProgressBar.visibility = View.VISIBLE
                } else {
                    binding.kaiserEventListProgressBar.visibility = View.GONE
                }
            }
        )

        return binding.root
    }

}