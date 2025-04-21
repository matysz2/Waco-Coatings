package com.example.waco.network
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.waco.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title: String?
        val message: String?

        if (remoteMessage.notification != null) {
            // Powiadomienie z pola "notification"
            title = remoteMessage.notification?.title
            message = remoteMessage.notification?.body
        } else if (remoteMessage.data.isNotEmpty()) {
            // Powiadomienie z pola "data"
            title = remoteMessage.data["title"]
            message = remoteMessage.data["body"]
        } else {
            title = "Powiadomienie"
            message = "Brak treści"
        }

        sendNotification(title, message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nowy token: $token")

        val userId = getUserIdFromMemory()

        if (userId != -1) {
            RetrofitInstance.api.updateFcmToken(userId, token)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d("FCM", "Token zapisany pomyślnie")
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("FCM", "Błąd zapisu tokena", t)
                    }
                })
        } else {
            Log.e("FCM", "Brak user_id w pamięci")
        }
    }

    private fun sendNotification(title: String?, message: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "order_status_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Order Status", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val iconRes = when {
            message?.contains("dostarczone", ignoreCase = true) == true -> R.drawable.ic_order_delivered
            message?.contains("anulowane", ignoreCase = true) == true -> R.drawable.ic_order_cancelled
            message?.contains("wysłane", ignoreCase = true) == true -> R.drawable.ic_order_shipped
            else -> R.drawable.ic_notification
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title ?: "Zamówienie")
            .setContentText(message ?: "Status zamówienia się zmienił")
            .setSmallIcon(iconRes)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }

    private fun getUserIdFromMemory(): Int {
        val sharedPreferences: SharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "") ?: ""
        return if (userId.isNotEmpty()) userId.toInt() else -1
    }
}
