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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.SafetyCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserProfile
import com.example.data.models.UserRole
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary

@Composable
fun AuthScreen(
    onLoginSuccess: (UserProfile) -> Unit,
    onQuickDemoLogin: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }
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

    // OTP simulation
    var isOtpSent by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf("1234") }

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
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SaffronPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestaurantMenu,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
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
                            // --- LOGIN FORM ---
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

                            Spacer(modifier = Modifier.height(12.dp))

                            // OTP Simulation Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = {
                                    if (mobileOrEmail.isBlank()) {
                                        Toast.makeText(context, "Please enter mobile number first", Toast.LENGTH_SHORT).show()
                                    } else {
                                        isOtpSent = true
                                        otpInput = "1234"
                                        Toast.makeText(context, "🔑 OTP sent to $mobileOrEmail! Auto-filled: 1234", Toast.LENGTH_LONG).show()
                                    }
                                }) {
                                    Text("Login via OTP (ओटीपी द्वारा लॉगिन)", color = Color(0xFF0288D1), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (isOtpSent) {
                                OutlinedTextField(
                                    value = otpInput,
                                    onValueChange = { otpInput = it },
                                    label = { Text("Enter 4-Digit OTP") },
                                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = SaffronPrimary) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SaffronPrimary)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (mobileOrEmail.isBlank() && !isOtpSent) {
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
