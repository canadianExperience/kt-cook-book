package com.me.kt_cook_book.ui.fragments.recipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.me.kt_cook_book.R
import com.me.kt_cook_book.adapters.RecipesAdapter
import com.me.kt_cook_book.databinding.FragmentRecipesBinding
import com.me.kt_cook_book.ui.MainViewModel
import com.me.kt_cook_book.utility.Constants.Companion.API_KEY
import com.me.kt_cook_book.utility.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes) {
    private var fragmentBinding: FragmentRecipesBinding? = null
    private val recipesAdapter by lazy { RecipesAdapter() }
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRecipesBinding.bind(view)
        fragmentBinding = binding

        setupRecyclerView(binding.recyclerview)
        requireApiData()
    }

    private fun setupRecyclerView(recyclerview: RecyclerView) {
        recyclerview.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun requireApiData(){
        mainViewModel.getRecipes(applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner){response ->
            when(response){
                is NetworkResult.Success -> {
                    //Hide shimmer
                    response.data?.let { recipesAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    //Hide shimmer
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is NetworkResult.Loading -> {
                    //Show shimmer
                }
            }
        }
    }

    private fun applyQueries(): HashMap<String,String>{
        val queries = hashMapOf<String,String>()
        queries["number"] = "1"
        queries["apiKey"] = API_KEY
        queries["type"] = "drink"
        queries["diet"] = "vegan"
        queries["addRecipeInformation"] = "true"
        queries["fillIngredients"] = "true"

        return queries
    }

    override fun onDestroy() {
        fragmentBinding = null
        super.onDestroy()
    }
}