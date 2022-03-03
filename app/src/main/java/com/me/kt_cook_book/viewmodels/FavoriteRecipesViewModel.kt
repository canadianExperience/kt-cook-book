package com.me.kt_cook_book.viewmodels

import androidx.lifecycle.*
import com.me.kt_cook_book.data.Repository
import com.me.kt_cook_book.data.apimanager.models.Result
import com.me.kt_cook_book.data.database.entities.FavoritesEntity
import com.me.kt_cook_book.utility.DeleteType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteRecipesViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val favoriteRecipesFlow = repository.local.readFavoriteRecipes()
    val favoriteRecipes: LiveData<List<FavoritesEntity>> get() = favoriteRecipesFlow.asLiveData()

    private val favoriteRecipesEventChannel = Channel<FavoriteRecipesEvent>()
    val favoriteRecipesEvent = favoriteRecipesEventChannel.receiveAsFlow()

    private val noFavoriteRecipesFlow = MutableStateFlow(false)
    val noFavoriteRecipes: LiveData<Boolean> get() = noFavoriteRecipesFlow.asLiveData()

   fun setNoFavoriteRecipesFlow(value: Boolean) = viewModelScope.launch {
        noFavoriteRecipesFlow.emit(value)
    }

    fun onRecipeClick(result: Result) = viewModelScope.launch {
        favoriteRecipesEventChannel.send(FavoriteRecipesEvent.NavigateToDetailsFragment(result, true))
    }

    fun onDeleteAllFavoriteRecipes() = viewModelScope.launch {
        val deletedFavoriteRecipesList = favoriteRecipesFlow.first()
        repository.local.deleteAllFavoriteRecipes()
        favoriteRecipesEventChannel.send(FavoriteRecipesEvent.DeleteSnackbar(
            "All favorite recipes deleted",
            deletedFavoriteRecipesList,
            DeleteType.DELETE_ALL_RECIPES
        )
        )
    }

    fun onUndoDeleteAllFavoriteRecipes(deletedFavoriteRecipesList: List<FavoritesEntity>) = viewModelScope.launch {
        repository.local.insertAllFavoriteRecipes(deletedFavoriteRecipesList)
    }

    fun onDeleteFavoriteRecipe(recipeId: Int) = viewModelScope.launch {
        val deletedFavoriteRecipe = repository.local.readFavoriteRecipe(recipeId)
        repository.local.deleteFavoriteRecipe(recipeId)
        favoriteRecipesEventChannel.send(FavoriteRecipesEvent.DeleteSnackbar(
            "Favorite recipe deleted",
            listOf(deletedFavoriteRecipe),
            DeleteType.DELETE_RECIPE)
        )
    }

    fun onUndoDeleteFavoriteRecipe(deletedFavoriteRecipe: FavoritesEntity) = viewModelScope.launch {
        repository.local.insertFavoriteRecipes(deletedFavoriteRecipe)
    }

    sealed class FavoriteRecipesEvent{
        class DeleteSnackbar(val message: String, val deletedFavoriteRecipes: List<FavoritesEntity>, val deleteType: DeleteType) : FavoriteRecipesEvent()
        class NavigateToDetailsFragment(val result: Result, val isFavorite: Boolean) : FavoriteRecipesEvent()
    }
}