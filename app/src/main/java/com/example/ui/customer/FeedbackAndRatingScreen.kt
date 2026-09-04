package com.example.ui.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Liquor
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CatererEntity
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.models.PartnerReviewEntity
import com.example.data.repository.CaterersViewModel
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VegGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FeedbackAndRatingScreen(
    orderId: String?,
    catererId: String?,
    viewModel: CaterersViewModel,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.ordersList.collectAsState()
    val caterers by viewModel.caterersList.collectAsState()
    val allReviews by viewModel.allPartnerReviews.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Find the target order & target caterer
    val deliveredOrders = orders.filter { it.orderStatus == OrderStatus.DELIVERED }
    var selectedOrder by remember(orderId, orders) {
        mutableStateOf(
            orders.find { it.orderId == orderId }
                ?: deliveredOrders.firstOrNull()
                ?: orders.firstOrNull()
        )
    }

    val targetCatererId = selectedOrder?.catererId ?: catererId ?: "caterer_1"
    val caterer = caterers.find { it.id == targetCatererId } ?: caterers.firstOrNull()
    val catererReviews = allReviews.filter { it.catererId == targetCatererId }

    // Screen Tabs: 0 = "Rate Partner", 1 = "Partner Reviews Wall"
    var selectedTab by remember { mutableIntStateOf(0) }

    // Rating State
    var overallRating by remember { mutableFloatStateOf(5.0f) }
    var tasteRating by remember { mutableFloatStateOf(5.0f) }
    var portionRating by remember { mutableFloatStateOf(5.0f) }
    var packagingRating by remember { mutableFloatStateOf(5.0f) }
    var deliveryRating by remember { mutableFloatStateOf(5.0f) }
    var bartanRating by remember { mutableFloatStateOf(5.0f) }

    // Tag selections
    val selectedTags = remember { mutableStateListOf<String>() }

    // Dish level feedback
    val dishNames = remember(selectedOrder) {
        val summary = selectedOrder?.itemsSummary ?: "Special Dum Biryani"
        summary.split(",").map { it.substringBefore("(").trim() }.filter { it.isNotEmpty() }
    }
    val likedDishes = remember { mutableStateListOf<String>().apply { addAll(dishNames) } }

    // Event Type & Feedback text
    var selectedEventType by remember { mutableStateOf("Family Feast / Gathering") }
    var reviewComment by remember { mutableStateOf("") }
    var isRecommended by remember { mutableStateOf(true) }
    var selectedTipAmount by remember { mutableFloatStateOf(50.0f) }
    var isAnonymous by remember { mutableStateOf(false) }
    var attachedPhotosCount by remember { mutableIntStateOf(2) }

    // Submission Success Dialog State
    var showCelebrationDialog by remember { mutableStateOf(false) }
    var submittedReviewId by remember { mutableStateOf("") }

    // Preset available tags
    val positiveTags = listOf(
        "🔥 Authentic Dum", "🍗 Melt-in-Mouth Meat", "🍲 Generous Portion",
        "✨ Pure Zafrani Aroma", "⏱️ Punctual Delivery", "👨‍🍳 Polite Valet",
        "🧂 Balanced Spices", "📦 Hot Sealed Degs", "🥘 Clean Utensils", "🌟 Royal Presentation"
    )
    val constructiveTags = listOf(
        "🧂 Needs Less Salt", "🌶️ Overly Spicy", "❄️ Arrived Lukewarm",
        "⌛ Slight Delay", "📦 Loose Deg Seal", "🥘 More Salan Needed"
    )
    val eventTypes = listOf(
        "Family Feast / Gathering", "Wedding / Nikah", "Birthday Celebration",
        "House Dawat", "Corporate Feast", "Festive Gathering"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Kitchen Partner Feedback",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = caterer?.name ?: "A1 Huma Caterers",
                            fontSize = 12.sp,
                            color = AmberSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("feedback_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Kitchen Partner & Order Context Header Card
            item {
                KitchenPartnerHeaderCard(
                    caterer = caterer,
                    order = selectedOrder,
                    deliveredOrders = deliveredOrders,
                    onSelectOrder = { selectedOrder = it }
                )
            }

            // 2. Navigation Tabs (Rate Experience vs. Community Reviews)
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = SaffronPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = SaffronPrimary,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rate Experience", fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_rate_experience")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reviews (${catererReviews.size})", fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_community_reviews")
                    )
                }
            }

            // Tab 0: Rating Form
            if (selectedTab == 0) {
                // Overall Interactive Rating Scorecard
                item {
                    OverallRatingScoreCard(
                        rating = overallRating,
                        onRatingChanged = { overallRating = it }
                    )
                }

                // Multi-Criteria Quality Dimensions
                item {
                    MultiCriteriaDimensionsCard(
                        tasteRating = tasteRating,
                        onTasteChanged = { tasteRating = it },
                        portionRating = portionRating,
                        onPortionChanged = { portionRating = it },
                        packagingRating = packagingRating,
                        onPackagingChanged = { packagingRating = it },
                        deliveryRating = deliveryRating,
                        onDeliveryChanged = { deliveryRating = it },
                        bartanRating = bartanRating,
                        onBartanChanged = { bartanRating = it }
                    )
                }

                // Tag Chips Cloud
                item {
                    ComplimentTagsCard(
                        overallRating = overallRating,
                        positiveTags = positiveTags,
                        constructiveTags = constructiveTags,
                        selectedTags = selectedTags,
                        onToggleTag = { tag ->
                            if (selectedTags.contains(tag)) selectedTags.remove(tag)
                            else selectedTags.add(tag)
                        }
                    )
                }

                // Per-Dish Feedback
                if (dishNames.isNotEmpty()) {
                    item {
                        DishFeedbackCard(
                            dishNames = dishNames,
                            likedDishes = likedDishes,
                            onToggleDish = { dish ->
                                if (likedDishes.contains(dish)) likedDishes.remove(dish)
                                else likedDishes.add(dish)
                            }
                        )
                    }
                }

                // Occasion & Written Review Card
                item {
                    WrittenReviewCard(
                        eventTypes = eventTypes,
                        selectedEventType = selectedEventType,
                        onSelectEventType = { selectedEventType = it },
                        reviewComment = reviewComment,
                        onReviewChange = { reviewComment = it },
                        isRecommended = isRecommended,
                        onToggleRecommend = { isRecommended = it },
                        isAnonymous = isAnonymous,
                        onToggleAnonymous = { isAnonymous = it },
                        attachedPhotosCount = attachedPhotosCount,
                        onAddPhoto = { attachedPhotosCount = (attachedPhotosCount + 1).coerceAtMost(5) },
                        onRemovePhoto = { attachedPhotosCount = (attachedPhotosCount - 1).coerceAtLeast(0) }
                    )
                }

                // Delivery Valet Tip Card
                item {
                    ValetTipCard(
                        deliveryBoyName = selectedOrder?.deliveryBoyName ?: "Ramesh Sharma",
                        selectedTip = selectedTipAmount,
                        onSelectTip = { selectedTipAmount = it }
                    )
                }

                // Submit Feedback Button
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Button(
                            onClick = {
                                val newReview = PartnerReviewEntity(
                                    id = "rev_${System.currentTimeMillis()}",
                                    orderId = selectedOrder?.orderId ?: "CW-${System.currentTimeMillis() % 100000}",
                                    catererId = targetCatererId,
                                    catererName = caterer?.name ?: "A1 Huma Caterers",
                                    customerName = if (isAnonymous) "Verified Foodie" else (currentUser?.name?.ifBlank { "Rohan Verma" } ?: "Rohan Verma"),
                                    customerMobile = currentUser?.mobile ?: "+91 98765 11223",
                                    overallRating = overallRating,
                                    tasteRating = tasteRating,
                                    portionRating = portionRating,
                                    packagingRating = packagingRating,
                                    deliveryRating = deliveryRating,
                                    bartanRating = bartanRating,
                                    tagsCsv = selectedTags.joinToString(","),
                                    eventType = selectedEventType,
                                    comment = reviewComment.ifBlank { "Everything was freshly cooked and deliciously delivered. Thank you!" },
                                    dishRatingsJson = dishNames.joinToString(", ") { "$it: ${if (likedDishes.contains(it)) "Loved ❤️" else "Good 👍"}" },
                                    isRecommended = isRecommended,
                                    tipAmount = selectedTipAmount.toDouble(),
                                    isAnonymous = isAnonymous,
                                    photosCount = attachedPhotosCount,
                                    createdAtTimestamp = System.currentTimeMillis()
                                )

                                submittedReviewId = newReview.id
                                viewModel.submitDetailedReview(newReview) {
                                    showCelebrationDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .shadow(6.dp, RoundedCornerShape(14.dp))
                                .testTag("submit_partner_feedback_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Celebration, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Submit Review & Earn 50 Points ⭐",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                // Tab 1: Community Reviews Wall
                item {
                    KitchenReviewMetricsSummary(
                        caterer = caterer,
                        reviews = catererReviews
                    )
                }

                if (catererReviews.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.QuestionAnswer,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No Reviews Yet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Be the first to review this kitchen partner!", fontSize = 13.sp, color = Color.Gray)
                            }
                        }
                    }
                } else {
                    items(catererReviews) { review ->
                        PublicReviewCard(review = review)
                    }
                }
            }
        }
    }

    // Celebration Reward Dialog
    if (showCelebrationDialog) {
        ReviewSuccessDialog(
            catererName = caterer?.name ?: "A1 Huma Caterers",
            rating = overallRating,
            onDismiss = {
                showCelebrationDialog = false
                onNavigateToHome()
            },
            onViewCommunityReviews = {
                showCelebrationDialog = false
                selectedTab = 1
            }
        )
    }
}

