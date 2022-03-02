package com.me.kt_cook_book.viewmodels

import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.me.kt_cook_book.data.Repository
import com.me.kt_cook_book.data.apimanager.models.Result
import com.me.kt_cook_book.data.datastore.DataStoreRepository
import com.me.kt_cook_book.utility.Constants.Companion.DEFAULT_DIET_TYPE
import com.me.kt_cook_book.utility.Constants.Companion.DEFAULT_MEAL_TYPE
import com.me.kt_cook_book.utility.NetworkListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
    private val connectivityManager: ConnectivityManager,
): ViewModel() {

    private val recipesEventChannel = Channel<RecipesEvent>()
    val recipesEvent = recipesEventChannel.receiveAsFlow()

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE
    var networkStatus = false
    var backOnline = false

    private val readBackOnlineFlow = dataStoreRepository.readBackOnline
    val readBackOnline get() = readBackOnlineFlow.asLiveData()

    private fun saveBackOnline(backOnline: Boolean) = viewModelScope.launch {
        dataStoreRepository.saveBackOnline(backOnline)
    }

    fun saveMealAndDietType(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) = viewModelScope.launch {
        dataStoreRepository.saveMealAndDietType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId
        )
        onBackFromRecipesBottomSheetClick()
    }

    fun onRecipesBottomSheetClick() = if(networkStatus) navigateToRecipesBottomSheet()
        else showToast("No Internet Connection")


    private fun navigateToRecipesBottomSheet() = viewModelScope.launch {
        recipesEventChannel.send(RecipesEvent.NavigateToRecipesBottomSheet)
    }

    fun showToast(message: String) = viewModelScope.launch {
        recipesEventChannel.send(RecipesEvent.ShowToast(message))
    }

    suspend fun onNetworkStatusChanged(networkListener: NetworkListener){
        networkListener.checkNetworkAvailability(connectivityManager)
            .collect { status ->
                Log.d("NetworkListener", status.toString())
                networkStatus = status
                showNetworkStatus()
            }
    }

    private fun showNetworkStatus() {
        if (!networkStatus) {
            showToast("No Internet Connection")
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                showToast("We're back online")
                saveBackOnline(false)
            }
        }
    }

    fun onRecipeClick(result: Result) = viewModelScope.launch {
        val isFavorite = repository.local.isFavoriteRecipe(result.recipeId)
        recipesEventChannel.send(RecipesEvent.NavigateToDetailsFragment(result, isFavorite))
    }

    private suspend fun onBackFromRecipesBottomSheetClick() = recipesEventChannel.send(RecipesEvent.BackFromRecipesBottomSheet)

    sealed class RecipesEvent{
        object NavigateToRecipesBottomSheet : RecipesEvent()
        object BackFromRecipesBottomSheet : RecipesEvent()
        class ShowToast(val message: String) : RecipesEvent()
        class NavigateToDetailsFragment(val result: Result, val isFavorite: Boolean) : RecipesEvent()
    }
}