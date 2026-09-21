package com.example.ui.kitchen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.app.DatePickerDialog
import java.util.Calendar
import java.util.Locale
import com.example.util.TimeSlotUtils
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
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

private fun matchesOrderDate(
    orderDate: String,
    filterMode: String, // "ALL", "TODAY", "TOMORROW", "CUSTOM"
    customDateYmd: String
): Boolean {
    if (filterMode == "ALL") return true
    val trimmed = orderDate.trim()
    if (trimmed.isEmpty()) return false

    val todayCal = TimeSlotUtils.getIndianCalendar()
    val tomorrowCal = TimeSlotUtils.getIndianCalendar().apply { add(Calendar.DAY_OF_YEAR, 1) }

    val targetCal = when (filterMode) {
        "TODAY" -> todayCal
        "TOMORROW" -> tomorrowCal
        "CUSTOM" -> {
            try {
                val cal = TimeSlotUtils.getIndianCalendar()
                val parsed = TimeSlotUtils.createDateFormat("yyyy-MM-dd").parse(customDateYmd)
                if (parsed != null) {
                    cal.time = parsed
                    cal
                } else null
            } catch (e: Exception) {
                null
            }
        }
        else -> null
    } ?: return true

    val ymd = TimeSlotUtils.createDateFormat("yyyy-MM-dd").format(targetCal.time)
    val dMmmYyyy = TimeSlotUtils.createDateFormat("d MMM yyyy").format(targetCal.time)
    val ddMmmYyyy = TimeSlotUtils.createDateFormat("dd MMM yyyy").format(targetCal.time)
    val dMmm = TimeSlotUtils.createDateFormat("d MMM").format(targetCal.time)
    val ddMmm = TimeSlotUtils.createDateFormat("dd MMM").format(targetCal.time)

    if (filterMode == "TODAY" && TimeSlotUtils.isToday(trimmed)) return true

    return trimmed.contains(ymd, ignoreCase = true) ||
           trimmed.contains(dMmmYyyy, ignoreCase = true) ||
           trimmed.contains(ddMmmYyyy, ignoreCase = true) ||
           trimmed.contains(dMmm, ignoreCase = true) ||
           trimmed.contains(ddMmm, ignoreCase = true)
}

private fun matchesOrderTimeSlot(
    orderTimeSlot: String,
    filterSlot: String // "ALL", "MORNING_SHIFT", "EVENING_SHIFT", "LUNCH", "SNACKS", "DINNER"
): Boolean {
    if (filterSlot == "ALL") return true
    val lower = orderTimeSlot.lowercase(Locale.ENGLISH)
    return when (filterSlot) {
        "MORNING_SHIFT" -> TimeSlotUtils.isMorningShift(orderTimeSlot)
        "EVENING_SHIFT" -> TimeSlotUtils.isEveningShift(orderTimeSlot)
        "LUNCH" -> lower.contains("lunch") || lower.contains("11:00") || lower.contains("12:00") || lower.contains("01:00") || lower.contains("02:00 pm")
        "SNACKS" -> lower.contains("snack") || lower.contains("tea") || lower.contains("02:00 pm") || lower.contains("03:00") || lower.contains("04:00") || lower.contains("05:00")
        "DINNER" -> lower.contains("dinner") || lower.contains("07:00") || lower.contains("08:00") || lower.contains("09:00") || lower.contains("10:00")
        else -> lower.contains(filterSlot.lowercase(Locale.ENGLISH))
    }
}

/**
 * Modern Date & Time Slot Filters for Kitchen Panel
 */
