package com.me.kt_cook_book.data

import com.me.kt_cook_book.data.database.RecipesDao
import com.me.kt_cook_book.data.database.entities.FavoritesEntity
import com.me.kt_cook_book.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {
    fun readRecipes(): Flow<List<RecipesEntity>> = recipesDao.readRecipes()

    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return recipesDao.readFavoriteRecipes()
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity) = recipesDao.insertRecipes(recipesEntity)

    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    suspend fun isFavoriteRecipe(resultId: Int): Boolean = recipesDao.isFavoriteRecipe(resultId)

    suspend fun deleteFavoriteRecipe(recipeId: Int) {
        recipesDao.deleteFavoriteRecipe(recipeId)
    }

    suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavoriteRecipes()
    }
}