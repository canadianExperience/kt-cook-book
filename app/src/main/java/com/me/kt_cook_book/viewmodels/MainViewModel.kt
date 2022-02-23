package com.me.kt_cook_book.viewmodels

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.me.kt_cook_book.data.Repository
import com.me.kt_cook_book.data.apimanager.models.FoodRecipe
import com.me.kt_cook_book.data.apimanager.NetworkResult
import com.me.kt_cook_book.data.database.RecipesEntity
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
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    private val recipesEventChannel = Channel<RecipesEvent>()
    val recipesEvent = recipesEventChannel.receiveAsFlow()

    /** ROOM DATABASE*/

    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readDatabase().asLiveData()

    private suspend fun insertRecipes(recipesEntity: RecipesEntity) = repository.local.insertRecipes(recipesEntity)



    /** RETROFIT*/

    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
   // val recipesResponseLiveData: LiveData<NetworkResult<FoodRecipe>> get() = recipesResponse

    fun getRecipes(queries: Map<String,String>) = viewModelScope.launch {
        getRecipesSaveCall(queries)
    }

    private suspend fun getRecipesSaveCall(queries: Map<String,String>) {
        recipesEventChannel.send(RecipesEvent.ApiCallResponse(NetworkResult.Loading()))
        if(hasInternetConnection()){
            try {
                val response = repository.remote.getRecipes(queries)

                val foodRecipe = handleFoodRecipesResponse(response)
                foodRecipe?.data?.let {
                    //Insert to local database (local cache)
                    insertRecipes(RecipesEntity(it))
                }
                recipesEventChannel.send(RecipesEvent.ApiCallResponse(foodRecipe))

            } catch (e: Exception){
                recipesEventChannel.send(RecipesEvent.ApiCallResponse(NetworkResult.Error("Recipes Not Found")))
            }
        } else{
            recipesEventChannel.send(RecipesEvent.ApiCallResponse(NetworkResult.Error("No Internet Connection")))
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

    fun onRequestApiData(query: HashMap<String,String>) = viewModelScope.launch {
        getRecipesSaveCall(query)
    }

    sealed class RecipesEvent{
        class ApiCallResponse(val response: NetworkResult<FoodRecipe>?) : RecipesEvent()
    }
}