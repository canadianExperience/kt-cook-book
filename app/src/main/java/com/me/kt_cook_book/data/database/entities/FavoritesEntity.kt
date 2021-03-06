package com.me.kt_cook_book.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.me.kt_cook_book.data.apimanager.models.Result
import com.me.kt_cook_book.utility.Constants.Companion.FAVORITE_RECIPES_TABLE

@Entity(tableName = FAVORITE_RECIPES_TABLE)
data class FavoritesEntity(
        var recipeId: Int,
        var result: Result,

        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
)