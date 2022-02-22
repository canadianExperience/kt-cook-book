package com.me.kt_cook_book.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.me.kt_cook_book.data.database.RecipesDatabase
import com.me.kt_cook_book.utility.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RecipesDatabase::class.java,
        DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideDao(database: RecipesDatabase) = database.recipesDao()

    @Singleton
    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context) = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager
}