package com.example.ui.kitchen

import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.repository.CaterersViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Data structures for Monthly Analytics & Performance Dashboard
 */
data class MonthlyMetric(
    val monthName: String,
    val shortName: String,
    val grossRevenue: Double,
    val netEarnings: Double,
    val ordersCount: Int,
    val totalVolumeKg: Double
)

data class DishPerformance(
    val id: String,
    val name: String,
    val category: String,
    val ordersCount: Int,
    val quantitySold: Double,
    val unit: String,
    val revenue: Double,
    val rating: Float,
    val marginPercentage: Double,
    val isTopSeller: Boolean = false
)

data class CategoryShare(
    val categoryName: String,
    val revenue: Double,
    val percentage: Float,
    val color: Color
)

data class PeakHourSlot(
    val timeLabel: String,
    val orderCount: Int,
    val revenueContribution: Double,
    val percentage: Float
)

@Composable
fun KitchenAnalyticsDashboard(
    viewModel: CaterersViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.ordersList.collectAsState()
    val menuItems by viewModel.menuItemsList.collectAsState()
    val caterers by viewModel.caterersList.collectAsState()
    val kitchen = caterers.find { it.id == "caterer_1" } ?: caterers.firstOrNull()

    var selectedTimeframe by remember { mutableStateOf("6_MONTHS") } // 6_MONTHS, CURRENT_MONTH, LAST_MONTH, YEAR
    var selectedChartMetric by remember { mutableStateOf("REVENUE") } // REVENUE, ORDERS, VOLUME
    var selectedMonthIndex by remember { mutableStateOf<Int?>(null) }
    var selectedCategoryIndex by remember { mutableStateOf<Int?>(null) }

    // Mock dataset representing realistic catering business cycle for A1 Huma Kitchen Partner
    val monthlyData = remember {
        listOf(
            MonthlyMetric("March 2026", "Mar", 285000.0, 242250.0, 52, 620.0),
            MonthlyMetric("April 2026", "Apr", 340000.0, 289000.0, 64, 780.0),
            MonthlyMetric("May 2026 (Wedding Season)", "May", 510000.0, 433500.0, 94, 1250.0),
            MonthlyMetric("June 2026", "Jun", 390000.0, 331500.0, 72, 890.0),
            MonthlyMetric("July 2026", "Jul", 445000.0, 378250.0, 81, 1020.0),
            MonthlyMetric("August 2026 (Current)", "Aug", 482000.0, 409700.0, 88, 1140.0)
        )
    }

    // Dynamic Top Performing Dishes
    val topDishes = remember {
        listOf(
            DishPerformance("d1", "Mutton Rogan Josh Dum Biryani (Degh)", "Biryani & Rice", 142, 568.0, "Kg", 170400.0, 4.9f, 42.0, true),
            DishPerformance("d2", "Chicken Dum Biryani (Hyderabadi)", "Biryani & Rice", 185, 555.0, "Kg", 138750.0, 4.8f, 38.5, true),
            DishPerformance("d3", "Shahi Paneer Butter Masala Handi", "Main Gravies", 120, 360.0, "Kg", 90000.0, 4.7f, 45.0),
            DishPerformance("d4", "Zafrani Firni & Shahi Tukda Pack", "Royal Desserts", 210, 420.0, "Portions", 52500.0, 4.9f, 52.0),
            DishPerformance("d5", "Galouti & Seekh Kebab Platter", "Tandoori Starters", 95, 285.0, "Plates", 48450.0, 4.8f, 40.0),
            DishPerformance("d6", "Dal Makhani Amritsari (Slow-Cooked)", "Main Gravies", 88, 264.0, "Kg", 39600.0, 4.7f, 48.0)
        )
    }

    val categoryShares = remember {
        listOf(
            CategoryShare("Mughlai & Biryanis", 309150.0, 57.3f, SaffronPrimary),
            CategoryShare("Main Gravies & Handi", 129600.0, 24.0f, AmberSecondary),
            CategoryShare("Royal Desserts", 52500.0, 9.7f, Color(0xFF10B981)),
            CategoryShare("Tandoor & Starters", 48450.0, 9.0f, Color(0xFF6366F1))
        )
    }

    val peakHours = remember {
        listOf(
            PeakHourSlot("Afternoon Lunch (12 PM - 3 PM)", 38, 205000.0, 42.5f),
            PeakHourSlot("Evening Banquet (7 PM - 10:30 PM)", 44, 245000.0, 50.8f),
            PeakHourSlot("Morning Breakfast & Corporate", 6, 32000.0, 6.7f)
        )
    }

    val totalGrossRevenue = monthlyData.sumOf { it.grossRevenue }
    val totalNetEarnings = monthlyData.sumOf { it.netEarnings }
    val totalOrders = monthlyData.sumOf { it.ordersCount }
    val averageOrderValue = if (totalOrders > 0) totalGrossRevenue / totalOrders else 0.0
    val currentMonthRevenue = monthlyData.last().grossRevenue
    val previousMonthRevenue = monthlyData[monthlyData.size - 2].grossRevenue
    val revenueGrowthPercent = ((currentMonthRevenue - previousMonthRevenue) / previousMonthRevenue) * 100

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Dashboard Header & Timeframe Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = AmberSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Partner Performance Analytics",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                            }
                            Text(
                                "${kitchen?.name ?: "A1 Huma Central Kitchen"} • Live Data",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (revenueGrowthPercent >= 0) Icons.Default.TrendingUp else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (revenueGrowthPercent >= 0) VegGreen else Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "${if (revenueGrowthPercent >= 0) "+" else ""}${String.format(Locale.getDefault(), "%.1f", revenueGrowthPercent)}% MoM",
                                    color = if (revenueGrowthPercent >= 0) VegGreen else Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Filter chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val filters = listOf(
                            "6_MONTHS" to "Last 6 Months",
                            "CURRENT_MONTH" to "Aug 2026 (Active)",
                            "JULY" to "July 2026",
                            "MAY" to "May Peak Season"
                        )
                        items(filters) { (key, label) ->
                            val isSelected = selectedTimeframe == key
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { selectedTimeframe = key }
                                    .testTag("filter_$key"),
                                color = if (isSelected) SaffronPrimary else Color(0xFF1E293B),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. High Level Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsSummaryCard(
                    title = "Monthly Gross Revenue",
                    value = "₹${currencyFormat.format(monthlyData.last().grossRevenue).replace("₹", "").trim()}",
                    subtitle = "vs ₹${currencyFormat.format(monthlyData[monthlyData.size - 2].grossRevenue).replace("₹", "").trim()} last month",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = SaffronPrimary,
                    modifier = Modifier.weight(1f)
                )

                AnalyticsSummaryCard(
                    title = "Net Partner Payout",
                    value = "₹${currencyFormat.format(monthlyData.last().netEarnings).replace("₹", "").trim()}",
                    subtitle = "After 70% delivery & 10% platform",
                    icon = Icons.Default.TrendingUp,
                    accentColor = VegGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsSummaryCard(
                    title = "Total Orders Volume",
                    value = "${monthlyData.last().ordersCount} Orders",
                    subtitle = "Avg ${String.format(Locale.getDefault(), "%.0f", monthlyData.last().totalVolumeKg)} Kg Food Cooked",
                    icon = Icons.Default.ShoppingBag,
                    accentColor = Color(0xFF0288D1),
                    modifier = Modifier.weight(1f)
                )

                AnalyticsSummaryCard(
                    title = "Avg Order Value (AOV)",
                    value = "₹${String.format(Locale.getDefault(), "%,.0f", monthlyData.last().grossRevenue / monthlyData.last().ordersCount)}",
                    subtitle = "High Ticket Bulk Deghs",
                    icon = Icons.Default.Restaurant,
                    accentColor = AmberSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Main Chart: Interactive Monthly Earnings & Revenue Curve (Recharts / D3 Style)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chart_monthly_revenue"),
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
                        Column {
                            Text(
                                "Monthly Revenue & Earnings Trend",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                "Interactive Smooth Area Chart (Tap points for details)",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Metric switcher
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedChartMetric = "REVENUE" },
                                color = if (selectedChartMetric == "REVENUE") SaffronPrimary.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Gross",
                                    color = if (selectedChartMetric == "REVENUE") SaffronPrimary else Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedChartMetric = "NET" },
                                color = if (selectedChartMetric == "NET") VegGreen.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Net Payout",
                                    color = if (selectedChartMetric == "NET") VegGreen else Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Canvas Line & Area Chart
                    MonthlyRevenueAreaChart(
                        monthlyData = monthlyData,
                        isNetMetric = selectedChartMetric == "NET",
                        selectedIndex = selectedMonthIndex,
                        onPointSelected = { selectedMonthIndex = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Selected Month Insight popup
                    if (selectedMonthIndex != null && selectedMonthIndex!! in monthlyData.indices) {
                        val sel = monthlyData[selectedMonthIndex!!]
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(sel.monthName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                    Text("${sel.ordersCount} Orders • ${sel.totalVolumeKg.toInt()} Kg Total Cooked", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("₹${String.format(Locale.getDefault(), "%,.0f", sel.grossRevenue)}", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = SaffronPrimary)
                                    Text("Net: ₹${String.format(Locale.getDefault(), "%,.0f", sel.netEarnings)}", fontWeight = FontWeight.Medium, fontSize = 11.sp, color = VegGreen)
                                }
                            }
                        }
                    }

                    // Legend
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(10.dp).background(SaffronPrimary, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gross Sales", fontSize = 11.sp, color = Color(0xFF475569))

                        Spacer(modifier = Modifier.width(16.dp))

                        Box(modifier = Modifier.size(10.dp).background(VegGreen, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Net Partner Payout", fontSize = 11.sp, color = Color(0xFF475569))
                    }
                }
            }
        }

        // 4. Order Volume Bar Histogram (D3 Bar Style)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chart_order_volume"),
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
                        Column {
                            Text(
                                "Monthly Order Volume & Degh Count",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                "Total bulk event bookings per month",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Surface(
                            color = Color(0xFFE0F2FE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "${totalOrders} Total Orders",
                                color = Color(0xFF0369A1),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OrderVolumeBarChart(
                        monthlyData = monthlyData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            }
        }

        // 5. Top-Performing Dishes & Menu Revenue Matrix
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("section_top_dishes"),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFEA580C),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Top-Performing Kitchen Dishes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Text(
                            "Revenue & Volume",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val maxRevenue = topDishes.maxOf { it.revenue }

                    topDishes.forEachIndexed { index, dish ->
                        DishPerformanceRow(
                            rank = index + 1,
                            dish = dish,
                            maxRevenue = maxRevenue
                        )
                        if (index < topDishes.size - 1) {
                            Divider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }

        // 6. Cuisine & Category Revenue Share (Donut / Pie Chart)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chart_category_share"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Cuisine Category Revenue Breakdown",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        "Contribution percentage by menu segment",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Donut Chart
                        CategoryDonutChart(
                            categories = categoryShares,
                            totalAmount = categoryShares.sumOf { it.revenue },
                            modifier = Modifier.size(150.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Category Legends
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categoryShares.forEach { cat ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(cat.color, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            cat.categoryName,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF1E293B)
                                        )
                                    }
                                    Text(
                                        "${cat.percentage}%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = cat.color
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Peak Kitchen Slot Distribution
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Kitchen Peak Delivery Windows",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        "Dispatch volume and staff readiness schedule",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    peakHours.forEach { slot ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(slot.timeLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                                Text("${slot.orderCount} Orders (${slot.percentage}%)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SaffronPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(slot.percentage / 100f)
                                        .height(6.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(AmberSecondary, SaffronPrimary)
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 8. Quick Export & PDF Financial Report Trigger
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFFDE68A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Monthly P&L Audit Statement",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                "Download GST & Payout report for CA filing",
                                fontSize = 11.sp,
                                color = Color(0xFFB45309)
                            )
                        }
                    }

                    Button(
                        onClick = { /* Export simulation */ },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Export CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF64748B))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(accentColor.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = Color(0xFF94A3B8))
        }
    }
}

/**
 * Native Canvas Smooth Area & Line Chart with Gradient Fill, Gridlines, and Tap Tooltip
 */
@Composable
fun MonthlyRevenueAreaChart(
    monthlyData: List<MonthlyMetric>,
    isNetMetric: Boolean,
    selectedIndex: Int?,
    onPointSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (monthlyData.isEmpty()) return

    val maxVal = monthlyData.maxOf { if (isNetMetric) it.netEarnings else it.grossRevenue } * 1.15f
    val minVal = 0.0

    val primaryColor = if (isNetMetric) VegGreen else SaffronPrimary
    val secondaryColor = if (isNetMetric) Color(0xFF86EFAC) else AmberSecondary

    Canvas(
        modifier = modifier
            .pointerInput(monthlyData) {
                detectTapGestures { offset ->
                    val spacing = size.width / (monthlyData.size - 1).coerceAtLeast(1)
                    val clickedIdx = ((offset.x + spacing / 2) / spacing).toInt().coerceIn(0, monthlyData.size - 1)
                    onPointSelected(clickedIdx)
                }
            }
    ) {
        val width = size.width
        val height = size.height - 40.dp.toPx() // leave room for labels
        val pointSpacing = width / (monthlyData.size - 1).coerceAtLeast(1)

        // Draw dashed horizontal grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = height * (1f - (i.toFloat() / gridLines))
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
            )

            // Grid y-axis label (in Lakhs)
            val gridVal = (maxVal * (i.toFloat() / gridLines)) / 100000.0
            drawContext.canvas.nativeCanvas.drawText(
                "₹${String.format(Locale.getDefault(), "%.1f", gridVal)}L",
                4f,
                y - 4f,
                Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 24f
                    isAntiAlias = true
                }
            )
        }

        // Build curve path for Area & Line
        val path = Path()
        val fillPath = Path()

        val points = monthlyData.mapIndexed { index, item ->
            val value = if (isNetMetric) item.netEarnings else item.grossRevenue
            val x = index * pointSpacing
            val y = height * (1f - ((value - minVal) / (maxVal - minVal)).toFloat())
            Offset(x, y)
        }

        if (points.isNotEmpty()) {
            path.moveTo(points.first().x, points.first().y)
            fillPath.moveTo(points.first().x, height)
            fillPath.lineTo(points.first().x, points.first().y)

            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2, p0.y)
                val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2, p1.y)

                path.cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    p1.x, p1.y
                )
                fillPath.cubicTo(
                    controlPoint1.x, controlPoint1.y,
                    controlPoint2.x, controlPoint2.y,
                    p1.x, p1.y
                )
            }

            fillPath.lineTo(points.last().x, height)
            fillPath.close()

            // Draw Area Fill with gradient
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.35f), primaryColor.copy(alpha = 0.02f)),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw Line
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw Data Points & X-Axis Labels
            points.forEachIndexed { index, pt ->
                val isSelected = selectedIndex == index
                val item = monthlyData[index]

                // Outer circle
                drawCircle(
                    color = Color.White,
                    radius = if (isSelected) 7.dp.toPx() else 4.5.dp.toPx(),
                    center = pt
                )
                // Inner circle
                drawCircle(
                    color = if (isSelected) Color(0xFF0F172A) else primaryColor,
                    radius = if (isSelected) 5.dp.toPx() else 3.dp.toPx(),
                    center = pt
                )

                // Draw X-axis label
                drawContext.canvas.nativeCanvas.drawText(
                    item.shortName,
                    pt.x - 16f,
                    size.height - 8f,
                    Paint().apply {
                        color = if (isSelected) android.graphics.Color.BLACK else android.graphics.Color.DKGRAY
                        textSize = 28f
                        isFakeBoldText = isSelected
                        isAntiAlias = true
                    }
                )
            }
        }
    }
}

