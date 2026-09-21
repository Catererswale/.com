package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.BartanRecordEntity
import com.example.data.models.CartItemEntity
import com.example.data.models.CatererEntity
import com.example.data.models.DeliveryBoyEntity
import com.example.data.models.FavoriteKitchenEntity
import com.example.data.models.KitchenUtensilEntity
import com.example.data.models.MenuItemEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.OrderEntity
import com.example.data.models.PartnerReviewEntity

@Database(
    entities = [
        CatererEntity::class,
        MenuItemEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        DeliveryBoyEntity::class,
        BartanRecordEntity::class,
        NotificationEntity::class,
        PartnerReviewEntity::class,
        FavoriteKitchenEntity::class,
        KitchenUtensilEntity::class
    ],
    version = 15,
    exportSchema = false
)
abstract class CaterersDatabase : RoomDatabase() {
    abstract fun caterersDao(): CaterersDao

    companion object {
        @Volatile
        private var INSTANCE: CaterersDatabase? = null

        fun getInstance(context: Context): CaterersDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CaterersDatabase::class.java,
                    "caterers_wale_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
