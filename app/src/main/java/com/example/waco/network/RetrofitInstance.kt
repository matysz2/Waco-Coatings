package com.example.waco.network

import com.example.waco.data.OrderDetails
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://waco.atwebpages.com/waco/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)


    // Token FCM
    fun create(): ApiService {
        return retrofit.create(ApiService::class.java)
    }



}
