package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.CaterersApplication
import com.example.data.models.CatererEntity
import com.example.data.models.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreFavoritesRepository(private val context: Context? = null) {

    private val tag = "FirestoreFavoritesRepo"
    private val usersCollection = "users"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            context?.let { CaterersApplication.initFirebase(it) }
            val apps = if (context != null) FirebaseApp.getApps(context) else emptyList()
            if (apps.isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                try {
                    FirebaseFirestore.getInstance()
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "FirebaseFirestore unavailable: ${e.message}")
            null
        }
    }

    /**
     * Real-time flow listening for user favorites from Firestore user document.
     */
    fun listenToUserFavorites(userId: String): Flow<List<String>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        var registration: ListenerRegistration? = null
        try {
            registration = db.collection(usersCollection).document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(tag, "Firestore favorites listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val rawList = snapshot.get("favoriteKitchenIds") as? List<*>
                        val favIds = rawList?.mapNotNull { it?.toString() } ?: emptyList()
                        trySend(favIds)
                    } else {
                        trySend(emptyList())
                    }
                }
        } catch (e: Exception) {
            Log.e(tag, "Error setting up favorites listener: ${e.message}")
            trySend(emptyList())
        }

        awaitClose {
            registration?.remove()
        }
    }

    /**
     * Toggles favorite status in the user's Firestore document.
     * Updates both favoriteKitchenIds array and favoriteKitchenProfiles map array for complete profile referencing.
     */
    fun toggleFavorite(
        userId: String,
        userProfile: UserProfile?,
        caterer: CatererEntity,
        isFavorite: Boolean,
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val db = firestore
        if (db == null) {
            onResult(false, "Firebase Firestore is not initialized")
            return
        }

        val docRef = db.collection(usersCollection).document(userId)

        val profileSummaryMap = hashMapOf<String, Any>(
            "catererId" to caterer.id,
            "catererName" to caterer.name,
            "kitchenName" to caterer.kitchenName,
            "rating" to caterer.rating,
            "address" to caterer.address,
            "city" to caterer.city,
            "deliveryTimeMinutes" to caterer.deliveryTimeMinutes,
            "minOrderAmount" to caterer.minOrderAmount,
            "isOpenForBooking" to caterer.isOpenForBooking,
            "fssaiLicense" to caterer.fssaiLicense,
            "favoritedAt" to System.currentTimeMillis()
        )

        // Ensure user document exists with basic metadata
        val baseUserData = hashMapOf<String, Any>(
            "userId" to userId,
            "name" to (userProfile?.name ?: "Customer"),
            "mobile" to (userProfile?.mobile ?: ""),
            "role" to (userProfile?.role?.name ?: "CUSTOMER"),
            "lastActive" to System.currentTimeMillis()
        )

        docRef.set(baseUserData, SetOptions.merge())
            .addOnSuccessListener {
                if (isFavorite) {
                    // Add caterer to favorites
                    docRef.update(
                        "favoriteKitchenIds", FieldValue.arrayUnion(caterer.id),
                        "favoriteKitchenProfiles", FieldValue.arrayUnion(profileSummaryMap),
                        "lastFavoriteUpdated" to System.currentTimeMillis()
                    ).addOnSuccessListener {
                        Log.i(tag, "Added ${caterer.name} to favorites in Firestore user doc ($userId)")
                        onResult(true, null)
                    }.addOnFailureListener { e ->
                        // If array update fails because field doesn't exist yet, do set with merge
                        val initialMap = hashMapOf<String, Any>(
                            "favoriteKitchenIds" to listOf(caterer.id),
                            "favoriteKitchenProfiles" to listOf(profileSummaryMap),
                            "lastFavoriteUpdated" to System.currentTimeMillis()
                        )
                        docRef.set(initialMap, SetOptions.merge())
                            .addOnSuccessListener { onResult(true, null) }
                            .addOnFailureListener { err -> onResult(false, err.message) }
                    }
                } else {
                    // Remove caterer from favorites
                    docRef.get().addOnSuccessListener { snapshot ->
                        val existingProfiles = snapshot.get("favoriteKitchenProfiles") as? List<Map<String, Any>> ?: emptyList()
                        val updatedProfiles = existingProfiles.filterNot { it["catererId"] == caterer.id }

                        docRef.update(
                            "favoriteKitchenIds", FieldValue.arrayRemove(caterer.id),
                            "favoriteKitchenProfiles", updatedProfiles,
                            "lastFavoriteUpdated" to System.currentTimeMillis()
                        ).addOnSuccessListener {
                            Log.i(tag, "Removed ${caterer.name} from favorites in Firestore user doc ($userId)")
                            onResult(true, null)
                        }.addOnFailureListener { e ->
                            onResult(false, e.message)
                        }
                    }.addOnFailureListener { e ->
                        onResult(false, e.message)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Failed to update Firestore user document: ${e.message}")
                onResult(false, e.message)
            }
    }
}
