package com.example.flash.network

import com.example.flash.data.InternetItem
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://training-uploads.internshala.com"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(
        Json.asConverterFactory(
            "application/json".toMediaType()
        )
    )
    .baseUrl(BASE_URL)
    .build()

interface FlashApiService{
    @GET("android/grocery_delivery_app/items.json")
    suspend fun getItems(): List<InternetItem>
//    suspend fun getItems(): String

}

object FlashApi{
    // lazy is used when we want to delay the creation of the object until it is accessed
    val retrofitService: FlashApiService by lazy{
        retrofit.create(
            FlashApiService::class.java       // This is because retrofit is in java, function reference operator is used (::)
        )
    }
}