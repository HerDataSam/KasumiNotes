package com.github.malitsplus.shizurunotes.ui.setting.extension

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.databinding.FragmentExtensionItemBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter

class ExtensionItemFragment : Fragment() {
    lateinit var binding: FragmentExtensionItemBinding
    lateinit var extensionItemVM: ExtensionItemViewModel
    private val extensionItemAdapter by lazy { ViewTypeAdapter<ViewType<*>>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        extensionItemVM = ViewModelProvider(this)[ExtensionItemViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExtensionItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        extensionItemAdapter.setList(extensionItemVM.viewList)
        with(binding.extensionItemRecycler) {
            adapter = extensionItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    // TODO: onclick listener
}