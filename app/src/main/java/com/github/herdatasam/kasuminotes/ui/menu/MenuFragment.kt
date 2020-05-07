package com.github.herdatasam.kasuminotes.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.github.herdatasam.kasuminotes.R
import com.github.herdatasam.kasuminotes.databinding.FragmentMenuBinding
import com.github.herdatasam.kasuminotes.ui.BottomNaviFragmentDirections

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
                    R.id.constraint_dungeon ->
                        it.findNavController().navigate(
                            BottomNaviFragmentDirections.actionNavBottomNavigationToNavDungeon()
                        )
                    R.id.constraint_calendar ->
                        it.findNavController().navigate(
                            BottomNaviFragmentDirections.actionNavBottomNavigationToNavCalendar()
                        )
                    R.id.constraint_setting ->
                        it.findNavController().navigate(
                            BottomNaviFragmentDirections.actionNavBottomNavigationToNavSettingContainer()
                        )
                    R.id.constraint_sekai ->
                        it.findNavController().navigate(
                            BottomNaviFragmentDirections.actionNavBottomNavigationToNavSekaiEvent()
                        )
                    R.id.constraint_rank_comparison ->
                        it.findNavController().navigate(
                            BottomNaviFragmentDirections.actionNavBottomNavigationToNavRankCompare()
                        )
                }
            }
        }

        return binding.root
    }

}
