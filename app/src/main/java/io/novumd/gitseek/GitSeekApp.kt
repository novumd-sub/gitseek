package io.novumd.gitseek

import android.app.Application
import timber.log.Timber

class GitSeekApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
