package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.util.*

@Serializable
@Parcelize
data class Objects(
    var objects: ArrayList<ObjectsItem>
) : Parcelable