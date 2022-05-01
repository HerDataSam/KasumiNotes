package com.github.malitsplus.shizurunotes.ui.tower

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.data.TowerArea
import com.github.malitsplus.shizurunotes.databinding.FragmentTowerAreaBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelTower
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelTowerFactory

class TowerAreaFragment : Fragment(), OnTowerAreaClickListener<TowerArea> {

    private lateinit var binding: FragmentTowerAreaBinding
    private lateinit var sharedTowerArea: SharedViewModelTower
    private lateinit var towerAreaVM: TowerAreaViewModel
    private val towerAreaAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedTowerArea = ViewModelProvider(requireActivity())[SharedViewModelTower::class.java]
        towerAreaVM = ViewModelProvider(this, SharedViewModelTowerFactory(sharedTowerArea))[TowerAreaViewModel::class.java]
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTowerAreaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedTowerArea.loadData()
        sharedTowerArea.towerAreaList.observe(viewLifecycleOwner, Observer {
            binding.towerAreaProgressBar.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            towerAreaAdapter.setUpdatedList(towerAreaVM.viewList)
        })
        binding.towerAreaToolbar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }
        with (binding.towerAreaRecycler) {
            adapter = towerAreaAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onTowerAreaClicked(item: TowerArea) {
        sharedTowerArea.selectedTowerArea.postValue(item)
        findNavController().navigate(TowerAreaFragmentDirections.actionNavTowerAreaToNavTowerWave())
    }

    override fun onItemClicked(position: Int) {
    }

}