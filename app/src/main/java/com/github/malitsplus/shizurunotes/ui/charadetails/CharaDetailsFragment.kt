package com.github.malitsplus.shizurunotes.ui.charadetails

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.databinding.FragmentCharaDetailsBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.base.*
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.powerspinner.SpinnerAnimation
import com.skydoves.powerspinner.createPowerSpinnerView

// TODO: 改成使用ViewType接口和适配器，避免NestedScrollView一次性渲染全部视图造成丢帧
class CharaDetailsFragment : Fragment(), View.OnClickListener, OnLoveLevelClickListener<Pair<Int, Int>> {

    private lateinit var detailsViewModel: CharaDetailsViewModel
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var binding: FragmentCharaDetailsBinding

    private val loveLevelAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }
    private val adapterSkill by lazy { SkillAdapter(sharedChara, SkillAdapter.FROM.CHARA_DETAILS) }
    lateinit var chara: Chara
    lateinit var levelList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity()).get(SharedViewModelChara::class.java)
        detailsViewModel = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[CharaDetailsViewModel::class.java]
        chara = sharedChara.selectedChara!!
        /*sharedElementEnterTransition =
            TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move).setDuration(300)

        sharedElementReturnTransition =
            TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move).setDuration(300)*/
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onDetach() {
        detailsViewModel.updateChara()
        if (!sharedChara.backFlag)
            (activity as MainActivity).showBottomNavigation()
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        binding.rankSpinnerCharaDetail.dismissDropDown()
        binding.levelTextInputListItemEquipments.dismiss()
        //binding.toolbarCharaDetail.menu.findItem(R.id.menu_chara_show_expression).isChecked = UserSettings.get().getExpression()
        reloadChara()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharaDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            toolbarCharaDetail.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            setBookmarkIcon(toolbarCharaDetail.menu.findItem(R.id.menu_chara_bookmark))

            toolbarCharaDetail.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_chara_customize -> {
                        Navigation.findNavController(binding.root).navigate(
                            CharaDetailsFragmentDirections.actionNavCharaDetailsToNavAnalyze()
                        )
                    }
                    R.id.menu_chara_bookmark -> {
                        val bookmarked = detailsViewModel.setBookmark()
                        if (bookmarked) {
                            Snackbar.make(binding.root, R.string.my_chara_added, Snackbar.LENGTH_SHORT).show()
                        } else {
                            Snackbar.make(binding.root, R.string.my_chara_deleted, Snackbar.LENGTH_SHORT).show()
                        }
                        setBookmarkIcon(it)
                    }
                    R.id.menu_chara_show_expression -> {
                        val singleItems = I18N.getStringArray(R.array.setting_skill_expression_options)
                        val checkedItem = UserSettings.get().getExpression()
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(I18N.getString(R.string.setting_skill_expression_title))
                            .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                                if (UserSettings.get().getExpression() != which) {
                                    UserSettings.get().setExpression(which)
                                    sharedChara.mSetSelectedChara(sharedChara.selectedChara)
                                    adapterSkill.notifyDataSetChanged()
                                }
                                dialog.dismiss()
                            }.show()
                    }
                }
                true
            }

            if (sharedChara.backFlag)
                appbarCharaDetails.setExpanded(false, false)

            detailsVM = detailsViewModel
            clickListener = this@CharaDetailsFragment

            // rank spinner
            var rankList: List<Int> = listOf()
            var spinnerRank = 0
            detailsViewModel.getChara()?.let {
                rankList = it.rankList.toList()
                spinnerRank = it.displaySetting.rank
            }

            rankSpinnerCharaDetail.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    detailsViewModel.changeRank(adapter.getItem(position).toString())
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@CharaDetailsFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        rankList.toTypedArray()
                    )
                )
                setText(spinnerRank.toString())
            }

            // levels
            var spinnerLevel = 0
            detailsViewModel.getChara()?.let {
                levelList = it.levelList.toList()
                spinnerLevel = it.displaySetting.level
            }

            levelTextInputListItemEquipments.apply {
                spinnerPopupWidth = Utils.getScreenPxWidth().toInt()

                setItems(levelList)
                setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
                    detailsViewModel.changeLevel(newItem.toInt())
                }
                getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 5)
                selectItemByIndex(levelList.indexOf(spinnerLevel.toString()))
                lifecycleOwner = this@CharaDetailsFragment
            }

            /*
            levelSpinnerCharaDetail.apply {
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                    detailsViewModel.changeLevel(adapter.getItem(position).toString())
                }
                setAdapter(
                    MaterialSpinnerAdapter(
                        this@CharaDetailsFragment.requireContext(),
                        R.layout.dropdown_item_chara_list,
                        levelList.toTypedArray()
                    )
                )
                setText(spinnerLevel.toString())
            }*/

            if (detailsViewModel.mutableChara.value?.maxCharaRarity!! != 6) {
                charaStar6.visibility = View.GONE
            }

            // unique equipments
            characterUniqueEquipment.uniqueEquipmentDetailsLevel.addOnChangeListener { _, value, _ ->
                detailsViewModel.changeUniqueEquipment(value.toInt())
            }
            characterUniqueEquipment.uniqueEquipmentDetailsDisplay.doAfterTextChanged {
                if (it?.isNotBlank() == true) {
                    val level = try {
                            it.toString().toInt()
                        } catch (e: NumberFormatException) {
                            0
                        }
                    detailsViewModel.changeUniqueEquipment(level)
                    detailsViewModel.mutableChara.value?.let { chara ->
                        if (chara.displaySetting.uniqueEquipment != characterUniqueEquipment.uniqueEquipmentDetailsLevel.value.toInt()) {
                            characterUniqueEquipment.uniqueEquipmentDetailsLevel.value =
                                chara.displaySetting.uniqueEquipment.toFloat()
                        }
                    }
                }
            }

            // collapsing status view
            // make invisible at the first time
            //val wrapContent = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            //collapsedStatDetailView.measure(wrapContent, wrapContent)
            //characterUniqueEquipment.root.measure(wrapContent, wrapContent)
            // TODO: if ViewType is applied, this would not need?
            // if no unique equipment, minus the size of unique equipment height
            val statsDetailViewHeight =
                if (detailsViewModel.mutableChara.value?.uniqueEquipment?.maxEnhanceLevel!! > 0)
                    collapsedStatDetailView.height
                else
                    collapsedStatDetailView.height - characterUniqueEquipment.root.height
            val statsDetailViewLayoutParams = collapsedStatDetailView.layoutParams
            // if not backFlagged, close, else open
            if (!sharedChara.backFlag) {
                statsDetailViewLayoutParams.height = 1
                collapsedStatDetailView.visibility = View.INVISIBLE
            }
            else {
                statsDetailViewLayoutParams.height = 0
                collapsedStatDetailView.visibility = View.VISIBLE
            }
            collapsedStatDetailView.layoutParams = statsDetailViewLayoutParams

            // on click animation
            detailsCharaStatsContainer.setOnClickListener {
                val valueAnimator: ValueAnimator // for height
                val rotateAnimator: ValueAnimator // for rotation of arrow

                if (collapsedStatDetailView.height == 1) {
                    valueAnimator = ValueAnimator.ofInt(1, statsDetailViewHeight)
                    valueAnimator.doOnStart {
                        collapsedStatDetailView.visibility = View.VISIBLE
                    }
                    rotateAnimator = ValueAnimator.ofFloat(0f, 180f)
                } else {
                    valueAnimator = ValueAnimator.ofInt(statsDetailViewHeight, 1)
                    valueAnimator.doOnEnd {
                        collapsedStatDetailView.visibility = View.INVISIBLE
                    }
                    rotateAnimator = ValueAnimator.ofFloat(180f, 0f)
                }
                valueAnimator.duration = 0L
                rotateAnimator.duration = 0L
                valueAnimator.addUpdateListener {
                    val animatedValue = it.animatedValue as Int
                    val layoutParams = collapsedStatDetailView.layoutParams
                    layoutParams.height = animatedValue
                    collapsedStatDetailView.layoutParams = layoutParams
                }
                rotateAnimator.addUpdateListener {
                    val animatedRotationValue = it.animatedValue as Float
                    GoToCharaStatsDetail.rotation = animatedRotationValue
                }

                valueAnimator.start()
                rotateAnimator.start()
            }

            loveLevelAdapter.setList(detailsViewModel.loveLevelViewList)
            val maxOtherChara = 4
            charaOtherLoveLevelRecycler.apply {
                adapter = loveLevelAdapter
                layoutManager = GridLayoutManager(requireContext(), maxOtherChara).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return 1
                        }
                    }
                }
            }
        }

        // 技能循环
        val adapterAttackPattern = AttackPatternContainerAdapter(context).apply {
            initializeItems(detailsViewModel.mutableChara.value?.attackPatternList)
        }
        binding.attackPatternRecycler.apply {
            layoutManager = GridLayoutManager(context, 6).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when(adapterAttackPattern.getItemViewType(position)) {
                            BaseHintAdapter.HINT_TEXT -> 6
                            else -> 1
                        }
                    }
                }
            }
            adapter = adapterAttackPattern
        }

        // 技能 Recycler
        val layoutManagerSkill = LinearLayoutManager(context)
        binding.skillRecycler.apply {
            layoutManager = layoutManagerSkill
            adapter = adapterSkill
        }

        //观察chara变化 감시 chara 변화
        // (1.0.0去掉rank下拉框后已经可以删掉了，留着备用）
        detailsViewModel.mutableChara.observe(
            viewLifecycleOwner, { chara: Chara ->
                binding.detailsVM = detailsViewModel
                adapterSkill.update(chara.skills)
                detailsViewModel.updateChara()
            }
        )
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.detailsItemCharaContainer) {
            sharedChara.selectedChara = detailsViewModel.mutableChara.value
            val action =
                CharaDetailsFragmentDirections.actionNavCharaDetailsToNavCharaProfile()
            Navigation.findNavController(v).navigate(action)
        }

        detailsViewModel.checkAndChangeEquipment(v?.id)
    }

    private fun setBookmarkIcon(v: MenuItem) {
        val icon = ResourcesCompat.getDrawable(resources, R.drawable.mic_bookmark, context?.theme)
        if (detailsViewModel.mutableChara.value?.isBookmarked == true) {
            icon?.setTint(resources.getColor(R.color.yellow_700, context?.theme))
        } else {
            icon?.setTintList(null)
        }
        v.icon = icon
    }

    private fun reloadChara() {
        detailsViewModel.reloadChara()
        detailsViewModel.getChara()?.let {
            val spinnerRank = it.displaySetting.rank
            val spinnerLevel = it.displaySetting.level

            //binding.levelSpinnerCharaDetail.apply {
            //    setText(spinnerLevel.toString())
            //}
            binding.levelTextInputListItemEquipments.apply {
                selectItemByIndex(levelList.indexOf(spinnerLevel.toString()))
            }
            binding.rankSpinnerCharaDetail.apply {
                setText(spinnerRank.toString())
            }
        }
    }

    override fun onItemClicked(position: Int) {
    }

    override fun onLoveLevelClickedListener(unitId: Int, up: Boolean) {
        detailsViewModel.changeLoveLevel(unitId, up)
        loveLevelAdapter.setList(detailsViewModel.loveLevelViewList)
        loveLevelAdapter.notifyDataSetChanged()
    }
}