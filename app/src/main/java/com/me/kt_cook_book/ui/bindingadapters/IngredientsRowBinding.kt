package com.me.kt_cook_book.ui.bindingadapters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import com.me.kt_cook_book.R
import com.me.kt_cook_book.utility.Constants.Companion.BASE_IMAGE_URL
import java.util.*

class IngredientsRowBinding {

    companion object {
        @BindingAdapter("app:loadIngredientImageFromUrl")
        @JvmStatic
        fun loadIngredientImageFromUrl(view: ImageView, imageUrl: String){
            view.load(BASE_IMAGE_URL + imageUrl){
                crossfade(600)
                error(R.drawable.ic_error_place_holder)
            }
        }

        @BindingAdapter("app:setIngredientName")
        @JvmStatic
        fun setIngredientName(view: TextView, name: String){
            view.text = name.uppercase(Locale.getDefault())
        }

        @BindingAdapter("app:setIngredientAmount")
        @JvmStatic
        fun setIngredientAmount(view: TextView, amount: Double){
            view.text = amount.toString()
        }

    }

}