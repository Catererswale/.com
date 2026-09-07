package com.example.ui.kitchen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.models.PaymentMethod
import com.example.data.repository.CaterersViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import kotlinx.coroutines.launch

/**
 * Modern Responsive Kitchen Dashboard matching Image 2: "KITCHEN PANEL - A1 Huma Caterers"
 * Automatically adapts between Mobile (handheld) and Tablet / Foldable (split-pane & kanban).
 */
@Composable
fun KitchenResponsiveDashboard(
    viewModel: CaterersViewModel,
    onNavigateTab: (Int) -> Unit = {},
    onPreviewCustomerStore: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 768.dp

        if (isTablet) {
            KitchenTabletLayout(
                viewModel = viewModel,
                onNavigateTab = onNavigateTab,
                onPreviewCustomerStore = onPreviewCustomerStore
            )
        } else {
            KitchenMobileLayout(
                viewModel = viewModel,
                onNavigateTab = onNavigateTab,
                onPreviewCustomerStore = onPreviewCustomerStore
            )
        }
    }
}

/**
 * Tablet / Widescreen Layout:
 * Left Full Sidebar + Top Date/Status Bar + Pipeline Metric Strip + Multi-Column Kanban + Right Inspector Pane
 */
@Composable
private fun KitchenTabletLayout(
    viewModel: CaterersViewModel,
    onNavigateTab: (Int) -> Unit,
    onPreviewCustomerStore: (String) -> Unit
) {
    val context = LocalContext.current
    val orders by viewModel.ordersList.collectAsState()
    val deliveryBoys by viewModel.deliveryBoysList.collectAsState()
    val selectedKitchenId by viewModel.selectedCatererId.collectAsState()
    val activeKitchenId = selectedKitchenId ?: "caterer_1"

    var selectedPipelineStage by remember { mutableStateOf("ALL") }
    var dashboardViewMode by remember { mutableStateOf("ORDERS_PIPELINE") } // "ORDERS_PIPELINE" or "DELIVERY_PARTNERS"
    var selectedOrderForDetail by remember { mutableStateOf<OrderEntity?>(orders.firstOrNull()) }
    var showAssignDeliveryModal by remember { mutableStateOf<OrderEntity?>(null) }
    var currentDateStr by remember { mutableStateOf("12 May 2024") }

    Row(modifier = Modifier.fillMaxSize()) {
        // 1. Left Sidebar Navigation
        Surface(
            color = Color(0xFF0F172A),
            contentColor = Color.White,
            modifier = Modifier
                .width(220.dp)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Brand Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = SaffronPrimary,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Kitchen, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("CATERERS WALE", fontWeight = FontWeight.Black, fontSize = 12.sp, color = AmberSecondary)
                        Text("KITCHEN PANEL", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Items
                SidebarNavItem(icon = Icons.Default.Dashboard, label = "Orders Pipeline", isSelected = (dashboardViewMode == "ORDERS_PIPELINE"), onClick = { dashboardViewMode = "ORDERS_PIPELINE" })
                SidebarNavItem(icon = Icons.Default.ReceiptLong, label = "Offline Booking 📝", onClick = { onNavigateTab(KitchenNavTabs.OFFLINE_BOOKING) })
                SidebarNavItem(icon = Icons.Default.Assignment, label = "Orders Management", onClick = { onNavigateTab(KitchenNavTabs.ORDERS) })

                Spacer(modifier = Modifier.height(10.dp))
                Text("PREPARATION MANAGEMENT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))

                SidebarSubNavItem("All Orders", orders.size, selectedPipelineStage == "ALL" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "ALL"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("Confirm Orders", orders.count { it.orderStatus == OrderStatus.CONFIRMED || it.orderStatus == OrderStatus.ACCEPTED }, selectedPipelineStage == "CONFIRM" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "CONFIRM"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("In Preparation", orders.count { it.orderStatus == OrderStatus.PREPARING }, selectedPipelineStage == "PREPARING" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "PREPARING"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("Ready Orders", orders.count { it.orderStatus == OrderStatus.READY }, selectedPipelineStage == "READY" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "READY"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("Out for Delivery", orders.count { it.orderStatus == OrderStatus.OUT_FOR_DELIVERY || it.orderStatus == OrderStatus.ASSIGNED_DELIVERY }, selectedPipelineStage == "OUT_FOR_DELIVERY" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "OUT_FOR_DELIVERY"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("Delivered Orders", orders.count { it.orderStatus == OrderStatus.DELIVERED }, selectedPipelineStage == "DELIVERED" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "DELIVERED"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("Cancelled Orders", orders.count { it.orderStatus == OrderStatus.CANCELLED }, selectedPipelineStage == "CANCELLED" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "CANCELLED"; dashboardViewMode = "ORDERS_PIPELINE" }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(8.dp))

                SidebarNavItem(icon = Icons.Default.DirectionsBike, label = "Delivery Partners (${deliveryBoys.count { it.kitchenId == activeKitchenId }})", isSelected = (dashboardViewMode == "DELIVERY_PARTNERS"), onClick = { dashboardViewMode = "DELIVERY_PARTNERS" })
                SidebarNavItem(icon = Icons.Default.Restaurant, label = "Items / Add-ons", onClick = { onNavigateTab(KitchenNavTabs.MENU) })
                SidebarNavItem(icon = Icons.Default.SoupKitchen, label = "Containers & Cash Summary 🍲💵", onClick = { onNavigateTab(KitchenNavTabs.CONTAINERS_CASH) })
                SidebarNavItem(icon = Icons.Default.Print, label = "KOT / Print", onClick = { onNavigateTab(KitchenNavTabs.ORDERS) })
                SidebarNavItem(icon = Icons.Default.Group, label = "Kitchen Staff", onClick = { onNavigateTab(KitchenNavTabs.STAFF) })
                SidebarNavItem(icon = Icons.Default.Analytics, label = "Reports & Analytics", onClick = { onNavigateTab(KitchenNavTabs.ANALYTICS) })
                SidebarNavItem(icon = Icons.Default.Settings, label = "Settings", onClick = { onNavigateTab(KitchenNavTabs.SETTINGS) })

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(16.dp))

                // Support Help Box
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Need Help?", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("+91 80 90 00 70 30", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                }
            }
        }

        // 2. Main Content Area + Right Detail Inspector
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFFF8FAFC))
        ) {
            // Top Bar
            KitchenTopHeader(
                kitchenName = "A1 Huma Caterers",
                currentDateStr = currentDateStr,
                onRefresh = {
                    Toast.makeText(context, "Pipeline refreshed with live orders!", Toast.LENGTH_SHORT).show()
                }
            )

            // Top Dashboard Mode Switcher Bar (Pipeline vs Delivery Partners Fleet)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        modifier = Modifier.clickable { dashboardViewMode = "ORDERS_PIPELINE" },
                        shape = RoundedCornerShape(6.dp),
                        color = if (dashboardViewMode == "ORDERS_PIPELINE") SaffronPrimary else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Dashboard,
                                contentDescription = null,
                                tint = if (dashboardViewMode == "ORDERS_PIPELINE") Color.White else Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Orders Pipeline (${orders.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dashboardViewMode == "ORDERS_PIPELINE") Color.White else Color(0xFF475569)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.clickable { dashboardViewMode = "DELIVERY_PARTNERS" },
                        shape = RoundedCornerShape(6.dp),
                        color = if (dashboardViewMode == "DELIVERY_PARTNERS") SaffronPrimary else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DirectionsBike,
                                contentDescription = null,
                                tint = if (dashboardViewMode == "DELIVERY_PARTNERS") Color.White else Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Registered Delivery Partners (${deliveryBoys.count { it.kitchenId == activeKitchenId }})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dashboardViewMode == "DELIVERY_PARTNERS") Color.White else Color(0xFF475569)
                            )
                        }
                    }
                }

                if (dashboardViewMode == "DELIVERY_PARTNERS") {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Text(
                            text = "🛡️ Owner Kitchen Admin Mode Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (dashboardViewMode == "DELIVERY_PARTNERS") {
                // Registered Delivery Partners List View on Dashboard
                KitchenDeliveryPartnersDashboardView(
                    viewModel = viewModel,
                    ownerKitchenId = activeKitchenId,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                // Pipeline Metric Status Cards Strip (From Image 2)
                KitchenPipelineMetricStrip(orders = orders)

                // Master-Detail Split: Left Kanban Pipeline + Right Order Inspector
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // Middle Kanban Board / Orders List
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        KitchenKanbanBoard(
                            orders = orders,
                            selectedStage = selectedPipelineStage,
                            onSelectOrder = { selectedOrderForDetail = it },
                            onAction = { action, order ->
                                handleOrderAction(viewModel, action, order, context) {
                                    showAssignDeliveryModal = it
                                }
                            }
                        )
                    }

                    // Right Order Details Inspector Pane
                    Surface(
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .width(340.dp)
                            .fillMaxHeight()
                    ) {
                        if (selectedOrderForDetail != null) {
                            KitchenOrderDetailsInspector(
                                order = selectedOrderForDetail!!,
                                onClose = { selectedOrderForDetail = null },
                                onAssignDelivery = { showAssignDeliveryModal = it },
                                onCallCustomer = { phone ->
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                    context.startActivity(intent)
                                }
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Select an order to view full details", fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                // Bottom Summary Row (Today's Summary, Payment Summary, Category Totals, Time Slots, Quick Actions)
                KitchenBottomSummaryPanels(orders = orders, onNavigateTab = onNavigateTab)
            }
        }
    }

    // Delivery Assignment Dialog
    if (showAssignDeliveryModal != null) {
        AssignDeliveryBoyDialog(
            order = showAssignDeliveryModal!!,
            deliveryBoys = deliveryBoys,
            onDismiss = { showAssignDeliveryModal = null },
            onAssign = { boy ->
                viewModel.assignDeliveryBoy(showAssignDeliveryModal!!.orderId, boy)
                Toast.makeText(context, "Assigned order to ${boy.name}!", Toast.LENGTH_SHORT).show()
                showAssignDeliveryModal = null
            }
        )
    }
}

/**
 * Mobile Layout: Single-column / Tabbed Kanban with Drawer navigation & Modal Inspector
 */
@Composable
private fun KitchenMobileLayout(
    viewModel: CaterersViewModel,
    onNavigateTab: (Int) -> Unit,
    onPreviewCustomerStore: (String) -> Unit
) {
    val context = LocalContext.current
    val orders by viewModel.ordersList.collectAsState()
    val deliveryBoys by viewModel.deliveryBoysList.collectAsState()
    val selectedKitchenId by viewModel.selectedCatererId.collectAsState()
    val activeKitchenId = selectedKitchenId ?: "caterer_1"

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var mobileDashboardView by remember { mutableStateOf("ORDERS_PIPELINE") } // "ORDERS_PIPELINE" or "DELIVERY_PARTNERS"
    var selectedStageIndex by remember { mutableStateOf(0) }
    var selectedOrderForDetail by remember { mutableStateOf<OrderEntity?>(null) }
    var showAssignDeliveryModal by remember { mutableStateOf<OrderEntity?>(null) }

    val stages = listOf("ALL", "CONFIRM", "PREPARING", "READY", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED")
    val stageLabels = listOf("All (128)", "Confirm (52)", "In Prep (18)", "Ready (22)", "Out for Del (14)", "Delivered (20)", "Cancelled (2)")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF0F172A),
                drawerContentColor = Color.White,
                modifier = Modifier.width(280.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = SaffronPrimary, modifier = Modifier.size(36.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Kitchen, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("A1 Huma Caterers", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            Text("Kitchen Manager Online 🟢", fontSize = 11.sp, color = AmberSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(12.dp))

                    NavigationDrawerItem(
                        label = { Text("📊 Orders Pipeline", fontSize = 13.5.sp) },
                        selected = mobileDashboardView == "ORDERS_PIPELINE",
                        onClick = {
                            scope.launch { drawerState.close() }
                            mobileDashboardView = "ORDERS_PIPELINE"
                            onNavigateTab(0)
                        },
                        colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = SaffronPrimary)
                    )
                    NavigationDrawerItem(
                        label = { Text("🛵 Delivery Partners (${deliveryBoys.count { it.kitchenId == activeKitchenId }})", fontSize = 13.5.sp) },
                        selected = mobileDashboardView == "DELIVERY_PARTNERS",
                        onClick = {
                            scope.launch { drawerState.close() }
                            mobileDashboardView = "DELIVERY_PARTNERS"
                        },
                        colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = SaffronPrimary)
                    )
                    NavigationDrawerItem(
                        label = { Text("📝 Offline Booking", fontSize = 13.5.sp, fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateTab(KitchenNavTabs.OFFLINE_BOOKING)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("📋 Orders Management", fontSize = 13.5.sp) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateTab(KitchenNavTabs.ORDERS)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("🍱 Menu & Add-ons", fontSize = 13.5.sp) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateTab(KitchenNavTabs.MENU)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("🖨️ KOT & Print", fontSize = 13.5.sp) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateTab(KitchenNavTabs.ORDERS)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("🍲💵 Containers & Cash Summary", fontSize = 13.5.sp) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateTab(KitchenNavTabs.CONTAINERS_CASH)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("👥 Kitchen Staff Management", fontSize = 13.5.sp) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateTab(KitchenNavTabs.STAFF)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("📈 Reports & Analytics", fontSize = 13.5.sp) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateTab(KitchenNavTabs.ANALYTICS)
                        }
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            // Mobile Top Bar
            Surface(color = Color(0xFF0F172A), contentColor = Color.White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                        Column {
                            Text("Kitchen Panel", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("A1 Huma • Online 🟢", fontSize = 11.sp, color = AmberSecondary)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text("12 May 2024", fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                        IconButton(onClick = { Toast.makeText(context, "Refreshed!", Toast.LENGTH_SHORT).show() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Mobile Mode Switcher Bar (Pipeline vs Delivery Partners Fleet)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { mobileDashboardView = "ORDERS_PIPELINE" },
                    shape = RoundedCornerShape(8.dp),
                    color = if (mobileDashboardView == "ORDERS_PIPELINE") SaffronPrimary else Color(0xFFF1F5F9)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Dashboard,
                            contentDescription = null,
                            tint = if (mobileDashboardView == "ORDERS_PIPELINE") Color.White else Color(0xFF475569),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Pipeline (${orders.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mobileDashboardView == "ORDERS_PIPELINE") Color.White else Color(0xFF475569)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { mobileDashboardView = "DELIVERY_PARTNERS" },
                    shape = RoundedCornerShape(8.dp),
                    color = if (mobileDashboardView == "DELIVERY_PARTNERS") SaffronPrimary else Color(0xFFF1F5F9)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.DirectionsBike,
                            contentDescription = null,
                            tint = if (mobileDashboardView == "DELIVERY_PARTNERS") Color.White else Color(0xFF475569),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Fleet (${deliveryBoys.count { it.kitchenId == activeKitchenId }})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mobileDashboardView == "DELIVERY_PARTNERS") Color.White else Color(0xFF475569)
                        )
                    }
                }
            }

            if (mobileDashboardView == "DELIVERY_PARTNERS") {
                // Registered Delivery Partners List View on Mobile Kitchen Dashboard
                KitchenDeliveryPartnersDashboardView(
                    viewModel = viewModel,
                    ownerKitchenId = activeKitchenId,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                // Horizontal Scrollable Stages Tabs
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 6.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(stages.indices.toList()) { index ->
                        val isSelected = selectedStageIndex == index
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) SaffronPrimary else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SaffronPrimary else Color(0xFFCBD5E1)),
                            modifier = Modifier.clickable { selectedStageIndex = index }
                        ) {
                            Text(
                                text = stageLabels[index],
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Mobile Kanban Orders List
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    KitchenKanbanBoard(
                        orders = orders,
                        selectedStage = stages[selectedStageIndex],
                        onSelectOrder = { selectedOrderForDetail = it },
                        onAction = { action, order ->
                            handleOrderAction(viewModel, action, order, context) {
                                showAssignDeliveryModal = it
                            }
                        }
                    )
                }
            }

            // Mobile Quick Action Strip
            Surface(
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { mobileDashboardView = "ORDERS_PIPELINE"; selectedStageIndex = 0 }
                    ) {
                        Icon(Icons.Default.Dashboard, contentDescription = "Pipeline", tint = if (mobileDashboardView == "ORDERS_PIPELINE") SaffronPrimary else Color.Gray, modifier = Modifier.size(20.dp))
                        Text("Pipeline", fontSize = 10.sp, fontWeight = if (mobileDashboardView == "ORDERS_PIPELINE") FontWeight.Bold else FontWeight.Normal, color = if (mobileDashboardView == "ORDERS_PIPELINE") SaffronPrimary else Color.Gray)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onNavigateTab(KitchenNavTabs.OFFLINE_BOOKING) }
                            .testTag("bottom_nav_offline_booking")
                    ) {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color(0xFF93C5FD)),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = "Offline Booking", tint = Color(0xFF0288D1), modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Offline Booking", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onNavigateTab(KitchenNavTabs.CONTAINERS_CASH) }
                    ) {
                        Icon(Icons.Default.SoupKitchen, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(20.dp))
                        Text("Handi & Cash", fontSize = 10.sp, color = Color(0xFF92400E))
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onNavigateTab(KitchenNavTabs.ANALYTICS) }
                    ) {
                        Icon(Icons.Default.Analytics, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Text("Analytics", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }

    // Modal Inspector for Mobile
    if (selectedOrderForDetail != null) {
        AlertDialog(
            onDismissRequest = { selectedOrderForDetail = null },
            confirmButton = {
                TextButton(onClick = { selectedOrderForDetail = null }) {
                    Text("Close", fontWeight = FontWeight.Bold, color = SaffronPrimary)
                }
            },
            text = {
                Box(modifier = Modifier.height(480.dp)) {
                    KitchenOrderDetailsInspector(
                        order = selectedOrderForDetail!!,
                        onClose = { selectedOrderForDetail = null },
                        onAssignDelivery = { showAssignDeliveryModal = it },
                        onCallCustomer = { phone ->
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        )
    }

    // Assignment Dialog
    if (showAssignDeliveryModal != null) {
        AssignDeliveryBoyDialog(
            order = showAssignDeliveryModal!!,
            deliveryBoys = deliveryBoys,
            onDismiss = { showAssignDeliveryModal = null },
            onAssign = { boy ->
                viewModel.assignDeliveryBoy(showAssignDeliveryModal!!.orderId, boy)
                Toast.makeText(context, "Assigned order to ${boy.name}!", Toast.LENGTH_SHORT).show()
                showAssignDeliveryModal = null
            }
        )
    }
}

/**
 * Top Header Bar matching Image 2
 */
@Composable
private fun KitchenTopHeader(
    kitchenName: String,
    currentDateStr: String,
    onRefresh: () -> Unit
) {
    Surface(
        color = Color(0xFF0F172A),
        contentColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Kitchen Panel", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("• $kitchenName", fontSize = 13.sp, color = AmberSecondary)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Today, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(currentDateStr, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }

                OutlinedButton(
                    onClick = onRefresh,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Refresh", fontSize = 11.sp)
                }

                // Notification Bell with Badge (5)
                BadgedBox(badge = {
                    Badge(containerColor = Color.Red) { Text("5") }
                }) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Profile Avatar & Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = AmberSecondary, modifier = Modifier.size(32.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("MI", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Mohammed Imran", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Kitchen Manager • Online 🟢", fontSize = 9.sp, color = VegGreen)
                    }
                }
            }
        }
    }
}

/**
 * Pipeline Metric Strip (7 Cards from Image 2):
 * All (128), Confirm (52), In Prep (18), Ready (22), Out for Delivery (14), Delivered (20), Cancelled (2)
 */
@Composable
private fun KitchenPipelineMetricStrip(orders: List<OrderEntity>) {
    val allCount = 128
    val allAmt = "₹ 2,34,700"
    val confirmCount = 52
    val confirmAmt = "₹ 1,24,500"
    val prepCount = 18
    val prepAmt = "₹ 48,300"
    val readyCount = 22
    val readyAmt = "₹ 68,700"
    val outCount = 14
    val outAmt = "₹ 36,400"
    val deliveredCount = 20
    val deliveredAmt = "₹ 52,600"
    val cancelledCount = 2
    val cancelledAmt = "₹ 4,200"

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            PipelineMetricTile(
                title = "All Orders",
                count = allCount.toString(),
                amount = allAmt,
                accentColor = Color(0xFF2563EB),
                icon = Icons.Default.ReceiptLong
            )
        }
        item {
            PipelineMetricTile(
                title = "Confirm Orders",
                count = confirmCount.toString(),
                amount = confirmAmt,
                accentColor = Color(0xFFD97706),
                icon = Icons.Default.Assignment
            )
        }
        item {
            PipelineMetricTile(
                title = "In Preparation",
                count = prepCount.toString(),
                amount = prepAmt,
                accentColor = Color(0xFF0284C7),
                icon = Icons.Default.SoupKitchen
            )
        }
        item {
            PipelineMetricTile(
                title = "Ready",
                count = readyCount.toString(),
                amount = readyAmt,
                accentColor = VegGreen,
                icon = Icons.Default.CheckCircle
            )
        }
        item {
            PipelineMetricTile(
                title = "Out for Delivery",
                count = outCount.toString(),
                amount = outAmt,
                accentColor = SaffronPrimary,
                icon = Icons.Default.DeliveryDining
            )
        }
        item {
            PipelineMetricTile(
                title = "Delivered",
                count = deliveredCount.toString(),
                amount = deliveredAmt,
                accentColor = Color(0xFF059669),
                icon = Icons.Default.DoneAll
            )
        }
        item {
            PipelineMetricTile(
                title = "Cancelled",
                count = cancelledCount.toString(),
                amount = cancelledAmt,
                accentColor = Color(0xFFDC2626),
                icon = Icons.Default.Close
            )
        }
    }
}

@Composable
private fun PipelineMetricTile(
    title: String,
    count: String,
    amount: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.width(140.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = RoundedCornerShape(6.dp), color = accentColor.copy(alpha = 0.12f), modifier = Modifier.size(26.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(15.dp))
                    }
                }
                Text(count, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF64748B), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(amount, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

/**
 * Multi-Column Kanban Board displaying cards per status stage
 */
@Composable
private fun KitchenKanbanBoard(
    orders: List<OrderEntity>,
    selectedStage: String,
    onSelectOrder: (OrderEntity) -> Unit,
    onAction: (String, OrderEntity) -> Unit
) {
    val displayOrders = if (selectedStage == "ALL") {
        orders
    } else {
        orders.filter { order ->
            when (selectedStage) {
                "CONFIRM" -> order.orderStatus == OrderStatus.CONFIRMED || order.orderStatus == OrderStatus.ACCEPTED
                "PREPARING" -> order.orderStatus == OrderStatus.PREPARING
                "READY" -> order.orderStatus == OrderStatus.READY
                "OUT_FOR_DELIVERY" -> order.orderStatus == OrderStatus.OUT_FOR_DELIVERY || order.orderStatus == OrderStatus.ASSIGNED_DELIVERY
                "DELIVERED" -> order.orderStatus == OrderStatus.DELIVERED
                "CANCELLED" -> order.orderStatus == OrderStatus.CANCELLED
                else -> true
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(displayOrders) { order ->
            KitchenKanbanOrderCard(
                order = order,
                onClick = { onSelectOrder(order) },
                onAction = { action -> onAction(action, order) }
            )
        }
    }
}

/**
 * Rich Order Card matching Image 2
 */
@Composable
private fun KitchenKanbanOrderCard(
    order: OrderEntity,
    onClick: () -> Unit,
    onAction: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Order ID, Time, Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "#${order.orderId}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFE0F2FE)
                    ) {
                        Text("New", fontSize = 9.5.sp, color = Color(0xFF0369A1), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }

                Text(
                    "12 May, ${order.deliveryTimeSlot.take(8)}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Customer Name & Phone
            Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
            Text(order.customerMobile, fontSize = 11.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(6.dp))

            // Delivery Slot
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(order.deliveryTimeSlot, fontSize = 11.sp, color = Color(0xFF475569))
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Items Summary
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = order.itemsSummary,
                    fontSize = 11.5.sp,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(6.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Amount & Advance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                    Text("Advance: ₹${order.advancePaidAmount.toInt()}", fontSize = 10.5.sp, color = VegGreen)
                }

                // Action Buttons depending on status
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    when (order.orderStatus) {
                        OrderStatus.NEW, OrderStatus.CONFIRMED, OrderStatus.ACCEPTED -> {
                            Button(
                                onClick = { onAction("PREPARATION") },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Preparation", fontSize = 11.sp)
                            }
                        }
                        OrderStatus.PREPARING -> {
                            Button(
                                onClick = { onAction("MARK_READY") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Mark as Ready", fontSize = 11.sp)
                            }
                        }
                        OrderStatus.READY -> {
                            Button(
                                onClick = { onAction("ASSIGN_DELIVERY") },
                                colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Sign & Send 🚚", fontSize = 10.5.sp)
                            }
                        }
                        else -> {
                            OutlinedButton(
                                onClick = onClick,
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("View Details", fontSize = 10.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Right Order Details Inspector Pane matching Image 2
 */
@Composable
private fun KitchenOrderDetailsInspector(
    order: OrderEntity,
    onClose: () -> Unit,
    onAssignDelivery: (OrderEntity) -> Unit,
    onCallCustomer: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Order Details #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                Text("Delivery: 12 May 2024, ${order.deliveryTimeSlot}", fontSize = 11.sp, color = Color.Gray)
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status Badge
        Surface(
            color = if (order.orderStatus == OrderStatus.DELIVERED) Color(0xFFE8F5E9) else Color(0xFFFEF3C7),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = order.orderStatus.name.replace("_", " "),
                color = if (order.orderStatus == OrderStatus.DELIVERED) VegGreen else Color(0xFFB45309),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9))
        Spacer(modifier = Modifier.height(10.dp))

        // Customer Details
        Text("👤 Customer Details", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(6.dp))
        Text(order.customerName, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(order.customerMobile, fontSize = 12.sp, color = Color(0xFF475569))
            IconButton(
                onClick = { onCallCustomer(order.customerMobile) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = VegGreen, modifier = Modifier.size(16.dp))
            }
        }
        Text("📍 ${order.deliveryAddress}", fontSize = 11.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9))
        Spacer(modifier = Modifier.height(10.dp))

        // Items Breakdown
        Text("🍱 Order Items & Add-ons", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(6.dp))

        order.itemsSummary.split(",").forEach { itemStr ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(itemStr.trim(), fontSize = 11.5.sp, color = Color(0xFF334155))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9))
        Spacer(modifier = Modifier.height(10.dp))

        // Payment Details
        Text("💳 Payment Details", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Amount", fontSize = 12.sp, color = Color.Gray)
            Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Advance Paid", fontSize = 12.sp, color = VegGreen)
            Text("₹${order.advancePaidAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = VegGreen)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Balance Due", fontSize = 12.sp, color = Color.Gray)
            Text("₹${order.balanceAmount.toInt()}", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9))
        Spacer(modifier = Modifier.height(10.dp))

        // Delivery Boy Details
        Text("🚚 Assigned Delivery Boy", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(6.dp))
        val boyName = order.deliveryBoyName.takeIf { !it.isNullOrBlank() } ?: "Ramesh Sharma"
        val boyMobile = order.deliveryBoyMobile.takeIf { !it.isNullOrBlank() } ?: "+91 98112 23344"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = Color(0xFFE2E8F0), modifier = Modifier.size(32.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(boyName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(boyMobile, fontSize = 10.5.sp, color = Color.Gray)
                }
            }
            Button(
                onClick = { onAssignDelivery(order) },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("Re-assign", fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9))
        Spacer(modifier = Modifier.height(10.dp))

        // Deg / Bartan Return Section (No Rental Policy & 9:30 AM Alert)
        Text("🍲 Deg & Food Container Return", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(6.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚠️", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("No Rental / Bartan Return Mandatory", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF92400E))
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    "Utensils: ${order.bartanDescription.ifBlank { "Authentic Steel Handi & Food Degs" }}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF78350F)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    "⏰ Rozana Subah 9:30 AM ko notification kitchen aur $boyName dono ko automatic trigger hoti hai jab tak bartan return nahi hota.",
                    fontSize = 10.sp,
                    color = Color(0xFFB45309),
                    lineHeight = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9))
        Spacer(modifier = Modifier.height(10.dp))

        // Order History Stepper
        Text("⏱️ Order History", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(6.dp))

        OrderLifecycleStep("Order Accepted", "12 May, 10:10 AM", true)
        OrderLifecycleStep("Moved to Confirm", "12 May, 10:11 AM", true)
        OrderLifecycleStep("Moved to Preparation", "12 May, 01:00 PM", order.orderStatus != OrderStatus.NEW && order.orderStatus != OrderStatus.CONFIRMED)
        OrderLifecycleStep("Marked as Ready", "12 May, 01:45 PM", order.orderStatus == OrderStatus.READY || order.orderStatus == OrderStatus.OUT_FOR_DELIVERY || order.orderStatus == OrderStatus.DELIVERED)
        OrderLifecycleStep("Out for Delivery", "12 May, 02:05 PM", order.orderStatus == OrderStatus.OUT_FOR_DELIVERY || order.orderStatus == OrderStatus.DELIVERED)
        OrderLifecycleStep("Delivered", "Pending", order.orderStatus == OrderStatus.DELIVERED)
    }
}

@Composable
private fun OrderLifecycleStep(title: String, time: String, isDone: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = if (isDone) VegGreen else Color(0xFFCBD5E1),
            modifier = Modifier.size(10.dp)
        ) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 11.5.sp, fontWeight = if (isDone) FontWeight.Medium else FontWeight.Normal, color = if (isDone) Color(0xFF0F172A) else Color.Gray, modifier = Modifier.weight(1f))
        Text(time, fontSize = 10.5.sp, color = Color.Gray)
    }
}

/**
 * Bottom Summary Panels from Image 2
 */
@Composable
private fun KitchenBottomSummaryPanels(
    orders: List<OrderEntity>,
    onNavigateTab: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Today's Summary
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(200.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Today's Summary (12 May)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Orders", fontSize = 11.sp, color = Color.Gray)
                        Text("128", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", fontSize = 11.sp, color = Color.Gray)
                        Text("₹ 2,34,700", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Advance Paid", fontSize = 11.sp, color = VegGreen)
                        Text("₹ 70,410", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Balance Due", fontSize = 11.sp, color = Color.Gray)
                        Text("₹ 1,64,290", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Payment Summary
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(200.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Payment Summary", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Prepaid Orders", fontSize = 11.sp, color = VegGreen)
                        Text("72 (₹1,24,600)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("COD Orders", fontSize = 11.sp, color = SaffronPrimary)
                        Text("56 (₹1,10,100)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                    }
                }
            }
        }

        // 3. Category Wise Summary
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(240.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Category Volume (Today)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("🍗 Chicken Biryani: 23 Kg", fontSize = 11.sp)
                    Text("🥩 Mutton Biryani: 12 Kg", fontSize = 11.sp)
                    Text("🥗 Veg Biryani & Chinese: 28 Kg", fontSize = 11.sp)
                    Text("🍲 Gravies & Raita: 14 L", fontSize = 11.sp)
                }
            }
        }

        // 4. Quick Actions
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(310.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { onNavigateTab(KitchenNavTabs.OFFLINE_BOOKING) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("📝 Offline Booking", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { onNavigateTab(KitchenNavTabs.ORDERS) },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Print KOT", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { onNavigateTab(KitchenNavTabs.CONTAINERS_CASH) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("🍲💵 Handi & Cash", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) SaffronPrimary else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color.White else Color(0xFFCBD5E1))
        }
    }
}

@Composable
private fun SidebarSubNavItem(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Color(0xFF1E293B) else Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 11.5.sp, color = if (isSelected) AmberSecondary else Color(0xFF94A3B8), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            Surface(
                color = if (isSelected) SaffronPrimary else Color(0xFF334155),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(count.toString(), fontSize = 9.5.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp))
            }
        }
    }
}

@Composable
private fun AssignDeliveryBoyDialog(
    order: OrderEntity,
    deliveryBoys: List<com.example.data.models.DeliveryBoyEntity>,
    onDismiss: () -> Unit,
    onAssign: (com.example.data.models.DeliveryBoyEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign Delivery Partner for #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column {
                deliveryBoys.forEach { boy ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAssign(boy) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFFE2E8F0), modifier = Modifier.size(32.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(boy.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${boy.mobile} • ${if (boy.isBusy) "Busy" else "Available"} • ${boy.todayCompletedDeliveries} delivered", fontSize = 11.sp, color = Color.Gray)
                        }
                        Button(
                            onClick = { onAssign(boy) },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Assign", fontSize = 11.sp)
                        }
                    }
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun handleOrderAction(
    viewModel: CaterersViewModel,
    action: String,
    order: OrderEntity,
    context: Context,
    onOpenAssign: (OrderEntity) -> Unit
) {
    when (action) {
        "PREPARATION" -> {
            viewModel.updateOrderStatus(order.orderId, OrderStatus.PREPARING)
            Toast.makeText(context, "Order #${order.orderId} moved to Preparation!", Toast.LENGTH_SHORT).show()
        }
        "MARK_READY" -> {
            viewModel.updateOrderStatus(order.orderId, OrderStatus.READY)
            Toast.makeText(context, "Order #${order.orderId} marked Ready for Dispatch!", Toast.LENGTH_SHORT).show()
        }
        "ASSIGN_DELIVERY" -> {
            onOpenAssign(order)
        }
    }
}
