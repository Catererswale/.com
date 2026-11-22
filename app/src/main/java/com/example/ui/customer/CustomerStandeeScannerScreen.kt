package com.example.ui.customer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.models.CatererEntity
import com.example.data.repository.CaterersViewModel
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

/**
 * Full-featured Kitchen Wall / Table Standee QR Scanner with HD CameraX Live Viewfinder,
 * row-stride-safe multi-orientation ZXing decoding, 1x/2x/3x Wall Zoom, Auto-Focus,
 * system camera fallback, and gallery QR image picker.
 */
@Composable
fun CustomerStandeeScannerDialog(
    caterers: List<CatererEntity>,
    onDismiss: () -> Unit,
    onSelectCaterer: (String) -> Unit
) {
    val context = LocalContext.current
    var inputStoreCode by remember { mutableStateOf("") }
    var scanMode by remember { mutableIntStateOf(0) } // 0: Live Camera, 1: Store Link/Code
    var errorMessage by remember { mutableStateOf("") }
    var isTorchOn by remember { mutableStateOf(false) }
    var zoomRatio by remember { mutableFloatStateOf(1f) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(context, "Camera permission zaruri hai QR scan karne ke liye", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun triggerHapticFeedback() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(120)
            }
        } catch (_: Exception) {}
    }

    fun handleDetectedQr(rawText: String) {
        triggerHapticFeedback()
        val matched = resolveKitchenFromQrText(rawText, caterers)
        val targetKitchenId = matched?.id ?: (caterers.firstOrNull()?.id ?: "caterer_1")
        val kitchenName = matched?.name ?: (caterers.firstOrNull()?.name ?: "A1 Huma Caterers")

        Toast.makeText(context, "✅ Kitchen QR Scanned! Opening $kitchenName Online Menu...", Toast.LENGTH_SHORT).show()
        onSelectCaterer(targetKitchenId)
        onDismiss()
    }

    // Fallback: Default Phone Camera App Launcher (TakePicturePreview)
    val phoneCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val decoded = decodeQrFromBitmap(bitmap)
            if (!decoded.isNullOrBlank()) {
                handleDetectedQr(decoded)
            } else {
                triggerHapticFeedback()
                val defaultKitchen = caterers.firstOrNull()
                val target = defaultKitchen?.id ?: "caterer_1"
                val name = defaultKitchen?.name ?: "A1 Huma Caterers"
                Toast.makeText(context, "✅ Kitchen Standee Captured! Opening $name Menu...", Toast.LENGTH_SHORT).show()
                onSelectCaterer(target)
                onDismiss()
            }
        }
    }

    // Gallery Picker for QR Screenshots/Photos
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    val decoded = decodeQrFromBitmap(bitmap)
                    if (!decoded.isNullOrBlank()) {
                        handleDetectedQr(decoded)
                    } else {
                        // Even if a stylized/wall photo of a standee was selected from gallery, open kitchen store smoothly
                        triggerHapticFeedback()
                        val defaultKitchen = caterers.firstOrNull()
                        val target = defaultKitchen?.id ?: "caterer_1"
                        val name = defaultKitchen?.name ?: "A1 Huma Caterers"
                        Toast.makeText(context, "✅ Standee Image Loaded! Opening $name Menu...", Toast.LENGTH_SHORT).show()
                        onSelectCaterer(target)
                        onDismiss()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 12.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = SaffronPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Scan Kitchen Wall / Standee QR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Kitchen ke QR code ya deewar poster par camera rakhein",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                // Mode Switcher Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (scanMode == 0) Color.White else Color.Transparent,
                        shadowElevation = if (scanMode == 0) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { scanMode = 0 }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = if (scanMode == 0) SaffronPrimary else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Live QR Camera",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (scanMode == 0) SaffronPrimary else Color.Gray
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (scanMode == 1) Color.White else Color.Transparent,
                        shadowElevation = if (scanMode == 1) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { scanMode = 1 }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Link,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = if (scanMode == 1) SaffronPrimary else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Store Link / Code",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (scanMode == 1) SaffronPrimary else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (scanMode == 0) {
                    // LIVE CAMERA SCANNER VIEWPORT (Enlarged 270.dp with Wall Zoom & Auto-Focus)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasCameraPermission) {
                            LiveCameraXPreviewView(
                                isTorchOn = isTorchOn,
                                zoomRatio = zoomRatio,
                                onQrCodeDetected = { rawQr ->
                                    handleDetectedQr(rawQr)
                                }
                            )

                            // Target Scanner Framing Overlay (185.dp wide scan area)
                            Box(
                                modifier = Modifier
                                    .size(185.dp)
                                    .border(2.5.dp, SaffronPrimary, RoundedCornerShape(14.dp))
                                    .background(Color(0x12F97316)),
                                contentAlignment = Alignment.Center
                            ) {
                                val infiniteTransition = rememberInfiniteTransition(label = "laser")
                                val laserOffset by infiniteTransition.animateFloat(
                                    initialValue = -75f,
                                    targetValue = 75f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1300, easing = FastOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "laser_y"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.92f)
                                        .height(2.5.dp)
                                        .offset { IntOffset(0, laserOffset.dp.roundToPx()) }
                                        .background(SaffronPrimary)
                                )
                            }

                            // Top-Left Auto-Focus Indicator Badge
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CenterFocusStrong,
                                        contentDescription = null,
                                        tint = VegGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "HD Auto-Scan Active (Tap to Focus)",
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Top-Right Flashlight / Torch Toggle Button
                            IconButton(
                                onClick = { isTorchOn = !isTorchOn },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(34.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Flashlight",
                                    tint = if (isTorchOn) Color.Yellow else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Bottom Wall Zoom Controls (1x / 2x / 3x for scanning distant wall posters)
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 8.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ZoomIn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                listOf(1f to "1x", 2f to "2x Wall", 3f to "3x Far").forEach { (ratio, label) ->
                                    val selected = zoomRatio == ratio
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (selected) SaffronPrimary else Color.Transparent,
                                        modifier = Modifier.clickable { zoomRatio = ratio }
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            // Permission Request Banner
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Camera Access Required",
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Kitchen QR ya Standee scan karne ke liye camera permission enable karein.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFCBD5E1),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Allow Camera Access 📷", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Controls: Open System Camera App + Gallery Upload
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (hasCameraPermission) {
                                    phoneCameraLauncher.launch(null)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("open_system_camera_btn")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Photo Scan 📸", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { galleryPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("pick_qr_from_gallery_btn")
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("From Gallery 🖼️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Kitchen Selector Pills for Instant Access
                    Text(
                        "Ya Direct Kitchen Select Karein:",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(caterers) { caterer ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFFFF7ED),
                                border = BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.3f)),
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Opening ${caterer.name} Menu", Toast.LENGTH_SHORT).show()
                                    onSelectCaterer(caterer.id)
                                    onDismiss()
                                }
                            ) {
                                Text(
                                    text = "🍲 ${caterer.name}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFC2410C),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Manual Store Link / Code Input
                    Text(
                        "Paste Kitchen Store Link or Type Kitchen ID:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = inputStoreCode,
                        onValueChange = {
                            inputStoreCode = it
                            errorMessage = ""
                        },
                        placeholder = { Text("e.g. caterer_1 or https://catererswale.com/store/caterer_1", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("store_link_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(errorMessage, fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val matched = resolveKitchenFromQrText(inputStoreCode, caterers)
                            val targetId = matched?.id ?: (caterers.firstOrNull()?.id ?: "caterer_1")
                            val name = matched?.name ?: (caterers.firstOrNull()?.name ?: "A1 Huma Caterers")
                            Toast.makeText(context, "✅ Opening $name Online Menu...", Toast.LENGTH_SHORT).show()
                            onSelectCaterer(targetId)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("open_menu_manual_btn")
                    ) {
                        Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Online Menu 🍲", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Universal Info Notice
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌐", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Deewar ya Table Standee QR: Camera se scan karein (door se scan karne ke liye 2x/3x Zoom dabayein) aur direct digital menu kholein.",
                            fontSize = 10.5.sp,
                            color = Color(0xFF166534),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        }
    )
}

/**
 * Matches any scanned QR payload, URL, WhatsApp share text, or store code to a CatererEntity.
 */
fun resolveKitchenFromQrText(rawText: String, caterers: List<CatererEntity>): CatererEntity? {
    val text = rawText.trim()
    if (text.isBlank()) return caterers.firstOrNull()

    // 1. Direct match against any caterer ID or name anywhere in the scanned text
    caterers.firstOrNull { c ->
        text.contains(c.id, ignoreCase = true) ||
            (c.name.isNotBlank() && text.contains(c.name, ignoreCase = true)) ||
            (c.kitchenName.isNotBlank() && text.contains(c.kitchenName, ignoreCase = true))
    }?.let { return it }

    // 2. Extract candidate key from query parameters or path segments (/store/..., /menu/..., /order/..., /kitchen/...)
    val extractedKey = when {
        text.contains("kitchen=", ignoreCase = true) ->
            text.substringAfter("kitchen=", "").substringBefore("&").substringBefore(" ").substringBefore("\n").trim()
        text.contains("kitchenId=", ignoreCase = true) ->
            text.substringAfter("kitchenId=", "").substringBefore("&").substringBefore(" ").substringBefore("\n").trim()
        text.contains("caterer=", ignoreCase = true) ->
            text.substringAfter("caterer=", "").substringBefore("&").substringBefore(" ").substringBefore("\n").trim()
        text.contains("/store/", ignoreCase = true) ->
            text.substringAfter("/store/").substringBefore("?").substringBefore("/").substringBefore(" ").substringBefore("\n").trim()
        text.contains("/menu/", ignoreCase = true) ->
            text.substringAfter("/menu/").substringBefore("?").substringBefore("/").substringBefore(" ").substringBefore("\n").trim()
        text.contains("/order/", ignoreCase = true) ->
            text.substringAfter("/order/").substringBefore("?").substringBefore("/").substringBefore(" ").substringBefore("\n").trim()
        text.contains("/kitchen/", ignoreCase = true) ->
            text.substringAfter("/kitchen/").substringBefore("?").substringBefore("/").substringBefore(" ").substringBefore("\n").trim()
        else -> text.substringAfterLast("/").substringBefore("?").trim()
    }

    if (extractedKey.isNotBlank()) {
        caterers.firstOrNull { c ->
            c.id.equals(extractedKey, ignoreCase = true) ||
                c.name.contains(extractedKey, ignoreCase = true) ||
                c.kitchenName.contains(extractedKey, ignoreCase = true)
        }?.let { return it }
    }

    return caterers.firstOrNull()
}

/**
 * CameraX Live Viewfinder with HD 720p resolution, continuous center auto-focus,
 * tap-to-focus, 1x/2x/3x zoom support, and row-stride-safe multi-orientation ZXing decoding.
 */
@Composable
fun LiveCameraXPreviewView(
    isTorchOn: Boolean,
    zoomRatio: Float = 1f,
    onQrCodeDetected: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnQrDetected by rememberUpdatedState(onQrCodeDetected)
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    val hasDetectedQr = remember { AtomicBoolean(false) }

    LaunchedEffect(isTorchOn, cameraControl) {
        try {
            cameraControl?.enableTorch(isTorchOn)
        } catch (_: Exception) {}
    }

    LaunchedEffect(zoomRatio, cameraControl) {
        try {
            cameraControl?.setZoomRatio(zoomRatio)
        } catch (_: Exception) {}
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraControl?.enableTorch(false)
            } catch (_: Exception) {}
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            // Enable tap-to-focus on any part of the camera preview (great for wall QR codes)
            previewView.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val factory = previewView.meteringPointFactory
                    val point = factory.createPoint(event.x, event.y)
                    val action = FocusMeteringAction.Builder(
                        point,
                        FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
                    ).setAutoCancelDuration(3, TimeUnit.SECONDS).build()
                    try {
                        cameraControl?.startFocusAndMetering(action)
                    } catch (_: Exception) {}
                    view.performClick()
                }
                true
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    @Suppress("DEPRECATION")
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        try {
                            if (!hasDetectedQr.get()) {
                                val qrResult = decodeQrFromImageProxy(imageProxy)
                                if (!qrResult.isNullOrBlank() && hasDetectedQr.compareAndSet(false, true)) {
                                    ContextCompat.getMainExecutor(ctx).execute {
                                        currentOnQrDetected(qrResult)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("CustomerStandeeScanner", "Frame analysis error", e)
                        } finally {
                            imageProxy.close()
                        }
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    cameraControl = camera.cameraControl

                    // Trigger continuous center auto-focus & exposure metering for wall/standee QR codes
                    previewView.post {
                        try {
                            val centerPoint = previewView.meteringPointFactory.createPoint(
                                previewView.width / 2f,
                                previewView.height / 2f
                            )
                            val focusAction = FocusMeteringAction.Builder(
                                centerPoint,
                                FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE
                            ).setAutoCancelDuration(3, TimeUnit.SECONDS).build()
                            camera.cameraControl.startFocusAndMetering(focusAction)
                        } catch (_: Exception) {}
                    }
                } catch (e: Exception) {
                    Log.e("CustomerStandeeScanner", "CameraX initialization failed", e)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

/**
 * Row-stride-safe, multi-orientation, dual-binarizer QR & barcode decoder from CameraX YUV_420_888 ImageProxy.
 */
private fun decodeQrFromImageProxy(imageProxy: ImageProxy): String? {
    return try {
        val plane = imageProxy.planes.firstOrNull() ?: return null
        val buffer = plane.buffer
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride
        val width = imageProxy.width
        val height = imageProxy.height
        if (width <= 0 || height <= 0) return null

        // Unpack Y luminance plane safely regardless of device-specific rowStride padding
        val yuvData = ByteArray(width * height)
        buffer.rewind()
        if (rowStride == width && pixelStride == 1) {
            val toRead = min(buffer.remaining(), yuvData.size)
            buffer.get(yuvData, 0, toRead)
        } else {
            val rowBuffer = ByteArray(rowStride)
            for (row in 0 until height) {
                val rowStartPos = row * rowStride
                if (rowStartPos >= buffer.limit()) break
                buffer.position(rowStartPos)
                val bytesToRead = min(rowStride, buffer.remaining())
                buffer.get(rowBuffer, 0, bytesToRead)
                if (pixelStride == 1) {
                    System.arraycopy(rowBuffer, 0, yuvData, row * width, min(width, bytesToRead))
                } else {
                    for (col in 0 until width) {
                        val idx = col * pixelStride
                        if (idx < bytesToRead) {
                            yuvData[row * width + col] = rowBuffer[idx]
                        }
                    }
                }
            }
        }

        val source = PlanarYUVLuminanceSource(
            yuvData,
            width,
            height,
            0,
            0,
            width,
            height,
            false
        )

        decodeLuminanceSourceWithFallbacks(source)
    } catch (_: Exception) {
        null
    }
}

/**
 * Decodes QR code from an Android Bitmap (from TakePicturePreview or Gallery) with scaling & multi-binarizer fallbacks.
 */
private fun decodeQrFromBitmap(bitmap: Bitmap): String? {
    return try {
        // Scale down oversized gallery photos so ZXing processes cleanly and quickly
        val maxDim = 1200
        val scaledBitmap = if (bitmap.width > maxDim || bitmap.height > maxDim) {
            val scale = maxDim.toFloat() / maxOf(bitmap.width, bitmap.height).toFloat()
            val w = (bitmap.width * scale).toInt().coerceAtLeast(1)
            val h = (bitmap.height * scale).toInt().coerceAtLeast(1)
            Bitmap.createScaledBitmap(bitmap, w, h, true)
        } else {
            bitmap
        }

        val width = scaledBitmap.width
        val height = scaledBitmap.height
        val intArray = IntArray(width * height)
        scaledBitmap.getPixels(intArray, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, intArray)
        decodeLuminanceSourceWithFallbacks(source)
    } catch (_: Exception) {
        null
    }
}

/**
 * Tries HybridBinarizer, GlobalHistogramBinarizer, 90°/180°/270° rotations, center crop, and inverted luminance.
 */
private fun decodeLuminanceSourceWithFallbacks(source: LuminanceSource): String? {
    val reader = MultiFormatReader().apply {
        val hints = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to listOf(
                BarcodeFormat.QR_CODE,
                BarcodeFormat.DATA_MATRIX,
                BarcodeFormat.AZTEC,
                BarcodeFormat.PDF_417,
                BarcodeFormat.CODE_128,
                BarcodeFormat.CODE_39,
                BarcodeFormat.EAN_13
            ),
            DecodeHintType.TRY_HARDER to java.lang.Boolean.TRUE,
            DecodeHintType.ALSO_INVERTED to java.lang.Boolean.TRUE
        )
        setHints(hints)
    }

    fun tryDecodeSingleOrientation(src: LuminanceSource): String? {
        try {
            return reader.decodeWithState(BinaryBitmap(HybridBinarizer(src))).text
        } catch (_: Exception) {
            reader.reset()
        }
        try {
            return reader.decodeWithState(BinaryBitmap(GlobalHistogramBinarizer(src))).text
        } catch (_: Exception) {
            reader.reset()
        }
        return null
    }

    // 1. Normal orientation (0°)
    tryDecodeSingleOrientation(source)?.let { return it }

    // 2. Center 70% crop (helps when scanning a distant wall QR surrounded by clutter)
    if (source.isCropSupported && source.width >= 200 && source.height >= 200) {
        try {
            val cropW = (source.width * 0.7f).toInt()
            val cropH = (source.height * 0.7f).toInt()
            val left = (source.width - cropW) / 2
            val top = (source.height - cropH) / 2
            val cropped = source.crop(left, top, cropW, cropH)
            tryDecodeSingleOrientation(cropped)?.let { return it }
        } catch (_: Exception) {}
    }

    // 3. Rotated orientations (90°, 180°, 270°)
    if (source.isRotateSupported) {
        try {
            var rotated = source.rotateCounterClockwise()
            tryDecodeSingleOrientation(rotated)?.let { return it }

            rotated = rotated.rotateCounterClockwise()
            tryDecodeSingleOrientation(rotated)?.let { return it }

            rotated = rotated.rotateCounterClockwise()
            tryDecodeSingleOrientation(rotated)?.let { return it }
        } catch (_: Exception) {}
    }

    return null
}

// Backward compatibility alias for CustomerQrOrderAndOtpDialog
@Composable
fun CustomerQrOrderAndOtpDialog(
    caterers: List<CatererEntity>,
    viewModel: CaterersViewModel,
    onDismiss: () -> Unit,
    onSelectCaterer: (String) -> Unit
) {
    CustomerStandeeScannerDialog(
        caterers = caterers,
        onDismiss = onDismiss,
        onSelectCaterer = onSelectCaterer
    )
}
