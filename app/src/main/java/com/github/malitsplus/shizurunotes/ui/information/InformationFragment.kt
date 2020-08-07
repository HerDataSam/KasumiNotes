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

class InformationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentInformationBinding>(
            inflater, R.layout.fragment_information, container, false
        ).apply {
            clickListener = View.OnClickListener {
                when(it.id){
                    R.id.constraint_dungeon ->
                        it.findNavController().navigate(
                            InformationFragmentDirections.actionNavInformationToNavDungeon()
                        )
                    R.id.constraint_sekai ->
                        it.findNavController().navigate(
                            InformationFragmentDirections.actionNavInformationToNavSekaiEvent()
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
                }
            }
        }

        return binding.root
    }

}
