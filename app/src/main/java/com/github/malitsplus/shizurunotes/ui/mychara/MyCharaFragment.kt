package com.github.malitsplus.shizurunotes.ui.mychara

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.data.Chara
import com.github.malitsplus.shizurunotes.databinding.FragmentMyCharaBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelChara
import com.github.malitsplus.shizurunotes.ui.shared.SharedViewModelCharaFactory
import com.github.malitsplus.shizurunotes.user.UserSettings
import com.github.malitsplus.shizurunotes.utils.Utils
import kotlin.concurrent.thread
import kotlin.math.floor

class MyCharaFragment : Fragment(), OnCharaClickListener<Chara> {
    private lateinit var binding: FragmentMyCharaBinding
    private lateinit var sharedChara: SharedViewModelChara
    private lateinit var myCharaVM: MyCharaViewModel
    private val maxSpan = floor(Utils.getScreenDPWidth() / 66.0).toInt()
    private val myCharaAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedChara = ViewModelProvider(requireActivity())[SharedViewModelChara::class.java]
        myCharaVM = ViewModelProvider(this, SharedViewModelCharaFactory(sharedChara))[MyCharaViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCharaBinding.inflate(inflater, container, false)
        setObserver()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            myCharaToolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            myCharaAdapter.setList(myCharaVM.viewList)
            with(myCharaRecycler) {
                adapter = myCharaAdapter
                layoutManager = GridLayoutManager(context, maxSpan).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (myCharaAdapter.getItemViewType(position)) {
                                R.layout.item_hint_text -> maxSpan
                                else -> 1
                            }
                        }
                    }
                }
            }
            setOptionItemClickListener(myCharaToolbar)
        }
    }

    private fun setObserver() {
        sharedChara.loadingFlag.observe(viewLifecycleOwner, Observer {
            binding.myCharaProgressBar.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
        sharedChara.charaList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                myCharaAdapter.setList(myCharaVM.viewList)
                myCharaAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun setOptionItemClickListener(toolbar: Toolbar) {
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_my_chara_select -> {
                    thread(start = true) {
                        Thread.sleep(100)
                        activity?.runOnUiThread {
                            MaterialDialog(requireContext(), MaterialDialog.DEFAULT_BEHAVIOR)
                                .title(R.string.my_chara_select_all)
                                .message(R.string.my_chara_select_all_summary)
                                .show {
                                    positiveButton(res = R.string.text_register) {
                                        sharedChara.charaList.value?.forEach { chara ->
                                            chara.setBookmark(true)
                                        }
                                        myCharaAdapter.setList(myCharaVM.viewList)
                                        myCharaAdapter.notifyDataSetChanged()
                                    }
                                    negativeButton(res = R.string.text_deny)
                                }
                        }
                    }
                    true
                }
                R.id.menu_my_chara_remove -> {
                    thread(start = true) {
                        Thread.sleep(100)
                        activity?.runOnUiThread {
                            MaterialDialog(requireContext(), MaterialDialog.DEFAULT_BEHAVIOR)
                                .title(R.string.my_chara_delete_all)
                                .message(R.string.my_chara_delete_all_summary)
                                .show {
                                    positiveButton(res = R.string.text_delete) {
                                        sharedChara.charaList.value?.forEach { chara ->
                                            chara.setBookmark(false)
                                        }
                                        myCharaAdapter.setList(myCharaVM.viewList)
                                        myCharaAdapter.notifyDataSetChanged()
                                    }
                                    negativeButton(res = R.string.text_deny)
                                }
                        }
                    }
                    true
                }
                else -> true
            }
        }
    }

        override fun onCharaClickedListener(chara: Chara) {
        chara.reverseBookmark()
        myCharaAdapter.setList(myCharaVM.viewList)
        myCharaAdapter.notifyDataSetChanged()
    }

    override fun onItemClicked(position: Int) {
    }
}