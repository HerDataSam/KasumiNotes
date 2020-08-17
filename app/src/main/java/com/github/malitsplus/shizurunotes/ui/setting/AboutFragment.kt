package com.github.malitsplus.shizurunotes.ui.setting

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.github.malitsplus.shizurunotes.R
import com.github.malitsplus.shizurunotes.common.I18N
import com.github.malitsplus.shizurunotes.databinding.FragmentAboutBinding
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val aboutViewModel = ViewModelProvider(this).get(AboutViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentAboutBinding>(
            inflater, R.layout.fragment_about, container, false
        ).apply {
            textVersion.text = aboutViewModel.versionText
            textBoard.apply {
                text = aboutViewModel.board
                movementMethod = LinkMovementMethod.getInstance()
            }
            textDeveloper.apply {
                text = aboutViewModel.developer
                movementMethod = LinkMovementMethod.getInstance()
            }
            textDistributor.apply {
                text = aboutViewModel.distributor
                movementMethod = LinkMovementMethod.getInstance()
            }
            textTranslator.apply {
                text = aboutViewModel.translator
                movementMethod = LinkMovementMethod.getInstance()
            }
            textLicense.apply {
                text = aboutViewModel.license
                movementMethod = LinkMovementMethod.getInstance()
            }
            textImageLicense.apply {
                text = aboutViewModel.imageLicense
                movementMethod = LinkMovementMethod.getInstance()
            }
            toolbarAboutFragment.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            imageViewAboutIcon.setOnClickListener { _ ->
                textConcept.text = aboutViewModel.concept
            }
        }
        return binding.root
    }
}