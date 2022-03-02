package com.me.kt_cook_book.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.me.kt_cook_book.data.apimanager.models.FoodRecipe
import com.me.kt_cook_book.utility.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
data class RecipesEntity(
    var foodRecipe: FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}