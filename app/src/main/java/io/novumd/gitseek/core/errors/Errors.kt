package io.novumd.gitseek.core.errors

sealed interface ApiErr {
    data object Offline : ApiErr
    data object Unexpected : ApiErr
}

sealed interface DbErr {
    data object Failure : DbErr
}
