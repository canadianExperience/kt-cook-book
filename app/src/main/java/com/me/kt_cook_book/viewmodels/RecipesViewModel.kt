package com.me.kt_cook_book.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.kt_cook_book.utility.Constants.Companion.API_KEY
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_API_KEY
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_DIET
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_NUMBER
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_TYPE
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RecipesViewModel: ViewModel() {

    private val recipesEventChannel = Channel<RecipesEvent>()
    val recipesEvent = recipesEventChannel.receiveAsFlow()


    fun applyQueries(): HashMap<String,String>{
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_NUMBER] = "50"
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = "snack"
        queries[QUERY_DIET] = "vegan"
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