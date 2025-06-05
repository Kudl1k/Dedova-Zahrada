package cz.kudladev.zahrada.core.data

import cz.kudladev.zahrada.core.domain.DataError
import cz.kudladev.zahrada.core.domain.DetailedDataError
import cz.kudladev.zahrada.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorGardenDataSource(
    private val httpClient: HttpClient
): GardenDataSource {
    override suspend fun getGardenData(
        limit: Int?,
        offset: Int?
    ): Result<List<GardenDataRecordDTO>, DetailedDataError.Remote> {
        val response = httpClient.get {
            url("https://docs.google.com/spreadsheets/d/1AoYdeRaRvuLTB1CPxs4cUBMQlfqJJ89uA3u-JwhrQKI/gviz/tq?tqx=out:csv&sheet=Data")
            contentType(ContentType.Text.CSV)
        }
        return if (response.status.value == 200) {
            try {
                Result.Success(response.bodyAsText().toGardenDataRecordDTOs().reversed())
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Error(DetailedDataError.Remote(DataError.Remote.SERIALIZATION, null))
            }
        } else {
            Result.Error(DetailedDataError.Remote(DataError.Remote.UNKNOWN, null))
        }
    }
}