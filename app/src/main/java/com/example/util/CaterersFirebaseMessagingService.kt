package com.example.util

import android.util.Log
import com.example.data.local.CaterersDatabase
import com.example.data.models.NotificationEntity
import com.example.data.models.UserRole
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaterersFirebaseMessagingService : FirebaseMessagingService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "New Firebase Cloud Messaging Token generated: $token")
        FcmNotificationManager.setToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Push Notification received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val notification = remoteMessage.notification

        val title = notification?.title ?: data["title"] ?: "CaterersWale Alert"
        val body = notification?.body ?: data["body"] ?: "You have a new update."
        val roleStr = data["targetRole"] ?: "CUSTOMER"
        val orderId = data["orderId"] ?: ""
        val catererName = data["catererName"] ?: "CaterersWale Partner"
        val msgType = data["type"] ?: "STATUS_UPDATE"
        val customerName = data["customerName"] ?: "Customer"
        val totalAmount = data["totalAmount"]?.toDoubleOrNull() ?: 0.0
        val slot = data["deliverySlot"] ?: "Scheduled Slot"

        val targetRole = try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            UserRole.CUSTOMER
        }

        // Show native system notification with appropriate channel and priority
        if (targetRole == UserRole.KITCHEN || msgType == "NEW_ORDER") {
            NotificationHelper.showKitchenNewOrderNotification(
                context = applicationContext,
                orderId = orderId.ifBlank { "NEW" },
                customerName = customerName,
                orderTotal = totalAmount,
                deliverySlot = slot,
                catererName = catererName
            )
        } else {
            NotificationHelper.showCustomerDeliveryStatusNotification(
                context = applicationContext,
                orderId = orderId,
                title = title,
                message = body,
                catererName = catererName
            )
        }

        // Save to local Room database in background for offline viewing and unread count
        scope.launch {
            try {
                val db = CaterersDatabase.getInstance(applicationContext)
                db.caterersDao().insertNotification(
                    NotificationEntity(
                        id = "fcm_${System.currentTimeMillis()}",
                        title = title,
                        message = body,
                        targetRole = targetRole
                    )
                )
            } catch (e: Exception) {
                Log.w(TAG, "Failed to persist FCM notification into Room: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "CaterersFcmService"
    }
}
