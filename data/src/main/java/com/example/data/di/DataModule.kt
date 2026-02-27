package com.example.data.di

import com.example.data.FakeMemoRepository
import com.example.data.MemoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindMemoRepository(
        fakeMemoRepository: FakeMemoRepository
    ): MemoRepository

    companion object {
        @Provides
        @Singleton
        fun provideFakeMemoRepository(): FakeMemoRepository {
            return FakeMemoRepository()
        }
    }
}
