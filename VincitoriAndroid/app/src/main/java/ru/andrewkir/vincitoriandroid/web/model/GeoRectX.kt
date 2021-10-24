package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class GeoRectX(
    @SerialName("maxLat")
    var maxLat: Double?,
    @SerialName("maxLng")
    var maxLng: Double?,
    @SerialName("minLat")
    var minLat: Double?,
    @SerialName("minLng")
    var minLng: Double?
) : Parcelable