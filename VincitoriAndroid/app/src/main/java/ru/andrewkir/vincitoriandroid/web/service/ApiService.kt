package ru.andrewkir.vincitoriandroid.web.service

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.andrewkir.vincitoriandroid.web.model.*

interface ApiService {

    @POST("/objects?fromLat=55.1471993&fromLng=36.75571401&toLat=56.0785417&toLng=38.06930099")
    suspend fun getObjects(@Body body: RequestBody? = null): Objects

    @GET("/heatmap/population-density?minLat=55.1471993&minLng=36.75571401&maxLat=56.0785417&maxLng=38.06930099")
    suspend fun getHeatMap(): HeatMap

    @POST("http://84.201.155.32/heatmap/sportzone-density")
    suspend fun getSportsZonesHeatMap(@Body body: RequestBody? = null): HeatMap

    @GET("/objects/filters")
    suspend fun getFilters(): Filters

    @GET("/objects/attributes")
    suspend fun getAttributes(@Query("objectID") objectId: Int): Attributes

    @GET("/statistics?minLat=55.1471993&minLng=36.75571401&maxLat=56.0785417&maxLng=38.06930099")
    suspend fun getStatistics(): Statisctics
}
