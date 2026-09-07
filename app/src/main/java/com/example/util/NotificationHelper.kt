package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_KITCHEN_ALERTS = "catererswale_kitchen_alerts"
    const val CHANNEL_DELIVERY_STATUS = "catererswale_delivery_status"
    const val CHANNEL_GENERAL = "catererswale_general"

    const val EXTRA_ORDER_ID = "extra_order_id"
    const val EXTRA_TARGET_SCREEN = "extra_target_screen"
    const val EXTRA_ROLE = "extra_role"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Kitchen Urgent Incoming Orders Channel
            val kitchenChannel = NotificationChannel(
                CHANNEL_KITCHEN_ALERTS,
                "Kitchen Incoming Orders & Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent high-priority alarms for new bulk orders requiring kitchen preparation"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 800)
                enableLights(true)
                setShowBadge(true)
            }

            // 2. Customer Delivery Status Updates Channel
            val deliveryChannel = NotificationChannel(
                CHANNEL_DELIVERY_STATUS,
                "Order Delivery Status Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time updates on cooking, driver assignment, out for delivery, and arrival"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
                enableLights(true)
                setShowBadge(true)
            }

            // 3. General Announcements
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General Updates & Promotions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General platform notifications, wallet rewards and offers"
            }

            notificationManager.createNotificationChannels(listOf(kitchenChannel, deliveryChannel, generalChannel))
        }
    }

    /**
     * Show high-priority alert for Kitchen Partners when a new order arrives
     */
    fun showKitchenNewOrderNotification(
        context: Context,
        orderId: String,
        customerName: String,
        orderTotal: Double,
        deliverySlot: String,
        catererName: String = "Your Kitchen"
    ) {
        initNotificationChannels(context)
        val title = "🛎️ NEW ORDER ALERT! #$orderId"
        val message = "New order for ₹${orderTotal.toInt()} from $customerName ($deliverySlot). Tap to review & accept!"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_ORDER_ID, orderId)
            putExtra(EXTRA_TARGET_SCREEN, "KITCHEN_ORDERS")
            putExtra(EXTRA_ROLE, "KITCHEN")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            orderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(context, CHANNEL_KITCHEN_ALERTS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$message\n\nOutlet: $catererName\nStatus: Pending Kitchen Acceptance")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 800))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), builder.build())
    }

    /**
     * Show live delivery status push notification for customers
     */
    fun showCustomerDeliveryStatusNotification(
        context: Context,
        orderId: String,
        title: String,
        message: String,
        catererName: String = "Caterer",
        statusKey: String = "STATUS"
    ) {
        initNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_ORDER_ID, orderId)
            putExtra(EXTRA_TARGET_SCREEN, "LIVE_TRACKING")
            putExtra(EXTRA_ROLE, "CUSTOMER")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            orderId.hashCode() + 10,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_DELIVERY_STATUS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$message\n\nCaterer: $catererName • Order: #$orderId")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), builder.build())
    }

    fun showSystemNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = (System.currentTimeMillis() % 100000).toInt()
    ) {
        try {
            initNotificationChannels(context)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_GENERAL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            notificationManager.notify(notificationId, builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 9:30 AM Alert for Kitchen regarding pending Degs/Bartans
     */
    fun show930AmBartanAlertForKitchen(
        context: Context,
        kitchenName: String,
        pendingCount: Int,
        detailSummary: String
    ) {
        val title = "⏰ 9:30 AM Deg/Bartan Alert: $pendingCount Return Pending!"
        val message = "Kitchen: $kitchenName • Khana delivered containers are pending collection. Tap to track & coordinate with assigned delivery boys."
        showSystemNotification(context, title, "$message\n\n$detailSummary", 930101)
    }

    /**
     * 9:30 AM Alert for Delivery Boy who originally delivered the order
     */
    fun show930AmBartanAlertForDeliveryBoy(
        context: Context,
        deliveryBoyName: String,
        customerName: String,
        utensilDescription: String,
        address: String,
        catererName: String
    ) {
        val title = "🛵 9:30 AM Pickup Task: Collect Deg/Bartan"
        val message = "$deliveryBoyName, please collect $utensilDescription from $customerName at $address and return to $catererName."
        showSystemNotification(context, title, message, (930200 + System.currentTimeMillis() % 1000).toInt())
    }

    /**
     * Manual Overdue Container Alert triggered by Kitchen to a specific delivery boy
     */
    fun showOverdueContainerReminderForDeliveryBoy(
        context: Context,
        deliveryBoyName: String,
        customerName: String,
        customerMobile: String,
        customerAddress: String,
        utensilDescription: String,
        orderId: String,
        catererName: String,
        daysOverdue: Int
    ) {
        initNotificationChannels(context)
        val title = "🚨 URGENT: Deg/Bartan Overdue ($daysOverdue Days Late)!"
        val message = "Hey $deliveryBoyName, Order #$orderId containers ($utensilDescription) at $customerName ($customerMobile, $customerAddress) are OVERDUE. Please collect and return to $catererName immediately."
        showSystemNotification(context, title, message, (930600 + Math.abs(orderId.hashCode() % 1000)))
    }
}