/**
 * Bar Chart Histogram for Monthly Orders Volume
 */
@Composable
fun OrderVolumeBarChart(
    monthlyData: List<MonthlyMetric>,
    modifier: Modifier = Modifier
) {
    if (monthlyData.isEmpty()) return
    val maxOrders = monthlyData.maxOf { it.ordersCount } * 1.2f

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height - 35.dp.toPx()
        val barWidth = 28.dp.toPx()
        val groupSpacing = width / monthlyData.size

        monthlyData.forEachIndexed { index, item ->
            val barHeight = (item.ordersCount / maxOrders) * height
            val x = index * groupSpacing + (groupSpacing - barWidth) / 2
            val y = height - barHeight

            // Draw bar with gradient
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF38BDF8), Color(0xFF0288D1)),
                    startY = y,
                    endY = height
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
            )

            // Draw count on top of bar
            drawContext.canvas.nativeCanvas.drawText(
                "${item.ordersCount}",
                x + 4f,
                y - 8f,
                Paint().apply {
                    color = android.graphics.Color.parseColor("#0F172A")
                    textSize = 24f
                    isFakeBoldText = true
                    isAntiAlias = true
                }
            )

            // Draw Month label below
            drawContext.canvas.nativeCanvas.drawText(
                item.shortName,
                x + 2f,
                size.height - 6f,
                Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 26f
                    isAntiAlias = true
                }
            )
        }
    }
}

