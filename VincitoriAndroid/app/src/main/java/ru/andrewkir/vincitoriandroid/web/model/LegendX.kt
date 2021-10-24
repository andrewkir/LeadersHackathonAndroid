package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class LegendX(
    @SerialName("color")
    var color: Int?,
    @SerialName("maxValue")
    var maxValue: Double?,
    @SerialName("minValue")
    var minValue: Double?
) : Parcelable