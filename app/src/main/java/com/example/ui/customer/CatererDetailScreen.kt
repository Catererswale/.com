package com.example.ui.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import com.example.ui.theme.VegGreen
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.models.CatererEntity
import com.example.data.models.FoodType
import com.example.data.models.KycStatus
import com.example.data.models.MenuItemEntity
import com.example.data.models.UnitType
import com.example.data.repository.CaterersViewModel
import com.example.ui.common.FssaiBadge
import com.example.ui.common.RatingBadge
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import com.example.ui.common.UnitQuantityPicker
import com.example.ui.common.VegNonVegBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary

@Composable
fun CatererDetailScreen(
    catererId: String,
    viewModel: CaterersViewModel,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenReviews: (String) -> Unit = {},
    isExclusiveStore: Boolean = false,
    isPartnerPreviewMode: Boolean = false,
    onReturnToKitchen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val caterers by viewModel.caterersList.collectAsState()
    val fallbackCaterer = remember {
        CatererEntity(
            id = "caterer_1",
            name = "A1 Huma Caterers",
            kitchenName = "A1 Huma Central Kitchen",
            logoUrl = "",
            bannerUrl = "",
            rating = 4.8f,
            reviewCount = 342,
            deliveryTimeMinutes = 45,
            minOrderAmount = 1000.0,
            deliveryCharge = 150.0,
            distanceKm = 2.4,
            fssaiLicense = "11223344556677",
            isFssaiVerified = true,
            isOpenForBooking = true,
            address = "Plot 42, Catering Market, Okhla Phase 3",
            city = "New Delhi",
            ownerMobile = "+91 9876543210",
            kycStatus = KycStatus.APPROVED
        )
    }
    val caterer = caterers.find { it.id == catererId } ?: caterers.firstOrNull() ?: fallbackCaterer
    val menuItems by viewModel.menuItemsList.collectAsState()
    val cartItems by viewModel.cartItemsList.collectAsState()
    val favoriteKitchenIds by viewModel.favoriteKitchenIds.collectAsState()
    val isFavorite = favoriteKitchenIds.contains(caterer.id)

    val dbMenu = menuItems.filter { it.catererId == caterer.id }
    val catererMenu = if (dbMenu.isNotEmpty()) dbMenu else remember {
        listOf(
            MenuItemEntity(
                id = "item_1",
                catererId = caterer.id,
                catererName = caterer.name,
                name = "Special Dum Chicken Biryani",
                description = "Slow cooked royal aromatic dum biryani with marinated tender chicken and aged basmati rice.",
                pricePerUnit = 480.0,
                unitType = UnitType.KG,
                category = "Biryani & Rice",
                foodType = FoodType.NON_VEG,
                imageUrl = "",
                minQuantity = 1.0,
                stepQuantity = 0.5,
                maxQuantity = 50.0,
                isAvailable = true,
                prepTimeMinutes = 90
            ),
            MenuItemEntity(
                id = "item_2",
                catererId = caterer.id,
                catererName = caterer.name,
                name = "Royal Hyderabadi Mutton Biryani",
                description = "Authentic rich mutton dum biryani packed in seal-packed handi with fried onions & saffron.",
                pricePerUnit = 750.0,
                unitType = UnitType.KG,
                category = "Biryani & Rice",
                foodType = FoodType.NON_VEG,
                imageUrl = "",
                minQuantity = 1.0,
                stepQuantity = 0.5,
                maxQuantity = 50.0,
                isAvailable = true,
                prepTimeMinutes = 120
            ),
            MenuItemEntity(
                id = "item_3",
                catererId = caterer.id,
                catererName = caterer.name,
                name = "Shahi Paneer Dum Biryani",
                description = "Fresh cottage cheese cooked with long grain basmati rice and rich whole spices.",
                pricePerUnit = 380.0,
                unitType = UnitType.KG,
                category = "Biryani & Rice",
                foodType = FoodType.VEG,
                imageUrl = "",
                minQuantity = 1.0,
                stepQuantity = 0.5,
                maxQuantity = 50.0,
                isAvailable = true,
                prepTimeMinutes = 60
            ),
            MenuItemEntity(
                id = "item_4",
                catererId = caterer.id,
                catererName = caterer.name,
                name = "Shahi Zafrani Kheer",
                description = "Slow simmered creamy saffron rice pudding enriched with dry fruits & cardamom.",
                pricePerUnit = 320.0,
                unitType = UnitType.LITRE,
                category = "Desserts & Sweets",
                foodType = FoodType.VEG,
                imageUrl = "",
                minQuantity = 1.0,
                stepQuantity = 0.5,
                maxQuantity = 50.0,
                isAvailable = true,
                prepTimeMinutes = 45
            )
        )
    }

    val categories = listOf("All") + catererMenu.map { it.category }.distinct()
    var selectedCategory by remember { mutableStateOf("All") }
    var eventGuestCount by remember { mutableStateOf(20) }
    var isGuestCalculatorActive by remember { mutableStateOf(false) }

    // Kitchen specific food serving ratios (1 Kg me kitne log khaenge)
    val liveKitchenSettings by viewModel.kitchenSettings.collectAsState()
    val bPerKg = if (caterer.id == "caterer_1") liveKitchenSettings.biryaniPersonsPerKg else if (caterer.biryaniPersonsPerKg > 0) caterer.biryaniPersonsPerKg else 6.67
    val sPerKg = if (caterer.id == "caterer_1") liveKitchenSettings.sweetPersonsPerKg else if (caterer.sweetPersonsPerKg > 0) caterer.sweetPersonsPerKg else 12.5
    val gPerKg = if (caterer.id == "caterer_1") liveKitchenSettings.gravyPersonsPerKg else if (caterer.gravyPersonsPerKg > 0) caterer.gravyPersonsPerKg else 8.33
    val rPerUnit = if (caterer.id == "caterer_1") liveKitchenSettings.rotiPersonsPerUnit else if (caterer.rotiPersonsPerUnit > 0) caterer.rotiPersonsPerUnit else 0.5

    val filteredItems = if (selectedCategory == "All") catererMenu
    else catererMenu.filter { it.category == selectedCategory }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9F6F0)),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header Image & Navigation
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    if (caterer.bannerUrl.isNotBlank()) {
                        AsyncImage(
                            model = caterer.bannerUrl,
                            contentDescription = "Kitchen Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner_1784989627411),
                            contentDescription = "Kitchen Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .testTag("caterer_detail_back")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out ${caterer.kitchenName} on CaterersWale:\nhttps://catererswale.app/store/${caterer.id}\nDirect Booking & Royal Mughlai Menu!")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Digital Store"))
                            },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share Store", tint = Color.White)
                        }

                        IconButton(
                            onClick = { viewModel.toggleFavoriteKitchen(caterer) },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .testTag("caterer_detail_favorite")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (isFavorite) Color(0xFFFF5252) else Color.White
                            )
                        }

                        IconButton(
                            onClick = onOpenCart,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                .testTag("caterer_detail_cart")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (cartItems.isNotEmpty()) {
                                        Badge { Text(cartItems.size.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = "Cart", tint = Color.White)
                            }
                        }
                    }
                }
            }

            // Exclusive Digital Store / Partner Preview Banner
            if (isPartnerPreviewMode) {
                item {
                    Surface(
                        color = Color(0xFF0F172A),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Store, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Customer View (ग्राहक को ऐसी दिखेगी)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    Text("Sirf aapki dukaan ka exclusive view", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                }
                            }
                            Button(
                                onClick = onReturnToKitchen,
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Back to Kitchen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else if (isExclusiveStore) {
                item {
                    Surface(
                        color = Color(0xFFFFF7ED),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AmberSecondary.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "👑 Official Exclusive Digital Store: Direct from ${caterer.kitchenName}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9A3412)
                            )
                        }
                    }
                }
            }

            // Kitchen Information Box
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SaffronPrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (caterer.logoUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = caterer.logoUrl,
                                            contentDescription = caterer.name,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Fastfood,
                                            contentDescription = null,
                                            tint = SaffronPrimary,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = caterer.name,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF212121)
                                    )
                                    Text(
                                        text = caterer.kitchenName,
                                        fontSize = 12.sp,
                                        color = SaffronPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clickable { onOpenReviews(caterer.id) }
                                        .testTag("caterer_reviews_badge")
                                ) {
                                    RatingBadge(rating = caterer.rating, reviewCount = caterer.reviewCount)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { viewModel.toggleFavoriteKitchen(caterer) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("caterer_detail_info_favorite")
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFavorite) Color(0xFFE53935) else Color(0xFF9E9E9E),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FssaiBadge(licenseNo = caterer.fssaiLicense)
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { onOpenReviews(caterer.id) }
                            ) {
                                Text(
                                    text = "★ Read ${caterer.reviewCount}+ Reviews ›",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("📍 ${caterer.address}", fontSize = 12.sp, color = Color.Gray)
                            Text("⏱️ ${caterer.deliveryTimeMinutes} mins", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Birthday & Function Guest Quantity Calculator (10 to 200 Guests)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isGuestCalculatorActive) Color(0xFFFFF7ED) else Color.White
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (isGuestCalculatorActive) SaffronPrimary else Color(0xFFE2E8F0)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isGuestCalculatorActive) SaffronPrimary else Color(0xFFFFF3E0),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Cake,
                                            contentDescription = null,
                                            tint = if (isGuestCalculatorActive) Color.White else SaffronPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "⚡ Event Food Calculator (10 - 200 Guests)",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isGuestCalculatorActive) Color(0xFF9A3412) else Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = "Birthday/Functions ke liye auto-suggest quantity",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Button(
                                onClick = { isGuestCalculatorActive = !isGuestCalculatorActive },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isGuestCalculatorActive) SaffronPrimary else Color(0xFFF1F5F9),
                                    contentColor = if (isGuestCalculatorActive) Color.White else Color(0xFF475569)
                                ),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("toggle_guest_calc")
                            ) {
                                Text(
                                    text = if (isGuestCalculatorActive) "Active ✓" else "Calculate",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (isGuestCalculatorActive) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Select Guests (मेहमान संख्या):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF431407)
                                )
                                Surface(
                                    color = SaffronPrimary,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "👥 $eventGuestCount Guests",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Slider(
                                value = eventGuestCount.toFloat(),
                                onValueChange = { eventGuestCount = it.toInt() },
                                valueRange = 10f..200f,
                                steps = 18,
                                colors = SliderDefaults.colors(
                                    thumbColor = SaffronPrimary,
                                    activeTrackColor = SaffronPrimary,
                                    inactiveTrackColor = Color(0xFFFED7AA)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Quick selection chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf(10, 25, 50, 100, 200).forEach { count ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (eventGuestCount == count) SaffronPrimary else Color.White,
                                        border = BorderStroke(1.dp, if (eventGuestCount == count) SaffronPrimary else Color(0xFFFED7AA)),
                                        modifier = Modifier.clickable { eventGuestCount = count }
                                    ) {
                                        Text(
                                            text = "$count Pax",
                                            fontSize = 10.sp,
                                            fontWeight = if (eventGuestCount == count) FontWeight.Bold else FontWeight.Normal,
                                            color = if (eventGuestCount == count) Color.White else Color(0xFF7C2D12),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Per-kitchen custom formula header & ratios
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                modifier = Modifier.fillMaxWidth().testTag("kitchen_portion_ratios_display")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "👨‍🍳 ${caterer.kitchenName} की 1 Kg सर्विंग दर (Serving Capacity):",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("• 1 Kg Biryani = ~${String.format("%.1f", bPerKg)} लोग", fontSize = 11.sp, color = Color(0xFF78350F), fontWeight = FontWeight.SemiBold)
                                        Text("• 1 Kg Meetha = ~${String.format("%.1f", sPerKg)} लोग", fontSize = 11.sp, color = Color(0xFF78350F), fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("• 1 Kg Gravy = ~${String.format("%.1f", gPerKg)} लोग", fontSize = 11.sp, color = Color(0xFF78350F), fontWeight = FontWeight.SemiBold)
                                        Text("• Roti = ${(1.0 / rPerUnit.coerceAtLeast(0.1)).toInt()} पीस / व्यक्ति", fontSize = 11.sp, color = Color(0xFF78350F), fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Exact live counting breakdown based on kitchen's ratios
                            val biryaniKg = String.format("%.1f", eventGuestCount / bPerKg.coerceAtLeast(0.1))
                            val sweetKg = String.format("%.1f", eventGuestCount / sPerKg.coerceAtLeast(0.1))
                            val gravyKg = String.format("%.1f", eventGuestCount / gPerKg.coerceAtLeast(0.1))
                            val rotiPcs = (eventGuestCount / rPerUnit.coerceAtLeast(0.1)).toInt()

                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                                modifier = Modifier.fillMaxWidth().testTag("event_food_counting_card")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "📊 $eventGuestCount लोगों के लिए लाइव काउंटिंग (Live Counting):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9A3412)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Biryani", fontSize = 10.sp, color = Color.Gray)
                                            Text("🍲 $biryaniKg Kg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Sweet/Meetha", fontSize = 10.sp, color = Color.Gray)
                                            Text("🍬 $sweetKg Kg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Gravy/Salan", fontSize = 10.sp, color = Color.Gray)
                                            Text("🥘 $gravyKg Kg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Roti/Naan", fontSize = 10.sp, color = Color.Gray)
                                            Text("🫓 $rotiPcs Pcs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Category Filter Bar
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = cat == selectedCategory,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("detail_category_$cat")
                        )
                    }
                }
            }

            // Product Menu Items List
            items(filteredItems) { item ->
                ProductMenuItemCard(
                    item = item,
                    guestCount = if (isGuestCalculatorActive) eventGuestCount else null,
                    biryaniPersonsPerKg = bPerKg,
                    sweetPersonsPerKg = sPerKg,
                    gravyPersonsPerKg = gPerKg,
                    rotiPersonsPerUnit = rPerUnit,
                    onAddToCart = { qty ->
                        viewModel.addToCart(item, qty)
                    }
                )
            }
        }

        // Bottom View Cart Bar if items present
        if (cartItems.isNotEmpty()) {
            Surface(
                color = SaffronPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clickable { onOpenCart() }
                    .testTag("floating_view_cart_bar")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${cartItems.size} ITEMS IN CART", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberSecondary)
                        Text("₹${cartItems.sumOf { it.totalPrice }.toInt()} Total", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("View Cart", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProductMenuItemCard(
    item: MenuItemEntity,
    guestCount: Int? = null,
    biryaniPersonsPerKg: Double = 6.67,
    sweetPersonsPerKg: Double = 12.5,
    gravyPersonsPerKg: Double = 8.33,
    rotiPersonsPerUnit: Double = 0.5,
    onAddToCart: (Double) -> Unit
) {
    var quantity by remember { mutableDoubleStateOf(item.minQuantity) }
    val effectivePrice = item.getEffectivePrice()
    val hasDiscount = effectivePrice < item.pricePerUnit

    // Smart automatic quantity recommendation based on kitchen's serving capacities
    LaunchedEffect(guestCount, biryaniPersonsPerKg, sweetPersonsPerKg, gravyPersonsPerKg, rotiPersonsPerUnit) {
        if (guestCount != null && guestCount > 0) {
            val suggestedQty = when (item.unitType) {
                UnitType.KG -> {
                    when {
                        item.name.contains("Biryani", ignoreCase = true) || item.name.contains("Rice", ignoreCase = true) || item.name.contains("Pulao", ignoreCase = true) -> {
                            val kgNeeded = guestCount / biryaniPersonsPerKg.coerceAtLeast(0.1)
                            kotlin.math.round(kgNeeded * 2) / 2.0
                        }
                        item.name.contains("Sweet", ignoreCase = true) || item.name.contains("Halwa", ignoreCase = true) ||
                        item.name.contains("Gulab", ignoreCase = true) || item.name.contains("Meetha", ignoreCase = true) ||
                        item.name.contains("Kheer", ignoreCase = true) || item.name.contains("Phirni", ignoreCase = true) ||
                        item.name.contains("Jalebi", ignoreCase = true) || item.name.contains("Rasgulla", ignoreCase = true) -> {
                            val kgNeeded = guestCount / sweetPersonsPerKg.coerceAtLeast(0.1)
                            maxOf(1.0, kotlin.math.round(kgNeeded * 2) / 2.0)
                        }
                        item.name.contains("Gravy", ignoreCase = true) || item.name.contains("Korma", ignoreCase = true) ||
                        item.name.contains("Curry", ignoreCase = true) || item.name.contains("Dal", ignoreCase = true) ||
                        item.name.contains("Nihari", ignoreCase = true) || item.name.contains("Paneer", ignoreCase = true) -> {
                            val kgNeeded = guestCount / gravyPersonsPerKg.coerceAtLeast(0.1)
                            maxOf(1.0, kotlin.math.round(kgNeeded * 2) / 2.0)
                        }
                        else -> {
                            val kgNeeded = guestCount / biryaniPersonsPerKg.coerceAtLeast(0.1)
                            kotlin.math.round(kgNeeded * 2) / 2.0
                        }
                    }
                }
                UnitType.PORTION -> {
                    guestCount.toDouble()
                }
                UnitType.DOZEN -> {
                    val rotisNeeded = guestCount / rotiPersonsPerUnit.coerceAtLeast(0.1)
                    maxOf(1.0, kotlin.math.ceil(rotisNeeded / 12.0))
                }
                UnitType.LITRE -> {
                    val kgNeeded = guestCount / gravyPersonsPerKg.coerceAtLeast(0.1)
                    maxOf(1.0, kotlin.math.round(kgNeeded * 2) / 2.0)
                }
            }
            quantity = suggestedQty.coerceIn(item.minQuantity, item.maxQuantity)
        }
    }

    val unitLabel = when (item.unitType) {
        UnitType.KG -> "/ Kg"
        UnitType.DOZEN -> "/ Dozen"
        UnitType.LITRE -> "/ Litre"
        UnitType.PORTION -> "/ Portion"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("product_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image / Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = SaffronPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VegNonVegBadge(foodType = item.foodType)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.description,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))
                if (guestCount != null) {
                    val ratioDesc = when {
                        item.name.contains("Biryani", ignoreCase = true) || item.name.contains("Rice", ignoreCase = true) -> "1 Kg = ${String.format("%.1f", biryaniPersonsPerKg)} लोग"
                        item.name.contains("Sweet", ignoreCase = true) || item.name.contains("Halwa", ignoreCase = true) || item.name.contains("Meetha", ignoreCase = true) -> "1 Kg = ${String.format("%.1f", sweetPersonsPerKg)} लोग"
                        item.name.contains("Gravy", ignoreCase = true) || item.name.contains("Korma", ignoreCase = true) || item.name.contains("Curry", ignoreCase = true) -> "1 Kg = ${String.format("%.1f", gravyPersonsPerKg)} लोग"
                        else -> "1 Kg = ${String.format("%.1f", biryaniPersonsPerKg)} लोग"
                    }
                    Surface(
                        color = Color(0xFFFFF7ED),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(0.8.dp, Color(0xFFFED7AA)),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Text(
                            text = "⚡ $guestCount Guests: ${if (quantity % 1.0 == 0.0) quantity.toInt() else String.format("%.1f", quantity)} ${item.unitType.name.lowercase()} (किचन मानक: $ratioDesc)",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC2410C),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${effectivePrice.toInt()} ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                    if (hasDiscount) {
                        Text(
                            text = "₹${item.pricePerUnit.toInt()} ",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            style = TextStyle(textDecoration = TextDecoration.LineThrough)
                        )
                    }
                    Text(
                        text = unitLabel,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    if (hasDiscount) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (item.catererDiscountType == com.example.data.models.DiscountType.PERCENTAGE) "${item.catererDiscountValue.toInt()}% OFF" else "₹${item.catererDiscountValue.toInt()} OFF",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = VegGreen,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UnitQuantityPicker(
                        quantity = quantity,
                        unitType = item.unitType,
                        minQty = item.minQuantity,
                        maxQty = item.maxQuantity,
                        step = item.stepQuantity,
                        onQuantityChange = { quantity = it }
                    )

                    Button(
                        onClick = { onAddToCart(quantity) },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("add_to_cart_${item.id}")
                    ) {
                        Text("Add to Cart", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
