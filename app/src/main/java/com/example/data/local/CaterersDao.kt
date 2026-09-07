package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.BartanRecordEntity
import com.example.data.models.CartItemEntity
import com.example.data.models.CatererEntity
import com.example.data.models.DeliveryBoyEntity
import com.example.data.models.FavoriteKitchenEntity
import com.example.data.models.MenuItemEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.models.PartnerReviewEntity
import com.example.data.models.PaymentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CaterersDao {

    // Caterers
    @Query("SELECT * FROM caterers")
    fun getAllCaterers(): Flow<List<CatererEntity>>

    @Query("SELECT * FROM caterers WHERE id = :id")
    suspend fun getCatererById(id: String): CatererEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaterers(caterers: List<CatererEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaterer(caterer: CatererEntity)

    @Update
    suspend fun updateCaterer(caterer: CatererEntity)

    // Menu Items
    @Query("SELECT * FROM menu_items")
    fun getAllMenuItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE catererId = :catererId")
    fun getMenuItemsByCaterer(catererId: String): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE category = :category")
    fun getMenuItemsByCategory(category: String): Flow<List<MenuItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(items: List<MenuItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(item: MenuItemEntity)

    @Query("DELETE FROM menu_items WHERE id = :id")
    suspend fun deleteMenuItem(id: String)

    // Cart Items
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Orders
    @Query("SELECT * FROM orders ORDER BY createdAtTimestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE catererId = :catererId ORDER BY createdAtTimestamp DESC")
    fun getOrdersByCaterer(catererId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderByIdFlow(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET orderStatus = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus)

    @Query("UPDATE orders SET orderStatus = :status, paymentStatus = :paymentStatus WHERE orderId = :orderId")
    suspend fun updateOrderStatusAndPayment(orderId: String, status: OrderStatus, paymentStatus: PaymentStatus)

    @Query("UPDATE orders SET deliveryBoyId = :boyId, deliveryBoyName = :boyName, deliveryBoyMobile = :boyMobile, orderStatus = :status WHERE orderId = :orderId")
    suspend fun assignDeliveryBoy(orderId: String, boyId: String, boyName: String, boyMobile: String, status: OrderStatus)

    @Query("UPDATE orders SET userRating = :rating, userReview = :review WHERE orderId = :orderId")
    suspend fun updateOrderReview(orderId: String, rating: Float, review: String)

    @Query("UPDATE orders SET isBartanReturned = :isReturned WHERE orderId = :orderId")
    suspend fun updateBartanStatus(orderId: String, isReturned: Boolean)

    @Query("UPDATE orders SET isCashSubmittedToKitchen = :submitted, paymentStatus = :paymentStatus WHERE orderId = :orderId")
    suspend fun updateOrderCashSubmitted(orderId: String, submitted: Boolean, paymentStatus: PaymentStatus = PaymentStatus.FULLY_SETTLED)

    @Query("UPDATE orders SET cashCollectedByDeliveryBoy = :cashAmount, isCashSubmittedToKitchen = :submitted WHERE orderId = :orderId")
    suspend fun updateOrderCashCollected(orderId: String, cashAmount: Double, submitted: Boolean)

    // Delivery Boys
    @Query("SELECT * FROM delivery_boys")
    fun getAllDeliveryBoys(): Flow<List<DeliveryBoyEntity>>

    @Query("SELECT * FROM delivery_boys WHERE kitchenId = :kitchenId")
    fun getDeliveryBoysByKitchen(kitchenId: String): Flow<List<DeliveryBoyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryBoy(boy: DeliveryBoyEntity)

    @Update
    suspend fun updateDeliveryBoy(boy: DeliveryBoyEntity)

    @Query("DELETE FROM delivery_boys WHERE id = :boyId AND kitchenId = :kitchenId")
    suspend fun deleteDeliveryBoy(boyId: String, kitchenId: String)

    @Query("UPDATE delivery_boys SET isOnline = :isOnline WHERE id = :boyId")
    suspend fun updateDeliveryBoyOnlineStatus(boyId: String, isOnline: Boolean)

    // Bartan Records
    @Query("SELECT * FROM bartan_records ORDER BY id DESC")
    fun getAllBartanRecords(): Flow<List<BartanRecordEntity>>

    @Query("SELECT * FROM bartan_records WHERE catererId = :catererId ORDER BY id DESC")
    fun getBartanRecordsByCaterer(catererId: String): Flow<List<BartanRecordEntity>>

    @Query("SELECT * FROM bartan_records WHERE deliveryBoyId = :deliveryBoyId ORDER BY id DESC")
    fun getBartanRecordsByDeliveryBoy(deliveryBoyId: String): Flow<List<BartanRecordEntity>>

    @Query("SELECT * FROM bartan_records WHERE isCollected = 0")
    suspend fun getPendingBartanRecordsList(): List<BartanRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBartanRecord(record: BartanRecordEntity)

    @Query("UPDATE bartan_records SET isCollected = :collected, collectedDate = :date WHERE id = :id")
    suspend fun updateBartanCollected(id: String, collected: Boolean, date: String)

    @Query("UPDATE bartan_records SET isCollected = :collected, collectedDate = :date, returnStatus = :status WHERE id = :id")
    suspend fun updateBartanCollectedFull(id: String, collected: Boolean, date: String, status: String)

    @Query("UPDATE bartan_records SET morningAlertSent = :alertSent, lastMorningAlertDate = :alertDate WHERE id = :id")
    suspend fun updateBartanMorningAlert(id: String, alertSent: Boolean, alertDate: String)

    // Notifications
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    // Partner Reviews & Feedback
    @Query("SELECT * FROM partner_reviews ORDER BY createdAtTimestamp DESC")
    fun getAllReviews(): Flow<List<PartnerReviewEntity>>

    @Query("SELECT * FROM partner_reviews WHERE catererId = :catererId ORDER BY createdAtTimestamp DESC")
    fun getReviewsByCaterer(catererId: String): Flow<List<PartnerReviewEntity>>

    @Query("SELECT * FROM partner_reviews WHERE orderId = :orderId LIMIT 1")
    suspend fun getReviewByOrderId(orderId: String): PartnerReviewEntity?

    @Query("SELECT * FROM partner_reviews WHERE catererId = :catererId")
    suspend fun getReviewsListForCaterer(catererId: String): List<PartnerReviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: PartnerReviewEntity)

    @Query("DELETE FROM partner_reviews WHERE id = :id")
    suspend fun deleteReview(id: String)

    // User Favorites (Kitchen Partners)
    @Query("SELECT * FROM user_favorites WHERE userId = :userId ORDER BY favoritedAt DESC")
    fun getFavoritesByUser(userId: String): Flow<List<FavoriteKitchenEntity>>

    @Query("SELECT catererId FROM user_favorites WHERE userId = :userId")
    fun getFavoriteCatererIds(userId: String): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM user_favorites WHERE userId = :userId AND catererId = :catererId)")
    suspend fun isFavorite(userId: String, catererId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteKitchenEntity)

    @Query("DELETE FROM user_favorites WHERE userId = :userId AND catererId = :catererId")
    suspend fun deleteFavorite(userId: String, catererId: String)
}
