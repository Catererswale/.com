package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CatererEntity
import com.example.data.models.FoodType
import com.example.data.models.KycStatus
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.NonVegRed
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import com.example.data.repository.CaterersViewModel

@Composable
fun AdminAddKitchenPartnerScreen(
    viewModel: CaterersViewModel,
    onPartnerAddedSuccess: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Form Field States
    var kitchenName by remember { mutableStateOf("") }
    var brandName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var ownerMobile by remember { mutableStateOf("") }
    var ownerWhatsapp by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var cuisineSpecialties by remember { mutableStateOf("") }
    var selectedFoodType by remember { mutableStateOf(FoodType.NON_VEG) }
    var city by remember { mutableStateOf("New Delhi") }
    var fullAddress by remember { mutableStateOf("") }
    var minOrderAmountText by remember { mutableStateOf("1500") }
    var deliveryChargeText by remember { mutableStateOf("150") }
    var deliveryTimeMinutesText by remember { mutableStateOf("45") }
    var distanceKmText by remember { mutableStateOf("2.5") }

    // Commission & Channel Booking Settings
    var onlineCommissionPercent by remember { mutableStateOf(10.0) }
    var isOpenForOnlineBooking by remember { mutableStateOf(true) }
    var offlineCommissionPercent by remember { mutableStateOf(5.0) }
    var isOfflineBookingEnabled by remember { mutableStateOf(true) }

    // Legal & KYC
    var fssaiLicenseNumber by remember { mutableStateOf("") }
    var isFssaiVerified by remember { mutableStateOf(true) }
    var kycStatus by remember { mutableStateOf(KycStatus.APPROVED) }
    var panNumber by remember { mutableStateOf("") }
    var aadhaarNumber by remember { mutableStateOf("") }
    var kycNotes by remember { mutableStateOf("Verified & Onboarded by Super Admin") }

    // Bank Account Payout Details
    var bankAccountNumber by remember { mutableStateOf("") }
    var bankIfscCode by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }

    // UI Feedback & Validation
    var validationError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var newlyCreatedPartner by remember { mutableStateOf<CatererEntity?>(null) }

    // Live Simulator Sample Amount
    var sampleOrderAmount by remember { mutableStateOf(10000.0) }

    val quickCities = listOf("New Delhi", "Noida", "Gurugram", "Lucknow", "Mumbai", "Hyderabad", "Bengaluru", "Kolkata")
    val onlineCommissionPresets = listOf(5.0, 8.0, 10.0, 12.0, 15.0, 18.0)
    val offlineCommissionPresets = listOf(2.0, 3.0, 5.0, 7.0, 10.0)

    fun autoFillDemoData() {
        val randomSuffix = (100..999).random()
        kitchenName = "Al-Barakah Royal Banquet & Kitchen $randomSuffix"
        brandName = "Al-Barakah Caterers"
        ownerName = "Mohd. Tariq Siddiqui"
        ownerMobile = "98" + (10000000..99999999).random().toString().take(8)
        ownerWhatsapp = ownerMobile
        emailAddress = "albarakah$randomSuffix@catererswale.com"
        cuisineSpecialties = "Hyderabadi Dum Biryani, Mughlai Korma, Shahi Tukda, Live Tandoor"
        selectedFoodType = FoodType.NON_VEG
        city = "New Delhi"
        fullAddress = "Shop 18-20, Central Catering Plaza, Jamia Nagar, Okhla Phase 2, New Delhi 110025"
        minOrderAmountText = "2000"
        deliveryChargeText = "150"
        deliveryTimeMinutesText = "45"
        distanceKmText = "3.2"
        onlineCommissionPercent = 10.0
        isOpenForOnlineBooking = true
        offlineCommissionPercent = 5.0
        isOfflineBookingEnabled = true
        fssaiLicenseNumber = "1152" + (1000000000L..9999999999L).random().toString()
        isFssaiVerified = true
        kycStatus = KycStatus.APPROVED
        panNumber = "ABCDE" + (1000..9999).random() + "F"
        aadhaarNumber = "9988 7766 " + (1000..9999).random()
        bankAccountNumber = "502000" + (10000000..99999999).random()
        bankIfscCode = "HDFC000" + (1000..9999).random()
        bankName = "HDFC Bank Connaught Place Branch"
        kycNotes = "Direct Super Admin Onboarding & Physical Kitchen Checked"
        validationError = null
    }

    fun clearForm() {
        kitchenName = ""
        brandName = ""
        ownerName = ""
        ownerMobile = ""
        ownerWhatsapp = ""
        emailAddress = ""
        cuisineSpecialties = ""
        selectedFoodType = FoodType.NON_VEG
        fullAddress = ""
        minOrderAmountText = "1500"
        deliveryChargeText = "150"
        deliveryTimeMinutesText = "45"
        distanceKmText = "2.5"
        onlineCommissionPercent = 10.0
        offlineCommissionPercent = 5.0
        fssaiLicenseNumber = ""
        panNumber = ""
        aadhaarNumber = ""
        bankAccountNumber = ""
        bankIfscCode = ""
        bankName = ""
        validationError = null
    }

    fun submitKitchenPartner() {
        if (kitchenName.trim().isBlank()) {
            validationError = "⚠️ Please enter the Kitchen / Business Name."
            return
        }
        if (ownerName.trim().isBlank()) {
            validationError = "⚠️ Please enter the Owner / Contact Person Name."
            return
        }
        val cleanMobile = ownerMobile.trim().replace(" ", "").replace("+91", "")
        if (cleanMobile.length < 10) {
            validationError = "⚠️ Please enter a valid 10-digit primary mobile number."
            return
        }
        if (fullAddress.trim().isBlank()) {
            validationError = "⚠️ Please provide the kitchen's full operating address."
            return
        }

        val partnerId = "caterer_" + System.currentTimeMillis()
        val displayName = if (brandName.trim().isNotBlank()) brandName.trim() else kitchenName.trim()
        val formattedMobile = if (ownerMobile.startsWith("+91")) ownerMobile.trim() else "+91 ${cleanMobile.take(5)} ${cleanMobile.drop(5)}"

        val newCaterer = CatererEntity(
            id = partnerId,
            name = displayName,
            kitchenName = kitchenName.trim(),
            logoUrl = "",
            bannerUrl = "",
            rating = 4.8f,
            reviewCount = 0,
            deliveryTimeMinutes = deliveryTimeMinutesText.toIntOrNull() ?: 45,
            minOrderAmount = minOrderAmountText.toDoubleOrNull() ?: 1500.0,
            deliveryCharge = deliveryChargeText.toDoubleOrNull() ?: 150.0,
            distanceKm = distanceKmText.toDoubleOrNull() ?: 2.5,
            fssaiLicense = if (fssaiLicenseNumber.isNotBlank()) fssaiLicenseNumber.trim() else "11524000112233",
            isFssaiVerified = isFssaiVerified,
            isOpenForBooking = isOpenForOnlineBooking,
            address = fullAddress.trim(),
            city = city.trim(),
            ownerMobile = formattedMobile,
            kycStatus = kycStatus,
            aadhaarNumber = aadhaarNumber.trim(),
            panNumber = panNumber.trim(),
            bankAccount = bankAccountNumber.trim(),
            bankIfsc = bankIfscCode.trim(),
            fssaiDocUrl = "fssai_cert_verified.pdf",
            aadhaarDocUrl = "aadhaar_card_doc.jpg",
            panDocUrl = "pan_card_doc.jpg",
            bankChequeDocUrl = "cancelled_cheque.jpg",
            kitchenPhotoUrl = "kitchen_sanitation.jpg",
            kycNotes = kycNotes.trim(),
            onlineCommissionPercentage = onlineCommissionPercent,
            offlineCommissionPercentage = offlineCommissionPercent,
            isOfflineBookingEnabled = isOfflineBookingEnabled
        )

        validationError = null
        viewModel.addKitchenPartner(newCaterer) { created ->
            newlyCreatedPartner = created
            showSuccessDialog = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(AmberSecondary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AddBusiness,
                                    contentDescription = null,
                                    tint = AmberSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Onboard New Kitchen Partner",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "सुपर एडमिन - नया किचन पार्टनर जोड़ें",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Quick Auto-Fill Demo Button
                        Button(
                            onClick = { autoFillDemoData() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SaffronPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_autofill_kitchen_btn")
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Auto-Fill Demo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Register business profile, contact details, KYC certification, and customize individual Online & Offline commission rates for this partner kitchen.",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Validation Error Banner
        AnimatedVisibility(visible = validationError != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                border = BorderStroke(1.dp, NonVegRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = NonVegRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = validationError ?: "",
                        fontSize = 13.sp,
                        color = NonVegRed,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { validationError = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Clear, contentDescription = "Close", tint = NonVegRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 1: Business Profile
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SectionHeader(
                    icon = Icons.Default.Storefront,
                    title = "1. Business & Kitchen Profile",
                    subtitle = "Kitchen registered name, brand display, cuisine & location"
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = kitchenName,
                    onValueChange = { kitchenName = it; if (brandName.isBlank()) brandName = it },
                    label = { Text("Kitchen Registered Business Name *") },
                    placeholder = { Text("e.g. Al-Barakah Royal Catering Services Pvt Ltd") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = SaffronPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_kitchen_business_name"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = brandName,
                        onValueChange = { brandName = it },
                        label = { Text("App Display / Brand Name *") },
                        placeholder = { Text("e.g. Al-Barakah Caterers") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = AmberSecondary) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_kitchen_brand_name"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Food Type Selector
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Food Category", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = selectedFoodType == FoodType.VEG,
                                onClick = { selectedFoodType = FoodType.VEG },
                                label = { Text("Veg 🌿", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VegGreen.copy(alpha = 0.15f),
                                    selectedLabelColor = VegGreen
                                )
                            )
                            FilterChip(
                                selected = selectedFoodType == FoodType.NON_VEG,
                                onClick = { selectedFoodType = FoodType.NON_VEG },
                                label = { Text("Non-Veg 🍗", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NonVegRed.copy(alpha = 0.15f),
                                    selectedLabelColor = NonVegRed
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = cuisineSpecialties,
                    onValueChange = { cuisineSpecialties = it },
                    label = { Text("Cuisine Specialties & Key Offerings") },
                    placeholder = { Text("e.g. Mughlai, Dum Biryani, Awadhi Gravies, Live Sweets") },
                    leadingIcon = { Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_kitchen_cuisines"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // City Selector Chips
                Text("Operating City / Zone *", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickCities.forEach { cityName ->
                        FilterChip(
                            selected = city == cityName,
                            onClick = { city = cityName },
                            label = { Text(cityName, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it },
                    label = { Text("Full Kitchen Address & Pin Code *") },
                    placeholder = { Text("Plot/Shop number, Street, Landmark, Pin Code") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = SaffronPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_kitchen_address"),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Logistics Parameters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = minOrderAmountText,
                        onValueChange = { minOrderAmountText = it },
                        label = { Text("Min Order (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = deliveryChargeText,
                        onValueChange = { deliveryChargeText = it },
                        label = { Text("Delivery Base (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = deliveryTimeMinutesText,
                        onValueChange = { deliveryTimeMinutesText = it },
                        label = { Text("Prep Time (m)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 2: Contact & Owner Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SectionHeader(
                    icon = Icons.Default.ContactPhone,
                    title = "2. Owner & Contact Information",
                    subtitle = "Direct communication, notification SMS, and WhatsApp alerts"
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = { ownerName = it },
                    label = { Text("Owner / Manager Full Name *") },
                    placeholder = { Text("e.g. Mohd. Tariq Siddiqui") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = SaffronPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_owner_name"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = ownerMobile,
                        onValueChange = {
                            ownerMobile = it
                            if (ownerWhatsapp.isBlank()) ownerWhatsapp = it
                        },
                        label = { Text("Primary Mobile (10-Digit) *") },
                        placeholder = { Text("9876543210") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = VegGreen) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_owner_mobile"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = ownerWhatsapp,
                        onValueChange = { ownerWhatsapp = it },
                        label = { Text("WhatsApp Number") },
                        placeholder = { Text("9876543210") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = VegGreen) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_owner_whatsapp"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = emailAddress,
                    onValueChange = { emailAddress = it },
                    label = { Text("Official Contact Email Address") },
                    placeholder = { Text("partner@catererswale.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_owner_email"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 3: Initial Commission & Booking Channel Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SectionHeader(
                    icon = Icons.Default.Percent,
                    title = "3. Commission & Booking Settings",
                    subtitle = "Set customized online & offline platform cut for this partner"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Online Commission Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
                    border = BorderStroke(1.dp, Color(0xFF90CAF9)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "📱 Online App Booking Commission",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0D47A1)
                                )
                                Text(
                                    text = "Commission cut charged on customer orders placed via app",
                                    fontSize = 11.sp,
                                    color = Color(0xFF546E7A)
                                )
                            }
                            Switch(
                                checked = isOpenForOnlineBooking,
                                onCheckedChange = { isOpenForOnlineBooking = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF0D47A1),
                                    checkedTrackColor = Color(0xFF90CAF9)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Rate: ${onlineCommissionPercent.toInt()}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0D47A1)
                            )
                            Text(
                                text = if (isOpenForOnlineBooking) "Status: ACTIVE" else "Status: PAUSED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOpenForOnlineBooking) VegGreen else Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick rate chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            onlineCommissionPresets.forEach { rate ->
                                FilterChip(
                                    selected = onlineCommissionPercent == rate,
                                    onClick = { onlineCommissionPercent = rate },
                                    label = { Text("${rate.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF0D47A1),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Offline Commission Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    border = BorderStroke(1.dp, AmberSecondary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🏬 Offline Direct POS Commission",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    text = "Convenience platform fee on walk-in direct bulk bookings",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6D4C41)
                                )
                            }
                            Switch(
                                checked = isOfflineBookingEnabled,
                                onCheckedChange = { isOfflineBookingEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFE65100),
                                    checkedTrackColor = Color(0xFFFFCC80)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Rate: ${offlineCommissionPercent.toInt()}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = if (isOfflineBookingEnabled) "Status: ENABLED" else "Status: DISABLED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOfflineBookingEnabled) VegGreen else Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick rate chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            offlineCommissionPresets.forEach { rate ->
                                FilterChip(
                                    selected = offlineCommissionPercent == rate,
                                    onClick = { offlineCommissionPercent = rate },
                                    label = { Text("${rate.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFE65100),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Revenue Split Calculator Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Calculate, contentDescription = null, tint = AmberSecondary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Live Commission Simulator", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                            // Toggle Sample Amount
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(5000.0, 10000.0, 25000.0).forEach { amt ->
                                    FilterChip(
                                        selected = sampleOrderAmount == amt,
                                        onClick = { sampleOrderAmount = amt },
                                        label = { Text("₹${amt.toInt() / 1000}k", fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AmberSecondary,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(10.dp))

                        val onlineFee = (sampleOrderAmount * (onlineCommissionPercent / 100.0)).toInt()
                        val onlinePayout = sampleOrderAmount.toInt() - onlineFee
                        val offlineFee = (sampleOrderAmount * (offlineCommissionPercent / 100.0)).toInt()
                        val offlinePayout = sampleOrderAmount.toInt() - offlineFee

                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Online Column
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Online App Order (₹${sampleOrderAmount.toInt()})", fontSize = 11.sp, color = Color(0xFF90CAF9), fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Platform Share: ₹$onlineFee (${onlineCommissionPercent.toInt()}%)", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Kitchen Payout: ₹$onlinePayout", fontSize = 12.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                            }

                            Divider(
                                modifier = Modifier
                                    .height(45.dp)
                                    .width(1.dp),
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            // Offline Column
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Offline Direct Order (₹${sampleOrderAmount.toInt()})", fontSize = 11.sp, color = Color(0xFFFFCC80), fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Platform Share: ₹$offlineFee (${offlineCommissionPercent.toInt()}%)", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Kitchen Payout: ₹$offlinePayout", fontSize = 12.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 4: Legal & KYC Verification
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SectionHeader(
                    icon = Icons.Default.Security,
                    title = "4. Legal & KYC Compliance (Initial Status)",
                    subtitle = "FSSAI license number, PAN card, and onboarding approval"
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = fssaiLicenseNumber,
                        onValueChange = { fssaiLicenseNumber = it },
                        label = { Text("14-Digit FSSAI License Number") },
                        placeholder = { Text("11521019000342") },
                        leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null, tint = VegGreen) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("input_fssai_license"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    // Initial KYC Status
                    Column(modifier = Modifier.weight(1f)) {
                        Text("KYC Status", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            FilterChip(
                                selected = kycStatus == KycStatus.APPROVED,
                                onClick = { kycStatus = KycStatus.APPROVED; isFssaiVerified = true },
                                label = { Text("Approve", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VegGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = kycStatus == KycStatus.PENDING,
                                onClick = { kycStatus = KycStatus.PENDING; isFssaiVerified = false },
                                label = { Text("Pending", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AmberSecondary,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = panNumber,
                        onValueChange = { panNumber = it.uppercase() },
                        label = { Text("Business PAN Number") },
                        placeholder = { Text("ABCDE1234F") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = aadhaarNumber,
                        onValueChange = { aadhaarNumber = it },
                        label = { Text("Aadhaar Number") },
                        placeholder = { Text("9876 5432 1098") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = kycNotes,
                    onValueChange = { kycNotes = it },
                    label = { Text("Super Admin Onboarding Notes") },
                    placeholder = { Text("Physical inspection verified by Super Admin team") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 5: Banking & Weekly Payout Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SectionHeader(
                    icon = Icons.Default.AccountBalance,
                    title = "5. Bank Account & Settlement Payouts",
                    subtitle = "Bank details for automated weekly NEFT/RTGS payouts"
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = bankAccountNumber,
                    onValueChange = { bankAccountNumber = it },
                    label = { Text("Bank Account Number") },
                    placeholder = { Text("50200098765432") },
                    leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = SaffronPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_bank_account"),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = bankIfscCode,
                        onValueChange = { bankIfscCode = it.uppercase() },
                        label = { Text("Bank IFSC Code") },
                        placeholder = { Text("HDFC0001234") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Bank & Branch Name") },
                        placeholder = { Text("HDFC Bank, New Delhi") },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { clearForm() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Clear, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset Form", fontSize = 14.sp)
            }

            Button(
                onClick = { submitKitchenPartner() },
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp)
                    .testTag("register_kitchen_partner_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SaffronPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(Icons.Default.AddBusiness, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Register & Activate Partner",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Success Confirmation Dialog
    if (showSuccessDialog && newlyCreatedPartner != null) {
        val partner = newlyCreatedPartner!!
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VegGreen, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kitchen Partner Onboarded!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Partner kitchen '${partner.name}' (${partner.kitchenName}) has been registered and activated into the CaterersWale network.",
                        fontSize = 13.sp,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("ID: ${partner.id}", fontSize = 11.sp, color = Color.Gray)
                            Text("City: ${partner.city}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Contact: ${partner.ownerMobile}", fontSize = 12.sp)
                            Text("Online Commission: ${partner.onlineCommissionPercentage.toInt()}%", fontSize = 12.sp, color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold)
                            Text("Offline POS Commission: ${partner.offlineCommissionPercentage.toInt()}%", fontSize = 12.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        clearForm()
                        onPartnerAddedSuccess?.invoke()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen)
                ) {
                    Text("Done / View Partners")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        clearForm()
                    }
                ) {
                    Text("Add Another Kitchen")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SaffronPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
