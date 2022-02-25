package com.me.kt_cook_book.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.me.kt_cook_book.data.datastore.DataStoreRepository
import com.me.kt_cook_book.utility.Constants.Companion.DEFAULT_DIET_TYPE
import com.me.kt_cook_book.utility.Constants.Companion.DEFAULT_MEAL_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {

    private val recipesEventChannel = Channel<RecipesEvent>()
    val recipesEvent = recipesEventChannel.receiveAsFlow()

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE
    var networkStatus = false
    var backOnline = false

    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

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


    fun onRecipesBottomSheetClick() = viewModelScope.launch {
        recipesEventChannel.send(RecipesEvent.NavigateToRecipesBottomSheet)
    }

    private suspend fun onBackFromRecipesBottomSheetClick() = recipesEventChannel.send(RecipesEvent.BackFromRecipesBottomSheet)

    sealed class RecipesEvent{
        object NavigateToRecipesBottomSheet : RecipesEvent()
        object BackFromRecipesBottomSheet : RecipesEvent()
    }
}