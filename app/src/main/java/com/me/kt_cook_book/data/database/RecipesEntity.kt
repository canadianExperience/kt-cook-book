package com.me.kt_cook_book.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.me.kt_cook_book.data.apimanager.models.FoodRecipe
import com.me.kt_cook_book.utility.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(
    var foodRecipe: FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}