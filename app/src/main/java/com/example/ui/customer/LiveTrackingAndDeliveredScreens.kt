package com.example.ui.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CatererEntity
import com.example.data.models.DeliveryBoyEntity
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.models.PaymentMethod
import com.example.data.models.PaymentStatus
import com.example.data.repository.CaterersViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen

@Composable
fun LiveTrackingScreen(
    orderId: String,
    viewModel: CaterersViewModel,
    onBack: () -> Unit,
    onOpenDeliveredView: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.ordersList.collectAsState()
    val caterers by viewModel.caterersList.collectAsState()
    val deliveryBoys by viewModel.deliveryBoysList.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val fallbackOrder = remember {
        OrderEntity(
            orderId = "CW-89210",
            customerName = "Rohan Verma",
            customerMobile = "+91 9876511223",
            deliveryAddress = "Flat 402, Green Park Apartments, Okhla Phase 3",
            catererId = "caterer_1",
            catererName = "A1 Huma Caterers",
            itemsSummary = "Special Dum Chicken Biryani (5.0 Kg), Shahi Zafrani Kheer (3.0 Litre)",
            totalAmount = 2560.0,
            advancePaidAmount = 768.0,
            balanceAmount = 1792.0,
            paymentMethod = PaymentMethod.UPI,
            paymentStatus = PaymentStatus.ADVANCE_PAID_30,
            orderStatus = OrderStatus.OUT_FOR_DELIVERY,
            deliveryDate = "2026-07-25",
            deliveryTimeSlot = "12:30 PM - 01:00 PM",
            deliveryOtp = "4829",
            deliveryBoyId = "db_1",
            deliveryBoyName = "Ramesh Sharma",
            deliveryBoyMobile = "+91 9811223344",
            isBartanPending = true,
            bartanDescription = "2 Metal Biryani Handi, 1 Kheer Pot"
        )
    }

    val order = orders.find { it.orderId == orderId }
        ?: orders.find { it.orderStatus == OrderStatus.OUT_FOR_DELIVERY || it.orderStatus == OrderStatus.PREPARING }
        ?: orders.firstOrNull()
        ?: fallbackOrder

    if (order.orderStatus == OrderStatus.DELIVERED) {
        LaunchedEffect(Unit) {
            onOpenDeliveredView()
        }
    }

    val caterer = caterers.find { it.id == order.catererId }
    val deliveryBoy = deliveryBoys.find { it.id == order.deliveryBoyId }

    var showOrderDetailsSheet by remember { mutableStateOf(false) }
    var showDemoSimulationControls by remember { mutableStateOf(true) }
    var showCancellationDialog by remember { mutableStateOf(false) }
    var showRescheduleDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // 1. Top Header Bar
        Surface(
            color = SaffronPrimary,
            contentColor = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("tracking_back")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Live Order Tracking",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Live Pulsing Dot Badge
                            PulsingLiveDotBadge()
                        }
                        Text(
                            text = "Order #${order.orderId} • ${order.catererName}",
                            fontSize = 12.sp,
                            color = AmberSecondary,
                            maxLines = 1
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(order.orderId))
                            viewModel.showFeedback("Order #${order.orderId} copied to clipboard!")
                        },
                        modifier = Modifier.testTag("copy_order_id_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy ID", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    IconButton(
                        onClick = {
                            viewModel.showFeedback("Refreshing live GPS location...")
                        },
                        modifier = Modifier.testTag("refresh_tracking_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 2. Interactive Real-Time GPS Animated Route Map
            item {
                LiveAnimatedGpsMapCard(
                    order = order,
                    caterer = caterer,
                    deliveryBoy = deliveryBoy
                )
            }

            // 3. Current Live Status Hero & ETA Card
            item {
                LiveStatusHeroCard(
                    order = order,
                    onViewDetailsClick = { showOrderDetailsSheet = !showOrderDetailsSheet }
                )
            }

            // 4. Secure 4-Digit Delivery Verification OTP Card
            item {
                CustomerDeliveryOtpCard(
                    otp = order.deliveryOtp,
                    orderStatus = order.orderStatus,
                    onCopyOtp = {
                        clipboardManager.setText(AnnotatedString(order.deliveryOtp))
                        viewModel.showFeedback("Delivery OTP ${order.deliveryOtp} copied!")
                    },
                    onShareOtp = {
                        viewModel.showFeedback("Sharing OTP ${order.deliveryOtp} via WhatsApp...")
                    }
                )
            }

            // 5. Real-Time 6-Stage Progress Pipeline
            item {
                LiveTrackingPipelineCard(order = order)
            }

            // 6. Assigned Delivery Partner Card (If assigned)
            item {
                AssignedDeliveryPartnerCard(
                    order = order,
                    deliveryBoy = deliveryBoy,
                    onCall = { viewModel.showFeedback("Dialing Delivery Partner: ${order.deliveryBoyMobile ?: "+91 98765 43210"}") },
                    onWhatsApp = { viewModel.showFeedback("Opening WhatsApp Chat with ${order.deliveryBoyName ?: "Delivery Valet"}...") }
                )
            }

            // 7. Kitchen Details & Head Chef Contact Card
            item {
                KitchenContactCard(
                    caterer = caterer,
                    catererName = order.catererName,
                    onCall = { viewModel.showFeedback("Calling Kitchen Manager: ${caterer?.ownerMobile ?: "+91 99887 76655"}") },
                    onWhatsApp = { viewModel.showFeedback("Opening WhatsApp support with ${order.catererName}...") }
                )
            }

            // 8. Order Itemized Menu Summary & Utensils (Degs) Tracker (Collapsible)
            item {
                CateringOrderSummaryCard(
                    order = order,
                    isExpanded = showOrderDetailsSheet,
                    onToggleExpand = { showOrderDetailsSheet = !showOrderDetailsSheet },
                    onDownloadInvoice = { viewModel.showFeedback("Downloading Official Tax Invoice for #${order.orderId}...") }
                )
            }

            // 9. 24x7 Catering Hotline & Help Desk
            item {
                EmergencyHelplineCard(
                    onCallSupport = { viewModel.showFeedback("Connecting to 24x7 Caterers Wale VIP Support: 1800-419-CATER") }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 11. Cancellation & Reschedule Action Card
            if (order.orderStatus != OrderStatus.DELIVERED &&
                order.orderStatus != OrderStatus.CANCELLED &&
                order.orderStatus != OrderStatus.OUT_FOR_DELIVERY &&
                !order.isNonCancellable
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text("Change of Plans? (तारीख या रद्द करें)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFF0F172A))
                                    Text("Check 3-tier refund policy or reschedule within 7 days", fontSize = 10.5.sp, color = Color(0xFF64748B))
                                }
                                OutlinedButton(
                                    onClick = { showCancellationDialog = true },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("tracking_cancel_order_btn")
                                ) {
                                    Text("Cancel / Reschedule", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Customer Cancellation Dialog
        if (showCancellationDialog) {
            CustomerCancellationDialog(
                order = order,
                onDismiss = { showCancellationDialog = false },
                onConfirmCancel = { reason ->
                    viewModel.cancelOrderByCustomer(order.orderId, reason)
                    showCancellationDialog = false
                    onBack()
                },
                onSwitchToReschedule = {
                    showCancellationDialog = false
                    showRescheduleDialog = true
                }
            )
        }

        // Customer Reschedule Dialog
        if (showRescheduleDialog) {
            CustomerRescheduleDialog(
                order = order,
                onDismiss = { showRescheduleDialog = false },
                onConfirmReschedule = { newDate, newSlot ->
                    viewModel.rescheduleOrderByCustomer(order.orderId, newDate, newSlot)
                    showRescheduleDialog = false
                }
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Sub-Components
// -------------------------------------------------------------------------------------------------

@Composable
private fun PulsingLiveDotBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_live")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Surface(
        color = Color(0xFF10B981).copy(alpha = alpha),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color.White, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "LIVE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun LiveAnimatedGpsMapCard(
    order: OrderEntity,
    caterer: CatererEntity?,
    deliveryBoy: DeliveryBoyEntity?
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gps_wave")
    val waveRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_radius"
    )
    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_alpha"
    )

    // Animated progress along path based on status
    val targetProgress = when (order.orderStatus) {
        OrderStatus.NEW, OrderStatus.ACCEPTED, OrderStatus.CONFIRMED -> 0.05f
        OrderStatus.PREPARING -> 0.25f
        OrderStatus.READY -> 0.45f
        OrderStatus.ASSIGNED_DELIVERY -> 0.60f
        OrderStatus.OUT_FOR_DELIVERY -> 0.82f
        OrderStatus.DELIVERED -> 1.0f
        OrderStatus.CANCELLED -> 0.0f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "van_progress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .shadow(6.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Simulated Vector Map Background Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Dark grid pattern
                val stepX = w / 8
                val stepY = h / 6
                for (i in 0..8) {
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(i * stepX, 0f),
                        end = Offset(i * stepX, h),
                        strokeWidth = 1f
                    )
                }
                for (j in 0..6) {
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(0f, j * stepY),
                        end = Offset(w, j * stepY),
                        strokeWidth = 1f
                    )
                }

                // Road Network Lines
                val roadColor = Color(0xFF334155)
                drawLine(roadColor, Offset(0f, h * 0.45f), Offset(w, h * 0.45f), strokeWidth = 12f)
                drawLine(roadColor, Offset(w * 0.35f, 0f), Offset(w * 0.35f, h), strokeWidth = 10f)
                drawLine(roadColor, Offset(w * 0.7f, 0f), Offset(w * 0.7f, h), strokeWidth = 10f)
                drawLine(roadColor, Offset(0f, h * 0.8f), Offset(w, h * 0.7f), strokeWidth = 8f)

                // Route Curve from Kitchen (Left) to Customer (Right)
                val startX = w * 0.12f
                val startY = h * 0.72f
                val endX = w * 0.88f
                val endY = h * 0.28f

                val control1X = w * 0.35f
                val control1Y = h * 0.45f
                val control2X = w * 0.65f
                val control2Y = h * 0.45f

                val routePath = Path().apply {
                    moveTo(startX, startY)
                    cubicTo(control1X, control1Y, control2X, control2Y, endX, endY)
                }

                // Background route stroke
                drawPath(
                    path = routePath,
                    color = Color(0xFF475569),
                    style = Stroke(width = 8f, cap = StrokeCap.Round)
                )

                // Active Completed Route Stroke (Vibrant Gold/Amber)
                drawPath(
                    path = routePath,
                    color = Color(0xFFF59E0B),
                    style = Stroke(
                        width = 6f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f), 0f)
                    )
                )

                // Origin Kitchen Marker
                drawCircle(
                    color = Color(0xFFEA580C),
                    radius = 16f,
                    center = Offset(startX, startY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 7f,
                    center = Offset(startX, startY)
                )

                // Destination Customer Marker
                drawCircle(
                    color = Color(0xFF10B981),
                    radius = 18f,
                    center = Offset(endX, endY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 8f,
                    center = Offset(endX, endY)
                )

                // Delivery Van Current Position along Bezier
                val t = animatedProgress.coerceIn(0f, 1f)
                val oneMinusT = 1f - t
                val riderX = (oneMinusT * oneMinusT * oneMinusT * startX) +
                        (3 * oneMinusT * oneMinusT * t * control1X) +
                        (3 * oneMinusT * t * t * control2X) +
                        (t * t * t * endX)
                val riderY = (oneMinusT * oneMinusT * oneMinusT * startY) +
                        (3 * oneMinusT * oneMinusT * t * control1Y) +
                        (3 * oneMinusT * t * t * control2Y) +
                        (t * t * t * endY)

                // Animated Radar Wave around Delivery Van
                if (order.orderStatus == OrderStatus.OUT_FOR_DELIVERY) {
                    drawCircle(
                        color = Color(0xFF38BDF8).copy(alpha = waveAlpha),
                        radius = waveRadius,
                        center = Offset(riderX, riderY),
                        style = Stroke(width = 3f)
                    )
                }

                // Van Pin Base
                drawCircle(
                    color = Color(0xFF0284C7),
                    radius = 20f,
                    center = Offset(riderX, riderY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 14f,
                    center = Offset(riderX, riderY)
                )
            }

            // Map Overlays: Origin Label (Top Left) & Destination Label (Top Right)
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Surface(
                    color = Color(0xDD0F172A),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFFEA580C), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Kitchen: ${order.catererName.take(16)}...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Real-Time Speed & Distance Overlay Pill (Bottom Left)
            Surface(
                color = Color(0xEE0F172A),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (order.orderStatus == OrderStatus.OUT_FOR_DELIVERY) Icons.Default.Speed else Icons.Default.Kitchen,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (order.orderStatus) {
                            OrderStatus.OUT_FOR_DELIVERY -> "Live: 28 km/h • 1.8 km away"
                            OrderStatus.PREPARING -> "Kitchen Prep: 85°C Thermal Sealed"
                            OrderStatus.READY, OrderStatus.ASSIGNED_DELIVERY -> "Van Loading & Sanitization"
                            OrderStatus.DELIVERED -> "Delivered at Destination"
                            else -> "Order Confirmed by Master Chef"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // ETA Pill Overlay (Bottom Right)
            Surface(
                color = if (order.orderStatus == OrderStatus.DELIVERED) VegGreen else SaffronPrimary,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (order.orderStatus) {
                            OrderStatus.DELIVERED -> "Arrived ✅"
                            OrderStatus.OUT_FOR_DELIVERY -> "ETA: 18 mins"
                            OrderStatus.PREPARING -> "Slot: ${order.deliveryTimeSlot}"
                            else -> "Est: ${order.deliveryTimeSlot}"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveStatusHeroCard(
    order: OrderEntity,
    onViewDetailsClick: () -> Unit
) {
    val (statusTitle, statusSubtitle, statusIcon, iconBg, progressValue) = when (order.orderStatus) {
        OrderStatus.NEW, OrderStatus.ACCEPTED, OrderStatus.CONFIRMED -> {
            LiveStatusData(
                title = "Order Confirmed & Acknowledged",
                subtitle = "Master Chef has received your catering requirements and scheduled bulk ingredient prep.",
                icon = Icons.Default.CheckCircle,
                iconBg = Color(0xFF3B82F6),
                progress = 0.20f
            )
        }
        OrderStatus.PREPARING -> {
            LiveStatusData(
                title = "Master Chef Cooking in Progress 🔥",
                subtitle = "Degs are simmering in traditional firewood/charcoal bhattis with premium spices & ghee.",
                icon = Icons.Default.LocalFireDepartment,
                iconBg = Color(0xFFEA580C),
                progress = 0.45f
            )
        }
        OrderStatus.READY -> {
            LiveStatusData(
                title = "Packed & Thermal Temperature Sealed",
                subtitle = "Food is hygienic sealed in insulated degs & chafing trays for maximum freshness.",
                icon = Icons.Default.Inventory,
                iconBg = Color(0xFF8B5CF6),
                progress = 0.65f
            )
        }
        OrderStatus.ASSIGNED_DELIVERY -> {
            LiveStatusData(
                title = "Delivery Valet Assigned & Vehicle Loading",
                subtitle = "${order.deliveryBoyName ?: "Delivery Valet"} is loading hot containers onto the insulated catering van.",
                icon = Icons.Default.DeliveryDining,
                iconBg = Color(0xFF0284C7),
                progress = 0.75f
            )
        }
        OrderStatus.OUT_FOR_DELIVERY -> {
            LiveStatusData(
                title = "Catering Van Out for Delivery 🚚",
                subtitle = "En route to your venue. Driver will call on arrival. Keep your 4-digit OTP ready.",
                icon = Icons.Default.LocalShipping,
                iconBg = Color(0xFFF59E0B),
                progress = 0.90f
            )
        }
        OrderStatus.DELIVERED -> {
            LiveStatusData(
                title = "Order Delivered & Verified ✅",
                subtitle = "OTP verified and catering containers successfully handed over at your venue.",
                icon = Icons.Default.CheckCircle,
                iconBg = VegGreen,
                progress = 1.0f
            )
        }
        OrderStatus.CANCELLED -> {
            LiveStatusData(
                title = "Order Cancelled",
                subtitle = "This order was cancelled. Please contact kitchen support for refund details.",
                icon = Icons.Default.Warning,
                iconBg = Color(0xFFEF4444),
                progress = 0.0f
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(statusIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = statusTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Event Date: ${order.deliveryDate} (${order.deliveryTimeSlot})",
                        fontSize = 12.sp,
                        color = SaffronPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = statusSubtitle,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Progress Indicator
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Delivery Journey Progress",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "${(progressValue * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SaffronPrimary,
                    trackColor = Color(0xFFE2E8F0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Drop: ${order.deliveryAddress.take(24)}...",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                TextButton(
                    onClick = onViewDetailsClick,
                    modifier = Modifier.testTag("toggle_order_items_button")
                ) {
                    Text("View Menu Items (${order.itemsSummary.split(",").size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                }
            }
        }
    }
}

private data class LiveStatusData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBg: Color,
    val progress: Float
)

@Composable
private fun CustomerDeliveryOtpCard(
    otp: String,
    orderStatus: OrderStatus,
    onCopyOtp: () -> Unit,
    onShareOtp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1010)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(AmberSecondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delivery Verification OTP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    color = Color(0xFF374151),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "COMPULSORY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Big Bold High-Contrast OTP Digits Display
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                otp.padEnd(4, '0').take(4).forEach { digit ->
                    Surface(
                        color = SaffronPrimary,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(54.dp, 60.dp),
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = digit.toString(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyOtp,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4B5563)),
                    modifier = Modifier.testTag("copy_otp_btn")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy OTP", fontSize = 11.sp)
                }

                Button(
                    onClick = onShareOtp,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                    modifier = Modifier.testTag("share_otp_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share with Venue Host", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFF2B1C1C), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Security Tip: Share this OTP ONLY after verifying hot food degs & parcel seal upon arrival.",
                    fontSize = 10.5.sp,
                    color = Color(0xFFD1D5DB),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun LiveTrackingPipelineCard(order: OrderEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Order Progress Timeline",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Real-time sync",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val currentStatus = order.orderStatus

            PipelineStepItem(
                stepNumber = 1,
                title = "Order Placed & Acknowledged",
                subtitle = "Catering order confirmed with ${order.catererName}",
                time = "Confirmed",
                isCompleted = true,
                isCurrent = currentStatus == OrderStatus.ACCEPTED || currentStatus == OrderStatus.CONFIRMED,
                isLast = false
            )

            PipelineStepItem(
                stepNumber = 2,
                title = "Master Chef Cooking Bulk Degs",
                subtitle = "Traditional dum cooking & marination active in kitchen",
                time = if (currentStatus >= OrderStatus.PREPARING) "In Kitchen" else "Upcoming",
                isCompleted = currentStatus >= OrderStatus.PREPARING,
                isCurrent = currentStatus == OrderStatus.PREPARING,
                isLast = false
            )

            PipelineStepItem(
                stepNumber = 3,
                title = "Hygienic Packaging & Temperature Seal",
                subtitle = "Degs & serving utensils quality checked & sealed hot",
                time = if (currentStatus >= OrderStatus.READY) "Packed" else "Upcoming",
                isCompleted = currentStatus >= OrderStatus.READY,
                isCurrent = currentStatus == OrderStatus.READY,
                isLast = false
            )

            PipelineStepItem(
                stepNumber = 4,
                title = "Delivery Valet Assigned",
                subtitle = order.deliveryBoyName?.let { "Assigned to $it (${order.deliveryBoyMobile ?: "Driver"})" }
                    ?: "Assigning closest temperature-controlled catering van",
                time = if (currentStatus >= OrderStatus.ASSIGNED_DELIVERY) "Assigned" else "Pending",
                isCompleted = currentStatus >= OrderStatus.ASSIGNED_DELIVERY,
                isCurrent = currentStatus == OrderStatus.ASSIGNED_DELIVERY,
                isLast = false
            )

            PipelineStepItem(
                stepNumber = 5,
                title = "Catering Van Out for Delivery",
                subtitle = "Driver is on the way with insulated warm containers",
                time = if (currentStatus >= OrderStatus.OUT_FOR_DELIVERY) "En-Route" else "Upcoming",
                isCompleted = currentStatus >= OrderStatus.OUT_FOR_DELIVERY,
                isCurrent = currentStatus == OrderStatus.OUT_FOR_DELIVERY,
                isLast = false
            )

            PipelineStepItem(
                stepNumber = 6,
                title = "Handover & OTP Verification Complete",
                subtitle = "Delivery verified with 4-digit OTP at venue",
                time = if (currentStatus == OrderStatus.DELIVERED) "Delivered" else "Pending",
                isCompleted = currentStatus == OrderStatus.DELIVERED,
                isCurrent = currentStatus == OrderStatus.DELIVERED,
                isLast = true
            )
        }
    }
}

@Composable
private fun PipelineStepItem(
    stepNumber: Int,
    title: String,
    subtitle: String,
    time: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        // Vertical Indicator Line & Circle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = when {
                            isCompleted -> VegGreen
                            isCurrent -> SaffronPrimary
                            else -> Color(0xFFE2E8F0)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(
                        text = "$stepNumber",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) Color.White else Color(0xFF64748B)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(if (isCompleted) VegGreen else Color(0xFFE2E8F0))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    color = if (isCurrent) SaffronPrimary else if (isCompleted) Color(0xFF0F172A) else Color(0xFF94A3B8)
                )

                Surface(
                    color = if (isCurrent) Color(0xFFFEF3C7) else if (isCompleted) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = time,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) Color(0xFFD97706) else if (isCompleted) VegGreen else Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = Color(0xFF64748B),
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun AssignedDeliveryPartnerCard(
    order: OrderEntity,
    deliveryBoy: DeliveryBoyEntity?,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE0F2FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = order.deliveryBoyName ?: "Zaid Khan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(12.dp))
                                    Text("4.9", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                }
                            }
                        }
                        Text(
                            text = "Insulated Catering Van • DL 01 VB 4821",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "Trained in Heavy Degs & Buffet Setup",
                            fontSize = 10.5.sp,
                            color = VegGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onWhatsApp,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFDCFCE7), CircleShape)
                            .testTag("whatsapp_driver_btn")
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = VegGreen, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = onCall,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFFEF3C7), CircleShape)
                            .testTag("call_driver_btn")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun KitchenContactCard(
    caterer: CatererEntity?,
    catererName: String,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        .size(44.dp)
                        .background(Color(0xFFFFEDD5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Kitchen, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = catererName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, contentDescription = "FSSAI Verified", tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = "FSSAI License: ${caterer?.fssaiLicense ?: "13321008000412"}",
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = caterer?.address?.take(28) ?: "Okhla Phase 2, Central Kitchen Hub",
                        fontSize = 10.5.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onWhatsApp,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFDCFCE7), CircleShape)
                        .testTag("whatsapp_kitchen_btn")
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = VegGreen, modifier = Modifier.size(16.dp))
                }

                IconButton(
                    onClick = onCall,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFFEDD5), CircleShape)
                        .testTag("call_kitchen_btn")
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFFEA580C), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun CateringOrderSummaryCard(
    order: OrderEntity,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onDownloadInvoice: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Catering Order Summary & Bill",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                Text(
                    text = if (isExpanded) "Hide ▲" else "View Details ▼",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SaffronPrimary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Selected Food Menu Items:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(4.dp))

                    order.itemsSummary.split(",").forEach { itemLine ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(SaffronPrimary, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(itemLine.trim(), fontSize = 12.sp, color = Color(0xFF475569))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Bartan / Equipment Return Tracker
                    if (order.isBartanPending) {
                        Surface(
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Bartan & Degs Tracking", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFC2410C))
                                    Text(
                                        "${order.bartanDescription} (Scheduled for pickup next morning)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF7C2D12)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Payment Breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Catering Amount", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Payment Status", fontSize = 12.sp, color = Color(0xFF64748B))
                        Surface(
                            color = if (order.paymentStatus == PaymentStatus.FULL_PAID) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = order.paymentStatus.name.replace("_", " "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (order.paymentStatus == PaymentStatus.FULL_PAID) VegGreen else Color(0xFFD97706),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onDownloadInvoice,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        modifier = Modifier.fillMaxWidth().testTag("download_invoice_btn")
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download GST Tax Invoice & Bill", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmergencyHelplineCard(
    onCallSupport: () -> Unit
) {
    Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Headphones, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Need Help with your Event?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Text("24/7 Catering Operations Hotline", fontSize = 11.sp, color = Color(0xFF94A3B8))
                }
            }

            Button(
                onClick = onCallSupport,
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("call_catering_hotline")
            ) {
                Text("Call Support", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private operator fun OrderStatus.compareTo(other: OrderStatus): Int {
    return this.ordinal.compareTo(other.ordinal)
}

@Composable
fun DeliveredScreen(
    order: OrderEntity,
    viewModel: CaterersViewModel,
    onBackToHome: () -> Unit,
    onOpenFeedbackScreen: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var rating by remember { mutableFloatStateOf(5.0f) }
    var reviewComment by remember { mutableStateOf("") }
    var reviewSubmitted by remember { mutableStateOf(order.userRating > 0f) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F6F0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(VegGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Order Delivered Successfully!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
            Text("Order #${order.orderId}", fontSize = 14.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Delivery Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Caterer: ${order.catererName}", fontSize = 13.sp)
                    Text("Items: ${order.itemsSummary}", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Total Paid: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = VegGreen, fontSize = 14.sp)
                    if (order.isBartanPending) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                "Bartan/Utensil Return Pending: ${order.bartanDescription}",
                                fontSize = 11.sp,
                                color = Color(0xFFE65100),
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rate & Review Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Rate & Review Your Caterer", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.Center) {
                        for (i in 1..5) {
                            IconButton(onClick = { rating = i.toFloat() }, modifier = Modifier.testTag("rate_star_$i")) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Star $i",
                                    tint = if (i <= rating) AmberSecondary else Color.LightGray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        placeholder = { Text("Write your review about food taste, quantity & service...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_input"),
                        enabled = !reviewSubmitted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!reviewSubmitted) {
                        Button(
                            onClick = {
                                viewModel.submitReview(order.orderId, rating, reviewComment)
                                reviewSubmitted = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            modifier = Modifier.testTag("submit_review_button")
                        ) {
                            Text("Quick Submit Rating")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { onOpenFeedbackScreen(order.orderId) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SaffronPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("open_detailed_feedback_center_btn")
                        ) {
                            Text("⭐ Detailed Feedback & Photos (+50 Points)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        Text("Thank you! Review Submitted ⭐", color = VegGreen, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { onOpenFeedbackScreen(order.orderId) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SaffronPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View / Edit Detailed Feedback Wall", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBackToHome,
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("delivered_go_home")
            ) {
                Text("Back to Home")
            }
        }
    }
}

