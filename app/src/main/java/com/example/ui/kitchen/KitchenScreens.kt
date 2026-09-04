package com.example.ui.kitchen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.content.Context
import android.content.ClipboardManager
import android.content.ClipData
import android.content.Intent
import androidx.compose.material.icons.filled.ContentCopy
import android.graphics.Bitmap
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import android.app.DatePickerDialog
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Save
import com.example.data.models.CatererEntity
import com.example.data.models.KitchenSettingsConfig
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DeliveryBoyEntity
import com.example.data.models.FoodType
import com.example.data.models.KycStatus
import com.example.data.models.MenuItemEntity
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.models.PaymentMethod
import com.example.data.models.PaymentStatus
import com.example.data.models.UnitType
import com.example.data.repository.CaterersViewModel
import com.example.ui.common.FssaiBadge
import com.example.ui.common.VegNonVegBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen

@Composable
fun KitchenMainContainer(
    viewModel: CaterersViewModel,
    onPreviewCustomerStore: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedStageFilterForOrders by remember { mutableStateOf("ALL") }
    val tabs = listOf(
        "Dashboard",
        "Share Store Link 🔗",
        "Analytics & Charts",
        "Menu & Prices",
        "Orders KDS",
        "Raw Materials (Rashan)",
        "Staff & Halwai",
        "Production",
        "Bartan Inventory",
        "Offline Booking",
        "Finance & Profit",
        "KYC & Profile",
        "Kitchen Settings"
    )

    Column(modifier = modifier.fillMaxSize()) {
        // Kitchen Header
        Surface(color = SaffronPrimary, contentColor = Color.White) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Kitchen, contentDescription = null, tint = AmberSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("A1 Huma Central Kitchen", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Caterer Partner Portal", fontSize = 11.sp, color = AmberSecondary)
                        }
                    }
                    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("FSSAI Verified", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, style = MaterialTheme.typography.labelLarge, fontSize = 13.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium) },
                            modifier = Modifier.testTag("kitchen_tab_$index")
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF9F6F0))
        ) {
            when (selectedTab) {
                0 -> KitchenDashboardScreen(
                    viewModel = viewModel,
                    onNavigateTab = { selectedTab = it },
                    onNavigateToOrdersWithStage = { stage ->
                        selectedStageFilterForOrders = stage
                        selectedTab = 4
                    },
                    onPreviewCustomerStore = onPreviewCustomerStore
                )
                1 -> KitchenDigitalStoreShareScreen(viewModel, onPreviewCustomerStore = onPreviewCustomerStore)
                2 -> KitchenAnalyticsDashboard(viewModel)
                3 -> KitchenMenuManagementScreen(viewModel)
                4 -> KitchenOrderManagementScreen(viewModel, initialStageFilter = selectedStageFilterForOrders)
                5 -> KitchenRawMaterialScreen(viewModel)
                6 -> KitchenStaffManagementScreen(viewModel)
                7 -> KitchenProductionScreen(viewModel)
                8 -> KitchenBartanScreen(viewModel)
                9 -> KitchenOfflineBookingScreen(viewModel)
                10 -> KitchenFinanceScreen(viewModel)
                11 -> KitchenProfileKycScreen(viewModel)
                12 -> KitchenSettingsScreen(viewModel)
            }
        }
    }
}

