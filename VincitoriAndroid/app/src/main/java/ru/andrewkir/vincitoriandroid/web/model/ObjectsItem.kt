package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class ObjectsItem(
    @SerialName("address")
    val address: String?,
    @SerialName("id")
    val id: Int?,
    @SerialName("lat")
    val lat: Double?,
    @SerialName("lng")
    val lng: Double?,
    @SerialName("name")
    val name: String?
) : Parcelable