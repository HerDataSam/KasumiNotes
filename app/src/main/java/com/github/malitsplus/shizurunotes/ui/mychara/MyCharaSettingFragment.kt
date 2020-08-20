package com.github.malitsplus.shizurunotes.ui.mychara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.databinding.FragmentMyCharaSettingBinding
import com.github.malitsplus.shizurunotes.ui.base.MaterialSpinnerAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory

class MyCharaSettingFragment : Fragment() {
    lateinit var binding: FragmentMyCharaSettingBinding
    lateinit var sharedChara: SharedViewModelChara
    lateinit var myCharaSettingVM: MyCharaSettingViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        myCharaSettingVM = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[MyCharaSettingViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCharaSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (binding) {
            levelSpinnerCharaSetting.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    myCharaSettingVM.settingLevel = adapter.getItem(position).toString().toInt()
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@MyCharaSettingFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        myCharaSettingVM.levelList.toTypedArray()
                    )
                )
                setText(myCharaSettingVM.levelList[0].toString())
            }
            settingApplyButton.setOnClickListener {
                myCharaSettingVM.applySetting()
            }
        }
    }
}