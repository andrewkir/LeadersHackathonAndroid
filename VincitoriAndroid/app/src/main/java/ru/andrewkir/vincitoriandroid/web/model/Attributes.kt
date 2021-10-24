package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class Attributes(
    @SerialName("address")
    var address: String?,
    @SerialName("department")
    var department: String?,
    @SerialName("name")
    var name: String?,
    @SerialName("proximity")
    var proximity: String?,
    @SerialName("sportzones")
    var sportzones: List<Sportzone>?
) : Parcelable