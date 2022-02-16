package com.me.kt_cook_book

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.me.kt_cook_book.databinding.FragmentFavoriteRecipesBinding

class FavoriteRecipesFragment : Fragment(R.layout.fragment_favorite_recipes) {
    private var fragmentBinding: FragmentFavoriteRecipesBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentFavoriteRecipesBinding.bind(view)
        fragmentBinding = binding
    }

    override fun onDestroy() {
        fragmentBinding = null
        super.onDestroy()
    }
}