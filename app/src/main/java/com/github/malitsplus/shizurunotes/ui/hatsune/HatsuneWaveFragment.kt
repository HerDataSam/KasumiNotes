package com.github.malitsplus.shizurunotes.ui.hatsune

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.WaveGroup
import com.github.malitsplus.shizurunotes.databinding.FragmentHatsuneWaveBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelHatsune
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelHatsuneFactory

class HatsuneWaveFragment : Fragment(), OnWaveClickListener {

    private lateinit var binding: FragmentHatsuneWaveBinding
    private lateinit var sharedHatsune: SharedViewModelHatsune
    private lateinit var sharedClanBattle: SharedViewModelClanBattle
    private lateinit var hatsuneWaveVM: HatsuneWaveViewModel
    private val hatsuneWaveAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedHatsune = ViewModelProvider(requireActivity())[SharedViewModelHatsune::class.java]
        sharedClanBattle = ViewModelProvider(requireActivity())[SharedViewModelClanBattle::class.java]
        hatsuneWaveVM =
            ViewModelProvider(this, SharedViewModelHatsuneFactory(sharedHatsune))[HatsuneWaveViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHatsuneWaveBinding.inflate(inflater, container, false)
        binding.apply {
            hatsuneWaveToolbar.apply {
                title = sharedHatsune.selectedHatsune.value!!.title
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedHatsune.loadHatsuneWaveData()
        sharedHatsune.selectedHatsune.observe(viewLifecycleOwner, Observer {
            binding.hatsuneWaveProgressBar.visibility = if (it.battleWaveGroupMap.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            hatsuneWaveAdapter.setUpdatedList(hatsuneWaveVM.viewList)
        })
        binding.hatsuneWaveToolbar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }
        hatsuneWaveAdapter.setList(hatsuneWaveVM.viewList)
        with(binding.hatsuneWaveRecycler) {
            adapter = hatsuneWaveAdapter
            layoutManager = GridLayoutManager(context, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (hatsuneWaveAdapter.getItemViewType(position)) {
                            R.layout.item_item_icon -> 1
                            else -> 2
                        }
                    }
                }
            }
            //androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        }
    }

    override fun onWaveClick(waveGroup: WaveGroup) {
        sharedClanBattle.mSetSelectedBoss(waveGroup.enemyList)
        findNavController().navigate(HatsuneWaveFragmentDirections.actionNavHatsuneWaveToNavEnemy())
    }

    override fun onItemClicked(position: Int) {
    }
}
