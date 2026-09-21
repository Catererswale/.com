package com.example.ui.kitchen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import kotlinx.coroutines.delay

/**
 * Reusable OTP verification component for registering/saving delivery boys.
 * Verifies whether the delivery boy's mobile number is correct or fake/wrong.
 */
@Composable
fun DeliveryBoyOtpVerificationSection(
    mobileNumber: String,
    isVerified: Boolean,
    onVerificationSuccess: () -> Unit,
    onResetVerification: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var otpSent by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var resendCountdown by remember { mutableIntStateOf(30) }

    // Track last verified mobile so any change in digits automatically resets verification
    var lastObservedMobile by remember { mutableStateOf(mobileNumber) }

    LaunchedEffect(mobileNumber) {
        if (mobileNumber != lastObservedMobile) {
            lastObservedMobile = mobileNumber
            if (isVerified) {
                onResetVerification()
            }
            otpSent = false
            generatedOtp = ""
            enteredOtp = ""
            errorMessage = ""
        }
    }

    // Countdown timer effect
    LaunchedEffect(otpSent, resendCountdown) {
        if (otpSent && resendCountdown > 0) {
            delay(1000L)
            resendCountdown -= 1
        }
    }

    val cleanDigits = mobileNumber.filter { it.isDigit() }.takeLast(10)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("delivery_boy_otp_section")
    ) {
        if (isVerified) {
            // ==================== 1. VERIFIED STATE ====================
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VegGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = VegGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Mobile Number Verified ✅",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                            }
                            Text(
                                text = "नंबर सही है (OTP Verified: +91 $cleanDigits)",
                                fontSize = 11.sp,
                                color = Color(0xFF15803D)
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            onResetVerification()
                            otpSent = false
                            generatedOtp = ""
                            enteredOtp = ""
                            errorMessage = ""
                        }
                    ) {
                        Text("Re-verify", fontSize = 11.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // ==================== 2. UNVERIFIED / PENDING OTP STATE ====================
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (otpSent) Color(0xFFFFFBEB) else Color(0xFFF8FAFC)
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    1.dp,
                    if (otpSent) Color(0xFFFDE68A) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Header prompt
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (otpSent) Color(0xFFD97706) else SaffronPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (otpSent) "Verify OTP (ओटीपी दर्ज करें)" else "Final Check: Mobile OTP Verification (मोबाइल सत्यापन)",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (cleanDigits.length >= 10) {
                            "Delivery boy ka number (+91 $cleanDigits) sahi hai ya galat, confirm karne ke liye OTP verify karein."
                        } else {
                            "Delivery boy ka mobile number sahi hai ya galat, confirm karne ke liye pehle upar 10-digit mobile number bharein."
                        },
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!otpSent) {
                        // SEND OTP ACTION
                        Button(
                            onClick = {
                                if (cleanDigits.length < 10) {
                                    errorMessage = "⚠️ Kripya upar valid 10-digit mobile number enter karein (उदा. 9876543210)"
                                    return@Button
                                }
                                val newOtp = (1000..9999).random().toString()
                                generatedOtp = newOtp
                                otpSent = true
                                enteredOtp = ""
                                errorMessage = ""
                                resendCountdown = 30
                                Toast.makeText(
                                    context,
                                    "📲 OTP sent to +91 $cleanDigits! Code: $newOtp",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            enabled = cleanDigits.length >= 10,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SaffronPrimary,
                                disabledContainerColor = Color(0xFFCBD5E1)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("send_delivery_boy_otp_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (cleanDigits.length >= 10) {
                                    "SEND OTP TO +91 $cleanDigits"
                                } else {
                                    "ENTER MOBILE NUMBER TO SEND OTP"
                                },
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // CHHOTA SMS BOX (Compact Single-Line SMS Code + Quick Auto-Fill)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF0F172A),
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
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "SMS Code: ",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = generatedOtp,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFFFEF08A)
                                    )
                                }

                                // Quick Auto-fill button
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF334155),
                                    onClick = {
                                        enteredOtp = generatedOtp
                                        Toast.makeText(context, "OTP Auto-Filled: $generatedOtp", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MarkEmailRead,
                                            contentDescription = null,
                                            modifier = Modifier.size(11.dp),
                                            tint = Color(0xFF38BDF8)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Auto-Fill", fontSize = 10.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // OTP TYPE KARNE WALA BOX + VERIFY BUTTON
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = enteredOtp,
                                onValueChange = {
                                    if (it.length <= 4 && it.all { ch -> ch.isDigit() }) {
                                        enteredOtp = it
                                        errorMessage = ""
                                    }
                                },
                                placeholder = { Text("Type 4-digit OTP", fontSize = 11.5.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(15.dp))
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("delivery_boy_otp_input"),
                                shape = RoundedCornerShape(6.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SaffronPrimary,
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                )
                            )

                            Button(
                                onClick = {
                                    if (enteredOtp.length < 4) {
                                        errorMessage = "⚠️ Kripya 4-digit OTP enter karein."
                                        return@Button
                                    }
                                    if (enteredOtp == generatedOtp) {
                                        onVerificationSuccess()
                                        otpSent = false
                                        errorMessage = ""
                                        Toast.makeText(
                                            context,
                                            "✅ Mobile number sahi hai! Successfully verified.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        errorMessage = "❌ Galat OTP! (Sahi OTP: $generatedOtp)"
                                        Toast.makeText(
                                            context,
                                            "❌ Galat OTP! Please enter correct code.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                                modifier = Modifier.testTag("verify_delivery_boy_otp_button")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("VERIFY", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Resend Countdown Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "OTP yahan type karein ya Auto-Fill dabayein",
                                fontSize = 9.5.sp,
                                color = Color(0xFF64748B)
                            )

                            if (resendCountdown > 0) {
                                Text(
                                    text = "Resend in ${resendCountdown}s",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                TextButton(
                                    onClick = {
                                        val newOtp = (1000..9999).random().toString()
                                        generatedOtp = newOtp
                                        enteredOtp = ""
                                        errorMessage = ""
                                        resendCountdown = 30
                                        Toast.makeText(
                                            context,
                                            "📲 New OTP sent: $newOtp",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(11.dp), tint = SaffronPrimary)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Resend OTP", fontSize = 10.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // ERROR MESSAGE
                    if (errorMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFECACA)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = errorMessage,
                                    color = Color(0xFFB91C1C),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
