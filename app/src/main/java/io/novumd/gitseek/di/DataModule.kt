package io.novumd.gitseek.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.novumd.gitseek.BuildConfig
import io.novumd.gitseek.data.remote.GitHubApi
import io.novumd.gitseek.data.remote.GitHubApiImpl
import io.novumd.gitseek.data.remote.KtorClientProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideHttpClient(@ApplicationContext context: Context): HttpClient = KtorClientProvider.create(
        context = context,
        githubToken = BuildConfig.GITHUB_TOKEN
    )

    @Provides
    @Singleton
    fun provideGitHubApi(client: HttpClient): GitHubApi = GitHubApiImpl(client)
}
