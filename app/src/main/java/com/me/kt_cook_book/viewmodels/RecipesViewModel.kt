package com.me.kt_cook_book.viewmodels

import androidx.lifecycle.ViewModel
import com.me.kt_cook_book.utility.Constants.Companion.API_KEY
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_API_KEY
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_DIET
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_NUMBER
import com.me.kt_cook_book.utility.Constants.Companion.QUERY_TYPE

class RecipesViewModel: ViewModel() {
//        number=50
//        apiKey=b57088d18bed41c9824cd5bb87184a83
//        type=snack
//        diet=vegan
//        addRecipeInformation=true
//        fillIngredients=true


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
}