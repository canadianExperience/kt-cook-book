package com.me.kt_cook_book.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.me.kt_cook_book.R
import com.me.kt_cook_book.data.apimanager.NetworkResult
import com.me.kt_cook_book.data.apimanager.models.Result
import com.me.kt_cook_book.databinding.FragmentRecipesBinding
import com.me.kt_cook_book.ui.adapters.IRecipeClickListener
import com.me.kt_cook_book.ui.adapters.RecipesAdapter
import com.me.kt_cook_book.utility.NetworkListener
import com.me.kt_cook_book.utility.exhaustive
import com.me.kt_cook_book.viewmodels.MainViewModel
import com.me.kt_cook_book.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes),
    SearchView.OnQueryTextListener,
IRecipeClickListener{

    private var _binding: FragmentRecipesBinding? = null
    private val binding  get() = _binding!!
    private val recipesAdapter by lazy { RecipesAdapter(this) }
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val recipesViewModel by viewModels<RecipesViewModel>()

    private lateinit var networkListener: NetworkListener

    private var searchView: SearchView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentRecipesBinding.bind(view)
        binding.mainViewModel = mainViewModel
        binding.recipesViewModel = recipesViewModel
        binding.lifecycleOwner = this

        setupRecyclerView()

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

        setHasOptionsMenu(true)
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

    private fun setupRecyclerView() {
        binding.recyclerview.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        showShimmerEffect()
    }

    private fun readDatabase() = mainViewModel.readRecipes.observe(viewLifecycleOwner) { databaseList ->
        showShimmerEffect()
        if (databaseList.isNotEmpty()) {
            Log.d("RecipesFragment", "requestDatabase called")
            val food = databaseList[0].foodRecipe
            recipesAdapter.setData(food.results)
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
                    searchView?.onActionViewCollapsed()
                    val action = RecipesFragmentDirections.actionRecipesFragmentToRecipesBottomSheet()
                    findNavController().navigate(action)
                }
                RecipesViewModel.RecipesEvent.BackFromRecipesBottomSheet -> {
                    apiRequest()
                }
                is RecipesViewModel.RecipesEvent.ShowToast -> {
                    Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
                }
                is RecipesViewModel.RecipesEvent.NavigateToDetailsFragment -> {
                    searchView?.onActionViewCollapsed()
                    val action = RecipesFragmentDirections.actionRecipesFragmentToDetailsFragment(event.result, event.isFavorite)
                    findNavController().navigate(action)
                }
            }.exhaustive
        }
    }

    private fun apiRequest(){
        showShimmerEffect()
        mainViewModel.apiRequest()
    }

    private fun searchApiRequest(query: String) {
        showShimmerEffect()
        mainViewModel.searchApiRequest(query)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        searchView = search.actionView as? SearchView

        searchView?.isSubmitButtonEnabled = true
        searchView?.maxWidth = Integer.MAX_VALUE
        searchView?.setOnQueryTextListener(this)
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {searchApiRequest(it) }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        return true
    }

    override fun onRecipeItemClick(result: Result) {
        recipesViewModel.onRecipeClick(result)
    }
}