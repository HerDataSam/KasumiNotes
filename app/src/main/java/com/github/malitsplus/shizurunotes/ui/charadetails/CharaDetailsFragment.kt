package com.github.malitsplus.shizurunotes.ui.charadetails

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
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
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.github.malitsplus.shizurunotes.ui.base.AttackPatternContainerAdapter
import com.github.malitsplus.shizurunotes.ui.base.BaseHintAdapter
import com.github.malitsplus.shizurunotes.ui.base.MaterialSpinnerAdapter
import com.google.android.material.slider.Slider
import kotlinx.android.synthetic.main.item_chara_unique_equipment_detail.*

class CharaDetailsFragment : Fragment(), View.OnClickListener {

    private lateinit var detailsViewModel: CharaDetailsViewModel
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var binding: FragmentCharaDetailsBinding
    private val args: CharaDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity()).get(SharedViewModelChara::class.java)

        sharedElementEnterTransition =
            TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move).setDuration(300)

        sharedElementReturnTransition =
            TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move).setDuration(300)
    }

    override fun onResume() {
        super.onResume()
        binding.rankSpinnerCharaDetail.dismissDropDown()
        binding.levelSpinnerCharaDetail.dismissDropDown()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chara_details,
            container,
            false
        )

        detailsViewModel = ViewModelProvider(
            this,
            SharedViewModelCharaFactory(sharedChara)
        ).get(CharaDetailsViewModel::class.java)

        // tool bar and rank
        binding.apply {
            detailsItemChara.transitionName = "transItem_${args.charaId}"
            toolbarCharaDetail.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            var rankList: List<Int> = listOf()
            detailsViewModel.getChara()?.let {
                rankList = it.rankList.toList()
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
                setText(rankList[0].toString())
            }

            // levels
            var levelList: List<Int> = listOf()
            var contentsMaxLevel: Int = 0
            detailsViewModel.getChara()?.let {
                levelList = it.levelList.toList()
                contentsMaxLevel = it.maxCharaContentsLevel
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
                setText(levelList[levelList.size - contentsMaxLevel].toString())
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
            val statsDetailViewHeight = if (detailsViewModel.mutableChara.value?.uniqueEquipment?.maxEnhanceLevel!! > 0)
                collapsedStatDetailView.measuredHeight
            else
                collapsedStatDetailView.measuredHeight - characterUniqueEquipment.root.measuredHeight
            val statsDetailViewLayoutParams = collapsedStatDetailView.layoutParams
            statsDetailViewLayoutParams.height = 1
            collapsedStatDetailView.visibility = View.INVISIBLE
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
                }
                else {
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

            if (sharedChara.backFlag)
                appbarCharaDetails.setExpanded(false, false)

        }.also {
            it.clickListener = this
        }

        //攻击顺序
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

        //技能 Recycler
        val layoutManagerSkill = LinearLayoutManager(context)
        val adapterSkill = SkillAdapter(sharedChara)
        binding.skillRecycler.apply {
            layoutManager = layoutManagerSkill
            adapter = adapterSkill
        }

        //观察chara变化 감시 chara 변화
        detailsViewModel.mutableChara.observe(
            viewLifecycleOwner,
            Observer<Chara> { chara: Chara ->
                binding.detailsVM = detailsViewModel
                adapterSkill.update(chara.skills)
            }
        )

        return binding.run {
            detailsVM = detailsViewModel
            root
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.detailsItemCharaContainer) {
            val action =
                CharaDetailsFragmentDirections.actionNavCharaDetailsToNavCharaProfile()
            Navigation.findNavController(v).navigate(action)
        }

        detailsViewModel.checkAndChangeEquipment(v?.id)
    }

    private fun setBlank(v: ImageView) {
        v.setImageDrawable(resources.getDrawable(R.drawable.mic_star_blank, context?.theme))
    }

    private fun setFilled(v: ImageView) {
        v.setImageDrawable(resources.getDrawable(R.drawable.mic_star_filled, context?.theme))
    }

}