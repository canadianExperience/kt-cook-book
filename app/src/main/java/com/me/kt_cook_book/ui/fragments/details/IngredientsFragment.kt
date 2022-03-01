package com.me.kt_cook_book.ui.fragments.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.me.kt_cook_book.R
import com.me.kt_cook_book.databinding.FragmentIngredientsBinding

class IngredientsFragment : Fragment(R.layout.fragment_ingredients) {
    private var _binding: FragmentIngredientsBinding? = null
    private val binding  get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentIngredientsBinding.bind(view)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}