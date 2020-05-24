package com.github.malitsplus.shizurunotes.ui.charadetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
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

            // stars
            charaStar1.setOnClickListener{ _ ->
                detailsViewModel.changeRarity(1)
                setFilled(charaStar1)
                setBlank(charaStar2)
                setBlank(charaStar3)
                setBlank(charaStar4)
                setBlank(charaStar5)
            }

            charaStar2.setOnClickListener{ _ ->
                detailsViewModel.changeRarity(2)
                setFilled(charaStar1)
                setFilled(charaStar2)
                setBlank(charaStar3)
                setBlank(charaStar4)
                setBlank(charaStar5)
            }

            charaStar3.setOnClickListener{ _ ->
                detailsViewModel.changeRarity(3)
                setFilled(charaStar1)
                setFilled(charaStar2)
                setFilled(charaStar3)
                setBlank(charaStar4)
                setBlank(charaStar5)
            }

            charaStar4.setOnClickListener{ _ ->
                detailsViewModel.changeRarity(4)
                setFilled(charaStar1)
                setFilled(charaStar2)
                setFilled(charaStar3)
                setFilled(charaStar4)
                setBlank(charaStar5)
            }

            charaStar5.setOnClickListener{ _ ->
                detailsViewModel.changeRarity(5)
                setFilled(charaStar1)
                setFilled(charaStar2)
                setFilled(charaStar3)
                setFilled(charaStar4)
                setFilled(charaStar5)
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
    }

    private fun setBlank(v: ImageView) {
        v.setImageDrawable(resources.getDrawable(R.drawable.mic_star_blank, context?.theme))
    }

    private fun setFilled(v: ImageView) {
        v.setImageDrawable(resources.getDrawable(R.drawable.mic_star_filled, context?.theme))
    }
}