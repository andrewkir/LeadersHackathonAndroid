package ru.andrewkir.vincitoriandroid.flows.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.andrewkir.vincitoriandroid.common.BaseViewModel
import ru.andrewkir.vincitoriandroid.web.model.ApiResponse
import ru.andrewkir.vincitoriandroid.web.model.Filters
import ru.andrewkir.vincitoriandroid.web.model.HeatMap

class MainViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel(mainRepository) {

    val heatMap: LiveData<HeatMap>
        get() = mHeatMap

    private val mHeatMap: MutableLiveData<HeatMap> by lazy {
        MutableLiveData<HeatMap>()
    }

    val filters: LiveData<Filters>
        get() = mFilters

    private val mFilters: MutableLiveData<Filters> by lazy {
        MutableLiveData<Filters>()
    }

    fun getObjects() {
        viewModelScope.launch {
            val objects = mainRepository.getHeatMap()
//            for (obj in objects) {
//                Log.d("ASD", obj.address!!)
//            }
        }
    }

    fun getHeatMap() {
        viewModelScope.launch {
            when(val heatMap = mainRepository.getHeatMap()){
                is ApiResponse.OnSuccessResponse -> {
                    mHeatMap.value = heatMap.value
                }
                is ApiResponse.OnErrorResponse -> {

                }
            }
        }
    }

    fun getFilters(){
        viewModelScope.launch {
            when(val filters = mainRepository.getFilters()){
                is ApiResponse.OnSuccessResponse -> {
                    mFilters.value = filters.value
                }
                is ApiResponse.OnErrorResponse -> {
                     Log.d("ERROR", filters.isNetworkFailure.toString())
                }
            }
        }
    }
}