package ru.andrewkir.vincitoriandroid.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import ru.andrewkir.vincitoriandroid.web.model.ApiResponse

abstract class BaseViewModel(
    private val repository: BaseRepository
) : ViewModel() {

    protected val mutableLoading: MutableLiveData<Boolean> = MutableLiveData()

    val loading: LiveData<Boolean>
        get() = mutableLoading


    val errorResponse: SingleLiveEvent<ApiResponse.OnErrorResponse> by lazy {
        SingleLiveEvent<ApiResponse.OnErrorResponse>()
    }
}