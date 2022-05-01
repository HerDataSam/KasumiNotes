package com.github.malitsplus.shizurunotes.ui.setting.extension

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.malitsplus.shizurunotes.data.extension.Extension
import com.github.malitsplus.shizurunotes.databinding.FragmentExtensionListBinding
import com.github.malitsplus.shizurunotes.ui.base.ViewType
import com.github.malitsplus.shizurunotes.ui.base.ViewTypeAdapter
import com.github.malitsplus.shizurunotes.user.UserSettings

class ExtensionFragment : Fragment(), OnExtensionClickListener<Extension> {
    lateinit var binding: FragmentExtensionListBinding
    lateinit var extensionVM: ExtensionViewModel
    private val extensionAdapter by lazy { ViewTypeAdapter<ViewType<*>>(onItemActionListener = this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        extensionVM = ViewModelProvider(this)[ExtensionViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExtensionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = extensionVM.viewList
        extensionAdapter.setList(list)
        with(binding.extensionRecycler) {
            adapter = extensionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.textExtensionNoResult.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    override fun onExtensionClickedListener(extension: Extension) {
        UserSettings.get().selectedExtension = extension
        findNavController().navigate(ExtensionFragmentDirections.actionNavExtensionListToNavExtensionItem())
    }

    override fun onItemClicked(position: Int) {

    }
}