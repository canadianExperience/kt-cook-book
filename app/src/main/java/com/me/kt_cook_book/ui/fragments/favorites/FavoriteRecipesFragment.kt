package com.me.kt_cook_book.ui.fragments.favorites

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.me.kt_cook_book.R
import com.me.kt_cook_book.data.apimanager.models.Result
import com.me.kt_cook_book.data.database.entities.FavoritesEntity
import com.me.kt_cook_book.databinding.FragmentFavoriteRecipesBinding
import com.me.kt_cook_book.ui.adapters.IRecipeClickListener
import com.me.kt_cook_book.ui.adapters.RecipesAdapter
import com.me.kt_cook_book.utility.DeleteType
import com.me.kt_cook_book.utility.exhaustive
import com.me.kt_cook_book.viewmodels.FavoriteRecipesViewModel
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

        binding.favoritesViewModel = favoritesViewModel
        binding.lifecycleOwner = this

        setupRecyclerView()
        readDatabase()
        getFavoriteRecipesEvents()

        onFavoriteRecipeSwipe()

        setHasOptionsMenu(true)
    }

    private fun setupRecyclerView() {
        binding.favoriteRecipesRecyclerView.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onFavoriteRecipeSwipe() = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.absoluteAdapterPosition
            val recipeId = recipesAdapter.getCurrentRecipeId(position)
            favoritesViewModel.onDeleteFavoriteRecipe(recipeId)
        }

    }).attachToRecyclerView(binding.favoriteRecipesRecyclerView)

    private fun readDatabase() = favoritesViewModel.favoriteRecipes.observe(viewLifecycleOwner){ databaseList ->
        val results = databaseList.map { it.result }
        recipesAdapter.setData(results)
        favoritesViewModel.setNoFavoriteRecipesFlow(!results.isNullOrEmpty())
    }

    private fun getFavoriteRecipesEvents() = lifecycleScope.launch {
        favoritesViewModel.favoriteRecipesEvent.collect { event->
            when(event){
                is FavoriteRecipesViewModel.FavoriteRecipesEvent.NavigateToDetailsFragment -> {
                    val action = FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsFragment(event.result, event.isFavorite)
                    findNavController().navigate(action)
                }
                is FavoriteRecipesViewModel.FavoriteRecipesEvent.DeleteSnackbar -> {
                    showSnackbar(event.message, event.deleteType, event.deletedFavoriteRecipes)
                }
            }.exhaustive
        }
    }

    private fun showSnackbar(
        message: String,
        deleteType: DeleteType,
        deletedRecipes: List<FavoritesEntity>
    ) = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        .setAction("UNDO"){
        when(deleteType){
            DeleteType.DELETE_RECIPE -> favoritesViewModel.onUndoDeleteFavoriteRecipe(deletedRecipes.first())
            DeleteType.DELETE_ALL_RECIPES -> favoritesViewModel.onUndoDeleteAllFavoriteRecipes(deletedRecipes)
        }
    }.show()

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