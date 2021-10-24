package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Serializable
@Parcelize
data class FiltersItem(
    @SerialName("items")
    var items: List<Item>?,
    @SerialName("title")
    var title: String?,
    @SerialName("requestName")
    var requestName: String?
) : Parcelable