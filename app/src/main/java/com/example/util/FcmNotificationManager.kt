package com.example.util

import android.content.Context
import android.util.Log
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.models.UserRole
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Data class representing a real-time FCM Push Event received
 */
data class FcmPushEvent(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val body: String,
    val targetRole: UserRole = UserRole.CUSTOMER,
    val orderId: String? = null,
    val catererId: String? = null,
    val status: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isUrgentAlarm: Boolean = false
)

object FcmNotificationManager {

    private const val TAG = "FcmNotificationManager"

    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken: StateFlow<String?> = _fcmToken.asStateFlow()

    private val _subscribedTopics = MutableStateFlow<Set<String>>(emptySet())
    val subscribedTopics: StateFlow<Set<String>> = _subscribedTopics.asStateFlow()

    // Shared flow for real-time in-app heads-up banners
    private val _incomingPushEvents = MutableSharedFlow<FcmPushEvent>(extraBufferCapacity = 64)
    val incomingPushEvents: SharedFlow<FcmPushEvent> = _incomingPushEvents.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    fun initialize(context: Context) {
        NotificationHelper.initNotificationChannels(context)
        val isGmsAvailable = try {
            val availability = com.google.android.gms.common.GoogleApiAvailability.getInstance()
            availability.isGooglePlayServicesAvailable(context) == com.google.android.gms.common.ConnectionResult.SUCCESS
        } catch (_: Throwable) {
            false
        }

        if (isGmsAvailable) {
            fetchFcmToken()
        } else {
            Log.i(TAG, "Google Play Services unavailable in current runtime environment; running local notification engine.")
            _fcmToken.value = "fcm_sandbox_token_${System.currentTimeMillis() % 1000000}"
            _subscribedTopics.value = setOf("catererswale_all_users")
        }
    }

