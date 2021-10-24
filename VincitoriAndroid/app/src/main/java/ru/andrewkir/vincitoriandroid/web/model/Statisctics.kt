package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class Statisctics(
    @SerialName("sports_count")
    var sportsCount: String?,
    @SerialName("sportzones_count")
    var sportzonesCount: String?,
    @SerialName("square")
    var square: String?
) : Parcelable