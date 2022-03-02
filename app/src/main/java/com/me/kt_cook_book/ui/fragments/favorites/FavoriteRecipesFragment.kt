package com.me.kt_cook_book.ui.fragments.favorites

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.me.kt_cook_book.R
import com.me.kt_cook_book.data.apimanager.models.Result
import com.me.kt_cook_book.databinding.FragmentFavoriteRecipesBinding
import com.me.kt_cook_book.ui.adapters.IRecipeClickListener
import com.me.kt_cook_book.ui.adapters.RecipesAdapter
import com.me.kt_cook_book.ui.fragments.recipes.RecipesFragmentDirections
import com.me.kt_cook_book.utility.exhaustive
import com.me.kt_cook_book.viewmodels.FavoriteRecipesViewModel
import com.me.kt_cook_book.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment(R.layout.fragment_favorite_recipes),
    IRecipeClickListener {
    private var _binding: FragmentFavoriteRecipesBinding? = null
    private val binding  get() = _binding!!
    private val recipesAdapter by lazy { RecipesAdapter(this) }
    private val favoritesViewModel by viewModels<FavoriteRecipesViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFavoriteRecipesBinding.bind(view)

        setupRecyclerView()
        readDatabase()
        getFavoriteRecipesEvents()

        setHasOptionsMenu(true)
    }

    private fun setupRecyclerView() {
        binding.favoriteRecipesRecyclerView.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun readDatabase() = favoritesViewModel.favoriteRecipes.observe(viewLifecycleOwner){ databaseList ->
        val results = databaseList.map { it.result }
        recipesAdapter.setData(results)
    }

    private fun getFavoriteRecipesEvents() = lifecycleScope.launch {
        favoritesViewModel.favoriteRecipesEvent.collect { event->
            when(event){
                is FavoriteRecipesViewModel.FavoriteRecipesEvent.NavigateToDetailsFragment -> {
                    val action = FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsFragment(event.result, event.isFavorite)
                    findNavController().navigate(action)
                }
                is FavoriteRecipesViewModel.FavoriteRecipesEvent.ShowSnackbar -> {
//                    Snackbar.make(
//                        requireView(),
//                        event.message,
//                        Snackbar.LENGTH_LONG
//                    ).setAction("UNDO"){
//
//                    }.show()
                }
                is FavoriteRecipesViewModel.FavoriteRecipesEvent.ShowToast -> {
                    Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
                }
            }.exhaustive
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_recipes_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAll_favorite_recipes -> {
                favoritesViewModel.onDeleteAllFavoriteRecipes()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onRecipeItemClick(result: Result) {
        favoritesViewModel.onRecipeClick(result)
    }
}