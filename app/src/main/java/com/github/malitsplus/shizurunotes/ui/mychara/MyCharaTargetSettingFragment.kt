package com.github.malitsplus.shizurunotes.ui.mychara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.databinding.FragmentMyCharaTargetSettingBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory

class MyCharaTargetSettingFragment : Fragment(), OnCharaTargetClickListener<Chara> {
    lateinit var binding: FragmentMyCharaTargetSettingBinding
    lateinit var sharedChara: SharedViewModelChara
    lateinit var myCharaTargetSettingVM: MyCharaTargetSettingViewModel
    val myCharaTargetSettingAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        myCharaTargetSettingVM =
            ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[MyCharaTargetSettingViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCharaTargetSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            charaTargetSettingToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            itemModel = myCharaTargetSettingVM.sampleChara

            targetApplyButton.setOnClickListener {
                sharedChara.charaList.value?.filter { it.isBookmarked }?.forEach {
                    it.setTargetRankEquipment(
                        myCharaTargetSettingVM.sampleChara.targetSetting.rank * 100
                                + myCharaTargetSettingVM.sampleChara.targetSetting.equipmentNumber
                    )
                }
            }
            myCharaTargetSettingAdapter.setList(myCharaTargetSettingVM.viewList)
            with(myCharaTargetSettingRecycler) {
                adapter = myCharaTargetSettingAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun onCharaTargetClickedListener(chara: Chara, value: Int) {
        when (value) {
            10, 20, 30 -> {

            }

            else -> {
                chara.setTargetRankEquipment(value)
                myCharaTargetSettingAdapter.setList(myCharaTargetSettingVM.viewList)
                myCharaTargetSettingAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onItemClicked(position: Int) {
    }

}