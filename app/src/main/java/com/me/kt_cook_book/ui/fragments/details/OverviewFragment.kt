package com.me.kt_cook_book.ui.fragments.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.me.kt_cook_book.R
import com.me.kt_cook_book.databinding.FragmentOverviewBinding
import com.me.kt_cook_book.viewmodels.DetailsViewModel

class OverviewFragment : Fragment(R.layout.fragment_overview) {
    private var _binding: FragmentOverviewBinding? = null
    private val binding  get() = _binding!!
    private val detailsViewModel by viewModels<DetailsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentOverviewBinding.bind(view)

        detailsViewModel.result?.let {
            binding.result = it
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}