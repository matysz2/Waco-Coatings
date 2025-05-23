package com.example.waco.network
import com.example.waco.data.ColorResponse
import com.example.waco.data.Invoice
import com.example.waco.data.InvoiceResponse
import com.example.waco.data.Order
import com.example.waco.data.OrderDetails
import com.example.waco.data.OrderItem
import com.example.waco.data.OrderRequest
import com.example.waco.data.OrderStatusResponse
import com.example.waco.data.OrdersItem
import com.example.waco.data.PriceItem
import com.example.waco.data.Product
import com.example.waco.data.Product2
import com.example.waco.data.ProductDetailResponse
import com.example.waco.model.AccountUpdateRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
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


    @FormUrlEncoded
    @POST("save_fcm_token.php")
    fun updateFcmToken(
        @Field("user_id") userId: Int,
        @Field("fcm_token") fcmToken: String
    ): Call<Void>

    @GET("get_order_status.php")
    fun getOrderStatus(
        @Query("order_id") orderId: Int
    ): Call<OrderStatusResponse>


    @GET("get_order_details.php")
    fun getOrderDetails(@Query("order_id") orderId: Int): Call<OrderDetails>



        @FormUrlEncoded
        @POST("find_colors.php")
        fun sendColor(
            @Field("colorHex") colorHex: String,
            @Field("colorRGB") colorRGB: String
        ): Call<ColorResponse> // <- odpowiedź JSON


    @FormUrlEncoded
    @POST("loginadmin.php")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("get_invoices.php")
    fun getInvoices(
        @Field("user_id") userId: Int
    ): Call<InvoiceResponse>


    @GET("get_all_invoices.php")
    fun getAllInvoices(): Call<List<Invoice>>


    @GET("get_all_orders.php")
    fun getAllOrders(): Call<List<Order>>

    @GET("dashboard_data.php")
    fun getDashboardData(@Query("user_id") userId: String): Call<ResponseBody>

    @GET("get_invoices.php")
    fun getInvoices(@Query("user_id") userId: String): Call<List<Invoice>>

    @Headers("Content-Type: application/json")
    @POST("update_account.php")
    fun updateAccountData(@Body requestBody: AccountUpdateRequest): Call<ResponseBody>


    @GET("get_order.php")
    fun getOrders(@Query("user_id") userId: String): Call<List<Order>>

    @GET("getOrderItems.php")
    fun getOrderItems(@Query("order_id") orderId: String): Call<List<OrdersItem>>

    @GET("get_orders_details.php")
    fun getOrdersDetails(@Query("order_id") orderId: String): Call<List<OrdersItem>>


    @GET("get_prices.php")
    suspend fun getPrices(
        @Query("group") group: String,
        @Query("price") priceColumn: String
    ): List<PriceItem>



    @GET("get_product_details.php")
    fun getProductDetails(@Query("code") code: String): Call<ProductDetailResponse>


    @FormUrlEncoded
    @POST("add_comment.php")
    fun postComment(
        @Field("product_id") productId: String,
        @Field("comment") comment: String,
        @Field("rating") rating: Int,
        @Field("username") username: String
    ): Call<Void>


    @FormUrlEncoded
    @POST("update_token.php")
    fun updateFcmTokenAdmin(
        @Field("user_id") userId: Int,
        @Field("token") token: String
    ): Call<Void>

}




