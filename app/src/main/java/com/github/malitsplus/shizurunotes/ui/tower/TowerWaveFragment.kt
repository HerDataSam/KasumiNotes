package com.github.malitsplus.shizurunotes.ui.tower

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.data.WaveGroup
import com.github.malitsplus.shizurunotes.databinding.FragmentTowerWaveBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelTower
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelTowerFactory

class TowerWaveFragment : Fragment(), OnWaveClickListener {

    private lateinit var binding: FragmentTowerWaveBinding
    private lateinit var sharedTowerArea: SharedViewModelTower
    private lateinit var sharedClanBattle: SharedViewModelClanBattle
    private lateinit var towerWaveVM: TowerWaveViewModel
    private val towerWaveAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedTowerArea = ViewModelProvider(requireActivity())[SharedViewModelTower::class.java]
        sharedClanBattle = ViewModelProvider(requireActivity())[SharedViewModelClanBattle::class.java]
        towerWaveVM = ViewModelProvider(this, SharedViewModelTowerFactory(sharedTowerArea))[TowerWaveViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTowerWaveBinding.inflate(inflater, container, false)
        binding.apply {
            towerWaveToolbar.apply {
                title = sharedTowerArea.selectedTowerArea.value?.towerString
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedTowerArea.loadTowerWaveData()
        sharedTowerArea.selectedTowerArea.observe(viewLifecycleOwner) {
            binding.towerWaveProgressBar.visibility = if (it.towerWaveGroupMap.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            towerWaveAdapter.setUpdatedList(towerWaveVM.viewList)
        }
        binding.towerWaveToolbar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }
        towerWaveAdapter.setList(towerWaveVM.viewList)
        with (binding.towerWaveRecycler) {
            adapter = towerWaveAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onWaveClick(waveGroup: Map.Entry<String, WaveGroup>) {
        sharedClanBattle.mSetSelectedBoss(waveGroup.value.enemyList, waveGroup.key)
        findNavController().navigate(TowerWaveFragmentDirections.actionNavTowerWaveToNavEnemy())
    }

    override fun onItemClicked(position: Int) {
    }


}