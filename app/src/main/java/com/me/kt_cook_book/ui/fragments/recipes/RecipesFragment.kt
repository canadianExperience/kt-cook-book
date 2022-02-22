package com.me.kt_cook_book.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.me.kt_cook_book.R
import com.me.kt_cook_book.ui.adapters.RecipesAdapter
import com.me.kt_cook_book.databinding.FragmentRecipesBinding
import com.me.kt_cook_book.viewmodels.MainViewModel
import com.me.kt_cook_book.data.apimanager.NetworkResult
import com.me.kt_cook_book.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes) {
    private var fragmentBinding: FragmentRecipesBinding? = null
    private val recipesAdapter by lazy { RecipesAdapter() }
    private val mainViewModel by viewModels<MainViewModel>()
    private val recipesViewModel by viewModels<RecipesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRecipesBinding.bind(view)
        fragmentBinding = binding

        setupRecyclerView(binding.recyclerview)
        readDatabase()
    }

    private fun showShimmerEffect() {
        fragmentBinding?.shimmer?.startShimmer()
        fragmentBinding?.shimmer?.visibility = View.VISIBLE
        fragmentBinding?.recyclerview?.visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        fragmentBinding?.shimmer?.stopShimmer()
        fragmentBinding?.shimmer?.visibility = View.GONE
        fragmentBinding?.recyclerview?.visibility = View.VISIBLE
    }

    private fun setupRecyclerView(recyclerview: RecyclerView) {
        recyclerview.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        showShimmerEffect()
    }

    private fun readDatabase(){
        mainViewModel.readRecipes.observe(viewLifecycleOwner){databaseList ->
            if(databaseList.isNotEmpty()){
                Log.d("RecipesFragment", "requestApiData called")
                recipesAdapter.setData(databaseList[0].foodRecipe)
                hideShimmerEffect()
            } else {
                requestApiData()
            }
        }
    }

    private fun requestApiData(){
        Log.d("RecipesFragment", "requestApiData called")

        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner){response ->
            when(response){
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { recipesAdapter.setData(it) }
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
        }
    }

    override fun onDestroy() {
        fragmentBinding = null
        super.onDestroy()
    }
}