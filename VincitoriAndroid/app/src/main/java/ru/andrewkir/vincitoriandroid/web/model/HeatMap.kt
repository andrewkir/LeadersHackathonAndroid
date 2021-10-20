package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class HeatMap(
    @SerialName("geoRect")
    var geoRect: GeoRect?,
    @SerialName("matrix")
    var matrix: List<List<Int>>?
) : Parcelable