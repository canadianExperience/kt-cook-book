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
import com.me.kt_cook_book.databinding.FragmentRecipesBinding
import com.me.kt_cook_book.ui.adapters.RecipesAdapter
import com.me.kt_cook_book.utility.NetworkListener
import com.me.kt_cook_book.utility.exhaustive
import com.me.kt_cook_book.viewmodels.MainViewModel
import com.me.kt_cook_book.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes) {
    private var _binding: FragmentRecipesBinding? = null
    private val binding  get() = _binding!!
    private val recipesAdapter by lazy { RecipesAdapter() }
    private val mainViewModel by viewModels<MainViewModel>()
    private val recipesViewModel by viewModels<RecipesViewModel>()

    private lateinit var networkListener: NetworkListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentRecipesBinding.bind(view)
        binding.mainViewModel = mainViewModel
        binding.recipesViewModel = recipesViewModel
        binding.lifecycleOwner = this

        setupRecyclerView(binding.recyclerview)

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner){
            recipesViewModel.backOnline = it
        }

        readDatabase()
        apiResponse()
        getRecipesEvents()

       lifecycleScope.launch {
           networkListener = NetworkListener()
           recipesViewModel.onNetworkStatusChanged(networkListener)
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
                apiRequest()
            }
    }


    private fun apiResponse() = mainViewModel.recipesResponseLiveData.observe(viewLifecycleOwner){ response ->
        when (response) {
            is NetworkResult.Success -> {
                hideShimmerEffect()
            }
            is NetworkResult.Error -> {
                hideShimmerEffect()
                recipesViewModel.showToast(response.message.toString())
            }
            is NetworkResult.Loading -> {
                showShimmerEffect()
            }
        }
    }

    private fun getRecipesEvents() = lifecycleScope.launch {
        recipesViewModel.recipesEvent.collect { event->
            when(event){
                RecipesViewModel.RecipesEvent.NavigateToRecipesBottomSheet -> {
                    val action = RecipesFragmentDirections.actionRecipesFragmentToRecipesBottomSheet()
                    findNavController().navigate(action)
                }
                RecipesViewModel.RecipesEvent.BackFromRecipesBottomSheet -> {
                    apiRequest()
                }
                is RecipesViewModel.RecipesEvent.ShowToast -> {
                    Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
                }
            }.exhaustive
        }
    }

    private fun apiRequest(){
        mainViewModel.apiRequest()
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}