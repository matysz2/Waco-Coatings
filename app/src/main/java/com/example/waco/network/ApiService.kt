package com.example.waco.network
import com.example.waco.data.Order
import com.example.waco.data.OrderRequest
import com.example.waco.data.Product
import com.example.waco.data.Product2
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
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



    @GET("productslist.php")
    fun getProducts(): Call<List<Product>>

    @POST("submit_order.php")
    fun submitOrder(@Body orderRequest: OrderRequest): Call<ResponseBody>


    @GET("order_history.php")
    fun getOrderHistory(@Query("client_id") clientId: String): Call<List<Order>>

    @GET("listorders.php")
    fun getOrders(@Query("user_id") userId: Int): Call<List<Order>>
}



