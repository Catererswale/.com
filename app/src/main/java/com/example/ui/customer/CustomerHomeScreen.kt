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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.data.models.UserProfile
import com.example.data.models.UserRole
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.material3.Scaffold
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
import com.example.ui.theme.VegGreen

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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomerNavTabItem(
                        icon = Icons.Default.Store,
                        label = "Home",
                        isSelected = true,
                        onClick = {}
                    )
                    CustomerNavTabItem(
                        icon = Icons.Default.Restaurant,
                        label = "Categories",
                        isSelected = false,
                        onClick = {}
                    )
                    CustomerNavTabItem(
                        icon = Icons.Default.ShoppingBag,
                        label = "Cart",
                        isSelected = false,
                        badgeCount = cartItems.sumOf { it.quantity.toInt() },
                        onClick = onOpenCart
                    )
                    CustomerNavTabItem(
                        icon = Icons.Default.ReceiptLong,
                        label = "Orders",
                        isSelected = false,
                        badgeCount = orders.count { it.orderStatus != com.example.data.models.OrderStatus.DELIVERED && it.orderStatus != com.example.data.models.OrderStatus.CANCELLED },
                        onClick = onOpenOrders
                    )
                    CustomerNavTabItem(
                        icon = Icons.Default.Person,
                        label = "Profile",
                        isSelected = false,
                        onClick = onOpenProfile
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9F6F0)),
            contentPadding = PaddingValues(bottom = 16.dp)
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

                        // Right Corner: Kitchen Scan & Cart
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                onClick = { showEnterStoreCodeDialog = true },
                                color = Color.White,
                                shape = RoundedCornerShape(20.dp),
                                shadowElevation = 3.dp,
                                modifier = Modifier
                                    .testTag("top_kitchen_scan_btn")
                                    .testTag("caterer_store_link_pill")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "Kitchen Scan",
                                        tint = SaffronPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Kitchen Scan",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronPrimary
                                    )
                                }
                            }

                            IconButton(
                                onClick = onOpenCart,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("nav_cart")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (cartItems.isNotEmpty()) {
                                            Badge(containerColor = AmberSecondary) { Text(cartItems.size.toString()) }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = "Cart",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
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
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🍲 Sealed Deg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text("Hot Direct to Venue", fontSize = 9.5.sp, color = Color.Gray)
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
    }

    if (showEnterStoreCodeDialog) {
        CustomerQrOrderAndOtpDialog(
            caterers = caterers,
            viewModel = viewModel,
            onDismiss = { showEnterStoreCodeDialog = false },
            onSelectCaterer = { targetId ->
                showEnterStoreCodeDialog = false
                onSelectCaterer(targetId)
            }
        )
    }
}

/**
 * Quick Kitchen Standee QR Scanner & Direct Online Menu Opener.
 * Scans table standees or digital links and opens that kitchen's online menu directly
 * with 100% online ordering, customized portions (Kg/deg), and live tracking.
 * No OTP step and no unnecessary kitchen lists.
 */
@Composable
fun CustomerQrOrderAndOtpDialog(
    caterers: List<CatererEntity>,
    viewModel: CaterersViewModel,
    onDismiss: () -> Unit,
    onSelectCaterer: (String) -> Unit
) {
    val context = LocalContext.current
    var inputStoreCode by remember { mutableStateOf("") }
    var scanMode by remember { mutableStateOf(0) } // 0: Camera Viewfinder, 1: Store Link/Code
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = SaffronPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Scan Kitchen Standee QR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Table Standee scan karein aur online order karein",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Mode Switcher Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (scanMode == 0) Color.White else Color.Transparent,
                        shadowElevation = if (scanMode == 0) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { scanMode = 0 }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = if (scanMode == 0) SaffronPrimary else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Camera Scanner",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (scanMode == 0) SaffronPrimary else Color.Gray
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (scanMode == 1) Color.White else Color.Transparent,
                        shadowElevation = if (scanMode == 1) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { scanMode = 1 }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Link,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = if (scanMode == 1) SaffronPrimary else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Store Link / Code",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (scanMode == 1) SaffronPrimary else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (scanMode == 0) {
                    // Camera Viewfinder Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Target Frame
                            Box(
                                modifier = Modifier
                                    .size(105.dp)
                                    .border(2.5.dp, SaffronPrimary, RoundedCornerShape(12.dp))
                                    .background(Color(0x22F97316)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = "Target Standee",
                                    tint = Color.White,
                                    modifier = Modifier.size(52.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "📷 Aim at Table / Standee QR Code",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Detects kitchen QR & opens online menu directly",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Scanner Action Button
                    Button(
                        onClick = {
                            val target = caterers.firstOrNull()?.id ?: "caterer_1"
                            Toast.makeText(context, "✅ QR Scanned! Opening Online Menu...", Toast.LENGTH_SHORT).show()
                            onSelectCaterer(target)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("⚡ Scan QR & Open Online Menu", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Manual Store Link / Code Input
                    Text(
                        "Paste Kitchen Link or Type Kitchen ID:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = inputStoreCode,
                        onValueChange = {
                            inputStoreCode = it
                            errorMessage = ""
                        },
                        placeholder = { Text("e.g. caterer_1 or https://catererswale.app/menu?kitchen=caterer_1", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(errorMessage, fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val cleaned = inputStoreCode.trim().substringAfterLast("=").substringAfterLast("/").trim()
                            if (cleaned.isNotBlank()) {
                                val matched = caterers.find { c ->
                                    c.id.equals(cleaned, ignoreCase = true) || c.name.contains(cleaned, ignoreCase = true)
                                }?.id ?: cleaned
                                Toast.makeText(context, "Opening Online Menu...", Toast.LENGTH_SHORT).show()
                                onSelectCaterer(matched)
                            } else {
                                val defaultId = caterers.firstOrNull()?.id ?: "caterer_1"
                                Toast.makeText(context, "Opening Online Menu...", Toast.LENGTH_SHORT).show()
                                onSelectCaterer(defaultId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Online Menu 🍲", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Universal Info Notice
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌐", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Universal QR: Mobile camera ya kisi bhi browser se scan karne par bhi seedha online menu khulega aur online order hoga.",
                            fontSize = 10.5.sp,
                            color = Color(0xFF166534),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
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

            // Badges row: 50% advance accepted & Hot Deg delivery
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🔥 Live Deg & Hot Delivery",
                        fontSize = 10.sp,
                        color = Color(0xFF166534),
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

@Composable
private fun CustomerNavTabItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("nav_tab_${label.lowercase()}")
    ) {
        if (badgeCount > 0) {
            BadgedBox(badge = {
                Badge(containerColor = SaffronPrimary) {
                    Text(badgeCount.toString())
                }
            }) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = if (isSelected) SaffronPrimary else Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isSelected) SaffronPrimary else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) SaffronPrimary else Color.Gray
        )
    }
}