/**
 * Donut Chart for Cuisine Category distribution
 */
@Composable
fun CategoryDonutChart(
    categories: List<CategoryShare>,
    totalAmount: Double,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 22.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        var startAngle = -90f

        categories.forEach { cat ->
            val sweepAngle = (cat.percentage / 100f) * 360f

            drawArc(
                color = cat.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle - 2f, // small gap between slices
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            startAngle += sweepAngle
        }

        // Center text in Lakhs
        drawContext.canvas.nativeCanvas.drawText(
            "₹${String.format(Locale.getDefault(), "%.1f", totalAmount / 100000.0)}L",
            center.x - 30f,
            center.y + 6f,
            Paint().apply {
                color = android.graphics.Color.parseColor("#0F172A")
                textSize = 34f
                isFakeBoldText = true
                isAntiAlias = true
            }
        )
    }
}

/**
 * Top Dish Performance Row with Progress Bar & Rating
 */
@Composable
private fun DishPerformanceRow(
    rank: Int,
    dish: DishPerformance,
    maxRevenue: Double
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    color = if (rank <= 2) SaffronPrimary else Color(0xFFF1F5F9),
                    shape = CircleShape,
                    modifier = Modifier.size(22.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "#$rank",
                            color = if (rank <= 2) Color.White else Color(0xFF475569),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            dish.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = Color(0xFF0F172A)
                        )
                        if (dish.isTopSeller) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "🔥 Best Seller",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        "${dish.category} • ${dish.ordersCount} Orders (${dish.quantitySold.toInt()} ${dish.unit})",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${String.format(Locale.getDefault(), "%,.0f", dish.revenue)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SaffronPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${dish.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${dish.marginPercentage.toInt()}% Margin", fontSize = 10.sp, color = VegGreen, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Visual Revenue Progress Bar
        val fraction = (dish.revenue / maxRevenue).toFloat().coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp))
                .background(Color(0xFFF1F5F9))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(5.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(AmberSecondary, SaffronPrimary)
                        )
                    )
            )
        }
    }
}
