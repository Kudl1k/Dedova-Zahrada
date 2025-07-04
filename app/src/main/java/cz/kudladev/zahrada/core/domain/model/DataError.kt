package cz.kudladev.zahrada.core.domain.model

sealed interface DataError: Error {
    enum class Remote: DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        CONFLICT,
        UNKNOWN
    }

    enum class Local: DataError {
        DISK_FULL,
        UNKNOWN
    }

    enum class Client: DataError {
        NO_TOKEN,
    }
}


sealed interface DetailedDataError : Error {
    data class Remote(val type: DataError.Remote, val message: String?) : DetailedDataError
    data class Local(val type: DataError.Local, val message: String?) : DetailedDataError
    data class Client(val type: DataError.Client, val message: String?) : DetailedDataError
}