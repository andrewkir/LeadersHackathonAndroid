package ru.andrewkir.vincitoriandroid

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch
import ru.andrewkir.vincitoriandroid.web.model.HeatMap
import ru.andrewkir.vincitoriandroid.web.service.ApiBuilder

class MainViewModel : ViewModel() {

    val heatMap: LiveData<HeatMap>
        get() = mHeatMap

    private val mHeatMap: MutableLiveData<HeatMap> by lazy {
        MutableLiveData<HeatMap>()
    }

    fun getObjects(context: Context) {
        viewModelScope.launch {
            val api = ApiBuilder(context).instance
            val objects = api.getObjects()
            for (obj in objects) {
                Log.d("ASD", obj.address!!)
            }
        }
    }

    fun getHeatMap(context: Context) {
        viewModelScope.launch {
            val api = ApiBuilder(context).instance
            mHeatMap.value = api.getHeatMap()
        }
    }
}