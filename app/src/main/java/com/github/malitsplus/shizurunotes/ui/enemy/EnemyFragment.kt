package com.github.malitsplus.shizurunotes.ui.enemy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Skill
import com.github.malitsplus.shizurunotes.databinding.FragmentEnemyBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattle
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelClanBattleFactory
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EnemyFragment : Fragment(), OnEnemyActionListener {

    lateinit var binding: FragmentEnemyBinding
    lateinit var sharedClanBattle: SharedViewModelClanBattle
    lateinit var enemyVM: EnemyViewModel
    private val maxSpan = 6
    private val enemyAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedClanBattle = ViewModelProvider(requireActivity())[SharedViewModelClanBattle::class.java]
        enemyVM = ViewModelProvider(this, SharedViewModelClanBattleFactory(sharedClanBattle))[EnemyViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnemyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        //binding.enemyToolbar.menu.findItem(R.id.menu_enemy_show_expression).isChecked = UserSettings.get().getExpression()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (binding) {
            enemyToolbar.apply {
                sharedClanBattle.selectedEnemyList.let {
                    title = if (it?.size == 1) {
                        it[0].name
                    } else if (sharedClanBattle.selectedEnemyTitle.isNotEmpty()) {
                        sharedClanBattle.selectedEnemyTitle
                    } else {
                        I18N.getString(R.string.title_enemy)
                    }
                }
                setNavigationOnClickListener {
                    it.findNavController().navigateUp()
                }
            }
            enemyToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_enemy_show_expression -> {
                        val singleItems = I18N.getStringArray(R.array.setting_skill_expression_options)
                        val checkedItem = UserSettings.get().getExpression()
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(I18N.getString(R.string.setting_skill_expression_title))
                            .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                                if (UserSettings.get().getExpression() != which) {
                                    UserSettings.get().setExpression(which)
                                    enemyAdapter.setList(enemyVM.viewList)
                                    enemyAdapter.notifyDataSetChanged()
                                }
                                dialog.dismiss()
                            }.show()
                    }
                }
                true
            }
            enemyAdapter.setList(enemyVM.viewList)
            enemyRecycler.apply {
                adapter = enemyAdapter
                layoutManager = GridLayoutManager(requireContext(), maxSpan).apply {
                    spanSizeLookup = spanSize
                }
            }
            this
        }
    }

    private val spanSize = object: GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (enemyAdapter.getItemViewType(position)) {
                R.layout.item_attack_pattern -> 1
                R.layout.item_resist_property -> maxSpan / 2
                else -> maxSpan
            }
        }
    }

    override fun onMinionClicked(skill: Skill) {
        if (skill.enemyMinionList.isNotEmpty()) {
            sharedClanBattle.selectedMinion = skill.enemyMinionList
            findNavController().navigate(EnemyFragmentDirections.actionNavEnemyToNavMinion())
        }
    }

    override fun onItemClicked(position: Int) {
    }
}
