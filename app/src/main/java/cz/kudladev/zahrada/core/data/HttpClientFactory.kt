package cz.kudladev.zahrada.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging


object HttpClientFactory {
    fun create(httpEngine: HttpClientEngine): HttpClient{
        return HttpClient(httpEngine){
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
            }
        }
    }
}