package com.github.malitsplus.shizurunotes.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentMenuBinding>(
            inflater, R.layout.fragment_menu, container, false
        ).apply {
            clickListener = View.OnClickListener {
                when(it.id){
                    R.id.constraint_my_chara ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavMenuToNavMyChara()
                        )
                    R.id.constraint_my_chara_target ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavMenuToNavMyCharaTarget()
                        )
                    R.id.constraint_calendar ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavMenuToNavCalendar()
                        )
                    R.id.constraint_setting ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavMenuToNavSettingContainer()
                        )
                    R.id.constraint_rank_comparison ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavMenuToNavRankCompare()
                        )
                    R.id.constraint_equipment_calculator ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavMenuToNavEquipmentAll()
                        )
                    R.id.constraint_gacha_list ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavMenuToNavGachaList()
                        )
                    R.id.constraint_srt_panel ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavMenuToNavSrtList()
                        )
                    R.id.constraint_kaiser_battle ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavMenuToNavKaiserBattle()
                        )
                }
            }
        }

        return binding.root
    }

}
