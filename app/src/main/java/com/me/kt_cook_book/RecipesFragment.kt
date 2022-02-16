package com.me.kt_cook_book

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.me.kt_cook_book.databinding.FragmentRecipesBinding

class RecipesFragment : Fragment(R.layout.fragment_recipes) {
    private var fragmentBinding: FragmentRecipesBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRecipesBinding.bind(view)
        fragmentBinding = binding

       // binding.recyclerview.sh
    }

    override fun onDestroy() {
        fragmentBinding = null
        super.onDestroy()
    }
}