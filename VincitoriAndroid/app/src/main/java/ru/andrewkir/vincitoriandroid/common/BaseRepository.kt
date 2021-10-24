package ru.andrewkir.vincitoriandroid.common

import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import ru.andrewkir.vincitoriandroid.web.model.ApiResponse
import java.net.SocketTimeoutException

abstract class BaseRepository {

    suspend fun <T> protectedApiCall(
        api: suspend () -> T
    ): ApiResponse<T> {
        return withContext(Dispatchers.IO) {
            try {
                ApiResponse.OnSuccessResponse(api.invoke())
            } catch (ex: Throwable) {
                when (ex) {
                    is HttpException -> {
                        ApiResponse.OnErrorResponse(false, ex.code(), ex.response()?.errorBody())
                    }
                    is SocketTimeoutException -> {
                        ApiResponse.OnErrorResponse(false, null, null)
                    }
                    is JsonSyntaxException -> {
                        ApiResponse.OnErrorResponse(false, null, null)
                    }
                    else -> {
                        ApiResponse.OnErrorResponse(true, null, null)
                    }
                }
            }
        }
    }
}