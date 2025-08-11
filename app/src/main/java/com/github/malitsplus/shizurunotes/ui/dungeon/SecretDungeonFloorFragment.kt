package com.github.malitsplus.shizurunotes.ui.dungeon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.data.SecretDungeon
import com.github.malitsplus.shizurunotes.databinding.FragmentSecretDungeonFloorBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattleFactory

class SecretDungeonFloorFragment : Fragment(), OnSecretDungeonFloorListener<SecretDungeon> {
    lateinit var binding: FragmentSecretDungeonFloorBinding
    private lateinit var sharedClanBattle: SharedViewModelClanBattle
    private lateinit var secretDungeonFloorVM: SecretDungeonFloorViewModel
    private val secretDungeonFloorAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedClanBattle = ViewModelProvider(requireActivity())[SharedViewModelClanBattle::class.java]
        secretDungeonFloorVM = ViewModelProvider(
            this,
            SharedViewModelClanBattleFactory(sharedClanBattle)
        )[SecretDungeonFloorViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecretDungeonFloorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedClanBattle.loadSecretDungeonWaves()
        sharedClanBattle.selectedSecretDungeon.observe(viewLifecycleOwner) {
            binding.secretDungeonFloorProgressBar.visibility = if (it.dungeonFloor.isEmpty())
                View.VISIBLE
            else
                View.GONE
            secretDungeonFloorAdapter.setUpdatedList(secretDungeonFloorVM.viewList)
        }
        binding.secretDungeonFloorToolbar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }
        binding.secretDungeonFloorToolbar.title = sharedClanBattle.selectedSecretDungeon.value?.title
        secretDungeonFloorAdapter.setList(secretDungeonFloorVM.viewList)
        with(binding.secretDungeonFloorRecycler) {
            adapter = secretDungeonFloorAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onItemClicked(position: Int) {
    }

    override fun onSecretDungeonFloorClick(item: SecretDungeon) {
        sharedClanBattle.mSetSelectedBoss(item.dungeonBoss)
        findNavController().navigate(SecretDungeonFloorFragmentDirections.actionNavDungeonFloorToNavEnemy())
    }
}