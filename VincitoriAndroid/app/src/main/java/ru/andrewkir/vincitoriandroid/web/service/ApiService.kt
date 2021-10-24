package ru.andrewkir.vincitoriandroid.web.service

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.andrewkir.vincitoriandroid.web.model.Attributes
import ru.andrewkir.vincitoriandroid.web.model.Filters
import ru.andrewkir.vincitoriandroid.web.model.HeatMap
import ru.andrewkir.vincitoriandroid.web.model.Objects

interface ApiService {

    @POST("/objects?fromLat=55.755819&fromLng=37.587644&toLat=55.785819&toLng=37.617644")
    suspend fun getObjects(@Body body: RequestBody? = null): Objects

    @GET("/heatmap/population-density?minLat=55.147199297273595&minLng=36.75571401201972&maxLat=56.078541702726405&maxLng=38.06930098798028")
    suspend fun getHeatMap(): HeatMap

    @GET("/objects/filters")
    suspend fun getFilters(): Filters

    @GET("/objects/attributes")
    suspend fun getAttributes(@Query("objectID") objectId: Int): Attributes
}
