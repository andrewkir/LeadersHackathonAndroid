package ru.andrewkir.vincitoriandroid.web.service

import retrofit2.http.GET
import retrofit2.http.POST
import ru.andrewkir.vincitoriandroid.web.model.Filters
import ru.andrewkir.vincitoriandroid.web.model.HeatMap
import ru.andrewkir.vincitoriandroid.web.model.Objects

interface ApiService {

    @POST("/objects?fromLat=55.755819&fromLng=37.617644&toLat=55.785819&&toLng=37.587644")
    suspend fun getObjects(): Objects

    @GET("/heatmap/population-density?minLat=55.147199297273595&minLng=36.75571401201972&maxLat=56.078541702726405&maxLng=38.06930098798028")
    suspend fun getHeatMap(): HeatMap

    @GET("/objects/filters")
    suspend fun getFilters(): Filters
}
