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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.ui.common.CompactSearchBar
import com.example.ui.common.QuickAddCatererDialog
import com.example.data.models.FoodType
import com.example.data.models.Language
import com.example.util.LocalizationManager
import com.example.util.LocationHelper
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation

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
    var showQuickAddCatererDialog by remember { mutableStateOf(false) }
    var isFetchingHomeGps by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val homeLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isFetchingHomeGps = true
            LocationHelper.fetchCurrentLocation(
                context = context,
                onSuccess = { res ->
                    isFetchingHomeGps = false
                    viewModel.setDeliveryLocation(res.addressLine)
                    Toast.makeText(context, "📍 Current Location: ${res.addressLine}", Toast.LENGTH_SHORT).show()
                },
                onError = { err ->
                    isFetchingHomeGps = false
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, "Location permission required for GPS", Toast.LENGTH_SHORT).show()
        }
    }

    if (showQuickAddCatererDialog) {
        QuickAddCatererDialog(
            onDismiss = { showQuickAddCatererDialog = false },
            onCatererCreated = { newCaterer ->
                viewModel.addKitchenPartner(newCaterer) {
                    showQuickAddCatererDialog = false
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp),
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                shadowElevation = 2.dp,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_catererswale_mark),
                                    contentDescription = "Catererswale Logo",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .padding(2.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showLocationSelectorDialog = true }
                                    .padding(vertical = 2.dp, horizontal = 4.dp)
                                    .testTag("location_picker_header")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Caterers Wale",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.3.sp,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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
                                        text = if (isFetchingHomeGps) "Detecting GPS..." else deliveryLocation,
                                        fontSize = 11.5.sp,
                                        color = Color.White.copy(alpha = 0.95f),
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        onClick = {
                                            if (LocationHelper.hasLocationPermission(context)) {
                                                isFetchingHomeGps = true
                                                LocationHelper.fetchCurrentLocation(
                                                    context = context,
                                                    onSuccess = { res ->
                                                        isFetchingHomeGps = false
                                                        viewModel.setDeliveryLocation(res.addressLine)
                                                        Toast.makeText(context, "📍 Current Location: ${res.addressLine}", Toast.LENGTH_SHORT).show()
                                                    },
                                                    onError = { err ->
                                                        isFetchingHomeGps = false
                                                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                                    }
                                                )
                                            } else {
                                                homeLocationPermissionLauncher.launch(
                                                    arrayOf(
                                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                                    )
                                                )
                                            }
                                        },
                                        color = VegGreen,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.testTag("home_header_gps_btn")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MyLocation,
                                                contentDescription = "Use Current GPS",
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "GPS",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        }
                                    }
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
                                        color = AwadhiCrimsonRed,
                                        maxLines = 1
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

                    // Compact unclipped search bar + Veg Mode quick chip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CompactSearchBar(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = if (currentLanguage == Language.HINDI) {
                                if (isVegMode) "शुद्ध शाकाहारी बिरयानी, पनीर खोजें..." else "शाही बिरयानी, कोरमा, कैटरर खोजें..."
                            } else {
                                if (isVegMode) "Search Pure Veg Biryani, Paneer..." else "Search Biryani, Korma, Caterer..."
                            },
                            height = 38.dp,
                            borderColor = if (isVegMode) VegGreen else AwadhiDesiGheeGold,
                            leadingIconTint = if (isVegMode) VegGreen else AwadhiCrimsonRed,
                            testTag = "search_bar",
                            modifier = Modifier.weight(1f)
                        )

                        Surface(
                            onClick = { viewModel.setVegMode(!isVegMode) },
                            shape = RoundedCornerShape(19.dp),
                            color = if (isVegMode) VegGreen else Color.White,
                            border = BorderStroke(1.dp, if (isVegMode) Color.White else AwadhiCardBorder),
                            modifier = Modifier.height(38.dp).testTag("home_veg_toggle_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isVegMode) Color.White else VegGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Pure Veg",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isVegMode) Color.White else VegGreen
                                )
                            }
                        }
                    }

                    // Compact indicator when Veg Mode is active
                    if (isVegMode) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDCFCE7),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showVegModeSheet = true }
                                .testTag("veg_mode_active_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("🌱", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (vegModeScope == "PURE_VEG_ONLY") "VEG MODE ON • Pure Veg Only" else "VEG MODE ON • Veg Dishes",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF14532D)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.setVegMode(false) },
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Turn off Veg Mode",
                                        tint = Color(0xFF166534),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Clean Category Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    val quickCats = listOf("All", "Chicken Biryani", "Mutton Biryani", "Veg Biryani", "Chicken Gravy", "Veg Gravy", "Desserts")
                    items(quickCats) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            onClick = { viewModel.setSelectedCategory(cat) },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) AwadhiCrimsonRed else Color.White,
                            border = BorderStroke(1.dp, if (isSelected) AwadhiCrimsonRed else Color(0xFFE2E8F0)),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }

                // Section Title + Add New Caterer Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedCategory.contains("Favorite", ignoreCase = true)) {
                                if (currentLanguage == Language.HINDI) "मेरे पसंदीदा कैटरर्स" else "My Favorite Caterers"
                            } else {
                                if (currentLanguage == Language.HINDI) "शाही कैटरर्स व बावर्ची (${caterers.size})" else "Featured Caterers (${caterers.size})"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AwadhiHandiCharcoal
                        )
                        Text(
                            text = "FSSAI Verified • Bulk & Party Catering",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Surface(
                        onClick = { showQuickAddCatererDialog = true },
                        color = SaffronPrimary,
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 2.dp,
                        modifier = Modifier.testTag("home_add_new_caterer_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add New Caterer",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentLanguage == Language.HINDI) "+ नया कैटरर जोड़ें" else "+ Add Caterer",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
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

        // 15. Clean Sleek Footer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Caterers Wale • FSSAI Certified Bulk Catering Marketplace",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )
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

    val context = LocalContext.current
    var isDetectingGps by remember { mutableStateOf(false) }

    val dialogLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isDetectingGps = true
            LocationHelper.fetchCurrentLocation(
                context = context,
                onSuccess = { res ->
                    isDetectingGps = false
                    Toast.makeText(context, "📍 Current Location: ${res.addressLine}", Toast.LENGTH_SHORT).show()
                    onLocationSelected(res.addressLine)
                },
                onError = { err ->
                    isDetectingGps = false
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, "Please allow Location permission to detect GPS", Toast.LENGTH_SHORT).show()
        }
    }

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
                    Text("डिलीवरी स्थान चुनें या करंट लोकेशन लें", fontSize = 11.sp, color = Color.Gray)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Prominent Use Current GPS Location Button
                Button(
                    onClick = {
                        if (LocationHelper.hasLocationPermission(context)) {
                            isDetectingGps = true
                            LocationHelper.fetchCurrentLocation(
                                context = context,
                                onSuccess = { res ->
                                    isDetectingGps = false
                                    Toast.makeText(context, "📍 Current Location: ${res.addressLine}", Toast.LENGTH_SHORT).show()
                                    onLocationSelected(res.addressLine)
                                },
                                onError = { err ->
                                    isDetectingGps = false
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            dialogLocationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("use_current_gps_location_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isDetectingGps) "Detecting GPS Location..." else "📍 Use My Current Location (GPS)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Current: $currentLocation",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SaffronPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

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
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clickable { onViewMenu() }
            .testTag("caterer_card_${caterer.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Consistent 84x84 Thumbnail with Rating Pill
                val displayImg = caterer.logoUrl.ifBlank { caterer.bannerUrl }
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A))
                            )
                        )
                        .border(1.dp, AwadhiDesiGheeGold.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                ) {
                    if (displayImg.isNotBlank()) {
                        AsyncImage(
                            model = displayImg,
                            contentDescription = caterer.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_catererswale_mark),
                                contentDescription = caterer.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFF15803D),
                        shape = RoundedCornerShape(topEnd = 8.dp),
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format(java.util.Locale.US, "%.1f", caterer.rating),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Details Column with balanced Medium & Small typography
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = caterer.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("favorite_btn_${caterer.id}")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (isFavorite) Color(0xFFE53935) else Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Dietary Badge & Cuisine Subtitle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        when (caterer.dietaryType.uppercase()) {
                            "PURE_VEG" -> {
                                VegNonVegBadge(foodType = FoodType.VEG, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == Language.HINDI) "शुद्ध शाकाहारी" else "Pure Veg",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VegGreen
                                )
                            }
                            "NON_VEG" -> {
                                VegNonVegBadge(foodType = FoodType.NON_VEG, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == Language.HINDI) "नॉन-वेज स्पेशल" else "Non-Veg Specialist",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NonVegRed
                                )
                            }
                            else -> {
                                VegNonVegBadge(foodType = FoodType.VEG, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                VegNonVegBadge(foodType = FoodType.NON_VEG, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Veg & Non-Veg",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                        if (caterer.kitchenName.isNotBlank() && caterer.kitchenName != caterer.name) {
                            Text(
                                text = " • ${caterer.kitchenName}",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Location & Distance Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = SaffronPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${caterer.address} • ${caterer.distanceKm} km",
                            fontSize = 11.5.sp,
                            color = Color(0xFF475569),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Bottom Divider + Footer Action Row
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Min ₹${caterer.minOrderAmount.toInt()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "⏱️ ${caterer.deliveryTimeMinutes} mins",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    if (caterer.isFssaiVerified) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "✓ FSSAI",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Surface(
                    onClick = onViewMenu,
                    color = AwadhiCrimsonRed,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("view_menu_button_${caterer.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == Language.HINDI) "मेन्यू देखें" else "View Menu",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
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
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .testTag("nav_tab_${label.lowercase()}")
    ) {
        if (badgeCount > 0) {
            BadgedBox(badge = {
                Badge(containerColor = SaffronPrimary) {
                    Text(badgeCount.toString(), fontSize = 9.sp)
                }
            }) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = if (isSelected) SaffronPrimary else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isSelected) SaffronPrimary else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) SaffronPrimary else Color.Gray
        )
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
