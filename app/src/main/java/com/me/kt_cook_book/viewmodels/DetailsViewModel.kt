package com.me.kt_cook_book.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.me.kt_cook_book.data.apimanager.models.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val state: SavedStateHandle
): ViewModel() {

    val result: Result? = state.get<Result>("result")

}