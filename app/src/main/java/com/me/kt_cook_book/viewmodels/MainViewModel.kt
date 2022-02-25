package com.me.kt_cook_book.viewmodels

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.*
import com.me.kt_cook_book.data.Repository
import com.me.kt_cook_book.data.apimanager.models.FoodRecipe
import com.me.kt_cook_book.data.apimanager.NetworkResult
import com.me.kt_cook_book.data.database.RecipesEntity
import com.me.kt_cook_book.data.datastore.DataStoreRepository
import com.me.kt_cook_book.data.datastore.MealAndDietType
import com.me.kt_cook_book.utility.Constants
import com.me.kt_cook_book.utility.Constants.Companion.API_KEY
import com.me.kt_cook_book.utility.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.me.kt_cook_book.utility.NetworkListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val connectivityManager: ConnectivityManager,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    val connectivity get() = connectivityManager

    /** ROOM DATABASE*/

    private val readRecipesFlow = repository.local.readDatabase()
    val readRecipes: LiveData<List<RecipesEntity>> get() = readRecipesFlow.asLiveData()

    private suspend fun insertRecipes(recipesEntity: RecipesEntity) = repository.local.insertRecipes(recipesEntity)

    /** DATASTORE*/

    private val readMealAndDietTypeFlow = dataStoreRepository.readMealAndDietType
    val readMealAndDietType: LiveData<MealAndDietType> get() = readMealAndDietTypeFlow.asLiveData()

    /** RETROFIT*/

    private var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    val recipesResponseLiveData: LiveData<NetworkResult<FoodRecipe>> get() = recipesResponse

    private fun mealAndDietTypeQueries(meal: String, diet: String):HashMap<String,String>{
        val queries: HashMap<String, String> = HashMap()
        queries[Constants.QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[Constants.QUERY_API_KEY] = API_KEY
        queries[Constants.QUERY_TYPE] = meal
        queries[Constants.QUERY_DIET] = diet
        queries[Constants.QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[Constants.QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun apiRequest() {
        recipesResponse.value = NetworkResult.Loading()

        viewModelScope.launch {
            readMealAndDietTypeFlow.collect { value ->
                //Get queries from datastore
                val queries = mealAndDietTypeQueries(value.selectedMealType, value.selectedDietType)

                //RecipesSaveCall
                if (hasInternetConnection()) {
                    try {
                        val response = repository.remote.getRecipes(queries)

                        val foodRecipe = handleFoodRecipesResponse(response)
                        foodRecipe?.let {
                            recipesResponse.postValue(it)
                            it.data?.let { data ->
                                //Insert to local database (local cache)
                                insertRecipes(RecipesEntity(data))
                            }
                        }
                    } catch (e: Exception) {
                        recipesResponse.postValue(NetworkResult.Error("Recipes Not Found"))
                    }
                } else {
                    recipesResponse.postValue(NetworkResult.Error("No Internet Connection"))
                }
            }
        }
    }


    private suspend fun getRecipesSaveCall(queries: HashMap<String, String>) {

        recipesResponse.postValue(NetworkResult.Loading())
        if(hasInternetConnection()){
            try {
                val response = repository.remote.getRecipes(queries)

                val foodRecipe = handleFoodRecipesResponse(response)
                foodRecipe?.let {
                    recipesResponse.postValue(it)
                    it.data?.let { data->
                        //Insert to local database (local cache)
                        insertRecipes(RecipesEntity(data))
                    }
                }
            } catch (e: Exception){
                recipesResponse.postValue(NetworkResult.Error("Recipes Not Found"))
            }
        } else{
            recipesResponse.postValue(NetworkResult.Error("No Internet Connection"))
        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>?{
        when{
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes Not Found")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean{
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}