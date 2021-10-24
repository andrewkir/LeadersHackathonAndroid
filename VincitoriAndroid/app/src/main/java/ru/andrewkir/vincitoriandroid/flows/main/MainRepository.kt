package ru.andrewkir.vincitoriandroid.flows.main

import ru.andrewkir.vincitoriandroid.common.BaseRepository
import ru.andrewkir.vincitoriandroid.web.service.ApiService

class MainRepository(
    private val apiService: ApiService
) : BaseRepository() {
    suspend fun getHeatMap() = protectedApiCall {
        apiService.getHeatMap()
    }

    suspend fun getFilters() = protectedApiCall {
        apiService.getFilters()
    }
}