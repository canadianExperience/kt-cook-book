package com.me.kt_cook_book.data.database

import androidx.room.*
import com.me.kt_cook_book.data.database.entities.FavoritesEntity
import com.me.kt_cook_book.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Query("SELECT CASE WHEN exists(SELECT 1 from favorite_recipes_table where (recipeId ==:resultId)  LIMIT 1) THEN CAST(1 as BIT) ELSE CAST(0 as BIT) END")
    suspend fun isFavoriteRecipe(resultId: Int): Boolean

    @Query("DELETE FROM favorite_recipes_table WHERE recipeId =:recipeId")
    suspend fun deleteFavoriteRecipe(recipeId: Int)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()
}