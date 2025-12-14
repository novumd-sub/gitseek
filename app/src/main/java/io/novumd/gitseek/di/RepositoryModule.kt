package io.novumd.gitseek.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.novumd.gitseek.data.remote.GitHubApi
import io.novumd.gitseek.data.repo.GitHubRepository
import io.novumd.gitseek.data.repo.GitHubRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideGitHubRepository(api: GitHubApi): GitHubRepository = GitHubRepositoryImpl(api)
}
