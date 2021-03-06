package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class ObjectsItem(
    @SerialName("address")
    var address: String?,
    @SerialName("color")
    var color: Int?,
    @SerialName("id")
    var id: Int?,
    @SerialName("lat")
    var lat: Double?,
    @SerialName("lng")
    var lng: Double?,
    @SerialName("name")
    var name: String?,
    @SerialName("radius")
    var radius: Int?,
    @SerialName("square")
    var square: Double?
) : Parcelable