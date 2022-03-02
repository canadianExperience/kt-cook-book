package com.me.kt_cook_book.viewmodels

import androidx.lifecycle.*
import com.me.kt_cook_book.data.Repository
import com.me.kt_cook_book.data.apimanager.models.Result
import com.me.kt_cook_book.data.database.entities.FavoritesEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
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

    fun onRecipeClick(result: Result) = viewModelScope.launch {
        favoriteRecipesEventChannel.send(FavoriteRecipesEvent.NavigateToDetailsFragment(result, true))
    }

    fun onDeleteAllFavoriteRecipes() = viewModelScope.launch {
        repository.local.deleteAllFavoriteRecipes()
        favoriteRecipesEventChannel.send(FavoriteRecipesEvent.ShowToast("All favorite recipes deleted"))
    }

    fun onDeleteFavoriteRecipe(recipeId: Int) = viewModelScope.launch {
        repository.local.deleteFavoriteRecipe(recipeId)
        favoriteRecipesEventChannel.send(FavoriteRecipesEvent.ShowSnackbar("Favorite recipe deleted"))
    }

    sealed class FavoriteRecipesEvent{
        class ShowToast(val message: String) : FavoriteRecipesEvent()
        class ShowSnackbar(val message: String) : FavoriteRecipesEvent()
        class NavigateToDetailsFragment(val result: Result, val isFavorite: Boolean) : FavoriteRecipesEvent()
    }

}