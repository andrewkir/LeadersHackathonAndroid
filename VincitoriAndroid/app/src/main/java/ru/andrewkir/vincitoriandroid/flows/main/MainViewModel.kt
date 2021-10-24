package ru.andrewkir.vincitoriandroid.flows.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.map.MapObject
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import ru.andrewkir.vincitoriandroid.common.BaseViewModel
import ru.andrewkir.vincitoriandroid.web.model.*


class MainViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {

    val statisctics: LiveData<Statisctics>
        get() = mStatistics

    private val mStatistics: MutableLiveData<Statisctics> by lazy {
        MutableLiveData<Statisctics>()
    }

    val heatMap: LiveData<HeatMap>
        get() = mHeatMap

    private val mHeatMap: MutableLiveData<HeatMap> by lazy {
        MutableLiveData<HeatMap>()
    }

    val sportsZonesHeatMap: LiveData<HeatMap>
        get() = mSportsZonesHeatMap

    private val mSportsZonesHeatMap: MutableLiveData<HeatMap> by lazy {
        MutableLiveData<HeatMap>()
    }

    val attributes: LiveData<Attributes>
        get() = mAttributes

    private val mAttributes: MutableLiveData<Attributes> by lazy {
        MutableLiveData<Attributes>()
    }

    val mapObjects: LiveData<Objects>
        get() = mObjects

    private val mObjects: MutableLiveData<Objects> by lazy {
        MutableLiveData<Objects>()
    }

    val filters: LiveData<Filters>
        get() = mFilters

    private val mFilters: MutableLiveData<Filters> by lazy {
        MutableLiveData<Filters>()
    }

    var isApiSet: Boolean = false
    var displayedObjects: MapObject? = null

    fun getObjects(query: String? = null, ids: Map<String, MutableList<Int>>? = null) {
        viewModelScope.launch {
            val body = JSONObject()
            body.put("objectName", query ?: "")
            if (!ids.isNullOrEmpty()) {
                for (key in ids.keys) {
                    body.put(key, ids[key])
                }
            }
            val bodyRequest: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                body.toString()
            )

            when (val objects = mainRepository.getObjects(bodyRequest)) {
                is ApiResponse.OnSuccessResponse -> {
                    mObjects.value = objects.value
                }
                is ApiResponse.OnErrorResponse -> {
                    Log.d("Error", objects.isNetworkFailure.toString())
                }
            }
        }
    }

    fun getHeatMap() {
        viewModelScope.launch {
            when (val heatMap = mainRepository.getHeatMap()) {
                is ApiResponse.OnSuccessResponse -> {
                    mHeatMap.value = heatMap.value
                }
                is ApiResponse.OnErrorResponse -> {
                    Log.d("error", heatMap.body.toString())
                }
            }
        }
    }


    fun getStatistics() {
        viewModelScope.launch {
            when (val statisctics = mainRepository.getStatistics()) {
                is ApiResponse.OnSuccessResponse -> {
                    mStatistics.value = statisctics.value
                }
                is ApiResponse.OnErrorResponse -> {
                    Log.d("error", statisctics.body.toString())
                }
            }
        }
    }

    fun getFilters() {
        viewModelScope.launch {
            when (val filters = mainRepository.getFilters()) {
                is ApiResponse.OnSuccessResponse -> {
                    mFilters.value = filters.value
                }
                is ApiResponse.OnErrorResponse -> {
                    Log.d("ERROR", filters.isNetworkFailure.toString())
                }
            }
        }
    }

    fun getAttributes(id: Int) {
        viewModelScope.launch {
            when (val attributes = mainRepository.getAttributes(id)) {
                is ApiResponse.OnSuccessResponse -> {
                    mAttributes.value = attributes.value
                }
                is ApiResponse.OnErrorResponse -> {
                    Log.d("ERROR", attributes.isNetworkFailure.toString())
                }
            }
        }
    }

    fun getSportZonesHeatMap(ids: Map<String, MutableList<Int>>? = null) {
        viewModelScope.launch {
            val body = JSONObject()
            if (!ids.isNullOrEmpty()) {
                body.put("sportzoneIds", ids["sportzoneIds"])
            }
            val bodyRequest: RequestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                body.toString()
            )
            when (val sportHeatMap = mainRepository.getSportZonesHeatMap(bodyRequest)) {
                is ApiResponse.OnSuccessResponse -> {
                    mSportsZonesHeatMap.value = sportHeatMap.value
                }
                is ApiResponse.OnErrorResponse -> {
                    Log.d("ERROR", sportHeatMap.isNetworkFailure.toString())
                }
            }
        }
    }
}