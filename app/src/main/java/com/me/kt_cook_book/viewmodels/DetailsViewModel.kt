package com.me.kt_cook_book.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.kt_cook_book.data.Repository
import com.me.kt_cook_book.data.apimanager.models.Result
import com.me.kt_cook_book.data.database.entities.FavoritesEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: Repository
): ViewModel() {

    val result: Result? = state.get<Result>("result")
    var isFavorite: Boolean = state.get("isFavorite") ?: false

    private val detailsEventChannel = Channel<DetailsEvent>()
    val detailsEvent = detailsEventChannel.receiveAsFlow()

    fun onFavoritesClick() {
        isFavorite = !isFavorite
        result?.let { recipe ->
            if(isFavorite) addFavoriteRecipe(recipe) else deleteFavoriteRecipe(recipe.recipeId)
        }
    }

    private fun addFavoriteRecipe(recipe: Result) = viewModelScope.launch {
        repository.local.insertFavoriteRecipes(FavoritesEntity(recipe.recipeId, recipe))
        detailsEventChannel.send(DetailsEvent.ShowSnackbar("Recipe added to favorites"))
    }

    private fun deleteFavoriteRecipe(recipeId: Int) = viewModelScope.launch {
        repository.local.deleteFavoriteRecipe(recipeId)
        detailsEventChannel.send(DetailsEvent.ShowSnackbar("Recipe removed from favorites"))
    }

    sealed class DetailsEvent{
        class ShowSnackbar(val message: String) : DetailsEvent()
    }

}