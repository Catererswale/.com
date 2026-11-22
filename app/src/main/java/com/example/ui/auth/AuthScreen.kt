package com.example.ui.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.KitchenStaffMember
import com.example.data.models.KitchenStaffRole
import com.example.data.models.UserProfile
import com.example.data.models.UserRole
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen

@Composable
fun AuthScreen(
    initialRole: UserRole = UserRole.CUSTOMER,
    isStandaloneRole: Boolean = false,
    onLoginSuccess: (UserProfile) -> Unit,
    onQuickDemoLogin: (UserRole) -> Unit,
    onQuickKitchenStaffLogin: ((KitchenStaffMember) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedRole by remember(initialRole) { mutableStateOf(initialRole) }
    var isSignUpMode by remember { mutableStateOf(false) }

    // Form fields
    var mobileOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("New Delhi") }
    var fssaiOrLicense by remember { mutableStateOf("") }
    var adminPin by remember { mutableStateOf("") }

    // Mobile OTP login state
    var loginMethod by remember { mutableStateOf("OTP") } // "OTP" or "PASSWORD"
    var otpMobileNumber by remember { mutableStateOf("9876543210") }
    var customerOtpName by remember { mutableStateOf("Rohan Verma") }
    var isOtpSent by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf("7291") }
    var resendCountdown by remember { mutableStateOf(0) }

    val defaultKitchenStaffList = remember {
        listOf(
            KitchenStaffMember("stf_2", "Mohd Salim", KitchenStaffRole.STORE_INCHARGE, "2222", "+91 98111 22332"),
            KitchenStaffMember("stf_3", "Arif Sheikh", KitchenStaffRole.CASHIER, "3333", "+91 98111 22333"),
            KitchenStaffMember("stf_4", "Feroz Khan", KitchenStaffRole.MANAGER, "4444", "+91 98111 22334"),
            KitchenStaffMember("stf_1", "Ustad Rashid Khan", KitchenStaffRole.HALWAI, "1111", "+91 98111 22331")
        )
    }
    var selectedKitchenStaff by remember { mutableStateOf(defaultKitchenStaffList[0]) }
    var staffPinInput by remember { mutableStateOf("") }

    LaunchedEffect(resendCountdown) {
        if (resendCountdown > 0) {
            kotlinx.coroutines.delay(1000)
            resendCountdown -= 1
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFF9F6F0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2B1B1B), Color(0xFF422822))
                        )
                    )
                    .padding(vertical = 24.dp, horizontal = 20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White,
                            shadowElevation = 3.dp,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_catererswale_mark),
                                contentDescription = "Catererswale Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "CaterersWale",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "कैटरर्सवाले पोर्टल - Login / Sign Up",
                                fontSize = 12.sp,
                                color = AmberSecondary
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                if (isStandaloneRole) {
                    val roleLabel = when (selectedRole) {
                        UserRole.CUSTOMER -> "🛒 CaterersWale Customer Login"
                        UserRole.KITCHEN -> "👨‍🍳 CW Kitchen Partner Login"
                        UserRole.DELIVERY_BOY -> "🚴 CW Delivery Partner Login"
                        UserRole.SUPER_ADMIN -> "🛡️ CaterersWale Admin Login"
                    }
                    val roleSub = when (selectedRole) {
                        UserRole.CUSTOMER -> "Bulk Food Ordering & Live Caterer Booking"
                        UserRole.KITCHEN -> "Kitchen Display System, Live Queue & Settlements"
                        UserRole.DELIVERY_BOY -> "Order Deliveries, Live Route & OTP Completion"
                        UserRole.SUPER_ADMIN -> "Platform Operations & Master Controls"
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF3E0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (selectedRole) {
                                    UserRole.CUSTOMER -> "🛒"
                                    UserRole.KITCHEN -> "👨‍🍳"
                                    UserRole.DELIVERY_BOY -> "🚴"
                                    UserRole.SUPER_ADMIN -> "🛡️"
                                },
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = roleLabel,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2B1B1B)
                                )
                                Text(
                                    text = roleSub,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF555555)
                                )
                            }
                        }
                    }
                } else {
                    // Role Selector Tabs
                    Text(
                        text = "Select Portal / रोल चुनें:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                RoleAuthChip(
                                    label = "Customer\n(ग्राहक)",
                                    icon = Icons.Default.Person,
                                    isSelected = selectedRole == UserRole.CUSTOMER,
                                    onClick = {
                                        selectedRole = UserRole.CUSTOMER
                                        isOtpSent = false
                                    },
                                    testTag = "auth_role_customer"
                                )
                                RoleAuthChip(
                                    label = "Kitchen\n(रसोई)",
                                    icon = Icons.Default.Kitchen,
                                    isSelected = selectedRole == UserRole.KITCHEN,
                                    onClick = {
                                        selectedRole = UserRole.KITCHEN
                                        isOtpSent = false
                                    },
                                    testTag = "auth_role_kitchen"
                                )
                                RoleAuthChip(
                                    label = "Delivery\n(डिलीवरी)",
                                    icon = Icons.Default.DeliveryDining,
                                    isSelected = selectedRole == UserRole.DELIVERY_BOY,
                                    onClick = {
                                        selectedRole = UserRole.DELIVERY_BOY
                                        isOtpSent = false
                                    },
                                    testTag = "auth_role_delivery"
                                )
                                RoleAuthChip(
                                    label = "Admin\n(एडमिन)",
                                    icon = Icons.Default.AdminPanelSettings,
                                    isSelected = selectedRole == UserRole.SUPER_ADMIN,
                                    onClick = {
                                        selectedRole = UserRole.SUPER_ADMIN
                                        isOtpSent = false
                                    },
                                    testTag = "auth_role_admin"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mode Selector: Login vs Sign Up
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Toggle Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(25.dp))
                                .background(Color(0xFFF0F0F0))
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (!isSignUpMode) SaffronPrimary else Color.Transparent)
                                    .clickable { isSignUpMode = false }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "LOGIN (लॉगिन)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (!isSignUpMode) Color.White else Color.Gray
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSignUpMode) SaffronPrimary else Color.Transparent)
                                    .clickable { isSignUpMode = true }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "SIGN UP (साइन अप)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSignUpMode) Color.White else Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        val roleTitle = when (selectedRole) {
                            UserRole.CUSTOMER -> "Customer Portal"
                            UserRole.KITCHEN -> "Kitchen & Caterer Portal"
                            UserRole.DELIVERY_BOY -> "Delivery Partner Portal"
                            UserRole.SUPER_ADMIN -> "Super Admin Portal"
                        }

                        Text(
                            text = if (isSignUpMode) "New Account Registration - $roleTitle" else "Account Login - $roleTitle",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (!isSignUpMode) {
                            if (selectedRole == UserRole.KITCHEN) {
                                Surface(
                                    color = Color(0xFFFFFBEB),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier.fillMaxWidth().testTag("kitchen_role_auth_section")
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("👨‍🍳", fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    "Kitchen Staff Role Login (पद अनुसार लॉगिन)",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF92400E)
                                                )
                                                Text(
                                                    "बर्तन सत्यापन व पेमेंट लेने वाले का नाम ऑडिट में दर्ज होगा",
                                                    fontSize = 10.5.sp,
                                                    color = Color(0xFFB45309)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        defaultKitchenStaffList.forEach { staff ->
                                            val isSelected = selectedKitchenStaff.id == staff.id
                                            Surface(
                                                color = if (isSelected) Color.White else Color(0xFFFEF3C7).copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(
                                                    if (isSelected) 2.dp else 1.dp,
                                                    if (isSelected) SaffronPrimary else Color(0xFFFDE68A)
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 3.dp)
                                                    .clickable { selectedKitchenStaff = staff }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                        Text(staff.roleBadge.take(2), fontSize = 16.sp)
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Column {
                                                            Text(staff.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E293B))
                                                            Text(
                                                                staff.roleTitle,
                                                                fontSize = 10.5.sp,
                                                                color = if (isSelected) SaffronPrimary else Color.Gray,
                                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                                            )
                                                        }
                                                    }
                                                    RadioButton(
                                                        selected = isSelected,
                                                        onClick = { selectedKitchenStaff = staff },
                                                        colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Button(
                                            onClick = {
                                                onQuickKitchenStaffLogin?.invoke(selectedKitchenStaff) ?: run {
                                                    val profile = UserProfile(
                                                        id = selectedKitchenStaff.id,
                                                        name = "${selectedKitchenStaff.name} (${selectedKitchenStaff.roleTitle})",
                                                        mobile = selectedKitchenStaff.mobile,
                                                        email = "kitchen@caterers.com",
                                                        role = UserRole.KITCHEN,
                                                        businessName = "A1 Huma Central Kitchen",
                                                        city = "New Delhi",
                                                        fssaiOrLicense = "23319008000123"
                                                    )
                                                    onLoginSuccess(profile)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(42.dp).testTag("login_kitchen_staff_btn")
                                        ) {
                                            Text("${selectedKitchenStaff.name} [${selectedKitchenStaff.roleBadge}] लॉगिन करें 🚀", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                                    Text("  या मोबाइल / पासवर्ड से  ", fontSize = 10.5.sp, color = Color.Gray)
                                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // --- SUB-TOGGLE: MOBILE OTP vs PASSWORD ---
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (loginMethod == "OTP") Color.White else Color.Transparent)
                                        .clickable { loginMethod = "OTP" }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Sms,
                                            contentDescription = null,
                                            tint = if (loginMethod == "OTP") SaffronPrimary else Color(0xFF64748B),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "📲 Mobile OTP (ओटीपी)",
                                            fontSize = 12.sp,
                                            fontWeight = if (loginMethod == "OTP") FontWeight.Bold else FontWeight.Medium,
                                            color = if (loginMethod == "OTP") SaffronPrimary else Color(0xFF64748B)
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (loginMethod == "PASSWORD") Color.White else Color.Transparent)
                                        .clickable { loginMethod = "PASSWORD" }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (loginMethod == "PASSWORD") SaffronPrimary else Color(0xFF64748B),
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            "🔑 Password (पासवर्ड)",
                                            fontSize = 12.sp,
                                            fontWeight = if (loginMethod == "PASSWORD") FontWeight.Bold else FontWeight.Medium,
                                            color = if (loginMethod == "PASSWORD") SaffronPrimary else Color(0xFF64748B)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (loginMethod == "OTP") {
                                // ===== MOBILE OTP LOGIN FLOW =====
                                if (!isOtpSent) {
                                    // STEP 1: Enter mobile number and request OTP
                                    Text(
                                        "Enter your registered mobile number for instant SMS login:",
                                        fontSize = 12.sp,
                                        color = Color(0xFF475569)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = otpMobileNumber,
                                        onValueChange = { input ->
                                            val digitsOnly = input.filter { it.isDigit() }
                                            if (digitsOnly.length <= 10) {
                                                otpMobileNumber = digitsOnly
                                            }
                                        },
                                        label = { Text("Mobile Number (10 डिजिट मोबाइल)") },
                                        leadingIcon = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                                            ) {
                                                Text("🇮🇳 +91", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Box(modifier = Modifier.width(1.dp).height(20.dp).background(Color(0xFFCBD5E1)))
                                            }
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        modifier = Modifier.fillMaxWidth().testTag("otp_mobile_input"),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = {
                                            if (otpMobileNumber.length < 10) {
                                                Toast.makeText(context, "Kripya 10-digit mobile number enter karein", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            val newCode = (1000..9999).random().toString()
                                            generatedOtp = newCode
                                            otpInput = ""
                                            isOtpSent = true
                                            resendCountdown = 30
                                            Toast.makeText(context, "📲 OTP sent to +91 $otpMobileNumber! Code: $newCode", Toast.LENGTH_LONG).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("send_otp_button")
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("SEND OTP VIA SMS (ओटीपी भेजें)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                } else {
                                    // STEP 2: Verify OTP
                                    Surface(
                                        color = Color(0xFFF0FDF4),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text("OTP sent via SMS", fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                                                    Text("+91 $otpMobileNumber", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF166534))
                                                }
                                            }
                                            TextButton(
                                                onClick = {
                                                    isOtpSent = false
                                                    otpInput = ""
                                                }
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text("Change", fontSize = 11.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Auto-Fill Demo Chip
                                    Surface(
                                        color = Color(0xFFFFFBEB),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Sms, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "SMS OTP: [ $generatedOtp ]",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color(0xFF92400E)
                                                )
                                            }
                                            Surface(
                                                color = SaffronPrimary,
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.clickable {
                                                    otpInput = generatedOtp
                                                    Toast.makeText(context, "OTP Auto-Filled: $generatedOtp", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Text(
                                                    "Auto-Fill ⚡",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = otpInput,
                                        onValueChange = { input ->
                                            val digitsOnly = input.filter { it.isDigit() }
                                            if (digitsOnly.length <= 4) {
                                                otpInput = digitsOnly
                                            }
                                        },
                                        label = { Text("Enter 4-Digit OTP (ओटीपी दर्ज करें)") },
                                        leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = SaffronPrimary) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth().testTag("otp_digit_input"),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                    )

                                    if (selectedRole == UserRole.CUSTOMER) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedTextField(
                                            value = customerOtpName,
                                            onValueChange = { customerOtpName = it },
                                            label = { Text("Your Name (आपका नाम)") },
                                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SaffronPrimary) },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth().testTag("otp_customer_name"),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Resend OTP Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (resendCountdown > 0) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Resend OTP in 0:${resendCountdown.toString().padStart(2, '0')}", fontSize = 11.sp, color = Color.Gray)
                                            }
                                        } else {
                                            TextButton(
                                                onClick = {
                                                    val newCode = (1000..9999).random().toString()
                                                    generatedOtp = newCode
                                                    otpInput = ""
                                                    resendCountdown = 30
                                                    Toast.makeText(context, "New OTP sent: $newCode", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Icon(Icons.Default.Refresh, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Resend OTP SMS (ओटीपी दोबारा भेजें)", fontSize = 11.5.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            if (otpInput.isBlank()) {
                                                Toast.makeText(context, "Kripya 4-digit OTP enter karein", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            val isValidOtp = (otpInput.trim() == generatedOtp) ||
                                                    (otpInput.trim() == "1234") ||
                                                    (otpInput.trim() == "0000") ||
                                                    (otpInput.trim() == "7291")

                                            if (!isValidOtp) {
                                                Toast.makeText(context, "❌ Invalid OTP! Please enter: $generatedOtp", Toast.LENGTH_LONG).show()
                                                return@Button
                                            }

                                            val nameToUse = when (selectedRole) {
                                                UserRole.CUSTOMER -> customerOtpName.ifBlank { "Rohan Verma" }
                                                UserRole.KITCHEN -> "A1 Huma Caterers"
                                                UserRole.DELIVERY_BOY -> "Amit Kumar"
                                                UserRole.SUPER_ADMIN -> "Super Admin"
                                            }

                                            val profile = UserProfile(
                                                name = nameToUse,
                                                mobile = "+91 $otpMobileNumber",
                                                role = selectedRole,
                                                businessName = if (selectedRole == UserRole.KITCHEN) "A1 Huma Caterers" else "",
                                                city = "New Delhi"
                                            )
                                            Toast.makeText(context, "✅ Phone Verified Successfully! Welcome $nameToUse", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess(profile)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("verify_otp_button")
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("VERIFY OTP & LOGIN / लॉगिन करें", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            } else {
                                // ===== PASSWORD LOGIN FLOW =====
                                OutlinedTextField(
                                    value = mobileOrEmail,
                                    onValueChange = { mobileOrEmail = it },
                                    label = { Text("Mobile Number or Email / मोबाइल या ईमेल") },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SaffronPrimary) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.fillMaxWidth().testTag("login_input_phone"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Password / पासवर्ड") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SaffronPrimary) },
                                    trailingIcon = {
                                        IconButton(onClick = { showPassword = !showPassword }) {
                                            Icon(
                                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle password"
                                            )
                                        }
                                    },
                                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("login_input_password"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        if (mobileOrEmail.isBlank()) {
                                            Toast.makeText(context, "Please enter Mobile Number or Email", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        val nameToUse = when (selectedRole) {
                                            UserRole.CUSTOMER -> "Rohan Verma"
                                            UserRole.KITCHEN -> "A1 Huma Caterers"
                                            UserRole.DELIVERY_BOY -> "Amit Kumar"
                                            UserRole.SUPER_ADMIN -> "Super Admin"
                                        }
                                        val profile = UserProfile(
                                            name = nameToUse,
                                            mobile = if (mobileOrEmail.isNotBlank()) mobileOrEmail else "+91 98765 43210",
                                            role = selectedRole,
                                            businessName = if (selectedRole == UserRole.KITCHEN) "A1 Huma Caterers" else "",
                                            city = "New Delhi"
                                        )
                                        onLoginSuccess(profile)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("auth_submit_login")
                                ) {
                                    Text("LOGIN TO PORTAL / प्रवेश करें", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        } else {
                            // --- SIGN UP FORM ---
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text(if (selectedRole == UserRole.KITCHEN) "Owner / Manager Name (मालिक का नाम)" else "Full Name (पूरा नाम)") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SaffronPrimary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("signup_name"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (selectedRole == UserRole.KITCHEN || selectedRole == UserRole.DELIVERY_BOY) {
                                OutlinedTextField(
                                    value = businessName,
                                    onValueChange = { businessName = it },
                                    label = { Text(if (selectedRole == UserRole.KITCHEN) "Kitchen / Brand Name (रसोई/ब्रांड का नाम)" else "Vehicle Type / Agency (वाहन प्रकार/एजेंसी)") },
                                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = SaffronPrimary) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("signup_business"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            if (selectedRole == UserRole.KITCHEN) {
                                OutlinedTextField(
                                    value = fssaiOrLicense,
                                    onValueChange = { fssaiOrLicense = it },
                                    label = { Text("FSSAI License Number (एफएसएसएआई नंबर)") },
                                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = SaffronPrimary) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("signup_fssai"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            if (selectedRole == UserRole.SUPER_ADMIN) {
                                OutlinedTextField(
                                    value = adminPin,
                                    onValueChange = { adminPin = it },
                                    label = { Text("Admin Passcode / Secret Key (default: 8888)") },
                                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = SaffronPrimary) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    modifier = Modifier.fillMaxWidth().testTag("signup_admin_pin"),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            OutlinedTextField(
                                value = mobileOrEmail,
                                onValueChange = { mobileOrEmail = it },
                                label = { Text("Mobile Number (मोबाइल नंबर)") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SaffronPrimary) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth().testTag("signup_mobile"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City / Area (शहर / क्षेत्र)") },
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = SaffronPrimary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("signup_city"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Create Password (पासवर्ड बनाएं)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SaffronPrimary) },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("signup_password"),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (fullName.isBlank() || mobileOrEmail.isBlank()) {
                                        Toast.makeText(context, "Please fill required fields (Name & Mobile)", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (selectedRole == UserRole.SUPER_ADMIN && adminPin.isNotBlank() && adminPin != "8888" && adminPin != "admin123") {
                                        Toast.makeText(context, "Incorrect Admin Key! Hint: 8888", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    val profile = UserProfile(
                                        name = fullName,
                                        mobile = mobileOrEmail,
                                        role = selectedRole,
                                        businessName = businessName,
                                        city = city,
                                        fssaiOrLicense = fssaiOrLicense
                                    )
                                    onLoginSuccess(profile)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("auth_submit_signup")
                            ) {
                                Text("REGISTER & CREATE ACCOUNT / खाता बनाएं", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Demo Login Section for Instant Testing
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SafetyCheck, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Quick Testing / तुरंत लॉगिन (Instant Access):",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onQuickDemoLogin(UserRole.CUSTOMER) },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                modifier = Modifier.weight(1f).height(38.dp).testTag("demo_customer_login")
                            ) {
                                Text("👤 Customer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onQuickDemoLogin(UserRole.KITCHEN) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                modifier = Modifier.weight(1f).height(38.dp).testTag("demo_kitchen_login")
                            ) {
                                Text("👨‍🍳 Kitchen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Kitchen Role Direct Testing shortcuts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onQuickKitchenStaffLogin?.invoke(defaultKitchenStaffList[0]) }
                                    .testTag("demo_kitchen_store_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🍲 Store Incharge (बर्तन)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                                }
                            }

                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onQuickKitchenStaffLogin?.invoke(defaultKitchenStaffList[1]) }
                                    .testTag("demo_kitchen_cashier_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("💵 Cashier (कैश)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onQuickDemoLogin(UserRole.DELIVERY_BOY) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                modifier = Modifier.weight(1f).height(38.dp).testTag("demo_delivery_login")
                            ) {
                                Text("🏍️ Delivery", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onQuickDemoLogin(UserRole.SUPER_ADMIN) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                modifier = Modifier.weight(1f).height(38.dp).testTag("demo_admin_login")
                            ) {
                                Text("🛡️ Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun RoleAuthChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor = if (isSelected) SaffronPrimary else Color(0xFFF5F5F5)
    val contentColor = if (isSelected) Color.White else Color(0xFF444444)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                lineHeight = 13.sp
            )
        }
    }
}
