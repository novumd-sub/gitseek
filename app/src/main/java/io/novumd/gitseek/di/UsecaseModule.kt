package io.novumd.gitseek.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.novumd.gitseek.domain.usecase.SearchReposUseCase
import io.novumd.gitseek.domain.usecase.SearchReposUsecaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UsecaseModule {
    @Binds
    @Singleton
    abstract fun bindSearchReposUseCase(impl: SearchReposUsecaseImpl): SearchReposUseCase
}
