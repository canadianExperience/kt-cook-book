package com.me.kt_cook_book.data

import com.me.kt_cook_book.data.apimanager.FoodRecipesApi
import com.me.kt_cook_book.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) {
    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> = foodRecipesApi.getRecipes(queries)
}