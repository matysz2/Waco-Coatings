package com.example.waco.network
import com.example.waco.data.LoginRequest
import com.example.waco.data.Order
import com.example.waco.data.Product
import com.example.waco.data.Product2
import com.example.waco.data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("getPodkłady.php")
    fun getPodklady(): Call<List<Product2>>

    @GET("getLakiery.php")
    fun getLakiery(): Call<List<Product2>>

    @GET("getKatalizatory.php")
    fun getKatalizatory(): Call<List<Product2>>

    @GET("getDodatki.php")
    fun getDodatki(): Call<List<Product2>>

    @GET("getRozpuszczalniki.php")
    fun getRozpuszczalniki(): Call<List<Product2>>



    @GET("get_products.php")
    fun getProducts(): Call<List<Product>>

    @POST("submit_order.php")
    fun submitOrder(@Body products: List<Product>): Call<Void>

    @GET("order_history.php")
    fun getOrderHistory(@Query("client_id") clientId: String): Call<List<Order>>


}