    fun fetchFcmToken() {
        try {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Log.d(TAG, "FCM Registration Token fetched: $token")
                    _fcmToken.value = token
                    try {
                        FirebaseMessaging.getInstance().subscribeToTopic("catererswale_all_users")
                            .addOnSuccessListener {
                                _subscribedTopics.value = _subscribedTopics.value + "catererswale_all_users"
                            }
                    } catch (e: Exception) {
                        Log.d(TAG, "FCM topic subscribe skipped: ${e.message}")
                    }
                }
                .addOnFailureListener { e ->
                    Log.i(TAG, "FCM token registration unavailable (${e.message}); using local notification engine.")
                    if (_fcmToken.value == null) {
                        _fcmToken.value = "fcm_token_catererswale_${System.currentTimeMillis() % 1000000}"
                    }
                    _subscribedTopics.value = _subscribedTopics.value + "catererswale_all_users"
                }
        } catch (e: Exception) {
            Log.i(TAG, "FirebaseMessaging unavailable: ${e.message}")
            _fcmToken.value = "fcm_sandbox_token_${System.currentTimeMillis() % 1000000}"
            _subscribedTopics.value = _subscribedTopics.value + "catererswale_all_users"
        }
    }

    fun setToken(token: String) {
        _fcmToken.value = token
    }

    fun subscribeToTopic(topic: String) {
        val cleanTopic = topic.replace("[^a-zA-Z0-9-_.~%]".toRegex(), "_")
        _subscribedTopics.value = _subscribedTopics.value + cleanTopic
        val token = _fcmToken.value
        if (token != null && !token.startsWith("fcm_token_catererswale_") && !token.startsWith("fcm_sandbox_")) {
            try {
                FirebaseMessaging.getInstance().subscribeToTopic(cleanTopic)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Subscribed to FCM topic: $cleanTopic")
                        }
                    }
            } catch (e: Exception) {
                Log.d(TAG, "Topic subscription skipped: ${e.message}")
            }
        }
    }

    fun unsubscribeFromTopic(topic: String) {
        val cleanTopic = topic.replace("[^a-zA-Z0-9-_.~%]".toRegex(), "_")
        _subscribedTopics.value = _subscribedTopics.value - cleanTopic
        val token = _fcmToken.value
        if (token != null && !token.startsWith("fcm_token_catererswale_") && !token.startsWith("fcm_sandbox_")) {
            try {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(cleanTopic)
            } catch (e: Exception) {
                Log.d(TAG, "Topic unsubscription skipped: ${e.message}")
            }
        }
    }

    /**
     * Subscribe a Kitchen Partner to their private kitchen topic
     */
    fun subscribeKitchen(catererId: String) {
        val topic = "kitchen_${catererId.replace(" ", "_")}"
        subscribeToTopic(topic)
        subscribeToTopic("all_kitchen_partners")
    }

    /**
     * Subscribe Customer to updates for a specific order
     */
    fun subscribeCustomerOrder(orderId: String) {
        val topic = "order_${orderId.replace(" ", "_")}"
        subscribeToTopic(topic)
        subscribeToTopic("all_customers")
    }

    /**
     * Dispatch an urgent real-time FCM Push Notification to Kitchen Partner
     */
    fun notifyKitchenNewOrder(
        context: Context,
        orderId: String,
        customerName: String,
        orderTotal: Double,
        deliverySlot: String,
        catererId: String,
        catererName: String
    ) {
        val title = "🛎️ NEW ORDER ALERT! #$orderId"
        val body = "New bulk order for ₹${orderTotal.toInt()} from $customerName ($deliverySlot). Tap to accept & start prep!"

        // 1. Post Android System Notification with custom channel & high priority
        NotificationHelper.showKitchenNewOrderNotification(
            context = context,
            orderId = orderId,
            customerName = customerName,
            orderTotal = orderTotal,
            deliverySlot = deliverySlot,
            catererName = catererName
        )

        // 2. Emit in-app push event for live heads-up banner
        val event = FcmPushEvent(
            title = title,
            body = body,
            targetRole = UserRole.KITCHEN,
            orderId = orderId,
            catererId = catererId,
            status = "NEW",
            isUrgentAlarm = true
        )
        scope.launch {
            _incomingPushEvents.emit(event)
        }
    }

    /**
     * Dispatch a real-time FCM Delivery Status update Push Notification to Customer
     */
    fun notifyCustomerOrderStatusUpdate(
        context: Context,
        order: OrderEntity,
        newStatus: OrderStatus
    ) {
        val driver = order.deliveryBoyName ?: "Assigned Delivery Partner"
        val (title, body) = when (newStatus) {
            OrderStatus.NEW -> Pair(
                "Order Placed with ${order.catererName} 🛎️",
                "Order #${order.orderId} sent to kitchen. Awaiting confirmation."
            )
            OrderStatus.ACCEPTED, OrderStatus.CONFIRMED -> Pair(
                "Order Confirmed by ${order.catererName}! 🟢",
                "Kitchen has accepted order #${order.orderId} and scheduled cooking."
            )
            OrderStatus.PREPARING -> Pair(
                "🍳 Food Preparation Started!",
                "Chef at ${order.catererName} has started cooking your fresh feast for order #${order.orderId}."
            )
            OrderStatus.READY -> Pair(
                "📦 Order Packed & Ready!",
                "Kitchen ${order.catererName} has finished cooking & packed order #${order.orderId} in insulated warmers."
            )
            OrderStatus.ASSIGNED_DELIVERY -> Pair(
                "🚴 Valet Assigned: $driver",
                "Delivery driver $driver is assigned to pick up order #${order.orderId}."
            )
            OrderStatus.OUT_FOR_DELIVERY -> Pair(
                "🚚 Out for Delivery!",
                "Driver $driver is en route with order #${order.orderId}. Keep Delivery OTP (${order.deliveryOtp}) ready!"
            )
            OrderStatus.DELIVERED -> Pair(
                "🎉 Order Delivered! Bon Appétit",
                "Order #${order.orderId} successfully delivered with OTP verification. Enjoy your feast!"
            )
            OrderStatus.CANCELLED -> Pair(
                "❌ Order #${order.orderId} Cancelled",
                "Your order #${order.orderId} was cancelled. Refund will be processed as per policy."
            )
            OrderStatus.ON_HOLD -> Pair(
                "⏸️ Order #${order.orderId} Placed On Hold",
                "Booking enquiry #${order.orderId} with ${order.catererName} is on hold pending confirmation."
            )
        }

        // 1. Post Android System Notification
        NotificationHelper.showCustomerDeliveryStatusNotification(
            context = context,
            orderId = order.orderId,
            title = title,
            message = body,
            catererName = order.catererName,
            statusKey = newStatus.name
        )

        // 2. Emit in-app push event
        val event = FcmPushEvent(
            title = title,
            body = body,
            targetRole = UserRole.CUSTOMER,
            orderId = order.orderId,
            catererId = order.catererId,
            status = newStatus.name,
            isUrgentAlarm = false
        )
        scope.launch {
            _incomingPushEvents.emit(event)
        }
    }

    /**
     * Test push trigger for previewing FCM alerts in real-time
     */
    fun sendTestPush(
        context: Context,
        role: UserRole,
        title: String,
        message: String,
        orderId: String = "CW-${System.currentTimeMillis() % 10000}"
    ) {
        if (role == UserRole.KITCHEN) {
            NotificationHelper.showKitchenNewOrderNotification(
                context = context,
                orderId = orderId,
                customerName = "Priya Sharma",
                orderTotal = 8500.0,
                deliverySlot = "Today, 01:30 PM",
                catererName = "A1 Huma Caterers"
            )
        } else {
            NotificationHelper.showCustomerDeliveryStatusNotification(
                context = context,
                orderId = orderId,
                title = title,
                message = message,
                catererName = "A1 Huma Caterers"
            )
        }

        val event = FcmPushEvent(
            title = title,
            body = message,
            targetRole = role,
            orderId = orderId,
            isUrgentAlarm = role == UserRole.KITCHEN
        )
        scope.launch {
            _incomingPushEvents.emit(event)
        }
    }
}