@Composable
fun KitchenDashboardScreen(
    viewModel: CaterersViewModel,
    onNavigateTab: (Int) -> Unit,
    onNavigateToOrdersWithStage: ((String) -> Unit)? = null,
    onPreviewCustomerStore: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val caterers by viewModel.caterersList.collectAsState()
    val kitchen = caterers.find { it.id == "caterer_1" } ?: caterers.firstOrNull()
    val orders by viewModel.ordersList.collectAsState()
    val deliveryBoys by viewModel.deliveryBoysList.collectAsState()

    var activeViewMode by remember { mutableStateOf("OVERVIEW") } // "OVERVIEW" or "ANALYTICS"
    var isOpenBooking by remember { mutableStateOf(kitchen?.isOpenForBooking ?: true) }

    // Click state for interactive order counters and summary cards
    var counterSelectedCategory by remember { mutableStateOf<String?>(null) }
    var counterSelectedOrderForDetail by remember { mutableStateOf<OrderEntity?>(null) }
    var counterAssignDialogOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var counterKotDialogOrder by remember { mutableStateOf<OrderEntity?>(null) }

    val todaySales = orders.sumOf { it.totalAmount }
    val onlineSales = orders.filter { it.paymentMethod != PaymentMethod.CASH_ON_DELIVERY }.sumOf { it.advancePaidAmount }
    val pendingCollection = orders.sumOf { it.balanceAmount }

    if (activeViewMode == "ANALYTICS") {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📊 Monthly Charts & Analytics", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                OutlinedButton(
                    onClick = { activeViewMode = "OVERVIEW" },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SaffronPrimary)
                ) {
                    Text("Back to Live Ops", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            KitchenAnalyticsDashboard(viewModel, modifier = Modifier.weight(1f))
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Mode Switcher Banner (Live Ops vs Full Analytics Charts)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { activeViewMode = "ANALYTICS" }
                    .testTag("btn_view_monthly_analytics"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = SaffronPrimary,
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Monthly Earnings & Charts Dashboard", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
                            Text("View revenue curves, order volume & top dishes", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                    }

                    Button(
                        onClick = { activeViewMode = "ANALYTICS" },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Open Charts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Business Status Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (isOpenBooking) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isOpenBooking) "🟢 OPEN FOR BOOKING" else "🔴 BOOKING CLOSED",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isOpenBooking) VegGreen else Color.Red
                        )
                        Text("Accept new catering orders from customer app", fontSize = 11.sp, color = Color.Gray)
                    }

                    Switch(
                        checked = isOpenBooking,
                        onCheckedChange = {
                            isOpenBooking = it
                            kitchen?.let { k -> viewModel.toggleKitchenBookingStatus(k.id, it) }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = VegGreen, checkedTrackColor = Color(0xFFA5D6A7)),
                        modifier = Modifier.testTag("booking_status_switch")
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Exclusive Digital Store Link Sharing Hero Card
        item {
            val context = LocalContext.current
            val storeUrl = "https://catererswale.app/store/${kitchen?.id ?: "caterer_1"}"
            val whatsappText = "🎉 *${kitchen?.kitchenName ?: "A1 Huma Caterers"}* Online Dawat Menu & Booking!\n\nNamaste! Ab aap hamari royal biryani, mughlai starters aur catering packages direct online dekh kar live book kar sakte hain:\n\n🔗 $storeUrl\n\n✅ FSSAI Certified | ⭐ 4.8 Rating | 📞 Helpline: ${kitchen?.ownerMobile ?: "9876543210"}"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("kitchen_share_store_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF25D366),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("मेरी दुकान का लिंक शेयर करें", fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = Color(0xFF0F172A))
                                Text("Customer ko bhejein - Sirf aapka menu dikhega", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        TextButton(onClick = { onNavigateTab(1) }) {
                            Text("QR Standee 👉", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = storeUrl,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SaffronPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Caterer Store Link", storeUrl)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "✅ Store link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, whatsappText)
                                    type = "text/plain"
                                    setPackage("com.whatsapp")
                                }
                                try {
                                    context.startActivity(sendIntent)
                                } catch (e: Exception) {
                                    val fallback = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, whatsappText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(fallback, "Share Store via"))
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp 💬", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = {
                                onPreviewCustomerStore(kitchen?.id ?: "caterer_1")
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F172A)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Preview 👁️", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Sales Metrics
        item {
            Text("Today's Business Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Total Sales",
                    value = "₹${todaySales.toInt()}",
                    bgColor = Color(0xFF1E293B),
                    textColor = Color.White,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_total_sales",
                    onClick = { counterSelectedCategory = "ALL" }
                )
                MetricCard(
                    title = "Advance Online",
                    value = "₹${onlineSales.toInt()}",
                    bgColor = VegGreen,
                    textColor = Color.White,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_advance_online",
                    onClick = { counterSelectedCategory = "ONLINE_ADVANCE" }
                )
                MetricCard(
                    title = "Pending Balance",
                    value = "₹${pendingCollection.toInt()}",
                    bgColor = SaffronPrimary,
                    textColor = Color.White,
                    modifier = Modifier.weight(1f),
                    testTag = "metric_pending_balance",
                    onClick = { counterSelectedCategory = "PENDING_BALANCE" }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Order Status Counters (Interactive - Tap to view order details)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Order Pipeline Counters", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                Surface(
                    color = SaffronPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Tap counter to view details 👆",
                        fontSize = 10.5.sp,
                        color = SaffronPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CounterChip(
                        label = "New Orders",
                        count = orders.count { it.orderStatus == OrderStatus.NEW }.toString(),
                        color = SaffronPrimary,
                        modifier = Modifier.weight(1f),
                        testTag = "counter_chip_new",
                        onClick = { counterSelectedCategory = "NEW" }
                    )
                    CounterChip(
                        label = "Preparing",
                        count = orders.count { it.orderStatus == OrderStatus.PREPARING }.toString(),
                        color = AmberSecondary,
                        modifier = Modifier.weight(1f),
                        testTag = "counter_chip_preparing",
                        onClick = { counterSelectedCategory = "PREPARING" }
                    )
                    CounterChip(
                        label = "Out for Delivery",
                        count = orders.count { it.orderStatus == OrderStatus.OUT_FOR_DELIVERY || it.orderStatus == OrderStatus.ASSIGNED_DELIVERY }.toString(),
                        color = Color(0xFF0288D1),
                        modifier = Modifier.weight(1f),
                        testTag = "counter_chip_out_for_delivery",
                        onClick = { counterSelectedCategory = "OUT_FOR_DELIVERY" }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CounterChip(
                        label = "Delivered",
                        count = orders.count { it.orderStatus == OrderStatus.DELIVERED }.toString(),
                        color = VegGreen,
                        modifier = Modifier.weight(1f),
                        testTag = "counter_chip_delivered",
                        onClick = { counterSelectedCategory = "DELIVERED" }
                    )
                    CounterChip(
                        label = "Total Orders",
                        count = orders.size.toString(),
                        color = Color(0xFF424242),
                        modifier = Modifier.weight(1f),
                        testTag = "counter_chip_total",
                        onClick = { counterSelectedCategory = "ALL" }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Quick Shortcuts
        item {
            Text("Kitchen Quick Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickActionTile("Analytics Charts", Icons.Default.Analytics, SaffronPrimary, Modifier.weight(1f)) {
                    activeViewMode = "ANALYTICS"
                }
                QuickActionTile("Rashan List", Icons.Default.ShoppingCart, VegGreen, Modifier.weight(1f)) {
                    onNavigateTab(5) // Raw materials (Rashan)
                }
                QuickActionTile("Offline Booking", Icons.Default.ReceiptLong, Color(0xFF0288D1), Modifier.weight(1f)) {
                    onNavigateTab(9) // Offline booking tab
                }
            }
        }
    }

    // 1. Interactive Counter Orders Details Dialog
    if (counterSelectedCategory != null) {
        val categoryKey = counterSelectedCategory!!
        val catTitle: String
        val catColor: Color
        val catSubtitle: String
        val catOrders: List<OrderEntity>
        val targetStage: String

        when (categoryKey) {
            "NEW" -> {
                catTitle = "🛎️ New Orders (${orders.count { it.orderStatus == OrderStatus.NEW }})"
                catColor = SaffronPrimary
                catSubtitle = "Recently placed orders waiting for kitchen confirmation"
                catOrders = orders.filter { it.orderStatus == OrderStatus.NEW }
                targetStage = "NEW"
            }
            "PREPARING" -> {
                catTitle = "🍳 Preparing Orders (${orders.count { it.orderStatus == OrderStatus.PREPARING }})"
                catColor = AmberSecondary
                catSubtitle = "Batches currently cooking and being packed in kitchen"
                catOrders = orders.filter { it.orderStatus == OrderStatus.PREPARING }
                targetStage = "PREPARING"
            }
            "OUT_FOR_DELIVERY" -> {
                val outCount = orders.count { it.orderStatus == OrderStatus.OUT_FOR_DELIVERY || it.orderStatus == OrderStatus.ASSIGNED_DELIVERY }
                catTitle = "🚚 Out for Delivery ($outCount)"
                catColor = Color(0xFF0288D1)
                catSubtitle = "Orders currently in transit with delivery partners"
                catOrders = orders.filter { it.orderStatus == OrderStatus.OUT_FOR_DELIVERY || it.orderStatus == OrderStatus.ASSIGNED_DELIVERY }
                targetStage = "OUT_FOR_DELIVERY"
            }
            "DELIVERED" -> {
                catTitle = "✅ Delivered Orders (${orders.count { it.orderStatus == OrderStatus.DELIVERED }})"
                catColor = VegGreen
                catSubtitle = "Successfully completed and delivered catering bookings"
                catOrders = orders.filter { it.orderStatus == OrderStatus.DELIVERED }
                targetStage = "DELIVERED"
            }
            "ONLINE_ADVANCE" -> {
                val advOrders = orders.filter { it.advancePaidAmount > 0 }
                catTitle = "💳 Online Advance Paid (${advOrders.size})"
                catColor = VegGreen
                catSubtitle = "Orders with verified digital advance payment"
                catOrders = advOrders
                targetStage = "ALL"
            }
            "PENDING_BALANCE" -> {
                val balOrders = orders.filter { it.balanceAmount > 0 }
                catTitle = "💰 Pending Balance Collection (${balOrders.size})"
                catColor = SaffronPrimary
                catSubtitle = "Orders with remaining balance due to be collected"
                catOrders = balOrders
                targetStage = "ALL"
            }
            else -> {
                catTitle = "📦 Total Catering Orders (${orders.size})"
                catColor = Color(0xFF424242)
                catSubtitle = "Complete list of online and offline bookings"
                catOrders = orders
                targetStage = "ALL"
            }
        }

        AlertDialog(
            onDismissRequest = { counterSelectedCategory = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = CircleShape,
                            color = catColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = catColor, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(catTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            Text("${catOrders.size} Orders • Tap any card for full details", fontSize = 10.5.sp, color = Color.Gray)
                        }
                    }
                    IconButton(onClick = { counterSelectedCategory = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(catSubtitle, fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.padding(bottom = 8.dp))

                    if (catOrders.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No orders found in this status", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text("इस श्रेणी में अभी कोई सक्रिय ऑर्डर नहीं है", fontSize = 11.sp, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { counterSelectedCategory = "ALL" },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("View All Orders (सभी देखें)", fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 420.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(catOrders) { order ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            counterSelectedOrderForDetail = order
                                        }
                                        .testTag("counter_order_card_${order.orderId}"),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        // Top Row: Order ID & Status Chip
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("#${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SaffronPrimary)
                                                if (order.isOfflineBooking) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Surface(color = Color(0xFFFFE0B2), shape = RoundedCornerShape(4.dp)) {
                                                        Text("OFFLINE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                    }
                                                }
                                            }

                                            Surface(
                                                color = when (order.orderStatus) {
                                                    OrderStatus.NEW -> Color(0xFFE0F2FE)
                                                    OrderStatus.CONFIRMED, OrderStatus.ACCEPTED -> Color(0xFFE8F5E9)
                                                    OrderStatus.PREPARING -> Color(0xFFFFF3E0)
                                                    OrderStatus.READY -> Color(0xFFF3E8FF)
                                                    OrderStatus.ASSIGNED_DELIVERY, OrderStatus.OUT_FOR_DELIVERY -> AmberSecondary
                                                    OrderStatus.DELIVERED -> VegGreen
                                                    else -> Color(0xFFF1F5F9)
                                                },
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    order.orderStatus.name,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (order.orderStatus == OrderStatus.DELIVERED) Color.White else Color.Black,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Delivery Date & Slot
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("📅 Date: ${order.deliveryDate}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                            Text("⏰ Slot: ${order.deliveryTimeSlot}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Customer Info & Clickable Call
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("👤 ${order.customerName}", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFF1E293B))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable {
                                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.customerMobile}"))
                                                    context.startActivity(intent)
                                                }
                                            ) {
                                                Icon(Icons.Default.Phone, contentDescription = "Call Customer", tint = VegGreen, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(order.customerMobile, fontSize = 11.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Text("📍 Venue: ${order.deliveryAddress}", fontSize = 10.5.sp, color = Color.Gray, maxLines = 1, modifier = Modifier.padding(top = 2.dp))

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Food Items
                                        Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                            Text("🍽️ Dishes: ${order.itemsSummary}", fontSize = 11.sp, color = Color(0xFF334155), modifier = Modifier.padding(6.dp))
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Financial breakdown
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Total: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFF1E293B))
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text("Adv: ₹${order.advancePaidAmount.toInt()}", fontSize = 10.5.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                                Text("Bal: ₹${order.balanceAmount.toInt()}", fontSize = 10.5.sp, color = if (order.balanceAmount > 0) Color(0xFFD32F2F) else VegGreen, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Action buttons row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = {
                                                    counterSelectedOrderForDetail = order
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.weight(1.1f)
                                            ) {
                                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Details 👁️", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    counterKotDialogOrder = order
                                                },
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.weight(0.7f)
                                            ) {
                                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("KOT 🖨️", fontSize = 10.5.sp)
                                            }

                                            // Quick lifecycle button
                                            when (order.orderStatus) {
                                                OrderStatus.NEW -> {
                                                    Button(
                                                        onClick = {
                                                            viewModel.updateOrderStatus(order.orderId, OrderStatus.CONFIRMED)
                                                            Toast.makeText(context, "Order #${order.orderId} Confirmed 🟢", Toast.LENGTH_SHORT).show()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier.weight(0.9f)
                                                    ) {
                                                        Text("Accept 🟢", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                OrderStatus.CONFIRMED -> {
                                                    Button(
                                                        onClick = {
                                                            viewModel.updateOrderStatus(order.orderId, OrderStatus.PREPARING)
                                                            Toast.makeText(context, "Cooking started for #${order.orderId} 🍳", Toast.LENGTH_SHORT).show()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier.weight(0.9f)
                                                    ) {
                                                        Text("Cook 🍳", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                OrderStatus.PREPARING -> {
                                                    Button(
                                                        onClick = {
                                                            viewModel.updateOrderStatus(order.orderId, OrderStatus.READY)
                                                            Toast.makeText(context, "Order #${order.orderId} Ready 🍱", Toast.LENGTH_SHORT).show()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier.weight(0.9f)
                                                    ) {
                                                        Text("Ready 🍱", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                                OrderStatus.READY -> {
                                                    Button(
                                                        onClick = {
                                                            counterAssignDialogOrder = order
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                                        shape = RoundedCornerShape(6.dp),
                                                        modifier = Modifier.weight(0.9f)
                                                    ) {
                                                        Text("Assign 🛵", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                                    }
                                                }
                                                else -> {}
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val stageToOpen = targetStage
                        counterSelectedCategory = null
                        if (onNavigateToOrdersWithStage != null) {
                            onNavigateToOrdersWithStage.invoke(stageToOpen)
                        } else {
                            onNavigateTab(4)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open in Full Orders KDS 👉", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { counterSelectedCategory = null }) {
                    Text("Close", color = Color.Gray)
                }
            }
        )
    }

    // 2. Comprehensive Order Details Dialog
    if (counterSelectedOrderForDetail != null) {
        val order = counterSelectedOrderForDetail!!
        KitchenOrderDetailDialog(
            order = order,
            deliveryBoys = deliveryBoys,
            onDismiss = { counterSelectedOrderForDetail = null },
            onUpdateStatus = { newStatus ->
                viewModel.updateOrderStatus(order.orderId, newStatus)
                counterSelectedOrderForDetail = null
            },
            onOpenAssignDialog = {
                counterAssignDialogOrder = order
                counterSelectedOrderForDetail = null
            },
            onPrintKot = {
                counterKotDialogOrder = order
                counterSelectedOrderForDetail = null
            }
        )
    }

    // 3. Assign Delivery Partner Dialog from Counter Details
    if (counterAssignDialogOrder != null) {
        val targetOrder = counterAssignDialogOrder!!
        AlertDialog(
            onDismissRequest = { counterAssignDialogOrder = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Assign Delivery Boy to #${targetOrder.orderId}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Select available delivery partner for pickup:", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 280.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(deliveryBoys) { boy ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.assignDeliveryBoy(targetOrder.orderId, boy)
                                        viewModel.updateOrderStatus(targetOrder.orderId, OrderStatus.OUT_FOR_DELIVERY)
                                        counterAssignDialogOrder = null
                                        Toast.makeText(context, "🚴 Assigned ${boy.name}! Order moved to Out for Delivery.", Toast.LENGTH_SHORT).show()
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFE0F2FE),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.padding(8.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(boy.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
                                        Text("📞 ${boy.mobile} | Aadhaar: Verified ✅", fontSize = 11.sp, color = Color.Gray)
                                        Text("Completed: ${boy.todayCompletedDeliveries} | Status: ${if (boy.isBusy) "On Delivery 🚚" else "Available 🟢"}", fontSize = 10.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.assignDeliveryBoy(targetOrder.orderId, boy)
                                            viewModel.updateOrderStatus(targetOrder.orderId, OrderStatus.OUT_FOR_DELIVERY)
                                            counterAssignDialogOrder = null
                                            Toast.makeText(context, "🚴 Assigned ${boy.name}!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.testTag("counter_assign_boy_${boy.id}")
                                    ) {
                                        Text("Assign", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { counterAssignDialogOrder = null }) { Text("Cancel") }
            }
        )
    }

    // 4. KOT Preview & Print Dialog from Counter Details
    if (counterKotDialogOrder != null) {
        val order = counterKotDialogOrder!!
        AlertDialog(
            onDismissRequest = { counterKotDialogOrder = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kitchen Order Token (KOT)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)), shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("A1 HUMA KITCHEN - PRODUCTION KOT", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                        Text("ORDER ID: #${order.orderId}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        Text("SLOT: ${order.deliveryDate} (${order.deliveryTimeSlot})", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Divider(modifier = Modifier.padding(vertical = 6.dp))
                        Text("DISHES TO PREPARE:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                        Text(order.itemsSummary, fontSize = 13.sp, color = Color.DarkGray)
                        Divider(modifier = Modifier.padding(vertical = 6.dp))
                        Text("CUSTOMER: ${order.customerName} (${order.customerMobile})", fontSize = 11.sp)
                        Text("VENUE: ${order.deliveryAddress}", fontSize = 11.sp)
                        Text("PAYMENT: Total ₹${order.totalAmount.toInt()} | Adv: ₹${order.advancePaidAmount.toInt()} | Bal: ₹${order.balanceAmount.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        counterKotDialogOrder = null
                        printTodayOrdersHtml(context, listOf(order), "A1 Huma Kitchen KOT")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Print KOT Slip 🖨️")
                }
            },
            dismissButton = {
                TextButton(onClick = { counterKotDialogOrder = null }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = if (onClick != null) {
            modifier
                .testTag(testTag)
                .clickable { onClick() }
        } else modifier.testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = bgColor, contentColor = textColor),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(11.dp)) {
            Text(title, fontSize = 10.sp, color = textColor.copy(alpha = 0.85f))
            Spacer(modifier = Modifier.height(3.dp))
            Text(value, fontSize = 15.5.sp, fontWeight = FontWeight.Bold, color = textColor)
            if (onClick != null) {
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = textColor.copy(alpha = 0.9f), modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Tap for details", fontSize = 8.5.sp, color = textColor.copy(alpha = 0.85f), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun CounterChip(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, color.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B), maxLines = 1)
                Surface(color = color, shape = CircleShape) {
                    Text(
                        count,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = color, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "View Orders 👉",
                    fontSize = 9.5.sp,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun QuickActionTile(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        }
    }
}

@Composable
fun KitchenOrderManagementScreen(
    viewModel: CaterersViewModel,
    initialStageFilter: String = "ALL"
) {
    val context = LocalContext.current
    val orders by viewModel.ordersList.collectAsState()
    val deliveryBoys by viewModel.deliveryBoysList.collectAsState()

    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDateStr = sdf.format(cal.time)
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrowDateStr = sdf.format(cal.time)

    // State
    var selectedDateFilter by remember { mutableStateOf("ALL") } // "ALL", "TODAY", "TOMORROW", or "yyyy-MM-dd"
    var customPickedDate by remember { mutableStateOf<String?>(null) }
    var selectedStageFilter by remember(initialStageFilter) { mutableStateOf(initialStageFilter) }
    var showAssignDialogForOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var showKotDialogForOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var showDetailDialogForOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var showPrintSummaryDialog by remember { mutableStateOf(false) }
    var expandedOrderIds by remember { mutableStateOf(setOf<String>()) }

    // Distinct dates from orders
    val availableOrderDates = remember(orders) {
        orders.map { it.deliveryDate }.filter { it.isNotBlank() }.distinct().sortedDescending()
    }

    // Filter by Date first
    val dateFilteredOrders = orders.filter { order ->
        when (selectedDateFilter) {
            "ALL" -> true
            "TODAY" -> order.deliveryDate == todayDateStr || order.deliveryDate == "2026-07-25"
            "TOMORROW" -> order.deliveryDate == tomorrowDateStr || order.deliveryDate == "2026-07-26"
            else -> order.deliveryDate == selectedDateFilter
        }
    }

    // Counts for Stage Boxes (for the currently selected date)
    val newOrdersCount = dateFilteredOrders.count { it.orderStatus == OrderStatus.NEW }
    val confirmedOrdersCount = dateFilteredOrders.count { it.orderStatus == OrderStatus.CONFIRMED || it.orderStatus == OrderStatus.ACCEPTED }
    val prepOrdersCount = dateFilteredOrders.count { it.orderStatus == OrderStatus.PREPARING }
    val readyOrdersCount = dateFilteredOrders.count { it.orderStatus == OrderStatus.READY }
    val outForDeliveryOrdersCount = dateFilteredOrders.count { it.orderStatus == OrderStatus.OUT_FOR_DELIVERY || it.orderStatus == OrderStatus.ASSIGNED_DELIVERY }
    val deliveredOrdersCount = dateFilteredOrders.count { it.orderStatus == OrderStatus.DELIVERED }

    // Filter by Stage
    val finalOrders = dateFilteredOrders.filter { order ->
        when (selectedStageFilter) {
            "ALL" -> true
            "NEW" -> order.orderStatus == OrderStatus.NEW
            "CONFIRMED" -> order.orderStatus == OrderStatus.CONFIRMED || order.orderStatus == OrderStatus.ACCEPTED
            "PREPARING" -> order.orderStatus == OrderStatus.PREPARING
            "READY" -> order.orderStatus == OrderStatus.READY
            "OUT_FOR_DELIVERY" -> order.orderStatus == OrderStatus.OUT_FOR_DELIVERY || order.orderStatus == OrderStatus.ASSIGNED_DELIVERY
            "DELIVERED" -> order.orderStatus == OrderStatus.DELIVERED
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // TOP: Date Filter & Summary Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("📅 Kitchen Orders & Dispatch System", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    text = if (selectedDateFilter == "ALL") "Viewing All Event Dates (${dateFilteredOrders.size} Orders)" else "Filtered Date: $selectedDateFilter (${dateFilteredOrders.size} Orders)",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Button(
                            onClick = { showPrintSummaryDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("print_summary_button")
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Print 🖨️", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Date Selection Chips Bar
                    Text("SELECT EVENT / DELIVERY DATE (तारीख चुनें):", color = AmberSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedDateFilter == "ALL",
                            onClick = { selectedDateFilter = "ALL"; customPickedDate = null },
                            label = { Text("ALL DATES", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color.LightGray
                            ),
                            modifier = Modifier.testTag("date_filter_all")
                        )

                        FilterChip(
                            selected = selectedDateFilter == "TODAY" || selectedDateFilter == "2026-07-25" || selectedDateFilter == todayDateStr,
                            onClick = { selectedDateFilter = "TODAY"; customPickedDate = null },
                            label = { Text("TODAY", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color.LightGray
                            ),
                            modifier = Modifier.testTag("date_filter_today")
                        )

                        FilterChip(
                            selected = selectedDateFilter == "TOMORROW" || selectedDateFilter == "2026-07-26" || selectedDateFilter == tomorrowDateStr,
                            onClick = { selectedDateFilter = "TOMORROW"; customPickedDate = null },
                            label = { Text("TOMORROW", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color.LightGray
                            ),
                            modifier = Modifier.testTag("date_filter_tomorrow")
                        )

                        // Custom Date Picker button
                        Button(
                            onClick = {
                                val c = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val picked = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                                        customPickedDate = picked
                                        selectedDateFilter = picked
                                    },
                                    c.get(Calendar.YEAR),
                                    c.get(Calendar.MONTH),
                                    c.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (customPickedDate != null) AmberSecondary else Color(0xFF334155),
                                contentColor = if (customPickedDate != null) Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("date_filter_pick")
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(customPickedDate ?: "Pick Date 🗓️", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Available order dates chips if any
                    if (availableOrderDates.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            availableOrderDates.take(3).forEach { d ->
                                FilterChip(
                                    selected = selectedDateFilter == d,
                                    onClick = { selectedDateFilter = d; customPickedDate = d },
                                    label = { Text(d, fontSize = 9.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFE65100),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFF1E293B),
                                        labelColor = Color.Gray
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // STAGE BOXES PIPELINE BAR
        item {
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Text("📦 ORDER STAGE BOXES (ऑर्डर स्थिति बॉक्स):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(6.dp))

                // Scrollable Row of Stage Boxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StageBoxChip(
                        title = "ALL",
                        hindi = "सभी",
                        count = dateFilteredOrders.size,
                        isSelected = selectedStageFilter == "ALL",
                        activeColor = Color(0xFF334155),
                        onClick = { selectedStageFilter = "ALL" },
                        testTag = "stage_box_all"
                    )
                    StageBoxChip(
                        title = "NEW 🛎️",
                        hindi = "नया",
                        count = newOrdersCount,
                        isSelected = selectedStageFilter == "NEW",
                        activeColor = Color(0xFF0288D1),
                        onClick = { selectedStageFilter = "NEW" },
                        testTag = "stage_box_new"
                    )
                    StageBoxChip(
                        title = "CONFIRMED 🟢",
                        hindi = "कन्फर्म",
                        count = confirmedOrdersCount,
                        isSelected = selectedStageFilter == "CONFIRMED",
                        activeColor = VegGreen,
                        onClick = { selectedStageFilter = "CONFIRMED" },
                        testTag = "stage_box_confirmed"
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StageBoxChip(
                        title = "PREPARING 🍳",
                        hindi = "तैयारी",
                        count = prepOrdersCount,
                        isSelected = selectedStageFilter == "PREPARING",
                        activeColor = SaffronPrimary,
                        onClick = { selectedStageFilter = "PREPARING" },
                        testTag = "stage_box_prep"
                    )
                    StageBoxChip(
                        title = "READY 🍱",
                        hindi = "तैयार",
                        count = readyOrdersCount,
                        isSelected = selectedStageFilter == "READY",
                        activeColor = Color(0xFF7C3AED),
                        onClick = { selectedStageFilter = "READY" },
                        testTag = "stage_box_ready"
                    )
                    StageBoxChip(
                        title = "DISPATCH 🚚",
                        hindi = "रवाना",
                        count = outForDeliveryOrdersCount,
                        isSelected = selectedStageFilter == "OUT_FOR_DELIVERY",
                        activeColor = AmberSecondary,
                        onClick = { selectedStageFilter = "OUT_FOR_DELIVERY" },
                        testTag = "stage_box_dispatch"
                    )
                    StageBoxChip(
                        title = "DELIVERED ✅",
                        hindi = "डिलीवर",
                        count = deliveredOrdersCount,
                        isSelected = selectedStageFilter == "DELIVERED",
                        activeColor = VegGreen,
                        onClick = { selectedStageFilter = "DELIVERED" },
                        testTag = "stage_box_delivered"
                    )
                }
            }
        }

        // Active Stage Banner
        item {
            Surface(
                color = when (selectedStageFilter) {
                    "NEW" -> Color(0xFFE0F2FE)
                    "CONFIRMED" -> Color(0xFFE8F5E9)
                    "PREPARING" -> Color(0xFFFFF3E0)
                    "READY" -> Color(0xFFF3E8FF)
                    "OUT_FOR_DELIVERY" -> Color(0xFFFFF8E1)
                    "DELIVERED" -> Color(0xFFDCFCE7)
                    else -> Color(0xFFF1F5F9)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (selectedStageFilter) {
                            "NEW" -> "🛎️ 1. NEW ORDERS BOX: Accept to confirm order for the target event date"
                            "CONFIRMED" -> "🟢 2. CONFIRMED BOX: Press 'Preparation' to begin kitchen cooking"
                            "PREPARING" -> "🍳 3. PREPARATION BOX: Press 'Ready' when dishes & degs are cooked"
                            "READY" -> "🍱 4. READY BOX: Press 'Assign' to select Delivery Boy for pickup"
                            "OUT_FOR_DELIVERY" -> "🚚 5. OUT FOR DELIVERY BOX: Driver in transit, press 'Mark Delivered' when done"
                            "DELIVERED" -> "✅ 6. DELIVERED BOX: Successfully delivered & settled orders"
                            else -> "📋 ALL ORDERS IN PIPELINE (${finalOrders.size} active)"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Empty state
        if (finalOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No orders in this box for $selectedDateFilter", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Try switching date filters (Today, Tomorrow, All Dates) or stage tabs.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        // ORDERS LIST
        items(finalOrders) { order ->
            val isExpanded = expandedOrderIds.contains(order.orderId)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { showDetailDialogForOrder = order }
                    .testTag("kitchen_order_${order.orderId}"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header: Order ID + Status Badge + Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Order #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SaffronPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(onClick = { showKotDialogForOrder = order }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Print, contentDescription = "Print KOT", tint = Color.Gray)
                            }
                        }

                        Surface(
                            color = when (order.orderStatus) {
                                OrderStatus.NEW -> Color(0xFFE0F2FE)
                                OrderStatus.CONFIRMED, OrderStatus.ACCEPTED -> Color(0xFFE8F5E9)
                                OrderStatus.PREPARING -> Color(0xFFFFF3E0)
                                OrderStatus.READY -> Color(0xFFF3E8FF)
                                OrderStatus.ASSIGNED_DELIVERY, OrderStatus.OUT_FOR_DELIVERY -> AmberSecondary
                                OrderStatus.DELIVERED -> VegGreen
                                else -> Color(0xFFF1F5F9)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = when (order.orderStatus) {
                                    OrderStatus.NEW -> "NEW 🛎️"
                                    OrderStatus.CONFIRMED, OrderStatus.ACCEPTED -> "CONFIRMED 🟢"
                                    OrderStatus.PREPARING -> "COOKING 🍳"
                                    OrderStatus.READY -> "READY 🍱"
                                    OrderStatus.ASSIGNED_DELIVERY, OrderStatus.OUT_FOR_DELIVERY -> "OUT FOR DELIVERY 🚚"
                                    OrderStatus.DELIVERED -> "DELIVERED ✅"
                                    else -> order.orderStatus.name
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (order.orderStatus == OrderStatus.DELIVERED) Color.White else Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Date & Time Banner
                    Surface(
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "📅 Event Date: ${order.deliveryDate} ${if (order.deliveryDate == todayDateStr || order.deliveryDate == "2026-07-25") "(TODAY)" else if (order.deliveryDate == tomorrowDateStr || order.deliveryDate == "2026-07-26") "(TOMORROW)" else ""}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )
                            }
                            Text("⏰ Slot: ${order.deliveryTimeSlot}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Customer & Venue Info
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Customer: ${order.customerName}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = VegGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(order.customerMobile, fontSize = 11.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("Venue: ${order.deliveryAddress}", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))

                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("Dishes: ${order.itemsSummary}", fontSize = 12.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium, modifier = Modifier.padding(8.dp))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Financial Summary
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Total: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Adv: ₹${order.advancePaidAmount.toInt()}", fontSize = 11.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                            Text("Bal: ₹${order.balanceAmount.toInt()}", fontSize = 11.sp, color = if (order.balanceAmount > 0) Color(0xFFD32F2F) else VegGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Assigned Driver if in Out for Delivery / Ready
                    if (order.deliveryBoyName != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Delivery Partner: ${order.deliveryBoyName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                                }
                                Text(order.deliveryBoyMobile ?: "", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Expandable Details (Inline)
                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("📋 DETAILED BREAKDOWN:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text("• Payment Mode: ${order.paymentMethod.name} (${order.paymentStatus.name})", fontSize = 10.sp, color = Color.DarkGray)
                            Text("• OTP for Delivery: ${order.deliveryOtp}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                            if (order.isBartanPending) {
                                Text("• Metal Handi / Degs: ${order.bartanDescription}", fontSize = 10.sp, color = Color(0xFFB45309))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ACTION BUTTONS (Universal Order Detail + Step-by-Step Lifecycle Button)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. "View Order Details" Button (AVAILABLE IN EVERY BOX!)
                        OutlinedButton(
                            onClick = { showDetailDialogForOrder = order },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("view_details_${order.orderId}"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Details 👁️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // 2. Step Lifecycle Action Button
                        when (order.orderStatus) {
                            OrderStatus.NEW -> {
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order.orderId, OrderStatus.CONFIRMED) },
                                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .testTag("accept_order_${order.orderId}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Accept Order ✅", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            OrderStatus.CONFIRMED, OrderStatus.ACCEPTED -> {
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order.orderId, OrderStatus.PREPARING) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .testTag("start_prep_${order.orderId}")
                                ) {
                                    Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Preparation 🍳", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            OrderStatus.PREPARING -> {
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order.orderId, OrderStatus.READY) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .testTag("mark_ready_${order.orderId}")
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Food Ready 🍱", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            OrderStatus.READY -> {
                                Button(
                                    onClick = { showAssignDialogForOrder = order },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .testTag("assign_delivery_${order.orderId}")
                                ) {
                                    Icon(Icons.Default.DeliveryDining, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Assign Driver 🚴", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            OrderStatus.ASSIGNED_DELIVERY, OrderStatus.OUT_FOR_DELIVERY -> {
                                Button(
                                    onClick = { viewModel.updateOrderStatus(order.orderId, OrderStatus.DELIVERED) },
                                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .testTag("mark_delivered_${order.orderId}")
                                ) {
                                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Mark Delivered ✅", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            OrderStatus.DELIVERED -> {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VegGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Completed & Settled", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    // 1. ORDER DETAIL MODAL (Available for every box!)
    if (showDetailDialogForOrder != null) {
        val order = showDetailDialogForOrder!!
        KitchenOrderDetailDialog(
            order = order,
            deliveryBoys = deliveryBoys,
            onDismiss = { showDetailDialogForOrder = null },
            onUpdateStatus = { newStatus ->
                viewModel.updateOrderStatus(order.orderId, newStatus)
                showDetailDialogForOrder = null
            },
            onOpenAssignDialog = {
                showDetailDialogForOrder = null
                showAssignDialogForOrder = order
            },
            onPrintKot = {
                showDetailDialogForOrder = null
                showKotDialogForOrder = order
            }
        )
    }

    // 2. ASSIGN DELIVERY BOY MODAL
    if (showAssignDialogForOrder != null) {
        val targetOrder = showAssignDialogForOrder!!
        AlertDialog(
            onDismissRequest = { showAssignDialogForOrder = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Assign Delivery Boy to #${targetOrder.orderId}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Select available delivery partner for pickup:", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))

                    if (deliveryBoys.isEmpty()) {
                        Text("No registered delivery partners found. Please add in staff tab.", color = Color.Red, fontSize = 12.sp)
                    }

                    deliveryBoys.forEach { boy ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.assignDeliveryBoy(targetOrder.orderId, boy)
                                    // Also set order status directly to OUT_FOR_DELIVERY
                                    viewModel.updateOrderStatus(targetOrder.orderId, OrderStatus.OUT_FOR_DELIVERY)
                                    showAssignDialogForOrder = null
                                    Toast.makeText(context, "🚴 Assigned ${boy.name}! Order moved to Out for Delivery.", Toast.LENGTH_SHORT).show()
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFE0F2FE),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF0288D1),
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(boy.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
                                    Text("📞 ${boy.mobile} | Aadhaar: Verified ✅", fontSize = 11.sp, color = Color.Gray)
                                    Text("Completed Today: ${boy.todayCompletedDeliveries} | Status: ${if (boy.isBusy) "On Delivery 🚚" else "Available 🟢"}", fontSize = 10.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        viewModel.assignDeliveryBoy(targetOrder.orderId, boy)
                                        viewModel.updateOrderStatus(targetOrder.orderId, OrderStatus.OUT_FOR_DELIVERY)
                                        showAssignDialogForOrder = null
                                        Toast.makeText(context, "🚴 Assigned ${boy.name}!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("assign_boy_${boy.id}")
                                ) {
                                    Text("Assign", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAssignDialogForOrder = null }) { Text("Cancel") }
            }
        )
    }

    // 3. KITCHEN ORDER TOKEN (KOT) PREVIEW
    if (showKotDialogForOrder != null) {
        val order = showKotDialogForOrder!!
        AlertDialog(
            onDismissRequest = { showKotDialogForOrder = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kitchen Order Token (KOT)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)), shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("A1 HUMA KITCHEN - PRODUCTION KOT", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                        Text("ORDER ID: #${order.orderId}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        Text("SLOT: ${order.deliveryDate} (${order.deliveryTimeSlot})", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Divider(modifier = Modifier.padding(vertical = 6.dp))
                        Text("DISHES TO PREPARE:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                        Text(order.itemsSummary, fontSize = 13.sp, color = Color.DarkGray)
                        Divider(modifier = Modifier.padding(vertical = 6.dp))
                        Text("CUSTOMER: ${order.customerName} (${order.customerMobile})", fontSize = 11.sp)
                        Text("VENUE: ${order.deliveryAddress}", fontSize = 11.sp)
                        Text("PAYMENT: Total ₹${order.totalAmount.toInt()} | Adv: ₹${order.advancePaidAmount.toInt()} | Bal: ₹${order.balanceAmount.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showKotDialogForOrder = null
                        printTodayOrdersHtml(context, listOf(order), "A1 Huma Kitchen KOT")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Print KOT Slip 🖨️")
                }
            },
            dismissButton = {
                TextButton(onClick = { showKotDialogForOrder = null }) { Text("Close") }
            }
        )
    }

    // 4. PRINT TODAY'S SUMMARY DIALOG
    if (showPrintSummaryDialog) {
        val totalRev = dateFilteredOrders.sumOf { it.totalAmount }
        val totalAdv = dateFilteredOrders.sumOf { it.advancePaidAmount }
        val totalBal = dateFilteredOrders.sumOf { it.balanceAmount }

        AlertDialog(
            onDismissRequest = { showPrintSummaryDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kitchen Production & Orders Manifest", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.height(380.dp)) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7)), shape = RoundedCornerShape(8.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("A1 HUMA CATERERS - PRODUCTION MANIFEST", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                                Text("DATE FILTER: $selectedDateFilter | TOTAL BOOKINGS: ${dateFilteredOrders.size}", fontSize = 10.sp, color = Color.DarkGray)
                                Divider(modifier = Modifier.padding(vertical = 6.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total Orders: ${dateFilteredOrders.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Revenue: ₹${totalRev.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Advance Paid: ₹${totalAdv.toInt()}", fontSize = 11.sp, color = VegGreen)
                                    Text("Balance Due: ₹${totalBal.toInt()}", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                                }

                                Divider(modifier = Modifier.padding(vertical = 6.dp))
                                Text("ITEMIZED ORDERS LIST:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                                dateFilteredOrders.forEachIndexed { idx, o ->
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text("${idx + 1}. #${o.orderId} - ${o.customerName} (${o.customerMobile}) [${o.orderStatus.name}]", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("⏰ Date & Slot: ${o.deliveryDate} (${o.deliveryTimeSlot})", fontSize = 10.sp, color = SaffronPrimary)
                                        Text("📍 Venue: ${o.deliveryAddress}", fontSize = 10.sp, color = Color.Gray)
                                        Text("🍲 Menu: ${o.itemsSummary}", fontSize = 10.sp, color = Color.DarkGray)
                                        Text("💰 Total: ₹${o.totalAmount.toInt()} | Adv: ₹${o.advancePaidAmount.toInt()} | Bal: ₹${o.balanceAmount.toInt()}", fontSize = 10.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                        Divider(modifier = Modifier.padding(top = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPrintSummaryDialog = false
                        printTodayOrdersHtml(context, dateFilteredOrders, "A1 Huma Caterers")
                        Toast.makeText(context, "🖨️ Opening Android Printer Service...", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Print via Printer 🖨️")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPrintSummaryDialog = false }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun StageBoxChip(
    title: String,
    hindi: String,
    count: Int,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) activeColor else Color.White,
            contentColor = if (isSelected) Color.White else Color(0xFF1E293B)
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    color = if (isSelected) Color.White.copy(alpha = 0.3f) else activeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "$count",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else activeColor,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
            Text(hindi, fontSize = 9.sp, color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray)
        }
    }
}

@Composable
fun KitchenOrderDetailDialog(
    order: OrderEntity,
    deliveryBoys: List<DeliveryBoyEntity>,
    onDismiss: () -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit,
    onOpenAssignDialog: () -> Unit,
    onPrintKot: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Order #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Surface(
                    color = when (order.orderStatus) {
                        OrderStatus.NEW -> Color(0xFFE0F2FE)
                        OrderStatus.CONFIRMED, OrderStatus.ACCEPTED -> Color(0xFFE8F5E9)
                        OrderStatus.PREPARING -> Color(0xFFFFF3E0)
                        OrderStatus.READY -> Color(0xFFF3E8FF)
                        OrderStatus.ASSIGNED_DELIVERY, OrderStatus.OUT_FOR_DELIVERY -> AmberSecondary
                        OrderStatus.DELIVERED -> VegGreen
                        else -> Color(0xFFF1F5F9)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        order.orderStatus.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (order.orderStatus == OrderStatus.DELIVERED) Color.White else Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            LazyColumn(modifier = Modifier.height(420.dp)) {
                // 1. Visual 6-Step Progress Pipeline
                item {
                    Text("KITCHEN PIPELINE STATUS TRACKER:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(6.dp))

                    val steps = listOf(
                        Triple("1. New 🛎️", order.orderStatus == OrderStatus.NEW, OrderStatus.NEW),
                        Triple("2. Confirmed 🟢", order.orderStatus == OrderStatus.CONFIRMED || order.orderStatus == OrderStatus.ACCEPTED, OrderStatus.CONFIRMED),
                        Triple("3. Preparing 🍳", order.orderStatus == OrderStatus.PREPARING, OrderStatus.PREPARING),
                        Triple("4. Ready 🍱", order.orderStatus == OrderStatus.READY, OrderStatus.READY),
                        Triple("5. Dispatch 🚚", order.orderStatus == OrderStatus.OUT_FOR_DELIVERY || order.orderStatus == OrderStatus.ASSIGNED_DELIVERY, OrderStatus.OUT_FOR_DELIVERY),
                        Triple("6. Delivered ✅", order.orderStatus == OrderStatus.DELIVERED, OrderStatus.DELIVERED)
                    )

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            steps.forEach { (label, isActive, status) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (isActive) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = if (isActive) VegGreen else Color.LightGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isActive) Color(0xFF1E293B) else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Customer & Event Details
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Customer: ${order.customerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.customerMobile}"))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call Customer", fontSize = 10.sp)
                                }
                            }
                            Text("Mobile: ${order.customerMobile}", fontSize = 11.sp, color = Color.DarkGray)
                            Text("📍 Delivery Address: ${order.deliveryAddress}", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("📅 Event Date: ${order.deliveryDate} | ⏰ Slot: ${order.deliveryTimeSlot}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        }
                    }
                }

                // 3. Menu Breakdown
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("🍲 ORDERED DISHES & QUANTITIES:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E293B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(order.itemsSummary, fontSize = 12.sp, color = Color.DarkGray)

                            if (order.isBartanPending) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("🍲 Metal Bartan / Degs: ${order.bartanDescription}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            }
                        }
                    }
                }

                // 4. Financial & Payment Summary
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("💰 BILLING & PAYMENT SETTLEMENT:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E293B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Bill Amount:", fontSize = 11.sp)
                                Text("₹${order.totalAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Advance Paid (${order.paymentMethod.name}):", fontSize = 11.sp, color = VegGreen)
                                Text("₹${order.advancePaidAmount.toInt()}", fontSize = 11.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Balance Due upon Delivery:", fontSize = 11.sp, color = if (order.balanceAmount > 0) Color.Red else VegGreen)
                                Text("₹${order.balanceAmount.toInt()}", fontSize = 12.sp, color = if (order.balanceAmount > 0) Color.Red else VegGreen, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("🔐 Delivery OTP: ${order.deliveryOtp}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                        }
                    }
                }

                // 5. Delivery Partner details (if assigned)
                if (order.deliveryBoyName != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("🚴 ASSIGNED DELIVERY PARTNER:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0288D1))
                                Text("Name: ${order.deliveryBoyName}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Mobile: ${order.deliveryBoyMobile}", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // 6. Direct Stage Action Button inside Modal
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("ADVANCE TO NEXT STAGE (अगले चरण में भेजें):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))

                    when (order.orderStatus) {
                        OrderStatus.NEW -> {
                            Button(
                                onClick = { onUpdateStatus(OrderStatus.CONFIRMED) },
                                colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("✅ Accept & Move to Confirmed Box")
                            }
                        }
                        OrderStatus.CONFIRMED, OrderStatus.ACCEPTED -> {
                            Button(
                                onClick = { onUpdateStatus(OrderStatus.PREPARING) },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🍳 Start Preparation (Move to Prep Box)")
                            }
                        }
                        OrderStatus.PREPARING -> {
                            Button(
                                onClick = { onUpdateStatus(OrderStatus.READY) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🍱 Food Ready (Move to Ready Box)")
                            }
                        }
                        OrderStatus.READY -> {
                            Button(
                                onClick = onOpenAssignDialog,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🚴 Assign Delivery Boy (Move to Dispatch)")
                            }
                        }
                        OrderStatus.ASSIGNED_DELIVERY, OrderStatus.OUT_FOR_DELIVERY -> {
                            Button(
                                onClick = { onUpdateStatus(OrderStatus.DELIVERED) },
                                colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("✅ Mark Delivered (Move to Delivered Box)")
                            }
                        }
                        OrderStatus.DELIVERED -> {
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "🎉 Order Successfully Completed & Settled",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VegGreen,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onPrintKot,
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Print KOT 🖨️")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

private fun printTodayOrdersHtml(context: Context, orders: List<OrderEntity>, kitchenName: String) {
    val dateStr = "2026-07-25"
    val html = StringBuilder().apply {
        append("<!DOCTYPE html><html><head><meta charset='utf-8'/><style>")
        append("body { font-family: 'Helvetica Neue', Arial, sans-serif; padding: 20px; color: #111; line-height: 1.4; }")
        append(".header { text-align: center; border-bottom: 2px solid #E65100; padding-bottom: 10px; margin-bottom: 15px; }")
        append(".company { font-size: 24px; font-weight: bold; color: #E65100; }")
        append(".sub { font-size: 12px; color: #555; margin-top: 2px; }")
        append(".summary-box { background: #FFF3E0; border: 1px solid #FFE082; padding: 12px; border-radius: 6px; margin-bottom: 15px; font-size: 13px; font-weight: bold; }")
        append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }")
        append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; font-size: 12px; }")
        append("th { background-color: #FFF3E0; color: #E65100; font-weight: bold; }")
        append(".card { border: 1px solid #ccc; border-radius: 6px; padding: 12px; margin-bottom: 12px; background: #fafafa; page-break-inside: avoid; }")
        append(".card-title { font-weight: bold; color: #1E293B; font-size: 14px; margin-bottom: 4px; }")
        append(".footer { text-align: center; margin-top: 25px; font-size: 11px; color: #777; border-top: 1px dashed #ccc; padding-top: 10px; }")
        append("</style></head><body>")
        append("<div class='header'>")
        append("<div class='company'>$kitchenName</div>")
        append("<div class='sub'>TODAY PRODUCTION MANIFEST & ORDERS SUMMARY</div>")
        append("<div class='sub'>Date: $dateStr | FSSAI Lic No: 23319008000123 | Contact: +91 98765 43210</div>")
        append("</div>")

        val totalRev = orders.sumOf { it.totalAmount }
        val totalAdv = orders.sumOf { it.advancePaidAmount }
        val totalBal = orders.sumOf { it.balanceAmount }

        append("<div class='summary-box'>")
        append("Active Orders Today: ${orders.size} &nbsp;|&nbsp; Total Revenue: ₹${totalRev.toInt()} &nbsp;|&nbsp; Advance Received: ₹${totalAdv.toInt()} &nbsp;|&nbsp; Balance Due: ₹${totalBal.toInt()}")
        append("</div>")

        append("<h3 style='color:#E65100; margin-bottom:8px;'>📦 KITCHEN AGGREGATED PRODUCTION DISHES:</h3>")
        append("<table>")
        append("<tr><th>Dish / Menu Item Name</th><th>Total Quantity Required</th><th>Status</th></tr>")
        append("<tr><td><b>Special Chicken Biryani</b></td><td><b>42 Kg</b></td><td>COOKING ⏳</td></tr>")
        append("<tr><td><b>Royal Hyderabadi Mutton Biryani</b></td><td><b>21 Kg</b></td><td>COOKING ⏳</td></tr>")
        append("<tr><td><b>Shahi Paneer Dum Biryani</b></td><td><b>18 Kg</b></td><td>COOKING ⏳</td></tr>")
        append("<tr><td><b>Tandoori Chicken Starter</b></td><td><b>14 Dozen</b></td><td>PREPARING ⏳</td></tr>")
        append("<tr><td><b>Shahi Zafrani Kheer</b></td><td><b>35 Litre</b></td><td>COOKED ✅</td></tr>")
        append("<tr><td><b>Soft Gulab Jamun</b></td><td><b>28 Dozen</b></td><td>COOKED ✅</td></tr>")
        append("</table>")

        append("<h3 style='color:#E65100; margin-top:20px; margin-bottom:8px;'>📋 ITEMIZED BOOKINGS BREAKDOWN:</h3>")
        orders.forEach { o ->
            append("<div class='card'>")
            append("<div class='card-title'>Order #${o.orderId} — Customer: ${o.customerName} (${o.customerMobile})</div>")
            append("<div style='margin-bottom:4px;'><b>Delivery Address:</b> ${o.deliveryAddress} &nbsp;|&nbsp; <b>Time Slot:</b> ${o.deliveryTimeSlot}</div>")
            append("<div style='background:#fff; border:1px solid #eee; padding:8px; margin:6px 0; border-radius:4px;'><b>Items Ordered:</b> ${o.itemsSummary}</div>")
            append("<div><b>Total Bill:</b> ₹${o.totalAmount.toInt()} &nbsp;|&nbsp; <span style='color:#2E7D32'><b>Advance Paid:</b> ₹${o.advancePaidAmount.toInt()}</span> &nbsp;|&nbsp; <span style='color:#D32F2F'><b>Remaining Due:</b> ₹${o.balanceAmount.toInt()}</span></div>")
            append("</div>")
        }

        append("<div class='footer'>--- END OF PRODUCTION MANIFEST REPORT — A1 HUMA CATERERS ---</div>")
        append("</body></html>")
    }.toString()

    try {
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                val printAdapter = webView.createPrintDocumentAdapter("Today_Orders_$dateStr")
                printManager?.print("Today_Orders_$dateStr", printAdapter, PrintAttributes.Builder().build())
            }
        }
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun KitchenRawMaterialScreen(viewModel: CaterersViewModel) {
    var guestCountInput by remember { mutableStateOf("250") }
    val count = guestCountInput.toIntOrNull() ?: 250

    // Auto-calculated Rashan / Raw Material formula
    val rawMaterials = listOf(
        Triple("Basmati Rice (Biryani)", "${(count * 0.15).toInt()} Kg", "In Stock (80 Kg)"),
        Triple("Chicken / Mutton Meat", "${(count * 0.20).toInt()} Kg", "Need to Order"),
        Triple("Fresh Dairy Paneer", "${(count * 0.08).toInt()} Kg", "In Stock (25 Kg)"),
        Triple("Shahi Desi Ghee & Oil", "${(count * 0.05).toInt()} Litre", "In Stock (20 L)"),
        Triple("Biryani Spices & Whole Masala", "${(count * 0.02).toInt()} Kg", "In Stock"),
        Triple("Onions & Fresh Garlic Ginger", "${(count * 0.12).toInt()} Kg", "Need to Order"),
        Triple("Commercial LPG Gas Cylinders", "${(count / 100) + 1} Cylinders", "2 Ready")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = AmberSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rashan & Raw Material Calculator", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text("Auto-calculates bulk ingredients required based on guest headcount.", color = Color.LightGray, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = guestCountInput,
                        onValueChange = { guestCountInput = it },
                        label = { Text("Total Event Guest Count", color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberSecondary,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = AmberSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Required Ingredients Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                Button(
                    onClick = { /* Share Rashan Parcha */ },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    modifier = Modifier.testTag("download_rashan_list_button")
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share Rashan Parcha", fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(rawMaterials) { (item, reqQty, stockStatus) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                        Text("Status: $stockStatus", fontSize = 11.sp, color = if (stockStatus.contains("In Stock")) VegGreen else SaffronPrimary)
                    }

                    Surface(color = Color(0xFF1E293B), shape = RoundedCornerShape(6.dp)) {
                        Text(reqQty, color = AmberSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun KitchenStaffManagementScreen(viewModel: CaterersViewModel) {
    val staffList = remember {
        mutableStateListOf(
            Triple("Ustad Rashid Khan", "Head Chef (Main Halwai)", "₹2,500/day"),
            Triple("Mohd Salim", "Assistant Cook", "₹1,200/day"),
            Triple("Amaan Sheikh", "Helper & Dish Washer", "₹800/day"),
            Triple("Vicky Kumar", "Head Waiter Supervisor", "₹1,000/day")
        )
    }

    var showAddStaffDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Kitchen Staff & Halwai Team", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                    Text("Manage chefs, karigars, and daily wages", fontSize = 11.sp, color = Color.Gray)
                }
                Button(
                    onClick = { showAddStaffDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    modifier = Modifier.testTag("add_staff_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Staff", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(staffList) { (name, role, wage) ->
            var isPresent by remember { mutableStateOf(true) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(SaffronPrimary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SaffronPrimary)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                            Text(role, fontSize = 11.sp, color = Color.Gray)
                            Text("Wage: $wage", fontSize = 11.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Switch(
                            checked = isPresent,
                            onCheckedChange = { isPresent = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = VegGreen)
                        )
                        Text(if (isPresent) "PRESENT" else "ABSENT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isPresent) VegGreen else Color.Red)
                    }
                }
            }
        }
    }

    if (showAddStaffDialog) {
        var staffName by remember { mutableStateOf("") }
        var staffRole by remember { mutableStateOf("Karigar Cook") }
        var staffWage by remember { mutableStateOf("1200") }

        AlertDialog(
            onDismissRequest = { showAddStaffDialog = false },
            title = { Text("Register New Kitchen Staff / Karigar") },
            text = {
                Column {
                    OutlinedTextField(value = staffName, onValueChange = { staffName = it }, label = { Text("Staff Full Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = staffRole, onValueChange = { staffRole = it }, label = { Text("Designation / Role") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = staffWage, onValueChange = { staffWage = it }, label = { Text("Daily Wage Rate (₹)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (staffName.isNotBlank()) {
                            staffList.add(Triple(staffName, staffRole, "₹$staffWage/day"))
                            showAddStaffDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Save Staff Member")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStaffDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun KitchenProductionScreen(viewModel: CaterersViewModel) {
    val context = LocalContext.current
    val orders by viewModel.ordersList.collectAsState()
    val activeOrders = orders.filter { it.orderStatus == OrderStatus.CONFIRMED || it.orderStatus == OrderStatus.PREPARING || it.orderStatus == OrderStatus.NEW }

    val productionList = listOf(
        Pair("Special Chicken Biryani", "42 Kg"),
        Pair("Royal Hyderabadi Mutton Biryani", "21 Kg"),
        Pair("Shahi Paneer Dum Biryani", "18 Kg"),
        Pair("Tandoori Chicken Starter", "14 Dozen"),
        Pair("Shahi Zafrani Kheer", "35 Litre"),
        Pair("Soft Gulab Jamun", "28 Dozen")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.SoupKitchen, contentDescription = null, tint = AmberSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Today's Production Aggregation (प्रोडक्शन समरी)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                    Text("Calculated from ${activeOrders.size} active catering bookings.", color = Color.LightGray, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            printTodayOrdersHtml(context, activeOrders, "A1 Huma Kitchen")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🖨️ PRINT PRODUCTION MANIFEST / HALWAI SLIP", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(productionList) { (item, qty) ->
            var isCompleted by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted) Color(0xFFE8F5E9) else Color.White,
                    contentColor = Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = { isCompleted = it },
                            colors = CheckboxDefaults.colors(checkedColor = VegGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(item, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                    }

                    Surface(color = if (isCompleted) VegGreen else SaffronPrimary, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = if (isCompleted) "COOKED ✅" else qty,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KitchenBartanScreen(viewModel: CaterersViewModel) {
    val bartanRecords by viewModel.bartanRecordsList.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Bartan & Equipment Return Tracker", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Track pending degs, buffet counters & serving utensils with customers", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(bartanRecords) { record ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Order #${record.orderId} - ${record.customerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Utensils: ${record.itemsDescription}", fontSize = 11.sp, color = SaffronPrimary)
                        Text("Delivery Date: ${record.deliveryDate}", fontSize = 11.sp, color = Color.Gray)
                    }

                    if (!record.isCollected) {
                        Button(
                            onClick = { viewModel.markBartanCollected(record.id, "2026-07-25") },
                            colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                            modifier = Modifier.testTag("mark_bartan_collected_${record.id}")
                        ) {
                            Text("Mark Returned", fontSize = 10.sp)
                        }
                    } else {
                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp)) {
                            Text("Collected ✅", color = VegGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KitchenFinanceScreen(viewModel: CaterersViewModel) {
    val orders by viewModel.ordersList.collectAsState()
    val totalRevenue = orders.sumOf { it.totalAmount }

    val rawMaterialExpense = totalRevenue * 0.45 // ~45% cost
    val labourExpense = totalRevenue * 0.12 // ~12% cost
    val transportExpense = totalRevenue * 0.05 // ~5% cost
    val netProfit = totalRevenue - (rawMaterialExpense + labourExpense + transportExpense)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Kitchen Net Profit Calculator", color = Color.LightGray, fontSize = 12.sp)
                    Text("₹${netProfit.toInt()}", color = VegGreen, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Estimated Net Margin: ~38% after Rashan, Gas & Labour costs", color = AmberSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Revenue vs Expense Breakdown", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    FinanceRow("Gross Booking Revenue", "₹${totalRevenue.toInt()}", SaffronPrimary)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    FinanceRow("Raw Material (Rashan) Cost", "- ₹${rawMaterialExpense.toInt()}", Color.Red)
                    FinanceRow("Halwai & Waiter Wages", "- ₹${labourExpense.toInt()}", Color.Red)
                    FinanceRow("Transport & Gas Cylinders", "- ₹${transportExpense.toInt()}", Color.Red)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    FinanceRow("Net Profit", "₹${netProfit.toInt()}", VegGreen)
                }
            }
        }
    }
}

@Composable
private fun FinanceRow(title: String, amount: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(amount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun KitchenMenuManagementScreen(viewModel: CaterersViewModel) {
    val context = LocalContext.current
    val allMenuItems by viewModel.menuItemsList.collectAsState()
    val kitchenMenuItems = allMenuItems.filter { it.catererId == "caterer_1" }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    var isAddDialogOpen by remember { mutableStateOf(false) }
    var editingMenuItem by remember { mutableStateOf<MenuItemEntity?>(null) }
    var deletingMenuItem by remember { mutableStateOf<MenuItemEntity?>(null) }
    var changingPhotoItem by remember { mutableStateOf<MenuItemEntity?>(null) }

    val categories = listOf("All", "Biryani & Rice", "Starters & Snacks", "Main Course Curry", "Breads & Roti", "Desserts & Drinks")

    // Preset high-quality catering food photos
    val presetFoodPhotos = listOf(
        Pair("Chicken Dum Biryani", "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500"),
        Pair("Royal Mutton Biryani", "https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=500"),
        Pair("Shahi Paneer Biryani", "https://images.unsplash.com/photo-1642821373181-696a54913e9a?w=500"),
        Pair("Butter Chicken Gravy", "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=500"),
        Pair("Kadhai Paneer Gravy", "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=500"),
        Pair("Chicken Tikka / Kebab", "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=500"),
        Pair("Paneer Tikka Starter", "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=500"),
        Pair("Tandoori Naan & Roti", "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=500"),
        Pair("Shahi Zafrani Kheer", "https://images.unsplash.com/photo-1541832676-9b763b0239ab?w=500"),
        Pair("Soft Gulab Jamun", "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=500"),
        Pair("Mint Lassi / Jaljeera", "https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=500"),
        Pair("Veg Hakka Noodles", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500"),
        Pair("Catering Deg & Bartan", "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=500")
    )

    // Quick Photo update launchers for list items
    val quickItemPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            changingPhotoItem?.let { item ->
                viewModel.updateMenuItem(item.copy(imageUrl = uri.toString()))
                Toast.makeText(context, "✅ Dish photo updated for ${item.name}!", Toast.LENGTH_SHORT).show()
            }
            changingPhotoItem = null
        }
    }

    val filteredItems = kitchenMenuItems.filter { item ->
        val matchesCategory = if (selectedCategory == "All") true else item.category.contains(selectedCategory.split(" ").first(), ignoreCase = true)
        val matchesSearch = if (searchQuery.isBlank()) true else item.name.contains(searchQuery, ignoreCase = true) || item.category.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SaffronPrimary, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Menu & Dish Management", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Dishes, Photos, Per Kg rates & stock status", fontSize = 12.sp, color = AmberSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Total Dishes: ${kitchenMenuItems.size} | Active: ${kitchenMenuItems.count { it.isAvailable }}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { isAddDialogOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = SaffronPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("add_menu_item_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Dish", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search menu item by name...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("menu_search_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Scrollable Chips
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .testTag("filter_cat_$cat")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (filteredItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Menu Items Found", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Try searching for another dish or tap 'Add Dish' to create one.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        items(filteredItems) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("menu_item_card_${item.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.isAvailable) Color.White else Color(0xFFF1F5F9),
                    contentColor = Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            // Dish Image Thumbnail with 1-tap change photo badge
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFFFF3E0))
                                    .border(1.dp, Color(0xFFFFE0B2), RoundedCornerShape(10.dp))
                                    .clickable { changingPhotoItem = item },
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
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                // Camera icon badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(topStart = 6.dp))
                                        .padding(3.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = "Change photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    VegNonVegBadge(foodType = item.foodType)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(4.dp)) {
                                    Text(item.category, fontSize = 10.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        // Availability Toggle Switch
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                if (item.isAvailable) "Available" else "Stock Out",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isAvailable) VegGreen else Color.Red
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = item.isAvailable,
                                onCheckedChange = { viewModel.updateMenuItem(item.copy(isAvailable = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = VegGreen, checkedTrackColor = Color(0xFFC8E6C9)),
                                modifier = Modifier.testTag("availability_switch_${item.id}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(item.description, fontSize = 12.sp, color = Color.Gray, maxLines = 2)

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("₹${item.pricePerUnit.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = VegGreen)
                                Text(" / ${item.unitType.name.lowercase().replaceFirstChar { it.uppercase() }}", fontSize = 12.sp, color = Color.DarkGray)
                            }
                            Text("Min Order: ${item.minQuantity} ${item.unitType.name.lowercase()}", fontSize = 11.sp, color = Color.Gray)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { changingPhotoItem = item },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Photo", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { editingMenuItem = item },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("edit_menu_item_${item.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { deletingMenuItem = item },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("delete_menu_item_${item.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(13.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Quick Change Photo Dialog
    if (changingPhotoItem != null) {
        val target = changingPhotoItem!!
        var currentUrl by remember { mutableStateOf(target.imageUrl) }
        var urlInput by remember { mutableStateOf(target.imageUrl) }

        val singleDishImagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                currentUrl = uri.toString()
                viewModel.updateMenuItem(target.copy(imageUrl = uri.toString()))
                Toast.makeText(context, "✅ Dish photo updated!", Toast.LENGTH_SHORT).show()
                changingPhotoItem = null
            }
        }

        AlertDialog(
            onDismissRequest = { changingPhotoItem = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dish Photo: ${target.name}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.height(380.dp)) {
                    item {
                        // Current Image Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFF3E0))
                                .border(1.dp, Color(0xFFFFE0B2), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentUrl.isNotBlank()) {
                                AsyncImage(
                                    model = currentUrl,
                                    contentDescription = target.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Fastfood, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("No Photo Added Yet", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Upload from phone gallery button
                        Button(
                            onClick = {
                                singleDishImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("📱 Gallery se Photo Dalein", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Ya Preset Food Photos se Chunein:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.height(6.dp))

                        // Grid of Presets
                        presetFoodPhotos.forEach { (presetName, presetUrl) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (currentUrl == presetUrl) Color(0xFFFFF3E0) else Color(0xFFF8FAFC))
                                    .border(1.dp, if (currentUrl == presetUrl) SaffronPrimary else Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                    .clickable {
                                        currentUrl = presetUrl
                                        viewModel.updateMenuItem(target.copy(imageUrl = presetUrl))
                                        Toast.makeText(context, "✅ Preset photo applied: $presetName", Toast.LENGTH_SHORT).show()
                                        changingPhotoItem = null
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE2E8F0))
                                ) {
                                    AsyncImage(
                                        model = presetUrl,
                                        contentDescription = presetName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(presetName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B), modifier = Modifier.weight(1f))
                                if (currentUrl == presetUrl) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            label = { Text("Or Paste Image Link / URL", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (urlInput.isNotBlank()) {
                            viewModel.updateMenuItem(target.copy(imageUrl = urlInput))
                            Toast.makeText(context, "✅ Image link saved!", Toast.LENGTH_SHORT).show()
                        }
                        changingPhotoItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Save URL")
                }
            },
            dismissButton = {
                TextButton(onClick = { changingPhotoItem = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Add / Edit Dialog
    if (isAddDialogOpen || editingMenuItem != null) {
        val isEditing = editingMenuItem != null
        val target = editingMenuItem

        var name by remember { mutableStateOf(target?.name ?: "") }
        var category by remember { mutableStateOf(target?.category ?: "Biryani & Rice") }
        var description by remember { mutableStateOf(target?.description ?: "") }
        var imageUrl by remember { mutableStateOf(target?.imageUrl ?: "") }
        var priceStr by remember { mutableStateOf(target?.pricePerUnit?.toString() ?: "450") }
        var minQtyStr by remember { mutableStateOf(target?.minQuantity?.toString() ?: "5.0") }
        var selectedFoodType by remember { mutableStateOf(target?.foodType ?: FoodType.NON_VEG) }
        var selectedUnitType by remember { mutableStateOf(target?.unitType ?: UnitType.KG) }
        var discountType by remember { mutableStateOf(target?.catererDiscountType ?: com.example.data.models.DiscountType.PERCENTAGE) }
        var discountValueStr by remember { mutableStateOf(target?.catererDiscountValue?.toString() ?: "0.0") }

        // Photo Picker inside Add/Edit Dialog
        val dialogPhotoPicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                imageUrl = uri.toString()
                Toast.makeText(context, "📸 Dish photo selected!", Toast.LENGTH_SHORT).show()
            }
        }

        AlertDialog(
            onDismissRequest = {
                isAddDialogOpen = false
                editingMenuItem = null
            },
            title = {
                Text(
                    if (isEditing) "Edit Menu Item" else "Add New Menu Item",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(modifier = Modifier.height(420.dp)) {
                    item {
                        // Dish Photo Picker Section
                        Text("Dish Photo (डिश की फोटो)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF3E0))
                                    .border(1.dp, Color(0xFFFFE0B2), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(Icons.Default.Fastfood, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(28.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Button(
                                    onClick = {
                                        dialogPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Upload Photo 📱", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                if (imageUrl.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    TextButton(
                                        onClick = { imageUrl = "" },
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text("Remove Photo 🗑️", fontSize = 10.sp, color = Color.Red)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Presets selector row
                        Text("Quick Food Presets (त्वरित फोटो चयन):", fontSize = 10.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            presetFoodPhotos.take(6).forEach { (pName, pUrl) ->
                                Surface(
                                    color = if (imageUrl == pUrl) SaffronPrimary.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (imageUrl == pUrl) SaffronPrimary else Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.clickable { imageUrl = pUrl }
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(18.dp).clip(RoundedCornerShape(3.dp))) {
                                            AsyncImage(model = pUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(pName.split(" ").first(), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (imageUrl == pUrl) SaffronPrimary else Color(0xFF475569))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Dish Name (e.g. Shahi Mutton Deg Biryani)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category (Biryani, Starters, Curry, Breads, Desserts)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = priceStr,
                                onValueChange = { priceStr = it },
                                label = { Text("Price (₹)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = minQtyStr,
                                onValueChange = { minQtyStr = it },
                                label = { Text("Min Order Qty") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Food Type", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedFoodType == FoodType.VEG,
                                onClick = { selectedFoodType = FoodType.VEG },
                                label = { Text("Veg 🥬") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = VegGreen, selectedLabelColor = Color.White),
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = selectedFoodType == FoodType.NON_VEG,
                                onClick = { selectedFoodType = FoodType.NON_VEG },
                                label = { Text("Non-Veg 🍗") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color.Red, selectedLabelColor = Color.White),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Unit Type", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            UnitType.values().forEach { u ->
                                FilterChip(
                                    selected = selectedUnitType == u,
                                    onClick = { selectedUnitType = u },
                                    label = { Text(u.name, fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Per Product Discount (प्रोडक्ट डिस्काउंट)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(
                                selected = discountType == com.example.data.models.DiscountType.NONE,
                                onClick = { discountType = com.example.data.models.DiscountType.NONE },
                                label = { Text("No Discount", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = discountType == com.example.data.models.DiscountType.PERCENTAGE,
                                onClick = { discountType = com.example.data.models.DiscountType.PERCENTAGE },
                                label = { Text("Percentage (%)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = discountType == com.example.data.models.DiscountType.FLAT,
                                onClick = { discountType = com.example.data.models.DiscountType.FLAT },
                                label = { Text("Flat Amount (₹)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (discountType != com.example.data.models.DiscountType.NONE) {
                            OutlinedTextField(
                                value = discountValueStr,
                                onValueChange = { discountValueStr = it },
                                label = { Text(if (discountType == com.example.data.models.DiscountType.PERCENTAGE) "Discount Percentage (e.g. 10 for 10%)" else "Discount Flat Amount (e.g. 50 for ₹50)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description & Ingredients") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text("Custom Image Link / URL (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = priceStr.toDoubleOrNull() ?: 300.0
                        val minQty = minQtyStr.toDoubleOrNull() ?: 1.0
                        val discVal = discountValueStr.toDoubleOrNull() ?: 0.0

                        if (isEditing && target != null) {
                            viewModel.updateMenuItem(
                                target.copy(
                                    name = name,
                                    category = category,
                                    description = description,
                                    imageUrl = imageUrl,
                                    pricePerUnit = price,
                                    minQuantity = minQty,
                                    foodType = selectedFoodType,
                                    unitType = selectedUnitType,
                                    catererDiscountType = discountType,
                                    catererDiscountValue = discVal,
                                    discountPercent = if (discountType == com.example.data.models.DiscountType.PERCENTAGE) discVal else 0.0
                                )
                            )
                        } else {
                            val newItem = MenuItemEntity(
                                id = "item_${System.currentTimeMillis()}",
                                catererId = "caterer_1",
                                catererName = "A1 Huma Caterers",
                                category = category,
                                name = name,
                                description = description,
                                imageUrl = imageUrl,
                                foodType = selectedFoodType,
                                unitType = selectedUnitType,
                                pricePerUnit = price,
                                minQuantity = minQty,
                                isAvailable = true,
                                catererDiscountType = discountType,
                                catererDiscountValue = discVal,
                                discountPercent = if (discountType == com.example.data.models.DiscountType.PERCENTAGE) discVal else 0.0
                            )
                            viewModel.addMenuItem(newItem)
                        }
                        isAddDialogOpen = false
                        editingMenuItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text(if (isEditing) "Save Changes" else "Create Item")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isAddDialogOpen = false
                    editingMenuItem = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (deletingMenuItem != null) {
        val item = deletingMenuItem!!
        AlertDialog(
            onDismissRequest = { deletingMenuItem = null },
            title = { Text("Delete '${item.name}'?") },
            text = { Text("Are you sure you want to remove this item from your active catering menu? Customers will no longer be able to order this item.", fontSize = 13.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMenuItem(item.id)
                        deletingMenuItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete Dish 🗑️")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingMenuItem = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun KitchenProfileKycScreen(viewModel: CaterersViewModel) {
    val context = LocalContext.current
    val caterers by viewModel.caterersList.collectAsState()
    val kitchen = caterers.find { it.id == "caterer_1" } ?: caterers.firstOrNull()

    var viewingDoc by remember { mutableStateOf<Pair<String, String>?>(null) }
    var uploadingDocType by remember { mutableStateOf<String?>(null) }
    var isEditingDetails by remember { mutableStateOf(false) }
    var isChangingLogo by remember { mutableStateOf(false) }
    var isChangingBanner by remember { mutableStateOf(false) }

    // Logo Photo Picker
    val logoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && kitchen != null) {
            viewModel.updateCatererDetails(kitchen.copy(logoUrl = uri.toString()))
            Toast.makeText(context, "✅ Kitchen Logo updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Banner Photo Picker
    val bannerPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && kitchen != null) {
            viewModel.updateCatererDetails(kitchen.copy(bannerUrl = uri.toString()))
            Toast.makeText(context, "✅ Kitchen Banner photo updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "doc_${System.currentTimeMillis()}.pdf"
            val fullPath = "uploads/$fileName"
            uploadingDocType?.let { docType ->
                kitchen?.let { k ->
                    viewModel.updateCatererDocuments(
                        catererId = k.id,
                        fssaiDoc = if (docType.contains("FSSAI")) fullPath else k.fssaiDocUrl,
                        aadhaarDoc = if (docType.contains("Aadhaar")) fullPath else k.aadhaarDocUrl,
                        panDoc = if (docType.contains("PAN")) fullPath else k.panDocUrl,
                        bankChequeDoc = if (docType.contains("Bank")) fullPath else k.bankChequeDocUrl,
                        kitchenPhotoDoc = if (docType.contains("Premises")) fullPath else k.kitchenPhotoUrl
                    )
                    Toast.makeText(context, "✅ File selected and uploaded: $fileName", Toast.LENGTH_LONG).show()
                }
            }
            uploadingDocType = null
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        val camFileName = "camera_doc_${System.currentTimeMillis()}.jpg"
        uploadingDocType?.let { docType ->
            kitchen?.let { k ->
                viewModel.updateCatererDocuments(
                    catererId = k.id,
                    fssaiDoc = if (docType.contains("FSSAI")) camFileName else k.fssaiDocUrl,
                    aadhaarDoc = if (docType.contains("Aadhaar")) camFileName else k.aadhaarDocUrl,
                    panDoc = if (docType.contains("PAN")) camFileName else k.panDocUrl,
                    bankChequeDoc = if (docType.contains("Bank")) camFileName else k.bankChequeDocUrl,
                    kitchenPhotoDoc = if (docType.contains("Premises")) camFileName else k.kitchenPhotoUrl
                )
                Toast.makeText(context, "📸 Photo captured and uploaded successfully!", Toast.LENGTH_LONG).show()
            }
        }
        uploadingDocType = null
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                val camFileName = "cam_scan_${System.currentTimeMillis()}.jpg"
                uploadingDocType?.let { docType ->
                    kitchen?.let { k ->
                        viewModel.updateCatererDocuments(
                            catererId = k.id,
                            fssaiDoc = if (docType.contains("FSSAI")) camFileName else k.fssaiDocUrl,
                            aadhaarDoc = if (docType.contains("Aadhaar")) camFileName else k.aadhaarDocUrl,
                            panDoc = if (docType.contains("PAN")) camFileName else k.panDocUrl,
                            bankChequeDoc = if (docType.contains("Bank")) camFileName else k.bankChequeDocUrl,
                            kitchenPhotoDoc = if (docType.contains("Premises")) camFileName else k.kitchenPhotoUrl
                        )
                        Toast.makeText(context, "📸 Photo scan saved: $camFileName", Toast.LENGTH_LONG).show()
                    }
                }
                uploadingDocType = null
            }
        } else {
            val camFileName = "camera_doc_${System.currentTimeMillis()}.jpg"
            uploadingDocType?.let { docType ->
                kitchen?.let { k ->
                    viewModel.updateCatererDocuments(
                        catererId = k.id,
                        fssaiDoc = if (docType.contains("FSSAI")) camFileName else k.fssaiDocUrl,
                        aadhaarDoc = if (docType.contains("Aadhaar")) camFileName else k.aadhaarDocUrl,
                        panDoc = if (docType.contains("PAN")) camFileName else k.panDocUrl,
                        bankChequeDoc = if (docType.contains("Bank")) camFileName else k.bankChequeDocUrl,
                        kitchenPhotoDoc = if (docType.contains("Premises")) camFileName else k.kitchenPhotoUrl
                    )
                    Toast.makeText(context, "📸 Document photo submitted: $camFileName", Toast.LENGTH_LONG).show()
                }
            }
            uploadingDocType = null
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Kitchen Profile & Verification Documents", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
            Text("Manage business details, upload FSSAI & view admin verification", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            // Profile Overview Card with Banner and Logo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    // Kitchen Banner Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(Color(0xFFFFF3E0))
                    ) {
                        if (!kitchen?.bannerUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = kitchen?.bannerUrl,
                                contentDescription = "Kitchen Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(SaffronPrimary.copy(alpha = 0.85f), AmberSecondary.copy(alpha = 0.85f))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Fastfood, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Catering Kitchen Banner", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }

                        // Banner change button
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clickable { isChangingBanner = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Change Banner", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Circular Kitchen Logo
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 16.dp, bottom = 0.dp)
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(2.dp, Color.White, CircleShape)
                                .clickable { isChangingLogo = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!kitchen?.logoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = kitchen?.logoUrl,
                                    contentDescription = "Kitchen Logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(SaffronPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Restaurant,
                                        contentDescription = null,
                                        tint = SaffronPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            // Small edit camera badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(20.dp)
                                    .background(SaffronPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(kitchen?.name ?: "A1 Huma Caterers", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF1E293B))
                                Text("Kitchen: ${kitchen?.kitchenName}", fontSize = 12.sp, color = Color.Gray)
                            }
                            FssaiBadge(licenseNo = kitchen?.fssaiLicense ?: "")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Logo / Banner action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { isChangingLogo = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kitchen Logo 🖼️", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { isChangingBanner = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Kitchen Banner 🏷️", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Banner
                    Surface(
                        color = when (kitchen?.kycStatus) {
                            KycStatus.APPROVED -> Color(0xFFE8F5E9)
                            KycStatus.PENDING -> Color(0xFFFFF3E0)
                            KycStatus.REJECTED -> Color(0xFFFFEBEE)
                            else -> Color(0xFFE8F5E9)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (kitchen?.kycStatus == KycStatus.APPROVED) Icons.Default.Verified else Icons.Default.Description,
                                contentDescription = null,
                                tint = when (kitchen?.kycStatus) {
                                    KycStatus.APPROVED -> VegGreen
                                    KycStatus.PENDING -> SaffronPrimary
                                    KycStatus.REJECTED -> Color.Red
                                    else -> VegGreen
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    when (kitchen?.kycStatus) {
                                        KycStatus.APPROVED -> "Super Admin Verified Kitchen ✅"
                                        KycStatus.PENDING -> "KYC Documents Under Admin Verification ⏳"
                                        KycStatus.REJECTED -> "KYC Verification Rejected ❌"
                                        else -> "Super Admin Verified Kitchen ✅"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = when (kitchen?.kycStatus) {
                                        KycStatus.APPROVED -> VegGreen
                                        KycStatus.PENDING -> SaffronPrimary
                                        KycStatus.REJECTED -> Color.Red
                                        else -> VegGreen
                                    }
                                )
                                Text(kitchen?.kycNotes ?: "All documents verified.", fontSize = 11.sp, color = Color.DarkGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { isEditingDetails = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        modifier = Modifier.fillMaxWidth().testTag("edit_kitchen_info_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Kitchen & Bank Details")
                    }
                }
            }
        }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Uploaded Business & KYC Documents", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
            Text("Click 'View' to open document preview or 'Upload' to update file", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            KycDocumentCard(
                title = "1. FSSAI Food License Certificate",
                docName = kitchen?.fssaiDocUrl ?: "fssai_cert_112233.pdf",
                refNo = kitchen?.fssaiLicense ?: "11223344556677",
                isVerified = kitchen?.isFssaiVerified ?: true,
                onView = { viewingDoc = Pair("FSSAI License Certificate", kitchen?.fssaiDocUrl ?: "fssai_cert_112233.pdf") },
                onUpload = { uploadingDocType = "FSSAI License Certificate" }
            )

            KycDocumentCard(
                title = "2. Proprietor Aadhaar Card",
                docName = kitchen?.aadhaarDocUrl ?: "aadhaar_card_front.jpg",
                refNo = kitchen?.aadhaarNumber ?: "9988 7766 5544",
                isVerified = kitchen?.kycStatus == KycStatus.APPROVED,
                onView = { viewingDoc = Pair("Aadhaar Card", kitchen?.aadhaarDocUrl ?: "aadhaar_card_front.jpg") },
                onUpload = { uploadingDocType = "Aadhaar Card" }
            )

            KycDocumentCard(
                title = "3. Business PAN Card",
                docName = kitchen?.panDocUrl ?: "pan_card_sample.jpg",
                refNo = kitchen?.panNumber ?: "ABCDE1234F",
                isVerified = kitchen?.kycStatus == KycStatus.APPROVED,
                onView = { viewingDoc = Pair("PAN Card", kitchen?.panDocUrl ?: "pan_card_sample.jpg") },
                onUpload = { uploadingDocType = "PAN Card" }
            )

            KycDocumentCard(
                title = "4. Bank Cancelled Cheque / Passbook",
                docName = kitchen?.bankChequeDocUrl ?: "cancelled_cheque.jpg",
                refNo = "${kitchen?.bankAccount} (${kitchen?.bankIfsc})",
                isVerified = kitchen?.kycStatus == KycStatus.APPROVED,
                onView = { viewingDoc = Pair("Bank Cancelled Cheque", kitchen?.bankChequeDocUrl ?: "cancelled_cheque.jpg") },
                onUpload = { uploadingDocType = "Bank Cancelled Cheque" }
            )

            KycDocumentCard(
                title = "5. Kitchen Hygiene & Cooking Area Photo",
                docName = kitchen?.kitchenPhotoUrl ?: "kitchen_sanitation_photo.jpg",
                refNo = "A1 Huma Central Okhla Premises",
                isVerified = kitchen?.kycStatus == KycStatus.APPROVED,
                onView = { viewingDoc = Pair("Kitchen Sanitation Photo", kitchen?.kitchenPhotoUrl ?: "kitchen_sanitation_photo.jpg") },
                onUpload = { uploadingDocType = "Kitchen Premises Photo" }
            )
        }
    }

    // Document Viewer Modal
    if (viewingDoc != null) {
        val (docTitle, docFileName) = viewingDoc!!
        AlertDialog(
            onDismissRequest = { viewingDoc = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(docTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("File Name: $docFileName", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Uploaded: 25 Jul 2026 | Status: Verified by Super Admin", fontSize = 11.sp, color = VegGreen)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Graphic Document Viewer Box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("[ DOCUMENT PREVIEW ]", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(docFileName, color = Color.LightGray, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Official Verified Record - Catering Market Licensing", color = AmberSecondary, fontSize = 10.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewingDoc = null },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Close Document")
                }
            }
        )
    }

    // Upload Document Dialog
    if (uploadingDocType != null) {
        val docType = uploadingDocType!!
        var fileNameInput by remember { mutableStateOf("${docType.lowercase().replace(" ", "_")}_2026.pdf") }

        AlertDialog(
            onDismissRequest = { uploadingDocType = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload $docType", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Select how you want to upload this document for Admin verification:", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Option 1: Camera
                    Button(
                        onClick = {
                            val perm = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (perm == PackageManager.PERMISSION_GRANTED) {
                                try {
                                    cameraLauncher.launch(null)
                                } catch (e: Exception) {
                                    val camFileName = "camera_doc_${System.currentTimeMillis()}.jpg"
                                    uploadingDocType?.let { docType ->
                                        kitchen?.let { k ->
                                            viewModel.updateCatererDocuments(
                                                catererId = k.id,
                                                fssaiDoc = if (docType.contains("FSSAI")) camFileName else k.fssaiDocUrl,
                                                aadhaarDoc = if (docType.contains("Aadhaar")) camFileName else k.aadhaarDocUrl,
                                                panDoc = if (docType.contains("PAN")) camFileName else k.panDocUrl,
                                                bankChequeDoc = if (docType.contains("Bank")) camFileName else k.bankChequeDocUrl,
                                                kitchenPhotoDoc = if (docType.contains("Premises")) camFileName else k.kitchenPhotoUrl
                                            )
                                            Toast.makeText(context, "📸 Photo document scanned: $camFileName", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    uploadingDocType = null
                                }
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("camera_upload_button")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📷 Open Camera & Take Photo")
                    }

                    // Option 2: Choose File / Storage
                    Button(
                        onClick = {
                            try {
                                filePickerLauncher.launch("*/*")
                            } catch (e: Exception) {
                                val fileName = "doc_${System.currentTimeMillis()}.pdf"
                                val fullPath = "uploads/$fileName"
                                uploadingDocType?.let { docType ->
                                    kitchen?.let { k ->
                                        viewModel.updateCatererDocuments(
                                            catererId = k.id,
                                            fssaiDoc = if (docType.contains("FSSAI")) fullPath else k.fssaiDocUrl,
                                            aadhaarDoc = if (docType.contains("Aadhaar")) fullPath else k.aadhaarDocUrl,
                                            panDoc = if (docType.contains("PAN")) fullPath else k.panDocUrl,
                                            bankChequeDoc = if (docType.contains("Bank")) fullPath else k.bankChequeDocUrl,
                                            kitchenPhotoDoc = if (docType.contains("Premises")) fullPath else k.kitchenPhotoUrl
                                        )
                                        Toast.makeText(context, "📁 Selected document: $fileName", Toast.LENGTH_LONG).show()
                                    }
                                }
                                uploadingDocType = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("file_upload_button")
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📁 Choose File / Photo from Storage")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Or enter attachment path manually:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = fileNameInput,
                        onValueChange = { fileNameInput = it },
                        label = { Text("Attachment Path / File Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(6.dp)) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Supported formats: PDF, JPG, PNG (Max 10MB)", fontSize = 10.sp, color = Color.DarkGray)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (kitchen != null) {
                            viewModel.updateCatererDocuments(
                                catererId = kitchen.id,
                                fssaiDoc = if (docType.contains("FSSAI")) fileNameInput else kitchen.fssaiDocUrl,
                                aadhaarDoc = if (docType.contains("Aadhaar")) fileNameInput else kitchen.aadhaarDocUrl,
                                panDoc = if (docType.contains("PAN")) fileNameInput else kitchen.panDocUrl,
                                bankChequeDoc = if (docType.contains("Bank")) fileNameInput else kitchen.bankChequeDocUrl,
                                kitchenPhotoDoc = if (docType.contains("Premises")) fileNameInput else kitchen.kitchenPhotoUrl
                            )
                            Toast.makeText(context, "✅ Document uploaded and submitted to Admin!", Toast.LENGTH_LONG).show()
                        }
                        uploadingDocType = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen)
                ) {
                    Text("Save & Submit 📤")
                }
            },
            dismissButton = {
                TextButton(onClick = { uploadingDocType = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Quick Change Logo Dialog
    if (isChangingLogo && kitchen != null) {
        var logoUrlInput by remember { mutableStateOf(kitchen.logoUrl) }
        val logoPresetList = listOf(
            "Royal Crown" to "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=500&auto=format&fit=crop&q=80",
            "Mughlai Crest" to "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500&auto=format&fit=crop&q=80",
            "Traditional Dawat" to "https://images.unsplash.com/photo-1544025162-d76694265947?w=500&auto=format&fit=crop&q=80",
            "Golden Feast" to "https://images.unsplash.com/photo-1574484284002-952d92456975?w=500&auto=format&fit=crop&q=80"
        )

        AlertDialog(
            onDismissRequest = { isChangingLogo = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kitchen Logo (किचन का लोगो / फोटो)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.height(380.dp)) {
                    item {
                        // Current Logo Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (logoUrlInput.isNotBlank()) {
                                AsyncImage(
                                    model = logoUrlInput,
                                    contentDescription = "Logo Preview",
                                    modifier = Modifier.size(90.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Restaurant, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(48.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Upload from phone gallery button
                        Button(
                            onClick = {
                                logoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                isChangingLogo = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("📱 Gallery se Logo Chunein", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Preset Brand Logos (त्वरित लोगो):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.height(6.dp))

                        logoPresetList.forEach { (pName, pUrl) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (logoUrlInput == pUrl) Color(0xFFFFF3E0) else Color(0xFFF8FAFC))
                                    .border(1.dp, if (logoUrlInput == pUrl) SaffronPrimary else Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                    .clickable {
                                        logoUrlInput = pUrl
                                        viewModel.updateCatererDetails(kitchen.copy(logoUrl = pUrl))
                                        Toast.makeText(context, "✅ Logo applied: $pName", Toast.LENGTH_SHORT).show()
                                        isChangingLogo = false
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(36.dp).clip(CircleShape)) {
                                    AsyncImage(model = pUrl, contentDescription = pName, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(pName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B), modifier = Modifier.weight(1f))
                                if (logoUrlInput == pUrl) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = logoUrlInput,
                            onValueChange = { logoUrlInput = it },
                            label = { Text("Or Paste Logo Image URL", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCatererDetails(kitchen.copy(logoUrl = logoUrlInput))
                        Toast.makeText(context, "✅ Kitchen Logo updated!", Toast.LENGTH_SHORT).show()
                        isChangingLogo = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Save Logo")
                }
            },
            dismissButton = {
                TextButton(onClick = { isChangingLogo = false }) { Text("Cancel") }
            }
        )
    }

    // Quick Change Banner Dialog
    if (isChangingBanner && kitchen != null) {
        var bannerUrlInput by remember { mutableStateOf(kitchen.bannerUrl) }
        val bannerPresetList = listOf(
            "Royal Banquet Hall" to "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?w=1000&auto=format&fit=crop&q=80",
            "Mughlai Dawat Spread" to "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=1000&auto=format&fit=crop&q=80",
            "Luxury Buffet Setup" to "https://images.unsplash.com/photo-1544025162-d76694265947?w=1000&auto=format&fit=crop&q=80",
            "Tandoori BBQ Live Kitchen" to "https://images.unsplash.com/photo-1574484284002-952d92456975?w=1000&auto=format&fit=crop&q=80"
        )

        AlertDialog(
            onDismissRequest = { isChangingBanner = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kitchen Banner (किचन का बैनर फोटो)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.height(380.dp)) {
                    item {
                        // Current Banner Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (bannerUrlInput.isNotBlank()) {
                                AsyncImage(
                                    model = bannerUrlInput,
                                    contentDescription = "Banner Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.Image, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(48.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Upload from phone gallery button
                        Button(
                            onClick = {
                                bannerPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                isChangingBanner = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("📱 Gallery se Banner Photo Chunein", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Preset Banquet & Kitchen Banners:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.height(6.dp))

                        bannerPresetList.forEach { (pName, pUrl) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (bannerUrlInput == pUrl) Color(0xFFFFF3E0) else Color(0xFFF8FAFC))
                                    .border(1.dp, if (bannerUrlInput == pUrl) SaffronPrimary else Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                    .clickable {
                                        bannerUrlInput = pUrl
                                        viewModel.updateCatererDetails(kitchen.copy(bannerUrl = pUrl))
                                        Toast.makeText(context, "✅ Banner applied: $pName", Toast.LENGTH_SHORT).show()
                                        isChangingBanner = false
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(48.dp, 32.dp).clip(RoundedCornerShape(4.dp))) {
                                    AsyncImage(model = pUrl, contentDescription = pName, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(pName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B), modifier = Modifier.weight(1f))
                                if (bannerUrlInput == pUrl) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = bannerUrlInput,
                            onValueChange = { bannerUrlInput = it },
                            label = { Text("Or Paste Banner Image URL", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCatererDetails(kitchen.copy(bannerUrl = bannerUrlInput))
                        Toast.makeText(context, "✅ Kitchen Banner updated!", Toast.LENGTH_SHORT).show()
                        isChangingBanner = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Save Banner")
                }
            },
            dismissButton = {
                TextButton(onClick = { isChangingBanner = false }) { Text("Cancel") }
            }
        )
    }

    // Edit Profile Details Dialog
    if (isEditingDetails && kitchen != null) {
        var kitchenName by remember { mutableStateOf(kitchen.kitchenName) }
        var mobile by remember { mutableStateOf(kitchen.ownerMobile) }
        var logoUrl by remember { mutableStateOf(kitchen.logoUrl) }
        var bannerUrl by remember { mutableStateOf(kitchen.bannerUrl) }
        var fssai by remember { mutableStateOf(kitchen.fssaiLicense) }
        var aadhaar by remember { mutableStateOf(kitchen.aadhaarNumber) }
        var pan by remember { mutableStateOf(kitchen.panNumber) }
        var bankAcc by remember { mutableStateOf(kitchen.bankAccount) }
        var ifsc by remember { mutableStateOf(kitchen.bankIfsc) }

        AlertDialog(
            onDismissRequest = { isEditingDetails = false },
            title = { Text("Edit Kitchen & Bank Details") },
            text = {
                LazyColumn(modifier = Modifier.height(360.dp)) {
                    item {
                        OutlinedTextField(value = kitchenName, onValueChange = { kitchenName = it }, label = { Text("Kitchen Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Owner Mobile") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = logoUrl, onValueChange = { logoUrl = it }, label = { Text("Kitchen Logo URL") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = bannerUrl, onValueChange = { bannerUrl = it }, label = { Text("Kitchen Banner URL") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = fssai, onValueChange = { fssai = it }, label = { Text("FSSAI License No") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = aadhaar, onValueChange = { aadhaar = it }, label = { Text("Aadhaar No") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = pan, onValueChange = { pan = it }, label = { Text("PAN No") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = bankAcc, onValueChange = { bankAcc = it }, label = { Text("Bank Account No") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(value = ifsc, onValueChange = { ifsc = it }, label = { Text("IFSC Code") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCatererDetails(
                            kitchen.copy(
                                kitchenName = kitchenName,
                                ownerMobile = mobile,
                                logoUrl = logoUrl,
                                bannerUrl = bannerUrl,
                                fssaiLicense = fssai,
                                aadhaarNumber = aadhaar,
                                panNumber = pan,
                                bankAccount = bankAcc,
                                bankIfsc = ifsc
                            )
                        )
                        isEditingDetails = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Save Details")
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditingDetails = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun KycDocumentCard(
    title: String,
    docName: String,
    refNo: String,
    isVerified: Boolean,
    onView: () -> Unit,
    onUpload: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White, contentColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
                Surface(
                    color = if (isVerified) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        if (isVerified) "VERIFIED ✅" else "PENDING ⏳",
                        color = if (isVerified) VegGreen else SaffronPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("File: $docName | Ref: $refNo", fontSize = 11.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Document 👁️", fontSize = 11.sp)
                }

                Button(
                    onClick = onUpload,
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload / Replace 📤", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun KitchenSettingsScreen(viewModel: CaterersViewModel) {
    val settings by viewModel.kitchenSettings.collectAsState()

    var isKitchenOpen by remember(settings) { mutableStateOf(settings.isKitchenOpen) }
    var autoWaInv by remember(settings) { mutableStateOf(settings.autoSendWhatsappInvoice) }
    var minOrderValText by remember(settings) { mutableStateOf(settings.minimumOrderValue.toInt().toString()) }
    var defaultAdvPctText by remember(settings) { mutableStateOf(settings.defaultAdvancePercentage.toString()) }
    var gstPctText by remember(settings) { mutableStateOf(settings.gstTaxPercentage.toString()) }
    var radiusKmText by remember(settings) { mutableStateOf(settings.deliveryRadiusKm.toString()) }
    var delChargeText by remember(settings) { mutableStateOf(settings.deliveryChargePerKm.toInt().toString()) }
    var openingTime by remember(settings) { mutableStateOf(settings.openingTime) }
    var closingTime by remember(settings) { mutableStateOf(settings.closingTime) }
    var phone by remember(settings) { mutableStateOf(settings.kitchenPhone) }
    var address by remember(settings) { mutableStateOf(settings.kitchenAddress) }

    var allowSameDay by remember(settings) { mutableStateOf(settings.allowSameDayBooking) }
    var prepLeadTimeText by remember(settings) { mutableStateOf(settings.sameDayPrepLeadTimeHours.toString()) }
    var sameDayStart by remember(settings) { mutableStateOf(settings.sameDayDeliveryStartTime) }
    var sameDayEnd by remember(settings) { mutableStateOf(settings.sameDayDeliveryEndTime) }

    // Caterer Per-Order Discount Settings State
    var catererDiscType by remember(settings) { mutableStateOf(settings.catererOrderDiscountType) }
    var catererDiscValueStr by remember(settings) { mutableStateOf(settings.catererOrderDiscountValue.toString()) }
    var catererMinOrderStr by remember(settings) { mutableStateOf(settings.catererMinOrderForDiscount.toString()) }

    // Kitchen Food Serving Capacity Settings (1 Kg me kitne log khaenge)
    var biryaniPersonsStr by remember(settings) { mutableStateOf(String.format("%.1f", settings.biryaniPersonsPerKg)) }
    var sweetPersonsStr by remember(settings) { mutableStateOf(String.format("%.1f", settings.sweetPersonsPerKg)) }
    var gravyPersonsStr by remember(settings) { mutableStateOf(String.format("%.1f", settings.gravyPersonsPerKg)) }
    var rotiPersonsStr by remember(settings) { mutableStateOf(String.format("%.1f", 1.0 / settings.rotiPersonsPerUnit.coerceAtLeast(0.1))) }

    val timeSlots = remember(settings) { mutableStateListOf(*settings.deliveryTimeSlots.toTypedArray()) }
    var newSlotInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("kitchen_settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Kitchen & Admin Settings (किचन सेटिंग्स)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color(0xFFFF6D00).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6D00))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🔥 Firestore Active", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFAB40))
                                    }
                                }
                            }
                            Text("Real-time cloud dynamic configuration for radius, slots, prep time & rules", fontSize = 11.sp, color = Color.LightGray)
                        }
                    }
                }
            }
        }

        // 1. Kitchen Operational Status & WhatsApp Toggles
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Store, contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Operational Status & Automation", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Kitchen Accepting Orders (किचन स्टेटस)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                if (isKitchenOpen) "Kitchen is ONLINE and taking new catering orders" else "Kitchen is CLOSED / OFFLINE for new orders",
                                fontSize = 11.sp,
                                color = if (isKitchenOpen) VegGreen else Color.Red
                            )
                        }
                        Switch(
                            checked = isKitchenOpen,
                            onCheckedChange = { isKitchenOpen = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = VegGreen, checkedTrackColor = Color(0xFFC8E6C9)),
                            modifier = Modifier.testTag("kitchen_open_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto WhatsApp Invoice Sharing (ऑटो व्हाट्सएप इनवॉयस)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Automatically open WhatsApp link after saving offline or online booking", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = autoWaInv,
                            onCheckedChange = { autoWaInv = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SaffronPrimary),
                            modifier = Modifier.testTag("auto_wa_switch")
                        )
                    }
                }
            }
        }

        // 1.5. Same Day Delivery & Preparation Cutoff
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("⚡ Same Day Delivery & Prep Cutoff (सेम-डे डिलीवरी)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Allow Same Day Bookings (सेम-डे बुकिंग ऑन/ऑफ)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                if (allowSameDay) "Same Day Bookings are OPEN for customers" else "Same Day Bookings are CLOSED (Future dates only)",
                                fontSize = 11.sp,
                                color = if (allowSameDay) VegGreen else Color.Red
                            )
                        }
                        Switch(
                            checked = allowSameDay,
                            onCheckedChange = { allowSameDay = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SaffronPrimary),
                            modifier = Modifier.testTag("allow_same_day_switch")
                        )
                    }

                    if (allowSameDay) {
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = prepLeadTimeText,
                            onValueChange = { prepLeadTimeText = it },
                            label = { Text("Food Prep Lead Time Required (Hours e.g. 2.5)") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("prep_time_buffer_input")
                        )
                        Text(
                            "💡 E.g., if current time is 01:00 PM and prep time is 2.5 hours, customer sees slots starting after 03:30 PM.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = sameDayStart,
                                onValueChange = { sameDayStart = it },
                                label = { Text("Same Day Start Time") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = sameDayEnd,
                                onValueChange = { sameDayEnd = it },
                                label = { Text("Same Day End Time") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 2. Minimum Order & Advance Payment Defaults
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Booking Limits & Advance Payment Rules", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = minOrderValText,
                            onValueChange = { minOrderValText = it },
                            label = { Text("Min Order Value (₹)") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = defaultAdvPctText,
                            onValueChange = { defaultAdvPctText = it },
                            label = { Text("Default Advance (%)") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = gstPctText,
                        onValueChange = { gstPctText = it },
                        label = { Text("GST / Service Tax Rate (%)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 3. Delivery Coverage & Charges
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delivery Radius & Charges (डिलीवरी सेटिंग्स)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = radiusKmText,
                            onValueChange = { radiusKmText = it },
                            label = { Text("Max Radius (KM)") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("delivery_radius_input")
                        )
                        OutlinedTextField(
                            value = delChargeText,
                            onValueChange = { delChargeText = it },
                            label = { Text("Delivery Fee / KM (₹)") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 4. Working Hours & Contact Info
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LockClock, contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kitchen Timings & Contact Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = openingTime,
                            onValueChange = { openingTime = it },
                            label = { Text("Opening Time") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = closingTime,
                            onValueChange = { closingTime = it },
                            label = { Text("Closing Time") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Kitchen Support Contact Mobile") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Central Kitchen Dispatch Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 5. Time Slots Configuration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Configured Delivery Time Slots (टाइम स्लॉट)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    timeSlots.forEachIndexed { idx, slot ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• $slot", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            IconButton(onClick = { timeSlots.removeAt(idx) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Slot", tint = Color.Red, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newSlotInput,
                            onValueChange = { newSlotInput = it },
                            placeholder = { Text("e.g. 05:00 PM - 08:00 PM (Evening)") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newSlotInput.isNotBlank()) {
                                    timeSlots.add(newSlotInput.trim())
                                    newSlotInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Add", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 5.5. Caterer Per-Order Discount Settings Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalOffer, contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🏷️ Kitchen Order Discount Settings (किचन आर्डर डिस्काउंट)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Discount Strategy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = catererDiscType == com.example.data.models.DiscountType.NONE,
                            onClick = { catererDiscType = com.example.data.models.DiscountType.NONE },
                            label = { Text("No Discount", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = catererDiscType == com.example.data.models.DiscountType.PERCENTAGE,
                            onClick = { catererDiscType = com.example.data.models.DiscountType.PERCENTAGE },
                            label = { Text("Percentage (%)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = catererDiscType == com.example.data.models.DiscountType.FLAT,
                            onClick = { catererDiscType = com.example.data.models.DiscountType.FLAT },
                            label = { Text("Flat Amount (₹)", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (catererDiscType != com.example.data.models.DiscountType.NONE) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = catererDiscValueStr,
                                onValueChange = { catererDiscValueStr = it },
                                label = { Text(if (catererDiscType == com.example.data.models.DiscountType.PERCENTAGE) "Discount % (e.g. 5)" else "Discount ₹ (e.g. 200)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = catererMinOrderStr,
                                onValueChange = { catererMinOrderStr = it },
                                label = { Text("Min Order Required (₹)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 5.6 Kitchen Food Serving Capacity Settings (1 Kg me kitne log khaenge)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth().testTag("kitchen_serving_capacity_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SoupKitchen, contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("👨‍🍳 Food Serving Capacity (1 Kg में कितने लोग खाएंगे?)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF92400E))
                            Text("हर किचन अपने हिसाब से 1 Kg की सर्विंग सेट करे (कस्टमर प्रोफाइल पर यही दिखेगा)", fontSize = 11.sp, color = Color(0xFFB45309))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFFFDE68A))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = biryaniPersonsStr,
                            onValueChange = { biryaniPersonsStr = it },
                            label = { Text("1 Kg Biryani (लोग)") },
                            placeholder = { Text("e.g. 6.7") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("input_biryani_serving")
                        )
                        OutlinedTextField(
                            value = sweetPersonsStr,
                            onValueChange = { sweetPersonsStr = it },
                            label = { Text("1 Kg Meetha (लोग)") },
                            placeholder = { Text("e.g. 12.5") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("input_sweet_serving")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = gravyPersonsStr,
                            onValueChange = { gravyPersonsStr = it },
                            label = { Text("1 Kg Gravy (लोग)") },
                            placeholder = { Text("e.g. 8.3") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("input_gravy_serving")
                        )
                        OutlinedTextField(
                            value = rotiPersonsStr,
                            onValueChange = { rotiPersonsStr = it },
                            label = { Text("रोटी प्रति व्यक्ति") },
                            placeholder = { Text("e.g. 2.0") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("input_roti_serving")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Live counting preview for this kitchen
                    val bPer = biryaniPersonsStr.toDoubleOrNull() ?: 6.67
                    val sPer = sweetPersonsStr.toDoubleOrNull() ?: 12.5
                    val gPer = gravyPersonsStr.toDoubleOrNull() ?: 8.33
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("📊 आपकी किचन सेटिंग्स अनुसार लाइव काउंटिंग (Live Preview):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• 10 लोग: ${(10.0 / bPer.coerceAtLeast(0.1)).let { String.format("%.1f", it) }} Kg Biryani | ${(10.0 / sPer.coerceAtLeast(0.1)).let { String.format("%.1f", it) }} Kg Meetha", fontSize = 11.sp, color = Color(0xFF78350F))
                            Text("• 50 लोग: ${(50.0 / bPer.coerceAtLeast(0.1)).let { String.format("%.1f", it) }} Kg Biryani | ${(50.0 / sPer.coerceAtLeast(0.1)).let { String.format("%.1f", it) }} Kg Meetha", fontSize = 11.sp, color = Color(0xFF78350F))
                            Text("• 200 लोग: ${(200.0 / bPer.coerceAtLeast(0.1)).let { String.format("%.1f", it) }} Kg Biryani | ${(200.0 / sPer.coerceAtLeast(0.1)).let { String.format("%.1f", it) }} Kg Meetha", fontSize = 11.sp, color = Color(0xFF78350F))
                        }
                    }
                }
            }
        }

        // 6. Save Button
        item {
            Button(
                onClick = {
                    val rotisPerPerson = rotiPersonsStr.toDoubleOrNull() ?: 2.0
                    val newConfig = KitchenSettingsConfig(
                        deliveryRadiusKm = radiusKmText.toIntOrNull() ?: settings.deliveryRadiusKm,
                        deliveryTimeSlots = timeSlots.toList(),
                        minimumOrderValue = minOrderValText.toDoubleOrNull() ?: settings.minimumOrderValue,
                        defaultAdvancePercentage = defaultAdvPctText.toIntOrNull() ?: settings.defaultAdvancePercentage,
                        gstTaxPercentage = gstPctText.toDoubleOrNull() ?: settings.gstTaxPercentage,
                        deliveryChargePerKm = delChargeText.toDoubleOrNull() ?: settings.deliveryChargePerKm,
                        isKitchenOpen = isKitchenOpen,
                        autoSendWhatsappInvoice = autoWaInv,
                        openingTime = openingTime,
                        closingTime = closingTime,
                        kitchenAddress = address,
                        kitchenPhone = phone,
                        allowSameDayBooking = allowSameDay,
                        sameDayPrepLeadTimeHours = prepLeadTimeText.toDoubleOrNull() ?: settings.sameDayPrepLeadTimeHours,
                        sameDayDeliveryStartTime = sameDayStart,
                        sameDayDeliveryEndTime = sameDayEnd,
                        catererOrderDiscountType = catererDiscType,
                        catererOrderDiscountValue = catererDiscValueStr.toDoubleOrNull() ?: 0.0,
                        catererMinOrderForDiscount = catererMinOrderStr.toDoubleOrNull() ?: 0.0,
                        biryaniPersonsPerKg = biryaniPersonsStr.toDoubleOrNull() ?: settings.biryaniPersonsPerKg,
                        sweetPersonsPerKg = sweetPersonsStr.toDoubleOrNull() ?: settings.sweetPersonsPerKg,
                        gravyPersonsPerKg = gravyPersonsStr.toDoubleOrNull() ?: settings.gravyPersonsPerKg,
                        rotiPersonsPerUnit = (1.0 / rotisPerPerson.coerceAtLeast(0.1))
                    )
                    viewModel.updateKitchenSettings(newConfig)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_kitchen_settings_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("💾 SAVE KITCHEN SETTINGS (सेटिंग्स सेव करें)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
