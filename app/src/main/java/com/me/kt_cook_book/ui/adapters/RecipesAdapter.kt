package com.me.kt_cook_book.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.me.kt_cook_book.databinding.RecipesRowLayoutBinding
import com.me.kt_cook_book.data.apimanager.models.FoodRecipe
import com.me.kt_cook_book.data.apimanager.models.Result
import com.me.kt_cook_book.utility.MyDiffUtil

class RecipesAdapter(
    private val clickListener: IRecipeClickListener
): RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {

    private var recipes = emptyList<Result>()

    class MyViewHolder(private val binding: RecipesRowLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(result: Result, clickListener: IRecipeClickListener){
            binding.result = result
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe, clickListener)
    }

    override fun getItemCount(): Int {
       return recipes.size
    }

    fun getCurrentRecipeId(position: Int): Int = recipes[position].recipeId

    fun setData(newData: List<Result>){
        val recipesDiffUtil = MyDiffUtil(recipes, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newData

        diffUtilResult.dispatchUpdatesTo(this)
    }
}