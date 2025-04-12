package com.example.waco.network
import com.example.waco.data.Product
import com.example.waco.data.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("getPodkłady.php")
    fun getPodklady(): Call<List<Product>>

    @GET("getLakiery.php")
    fun getLakiery(): Call<List<Product>>

    @GET("getKatalizatory.php")
    fun getKatalizatory(): Call<List<Product>>

    @GET("getDodatki.php")
    fun getDodatki(): Call<List<Product>>

    @GET("getRozpuszczalniki.php")
    fun getRozpuszczalniki(): Call<List<Product>>

    @GET("login.php")
    fun login(
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<List<User>>

}
