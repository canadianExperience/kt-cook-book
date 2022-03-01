package com.me.kt_cook_book.ui.adapters

import com.me.kt_cook_book.data.apimanager.models.Result


interface IRecipeClickListener {
   fun onRecipeItemClick(result: Result)
}