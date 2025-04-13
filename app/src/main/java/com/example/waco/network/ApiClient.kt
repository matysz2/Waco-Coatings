package com.example.waco.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://waco.atwebpages.com/waco/"  // Zmień na URL swojego serwera

    fun getClient(): Retrofit {
        // Tworzymy instancję Gson z włączonym trybem lenient
        val gson: Gson = GsonBuilder().setLenient().create()

        // Tworzymy OkHttpClient z interceptor logującym odpowiedzi
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)

                // Logowanie odpowiedzi API - odczytujemy ciało odpowiedzi
                val responseBody = response.body?.string() ?: "Empty response"
                Log.d("API Response", responseBody)

                // Tworzymy nową odpowiedź z odczytanym ciałem, aby mogła zostać użyta przez Retrofit
                // Jeśli ciało odpowiedzi jest puste, zwracamy odpowiedź bez ciała
                val newResponseBody = ResponseBody.create(
                    response.body?.contentType(),
                    responseBody
                )

                return@addInterceptor response.newBuilder()
                    .body(newResponseBody)
                    .build()
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))  // Używamy lenient Gson
            .client(client)  // Dodajemy OkHttpClient z interceptor
            .build()
    }
}
