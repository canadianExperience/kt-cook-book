package com.me.kt_cook_book.viewmodels

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.me.kt_cook_book.data.Repository
import com.me.kt_cook_book.data.datastore.DataStoreRepository
import com.me.kt_cook_book.utility.Constants.Companion.API_KEY
import com.me.kt_cook_book.utility.Constants.Companion.DEFAULT_DIET_TYPE
import com.me.kt_cook_book.utility.Constants.Companion.DEFAULT_MEAL_TYPE
import com.me.kt_cook_book.utility.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_API_KEY
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_DIET
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_NUMBER
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
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

    private val readMealAndDietTypeFlow = dataStoreRepository.readMealAndDietType
    val readMealAndDietType get() = readMealAndDietTypeFlow.asLiveData()
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    fun saveMealAndDietType(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) = viewModelScope.launch { dataStoreRepository.saveMealAndDietType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId)
    }

    fun applyQueries(): HashMap<String,String>{
        val queries: HashMap<String, String> = HashMap()

        viewModelScope.launch {
            readMealAndDietTypeFlow.collect{value->
                mealType = value.selectedMealType
                dietType = value.selectedDietType
            }
        }

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = DEFAULT_MEAL_TYPE
        queries[QUERY_DIET] = DEFAULT_DIET_TYPE
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun onRecipesBottomSheetClick() = viewModelScope.launch {
        recipesEventChannel.send(RecipesEvent.NavigateToRecipesBottomSheet)
    }

    sealed class RecipesEvent{
        object NavigateToRecipesBottomSheet : RecipesEvent()
    }
}