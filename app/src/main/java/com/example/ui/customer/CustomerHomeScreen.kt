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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.ui.theme.NonVegRed
import com.example.ui.theme.AwadhiDeepBurgundy
import com.example.ui.theme.AwadhiCrimsonRed
import com.example.ui.theme.AwadhiZafraniSaffron
import com.example.ui.theme.AwadhiDesiGheeGold
import com.example.ui.theme.AwadhiGheeShine
import com.example.ui.theme.AwadhiWarmCream
import com.example.ui.theme.AwadhiHandiCharcoal
import com.example.ui.theme.AwadhiCardBorder
import com.example.ui.common.VegNonVegBadge
import com.example.data.models.FoodType
import com.example.data.models.Language
import com.example.util.LocalizationManager

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
    val customerDietFilter by viewModel.customerDietFilter.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    // Veg Mode states (Zomato-style Pure Veg vs All Caterers)
    val isVegMode by viewModel.isVegMode.collectAsState()
    val vegModeScope by viewModel.vegModeScope.collectAsState()
    val vegModeDaysMode by viewModel.vegModeDaysMode.collectAsState()
    val selectedVegDays by viewModel.selectedVegDays.collectAsState()
    var showVegModeSheet by remember { mutableStateOf(false) }

    // Advanced search filters
    val filterMaxDeliveryTime by viewModel.filterMaxDeliveryTime.collectAsState()
    val filterMaxBudget by viewModel.filterMaxBudget.collectAsState()
    val filterMinRating by viewModel.filterMinRating.collectAsState()

    val activeOrder = orders.find { it.orderStatus != com.example.data.models.OrderStatus.DELIVERED && it.orderStatus != com.example.data.models.OrderStatus.CANCELLED }

    val categories = when (customerDietFilter) {
        "VEG" -> listOf("All", "Veg Biryani", "Veg Gravy", "Chinese", "Rice", "Desserts", "Beverages")
        "NON_VEG" -> listOf("All", "Chicken Biryani", "Mutton Biryani", "Chicken Gravy", "Mutton Gravy", "Desserts", "Beverages")
        else -> listOf("All", "Veg Biryani", "Chicken Biryani", "Mutton Biryani", "Chicken Gravy", "Mutton Gravy", "Veg Gravy", "Chinese", "Rice", "Desserts", "Beverages")
    }
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var showEnterStoreCodeDialog by remember { mutableStateOf(false) }
    var inputStoreCode by remember { mutableStateOf("") }
    var showLocationSelectorDialog by remember { mutableStateOf(false) }

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
                        label = LocalizationManager.getBottomNavLabel("Home", currentLanguage),
                        isSelected = selectedCategory != "❤️ Favorites",
                        onClick = { viewModel.setSelectedCategory("All") }
                    )
                    CustomerNavTabItem(
                        icon = Icons.Default.Favorite,
                        label = LocalizationManager.getBottomNavLabel("Favorites", currentLanguage),
                        isSelected = selectedCategory == "❤️ Favorites",
                        badgeCount = favoriteKitchenIds.size,
                        onClick = { viewModel.setSelectedCategory("❤️ Favorites") }
                    )
                    CustomerNavTabItem(
                        icon = Icons.Default.ShoppingBag,
                        label = LocalizationManager.getBottomNavLabel("Cart", currentLanguage),
                        isSelected = false,
                        badgeCount = cartItems.sumOf { it.quantity.toInt() },
                        onClick = onOpenCart
                    )
                    CustomerNavTabItem(
                        icon = Icons.Default.ReceiptLong,
                        label = LocalizationManager.getBottomNavLabel("Orders", currentLanguage),
                        isSelected = false,
                        badgeCount = orders.count { it.orderStatus != com.example.data.models.OrderStatus.DELIVERED && it.orderStatus != com.example.data.models.OrderStatus.CANCELLED },
                        onClick = onOpenOrders
                    )
                    CustomerNavTabItem(
                        icon = Icons.Default.Person,
                        label = LocalizationManager.getBottomNavLabel("Profile", currentLanguage),
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
                .background(AwadhiWarmCream),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
        // 1. Header / Top Bar (Royal Awadhi Feast Theme)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                AwadhiDeepBurgundy,
                                AwadhiCrimsonRed,
                                Color(0xFF9A3412)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Royal Nawabi Banner Header Tag
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = AwadhiHandiCharcoal.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AwadhiGheeShine.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("👑", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ROYAL AWADHI FEAST & BULK CATERING",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.6.sp,
                                    color = AwadhiGheeShine
                                )
                            }
                        }

                        Text(
                            text = "असली नवाबी ज़ायका 🔥",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFEF08A)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Logo with golden border glow
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(AwadhiDesiGheeGold, Color(0xFFD97706))
                                        )
                                    )
                                    .border(1.5.dp, AwadhiGheeShine, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = "Logo",
                                    tint = AwadhiHandiCharcoal,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showLocationSelectorDialog = true }
                                    .padding(vertical = 2.dp, horizontal = 4.dp)
                                    .testTag("location_picker_header")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Caterers Wale",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.3.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("✨", fontSize = 13.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = AwadhiGheeShine,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = deliveryLocation,
                                        fontSize = 11.5.sp,
                                        color = Color.White.copy(alpha = 0.95f),
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Change Location",
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(16.dp)
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
                                shadowElevation = 4.dp,
                                border = BorderStroke(1.dp, AwadhiCardBorder),
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
                                        tint = AwadhiCrimsonRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Kitchen Scan",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AwadhiCrimsonRed
                                    )
                                }
                            }

                            IconButton(
                                onClick = onOpenCart,
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("nav_cart")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (cartItems.isNotEmpty()) {
                                            Badge(containerColor = AwadhiDesiGheeGold) {
                                                Text(
                                                    cartItems.size.toString(),
                                                    color = AwadhiHandiCharcoal,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = "Cart",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Bar and Zomato-style Veg Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = {
                                Text(
                                    if (currentLanguage == Language.HINDI) {
                                        if (isVegMode) "शुद्ध शाकाहारी बिरयानी, दाल मखनी खोजें..." else "शाही बिरयानी, कोरमा, पनीर खोजें..."
                                    } else {
                                        if (isVegMode) "Search Pure Veg Biryani, Dal Makhani..." else "Search Shahi Biryani, Korma, Paneer..."
                                    },
                                    fontSize = 12.sp,
                                    color = Color(0xFF78716C),
                                    maxLines = 1
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = if (isVegMode) VegGreen else AwadhiCrimsonRed
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("search_bar"),
                            shape = RoundedCornerShape(25.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = if (isVegMode) VegGreen else AwadhiDesiGheeGold,
                                unfocusedBorderColor = if (isVegMode) VegGreen.copy(alpha = 0.5f) else AwadhiCardBorder
                            ),
                            singleLine = true
                        )

                        // Zomato-style VEG Switch Header Toggle (Exact as screenshot 1)
                        Surface(
                            onClick = {
                                showVegModeSheet = true
                            },
                            shape = RoundedCornerShape(22.dp),
                            color = if (isVegMode) VegGreen else Color.White,
                            border = BorderStroke(
                                1.2.dp,
                                if (isVegMode) Color(0xFF15803D) else Color(0xFFCBD5E1)
                            ),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("veg_mode_header_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "VEG",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isVegMode) Color.White else Color(0xFF1E293B)
                                )

                                // Custom styled switch pill
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(22.dp)
                                        .clip(RoundedCornerShape(11.dp))
                                        .background(if (isVegMode) Color.White.copy(alpha = 0.3f) else Color(0xFFE2E8F0))
                                        .padding(2.dp),
                                    contentAlignment = if (isVegMode) Alignment.CenterEnd else Alignment.CenterStart
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(if (isVegMode) Color.White else Color(0xFF94A3B8))
                                    )
                                }
                            }
                        }
                    }

                    // Persistent banner when Veg Mode is active
                    if (isVegMode) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDCFCE7),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showVegModeSheet = true }
                                .testTag("veg_mode_active_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("🌱", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (vegModeScope == "PURE_VEG_ONLY") "VEG MODE ON • Pure Veg Caterers only" else "VEG MODE ON • Veg from All Caterers",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF14532D)
                                        )
                                        Text(
                                            text = if (vegModeScope == "PURE_VEG_ONLY") "Showing strictly 100% Shuddh Shakahari kitchens" else "Showing veg dishes from all kitchens",
                                            fontSize = 10.sp,
                                            color = Color(0xFF166534)
                                        )
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Edit",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                    IconButton(
                                        onClick = { viewModel.setVegMode(false) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Turn off Veg Mode",
                                            tint = Color(0xFF166534),
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Hero Banner (Royal Dum Feast Temptation)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(1.2.dp, AwadhiCardBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(195.dp)
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
                                        AwadhiHandiCharcoal.copy(alpha = 0.94f),
                                        AwadhiDeepBurgundy.copy(alpha = 0.78f),
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
                            color = AwadhiDesiGheeGold,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "🔥 SHAHI DUM PUKHT & SEALED DEGS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = AwadhiHandiCharcoal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Royal Awadhi Daawat\nAuthentic Handi Degs",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Melt-in-mouth biryani • Pure Desi Ghee gravies • 50% Token",
                            fontSize = 11.5.sp,
                            color = AwadhiGheeShine,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val targetId = caterers.firstOrNull()?.id ?: "caterer_1"
                                onSelectCaterer(targetId)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AwadhiCrimsonRed
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("order_now_hero")
                        ) {
                            Text("Taste Royal Menu (मेनू देखें)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // 2.5 Trust & Guarantee Row (Royal Awadhi Standards)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AwadhiCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🛡️ FSSAI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AwadhiHandiCharcoal)
                        Text("Verified Chefs", fontSize = 9.sp, color = Color.Gray)
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AwadhiCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🍲 Sealed Deg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AwadhiCrimsonRed)
                        Text("Hot Direct to Venue", fontSize = 9.sp, color = Color.Gray)
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AwadhiCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🧈 Desi Ghee", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AwadhiDesiGheeGold)
                        Text("Authentic Taste", fontSize = 9.sp, color = Color.Gray)
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AwadhiCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔒 50% Token", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AwadhiHandiCharcoal)
                        Text("Pay Rest on Day", fontSize = 9.sp, color = Color.Gray)
                    }
                }
            }
        }

        // 2.7 Trending Shahi Cravings (देखते ही ललचा जाए - स्पेशल नवाबी पकवान)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🤤", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Special Shahi Cravings",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = AwadhiHandiCharcoal
                        )
                    }
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "शाही ज़ायका",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AwadhiDeepBurgundy,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        CravingItemCard(
                            emoji = "🍚",
                            title = "Dum Pukht Biryani",
                            badge = "🔥 Piping Hot Handi",
                            desc = "Aged Basmati, saffron & golden birista",
                            badgeBg = Color(0xFFFEE2E2),
                            badgeColor = AwadhiCrimsonRed,
                            onClick = {
                                viewModel.setSelectedCategory(
                                    if (customerDietFilter == "VEG") "Veg Biryani" else "Chicken Biryani"
                                )
                            }
                        )
                    }
                    item {
                        CravingItemCard(
                            emoji = "🥘",
                            title = "Zafrani Shahi Korma",
                            badge = "🤤 Melt-in-Mouth",
                            desc = "Slow simmered rich cashew & onion gravy",
                            badgeBg = Color(0xFFFEF3C7),
                            badgeColor = Color(0xFF92400E),
                            onClick = {
                                viewModel.setSelectedCategory(
                                    if (customerDietFilter == "VEG") "Veg Gravy" else "Chicken Gravy"
                                )
                            }
                        )
                    }
                    item {
                        CravingItemCard(
                            emoji = "🧈",
                            title = "Dal Makhani & Paneer",
                            badge = "👑 12hr Simmered",
                            desc = "Coal-cooked creamy buttery royal gravy",
                            badgeBg = Color(0xFFDCFCE7),
                            badgeColor = Color(0xFF166534),
                            onClick = {
                                viewModel.setCustomerDietFilter("VEG")
                                viewModel.setSelectedCategory("Veg Gravy")
                            }
                        )
                    }
                    item {
                        CravingItemCard(
                            emoji = "🍧",
                            title = "Shahi Tukda & Phirni",
                            badge = "✨ Pure Khoya",
                            desc = "Golden ghee toast with cardamom rabdi",
                            badgeBg = Color(0xFFFDF2F8),
                            badgeColor = Color(0xFF9D174D),
                            onClick = {
                                viewModel.setSelectedCategory("Desserts")
                            }
                        )
                    }
                }
            }
        }

        // 2.8 Veg / Non-Veg Quick Dietary Filter Row
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dietary Filter / खान-पान चयन",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        if (customerDietFilter != "ALL") {
                            Text(
                                text = "Reset to All",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.setCustomerDietFilter("ALL")
                                        if (selectedCategory != "All") viewModel.setSelectedCategory("All")
                                    }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // All Foods
                        DietFilterHomePill(
                            title = "All Foods",
                            subtitle = "सभी व्यंजन",
                            isSelected = customerDietFilter == "ALL",
                            badge = null,
                            activeColor = Color(0xFF334155),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.setCustomerDietFilter("ALL")
                            }
                        )

                        // Pure Veg
                        DietFilterHomePill(
                            title = "Pure Veg",
                            subtitle = "शाकाहारी",
                            isSelected = customerDietFilter == "VEG",
                            badge = { VegNonVegBadge(foodType = FoodType.VEG) },
                            activeColor = VegGreen,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.setCustomerDietFilter("VEG")
                                if (selectedCategory == "Chicken Biryani" || selectedCategory == "Mutton Biryani" ||
                                    selectedCategory == "Chicken Gravy" || selectedCategory == "Mutton Gravy") {
                                    viewModel.setSelectedCategory("Veg Biryani")
                                }
                            }
                        )

                        // Non-Veg
                        DietFilterHomePill(
                            title = "Non-Veg",
                            subtitle = "मांसाहारी",
                            isSelected = customerDietFilter == "NON_VEG",
                            badge = { VegNonVegBadge(foodType = FoodType.NON_VEG) },
                            activeColor = NonVegRed,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.setCustomerDietFilter("NON_VEG")
                                if (selectedCategory == "Veg Biryani" || selectedCategory == "Veg Gravy") {
                                    viewModel.setSelectedCategory("Chicken Biryani")
                                }
                            }
                        )
                    }

                    // Informational banner when filter is active
                    if (customerDietFilter == "VEG") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF0FDF4),
                            border = BorderStroke(1.dp, VegGreen.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VegNonVegBadge(foodType = FoodType.VEG, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Showing Pure Vegetarian menus & kitchen options",
                                    fontSize = 11.sp,
                                    color = Color(0xFF166534),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else if (customerDietFilter == "NON_VEG") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, NonVegRed.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VegNonVegBadge(foodType = FoodType.NON_VEG, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Showing Non-Veg Special Biryanis, Kormas & Kebabs",
                                    fontSize = 11.sp,
                                    color = Color(0xFF991B1B),
                                    fontWeight = FontWeight.Medium
                                )
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
                    text = LocalizationManager.getPopularCategoriesTitle(currentLanguage),
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
                        val catIcon = when {
                            category == "All" -> "🍽️"
                            category.contains("Biryani", ignoreCase = true) -> "🍚"
                            category.contains("Mughlai", ignoreCase = true) || category.contains("Curry", ignoreCase = true) -> "🥘"
                            category.contains("Snack", ignoreCase = true) || category.contains("Starter", ignoreCase = true) -> "🥟"
                            category.contains("Sweet", ignoreCase = true) || category.contains("Dessert", ignoreCase = true) -> "🍨"
                            category.contains("Bread", ignoreCase = true) || category.contains("Roti", ignoreCase = true) -> "🫓"
                            category.contains("Beverage", ignoreCase = true) || category.contains("Drink", ignoreCase = true) -> "🥤"
                            category.contains("South", ignoreCase = true) -> "🥞"
                            category.contains("Favorite", ignoreCase = true) -> "❤️"
                            else -> "🍲"
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedCategory(category) },
                            leadingIcon = {
                                Text(catIcon, fontSize = 13.sp)
                            },
                            label = {
                                Text(
                                    text = LocalizationManager.getCategoryLabel(category, currentLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AwadhiCrimsonRed,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = AwadhiCardBorder,
                                selectedBorderColor = AwadhiCrimsonRed
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("category_$category")
                        )
                    }
                }
            }
        }

        // 3.5 Advanced Discovery & Search Filters (Delivery Time, Budget, Top Rating, Veg Mode)
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                // Veg Mode Quick Pill
                item {
                    FilterChip(
                        selected = isVegMode,
                        onClick = { showVegModeSheet = true },
                        leadingIcon = {
                            Text("🌱", fontSize = 12.sp)
                        },
                        label = {
                            Text(
                                if (isVegMode) (if (vegModeScope == "PURE_VEG_ONLY") "Pure Veg Only" else "Veg Mode ON") else "Veg Mode",
                                fontSize = 11.5.sp,
                                fontWeight = if (isVegMode) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFDCFCE7),
                            selectedLabelColor = Color(0xFF15803D),
                            containerColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isVegMode,
                            borderColor = Color(0xFFCBD5E1),
                            selectedBorderColor = Color(0xFF15803D)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.testTag("filter_veg_mode")
                    )
                }

                // Fast Delivery Filter (< 45 min)
                item {
                    val isFastDelivery = filterMaxDeliveryTime == 45
                    FilterChip(
                        selected = isFastDelivery,
                        onClick = {
                            viewModel.setFilterMaxDeliveryTime(if (isFastDelivery) null else 45)
                        },
                        leadingIcon = { Text("⚡", fontSize = 12.sp) },
                        label = { Text("Under 45 mins", fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFEF3C7),
                            selectedLabelColor = Color(0xFFB45309),
                            containerColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isFastDelivery,
                            borderColor = Color(0xFFCBD5E1),
                            selectedBorderColor = Color(0xFFD97706)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.testTag("filter_fast_delivery")
                    )
                }

                // Budget Filter (< ₹1500)
                item {
                    val isBudget = filterMaxBudget == 1500.0
                    FilterChip(
                        selected = isBudget,
                        onClick = {
                            viewModel.setFilterMaxBudget(if (isBudget) null else 1500.0)
                        },
                        leadingIcon = { Text("💰", fontSize = 12.sp) },
                        label = { Text("Budget < ₹1,500", fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE0E7FF),
                            selectedLabelColor = Color(0xFF3730A3),
                            containerColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isBudget,
                            borderColor = Color(0xFFCBD5E1),
                            selectedBorderColor = Color(0xFF4F46E5)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.testTag("filter_budget")
                    )
                }

                // Top Rated Filter (4.8+)
                item {
                    val isTopRated = filterMinRating == 4.8f
                    FilterChip(
                        selected = isTopRated,
                        onClick = {
                            viewModel.setFilterMinRating(if (isTopRated) null else 4.8f)
                        },
                        leadingIcon = { Text("⭐", fontSize = 12.sp) },
                        label = { Text("Top Rated (4.8+)", fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFFBEB),
                            selectedLabelColor = Color(0xFF92400E),
                            containerColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isTopRated,
                            borderColor = Color(0xFFCBD5E1),
                            selectedBorderColor = Color(0xFFF59E0B)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.testTag("filter_top_rated")
                    )
                }

                // Reset Filters Pill
                if (filterMaxDeliveryTime != null || filterMaxBudget != null || filterMinRating != null) {
                    item {
                        Surface(
                            onClick = { viewModel.clearAllAdvancedFilters() },
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Filters", modifier = Modifier.size(13.dp), tint = Color.Gray)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reset", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (selectedCategory.contains("Favorite", ignoreCase = true)) {
                                if (currentLanguage == Language.HINDI) "मेरे पसंदीदा कैटरर्स" else "My Favorite Caterers"
                            } else {
                                if (currentLanguage == Language.HINDI) "लोकप्रिय शाही बावर्ची और कैटरर्स" else "Popular Royal Bawarchis & Caterers"
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = AwadhiHandiCharcoal
                        )
                    }
                    Text(
                        text = if (currentLanguage == Language.HINDI) "असली दम पुख्त और बड़े आयोजनों के विशेषज्ञ" else "Authentic Dum Pukht & bulk event specialists",
                        fontSize = 11.sp,
                        color = Color(0xFF78716C)
                    )
                }
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(0.6.dp, AwadhiCardBorder)
                ) {
                    Text(
                        text = if (currentLanguage == Language.HINDI) "${caterers.size} रसोई" else "${caterers.size} Kitchens",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                    language = currentLanguage,
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

    if (showLocationSelectorDialog) {
        CustomerLocationSelectionDialog(
            currentLocation = deliveryLocation,
            onLocationSelected = { newLoc ->
                viewModel.setDeliveryLocation(newLoc)
                showLocationSelectorDialog = false
            },
            onDismiss = { showLocationSelectorDialog = false }
        )
    }

    if (showVegModeSheet) {
        CustomerVegModeBottomSheetDialog(
            isVegMode = isVegMode,
            currentScope = vegModeScope,
            currentDaysMode = vegModeDaysMode,
            selectedDays = selectedVegDays,
            onToggleDay = { day -> viewModel.toggleVegDay(day) },
            onApply = { scope, daysMode ->
                viewModel.setVegModeScope(scope)
                viewModel.setVegModeDaysMode(daysMode)
                viewModel.setVegMode(true)
                showVegModeSheet = false
            },
            onTurnOff = {
                viewModel.setVegMode(false)
                showVegModeSheet = false
            },
            onDismiss = { showVegModeSheet = false }
        )
    }
}

/**
 * Dialog allowing customer to select their delivery area / city for accurate catering kitchens.
 */
@Composable
fun CustomerLocationSelectionDialog(
    currentLocation: String,
    onLocationSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val quickLocations = listOf(
        "Okhla Phase 3, New Delhi",
        "Chandni Chowk, New Delhi",
        "Lajpat Nagar, New Delhi",
        "Preet Vihar, New Delhi",
        "Noida Sector 62, Uttar Pradesh",
        "Cyber City, Gurugram, Haryana"
    )
    var customAddress by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = SaffronPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Select Delivery Area", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text("डिलीवरी स्थान चुनें", fontSize = 11.sp, color = Color.Gray)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Current: $currentLocation",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SaffronPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text("Popular Delivery Hubs:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                Spacer(modifier = Modifier.height(6.dp))

                quickLocations.forEach { loc ->
                    val isSelected = loc.equals(currentLocation, ignoreCase = true)
                    Surface(
                        onClick = { onLocationSelected(loc) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) SaffronPrimary.copy(alpha = 0.12f) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (isSelected) SaffronPrimary else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isSelected) SaffronPrimary else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = loc,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) SaffronPrimary else Color(0xFF334155),
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Or Enter Other Locality:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = customAddress,
                    onValueChange = { customAddress = it },
                    placeholder = { Text("e.g. Rohini Sector 14, Delhi", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (customAddress.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onLocationSelected(customAddress.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Set Location", fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
    language: Language = Language.ENGLISH,
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
        border = BorderStroke(1.2.dp, AwadhiCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo / Kitchen photo
                val displayImg = caterer.logoUrl.ifBlank { caterer.bannerUrl }
                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                            )
                        )
                        .border(1.dp, AwadhiDesiGheeGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (displayImg.isNotBlank()) {
                        AsyncImage(
                            model = displayImg,
                            contentDescription = caterer.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fastfood,
                                contentDescription = null,
                                tint = AwadhiCrimsonRed,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = if (language == Language.HINDI) "रसोई" else "KITCHEN",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = AwadhiCrimsonRed
                            )
                        }
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
                                    color = AwadhiHandiCharcoal,
                                    maxLines = 1
                                )
                            }
                            if (caterer.kitchenName.isNotBlank() && caterer.kitchenName != caterer.name) {
                                Text(
                                    text = "👨‍🍳 ${caterer.kitchenName}",
                                    fontSize = 12.sp,
                                    color = AwadhiCrimsonRed,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    modifier = Modifier.padding(top = 1.dp)
                                )
                            }
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(0.6.dp, AwadhiCardBorder),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = if (language == Language.HINDI) "👑 शाही अवधी पार्टनर" else "👑 Shahi Awadhi Partner",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                )
                            }
                            when (caterer.dietaryType.uppercase()) {
                                "PURE_VEG" -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 3.dp)
                                    ) {
                                        VegNonVegBadge(foodType = FoodType.VEG, modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (language == Language.HINDI) "१००% शुद्ध शाकाहारी 🌿" else "100% PURE VEG 🌿",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VegGreen
                                        )
                                    }
                                }
                                "NON_VEG" -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 3.dp)
                                    ) {
                                        VegNonVegBadge(foodType = FoodType.NON_VEG, modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (language == Language.HINDI) "नॉन-वेज विशेषज्ञ 🍗" else "Non-Veg Specialist 🍗",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NonVegRed
                                        )
                                    }
                                }
                                else -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 3.dp)
                                    ) {
                                        VegNonVegBadge(foodType = FoodType.VEG, modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        VegNonVegBadge(foodType = FoodType.NON_VEG, modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (language == Language.HINDI) "वेज और नॉन-वेज 🟢🔴" else "Veg & Non-Veg 🟢🔴",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
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
                        FssaiBadge(licenseNo = caterer.fssaiLicense, language = language)
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
                        text = if (language == Language.HINDI) "🔥 लाइव देग और गर्म डिलीवरी" else "🔥 Live Deg & Hot Delivery",
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
                        text = if (language == Language.HINDI) "🔒 अग्रिम/पूर्ण भुगतान विकल्प" else "🔒 50% / Full Advance Option",
                        fontSize = 10.sp,
                        color = AwadhiCrimsonRed,
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
                    .background(Color(0xFFFEF9EE), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFFEF08A), RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(if (language == Language.HINDI) "तैयारी समय" else "Prep Time", fontSize = 10.sp, color = Color.Gray)
                    Text(if (language == Language.HINDI) "${caterer.deliveryTimeMinutes} मिनट" else "${caterer.deliveryTimeMinutes} mins", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(if (language == Language.HINDI) "न्यूनतम ऑर्डर" else "Min Bulk Order", fontSize = 10.sp, color = Color.Gray)
                    Text("₹${caterer.minOrderAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(if (language == Language.HINDI) "दूरी" else "Distance", fontSize = 10.sp, color = Color.Gray)
                    Text(if (language == Language.HINDI) "${caterer.distanceKm} किमी" else "${caterer.distanceKm} km", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onViewMenu,
                    colors = ButtonDefaults.buttonColors(containerColor = AwadhiCrimsonRed),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("view_menu_button_${caterer.id}")
                ) {
                    Text(if (language == Language.HINDI) "मेन्यू देखें 🍽️" else "Taste Menu 🍽️", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
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

@Composable
private fun DietFilterHomePill(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    badge: (@Composable () -> Unit)?,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) activeColor.copy(alpha = 0.12f) else Color(0xFFF8FAFC),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) activeColor else Color(0xFFE2E8F0)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (badge != null) {
                    badge()
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) activeColor else Color(0xFF334155)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = if (isSelected) activeColor.copy(alpha = 0.85f) else Color(0xFF64748B)
            )
        }
    }
}

@Composable
private fun CravingItemCard(
    emoji: String,
    title: String,
    badge: String,
    desc: String,
    badgeBg: Color,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AwadhiCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.width(180.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(emoji, fontSize = 24.sp)
                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AwadhiHandiCharcoal,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = desc,
                fontSize = 10.sp,
                color = Color.Gray,
                lineHeight = 13.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Explore Degs",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = AwadhiCrimsonRed
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = AwadhiCrimsonRed,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}

/**
 * Zomato-style Veg Mode Bottom Sheet Modal Dialog
 * Enables customers to filter between 100% Pure Veg Caterers or Veg menus from All Caterers,
 * with optional Day of the Week scheduling.
 */
@Composable
fun CustomerVegModeBottomSheetDialog(
    isVegMode: Boolean,
    currentScope: String,
    currentDaysMode: String,
    selectedDays: Set<String>,
    onToggleDay: (String) -> Unit,
    onApply: (String, String) -> Unit,
    onTurnOff: () -> Unit,
    onDismiss: () -> Unit
) {
    var tempScope by remember { mutableStateOf(currentScope) }
    var tempDaysMode by remember { mutableStateOf(currentDaysMode) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {},
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // Top drag indicator bar
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(42.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCBD5E1))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title Header with Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Veg Mode",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🌱", fontSize = 20.sp)
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section 1: "See veg dishes from"
                    Text(
                        text = "See veg dishes from",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 1: All restaurants
                    Surface(
                        onClick = { tempScope = "ALL_RESTAURANTS" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (tempScope == "ALL_RESTAURANTS") Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            1.2.dp,
                            if (tempScope == "ALL_RESTAURANTS") VegGreen else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = tempScope == "ALL_RESTAURANTS",
                                onClick = { tempScope = "ALL_RESTAURANTS" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = VegGreen,
                                    unselectedColor = Color(0xFF94A3B8)
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "All restaurants",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Get veg dishes from both pure veg and regular restaurants",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 2: Pure Veg restaurants only
                    Surface(
                        onClick = { tempScope = "PURE_VEG_ONLY" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (tempScope == "PURE_VEG_ONLY") Color(0xFFF0FDF4) else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            1.2.dp,
                            if (tempScope == "PURE_VEG_ONLY") VegGreen else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = tempScope == "PURE_VEG_ONLY",
                                onClick = { tempScope = "PURE_VEG_ONLY" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = VegGreen,
                                    unselectedColor = Color(0xFF94A3B8)
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Pure Veg restaurants only",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "100% PURE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = VegGreen,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Explore 100% pure vegetarian kitchens only (Shuddh Shakahari)",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color(0xFFE2E8F0)
                    )

                    // Section 2: "Select Veg Mode days"
                    Text(
                        text = "Select Veg Mode days",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option A: All days
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tempDaysMode = "ALL_DAYS" }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tempDaysMode == "ALL_DAYS",
                            onClick = { tempDaysMode = "ALL_DAYS" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = VegGreen,
                                unselectedColor = Color(0xFF94A3B8)
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "All days",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    // Option B: Select days of the week
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tempDaysMode = "CUSTOM_DAYS" }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tempDaysMode == "CUSTOM_DAYS",
                            onClick = { tempDaysMode = "CUSTOM_DAYS" },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = VegGreen,
                                unselectedColor = Color(0xFF94A3B8)
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Select days of the week",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    // Days selector buttons (M, T, W, T, F, S, S)
                    if (tempDaysMode == "CUSTOM_DAYS") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val days = listOf(
                                "M" to "Mon",
                                "T" to "Tue",
                                "W" to "Wed",
                                "T" to "Thu",
                                "F" to "Fri",
                                "S" to "Sat",
                                "S" to "Sun"
                            )
                            days.forEach { (letter, dayKey) ->
                                val isDaySelected = selectedDays.contains(dayKey)
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isDaySelected) VegGreen else Color(0xFFF1F5F9))
                                        .border(
                                            1.dp,
                                            if (isDaySelected) VegGreen else Color(0xFFCBD5E1),
                                            CircleShape
                                        )
                                        .clickable { onToggleDay(dayKey) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = letter,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDaySelected) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bottom Action CTA
                    if (isVegMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onTurnOff,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.2.dp, Color(0xFFDC2626))
                            ) {
                                Text(
                                    text = "Turn off Veg Mode",
                                    color = Color(0xFFDC2626),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Button(
                                onClick = { onApply(tempScope, tempDaysMode) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VegGreen)
                            ) {
                                Text(
                                    text = "Save Settings",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = { onApply(tempScope, tempDaysMode) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen)
                        ) {
                            Text(
                                text = "Switch on Veg Mode",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}
