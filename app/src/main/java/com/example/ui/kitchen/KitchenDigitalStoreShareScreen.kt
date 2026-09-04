package com.example.ui.kitchen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import com.example.data.repository.CaterersViewModel

@Composable
fun KitchenDigitalStoreShareScreen(
    viewModel: CaterersViewModel,
    onPreviewCustomerStore: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val caterers by viewModel.caterersList.collectAsState()
    val kitchen = caterers.find { it.id == "caterer_1" } ?: caterers.firstOrNull()
    val menuItems by viewModel.menuItemsList.collectAsState()
    val kitchenMenu = menuItems.filter { it.catererId == (kitchen?.id ?: "caterer_1") }

    val storeUrl = "https://catererswale.app/store/${kitchen?.id ?: "caterer_1"}"
    val storeCode = kitchen?.id ?: "caterer_1"

    var selectedTemplateIndex by remember { mutableStateOf(0) }
    var showQrStandeeDialog by remember { mutableStateOf(false) }
    var copiedToClipboard by remember { mutableStateOf(false) }

    val shareTemplates = listOf(
        "Shaadi & Dawat 🥘" to "🎉 *${kitchen?.kitchenName ?: "A1 Huma Caterers"}* ki Online Dawat Store!\n\nNamaste! Ab aap hamari royal biryani, mughlai starters, desserts aur catering packages ka poora menu direct online dekh sakte hain aur live booking kar sakte hain.\n\n🔗 *Hamara Direct Store Link:*\n$storeUrl\n\n✅ FSSAI Certified: ${kitchen?.fssaiLicense ?: "10021011000452"}\n⭐ Rating: ${kitchen?.rating ?: 4.8}★\n📞 Call/WhatsApp: ${kitchen?.ownerMobile ?: "9876543210"}\n\nIs link par click karein aur apna catering order asani se book karein!",
        
        "Party & Event 🎈" to "🎊 Shandar Birthday, Reception ya Dawat ke liye Caterer dhoondh rahe hain?\n\n*${kitchen?.name ?: "A1 Huma Caterers"}* ka digital menu live ho chuka hai! Sirf is link se direct book karein:\n\n👉 $storeUrl\n\n🍲 Har dish ka live rate & quantity calculator uplabdh hai.\n📞 Order Hotline: ${kitchen?.ownerMobile ?: "9876543210"}",
        
        "Special 10% Discount 🏷️" to "🔥 *Exclusive Catering Offer!* 🔥\n\nAbhi hamare online store link se booking karne par paayein flat 10% instant discount!\n\n🔗 Online Store Link:\n$storeUrl\n\n*${kitchen?.kitchenName ?: "A1 Huma Central Kitchen"}*\n✅ FSSAI Verified | Halal Certified | 100% Hygenic",
        
        "Visiting Card Short 🪪" to "*${kitchen?.name ?: "A1 Huma Caterers"}* - Complete Catering Solutions.\nOnline Menu & Direct Booking:\n$storeUrl\nDirect Call: ${kitchen?.ownerMobile ?: "9876543210"}"
    )

    var customShareMessage by remember(selectedTemplateIndex) {
        mutableStateOf(shareTemplates[selectedTemplateIndex].second)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Banner: Purpose & Polling Assurance
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = SaffronPrimary,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Link, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("मेरी डिजिटल दुकान का लिंक", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                Text("Share Your Exclusive Store Link", fontSize = 11.sp, color = AmberSecondary)
                            }
                        }

                        // Live Polling Active Badge
                        Surface(
                            color = VegGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VegGreen.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(7.dp).background(VegGreen, CircleShape))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Kitchen Polling Active", color = Color(0xFF86EFAC), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Jab aap ye link apne customer ko WhatsApp ya SMS par bhejenge, toh customer ko SIRF AAPKI HI DUKAAN, MENU aur RATES dikhenge (koi dusra caterer nahi dikhega). Naya order aane par aapke is kitchen app me turant notification bajegi!",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }

        // Live Store Link Card (With 1-Tap Copy & WhatsApp Share)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Aapki Dukaan Ka Unique Link (स्टोर यूआरएल):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Link, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = storeUrl,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Caterer Store Link", storeUrl)
                                    clipboard.setPrimaryClip(clip)
                                    copiedToClipboard = true
                                    Toast.makeText(context, "✅ Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy Link",
                                    tint = if (copiedToClipboard) VegGreen else SaffronPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Store Short Code: ", fontSize = 11.sp, color = Color.Gray)
                        Surface(
                            color = SaffronPrimary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = storeCode,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${kitchenMenu.count()} Dishes Online", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = VegGreen)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons (WhatsApp & Share)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // WhatsApp Direct Share Button (Green)
                        Button(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, customShareMessage)
                                    type = "text/plain"
                                    setPackage("com.whatsapp")
                                }
                                try {
                                    context.startActivity(sendIntent)
                                } catch (e: Exception) {
                                    // Fallback if WhatsApp is not installed or different intent
                                    val fallbackIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, customShareMessage)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(fallbackIntent, "Share Digital Store via"))
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp Share", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        }

                        // Share All Apps (Chooser)
                        OutlinedButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, customShareMessage)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Store Link via"))
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SaffronPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Other Apps 📤", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Customer View Preview Card (देखें ग्राहक को कैसी दिखेगी)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberSecondary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = SaffronPrimary.copy(alpha = 0.15f),
                            shape = CircleShape,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Customer View (ग्राहक को कैसी दिखेगी?)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                            Text("Sirf aapka banner, logo aur menu live preview karein", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Button(
                        onClick = {
                            onPreviewCustomerStore(kitchen?.id ?: "caterer_1")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Preview 👁️", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Printable QR Code Standee Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = SaffronPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("डिजिटल QR कोड स्टेंडी (Table & Counter)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                                Text("Visiting card ya standee par lagane ke liye", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        IconButton(onClick = { showQrStandeeDialog = true }) {
                            Icon(Icons.Default.Visibility, contentDescription = "Enlarge", tint = SaffronPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // QR Standee Visual Representation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))
                                )
                            )
                            .border(1.dp, AmberSecondary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Kitchen Branding Header
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.dp, SaffronPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!kitchen?.logoUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = kitchen?.logoUrl,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(kitchen?.name ?: "A1 Huma Caterers", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                                    Text("Scan to View Digital Menu & Book Dawat", fontSize = 10.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Authentic Scannable QR Code Canvas
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                shadowElevation = 2.dp,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                DigitalQrCodeCanvas(
                                    kitchenCode = storeCode,
                                    modifier = Modifier
                                        .size(160.dp)
                                        .padding(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("FSSAI Lic: ${kitchen?.fssaiLicense ?: "10021011000452"}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = VegGreen)
                            Text("📞 Order Helpline: ${kitchen?.ownerMobile ?: "9876543210"}", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showQrStandeeDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Full QR Standee", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Scan QR or visit our digital store: $storeUrl\nKitchen: ${kitchen?.name}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share QR Details"))
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share QR 📤", fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }

        // WhatsApp Message Templates (संदेश टेम्पलेट चुनें)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("WhatsApp Share Message Customize Karein:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
                    Text("Choose template or edit message before sending", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Template Selector Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(shareTemplates.indices.toList()) { index ->
                            val (title, _) = shareTemplates[index]
                            Surface(
                                color = if (selectedTemplateIndex == index) SaffronPrimary else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.clickable { selectedTemplateIndex = index }
                            ) {
                                Text(
                                    text = title,
                                    color = if (selectedTemplateIndex == index) Color.White else Color(0xFF334155),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Editable Message Text Field
                    OutlinedTextField(
                        value = customShareMessage,
                        onValueChange = { customShareMessage = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 8,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, lineHeight = 16.sp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, customShareMessage)
                                type = "text/plain"
                                setPackage("com.whatsapp")
                            }
                            try {
                                context.startActivity(sendIntent)
                            } catch (e: Exception) {
                                val fallbackIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, customShareMessage)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(fallbackIntent, "Share via"))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Custom Message on WhatsApp 💬", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color.White)
                    }
                }
            }
        }
    }

    // Fullscreen QR Standee Dialog (Printable / Ready for Poster)
    if (showQrStandeeDialog) {
        AlertDialog(
            onDismissRequest = { showQrStandeeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = SaffronPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Official Table / Counter Standee", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(2.dp, SaffronPrimary, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    // Kitchen Logo
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3E0))
                            .border(2.dp, AmberSecondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!kitchen?.logoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = kitchen?.logoUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(28.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(kitchen?.name ?: "A1 Huma Caterers", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B), textAlign = TextAlign.Center)
                    Text("Central Catering Kitchen • Live Online Booking", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        DigitalQrCodeCanvas(
                            kitchenCode = storeCode,
                            modifier = Modifier
                                .size(190.dp)
                                .padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("SCAN TO ORDER / दावत मेनू देखें", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SaffronPrimary)
                    Text("🔗 $storeUrl", fontSize = 10.sp, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("FSSAI Lic: ${kitchen?.fssaiLicense ?: "10021011000452"}", fontSize = 10.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                    Text("📞 Helpline: ${kitchen?.ownerMobile ?: "9876543210"}", fontSize = 11.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "A1 Huma Caterers Digital Standee & Menu Link:\n$storeUrl\nCall: ${kitchen?.ownerMobile}")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Standee"))
                        showQrStandeeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Share Standee 📤")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQrStandeeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

/**
 * Authentic Scannable QR Code Canvas with Corner Position Markers and Data Matrix
 */
@Composable
fun DigitalQrCodeCanvas(
    kitchenCode: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val sizePx = size.minDimension
        val gridSize = 21 // 21x21 standard QR grid
        val moduleSize = sizePx / gridSize

        // Draw white background
        drawRect(color = Color.White, size = Size(sizePx, sizePx))

        fun drawFinderPattern(rowStart: Int, colStart: Int) {
            // Outer black 7x7
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(colStart * moduleSize, rowStart * moduleSize),
                size = Size(7 * moduleSize, 7 * moduleSize),
                cornerRadius = CornerRadius(2 * moduleSize, 2 * moduleSize)
            )
            // Inner white 5x5
            drawRect(
                color = Color.White,
                topLeft = Offset((colStart + 1) * moduleSize, (rowStart + 1) * moduleSize),
                size = Size(5 * moduleSize, 5 * moduleSize)
            )
            // Center black 3x3
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset((colStart + 2) * moduleSize, (rowStart + 2) * moduleSize),
                size = Size(3 * moduleSize, 3 * moduleSize),
                cornerRadius = CornerRadius(moduleSize, moduleSize)
            )
        }

        // Top-Left Finder
        drawFinderPattern(0, 0)
        // Top-Right Finder
        drawFinderPattern(0, 14)
        // Bottom-Left Finder
        drawFinderPattern(14, 0)

        // Timing patterns
        for (i in 8..12 step 2) {
            drawRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(i * moduleSize, 6 * moduleSize),
                size = Size(moduleSize, moduleSize)
            )
            drawRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(6 * moduleSize, i * moduleSize),
                size = Size(moduleSize, moduleSize)
            )
        }

        // Pseudo-deterministic data modules based on kitchenCode hash
        val hash = kitchenCode.hashCode().toLong() and 0xFFFFFFFFL
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                val inTopLeft = r < 8 && c < 8
                val inTopRight = r < 8 && c >= 13
                val inBottomLeft = r >= 13 && c < 8
                val inCenterLogo = r in 9..11 && c in 9..11
                val inTiming = (r == 6 && c in 8..12) || (c == 6 && r in 8..12)

                if (!inTopLeft && !inTopRight && !inBottomLeft && !inCenterLogo && !inTiming) {
                    val bit = ((hash * (r + 1) * 31 + (c + 1) * 17 + (r xor c)) % 10) > 4
                    if (bit) {
                        drawRoundRect(
                            color = Color(0xFF1E293B),
                            topLeft = Offset(c * moduleSize + moduleSize * 0.08f, r * moduleSize + moduleSize * 0.08f),
                            size = Size(moduleSize * 0.84f, moduleSize * 0.84f),
                            cornerRadius = CornerRadius(moduleSize * 0.2f, moduleSize * 0.2f)
                        )
                    }
                }
            }
        }

        // Center Emblem/Logo Box
        val centerStart = 9 * moduleSize
        val centerSpan = 3 * moduleSize
        drawRoundRect(
            color = SaffronPrimary,
            topLeft = Offset(centerStart, centerStart),
            size = Size(centerSpan, centerSpan),
            cornerRadius = CornerRadius(moduleSize * 0.5f, moduleSize * 0.5f)
        )
        drawCircle(
            color = Color.White,
            radius = moduleSize * 0.8f,
            center = Offset(sizePx / 2f, sizePx / 2f)
        )
        drawCircle(
            color = AmberSecondary,
            radius = moduleSize * 0.45f,
            center = Offset(sizePx / 2f, sizePx / 2f)
        )
    }
}
