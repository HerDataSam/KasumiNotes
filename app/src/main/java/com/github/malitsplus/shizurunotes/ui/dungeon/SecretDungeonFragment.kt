package com.github.malitsplus.shizurunotes.ui.dungeon

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.data.SecretDungeonPeriod
import com.github.malitsplus.shizurunotes.databinding.FragmentSecretDungeonBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattleFactory

class SecretDungeonFragment: Fragment(), OnSecretDungeonListener<SecretDungeonPeriod> {
    private lateinit var binding: FragmentSecretDungeonBinding
    private lateinit var sharedClanBattle: SharedViewModelClanBattle
    private lateinit var secretDungeonVM: SecretDungeonViewModel
    private val secretDungeonAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedClanBattle = ViewModelProvider(requireActivity())[SharedViewModelClanBattle::class.java]
        secretDungeonVM = ViewModelProvider(this, SharedViewModelClanBattleFactory(sharedClanBattle))[SecretDungeonViewModel::class.java]
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
        binding = FragmentSecretDungeonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedClanBattle.loadSecretDungeon()
        sharedClanBattle.secretDungeonList.observe(viewLifecycleOwner) {
            binding.secretDungeonProgressBar.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            secretDungeonAdapter.setUpdatedList(secretDungeonVM.viewList)
        }
        binding.secretDungeonToolbar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }
        secretDungeonAdapter.setList(secretDungeonVM.viewList)
        with (binding.secretDungeonRecycler) {
            adapter = secretDungeonAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onItemClicked(position: Int) {
        ;
    }

    override fun onSecretDungeonClick(item: SecretDungeonPeriod) {
        sharedClanBattle.selectedSecretDungeon.value = item
        findNavController().navigate(SecretDungeonFragmentDirections.actionNavSecretDungeonToNavSecretDungeonFloor())
    }
}