@Composable
private fun KitchenDateTimeFilterBar(
    selectedDateFilterMode: String,
    onSelectDateFilterMode: (String) -> Unit,
    selectedTimeSlotFilter: String,
    onSelectTimeSlotFilter: (String) -> Unit,
    todayOrdersCount: Int,
    tomorrowOrdersCount: Int,
    totalOrdersCount: Int,
    customSelectedDateDisplay: String,
    onOpenDatePicker: () -> Unit,
    dateFilteredOrdersCount: Int,
    morningOrdersCount: Int = 0,
    eveningOrdersCount: Int = 0,
    isCollapsible: Boolean = true,
    initiallyExpanded: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    Surface(
        color = Color.White,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            if (isCollapsible && !isExpanded) {
                // Sleek, compact single-row strip (~38dp) - Maximizes screen space!
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Morning Shift Pill
                    item {
                        val isMorningSelected = selectedTimeSlotFilter == "MORNING_SHIFT"
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isMorningSelected) SaffronPrimary else Color(0xFFFFFBEB),
                            border = BorderStroke(1.dp, if (isMorningSelected) SaffronPrimary else Color(0xFFFDE68A)),
                            modifier = Modifier.clickable {
                                onSelectTimeSlotFilter(if (isMorningSelected) "ALL" else "MORNING_SHIFT")
                            }
                        ) {
                            Text(
                                "☀️ 6am-3pm ($morningOrdersCount)",
                                fontSize = 11.sp,
                                fontWeight = if (isMorningSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isMorningSelected) Color.White else Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Evening Shift Pill
                    item {
                        val isEveningSelected = selectedTimeSlotFilter == "EVENING_SHIFT"
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isEveningSelected) Color(0xFF6D28D9) else Color(0xFFF5F3FF),
                            border = BorderStroke(1.dp, if (isEveningSelected) Color(0xFF6D28D9) else Color(0xFFDDD6FE)),
                            modifier = Modifier.clickable {
                                onSelectTimeSlotFilter(if (isEveningSelected) "ALL" else "EVENING_SHIFT")
                            }
                        ) {
                            Text(
                                "🌙 3:30pm-12am ($eveningOrdersCount)",
                                fontSize = 11.sp,
                                fontWeight = if (isEveningSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isEveningSelected) Color.White else Color(0xFF5B21B6),
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Date Quick Pill
                    item {
                        val isDateFiltered = selectedDateFilterMode != "ALL"
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isDateFiltered) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (isDateFiltered) Color(0xFF0F172A) else Color(0xFFCBD5E1)),
                            modifier = Modifier.clickable { onOpenDatePicker() }
                        ) {
                            Text(
                                when (selectedDateFilterMode) {
                                    "TODAY" -> "📅 Today ($todayOrdersCount) ▾"
                                    "TOMORROW" -> "📅 Tomorrow ($tomorrowOrdersCount) ▾"
                                    "CUSTOM" -> "📅 $customSelectedDateDisplay ▾"
                                    else -> "📅 All Dates ($totalOrdersCount) ▾"
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isDateFiltered) FontWeight.Bold else FontWeight.Medium,
                                color = if (isDateFiltered) Color.White else Color(0xFF1E293B),
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Expand Filters Button
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.clickable { isExpanded = true }
                        ) {
                            Text(
                                "⚙️ फ़िल्टर ▾",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Reset Filters Button
                    if (selectedDateFilterMode != "ALL" || selectedTimeSlotFilter != "ALL") {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFFEE2E2),
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                modifier = Modifier.clickable {
                                    onSelectDateFilterMode("ALL")
                                    onSelectTimeSlotFilter("ALL")
                                }
                            ) {
                                Text(
                                    "✕ Reset",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Row 1: Date Filter Row (Expanded)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Today,
                            contentDescription = null,
                            tint = SaffronPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            "Order Date & Shift Filters:",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (selectedDateFilterMode != "ALL" || selectedTimeSlotFilter != "ALL") {
                            Surface(
                                color = Color(0xFFFEE2E2),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable {
                                    onSelectDateFilterMode("ALL")
                                    onSelectTimeSlotFilter("ALL")
                                }
                            ) {
                                Text(
                                    "Clear Filters ✕",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }

                        if (isCollapsible) {
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { isExpanded = false }
                            ) {
                                Text(
                                    "▲ छिपाएं (Collapse)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

            Spacer(modifier = Modifier.height(5.dp))

            // Date Quick Filter Chips (Today, Tomorrow, Pick Date, All Dates)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Today Chip
                item {
                    val isSelected = selectedDateFilterMode == "TODAY"
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) SaffronPrimary else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SaffronPrimary else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.clickable { onSelectDateFilterMode("TODAY") }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "⚡ Today ($todayOrdersCount)",
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF1E293B)
                            )
                        }
                    }
                }

                // Tomorrow Chip
                item {
                    val isSelected = selectedDateFilterMode == "TOMORROW"
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) SaffronPrimary else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SaffronPrimary else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.clickable { onSelectDateFilterMode("TOMORROW") }
                    ) {
                        Text(
                            "Tomorrow ($tomorrowOrdersCount)",
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF1E293B),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Pick Custom Date Chip
                item {
                    val isSelected = selectedDateFilterMode == "CUSTOM"
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) SaffronPrimary else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SaffronPrimary else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.clickable { onOpenDatePicker() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color(0xFF475569),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (isSelected) customSelectedDateDisplay else "Pick Date 📆",
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF1E293B)
                            )
                        }
                    }
                }

                // All Dates Chip
                item {
                    val isSelected = selectedDateFilterMode == "ALL"
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) SaffronPrimary else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) SaffronPrimary else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.clickable { onSelectDateFilterMode("ALL") }
                    ) {
                        Text(
                            "All Dates ($totalOrdersCount)",
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF1E293B),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            // Row 2: Time Slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    tint = AmberSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    "Kitchen Prep Shifts (तैयारी शिफ्ट्स):",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Two Quick Prep Shift Toggle Cards (Morning 6 AM - 3 PM & Evening 3:30 PM - 12 AM)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isMorningSelected = selectedTimeSlotFilter == "MORNING_SHIFT"
                val isEveningSelected = selectedTimeSlotFilter == "EVENING_SHIFT"

                // Morning Shift Card: 6 AM to 3 PM
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onSelectTimeSlotFilter(if (isMorningSelected) "ALL" else "MORNING_SHIFT")
                        },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isMorningSelected) SaffronPrimary else Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, if (isMorningSelected) SaffronPrimary else Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "☀️ Morning Shift",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMorningSelected) Color.White else Color(0xFF92400E)
                            )
                            Text(
                                "6 AM – 3 PM",
                                fontSize = 10.sp,
                                color = if (isMorningSelected) Color.White.copy(alpha = 0.9f) else Color(0xFFB45309)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isMorningSelected) Color.White.copy(alpha = 0.25f) else Color(0xFFFEF3C7)
                        ) {
                            Text(
                                "$morningOrdersCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMorningSelected) Color.White else Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Evening Shift Card: 3:30 PM to 12 AM
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onSelectTimeSlotFilter(if (isEveningSelected) "ALL" else "EVENING_SHIFT")
                        },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isEveningSelected) Color(0xFF6D28D9) else Color(0xFFF5F3FF),
                    border = BorderStroke(1.dp, if (isEveningSelected) Color(0xFF6D28D9) else Color(0xFFDDD6FE))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "🌙 Evening Shift",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isEveningSelected) Color.White else Color(0xFF5B21B6)
                            )
                            Text(
                                "3:30 PM – 12 AM",
                                fontSize = 10.sp,
                                color = if (isEveningSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF6D28D9)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isEveningSelected) Color.White.copy(alpha = 0.25f) else Color(0xFFEDE9FE)
                        ) {
                            Text(
                                "$eveningOrdersCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isEveningSelected) Color.White else Color(0xFF5B21B6),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val slots = listOf(
                    "ALL" to "⏰ All Slots ($dateFilteredOrdersCount)",
                    "MORNING_SHIFT" to "☀️ 6 AM - 3 PM ($morningOrdersCount)",
                    "EVENING_SHIFT" to "🌙 3:30 PM - 12 AM ($eveningOrdersCount)",
                    "LUNCH" to "☀️ Lunch (11 AM - 2 PM)",
                    "SNACKS" to "☕ Snacks (2 PM - 5 PM)",
                    "DINNER" to "🌙 Dinner (7 PM - 10:30 PM)"
                )

                items(slots) { (slotKey, slotLabel) ->
                    val isSelected = selectedTimeSlotFilter == slotKey
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF0F172A) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.clickable { onSelectTimeSlotFilter(slotKey) }
                    ) {
                        Text(
                            slotLabel,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}
}

