package com.example

import com.example.data.models.FavoriteKitchenEntity
import com.example.data.models.UserProfile
import com.example.data.models.UserRole
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testFavoriteKitchenEntityCreation() {
    val favorite = FavoriteKitchenEntity(
      id = "cust_1_cat_1",
      userId = "cust_1",
      catererId = "cat_1",
      catererName = "Royal Nawabi Caterers",
      kitchenName = "Royal Nawabi Cloud Kitchen",
      rating = 4.8f,
      cuisineType = "Awadhi & Biryani",
      favoritedAt = 1700000000L
    )

    assertEquals("cust_1_cat_1", favorite.id)
    assertEquals("cust_1", favorite.userId)
    assertEquals("cat_1", favorite.catererId)
    assertEquals("Royal Nawabi Caterers", favorite.catererName)
    assertEquals(4.8f, favorite.rating, 0.01f)
  }

  @Test
  fun testUserProfileFavoritesList() {
    val profile = UserProfile(
      id = "cust_1",
      name = "Rohan Verma",
      phone = "+91 98765 11223",
      role = UserRole.CUSTOMER,
      favoriteKitchenIds = listOf("cat_1", "cat_2")
    )

    assertEquals(2, profile.favoriteKitchenIds.size)
    assertTrue(profile.favoriteKitchenIds.contains("cat_1"))
    assertTrue(profile.favoriteKitchenIds.contains("cat_2"))
    assertFalse(profile.favoriteKitchenIds.contains("cat_3"))
  }
}