// -------------------------------------------------------------
// COMPONENT 1: Kitchen Partner & Order Context Header
// -------------------------------------------------------------
@Composable
private fun KitchenPartnerHeaderCard(
    caterer: CatererEntity?,
    order: OrderEntity?,
    deliveredOrders: List<OrderEntity>,
    onSelectOrder: (OrderEntity) -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                Brush.linearGradient(listOf(SaffronPrimary, AmberSecondary)),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = caterer?.name ?: "A1 Huma Caterers",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "📍 ${caterer?.address ?: "Okhla Phase 3, New Delhi"}",
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${caterer?.rating ?: 4.8}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order Selection Bar
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (deliveredOrders.size > 1) expandedDropdown = !expandedDropdown
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VegGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Order #${order?.orderId ?: "CW-89198"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Delivered: ${order?.deliveryDate ?: "2026-07-24"} (${order?.deliveryTimeSlot ?: "12:30 PM"})",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    if (deliveredOrders.size > 1) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Switch", fontSize = 11.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Dropdown selection if user has multiple orders
            if (expandedDropdown && deliveredOrders.size > 1) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "Select Delivered Order to Rate:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                    deliveredOrders.forEach { ord ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectOrder(ord)
                                    expandedDropdown = false
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("#${ord.orderId} - ${ord.itemsSummary.take(30)}...", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("Delivered on ${ord.deliveryDate}", fontSize = 10.sp, color = Color.Gray)
                            }
                            if (ord.orderId == order?.orderId) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 2: Overall Interactive Rating Scorecard
// -------------------------------------------------------------
@Composable
private fun OverallRatingScoreCard(
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    val sentiment = when {
        rating >= 5.0f -> SentimentData("🤩", "Sensational Dum Feast!", "Exceeded all culinary expectations & authentic aroma", Color(0xFF16A34A), Color(0xFFDCFCE7))
        rating >= 4.0f -> SentimentData("😋", "Delicious & Highly Satisfying!", "Great food taste, portion, and smooth delivery", Color(0xFF0284C7), Color(0xFFE0F2FE))
        rating >= 3.0f -> SentimentData("🙂", "Average Experience", "Decent meal, but room for spice & delivery improvement", Color(0xFFD97706), Color(0xFFFEF3C7))
        rating >= 2.0f -> SentimentData("😕", "Below Expectations", "Food or delivery did not meet standard expectations", Color(0xFFEA580C), Color(0xFFFFEDD5))
        else -> SentimentData("😞", "Disappointing Experience", "We apologize for the subpar catering experience", Color(0xFFDC2626), Color(0xFFFEE2E2))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HOW WAS YOUR CATERING EXPERIENCE?",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SaffronPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Emoji & Sentiment Badge
            Surface(
                color = sentiment.badgeBg,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(sentiment.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = sentiment.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = sentiment.badgeText
                        )
                        Text(
                            text = sentiment.subtitle,
                            fontSize = 10.5.sp,
                            color = sentiment.badgeText.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5 Interactive Big Gold Stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (star in 1..5) {
                    val isSelected = star <= rating
                    val starScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1.0f,
                        animationSpec = spring(),
                        label = "star_scale"
                    )

                    IconButton(
                        onClick = { onRatingChanged(star.toFloat()) },
                        modifier = Modifier
                            .scale(starScale)
                            .padding(horizontal = 4.dp)
                            .testTag("rating_star_$star")
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = "Star $star",
                            tint = if (isSelected) AmberSecondary else Color(0xFFCBD5E1),
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${rating.toInt()} / 5.0 Rating Selected",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

private data class SentimentData(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val badgeText: Color,
    val badgeBg: Color
)

// -------------------------------------------------------------
// COMPONENT 3: Multi-Criteria Quality Dimensions Card
// -------------------------------------------------------------
@Composable
private fun MultiCriteriaDimensionsCard(
    tasteRating: Float,
    onTasteChanged: (Float) -> Unit,
    portionRating: Float,
    onPortionChanged: (Float) -> Unit,
    packagingRating: Float,
    onPackagingChanged: (Float) -> Unit,
    deliveryRating: Float,
    onDeliveryChanged: (Float) -> Unit,
    bartanRating: Float,
    onBartanChanged: (Float) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RoomService, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Detailed Category Ratings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )
            }
            Text(
                text = "Rate individual parameters to help kitchen partner maintain peak quality",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            DimensionRatingRow("🍲 Food Taste & Dum Spices", "Aroma, rich spices & authentic tenderness", tasteRating, onTasteChanged, "taste")
            DimensionRatingRow("⚖️ Portion & Weight Accuracy", "Exact Kg / Pax served as booked", portionRating, onPortionChanged, "portion")
            DimensionRatingRow("📦 Packaging & Hot Deg Temp", "Degs tightly sealed & piping hot", packagingRating, onPackagingChanged, "packaging")
            DimensionRatingRow("🚴 Delivery Speed & Politeness", "Punctual arrival & courteous valet", deliveryRating, onDeliveryChanged, "delivery")
            DimensionRatingRow("🥘 Utensils & Handi Condition", "Clean, sturdy degs & serving spoons", bartanRating, onBartanChanged, "bartan")
        }
    }
}

@Composable
private fun DimensionRatingRow(
    title: String,
    subtitle: String,
    rating: Float,
    onRatingChange: (Float) -> Unit,
    tagPrefix: String
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Text(text = subtitle, fontSize = 10.sp, color = Color(0xFF94A3B8))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                for (s in 1..5) {
                    val filled = s <= rating
                    Icon(
                        imageVector = if (filled) Icons.Default.Star else Icons.Default.StarOutline,
                        contentDescription = "Star $s",
                        tint = if (filled) AmberSecondary else Color(0xFFE2E8F0),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onRatingChange(s.toFloat()) }
                            .padding(2.dp)
                            .testTag("${tagPrefix}_star_$s")
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { rating / 5.0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = if (rating >= 4.0f) VegGreen else AmberSecondary,
            trackColor = Color(0xFFF1F5F9)
        )
    }
}

// -------------------------------------------------------------
// COMPONENT 4: Compliment & Feedback Tag Chips
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ComplimentTagsCard(
    overallRating: Float,
    positiveTags: List<String>,
    constructiveTags: List<String>,
    selectedTags: List<String>,
    onToggleTag: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalOffer, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (overallRating >= 4.0f) "What did you love most?" else "Areas for Improvement",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )
            }
            Text(
                text = "Tap all badges that apply to your catering feast",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tagsToDisplay = if (overallRating >= 3.5f) positiveTags else constructiveTags + positiveTags.take(3)
                tagsToDisplay.forEach { tag ->
                    val isSelected = selectedTags.contains(tag)
                    Surface(
                        color = if (isSelected) SaffronPrimary else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (isSelected) SaffronPrimary else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .clickable { onToggleTag(tag) }
                            .testTag("tag_chip_${tag.replace(" ", "_")}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 5: Per-Dish Itemized Feedback Card
// -------------------------------------------------------------
@Composable
private fun DishFeedbackCard(
    dishNames: List<String>,
    likedDishes: List<String>,
    onToggleDish: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Fastfood, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Dish-by-Dish Rating",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )
            }
            Text(
                text = "Would you order these dishes again for future functions?",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(10.dp))

            dishNames.forEach { dish ->
                val isLiked = likedDishes.contains(dish)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dish,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        Surface(
                            color = if (isLiked) VegGreen else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable { if (!isLiked) onToggleDish(dish) }
                                .testTag("dish_like_${dish.take(5)}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ThumbUp, contentDescription = "Liked", tint = if (isLiked) Color.White else Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Loved", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isLiked) Color.White else Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Surface(
                            color = if (!isLiked) Color(0xFFEF4444) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clickable { if (isLiked) onToggleDish(dish) }
                                .testTag("dish_dislike_${dish.take(5)}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ThumbDown, contentDescription = "Disliked", tint = if (!isLiked) Color.White else Color.Gray, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 6: Written Review & Occasion Card
// -------------------------------------------------------------
@Composable
private fun WrittenReviewCard(
    eventTypes: List<String>,
    selectedEventType: String,
    onSelectEventType: (String) -> Unit,
    reviewComment: String,
    onReviewChange: (String) -> Unit,
    isRecommended: Boolean,
    onToggleRecommend: (Boolean) -> Unit,
    isAnonymous: Boolean,
    onToggleAnonymous: (Boolean) -> Unit,
    attachedPhotosCount: Int,
    onAddPhoto: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Event Type Selection
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Occasion / Event Type",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(eventTypes) { evt ->
                    FilterChip(
                        selected = evt == selectedEventType,
                        onClick = { onSelectEventType(evt) },
                        label = { Text(evt, fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("event_chip_${evt.take(5)}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Written Review Text
            Text(
                text = "Detailed Written Review",
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Quick Prompt Ideas
            val quickPrompts = listOf(
                "Aroma filled the whole room! 🍲",
                "Guests praised the soft mutton! 🍗",
                "Delivered piping hot on time! ⏱️",
                "Generous raita & salan portions! ✨"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(quickPrompts) { prompt ->
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.clickable {
                            val separator = if (reviewComment.isBlank()) "" else " "
                            onReviewChange(reviewComment + separator + prompt)
                        }
                    ) {
                        Text(
                            text = "+ $prompt",
                            fontSize = 10.5.sp,
                            color = Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = reviewComment,
                onValueChange = onReviewChange,
                placeholder = {
                    Text(
                        "Share details about food aroma, rice quality, guest feedback, and overall satisfaction...",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("written_review_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronPrimary,
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Photo Attachment Simulation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Feast Photos ($attachedPhotosCount Attached)", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }

                Row {
                    if (attachedPhotosCount > 0) {
                        TextButton(onClick = onRemovePhoto) {
                            Text("Remove", fontSize = 11.sp, color = Color.Red)
                        }
                    }
                    Button(
                        onClick = onAddPhoto,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_photo_btn")
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Photo", fontSize = 11.sp, color = SaffronPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recommend Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Recommend this Kitchen Partner?", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Helps others discover verified caterers", fontSize = 10.5.sp, color = Color.Gray)
                }
                Switch(
                    checked = isRecommended,
                    onCheckedChange = onToggleRecommend,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = VegGreen
                    ),
                    modifier = Modifier.testTag("recommend_switch")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Anonymous Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Post Anonymously?", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Displays as 'Verified Foodie' publicly", fontSize = 10.5.sp, color = Color.Gray)
                }
                Switch(
                    checked = isAnonymous,
                    onCheckedChange = onToggleAnonymous,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SaffronPrimary
                    ),
                    modifier = Modifier.testTag("anonymous_switch")
                )
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 7: Delivery Valet Tip Card
// -------------------------------------------------------------
@Composable
private fun ValetTipCard(
    deliveryBoyName: String,
    selectedTip: Float,
    onSelectTip: (Float) -> Unit
) {
    val tipAmounts = listOf(0.0f, 30.0f, 50.0f, 100.0f, 200.0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFE0F2FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Tip Delivery Valet ($deliveryBoyName)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("100% of tip goes directly to the driver", fontSize = 10.5.sp, color = Color.Gray)
                    }
                }

                if (selectedTip > 0) {
                    Text("₹${selectedTip.toInt()}", fontWeight = FontWeight.Bold, color = VegGreen, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tipAmounts.forEach { amount ->
                    val isSelected = selectedTip == amount
                    Surface(
                        color = if (isSelected) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isSelected) VegGreen else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectTip(amount) }
                            .testTag("tip_btn_${amount.toInt()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (amount == 0f) "None" else "₹${amount.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) Color(0xFF166534) else Color(0xFF334155)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 8: Community Reviews Wall Summary
// -------------------------------------------------------------
@Composable
private fun KitchenReviewMetricsSummary(
    caterer: CatererEntity?,
    reviews: List<PartnerReviewEntity>
) {
    val totalCount = reviews.size
    val fiveStars = reviews.count { it.overallRating >= 4.5f }
    val fourStars = reviews.count { it.overallRating in 3.5f..4.4f }
    val threeStars = reviews.count { it.overallRating in 2.5f..3.4f }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("KITCHEN RATING SCORECARD", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = SaffronPrimary, letterSpacing = 1.sp)
                    Text(caterer?.name ?: "A1 Huma Caterers", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Based on $totalCount verified event bookings", fontSize = 11.sp, color = Color.Gray)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${caterer?.rating ?: 4.8}", fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, color = SaffronPrimary)
                    Row {
                        repeat(5) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Rating Distribution Bars
            RatingDistributionRow(5, if (totalCount > 0) fiveStars.toFloat() / totalCount else 0.85f, fiveStars)
            RatingDistributionRow(4, if (totalCount > 0) fourStars.toFloat() / totalCount else 0.12f, fourStars)
            RatingDistributionRow(3, if (totalCount > 0) threeStars.toFloat() / totalCount else 0.03f, threeStars)

            Spacer(modifier = Modifier.height(14.dp))

            // Key Highlights Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MetricColumn("98%", "Recommend Rate")
                MetricColumn("4.9 ★", "Dum Taste")
                MetricColumn("100%", "On-Time Dispatch")
                MetricColumn("FSSAI", "Govt Verified")
            }
        }
    }
}

@Composable
private fun MetricColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SaffronPrimary)
        Text(label, fontSize = 10.sp, color = Color(0xFF64748B))
    }
}

@Composable
private fun RatingDistributionRow(stars: Int, progress: Float, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$stars ★", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), modifier = Modifier.width(28.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = AmberSecondary,
            trackColor = Color(0xFFF1F5F9)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("$count", fontSize = 11.sp, color = Color(0xFF94A3B8), modifier = Modifier.width(20.dp), textAlign = TextAlign.End)
    }
}

// -------------------------------------------------------------
// COMPONENT 9: Public Verified Customer Review Card
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PublicReviewCard(review: PartnerReviewEntity) {
    val dateStr = remember(review.createdAtTimestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(review.createdAtTimestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User info & rating row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFFEF3C7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.customerName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SaffronPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(review.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = VegGreen, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Verified Order", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                                }
                            }
                        }
                        Text("Occasion: ${review.eventType} • $dateStr", fontSize = 10.5.sp, color = Color.Gray)
                    }
                }

                Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(6.dp)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${review.overallRating}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF92400E))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tags
            if (review.tagList.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    review.tagList.forEach { tag ->
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Review comment
            if (review.comment.isNotEmpty()) {
                Text(
                    text = "\"${review.comment}\"",
                    fontSize = 12.5.sp,
                    color = Color(0xFF334155),
                    lineHeight = 18.sp
                )
            }

            // Dish rating summary
            if (review.dishRatingsJson.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFFFF7ED),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🍽️ ${review.dishRatingsJson}",
                        fontSize = 10.5.sp,
                        color = Color(0xFFC2410C),
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            // Kitchen Owner Response
            if (review.kitchenResponse.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${review.catererName} (Kitchen Owner Response)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SaffronPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = review.kitchenResponse,
                            fontSize = 11.5.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 10: Review Success & Celebration Dialog
// -------------------------------------------------------------
@Composable
private fun ReviewSuccessDialog(
    catererName: String,
    rating: Float,
    onDismiss: () -> Unit,
    onViewCommunityReviews: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("dialog_continue_btn")
            ) {
                Text("Back to Home")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onViewCommunityReviews,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("dialog_view_reviews_btn")
            ) {
                Text("View Community Wall")
            }
        },
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFDCFCE7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Celebration, contentDescription = null, tint = VegGreen, modifier = Modifier.size(36.dp))
            }
        },
        title = {
            Text(
                text = "Feedback Published! ⭐",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Thank you for rating $catererName! Your genuine review helps other families and party organizers book the finest catering feast.",
                    fontSize = 12.5.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("+50 Foodie Coins Added!", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFF92400E))
                            Text("Redeem on your next Deg / Feast order", fontSize = 10.sp, color = Color(0xFFB45309))
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}
