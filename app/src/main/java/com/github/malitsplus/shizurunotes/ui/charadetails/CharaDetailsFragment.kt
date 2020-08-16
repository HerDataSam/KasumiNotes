package com.github.malitsplus.shizurunotes.ui.charadetails

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.databinding.FragmentCharaDetailsBinding
import com.github.malitsplus.shizurunotes.ui.MainActivity
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.github.malitsplus.shizurunotes.ui.base.AttackPatternContainerAdapter
import com.github.malitsplus.shizurunotes.ui.base.BaseHintAdapter
import com.github.malitsplus.shizurunotes.ui.base.MaterialSpinnerAdapter
import com.github.malitsplus.shizurunotes.user.UserSettings
import kotlin.math.max

// TODO: 改成使用ViewType接口和适配器，避免NestedScrollView一次性渲染全部视图造成丢帧
class CharaDetailsFragment : Fragment(), View.OnClickListener {

    private lateinit var detailsViewModel: CharaDetailsViewModel
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var binding: FragmentCharaDetailsBinding
    private val args: CharaDetailsFragmentArgs by navArgs()

    private val adapterSkill by lazy { SkillAdapter(sharedChara, SkillAdapter.FROM.CHARA_DETAILS) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity()).get(SharedViewModelChara::class.java)
        detailsViewModel = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[CharaDetailsViewModel::class.java]

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
        binding.levelSpinnerCharaDetail.dismissDropDown()
        binding.toolbarCharaDetail.menu.findItem(R.id.menu_chara_show_expression).isChecked = UserSettings.get().getExpression()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharaDetailsBinding.inflate(
            inflater,
            container,
            false
        ).apply {
            //detailsItemChara.transitionName = "transItem_${args.charaId}"

            if (sharedChara.backFlag)
                appbarCharaDetails.setExpanded(false, false)

            detailsVM = detailsViewModel
            clickListener = this@CharaDetailsFragment

            // rank spinner
            var rankList: List<Int> = listOf()
            var displayRank = 0
            detailsViewModel.getChara()?.let {
                rankList = it.rankList.toList()
                displayRank = it.displayRank
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
                setText(rankList[max(0, rankList.size - displayRank)].toString())
            }

            // levels
            var levelList: List<Int> = listOf()
            var displayLevel = 0
            detailsViewModel.getChara()?.let {
                levelList = it.levelList.toList()
                displayLevel = it.displayLevel
            }

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
                setText(levelList[max(0, levelList.size - displayLevel)].toString())
            }

            if (detailsViewModel.mutableChara.value?.maxCharaRarity!! != 6) {
                charaStar6.visibility = View.GONE
            }

            // unique equipments
            characterUniqueEquipment.uniqueEquipmentDetailsLevel.addOnChangeListener { _, value, _ ->
                detailsViewModel.changeUniqueEquipment(value.toInt())
            }

            // collapsing status view
            // make invisible at the first time
            val wrapContent = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            collapsedStatDetailView.measure(wrapContent, wrapContent)
            characterUniqueEquipment.root.measure(wrapContent, wrapContent)
            // TODO: if ViewType is applied, this would not need?
            // if no unique equipment, minus the size of unique equipment height
            val statsDetailViewHeight =
                if (detailsViewModel.mutableChara.value?.uniqueEquipment?.maxEnhanceLevel!! > 0)
                    collapsedStatDetailView.measuredHeight
                else
                    collapsedStatDetailView.measuredHeight - characterUniqueEquipment.root.measuredHeight
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
                valueAnimator.duration = 300L
                rotateAnimator.duration = 300L
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

        }
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
                        detailsViewModel.setBookmark()
                        setBookmarkIcon(it)
                    }
                    R.id.menu_chara_show_expression -> {
                        it.isChecked = !it.isChecked
                        UserSettings.get().setExpression(it.isChecked)
                        sharedChara.mSetSelectedChara(sharedChara.selectedChara)
                        adapterSkill.notifyDataSetChanged()
                    }
                }
                true
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
            viewLifecycleOwner,
            Observer<Chara> { chara: Chara ->
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
        val icon = resources.getDrawable(R.drawable.mic_bookmark, context?.theme)
        if (detailsViewModel.mutableChara.value?.isBookmarked == true) {
            icon.setTint(resources.getColor(R.color.yellow_700, context?.theme))
        } else {
            icon.setTintList(null)
        }
        v.icon = icon
    }

    private fun setBlank(v: ImageView) {
        v.setImageDrawable(resources.getDrawable(R.drawable.mic_star_blank, context?.theme))
    }

    private fun setFilled(v: ImageView) {
        v.setImageDrawable(resources.getDrawable(R.drawable.mic_star_filled, context?.theme))
    }
}