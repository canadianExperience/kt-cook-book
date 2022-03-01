package com.me.kt_cook_book.ui.fragments.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.me.kt_cook_book.R
import com.me.kt_cook_book.databinding.FragmentIngredientsBinding
import com.me.kt_cook_book.ui.adapters.IngredientsAdapter
import com.me.kt_cook_book.viewmodels.DetailsViewModel

class IngredientsFragment : Fragment(R.layout.fragment_ingredients) {
    private var _binding: FragmentIngredientsBinding? = null
    private val binding  get() = _binding!!
    private val ingredientsAdapter by lazy { IngredientsAdapter() }
    private val detailsViewModel by viewModels<DetailsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentIngredientsBinding.bind(view)

        setupRecyclerView()

        detailsViewModel.result?.let {
            ingredientsAdapter.setData(it.extendedIngredients)
        }
    }

    private fun setupRecyclerView() {
        binding.ingredientsRecyclerview.apply {
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}