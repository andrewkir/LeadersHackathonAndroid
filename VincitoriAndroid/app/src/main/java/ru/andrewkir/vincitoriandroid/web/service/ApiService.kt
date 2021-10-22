package ru.andrewkir.vincitoriandroid.web.service

import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.andrewkir.vincitoriandroid.web.model.HeatMap
import ru.andrewkir.vincitoriandroid.web.model.Objects

interface ApiService {

    @POST("/objects?fromLat=55.755819&fromLng=37.617644&toLat=55.785819&&toLng=37.587644")
    suspend fun getObjects(): Objects

    @GET("/heatmap/population-density?minLat=55.147199297273595&minLng=36.75571401201972&maxLat=56.078541702726405&maxLng=38.06930098798028")
    suspend fun getHeatMap(): HeatMap
}