/**
 * Modern Responsive Kitchen Dashboard matching Image 2: "KITCHEN PANEL - A1 Huma Caterers"
 * Automatically adapts between Mobile (handheld) and Tablet / Foldable (split-pane & kanban).
 */
@Composable
fun KitchenResponsiveDashboard(
    viewModel: CaterersViewModel,
    onNavigateTab: (Int) -> Unit = {},
    onPreviewCustomerStore: (String) -> Unit = {},
    isBigScreenMode: Boolean = false,
    onToggleBigScreenMode: () -> Unit = {},
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
                onPreviewCustomerStore = onPreviewCustomerStore,
                isBigScreenMode = isBigScreenMode,
                onToggleBigScreenMode = onToggleBigScreenMode
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

    val todayCal = remember { TimeSlotUtils.getIndianCalendar() }
    val todayYmd = remember { TimeSlotUtils.createDateFormat("yyyy-MM-dd").format(todayCal.time) }
    val todayDisplay = remember { TimeSlotUtils.createDateFormat("d MMM").format(todayCal.time) }
    val todayDisplayFull = remember { TimeSlotUtils.createDateFormat("dd MMM yyyy").format(todayCal.time) }

    var selectedDateFilterMode by remember { mutableStateOf("ALL") }
    var customSelectedDateYmd by remember { mutableStateOf(todayYmd) }
    var customSelectedDateDisplay by remember { mutableStateOf(todayDisplayFull) }
    var selectedTimeSlotFilter by remember { mutableStateOf("ALL") }

    val dateFilteredOrders = remember(orders, selectedDateFilterMode, customSelectedDateYmd) {
        orders.filter { matchesOrderDate(it.deliveryDate, selectedDateFilterMode, customSelectedDateYmd) }
    }
    val dateAndTimeFilteredOrders = remember(dateFilteredOrders, selectedTimeSlotFilter) {
        dateFilteredOrders.filter { matchesOrderTimeSlot(it.deliveryTimeSlot, selectedTimeSlotFilter) }
    }

    val morningOrdersCount = remember(dateFilteredOrders) {
        dateFilteredOrders.count { TimeSlotUtils.isMorningShift(it.deliveryTimeSlot) }
    }
    val eveningOrdersCount = remember(dateFilteredOrders) {
        dateFilteredOrders.count { TimeSlotUtils.isEveningShift(it.deliveryTimeSlot) }
    }

    val todayOrdersCount = remember(orders) {
        orders.count { matchesOrderDate(it.deliveryDate, "TODAY", "") }
    }
    val tomorrowOrdersCount = remember(orders) {
        orders.count { matchesOrderDate(it.deliveryDate, "TOMORROW", "") }
    }

    val currentActiveDateLabel = when (selectedDateFilterMode) {
        "TODAY" -> "Today, $todayDisplay"
        "TOMORROW" -> {
            val tomCal = TimeSlotUtils.getIndianCalendar().apply { add(Calendar.DAY_OF_YEAR, 1) }
            "Tomorrow, " + TimeSlotUtils.createDateFormat("d MMM").format(tomCal.time)
        }
        "CUSTOM" -> customSelectedDateDisplay
        else -> "All Orders ($todayDisplayFull)"
    }

    var selectedPipelineStage by remember { mutableStateOf("ALL") }
    var dashboardViewMode by remember { mutableStateOf("ORDERS_PIPELINE") } // "ORDERS_PIPELINE" or "DELIVERY_PARTNERS"
    var selectedOrderForDetail by remember { mutableStateOf<OrderEntity?>(orders.firstOrNull()) }
    var showAssignDeliveryModal by remember { mutableStateOf<OrderEntity?>(null) }

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

                SidebarSubNavItem("All Orders", dateAndTimeFilteredOrders.size, selectedPipelineStage == "ALL" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "ALL"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("Confirm Orders", dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.CONFIRMED || it.orderStatus == OrderStatus.ACCEPTED }, selectedPipelineStage == "CONFIRM" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "CONFIRM"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("In Preparation", dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.PREPARING }, selectedPipelineStage == "PREPARING" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "PREPARING"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("Ready Orders", dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.READY }, selectedPipelineStage == "READY" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "READY"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("Out for Delivery", dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.OUT_FOR_DELIVERY || it.orderStatus == OrderStatus.ASSIGNED_DELIVERY }, selectedPipelineStage == "OUT_FOR_DELIVERY" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "OUT_FOR_DELIVERY"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("Delivered Orders", dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.DELIVERED }, selectedPipelineStage == "DELIVERED" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "DELIVERED"; dashboardViewMode = "ORDERS_PIPELINE" }
                SidebarSubNavItem("Cancelled Orders", dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.CANCELLED }, selectedPipelineStage == "CANCELLED" && dashboardViewMode == "ORDERS_PIPELINE") { selectedPipelineStage = "CANCELLED"; dashboardViewMode = "ORDERS_PIPELINE" }

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
                currentDateStr = currentActiveDateLabel,
                onRefresh = {
                    Toast.makeText(context, "Pipeline refreshed with live orders!", Toast.LENGTH_SHORT).show()
                },
                onDateClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val cal = TimeSlotUtils.getIndianCalendar().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            }
                            customSelectedDateYmd = TimeSlotUtils.createDateFormat("yyyy-MM-dd").format(cal.time)
                            customSelectedDateDisplay = TimeSlotUtils.createDateFormat("dd MMM yyyy").format(cal.time)
                            selectedDateFilterMode = "CUSTOM"
                        },
                        todayCal.get(Calendar.YEAR),
                        todayCal.get(Calendar.MONTH),
                        todayCal.get(Calendar.DAY_OF_MONTH)
                    ).show()
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
                                "Orders Pipeline (${dateAndTimeFilteredOrders.size})",
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
                // Kitchen Date & Time Filter Bar
                KitchenDateTimeFilterBar(
                    selectedDateFilterMode = selectedDateFilterMode,
                    onSelectDateFilterMode = { selectedDateFilterMode = it },
                    selectedTimeSlotFilter = selectedTimeSlotFilter,
                    onSelectTimeSlotFilter = { selectedTimeSlotFilter = it },
                    todayOrdersCount = todayOrdersCount,
                    tomorrowOrdersCount = tomorrowOrdersCount,
                    totalOrdersCount = orders.size,
                    customSelectedDateDisplay = customSelectedDateDisplay,
                    onOpenDatePicker = {
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val cal = TimeSlotUtils.getIndianCalendar().apply {
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                }
                                customSelectedDateYmd = TimeSlotUtils.createDateFormat("yyyy-MM-dd").format(cal.time)
                                customSelectedDateDisplay = TimeSlotUtils.createDateFormat("dd MMM yyyy").format(cal.time)
                                selectedDateFilterMode = "CUSTOM"
                            },
                            todayCal.get(Calendar.YEAR),
                            todayCal.get(Calendar.MONTH),
                            todayCal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    dateFilteredOrdersCount = dateFilteredOrders.size,
                    morningOrdersCount = morningOrdersCount,
                    eveningOrdersCount = eveningOrdersCount
                )

                // Pipeline Metric Status Cards Strip (From Image 2)
                KitchenPipelineMetricStrip(orders = dateAndTimeFilteredOrders)

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
                            orders = dateAndTimeFilteredOrders,
                            selectedStage = selectedPipelineStage,
                            onSelectOrder = { selectedOrderForDetail = it },
                            onAction = { action, order ->
                                handleOrderAction(viewModel, action, order, context) {
                                    showAssignDeliveryModal = it
                                }
                            },
                            onResetFilters = {
                                selectedDateFilterMode = "ALL"
                                selectedTimeSlotFilter = "ALL"
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

    // Delivery Assignment Dialog with Bartan Flow
    if (showAssignDeliveryModal != null) {
        AssignDeliveryBoyDialog(
            order = showAssignDeliveryModal!!,
            deliveryBoys = deliveryBoys,
            onDismiss = { showAssignDeliveryModal = null },
            onAssign = { boy, bartanDesc, handis, spoons, boxes ->
                viewModel.assignDeliveryBoy(
                    orderId = showAssignDeliveryModal!!.orderId,
                    boy = boy,
                    bartanDescription = bartanDesc,
                    handiCount = handis,
                    spoonsCount = spoons,
                    boxesCount = boxes
                )
                Toast.makeText(context, "Assigned to ${boy.name} with $bartanDesc!", Toast.LENGTH_SHORT).show()
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
    onPreviewCustomerStore: (String) -> Unit,
    isBigScreenMode: Boolean = false,
    onToggleBigScreenMode: () -> Unit = {}
) {
    val context = LocalContext.current
    var localBigScreenMode by remember { mutableStateOf(false) }
    val effectiveBigScreen = isBigScreenMode || localBigScreenMode
    val orders by viewModel.ordersList.collectAsState()
    val deliveryBoys by viewModel.deliveryBoysList.collectAsState()
    val selectedKitchenId by viewModel.selectedCatererId.collectAsState()
    val activeKitchenId = selectedKitchenId ?: "caterer_1"

    val todayCal = remember { TimeSlotUtils.getIndianCalendar() }
    val todayYmd = remember { TimeSlotUtils.createDateFormat("yyyy-MM-dd").format(todayCal.time) }
    val todayDisplay = remember { TimeSlotUtils.createDateFormat("d MMM").format(todayCal.time) }
    val todayDisplayFull = remember { TimeSlotUtils.createDateFormat("dd MMM yyyy").format(todayCal.time) }

    var selectedDateFilterMode by remember { mutableStateOf("ALL") }
    var customSelectedDateYmd by remember { mutableStateOf(todayYmd) }
    var customSelectedDateDisplay by remember { mutableStateOf(todayDisplayFull) }
    var selectedTimeSlotFilter by remember { mutableStateOf("ALL") }

    val dateFilteredOrders = remember(orders, selectedDateFilterMode, customSelectedDateYmd) {
        orders.filter { matchesOrderDate(it.deliveryDate, selectedDateFilterMode, customSelectedDateYmd) }
    }
    val dateAndTimeFilteredOrders = remember(dateFilteredOrders, selectedTimeSlotFilter) {
        dateFilteredOrders.filter { matchesOrderTimeSlot(it.deliveryTimeSlot, selectedTimeSlotFilter) }
    }

    val morningOrdersCount = remember(dateFilteredOrders) {
        dateFilteredOrders.count { TimeSlotUtils.isMorningShift(it.deliveryTimeSlot) }
    }
    val eveningOrdersCount = remember(dateFilteredOrders) {
        dateFilteredOrders.count { TimeSlotUtils.isEveningShift(it.deliveryTimeSlot) }
    }

    val todayOrdersCount = remember(orders) {
        orders.count { matchesOrderDate(it.deliveryDate, "TODAY", "") }
    }
    val tomorrowOrdersCount = remember(orders) {
        orders.count { matchesOrderDate(it.deliveryDate, "TOMORROW", "") }
    }

    val currentActiveDateLabel = when (selectedDateFilterMode) {
        "TODAY" -> "Today, $todayDisplay"
        "TOMORROW" -> {
            val tomCal = TimeSlotUtils.getIndianCalendar().apply { add(Calendar.DAY_OF_YEAR, 1) }
            "Tomorrow, " + TimeSlotUtils.createDateFormat("d MMM").format(tomCal.time)
        }
        "CUSTOM" -> customSelectedDateDisplay
        else -> "All Orders ($todayDisplayFull)"
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var mobileDashboardView by remember { mutableStateOf("ORDERS_PIPELINE") } // "ORDERS_PIPELINE" or "DELIVERY_PARTNERS"
    var selectedStageIndex by remember { mutableStateOf(0) }
    var selectedOrderForDetail by remember { mutableStateOf<OrderEntity?>(null) }
    var showAssignDeliveryModal by remember { mutableStateOf<OrderEntity?>(null) }

    val stages = listOf("ALL", "CONFIRM", "PREPARING", "READY", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED")
    val stageLabels = listOf(
        "All (${dateAndTimeFilteredOrders.size})",
        "Confirm (${dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.CONFIRMED || it.orderStatus == OrderStatus.ACCEPTED }})",
        "In Prep (${dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.PREPARING }})",
        "Ready (${dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.READY }})",
        "Out for Del (${dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.OUT_FOR_DELIVERY || it.orderStatus == OrderStatus.ASSIGNED_DELIVERY }})",
        "Delivered (${dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.DELIVERED }})",
        "Cancelled (${dateAndTimeFilteredOrders.count { it.orderStatus == OrderStatus.CANCELLED }})"
    )

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
            // Mobile Top Bar (Adaptive for Big Screen Mode)
            if (effectiveBigScreen) {
                Surface(color = Color(0xFF0F172A), contentColor = Color.White) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = AmberSecondary,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "⛶ बड़ी स्क्रीन चालू",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "${dateAndTimeFilteredOrders.size} Orders",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            color = Color(0xFF334155),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable {
                                localBigScreenMode = false
                                onToggleBigScreenMode()
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("सामान्य दृश्य (Exit) ✕", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            } else {
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
                            // Big Screen Mode Toggle Button
                            Surface(
                                color = AmberSecondary,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .clickable {
                                        localBigScreenMode = true
                                        onToggleBigScreenMode()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⛶ बड़ी स्क्रीन", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }

                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .clickable {
                                        DatePickerDialog(
                                            context,
                                            { _, year, month, dayOfMonth ->
                                                val cal = TimeSlotUtils.getIndianCalendar().apply {
                                                    set(Calendar.YEAR, year)
                                                    set(Calendar.MONTH, month)
                                                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                                }
                                                customSelectedDateYmd = TimeSlotUtils.createDateFormat("yyyy-MM-dd").format(cal.time)
                                                customSelectedDateDisplay = TimeSlotUtils.createDateFormat("dd MMM yyyy").format(cal.time)
                                                selectedDateFilterMode = "CUSTOM"
                                            },
                                            todayCal.get(Calendar.YEAR),
                                            todayCal.get(Calendar.MONTH),
                                            todayCal.get(Calendar.DAY_OF_MONTH)
                                        ).show()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Today, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(currentActiveDateLabel, fontSize = 11.sp, color = Color.White)
                                }
                            }
                            IconButton(onClick = { Toast.makeText(context, "Refreshed live orders!", Toast.LENGTH_SHORT).show() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
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
                            "Pipeline (${dateAndTimeFilteredOrders.size})",
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
                // Kitchen Date & Time Filter Bar
                KitchenDateTimeFilterBar(
                    selectedDateFilterMode = selectedDateFilterMode,
                    onSelectDateFilterMode = { selectedDateFilterMode = it },
                    selectedTimeSlotFilter = selectedTimeSlotFilter,
                    onSelectTimeSlotFilter = { selectedTimeSlotFilter = it },
                    todayOrdersCount = todayOrdersCount,
                    tomorrowOrdersCount = tomorrowOrdersCount,
                    totalOrdersCount = orders.size,
                    customSelectedDateDisplay = customSelectedDateDisplay,
                    onOpenDatePicker = {
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val cal = TimeSlotUtils.getIndianCalendar().apply {
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                }
                                customSelectedDateYmd = TimeSlotUtils.createDateFormat("yyyy-MM-dd").format(cal.time)
                                customSelectedDateDisplay = TimeSlotUtils.createDateFormat("dd MMM yyyy").format(cal.time)
                                selectedDateFilterMode = "CUSTOM"
                            },
                            todayCal.get(Calendar.YEAR),
                            todayCal.get(Calendar.MONTH),
                            todayCal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    dateFilteredOrdersCount = dateFilteredOrders.size,
                    morningOrdersCount = morningOrdersCount,
                    eveningOrdersCount = eveningOrdersCount,
                    isCollapsible = true,
                    initiallyExpanded = false
                )

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
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    KitchenKanbanBoard(
                        orders = dateAndTimeFilteredOrders,
                        selectedStage = stages[selectedStageIndex],
                        onSelectOrder = { selectedOrderForDetail = it },
                        onAction = { action, order ->
                            handleOrderAction(viewModel, action, order, context) {
                                showAssignDeliveryModal = it
                            }
                        },
                        onResetFilters = {
                            selectedDateFilterMode = "ALL"
                            selectedTimeSlotFilter = "ALL"
                        }
                    )
                }
            }

            // Mobile Quick Action Strip (hidden in Big Screen Mode to maximize order visibility)
            if (!effectiveBigScreen) {
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

    // Assignment Dialog with Bartan Flow
    if (showAssignDeliveryModal != null) {
        AssignDeliveryBoyDialog(
            order = showAssignDeliveryModal!!,
            deliveryBoys = deliveryBoys,
            onDismiss = { showAssignDeliveryModal = null },
            onAssign = { boy, bartanDesc, handis, spoons, boxes ->
                viewModel.assignDeliveryBoy(
                    orderId = showAssignDeliveryModal!!.orderId,
                    boy = boy,
                    bartanDescription = bartanDesc,
                    handiCount = handis,
                    spoonsCount = spoons,
                    boxesCount = boxes
                )
                Toast.makeText(context, "Assigned to ${boy.name} with $bartanDesc!", Toast.LENGTH_SHORT).show()
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
    onRefresh: () -> Unit,
    onDateClick: (() -> Unit)? = null
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
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable(enabled = onDateClick != null) { onDateClick?.invoke() }
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
    val allCount = orders.size
    val allAmt = "₹ " + "%,d".format(orders.sumOf { it.totalAmount }.toLong())
    val confirmOrders = orders.filter { it.orderStatus == OrderStatus.CONFIRMED || it.orderStatus == OrderStatus.ACCEPTED }
    val confirmCount = confirmOrders.size
    val confirmAmt = "₹ " + "%,d".format(confirmOrders.sumOf { it.totalAmount }.toLong())
    val prepOrders = orders.filter { it.orderStatus == OrderStatus.PREPARING }
    val prepCount = prepOrders.size
    val prepAmt = "₹ " + "%,d".format(prepOrders.sumOf { it.totalAmount }.toLong())
    val readyOrders = orders.filter { it.orderStatus == OrderStatus.READY }
    val readyCount = readyOrders.size
    val readyAmt = "₹ " + "%,d".format(readyOrders.sumOf { it.totalAmount }.toLong())
    val outOrders = orders.filter { it.orderStatus == OrderStatus.OUT_FOR_DELIVERY || it.orderStatus == OrderStatus.ASSIGNED_DELIVERY }
    val outCount = outOrders.size
    val outAmt = "₹ " + "%,d".format(outOrders.sumOf { it.totalAmount }.toLong())
    val deliveredOrders = orders.filter { it.orderStatus == OrderStatus.DELIVERED }
    val deliveredCount = deliveredOrders.size
    val deliveredAmt = "₹ " + "%,d".format(deliveredOrders.sumOf { it.totalAmount }.toLong())
    val cancelledOrders = orders.filter { it.orderStatus == OrderStatus.CANCELLED }
    val cancelledCount = cancelledOrders.size
    val cancelledAmt = "₹ " + "%,d".format(cancelledOrders.sumOf { it.totalAmount }.toLong())

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
 * Shift Header Section for grouping orders by Morning Shift (6 AM - 3 PM)
 * and Evening Shift (3:30 PM - 12 AM)
 */
@Composable
private fun KitchenShiftHeaderSection(
    title: String,
    timeRange: String,
    ordersCount: Int,
    containerColor: Color,
    contentColor: Color,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = contentColor
                )
                Text(
                    text = "$timeRange • Earlier orders on top ⬆️",
                    fontSize = 10.5.sp,
                    color = contentColor.copy(alpha = 0.85f)
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accentColor
            ) {
                Text(
                    text = "$ordersCount Orders",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/**
 * Multi-Column Kanban Board displaying cards per status stage with
 * Morning Shift (6 AM - 3 PM) and Evening Shift (3:30 PM - 12 AM) grouping,
 * sorted chronologically with earlier orders on top.
 */
@Composable
private fun KitchenKanbanBoard(
    orders: List<OrderEntity>,
    selectedStage: String,
    onSelectOrder: (OrderEntity) -> Unit,
    onAction: (String, OrderEntity) -> Unit,
    onResetFilters: (() -> Unit)? = null
) {
    val stageFilteredOrders = if (selectedStage == "ALL") {
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

    // Chronological sorting:
    // 1. Shift: Morning (6am-3pm) first, Evening (3:30pm-12am) second
    // 2. Delivery Start Time: earliest minute on top (e.g. 11:00 AM before 12:30 PM before 01:30 PM)
    // 3. Created Timestamp: earlier booked order on top
    val sortedOrders = remember(stageFilteredOrders) {
        stageFilteredOrders.sortedWith { o1, o2 -> TimeSlotUtils.compareOrdersForKitchenPrep(o1, o2) }
    }

    val morningOrders = remember(sortedOrders) {
        sortedOrders.filter { TimeSlotUtils.isMorningShift(it.deliveryTimeSlot) }
    }
    val eveningOrders = remember(sortedOrders) {
        sortedOrders.filter { TimeSlotUtils.isEveningShift(it.deliveryTimeSlot) }
    }
    val otherOrders = remember(sortedOrders) {
        sortedOrders.filter { !TimeSlotUtils.isMorningShift(it.deliveryTimeSlot) && !TimeSlotUtils.isEveningShift(it.deliveryTimeSlot) }
    }

    if (sortedOrders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No orders found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "There are no orders matching this stage and date/time filter.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                    if (onResetFilters != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onResetFilters,
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Date & Time Filters", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Group 1: Morning Shift Orders (06:00 AM – 03:00 PM)
            if (morningOrders.isNotEmpty()) {
                item {
                    KitchenShiftHeaderSection(
                        title = "☀️ Morning Shift Orders (सुबह - दोपहर तैयारी)",
                        timeRange = "06:00 AM – 03:00 PM",
                        ordersCount = morningOrders.size,
                        containerColor = Color(0xFFFFFBEB),
                        contentColor = Color(0xFF92400E),
                        accentColor = SaffronPrimary
                    )
                }
                items(morningOrders, key = { it.orderId }) { order ->
                    KitchenKanbanOrderCard(
                        order = order,
                        onClick = { onSelectOrder(order) },
                        onAction = { action -> onAction(action, order) }
                    )
                }
            }

            // Group 2: Evening Shift Orders (03:30 PM – 12:00 AM)
            if (eveningOrders.isNotEmpty()) {
                item {
                    if (morningOrders.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    KitchenShiftHeaderSection(
                        title = "🌙 Evening Shift Orders (शाम - रात तैयारी)",
                        timeRange = "03:30 PM – 12:00 AM",
                        ordersCount = eveningOrders.size,
                        containerColor = Color(0xFFF5F3FF),
                        contentColor = Color(0xFF5B21B6),
                        accentColor = Color(0xFF7C3AED)
                    )
                }
                items(eveningOrders, key = { it.orderId }) { order ->
                    KitchenKanbanOrderCard(
                        order = order,
                        onClick = { onSelectOrder(order) },
                        onAction = { action -> onAction(action, order) }
                    )
                }
            }

            // Group 3: Other Time Slot Orders (if any)
            if (otherOrders.isNotEmpty()) {
                item {
                    if (morningOrders.isNotEmpty() || eveningOrders.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    KitchenShiftHeaderSection(
                        title = "📦 Other Time Slot Orders (अन्य ऑर्डर्स)",
                        timeRange = "All Slots",
                        ordersCount = otherOrders.size,
                        containerColor = Color(0xFFF8FAFC),
                        contentColor = Color(0xFF334155),
                        accentColor = Color(0xFF64748B)
                    )
                }
                items(otherOrders, key = { it.orderId }) { order ->
                    KitchenKanbanOrderCard(
                        order = order,
                        onClick = { onSelectOrder(order) },
                        onAction = { action -> onAction(action, order) }
                    )
                }
            }
        }
    }
}

/**
 * Rich Order Card matching Image 2 with expandable details
 */
@Composable
private fun KitchenKanbanOrderCard(
    order: OrderEntity,
    onClick: () -> Unit,
    onAction: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val formattedDateDisplay = remember(order.deliveryDate) {
        try {
            val parsed = TimeSlotUtils.createDateFormat("yyyy-MM-dd").parse(order.deliveryDate)
            if (parsed != null) {
                TimeSlotUtils.createDateFormat("d MMM").format(parsed)
            } else {
                order.deliveryDate
            }
        } catch (e: Exception) {
            order.deliveryDate
        }
    }

    val isMorning = remember(order.deliveryTimeSlot) {
        TimeSlotUtils.isMorningShift(order.deliveryTimeSlot)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Order ID, Time, Shift Badge
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

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isMorning) Color(0xFFFFFBEB) else Color(0xFFF5F3FF),
                    border = BorderStroke(1.dp, if (isMorning) Color(0xFFFDE68A) else Color(0xFFDDD6FE))
                ) {
                    Text(
                        if (isMorning) "☀️ Morning Shift" else "🌙 Evening Shift",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMorning) Color(0xFF92400E) else Color(0xFF5B21B6),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Customer Name & Phone
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
                Text(order.customerMobile, fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Delivery Slot with highlighted start time
            Surface(
                color = if (isMorning) Color(0xFFFEF3C7).copy(alpha = 0.35f) else Color(0xFFEDE9FE).copy(alpha = 0.35f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isMorning) SaffronPrimary else Color(0xFF7C3AED),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "$formattedDateDisplay • ${order.deliveryTimeSlot}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Text(
                        text = if (isMorning) "6 AM – 3 PM" else "3:30 PM – 12 AM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMorning) Color(0xFFB45309) else Color(0xFF6D28D9)
                    )
                }
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
                    maxLines = if (isExpanded) 10 else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Expandable details (Address & Handi / Containers)
            if (isExpanded) {
                Spacer(modifier = Modifier.height(6.dp))
                if (order.deliveryAddress.isNotBlank()) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(order.deliveryAddress, fontSize = 11.sp, color = Color(0xFF475569))
                    }
                }
                if (order.bartanDescription.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.SoupKitchen, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Containers: ${order.bartanDescription} (OTP: ${order.deliveryOtp})", fontSize = 10.5.sp, color = Color(0xFF92400E))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Amount, Advance, Expand toggle & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                    Text("Adv: ₹${order.advancePaidAmount.toInt()} • Bal: ₹${order.balanceAmount.toInt()}", fontSize = 10.5.sp, color = VegGreen)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Expand / Collapse details button
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.clickable { isExpanded = !isExpanded }
                    ) {
                        Text(
                            if (isExpanded) "कम ▲" else "विवरण ▼",
                            fontSize = 10.5.sp,
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
                        )
                    }

                    // Action Buttons depending on status
                    when (order.orderStatus) {
                        OrderStatus.NEW, OrderStatus.CONFIRMED, OrderStatus.ACCEPTED -> {
                            val isPrepAllowed = TimeSlotUtils.isKitchenPrepAllowed(order.deliveryDate, order.deliveryTimeSlot)
                            val prepLabel = TimeSlotUtils.getKitchenPrepCountdownLabel(order.deliveryDate, order.deliveryTimeSlot)
                            Button(
                                onClick = { onAction("PREPARATION") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPrepAllowed) SaffronPrimary else Color(0xFF94A3B8)
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(prepLabel, fontSize = 11.sp)
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
    onAssign: (com.example.data.models.DeliveryBoyEntity, String, Int, Int, Int) -> Unit
) {
    // Smart estimation based on itemsSummary
    val suggestedCounts = remember(order.itemsSummary) {
        val lower = order.itemsSummary.lowercase()
        var handis = 2
        if (lower.contains("kg") || lower.contains("kilo")) {
            val kgMatch = Regex("""(\d+)\s*(?:kg|kilo)""").find(lower)
            val kgVal = kgMatch?.groupValues?.get(1)?.toIntOrNull() ?: 2
            handis = when {
                kgVal <= 2 -> 1
                kgVal <= 5 -> 2
                kgVal <= 10 -> 4
                else -> (kgVal / 3).coerceAtLeast(2)
            }
        } else if (lower.contains("gravy") || lower.contains("curry") || lower.contains("dal")) {
            handis = 2
        } else if (lower.contains("plate") || lower.contains("pack")) {
            handis = 1
        }
        val spoons = if (handis > 0) handis.coerceIn(1, 4) else 1
        val boxes = if (handis >= 2) 2 else 1
        Triple(handis, spoons, boxes)
    }

    var isReusablePackaging by remember { mutableStateOf(true) }
    var handiCount by remember { mutableIntStateOf(suggestedCounts.first) }
    var spoonsCount by remember { mutableIntStateOf(suggestedCounts.second) }
    var boxesCount by remember { mutableIntStateOf(suggestedCounts.third) }
    var customRemarks by remember {
        mutableStateOf(
            if (order.bartanDescription.isNotBlank() && order.bartanDescription != "Handi & Serving Trays") {
                order.bartanDescription
            } else {
                "${suggestedCounts.first} Metal Handi/Degs, ${suggestedCounts.second} Serving Spoons, ${suggestedCounts.third} Hot Crate"
            }
        )
    }

    fun syncRemarks(h: Int, s: Int, b: Int, isReusable: Boolean) {
        if (!isReusable) {
            customRemarks = "Disposable Packaging (No Bartan to Return)"
        } else {
            val parts = mutableListOf<String>()
            if (h > 0) parts.add("$h Metal Handi/Degs")
            if (s > 0) parts.add("$s Serving Spoons")
            if (b > 0) parts.add("$b Hot Crate/Boxes")
            if (parts.isEmpty()) parts.add("1 Standard Deg/Handi")
            customRemarks = parts.joinToString(", ")
        }
    }

    var selectedDeliveryBoy by remember { mutableStateOf<com.example.data.models.DeliveryBoyEntity?>(deliveryBoys.firstOrNull { !it.isBusy } ?: deliveryBoys.firstOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SoupKitchen, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Assign Delivery Partner & Bartan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                }
                Text("Order #${order.orderId} • ${order.customerName}", fontSize = 12.sp, color = Color(0xFF64748B))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Order items summary pill
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("📦 Ordered Items (मेन्यू):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                        Text(order.itemsSummary, fontSize = 12.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Packaging Type Toggle
                Text("1. Packaging & Bartan Mode (बर्तन का हिसाब):", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isReusablePackaging) Color(0xFFFFF7ED) else Color(0xFFF8FAFC),
                        border = BorderStroke(if (isReusablePackaging) 1.5.dp else 1.dp, if (isReusablePackaging) SaffronPrimary else Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                isReusablePackaging = true
                                syncRemarks(handiCount, spoonsCount, boxesCount, true)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🥘 Reusable Bartan", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = if (isReusablePackaging) SaffronPrimary else Color(0xFF475569))
                            Text("Handi return required 🔄", fontSize = 9.5.sp, color = Color(0xFF92400E))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (!isReusablePackaging) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                        border = BorderStroke(if (!isReusablePackaging) 1.5.dp else 1.dp, if (!isReusablePackaging) Color(0xFF2563EB) else Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                isReusablePackaging = false
                                syncRemarks(0, 0, 0, false)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📦 Disposable Pack", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = if (!isReusablePackaging) Color(0xFF2563EB) else Color(0xFF475569))
                            Text("No utensils return ❌", fontSize = 9.5.sp, color = Color.Gray)
                        }
                    }
                }

                if (isReusablePackaging) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Smart Utensil Item Counters
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💡 Smart Auto-Calculated for Menu", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF92400E))
                                Surface(shape = RoundedCornerShape(4.dp), color = AmberSecondary) {
                                    Text("${handiCount + spoonsCount + boxesCount} Items", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Counter 1: Handis / Degs
                            UtensilCounterRow(
                                icon = "🍲",
                                label = "Metal Handi / Degs (हांडी)",
                                count = handiCount,
                                onDecrement = {
                                    if (handiCount > 0) {
                                        handiCount--
                                        syncRemarks(handiCount, spoonsCount, boxesCount, true)
                                    }
                                },
                                onIncrement = {
                                    handiCount++
                                    syncRemarks(handiCount, spoonsCount, boxesCount, true)
                                }
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Counter 2: Serving Spoons
                            UtensilCounterRow(
                                icon = "🥄",
                                label = "Serving Spoons (चम्मच)",
                                count = spoonsCount,
                                onDecrement = {
                                    if (spoonsCount > 0) {
                                        spoonsCount--
                                        syncRemarks(handiCount, spoonsCount, boxesCount, true)
                                    }
                                },
                                onIncrement = {
                                    spoonsCount++
                                    syncRemarks(handiCount, spoonsCount, boxesCount, true)
                                }
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Counter 3: Crates / Bags / Hot Pots
                            UtensilCounterRow(
                                icon = "📦",
                                label = "Hot Crate / Box (क्रेट्स)",
                                count = boxesCount,
                                onDecrement = {
                                    if (boxesCount > 0) {
                                        boxesCount--
                                        syncRemarks(handiCount, spoonsCount, boxesCount, true)
                                    }
                                },
                                onIncrement = {
                                    boxesCount++
                                    syncRemarks(handiCount, spoonsCount, boxesCount, true)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Editable description field
                    OutlinedTextField(
                        value = customRemarks,
                        onValueChange = { customRemarks = it },
                        label = { Text("Utensil Details shown to Customer & Rider", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 11.5.sp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Select Delivery Partner Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("2. Choose Delivery Partner (राइडर):", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFF0F172A))
                    Text("${deliveryBoys.size} Available", fontSize = 11.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                deliveryBoys.forEach { boy ->
                    val isSelected = selectedDeliveryBoy?.id == boy.id
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFFFFF7ED) else Color.White,
                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) SaffronPrimary else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedDeliveryBoy = boy }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) SaffronPrimary else Color(0xFFE2E8F0),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Color(0xFF475569),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(boy.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                    if (boy.pendingBartanCount > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                            Text("🍲 ${boy.pendingBartanCount} bartan active", fontSize = 9.5.sp, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                }
                                Text("${boy.mobile} • ${if (boy.isBusy) "Busy" else "Available"} • ${boy.todayCompletedDeliveries} delivered", fontSize = 11.sp, color = Color.Gray)
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedDeliveryBoy = boy },
                                colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val boy = selectedDeliveryBoy ?: deliveryBoys.firstOrNull()
                    if (boy != null) {
                        val finalDesc = if (isReusablePackaging) {
                            customRemarks.ifBlank { "${handiCount} Handi, ${spoonsCount} Spoons" }
                        } else {
                            "Disposable Packaging (No Bartan to Return)"
                        }
                        val finalHandis = if (isReusablePackaging) handiCount else 0
                        val finalSpoons = if (isReusablePackaging) spoonsCount else 0
                        val finalBoxes = if (isReusablePackaging) boxesCount else 0
                        onAssign(boy, finalDesc, finalHandis, finalSpoons, finalBoxes)
                    }
                },
                enabled = selectedDeliveryBoy != null || deliveryBoys.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                val totalPcs = if (isReusablePackaging) (handiCount + spoonsCount + boxesCount) else 0
                val boyName = selectedDeliveryBoy?.name?.split(" ")?.firstOrNull() ?: "Partner"
                Text(
                    if (totalPcs > 0) "Assign & Handover $totalPcs Containers to $boyName 🚀" else "Assign to $boyName 🚀",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun UtensilCounterRow(
    icon: String,
    label: String,
    count: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 11.5.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onDecrement() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("-", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF334155))
                }
            }

            Text(
                count.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = SaffronPrimary,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onIncrement() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("+", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                }
            }
        }
    }
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
            val isAllowed = TimeSlotUtils.isKitchenPrepAllowed(order.deliveryDate, order.deliveryTimeSlot)
            if (!isAllowed) {
                val countdown = TimeSlotUtils.getKitchenPrepCountdownLabel(order.deliveryDate, order.deliveryTimeSlot)
                Toast.makeText(context, "⚠️ Preparation Locked: Unlocks 10 hours before delivery slot ($countdown)", Toast.LENGTH_LONG).show()
            } else {
                viewModel.updateOrderStatus(order.orderId, OrderStatus.PREPARING)
                Toast.makeText(context, "Order #${order.orderId} moved to Preparation! 🍳", Toast.LENGTH_SHORT).show()
            }
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
