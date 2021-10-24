package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class Item(
    @SerialName("id")
    var id: Int?,
    @SerialName("name")
    var name: String?
) : Parcelable