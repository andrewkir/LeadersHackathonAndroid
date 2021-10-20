package ru.andrewkir.vincitoriandroid.web.service

import retrofit2.http.GET
import retrofit2.http.POST
import ru.andrewkir.vincitoriandroid.web.model.HeatMap
import ru.andrewkir.vincitoriandroid.web.model.Objects

interface ApiService {

    @POST("/objects")
    suspend fun getObjects(): Objects

    @GET("/heatmap/population-density?minLat=55.2&minLng=36.9&maxLat=55.3&maxLng=37.1")
    suspend fun getHeatMap(): HeatMap
}
