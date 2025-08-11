package com.github.malitsplus.shizurunotes.ui.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.databinding.FragmentAnalyzeBinding
import com.github.malitsplus.shizurunotes.ui.base.MaterialSpinnerAdapter
import com.github.malitsplus.shizurunotes.ui.charadetails.SkillAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class AnalyzeFragment : Fragment() {

    private lateinit var binding: FragmentAnalyzeBinding
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var analyzeVM: AnalyzeViewModel
    private lateinit var starViewList: List<ImageView>
    private lateinit var skillAdapter: SkillAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        analyzeVM = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[AnalyzeViewModel::class.java]
        skillAdapter = SkillAdapter(sharedChara, SkillAdapter.FROM.CHARA_ANALYZE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        binding.viewModel = analyzeVM
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //sharedChara.backFlag = false
        with(binding) {

            // 星星的6个ImageView
            starViewList = listOf(
                charaStar1,
                charaStar2,
                charaStar3,
                charaStar4,
                charaStar5,
                charaStar6
            )

            // 标题条
            analyzeToolbar.apply {
                sharedChara.selectedChara?.let {
                    title = it.unitName
                }
                setNavigationOnClickListener {
                    it.findNavController().navigateUp()
                }
            }
            setOptionItemClickListener(analyzeToolbar)

            // Rank下拉框
            rankDropdown.apply {
                setAdapter(
                    MaterialSpinnerAdapter(
                        context,
                        R.layout.dropdown_item_chara_list,
                        analyzeVM.rankList.toTypedArray()
                    )
                )
                setText(analyzeVM.rank.toString())
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    analyzeVM.changeRank(analyzeVM.rankList[position])
                }
            }

            // Level下拉框
            levelDropdown.apply {
                setAdapter(
                    MaterialSpinnerAdapter(
                        context,
                        R.layout.dropdown_item_chara_list,
                        analyzeVM.levelList.toTypedArray()
                    )
                )
                setText(analyzeVM.level.toString())
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    analyzeVM.changeLevel(analyzeVM.levelList[position])
                }
            }

            characterEquipments.apply {
                clickListener = analyzeVM
            }
            characterUniqueEquipment.apply {
                clickListener = analyzeVM
            }
            characterUniqueEquipment.uniqueEquipmentDetailsLevel.addOnChangeListener { _, value, _ ->
                analyzeVM.changeUniqueEquipment(value.toInt())
            }
            characterUniqueEquipment.uniqueEquipmentDetailsDisplay.doAfterTextChanged {
                if (it?.isNotBlank() == true) {
                    analyzeVM.changeUniqueEquipment(it.toString().toInt())
                    analyzeVM.chara.value?.let { chara ->
                        if (chara.displaySetting.uniqueEquipment != characterUniqueEquipment.uniqueEquipmentDetailsLevel.value.toInt()) {
                            characterUniqueEquipment.uniqueEquipmentDetailsLevel.value =
                                chara.displaySetting.uniqueEquipment.toFloat()
                        }
                    }
                }
            }
            //guessCombatButton.setOnClickListener {
            //    val guess = guessCombatText.text.toString().toInt()
            //    analyzeVM.findCharaSim(guess)
            //}
            // 角色星级点击监听
            for (i in 1..6) {
                starViewList[i - 1].setOnClickListener {
                    changeStarImage(i)
                    analyzeVM.changeRarity(i)
                }
            }

            // 如果没有6星则隐藏
            analyzeVM.chara.value?.let {
                if (it.maxCharaRarity == 5) {
                    charaStar6.visibility = View.GONE
                }
                changeStarImage(it.displaySetting.rarity)
            }

            // 敌人等级slider
            enemyLevelSlider.valueTo = sharedChara.maxEnemyLevel.toFloat()
            enemyLevelSlider.addOnChangeListener { _, value, _ ->
                analyzeVM.enemyLevel = value.toInt()
                analyzeVM.enemyProperty.def = value.toDouble()
                updateViewModel()
            }

            // 敌人命中slider
            enemyAccuracySlider.addOnChangeListener { _, value, _ ->
                analyzeVM.enemyAccuracy = value.toInt()
                updateViewModel()
            }

            // 敌人回避slider
            enemyDodgeSlider.addOnChangeListener { _, value, _ ->
                analyzeVM.enemyDodge = value.toInt()
                updateViewModel()
            }

            val layoutManagerSkill = LinearLayoutManager(context)
            skillRecyclerAnalyze.apply {
                layoutManager = layoutManagerSkill
                adapter = skillAdapter
                skillAdapter.update(analyzeVM.chara.value?.skills!!)
            }
        }
        analyzeVM.chara.observe(viewLifecycleOwner, charaObserver)
    }

    // 改变星星的填充
    private fun changeStarImage(rarity: Int) {
        for (i in 1..6) {
            if (i <= rarity) {
                starViewList[i - 1].setImageResource(R.drawable.mic_star_filled)
            } else {
                starViewList[i - 1].setImageResource(R.drawable.mic_star_blank)
            }
        }
    }

    // property变化观察器
    private val charaObserver = Observer<Chara> {
        binding.analyzePropertyGroup.apply {
            property = it.charaProperty
            combatPower = it.combatPower
            loveLevel = it.displaySetting.loveLevel
        }
        skillAdapter.update(it.skills)
        updateViewModel()
    }

    private fun setOptionItemClickListener(toolbar: MaterialToolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_save -> {
                    analyzeVM.updateChara()
                    Snackbar.make(binding.root, R.string.text_saved, Snackbar.LENGTH_SHORT).show()
                }

                R.id.comparison_expression_style -> {
                    val singleItems = I18N.getStringArray(R.array.setting_skill_expression_options)
                    val checkedItem = UserSettings.get().getExpression()
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(I18N.getString(R.string.setting_skill_expression_title))
                        .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                            if (UserSettings.get().getExpression() != which) {
                                UserSettings.get().setExpression(which)
                                skillAdapter.update(analyzeVM.chara.value?.skills!!)
                                skillAdapter.notifyDataSetChanged()
                            }
                            dialog.dismiss()
                        }.show()
                }
            }
            true
        }
    }

    private fun updateViewModel() {
        binding.viewModel = analyzeVM
    }
}