package com.example.ui.common

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.CatererEntity
import com.example.data.models.KycStatus
import com.example.ui.theme.NonVegRed
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import com.example.util.LocationHelper

/**
 * Vertically centered, non-clipping Search Bar designed for clean Small/Medium/Large visual hierarchy.
 */
@Composable
fun CompactSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    height: Dp = 44.dp,
    accentColor: Color = SaffronPrimary,
    containerColor: Color = Color.White,
    backgroundColor: Color = containerColor,
    borderColor: Color = Color(0xFFCBD5E1),
    leadingIconTint: Color = accentColor,
    testTag: String = ""
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier),
        shape = RoundedCornerShape(height / 2),
        color = backgroundColor,
        border = BorderStroke(1.2.dp, if (value.isNotBlank()) accentColor else borderColor),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = leadingIconTint,
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0F172A)
                    ),
                    cursorBrush = SolidColor(accentColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (value.isNotBlank()) {
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = { onValueChange("") },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Universal Modal Dialog to quickly register and activate a New Caterer / Kitchen Partner
 * from Kitchen App, Admin App, or Customer App, complete with live GPS location detection
 * and automatic starter menu generation.
 */
@Composable
fun QuickAddCatererDialog(
    onDismiss: () -> Unit,
    onCatererCreated: (CatererEntity) -> Unit = {},
    onCreateCaterer: (CatererEntity) -> Unit = onCatererCreated
) {
    val context = LocalContext.current
    var kitchenName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Mumbai") }
    var address by remember { mutableStateOf("") }
    var dietaryType by remember { mutableStateOf("BOTH") } // "PURE_VEG", "BOTH", "NON_VEG"
    var minOrderAmount by remember { mutableStateOf("1500") }
    var deliveryCharge by remember { mutableStateOf("150") }
    var fssaiLicense by remember { mutableStateOf("") }
    var isFetchingGps by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val gpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        isFetchingGps = true
        LocationHelper.detectCurrentLocation(context) { loc ->
            isFetchingGps = false
            address = "${loc.streetArea}, ${loc.landmark}, ${loc.city} - ${loc.pincode}"
            city = loc.city
            Toast.makeText(context, "📍 Current GPS Location Set: ${loc.fullAddress}", Toast.LENGTH_SHORT).show()
        }
    }

    fun autoFillNewCaterer() {
        val num = (101..999).random()
        kitchenName = "Zaika Royal Caterers & Banquet $num"
        ownerName = "Ustad Imran Qureshi"
        mobileNumber = "9820" + (100000..999999).random().toString()
        city = "Mumbai"
        address = "Shop 12, LBS Marg, Kurla West, Mumbai - 400070"
        dietaryType = "BOTH"
        minOrderAmount = "1500"
        deliveryCharge = "150"
        fssaiLicense = "1152400" + (1000000..9999999).random().toString()
        errorMessage = null
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 650.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = CircleShape,
                            color = SaffronPrimary.copy(alpha = 0.14f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.AddBusiness,
                                    contentDescription = null,
                                    tint = SaffronPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Add New Caterer (नया कैटरर जोड़ें)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Register & activate new kitchen with starter menu",
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Auto-Fill Demo Bar + GPS Location Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { autoFillNewCaterer() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFEF3C7),
                            contentColor = Color(0xFF92400E)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("quick_add_caterer_autofill")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Auto-Fill Demo", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (LocationHelper.hasLocationPermission(context)) {
                                isFetchingGps = true
                                LocationHelper.detectCurrentLocation(context) { loc ->
                                    isFetchingGps = false
                                    address = "${loc.streetArea}, ${loc.landmark}, ${loc.city} - ${loc.pincode}"
                                    city = loc.city
                                    Toast.makeText(context, "📍 Current Location: ${loc.fullAddress}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                gpsLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDCFCE7),
                            contentColor = Color(0xFF166534)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("quick_add_caterer_gps")
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            if (isFetchingGps) "Detecting GPS..." else "📍 Current Location",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = kitchenName,
                        onValueChange = { kitchenName = it; errorMessage = null },
                        label = { Text("Caterer / Kitchen Name (कैटरर का नाम) *") },
                        placeholder = { Text("e.g. Royal Zaika Caterers") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = SaffronPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_caterer_name_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text("Owner Name (मालिक)") },
                            placeholder = { Text("e.g. Mohd. Tariq") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SaffronPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = { mobileNumber = it },
                            label = { Text("Mobile (मोबाइल)") },
                            placeholder = { Text("9876543210") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = VegGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Dietary Specialization
                    Text(
                        text = "Kitchen Dietary Type (शाकाहारी / मांसाहारी):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = dietaryType == "PURE_VEG",
                            onClick = { dietaryType = "PURE_VEG" },
                            label = { Text("Pure Veg 🌿", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VegGreen.copy(alpha = 0.18f),
                                selectedLabelColor = VegGreen
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = dietaryType == "BOTH",
                            onClick = { dietaryType = "BOTH" },
                            label = { Text("Both 🟢🔴", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronPrimary.copy(alpha = 0.18f),
                                selectedLabelColor = SaffronPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = dietaryType == "NON_VEG",
                            onClick = { dietaryType = "NON_VEG" },
                            label = { Text("Non-Veg 🍗", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NonVegRed.copy(alpha = 0.18f),
                                selectedLabelColor = NonVegRed
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Kitchen Address / Location (पता)") },
                        placeholder = { Text("Tap '📍 Current Location' or type address") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = SaffronPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City (शहर)") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minOrderAmount,
                            onValueChange = { minOrderAmount = it },
                            label = { Text("Min Order (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = deliveryCharge,
                            onValueChange = { deliveryCharge = it },
                            label = { Text("Delivery (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (errorMessage != null) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, NonVegRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NonVegRed,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            if (kitchenName.trim().isBlank()) {
                                errorMessage = "⚠️ कृपया कैटरर / किचन का नाम लिखें ( या 'Auto-Fill Demo' दबाएं )"
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val finalName = kitchenName.trim()
                            val cleanMob = mobileNumber.trim().ifBlank { "9876543210" }
                            val finalAddress = address.trim().ifBlank { "Main Market Road, ${city.ifBlank { "Mumbai" }}" }
                            val finalCity = city.trim().ifBlank { "Mumbai" }
                            val newCaterer = CatererEntity(
                                id = "caterer_${System.currentTimeMillis()}",
                                name = finalName,
                                kitchenName = finalName,
                                logoUrl = "",
                                bannerUrl = "",
                                rating = 4.8f,
                                reviewCount = 12,
                                deliveryTimeMinutes = 40,
                                minOrderAmount = minOrderAmount.toDoubleOrNull() ?: 1500.0,
                                deliveryCharge = deliveryCharge.toDoubleOrNull() ?: 150.0,
                                distanceKm = 1.8,
                                fssaiLicense = fssaiLicense.trim().ifBlank { "11524009887766" },
                                isFssaiVerified = true,
                                isOpenForBooking = true,
                                address = finalAddress,
                                city = finalCity,
                                ownerMobile = if (cleanMob.startsWith("+91")) cleanMob else "+91 $cleanMob",
                                kycStatus = KycStatus.APPROVED,
                                onlineCommissionPercentage = 10.0,
                                offlineCommissionPercentage = 5.0,
                                isOfflineBookingEnabled = true,
                                dietaryType = dietaryType
                            )
                            onCreateCaterer(newCaterer)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SaffronPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.8f)
                            .height(46.dp)
                            .testTag("quick_submit_new_caterer_btn")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Caterer Now", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
