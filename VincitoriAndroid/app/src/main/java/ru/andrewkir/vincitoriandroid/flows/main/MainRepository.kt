package ru.andrewkir.vincitoriandroid.flows.main

import okhttp3.RequestBody
import retrofit2.http.Body
import ru.andrewkir.vincitoriandroid.common.BaseRepository
import ru.andrewkir.vincitoriandroid.web.service.ApiService

class MainRepository(
    private val apiService: ApiService
) : BaseRepository() {
    suspend fun getObjects(body: RequestBody? = null) = protectedApiCall {
        apiService.getObjects(body)
    }

    suspend fun getHeatMap() = protectedApiCall {
        apiService.getHeatMap()
    }

    suspend fun getFilters() = protectedApiCall {
        apiService.getFilters()
    }


    suspend fun getAttributes(id: Int) = protectedApiCall {
        apiService.getAttributes(id)
    }

    suspend fun getSportZonesHeatMap(body: RequestBody) = protectedApiCall {
        apiService.getSportsZonesHeatMap(body)
    }

    suspend fun getStatistics() = protectedApiCall {
        apiService.getStatistics()
    }
}