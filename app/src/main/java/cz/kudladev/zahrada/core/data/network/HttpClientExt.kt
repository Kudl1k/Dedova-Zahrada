package cz.kudladev.zahrada.core.data.network


import cz.kudladev.zahrada.core.domain.model.DataError
import cz.kudladev.zahrada.core.domain.model.DetailedDataError
import cz.kudladev.zahrada.core.domain.model.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

@OptIn(InternalAPI::class)
suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, DetailedDataError.Remote> {
    val response = try {
        execute()
    } catch(e: SocketTimeoutException) {
        return Result.Error(DetailedDataError.Remote(DataError.Remote.REQUEST_TIMEOUT, null))
    } catch(e: UnresolvedAddressException) {
        return Result.Error(DetailedDataError.Remote(DataError.Remote.NO_INTERNET, null))
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        return Result.Error(DetailedDataError.Remote(DataError.Remote.UNKNOWN, null))
    }
    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, DetailedDataError.Remote> {
    return when(response.status.value) {
        in 200..299 -> {
            try {
                val message = response.body<T>()
                Result.Success(message)
            } catch(e: NoTransformationFoundException) {
                e.printStackTrace()
                Result.Error(DetailedDataError.Remote(DataError.Remote.SERIALIZATION, null))
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Error(DetailedDataError.Remote(DataError.Remote.SERIALIZATION, null))
            }
        }
        408 -> {
            try {
                val message = response.body<String>().subSequence(1, response.body<String>().length - 1).toString()
                Result.Error(DetailedDataError.Remote(DataError.Remote.REQUEST_TIMEOUT,message))
            } catch(e: NoTransformationFoundException) {
                Result.Error(DetailedDataError.Remote(DataError.Remote.REQUEST_TIMEOUT, null))
            } catch (e: Exception) {
                Result.Error(DetailedDataError.Remote(DataError.Remote.REQUEST_TIMEOUT, null))
            }
        }
        409 -> {
            try {
                val message = response.body<String>().subSequence(1, response.body<String>().length - 1).toString()
                Result.Error(DetailedDataError.Remote(DataError.Remote.CONFLICT,message))
            } catch(e: NoTransformationFoundException) {
                Result.Error(DetailedDataError.Remote(DataError.Remote.CONFLICT, null))
            } catch (e: Exception) {
                Result.Error(DetailedDataError.Remote(DataError.Remote.CONFLICT, null))
            }
        }
        429 -> {
            try {
                val message = response.body<String>().subSequence(1, response.body<String>().length - 1).toString()
                Result.Error(DetailedDataError.Remote(DataError.Remote.TOO_MANY_REQUESTS,message))
            } catch(e: NoTransformationFoundException) {
                Result.Error(DetailedDataError.Remote(DataError.Remote.TOO_MANY_REQUESTS, null))
            } catch (e: Exception) {
                Result.Error(DetailedDataError.Remote(DataError.Remote.TOO_MANY_REQUESTS, null))
            }
        }
        in 500..599 -> {
            try {
                val message = response.body<String>().subSequence(1, response.body<String>().length - 1).toString()
                Result.Error(DetailedDataError.Remote(DataError.Remote.SERVER,message))
            } catch(e: NoTransformationFoundException) {
                Result.Error(DetailedDataError.Remote(DataError.Remote.SERVER, null))
            } catch (e: Exception) {
                Result.Error(DetailedDataError.Remote(DataError.Remote.SERVER, null))
            }
        }
        else -> {
            Result.Error(DetailedDataError.Remote(DataError.Remote.UNKNOWN, null))
        }
    }
}