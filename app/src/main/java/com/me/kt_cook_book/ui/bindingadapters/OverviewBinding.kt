package com.me.kt_cook_book.ui.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.me.kt_cook_book.R

class OverviewBinding {

    companion object {

        @BindingAdapter("checkmarkColor")
        @JvmStatic
        fun checkmarkColor(
            view: View,
            isActive: Boolean?
        ) {
            val context = view.context
            val color = if(isActive == true){
                ContextCompat.getColor(context, R.color.green)
            } else ContextCompat.getColor(context, R.color.itemColor)

            when(view){
                is ImageView -> view.setColorFilter(color)
                is TextView -> view.setTextColor(color)
            }
        }
    }

}