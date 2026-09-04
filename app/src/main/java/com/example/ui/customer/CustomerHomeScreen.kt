package com.example.ui.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.CatererEntity
import com.example.data.repository.CaterersViewModel
import com.example.ui.common.FssaiBadge
import com.example.ui.common.RatingBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary

@Composable
fun CustomerHomeScreen(
    viewModel: CaterersViewModel,
    onSelectCaterer: (String) -> Unit,
    onOpenCart: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSupport: () -> Unit,
    onTrackOrder: (String) -> Unit = {},
    onRateKitchen: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val caterers by viewModel.filteredCaterers.collectAsState()
    val favoriteCaterers by viewModel.favoriteCaterersList.collectAsState()
    val favoriteKitchenIds by viewModel.favoriteKitchenIds.collectAsState()
    val orders by viewModel.ordersList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val deliveryLocation by viewModel.deliveryLocation.collectAsState()
    val cartItems by viewModel.cartItemsList.collectAsState()

    val activeOrder = orders.find { it.orderStatus != com.example.data.models.OrderStatus.DELIVERED && it.orderStatus != com.example.data.models.OrderStatus.CANCELLED }

    val categories = listOf(
        "All", "❤️ Favorites", "Veg Biryani", "Chicken Biryani", "Mutton Biryani",
        "Chicken Gravy", "Mutton Gravy", "Veg Gravy", "Chinese", "Rice", "Desserts", "Beverages"
    )
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showEnterStoreCodeDialog by remember { mutableStateOf(false) }
    var inputStoreCode by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0)),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Header / Top Bar
        item {
            Surface(
                color = SaffronPrimary,
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Logo
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(AmberSecondary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = "Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Caterers Wale",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = AmberSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = deliveryLocation,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onOpenOrders, modifier = Modifier.testTag("nav_orders")) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "My Orders",
                                    tint = Color.White
                                )
                            }

                            IconButton(onClick = onOpenCart, modifier = Modifier.testTag("nav_cart")) {
                                BadgedBox(
                                    badge = {
                                        if (cartItems.isNotEmpty()) {
                                            Badge { Text(cartItems.size.toString()) }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = "Cart",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search caterer, biryani, gravy, wedding menu...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SaffronPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("search_bar"),
                        shape = RoundedCornerShape(26.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AmberSecondary,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // Direct Caterer Store Link / Code Pill
        item {
            Surface(
                color = Color(0xFFFFF7ED),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AmberSecondary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { showEnterStoreCodeDialog = true }
                    .testTag("caterer_store_link_pill")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Link, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Have a Caterer's Store Link / Code? (डायरेक्ट लिंक)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF9A3412)
                        )
                    }
                    Text("Enter 👉", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                }
            }
        }

        // Active Order Live Tracking Banner (If active order exists)
        if (activeOrder != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onTrackOrder(activeOrder.orderId) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(SaffronPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Fastfood, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Order #${activeOrder.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = AmberSecondary, shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = activeOrder.orderStatus.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${activeOrder.catererName} • Slot: ${activeOrder.deliveryTimeSlot}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Button(
                            onClick = { onTrackOrder(activeOrder.orderId) },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("home_track_live_order_btn")
                        ) {
                            Text("Track Live", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Delivered Order Feedback Banner (If a recently delivered order exists)
        val latestDeliveredOrder = orders.firstOrNull { it.orderStatus == com.example.data.models.OrderStatus.DELIVERED }
        if (latestDeliveredOrder != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { onRateKitchen(latestDeliveredOrder.orderId) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFFEF3C7), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("How was the feast from ${latestDeliveredOrder.catererName}?", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFF92400E))
                                Text("Rate your experience & earn +50 Foodie Coins ⭐", fontSize = 11.sp, color = Color(0xFFB45309))
                            }
                        }

                        Button(
                            onClick = { onRateKitchen(latestDeliveredOrder.orderId) },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("home_rate_feast_btn")
                        ) {
                            Text("Rate Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner_1784989627411),
                        contentDescription = "Hero Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.88f),
                                        Color.Black.copy(alpha = 0.35f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = AmberSecondary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "50% ADVANCE TOKEN BOOKING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Royal Bulk Catering\n& Authentic Sealed Deghs",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 23.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pay 50% today • Rest 50% upon delivery at your event",
                            fontSize = 11.sp,
                            color = Color(0xFFFFD54F)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val targetId = caterers.firstOrNull()?.id ?: "caterer_1"
                                onSelectCaterer(targetId)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("order_now_hero")
                        ) {
                            Text("Explore Caterers Menu", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // 2.5 Trust & Guarantee Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🛡️ FSSAI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text("Verified Kitchens", fontSize = 9.5.sp, color = Color.Gray)
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔒 50% Token", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        Text("Pay Rest on Day", fontSize = 9.5.sp, color = Color.Gray)
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("👨‍🍳 Servers", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text("Staff Add-ons", fontSize = 9.5.sp, color = Color.Gray)
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🍲 Sealed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text("Hot Degh Van", fontSize = 9.5.sp, color = Color.Gray)
                    }
                }
            }
        }

        // 2.7 Catering Add-On Services Highlight Banner (Requested by user)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                border = BorderStroke(1.dp, Color(0xFFFDE68A))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✨ Event Add-On Services (अतिरिक्त सेवाएं)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color(0xFF92400E))
                        }
                        Surface(color = AmberSecondary, shape = RoundedCornerShape(6.dp)) {
                            Text("In Menu Cart", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, Color(0xFFFCD34D)),
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("👨‍🍳 Biryani Server", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                Text("₹500/staff", fontSize = 9.sp, color = SaffronPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, Color(0xFFFCD34D)),
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🥗 Kachumar & Salan", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                Text("₹150/pack", fontSize = 9.sp, color = SaffronPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, Color(0xFFFCD34D)),
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🍽️ Plates & Tissue", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                Text("₹350 (50 pax)", fontSize = 9.sp, color = SaffronPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, Color(0xFFFCD34D)),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🍬 Shahi Mukhwas", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                Text("₹200/kit", fontSize = 9.sp, color = SaffronPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // 3. Categories Horizontal Carousel
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "Explore Menu Categories",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D1515),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedCategory(category) },
                            label = {
                                Text(
                                    text = category,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("category_$category")
                        )
                    }
                }
            }
        }

        // 3.5 Featured Favorites Section (If favorites exist and category is All)
        if (favoriteCaterers.isNotEmpty() && selectedCategory == "All") {
            item {
                Column(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "❤️ My Favorite Kitchens",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D1515)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${favoriteCaterers.size} Saved",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        TextButton(
                            onClick = { viewModel.setSelectedCategory("❤️ Favorites") },
                            modifier = Modifier.testTag("view_all_favorites_btn")
                        ) {
                            Text("View All", fontSize = 12.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(favoriteCaterers) { fav ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable { onSelectCaterer(fav.id) }
                                    .testTag("fav_carousel_card_${fav.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RatingBadge(rating = fav.rating, reviewCount = fav.reviewCount)
                                        IconButton(
                                            onClick = { viewModel.toggleFavoriteKitchen(fav) },
                                            modifier = Modifier
                                                .size(28.dp)
                                                .testTag("fav_carousel_heart_${fav.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Favorite,
                                                contentDescription = "Unfavorite",
                                                tint = Color(0xFFE53935),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = fav.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF212121),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "📍 ${fav.address}",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("⏱️ ${fav.deliveryTimeMinutes}m", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Button(
                                            onClick = { onSelectCaterer(fav.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Menu", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Popular Caterers Near You
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (selectedCategory.contains("Favorite", ignoreCase = true)) "My Favorite Caterers" else "Popular Caterers Near You",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D1515)
                    )
                    Text(
                        text = if (selectedCategory.contains("Favorite", ignoreCase = true)) "Quick 1-tap bulk catering from your heart-marked kitchens" else "FSSAI verified bulk catering kitchens",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        if (caterers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("empty_caterers_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (selectedCategory.contains("Favorite", ignoreCase = true)) Icons.Default.FavoriteBorder else Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = if (selectedCategory.contains("Favorite", ignoreCase = true)) Color(0xFFE53935) else Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedCategory.contains("Favorite", ignoreCase = true)) "No Favorite Kitchens Yet" else "No Caterers Found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (selectedCategory.contains("Favorite", ignoreCase = true))
                                "Tap the ❤️ heart icon on any catering partner card to save them to your Firestore profile for quick ordering."
                            else "Try searching for a different dish, kitchen name, or clear your filters.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.setSelectedCategory("All") },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Explore All Caterers", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(caterers) { caterer ->
                val isFav = favoriteKitchenIds.contains(caterer.id)
                CatererCard(
                    caterer = caterer,
                    isFavorite = isFav,
                    onToggleFavorite = { viewModel.toggleFavoriteKitchen(caterer) },
                    onViewMenu = { onSelectCaterer(caterer.id) }
                )
            }
        }

        // 15. Help & Footer Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                color = Color(0xFF1F1111),
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = AmberSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Caterers Wale",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "India's #1 Dedicated Bulk Catering Marketplace",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = onOpenSupport) { Text("Help & Support", color = AmberSecondary) }
                        TextButton(onClick = onOpenProfile) { Text("My Profile", color = AmberSecondary) }
                        TextButton(onClick = onOpenOrders) { Text("Order History", color = AmberSecondary) }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Copyright © 2026 Caterers Wale. All Rights Reserved.\nFSSAI Approved Partners Only",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }

    if (showEnterStoreCodeDialog) {
        AlertDialog(
            onDismissRequest = { showEnterStoreCodeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enter Caterer Store Link or Code", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Aapke caterer ne jo WhatsApp ya SMS par link bheja hai, use yahan paste karein ya code dalein:", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputStoreCode,
                        onValueChange = { inputStoreCode = it },
                        placeholder = { Text("e.g. caterer_1 or paste store link", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Registered Kitchens (Tap to open directly):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    caterers.take(3).forEach { cat ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    showEnterStoreCodeDialog = false
                                    onSelectCaterer(cat.id)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                Text(cat.id, fontSize = 11.sp, color = SaffronPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cleanedCode = inputStoreCode.trim().substringAfterLast("/").trim()
                        val matchedCaterer = caterers.find { it.id.equals(cleanedCode, ignoreCase = true) || it.name.contains(cleanedCode, ignoreCase = true) }
                        showEnterStoreCodeDialog = false
                        val targetId = matchedCaterer?.id ?: caterers.firstOrNull()?.id ?: "caterer_1"
                        onSelectCaterer(targetId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Open Store 🛒")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEnterStoreCodeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CatererCard(
    caterer: CatererEntity,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onViewMenu: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onViewMenu() }
            .testTag("caterer_card_${caterer.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo placeholder or image
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(14.dp))
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
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = caterer.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B),
                                    maxLines = 1
                                )
                            }
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "👑 Verified Caterer Partner",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RatingBadge(rating = caterer.rating, reviewCount = caterer.reviewCount)
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = onToggleFavorite,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("favorite_btn_${caterer.id}")
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                    tint = if (isFavorite) Color(0xFFE53935) else Color(0xFF9E9E9E),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "📍 ${caterer.address}",
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (caterer.isFssaiVerified) {
                        FssaiBadge(licenseNo = caterer.fssaiLicense)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: Staff available & 50% advance accepted
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "👨‍🍳 Staff Add-ons Available",
                        fontSize = 10.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                    )
                }

                Surface(
                    color = Color(0xFFFFF7ED),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🔒 50% Advance Booking",
                        fontSize = 10.sp,
                        color = SaffronPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details and Action row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF8F0), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Prep Time", fontSize = 10.sp, color = Color.Gray)
                    Text("${caterer.deliveryTimeMinutes} mins", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Min Bulk Order", fontSize = 10.sp, color = Color.Gray)
                    Text("₹${caterer.minOrderAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Distance", fontSize = 10.sp, color = Color.Gray)
                    Text("${caterer.distanceKm} km", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onViewMenu,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("view_menu_button_${caterer.id}")
                ) {
                    Text("Explore Menu", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
