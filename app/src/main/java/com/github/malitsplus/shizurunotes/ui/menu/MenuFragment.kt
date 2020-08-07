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
                    R.id.constraint_calendar ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavBottomNavigationToNavCalendar()
                        )
                    R.id.constraint_setting ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavBottomNavigationToNavSettingContainer()
                        )
                    R.id.constraint_rank_comparison ->
                        it.findNavController().navigate(
                            MenuFragmentDirections.actionNavBottomNavigationToNavRankCompare()
                        )
                }
            }
        }

        return binding.root
    }

}
