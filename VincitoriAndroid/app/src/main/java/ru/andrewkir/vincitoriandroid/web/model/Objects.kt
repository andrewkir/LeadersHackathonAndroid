package ru.andrewkir.vincitoriandroid.web.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.util.*

@Serializable
@Parcelize
class Objects: ArrayList<ObjectsItem>(), Parcelable