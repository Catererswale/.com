package com.example.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ExitToApp
import com.example.data.models.FoodType
import com.example.data.models.Language
import com.example.data.models.UnitType
import com.example.data.models.UserProfile
import com.example.data.models.UserRole
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.NonVegRed
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen

@Composable
fun RoleSelectorBar(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    currentLanguage: Language,
    onLanguageChange: (Language) -> Unit,
    currentUser: UserProfile? = null,
    isLoggedIn: Boolean = false,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showLangDialog by remember { mutableStateOf(false) }

    Surface(
        color = Color(0xFF2B1B1B),
        contentColor = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoggedIn && currentUser != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "👤 ${currentUser.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SaffronPrimary
                        ) {
                            Text(
                                text = currentRole.name.replace("_", " "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Portal Selector:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoggedIn) {
                        Surface(
                            onClick = onLogout,
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.testTag("topbar_logout_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Logout",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = { showLangDialog = true },
                        modifier = Modifier.size(32.dp).testTag("lang_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = AmberSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = currentLanguage.name.take(2),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberSecondary
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RoleChip(
                    label = "Customer",
                    icon = Icons.Default.Person,
                    isSelected = currentRole == UserRole.CUSTOMER,
                    onClick = { onRoleSelected(UserRole.CUSTOMER) },
                    testTag = "role_customer"
                )
                RoleChip(
                    label = "Kitchen",
                    icon = Icons.Default.Kitchen,
                    isSelected = currentRole == UserRole.KITCHEN,
                    onClick = { onRoleSelected(UserRole.KITCHEN) },
                    testTag = "role_kitchen"
                )
                RoleChip(
                    label = "Delivery",
                    icon = Icons.Default.DeliveryDining,
                    isSelected = currentRole == UserRole.DELIVERY_BOY,
                    onClick = { onRoleSelected(UserRole.DELIVERY_BOY) },
                    testTag = "role_delivery"
                )
                RoleChip(
                    label = "Admin",
                    icon = Icons.Default.AdminPanelSettings,
                    isSelected = currentRole == UserRole.SUPER_ADMIN,
                    onClick = { onRoleSelected(UserRole.SUPER_ADMIN) },
                    testTag = "role_admin"
                )
            }
        }
    }

    if (showLangDialog) {
        LanguageDialog(
            currentLanguage = currentLanguage,
            onSelect = {
                onLanguageChange(it)
                showLangDialog = false
            },
            onDismiss = { showLangDialog = false }
        )
    }
}

@Composable
private fun RoleChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val bgColor = if (isSelected) SaffronPrimary else Color.White.copy(alpha = 0.12f)
    val textColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.85f)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        modifier = Modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 13.sp,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun VegNonVegBadge(foodType: FoodType, modifier: Modifier = Modifier) {
    val isVeg = foodType == FoodType.VEG
    val color = if (isVeg) VegGreen else NonVegRed

    Box(
        modifier = modifier
            .size(16.dp)
            .border(1.5.dp, color, RoundedCornerShape(2.dp))
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )
    }
}

@Composable
fun FssaiBadge(licenseNo: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFFE8F5E9),
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "FSSAI Verified",
                tint = VegGreen,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "FSSAI Verified #$licenseNo",
                fontSize = 10.sp,
                color = Color(0xFF1B5E20),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RatingBadge(rating: Float, reviewCount: Int? = null) {
    Surface(
        color = VegGreen,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format("%.1f", rating),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(10.dp)
            )
            if (reviewCount != null) {
                Text(
                    text = " ($reviewCount)",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun UnitQuantityPicker(
    quantity: Double,
    unitType: UnitType,
    minQty: Double = 1.0,
    maxQty: Double = 50.0,
    step: Double = 0.5,
    onQuantityChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val unitLabel = when (unitType) {
        UnitType.KG -> "Kg"
        UnitType.DOZEN -> "Dozen"
        UnitType.LITRE -> "Litre"
        UnitType.PORTION -> "Portion"
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            IconButton(
                onClick = {
                    if (quantity - step >= minQty) {
                        onQuantityChange(quantity - step)
                    }
                },
                enabled = quantity > minQty,
                modifier = Modifier.size(28.dp).testTag("qty_minus")
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = if (quantity > minQty) SaffronPrimary else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "${if (quantity % 1.0 == 0.0) quantity.toInt() else String.format("%.1f", quantity)} $unitLabel",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            IconButton(
                onClick = {
                    if (quantity + step <= maxQty) {
                        onQuantityChange(quantity + step)
                    }
                },
                enabled = quantity < maxQty,
                modifier = Modifier.size(28.dp).testTag("qty_plus")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = if (quantity < maxQty) SaffronPrimary else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun LanguageDialog(
    currentLanguage: Language,
    onSelect: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select App Language / भाषा चुनें", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                LanguageOption("English (EN)", Language.ENGLISH, currentLanguage) { onSelect(Language.ENGLISH) }
                LanguageOption("हिंदी (Hindi)", Language.HINDI, currentLanguage) { onSelect(Language.HINDI) }
                LanguageOption("Hinglish (Mix)", Language.HINGLISH, currentLanguage) { onSelect(Language.HINGLISH) }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun LanguageOption(
    title: String,
    lang: Language,
    current: Language,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = current == lang, onClick = onSelect)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun NotificationFeedbackToast(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(message) {
        if (message != null) {
            kotlinx.coroutines.delay(2800)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        modifier = modifier
    ) {
        if (message != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onDismiss() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AmberSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = message,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
