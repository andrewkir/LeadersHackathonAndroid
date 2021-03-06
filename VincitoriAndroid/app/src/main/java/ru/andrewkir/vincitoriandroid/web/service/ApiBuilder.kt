package ru.andrewkir.vincitoriandroid.web.service

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.andrewkir.vincitoriandroid.BuildConfig
import java.util.concurrent.TimeUnit

class ApiBuilder() {
    companion object {
        private const val BASE_URL = "http://84.201.155.32/"
    }

    fun <Api> provideApi(
        api: Class<Api>
    ): Api =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(provideOkHTPPClient())
            .build()
            .create(api)

    private fun provideOkHTPPClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .also { client ->
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
            }.build()
    }
}