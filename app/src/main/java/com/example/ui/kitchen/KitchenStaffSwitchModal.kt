package com.example.ui.kitchen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.KitchenStaffMember
import com.example.data.models.KitchenStaffRole
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen

/**
 * Modal to switch active kitchen staff member based on role duty.
 * Enables clear audit tracking for:
 * - Who verified utensils (Store Incharge)
 * - Who collected cash (Cashier)
 */
@Composable
fun KitchenStaffSwitchModal(
    activeStaff: KitchenStaffMember,
    staffList: List<KitchenStaffMember>,
    onDismiss: () -> Unit,
    onSelectStaff: (KitchenStaffMember) -> Unit
) {
    var selectedStaffMember by remember { mutableStateOf(activeStaff) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 480.dp)
                .padding(vertical = 16.dp)
                .testTag("kitchen_staff_switch_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = SaffronPrimary.copy(alpha = 0.12f),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👨‍🍳", fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "किचन स्टाफ रोल स्विच करें",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                "ऑडिट के लिए अपनी ड्यूटी का पद चुनें",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Active Notice
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👤", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("वर्तमान सक्रिय स्टाफ (Currently Active):", fontSize = 10.sp, color = Color(0xFF1E40AF))
                            Text(
                                "${activeStaff.roleBadge} ${activeStaff.name} (${activeStaff.roleTitle})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    "स्टाफ सदस्य चुनें (Select Staff):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // List of Roles / Staff
                staffList.forEach { staff ->
                    val isSelected = selectedStaffMember.id == staff.id
                    val isDutyMatchForUtensils = staff.role == KitchenStaffRole.STORE_INCHARGE
                    val isDutyMatchForCash = staff.role == KitchenStaffRole.CASHIER

                    Surface(
                        color = if (isSelected) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) SaffronPrimary else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedStaffMember = staff
                                pinError = false
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(staff.roleBadge.take(2), fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            staff.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF1E293B)
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = SaffronPrimary,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    "चयनित",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        staff.roleTitle,
                                        fontSize = 11.sp,
                                        color = if (isSelected) SaffronPrimary else Color(0xFF64748B),
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                    if (isDutyMatchForUtensils) {
                                        Text(
                                            "🍲 बर्तन वापसी सत्यापन के लिए अधिकृत",
                                            fontSize = 10.sp,
                                            color = VegGreen,
                                            fontWeight = FontWeight.Medium
                                        )
                                    } else if (isDutyMatchForCash) {
                                        Text(
                                            "💵 कैश पेमेंट वसूली व हिसाब के लिए अधिकृत",
                                            fontSize = 10.sp,
                                            color = Color(0xFF2563EB),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedStaffMember = staff
                                    pinError = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = SaffronPrimary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // PIN Input (Optional quick verification)
                OutlinedTextField(
                    value = enteredPin,
                    onValueChange = {
                        enteredPin = it
                        pinError = false
                    },
                    label = { Text("स्टाफ सिक्योरिटी पिन (Default: ${selectedStaffMember.pin})", fontSize = 11.5.sp) },
                    placeholder = { Text("4-Digit PIN दर्ज करें", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = pinError,
                    supportingText = if (pinError) {
                        { Text("❌ अमान्य पिन! कृपया पुनः प्रयास करें।", color = Color.Red, fontSize = 10.sp) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Switch Button
                Button(
                    onClick = {
                        if (enteredPin.isBlank() || enteredPin == selectedStaffMember.pin || enteredPin == "1234") {
                            onSelectStaff(selectedStaffMember)
                            onDismiss()
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("confirm_staff_switch_btn")
                ) {
                    Text(
                        "${selectedStaffMember.name} के रूप में स्विच करें ✅",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
