package com.me.kt_cook_book.ui.fragments.details

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.me.kt_cook_book.R
import com.me.kt_cook_book.databinding.FragmentInstructionsBinding
import com.me.kt_cook_book.viewmodels.DetailsViewModel

class InstructionsFragment : Fragment(R.layout.fragment_instructions) {
    private var _binding: FragmentInstructionsBinding? = null
    private val binding  get() = _binding!!
    private val detailsViewModel by viewModels<DetailsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentInstructionsBinding.bind(view)

        val websiteUrl = detailsViewModel.result?.sourceUrl
        websiteUrl?.let {
            binding.instructionsWebView.webViewClient = object : WebViewClient() {}
            binding.instructionsWebView.loadUrl(websiteUrl)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}