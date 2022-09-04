package com.github.malitsplus.shizurunotes.ui.information

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.databinding.FragmentInformationBinding
import com.github.malitsplus.shizurunotes.databinding.FragmentMenuBinding
import com.github.malitsplus.shizurunotes.ui.information.InformationFragmentDirections
import com.github.malitsplus.shizurunotes.ui.menu.MenuFragmentDirections

class InformationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentInformationBinding>(
            inflater, R.layout.fragment_information, container, false
        ).apply {
            clickListener = View.OnClickListener {
                when(it.id){
                    R.id.constraint_quest ->
                        it.findNavController().navigate(
                            InformationFragmentDirections.actionNavInformationToNavQuestArea()
                        )
                    R.id.constraint_dungeon ->
                        it.findNavController().navigate(
                            InformationFragmentDirections.actionNavInformationToNavDungeon()
                        )
                    R.id.constraint_secret_dungeon ->
                        it.findNavController().navigate(
                            InformationFragmentDirections.actionNavInformationToNavSecretDungeon()
                        )
                    R.id.constraint_hatsune ->
                        it.findNavController().navigate(
                            InformationFragmentDirections.actionNavInformationToNavHatsuneStage()
                        )
                    R.id.constraint_tower ->
                        it.findNavController().navigate(
                            InformationFragmentDirections.actionNavInformationToNavTowerArea()
                        )
                    R.id.constraint_clan_battle ->
                        it.findNavController().navigate(
                            InformationFragmentDirections.actionNavInformationToNavClanBattle()
                        )
                    R.id.constraint_srt_panel ->
                        it.findNavController().navigate(
                            InformationFragmentDirections.actionNavInformationToNavSrtList()
                        )
                    R.id.constraint_special_event ->
                        it.findNavController().navigate(
                            InformationFragmentDirections.actionNavInformationToNavSpecialEvent()
                        )
                    R.id.constraint_skill_search ->
                        it.findNavController().navigate(
                            InformationFragmentDirections.actionNavInformationToNavSkillSearchList()
                        )
                }
            }
        }

        return binding.root
    }

}
