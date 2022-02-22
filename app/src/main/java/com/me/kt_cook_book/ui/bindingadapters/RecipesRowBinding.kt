package com.me.kt_cook_book.ui.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.me.kt_cook_book.R

class RecipesRowBinding {

    companion object{

        @BindingAdapter("app:loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(view: ImageView, imageUrl: String){
            view.load(imageUrl){
                crossfade(600)
                error(R.drawable.ic_error_place_holder)
            }
        }

        @BindingAdapter("app:setNumberOfLikes")
        @JvmStatic
        fun setNumberOfLikes(view: TextView, likes: Int){
            view.text = likes.toString()
        }

        @BindingAdapter("app:setNumberOfMinutes")
        @JvmStatic
        fun setNumberOfMinutes(view: TextView, minutes: Int){
            view.text = minutes.toString()
        }

        @BindingAdapter("app:applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view: View, vegan: Boolean){
            if(vegan){
                when(view){
                    is TextView -> {
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }
    }
}