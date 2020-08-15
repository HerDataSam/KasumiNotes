package com.github.malitsplus.shizurunotes.ui.mychara

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.databinding.FragmentMyCharaTargetBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory

class MyCharaTargetFragment : Fragment(), OnCharaTargetClickListener<Pair<Chara, Int>> {
    private lateinit var binding: FragmentMyCharaTargetBinding
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var myCharaTargetVM: MyCharaTargetViewModel
    private val myCharaTargetAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        myCharaTargetVM = ViewModelProvider(
            this,
            SharedViewModelCharaFactory(sharedChara)
        )[MyCharaTargetViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCharaTargetBinding.inflate(inflater, container, false)
        setObserver()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            myCharaTargetToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            setOptionItemClickListener(myCharaTargetToolbar)
            myCharaTargetAdapter.setList(myCharaTargetVM.viewList)
            with(myCharaTargetRecycler) {
                adapter = myCharaTargetAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onDetach() {
        (activity as MainActivity).showBottomNavigation()
        super.onDetach()
    }
    private fun setObserver() {
        sharedChara.loadingFlag.observe(viewLifecycleOwner, Observer {
            binding.myCharaTargetProgressBar.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
        sharedChara.charaList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                myCharaTargetAdapter.setList(myCharaTargetVM.viewList)
                myCharaTargetAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun setOptionItemClickListener(toolbar: Toolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                else -> true
            }
        }
    }

    override fun onCharaTargetClickedListener(chara: Chara, value: Int) {
        when (value) {
            10 -> {
                sharedChara.mSetSelectedChara(chara)
                sharedChara.backFlag = true
                findNavController().navigate(MyCharaTargetFragmentDirections.actionNavMyCharaTargetToNavCharaDetails())
                myCharaTargetAdapter.notifyDataSetChanged()
            }
            20 -> {
                sharedChara.mSetSelectedChara(chara)
                sharedChara.rankComparisonFrom = chara.displayRank
                sharedChara.rankComparisonTo = chara.targetRank
                sharedChara.equipmentComparisonFrom = chara.displayEquipmentNumber
                sharedChara.equipmentComparisonTo = chara.targetEquipmentNumber
                findNavController().navigate(MyCharaTargetFragmentDirections.actionNavMyCharaTargetToNavComparisonDetails())
            }
            30 -> {
                chara.isBookmarkLocked = !chara.isBookmarkLocked
                myCharaTargetAdapter.notifyDataSetChanged()
            }
            else -> {
                chara.setTargetRankEquipment(value)
                myCharaTargetAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onItemClicked(position: Int) {
    }
}