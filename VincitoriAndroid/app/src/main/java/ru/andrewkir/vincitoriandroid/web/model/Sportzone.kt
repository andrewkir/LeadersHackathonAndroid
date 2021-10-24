package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class Sportzone(
    @SerialName("name")
    var name: String?,
    @SerialName("sportstypes")
    var sportstypes: List<String>?,
    @SerialName("square")
    var square: Int?
) : Parcelable