package com.example.database.di

import android.content.Context
import androidx.room.Room
import com.example.database.SwemoDatabase
import com.example.database.dao.CategoryDao
import com.example.database.dao.MemoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideSwemoDatabase(
        @ApplicationContext context: Context
    ): SwemoDatabase = Room.databaseBuilder(
        context,
        SwemoDatabase::class.java,
        "swemo-database"
    ).build()

    @Provides
    fun provideCategoryDao(
        database: SwemoDatabase
    ): CategoryDao = database.categoryDao()

    @Provides
    fun provideMemoDao(
        database: SwemoDatabase
    ): MemoDao = database.memoDao()
}