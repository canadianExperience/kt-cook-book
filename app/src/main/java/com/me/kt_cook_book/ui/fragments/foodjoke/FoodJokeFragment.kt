package com.me.kt_cook_book.ui.fragments.foodjoke

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.me.kt_cook_book.R
import com.me.kt_cook_book.databinding.FragmentFoodJokeBinding

class FoodJokeFragment : Fragment(R.layout.fragment_food_joke) {
    private var fragmentBinding: FragmentFoodJokeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentFoodJokeBinding.bind(view)
        fragmentBinding = binding
    }
    override fun onDestroy() {
        fragmentBinding = null
        super.onDestroy()
    }
}