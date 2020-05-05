package com.github.herdatasam.kasuminotes.ui.clanbattle.clanbattledetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.herdatasam.kasuminotes.R
import com.github.herdatasam.kasuminotes.databinding.FragmentClanBattleBossDetailsBinding
import com.github.herdatasam.kasuminotes.ui.shared.SharedViewModelClanBattle
import com.github.herdatasam.kasuminotes.ui.base.AttackPatternContainerAdapter
import com.github.herdatasam.kasuminotes.ui.base.BaseHintAdapter
import com.github.herdatasam.kasuminotes.ui.clanbattle.clanbattledetails.adapters.ClanBattleBossChildAdapter
import com.github.herdatasam.kasuminotes.ui.clanbattle.clanbattledetails.adapters.ClanBattleBossSkillAdapter

/**
 * A simple [Fragment] subclass.
 */
class ClanBattleBossDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sharedClanBattle = ViewModelProvider(requireActivity()).get(SharedViewModelClanBattle::class.java)
//        val detailsVM = ViewModelProvider(this, SharedViewModelClanBattleFactory(sharedClanBattle))[ClanBattleDetailsViewModel::class.java]
        val binding = DataBindingUtil.inflate<FragmentClanBattleBossDetailsBinding>(
            inflater,
            R.layout.fragment_clan_battle_boss_details,
            container,
            false
        ).apply {
            boss = sharedClanBattle.selectedBoss

            val adapterAttackPattern = AttackPatternContainerAdapter(context).apply {
                initializeItems(sharedClanBattle.selectedBoss?.attackPatternList)
            }
            sharedClanBattle.selectedBoss?.let {
                bossAttackPatternRecyclerView.apply {
                    layoutManager = GridLayoutManager(context, 6).apply {
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return when(adapterAttackPattern.getItemViewType(position)) {
                                    BaseHintAdapter.HINT_TEXT -> 6
                                    else -> 1
                                }
                            }
                        }
                    }
                    adapter = adapterAttackPattern
                    setHasFixedSize(true)
                }

                childrenRecycler.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter =
                        ClanBattleBossChildAdapter(
                            it.children
                        )
                    setHasFixedSize(true)
                }

                bossSkillRecycler.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter =
                        ClanBattleBossSkillAdapter(
                            it.skills,
                            sharedClanBattle
                        )
                    setHasFixedSize(true)
                }

                clanBattleBossDetailsToolbar.title = it.name
                it
            }

            clanBattleBossDetailsToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }

        return binding.root
    }

}
