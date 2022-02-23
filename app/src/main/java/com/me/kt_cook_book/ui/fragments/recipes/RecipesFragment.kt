package com.me.kt_cook_book.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.me.kt_cook_book.R
import com.me.kt_cook_book.data.apimanager.NetworkResult
import com.me.kt_cook_book.data.apimanager.models.FoodRecipe
import com.me.kt_cook_book.databinding.FragmentRecipesBinding
import com.me.kt_cook_book.ui.adapters.RecipesAdapter
import com.me.kt_cook_book.utility.exhaustive
import com.me.kt_cook_book.viewmodels.MainViewModel
import com.me.kt_cook_book.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes) {
    private var _binding: FragmentRecipesBinding? = null
    private val binding  get() = _binding!!
    private val recipesAdapter by lazy { RecipesAdapter() }
    private val mainViewModel by viewModels<MainViewModel>()
    private val recipesViewModel by viewModels<RecipesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentRecipesBinding.bind(view)

        setupRecyclerView(binding.recyclerview)
        readDatabase()

        getRecipesEvents()
    }

    private fun getRecipesEvents() = lifecycleScope.launch {
        mainViewModel.recipesEvent.collect { event->
            when(event){
                is MainViewModel.RecipesEvent.ApiCallResponse -> {
                    event.response?.let { onApiResponse(it) }
                }
            }.exhaustive
        }
    }

    private fun onApiResponse(response: NetworkResult<FoodRecipe>) = when (response) {
        is NetworkResult.Success -> {
            hideShimmerEffect()
        }
        is NetworkResult.Error -> {
            hideShimmerEffect()
            Toast.makeText(
                requireContext(),
                response.message.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
        is NetworkResult.Loading -> {
            showShimmerEffect()
        }
    }

    private fun showShimmerEffect() {
        binding.shimmer.startShimmer()
        binding.shimmer.visibility = View.VISIBLE
        binding.recyclerview.visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        binding.shimmer.stopShimmer()
        binding.shimmer.visibility = View.GONE
        binding.recyclerview.visibility = View.VISIBLE
    }

    private fun setupRecyclerView(recyclerview: RecyclerView) {
        recyclerview.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        showShimmerEffect()
    }

    private fun readDatabase() = mainViewModel.readRecipes.observe(viewLifecycleOwner) { databaseList ->
            if (databaseList.isNotEmpty()) {
                Log.d("RecipesFragment", "requestDatabase called")
                recipesAdapter.setData(databaseList[0].foodRecipe)
                hideShimmerEffect()
            } else {
                Log.d("RecipesFragment", "requestApiData called")
                mainViewModel.onRequestApiData(recipesViewModel.applyQueries())
            }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}