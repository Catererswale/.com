package com.example.data.repository

import android.content.Context
import com.example.util.NotificationHelper
import com.example.data.local.CaterersDao
import com.example.data.models.BartanRecordEntity
import com.example.data.models.CartItemEntity
import com.example.data.models.CatererEntity
import com.example.data.models.DeliveryBoyEntity
import com.example.data.models.FavoriteKitchenEntity
import com.example.data.models.FoodType
import com.example.data.models.KitchenUtensilEntity
import com.example.data.models.KycStatus
import com.example.data.models.MenuItemEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.OrderEntity
import com.example.data.models.OrderStatus
import com.example.data.models.PartnerReviewEntity
import com.example.data.models.PaymentMethod
import com.example.data.models.PaymentStatus
import com.example.data.models.UnitType
import com.example.data.models.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CaterersRepository(private val dao: CaterersDao) {

    val allCaterers: Flow<List<CatererEntity>> = dao.getAllCaterers()
    val allMenuItems: Flow<List<MenuItemEntity>> = dao.getAllMenuItems()
    val cartItems: Flow<List<CartItemEntity>> = dao.getCartItems()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allDeliveryBoys: Flow<List<DeliveryBoyEntity>> = dao.getAllDeliveryBoys()
    val allBartanRecords: Flow<List<BartanRecordEntity>> = dao.getAllBartanRecords()
    val allNotifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()

    fun getMenuItemsByCaterer(catererId: String): Flow<List<MenuItemEntity>> =
        dao.getMenuItemsByCaterer(catererId)

    fun getOrderByIdFlow(orderId: String): Flow<OrderEntity?> =
        dao.getOrderByIdFlow(orderId)

    suspend fun getOrderById(orderId: String): OrderEntity? =
        dao.getOrderById(orderId)

    suspend fun seedInitialDataIfEmpty() {
        val existingCaterers = allCaterers.first()
        if (existingCaterers.isEmpty()) {
            val caterersList = listOf(
                CatererEntity(
                    id = "caterer_1",
                    name = "A1 Huma Caterers",
                    kitchenName = "A1 Huma Central Kitchen",
                    logoUrl = "",
                    bannerUrl = "",
                    rating = 4.8f,
                    reviewCount = 342,
                    deliveryTimeMinutes = 45,
                    minOrderAmount = 1000.0,
                    deliveryCharge = 150.0,
                    distanceKm = 2.4,
                    fssaiLicense = "11223344556677",
                    isFssaiVerified = true,
                    isOpenForBooking = true,
                    address = "Plot 42, Catering Market, Okhla Phase 3",
                    city = "New Delhi",
                    ownerMobile = "+91 9876543210",
                    kycStatus = KycStatus.APPROVED,
                    aadhaarNumber = "9988 7766 5544",
                    panNumber = "ABCDE1234F",
                    bankAccount = "918273645012",
                    bankIfsc = "HDFC0001234",
                    dietaryType = "BOTH"
                ),
                CatererEntity(
                    id = "caterer_2",
                    name = "Shahi Dawat Catering",
                    kitchenName = "Shahi Dawat Royal Kitchen",
                    logoUrl = "",
                    bannerUrl = "",
                    rating = 4.6f,
                    reviewCount = 215,
                    deliveryTimeMinutes = 50,
                    minOrderAmount = 1500.0,
                    deliveryCharge = 200.0,
                    distanceKm = 3.8,
                    fssaiLicense = "22334455667788",
                    isFssaiVerified = true,
                    isOpenForBooking = true,
                    address = "12 Heritage Lane, Chandni Chowk",
                    city = "New Delhi",
                    ownerMobile = "+91 9812345678",
                    kycStatus = KycStatus.APPROVED,
                    aadhaarNumber = "1122 3344 5566",
                    panNumber = "XYZAB5678C",
                    bankAccount = "501002345678",
                    bankIfsc = "ICIC0005678",
                    dietaryType = "BOTH"
                ),
                CatererEntity(
                    id = "caterer_3",
                    name = "Royal Banquet Kitchens",
                    kitchenName = "Royal Banquet Bulk Catering",
                    logoUrl = "",
                    bannerUrl = "",
                    rating = 4.9f,
                    reviewCount = 512,
                    deliveryTimeMinutes = 40,
                    minOrderAmount = 2000.0,
                    deliveryCharge = 100.0,
                    distanceKm = 1.8,
                    fssaiLicense = "33445566778899",
                    isFssaiVerified = true,
                    isOpenForBooking = true,
                    address = "78 Ring Road, Lajpat Nagar",
                    city = "New Delhi",
                    ownerMobile = "+91 9900112233",
                    kycStatus = KycStatus.APPROVED,
                    offersAddonServices = false,
                    dietaryType = "BOTH"
                ),
                CatererEntity(
                    id = "caterer_4",
                    name = "Shri Krishna Pure Veg Caterers",
                    kitchenName = "Shri Krishna Shuddh Shakahari Rasoi",
                    logoUrl = "",
                    bannerUrl = "",
                    rating = 4.9f,
                    reviewCount = 428,
                    deliveryTimeMinutes = 35,
                    minOrderAmount = 1200.0,
                    deliveryCharge = 100.0,
                    distanceKm = 1.5,
                    fssaiLicense = "11523000445566",
                    isFssaiVerified = true,
                    isOpenForBooking = true,
                    address = "Shop 14, Mandir Marg, Karol Bagh",
                    city = "New Delhi",
                    ownerMobile = "+91 9811223344",
                    kycStatus = KycStatus.APPROVED,
                    dietaryType = "PURE_VEG"
                )
            )
            dao.insertCaterers(caterersList)

            // Seed Menu Items
            val menuItems = listOf(
                MenuItemEntity(
                    id = "m1", catererId = "caterer_1", catererName = "A1 Huma Caterers",
                    category = "Chicken Biryani", name = "Special Dum Chicken Biryani",
                    description = "Aromatic basmati rice cooked with succulent bone-in chicken & secret shahi spices.",
                    imageUrl = "", foodType = FoodType.NON_VEG, unitType = UnitType.KG,
                    pricePerUnit = 380.0, minQuantity = 1.0, maxQuantity = 50.0, stepQuantity = 0.5,
                    isPopular = true, isRecommended = true
                ),
                MenuItemEntity(
                    id = "m2", catererId = "caterer_1", catererName = "A1 Huma Caterers",
                    category = "Mutton Biryani", name = "Royal Hyderabadi Mutton Biryani",
                    description = "Tender mutton pieces layered with saffron saffron rice and fried onions.",
                    imageUrl = "", foodType = FoodType.NON_VEG, unitType = UnitType.KG,
                    pricePerUnit = 650.0, minQuantity = 1.0, maxQuantity = 40.0, stepQuantity = 0.5,
                    isPopular = true
                ),
                MenuItemEntity(
                    id = "m3", catererId = "caterer_1", catererName = "A1 Huma Caterers",
                    category = "Veg Biryani", name = "Shahi Paneer Dum Biryani",
                    description = "Fresh cottage cheese cubes marinated in yogurt and spices layered with fragrant rice.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.KG,
                    pricePerUnit = 280.0, minQuantity = 1.0, maxQuantity = 50.0, stepQuantity = 0.5,
                    isRecommended = true
                ),
                MenuItemEntity(
                    id = "m4", catererId = "caterer_1", catererName = "A1 Huma Caterers",
                    category = "Chicken Gravy", name = "Butter Chicken (Shahi Style)",
                    description = "Rich, creamy tomato-butter gravy with char-grilled chicken tikka.",
                    imageUrl = "", foodType = FoodType.NON_VEG, unitType = UnitType.KG,
                    pricePerUnit = 420.0, minQuantity = 1.0, maxQuantity = 30.0, stepQuantity = 0.5
                ),
                MenuItemEntity(
                    id = "m5", catererId = "caterer_1", catererName = "A1 Huma Caterers",
                    category = "Veg Gravy", name = "Kadhai Paneer Gravy",
                    description = "Paneer cooked with bell peppers, onion, and freshly ground kadhai spices.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.KG,
                    pricePerUnit = 340.0, minQuantity = 1.0, maxQuantity = 30.0, stepQuantity = 0.5
                ),
                MenuItemEntity(
                    id = "m6", catererId = "caterer_1", catererName = "A1 Huma Caterers",
                    category = "Chinese", name = "Veg Hakka Noodles",
                    description = "Wok-tossed noodles with crunchy spring vegetables and oriental sauces.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.KG,
                    pricePerUnit = 240.0, minQuantity = 1.0, maxQuantity = 25.0, stepQuantity = 0.5
                ),
                MenuItemEntity(
                    id = "m7", catererId = "caterer_1", catererName = "A1 Huma Caterers",
                    category = "Desserts", name = "Shahi Zafrani Kheer",
                    description = "Creamy rice pudding infused with saffron, green cardamom, and dry fruits.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.LITRE,
                    pricePerUnit = 220.0, minQuantity = 1.0, maxQuantity = 50.0, stepQuantity = 1.0,
                    isPopular = true
                ),
                MenuItemEntity(
                    id = "m8", catererId = "caterer_1", catererName = "A1 Huma Caterers",
                    category = "Desserts", name = "Soft Gulab Jamun",
                    description = "Melt-in-mouth khoya dumplings soaked in rose-scented sugar syrup.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.DOZEN,
                    pricePerUnit = 200.0, minQuantity = 1.0, maxQuantity = 50.0, stepQuantity = 1.0
                ),
                MenuItemEntity(
                    id = "m9", catererId = "caterer_1", catererName = "A1 Huma Caterers",
                    category = "Beverages", name = "Fresh Mint Jaljeera Lassi",
                    description = "Refreshing chilled yogurt lassi flavoured with fresh mint and roasted cumin.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.LITRE,
                    pricePerUnit = 140.0, minQuantity = 2.0, maxQuantity = 100.0, stepQuantity = 1.0
                ),
                // Shahi Dawat Catering (caterer_2)
                MenuItemEntity(
                    id = "m10", catererId = "caterer_2", catererName = "Shahi Dawat Catering",
                    category = "Chicken Biryani", name = "Awadhi Dum Chicken Biryani",
                    description = "Traditional slow cooked Awadhi biryani with kewra water and saffron chicken.",
                    imageUrl = "", foodType = FoodType.NON_VEG, unitType = UnitType.KG,
                    pricePerUnit = 450.0, minQuantity = 1.0, maxQuantity = 50.0, stepQuantity = 0.5,
                    isPopular = true, isRecommended = true
                ),
                MenuItemEntity(
                    id = "m11", catererId = "caterer_2", catererName = "Shahi Dawat Catering",
                    category = "Veg Biryani", name = "Navratan Saffron Veg Biryani",
                    description = "Mixed farm vegetables and cottage cheese layered with golden saffron rice.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.KG,
                    pricePerUnit = 320.0, minQuantity = 1.0, maxQuantity = 50.0, stepQuantity = 0.5,
                    isRecommended = true
                ),
                MenuItemEntity(
                    id = "m12", catererId = "caterer_2", catererName = "Shahi Dawat Catering",
                    category = "Mutton Gravy", name = "Shahi Mutton Korma Handi",
                    description = "Melt-in-mouth mutton shank pieces simmered in thick cashew-onion gravy.",
                    imageUrl = "", foodType = FoodType.NON_VEG, unitType = UnitType.KG,
                    pricePerUnit = 720.0, minQuantity = 1.0, maxQuantity = 30.0, stepQuantity = 0.5,
                    isPopular = true
                ),
                MenuItemEntity(
                    id = "m13", catererId = "caterer_2", catererName = "Shahi Dawat Catering",
                    category = "Veg Gravy", name = "Dal Makhani (Bukhara Style)",
                    description = "Slow cooked black lentils on charcoal overnight with white butter and cream.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.KG,
                    pricePerUnit = 290.0, minQuantity = 1.0, maxQuantity = 40.0, stepQuantity = 0.5
                ),
                MenuItemEntity(
                    id = "m14", catererId = "caterer_2", catererName = "Shahi Dawat Catering",
                    category = "Desserts", name = "Rabdi Malpua Platter",
                    description = "Crispy golden malpua served with rich reduced milk rabdi and pistachios.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.KG,
                    pricePerUnit = 380.0, minQuantity = 1.0, maxQuantity = 30.0, stepQuantity = 0.5
                ),
                // Royal Banquet Kitchens (caterer_3)
                MenuItemEntity(
                    id = "m15", catererId = "caterer_3", catererName = "Royal Banquet Kitchens",
                    category = "Chicken Biryani", name = "Royal Kolkata Chicken Biryani",
                    description = "Lightly spiced long-grain biryani with tender chicken and golden browned potatoes.",
                    imageUrl = "", foodType = FoodType.NON_VEG, unitType = UnitType.KG,
                    pricePerUnit = 420.0, minQuantity = 1.0, maxQuantity = 50.0, stepQuantity = 0.5,
                    isPopular = true
                ),
                MenuItemEntity(
                    id = "m16", catererId = "caterer_3", catererName = "Royal Banquet Kitchens",
                    category = "Veg Biryani", name = "Paneer Tikka Dum Biryani",
                    description = "Tandoor roasted paneer cubes tossed in spicy masala layered in fragrant rice.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.KG,
                    pricePerUnit = 350.0, minQuantity = 1.0, maxQuantity = 50.0, stepQuantity = 0.5,
                    isRecommended = true
                ),
                MenuItemEntity(
                    id = "m17", catererId = "caterer_3", catererName = "Royal Banquet Kitchens",
                    category = "Mutton Biryani", name = "Degi Mutton Yakhni Biryani",
                    description = "Rich mutton cooked in seasoned yakhni broth with basmati rice.",
                    imageUrl = "", foodType = FoodType.NON_VEG, unitType = UnitType.KG,
                    pricePerUnit = 780.0, minQuantity = 1.0, maxQuantity = 40.0, stepQuantity = 0.5,
                    isPopular = true
                ),
                MenuItemEntity(
                    id = "m18", catererId = "caterer_3", catererName = "Royal Banquet Kitchens",
                    category = "Desserts", name = "Kesari Phirni in Earthen Pots",
                    description = "Fine ground basmati rice pudding topped with silver vark and almonds.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.PORTION,
                    pricePerUnit = 60.0, minQuantity = 10.0, maxQuantity = 200.0, stepQuantity = 5.0
                ),
                // Shri Krishna Pure Veg Caterers (caterer_4) - 100% Pure Shakahari
                MenuItemEntity(
                    id = "m19", catererId = "caterer_4", catererName = "Shri Krishna Pure Veg Caterers",
                    category = "Veg Biryani", name = "Shahi Paneer Dum Handi Biryani",
                    description = "100% Pure Veg: Fragrant basmati rice slow dum cooked with marinated malai paneer & saffron.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.KG,
                    pricePerUnit = 320.0, minQuantity = 1.0, maxQuantity = 50.0, stepQuantity = 0.5,
                    isPopular = true, isRecommended = true
                ),
                MenuItemEntity(
                    id = "m20", catererId = "caterer_4", catererName = "Shri Krishna Pure Veg Caterers",
                    category = "Veg Gravy", name = "Dal Makhani (Desi Ghee Bukhara)",
                    description = "100% Pure Veg: Black urad lentils slow simmered for 18 hours in pure cow desi ghee.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.KG,
                    pricePerUnit = 290.0, minQuantity = 1.0, maxQuantity = 40.0, stepQuantity = 0.5,
                    isPopular = true
                ),
                MenuItemEntity(
                    id = "m21", catererId = "caterer_4", catererName = "Shri Krishna Pure Veg Caterers",
                    category = "Veg Gravy", name = "Kaju Shahi Paneer Butter Masala",
                    description = "100% Pure Veg: Soft paneer cubes cooked in rich cashew and ripe tomato velvet gravy.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.KG,
                    pricePerUnit = 380.0, minQuantity = 1.0, maxQuantity = 40.0, stepQuantity = 0.5,
                    isRecommended = true
                ),
                MenuItemEntity(
                    id = "m22", catererId = "caterer_4", catererName = "Shri Krishna Pure Veg Caterers",
                    category = "Desserts", name = "Gulab Jamun Handi (Desi Ghee)",
                    description = "100% Pure Veg: Melt-in-mouth mawa gulab jamun soaked in rose-cardamom sugar syrup.",
                    imageUrl = "", foodType = FoodType.VEG, unitType = UnitType.PORTION,
                    pricePerUnit = 45.0, minQuantity = 10.0, maxQuantity = 250.0, stepQuantity = 5.0,
                    isPopular = true
                )
            )
            dao.insertMenuItems(menuItems)

            // Seed Delivery Boys
            val boys = listOf(
                DeliveryBoyEntity("db_1", "caterer_1", "Ramesh Sharma", "+91 9811223344", "2345 6789 0123", "DL-0420190012345", true, true, false, 5, 1250.0, 2),
                DeliveryBoyEntity("db_2", "caterer_1", "Imran Khan", "+91 9822334455", "3456 7890 1234", "", true, true, false, 3, 800.0, 1),
                DeliveryBoyEntity("db_3", "caterer_1", "Aman Singh", "+91 9833445566", "4567 8901 2345", "DL-0720210087654", true, false, false, 0, 0.0, 0),
                DeliveryBoyEntity("db_4", "caterer_2", "Shabbir Ahmed", "+91 9844556677", "5678 9012 3456", "DL-0520200054321", true, true, false, 4, 950.0, 1),
                DeliveryBoyEntity("db_5", "caterer_2", "Sameer Malik", "+91 9855667788", "6789 0123 4567", "", true, true, false, 2, 500.0, 0)
            )
            for (boy in boys) dao.insertDeliveryBoy(boy)

            // Seed Sample Orders
            val order1 = OrderEntity(
                orderId = "CW-89210",
                customerName = "Rohan Verma",
                customerMobile = "+91 9876511223",
                deliveryAddress = "Flat 402, Green Park Apartments, Okhla Phase 3",
                catererId = "caterer_1",
                catererName = "A1 Huma Caterers",
                itemsSummary = "Special Dum Chicken Biryani (5.0 Kg), Shahi Zafrani Kheer (3.0 Litre)",
                totalAmount = 2560.0,
                advancePaidAmount = 768.0,
                balanceAmount = 1792.0,
                paymentMethod = PaymentMethod.UPI,
                paymentStatus = PaymentStatus.ADVANCE_PAID_30,
                orderStatus = OrderStatus.OUT_FOR_DELIVERY,
                deliveryDate = "2026-07-25",
                deliveryTimeSlot = "12:30 PM - 01:00 PM",
                deliveryOtp = "4829",
                deliveryBoyId = "db_1",
                deliveryBoyName = "Ramesh Sharma",
                deliveryBoyMobile = "+91 9811223344",
                isBartanPending = true,
                bartanDescription = "2 Metal Biryani Handi, 1 Kheer Pot"
            )

            val order2 = OrderEntity(
                orderId = "CW-89211",
                customerName = "Priya Sharma",
                customerMobile = "+91 9811998877",
                deliveryAddress = "House 18, Block B, Preet Vihar, Delhi",
                catererId = "caterer_1",
                catererName = "A1 Huma Caterers",
                itemsSummary = "Shahi Paneer Dum Biryani (10.0 Kg), Soft Gulab Jamun (4 Dozen)",
                totalAmount = 3600.0,
                advancePaidAmount = 3600.0,
                balanceAmount = 0.0,
                paymentMethod = PaymentMethod.DYNAMIC_QR,
                paymentStatus = PaymentStatus.FULL_PAID,
                orderStatus = OrderStatus.PREPARING,
                deliveryDate = "2026-07-25",
                deliveryTimeSlot = "07:30 PM - 08:00 PM",
                deliveryOtp = "1923"
            )

            val order3 = OrderEntity(
                orderId = "CW-89198",
                customerName = "Vikram Malhotra",
                customerMobile = "+91 9711223344",
                deliveryAddress = "A-24 Rajouri Garden, New Delhi",
                catererId = "caterer_1",
                catererName = "A1 Huma Caterers",
                itemsSummary = "Royal Hyderabadi Mutton Biryani (8.0 Kg)",
                totalAmount = 5200.0,
                advancePaidAmount = 2600.0,
                balanceAmount = 2600.0,
                paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
                paymentStatus = PaymentStatus.BALANCE_PENDING,
                isOfflineBooking = true,
                orderStatus = OrderStatus.DELIVERED,
                deliveryDate = "2026-07-24",
                deliveryTimeSlot = "01:00 PM - 01:30 PM",
                deliveryOtp = "8812",
                deliveryBoyId = "db_1",
                deliveryBoyName = "Ramesh Sharma",
                deliveryBoyMobile = "+91 9811223344",
                isBartanPending = true,
                bartanDescription = "3 Royal Biryani Degs",
                isBartanReturned = false,
                cashCollectedByDeliveryBoy = 2600.0,
                isCashSubmittedToKitchen = false,
                userRating = 5.0f,
                userReview = "Flavours were superb! On-time delivery for my daughter's birthday."
            )

            val order4 = OrderEntity(
                orderId = "CW-89215",
                customerName = "Zaid Siddiqui",
                customerMobile = "+91 9899112233",
                deliveryAddress = "Villa 12, Jasola Vihar, New Delhi",
                catererId = "caterer_1",
                catererName = "A1 Huma Caterers",
                itemsSummary = "Special Awadhi Mutton Biryani (12.0 Kg), Shahi Zafrani Kheer (6.0 Litre)",
                totalAmount = 7800.0,
                advancePaidAmount = 2340.0,
                balanceAmount = 5460.0,
                paymentMethod = PaymentMethod.UPI,
                paymentStatus = PaymentStatus.ADVANCE_PAID_30,
                orderStatus = OrderStatus.NEW,
                deliveryDate = "2026-07-25",
                deliveryTimeSlot = "08:00 PM - 08:30 PM",
                deliveryOtp = "5512",
                isBartanPending = true,
                bartanDescription = "4 Degs Mutton Biryani"
            )

            val order5 = OrderEntity(
                orderId = "CW-89216",
                customerName = "Anita Desai",
                customerMobile = "+91 9877665544",
                deliveryAddress = "Sector 15, Noida, UP",
                catererId = "caterer_1",
                catererName = "A1 Huma Caterers",
                itemsSummary = "Veg Gravy Combo & Shahi Paneer (15.0 Kg), Rumali Roti (50 Pcs)",
                totalAmount = 4500.0,
                advancePaidAmount = 1350.0,
                balanceAmount = 3150.0,
                paymentMethod = PaymentMethod.UPI,
                paymentStatus = PaymentStatus.ADVANCE_PAID_30,
                orderStatus = OrderStatus.CONFIRMED,
                deliveryDate = "2026-07-25",
                deliveryTimeSlot = "01:30 PM - 02:00 PM",
                deliveryOtp = "9014"
            )

            val order6 = OrderEntity(
                orderId = "CW-89217",
                customerName = "Karan Oberoi",
                customerMobile = "+91 9811447788",
                deliveryAddress = "GK-2 Enclave, New Delhi",
                catererId = "caterer_1",
                catererName = "A1 Huma Caterers",
                itemsSummary = "Special Chicken Biryani (15.0 Kg), Soft Gulab Jamun (5 Dozen)",
                totalAmount = 6200.0,
                advancePaidAmount = 6200.0,
                balanceAmount = 0.0,
                paymentMethod = PaymentMethod.DYNAMIC_QR,
                paymentStatus = PaymentStatus.FULL_PAID,
                orderStatus = OrderStatus.READY,
                deliveryDate = "2026-07-25",
                deliveryTimeSlot = "02:00 PM - 02:30 PM",
                deliveryOtp = "7231",
                isBartanPending = true,
                bartanDescription = "3 Big Stainless Handis"
            )

            val order7 = OrderEntity(
                orderId = "CW-89220",
                customerName = "Meera Kapoor",
                customerMobile = "+91 9955443322",
                deliveryAddress = "Tower 4, DLF Phase 5, Gurugram",
                catererId = "caterer_1",
                catererName = "A1 Huma Caterers",
                itemsSummary = "Royal Mutton Biryani (20.0 Kg), Shahi Zafrani Kheer (10.0 Litre)",
                totalAmount = 14200.0,
                advancePaidAmount = 4260.0,
                balanceAmount = 9940.0,
                paymentMethod = PaymentMethod.UPI,
                paymentStatus = PaymentStatus.ADVANCE_PAID_30,
                orderStatus = OrderStatus.CONFIRMED,
                deliveryDate = "2026-07-26",
                deliveryTimeSlot = "08:30 PM - 09:00 PM",
                deliveryOtp = "3421",
                isBartanPending = true,
                bartanDescription = "6 Large Wedding Degs"
            )

            val order8 = OrderEntity(
                orderId = "CW-89182",
                customerName = "Farhan Akhtar",
                customerMobile = "+91 9822114455",
                deliveryAddress = "Flat 402, Green Glen, Saket, New Delhi",
                catererId = "caterer_1",
                catererName = "A1 Huma Caterers",
                itemsSummary = "Chicken Dum Biryani (10.0 Kg), Mutton Seekh Kebab (40 Pcs)",
                totalAmount = 6400.0,
                advancePaidAmount = 3200.0,
                balanceAmount = 3200.0,
                paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
                paymentStatus = PaymentStatus.FULLY_SETTLED,
                isOfflineBooking = true,
                orderStatus = OrderStatus.DELIVERED,
                deliveryDate = "2026-07-23",
                deliveryTimeSlot = "08:00 PM - 08:30 PM",
                deliveryOtp = "4391",
                deliveryBoyId = "db_2",
                deliveryBoyName = "Imran Khan",
                deliveryBoyMobile = "+91 9822334455",
                isBartanPending = false,
                bartanDescription = "4 Degs & Serving Trays",
                isBartanReturned = true,
                cashCollectedByDeliveryBoy = 3200.0,
                isCashSubmittedToKitchen = true
            )

            dao.insertOrder(order1)
            dao.insertOrder(order2)
            dao.insertOrder(order3)
            dao.insertOrder(order4)
            dao.insertOrder(order5)
            dao.insertOrder(order6)
            dao.insertOrder(order7)
            dao.insertOrder(order8)

            // Seed Bartan Records (Food Delivery Containers to be Returned - Not Rented)
            dao.insertBartanRecord(
                BartanRecordEntity(
                    id = "b_1",
                    orderId = "CW-89198",
                    customerName = "Vikram Malhotra",
                    customerMobile = "+91 9711223344",
                    customerAddress = "A-24 Rajouri Garden, New Delhi",
                    catererId = "caterer_1",
                    catererName = "A1 Huma Caterers",
                    itemsDescription = "3 Royal Biryani Degs",
                    deliveryDate = "2026-07-24",
                    deliveryBoyId = "db_1",
                    deliveryBoyName = "Ramesh Sharma",
                    deliveryBoyMobile = "+91 9811223344",
                    isCollected = false,
                    returnStatus = "PENDING"
                )
            )
            dao.insertBartanRecord(
                BartanRecordEntity(
                    id = "b_2",
                    orderId = "CW-89217",
                    customerName = "Karan Oberoi",
                    customerMobile = "+91 9811447788",
                    customerAddress = "GK-2 Enclave, New Delhi",
                    catererId = "caterer_1",
                    catererName = "A1 Huma Caterers",
                    itemsDescription = "3 Big Stainless Handis",
                    deliveryDate = "2026-07-25",
                    deliveryBoyId = "db_2",
                    deliveryBoyName = "Suresh Verma",
                    deliveryBoyMobile = "+91 9877001122",
                    isCollected = false,
                    returnStatus = "PENDING"
                )
            )
            dao.insertBartanRecord(
                BartanRecordEntity(
                    id = "b_3",
                    orderId = "CW-89182",
                    customerName = "Farhan Akhtar",
                    customerMobile = "+91 9822114455",
                    customerAddress = "Flat 402, Green Glen, Saket, New Delhi",
                    catererId = "caterer_1",
                    catererName = "A1 Huma Caterers",
                    itemsDescription = "4 Degs & Serving Trays",
                    deliveryDate = "2026-07-23",
                    deliveryBoyId = "db_2",
                    deliveryBoyName = "Imran Khan",
                    deliveryBoyMobile = "+91 9822334455",
                    isCollected = true,
                    collectedDate = "2026-07-24",
                    returnStatus = "RETURNED_TO_KITCHEN"
                )
            )

            // Seed Initial Verified Partner Reviews
            val sampleReviews = listOf(
                PartnerReviewEntity(
                    id = "rev_1",
                    orderId = "CW-89198",
                    catererId = "caterer_1",
                    catererName = "A1 Huma Caterers",
                    customerName = "Vikram Malhotra",
                    customerMobile = "+91 9711223344",
                    overallRating = 5.0f,
                    tasteRating = 5.0f,
                    portionRating = 5.0f,
                    packagingRating = 5.0f,
                    deliveryRating = 4.8f,
                    bartanRating = 5.0f,
                    tagsCsv = "🔥 Authentic Dum,🍗 Melt-in-mouth Meat,🍲 Generous Portion,✨ Sealed Steaming Degs",
                    eventType = "Birthday Celebration (45 Guests)",
                    comment = "The Dum Mutton Biryani was absolutely world-class! Every guest was asking for the caterer's number. Degs were delivered steaming hot with traditional seal intact.",
                    dishRatingsJson = "Royal Hyderabadi Mutton Biryani: 5★ (Loved it ❤️)",
                    isRecommended = true,
                    tipAmount = 100.0,
                    isAnonymous = false,
                    photosCount = 3,
                    createdAtTimestamp = System.currentTimeMillis() - (86400000L * 2),
                    kitchenResponse = "Thank you so much Vikram ji! It was our absolute honor catering for your daughter's birthday feast. Looking forward to serving your family again!",
                    kitchenResponseDate = "2026-07-24"
                ),
                PartnerReviewEntity(
                    id = "rev_2",
                    orderId = "CW-89140",
                    catererId = "caterer_1",
                    catererName = "A1 Huma Caterers",
                    customerName = "Farhana Begum",
                    customerMobile = "+91 9811002233",
                    overallRating = 5.0f,
                    tasteRating = 5.0f,
                    portionRating = 4.9f,
                    packagingRating = 5.0f,
                    deliveryRating = 5.0f,
                    bartanRating = 4.8f,
                    tagsCsv = "🧂 Balanced Spices,✨ Pure Zafrani Aroma,👨‍🍳 Polite Valet,⏱️ On-Time Delivery",
                    eventType = "Family Dawat / Get-Together (80 Guests)",
                    comment = "Zafrani Kheer and Dum Chicken Biryani were sensational. The aroma when opening the deg filled the entire hall. Punctual delivery right at 12:45 PM.",
                    dishRatingsJson = "Special Dum Chicken Biryani: 5★, Shahi Zafrani Kheer: 5★",
                    isRecommended = true,
                    tipAmount = 50.0,
                    isAnonymous = false,
                    photosCount = 2,
                    createdAtTimestamp = System.currentTimeMillis() - (86400000L * 5),
                    kitchenResponse = "Shukriya Farhana ji! We are delighted that your family enjoyed our authentic Zafrani aroma and timely service.",
                    kitchenResponseDate = "2026-07-21"
                ),
                PartnerReviewEntity(
                    id = "rev_3",
                    orderId = "CW-89088",
                    catererId = "caterer_1",
                    catererName = "A1 Huma Caterers",
                    customerName = "Anurag Kashyap",
                    customerMobile = "+91 9988112233",
                    overallRating = 4.8f,
                    tasteRating = 4.8f,
                    portionRating = 5.0f,
                    packagingRating = 4.7f,
                    deliveryRating = 5.0f,
                    bartanRating = 4.8f,
                    tagsCsv = "🍲 Generous Portion,⏱️ On-Time Delivery,🔥 Authentic Dum",
                    eventType = "Corporate Team Feast (60 Pax)",
                    comment = "Ordered for an office milestone lunch. Biryani portions were very generous, and mirchi ka salan + burani raita complimented it flawlessly.",
                    dishRatingsJson = "Special Dum Chicken Biryani: 5★",
                    isRecommended = true,
                    tipAmount = 100.0,
                    isAnonymous = false,
                    photosCount = 1,
                    createdAtTimestamp = System.currentTimeMillis() - (86400000L * 9),
                    kitchenResponse = "Thank you Anurag for trusting A1 Huma Caterers for your corporate celebration!",
                    kitchenResponseDate = "2026-07-17"
                ),
                PartnerReviewEntity(
                    id = "rev_4",
                    orderId = "CW-88990",
                    catererId = "caterer_2",
                    catererName = "Royal Dawat & Handi",
                    customerName = "Kunal Kapoor",
                    customerMobile = "+91 9771122334",
                    overallRating = 4.7f,
                    tasteRating = 4.8f,
                    portionRating = 4.7f,
                    packagingRating = 4.8f,
                    deliveryRating = 4.6f,
                    bartanRating = 4.6f,
                    tagsCsv = "🔥 Authentic Dum,🍗 Tender Meat,✨ Sealed Steaming Degs",
                    eventType = "Anniversary Dinner",
                    comment = "Rich Hyderabadi flavor with pure ghee tadka. Loved the clay handi presentation.",
                    dishRatingsJson = "Royal Hyderabadi Mutton Biryani: 5★",
                    isRecommended = true,
                    tipAmount = 50.0,
                    isAnonymous = false,
                    photosCount = 2,
                    createdAtTimestamp = System.currentTimeMillis() - (86400000L * 12)
                )
            )
            for (rev in sampleReviews) {
                dao.insertReview(rev)
            }

            // Seed initial notifications
            dao.insertNotification(
                NotificationEntity(
                    id = "n_1",
                    title = "New Order Assigned! 📦",
                    message = "Order #CW-89210 assigned for delivery to Rohan Verma at Green Park Apartments.",
                    targetRole = UserRole.DELIVERY_BOY
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    id = "n_2",
                    title = "Order Accepted #CW-89210 🟢",
                    message = "Kitchen A1 Huma Caterers is preparing your order for 12:30 PM delivery.",
                    targetRole = UserRole.CUSTOMER
                )
            )

            // Seed default kitchen utensils inventory
            val defaultUtensils = listOf(
                KitchenUtensilEntity(id = "utensil_1", kitchenId = "caterer_1", name = "Biryani Handi / Tapela", icon = "🍲", totalStock = 50, unit = "Pcs"),
                KitchenUtensilEntity(id = "utensil_2", kitchenId = "caterer_1", name = "Bada Chammach / Karchi", icon = "🥄", totalStock = 80, unit = "Pcs"),
                KitchenUtensilEntity(id = "utensil_3", kitchenId = "caterer_1", name = "Kachumber Box / Dabba", icon = "🥗", totalStock = 40, unit = "Pcs"),
                KitchenUtensilEntity(id = "utensil_4", kitchenId = "caterer_1", name = "Raita Bowl / Dabba", icon = "🥣", totalStock = 30, unit = "Pcs"),
                KitchenUtensilEntity(id = "utensil_5", kitchenId = "caterer_1", name = "Chafing Dish / Buffet Stand", icon = "🥘", totalStock = 15, unit = "Sets")
            )
            for (u in defaultUtensils) {
                dao.insertUtensil(u)
            }
        }
    }

    suspend fun addToCart(item: MenuItemEntity, qty: Double) {
        val cartItemId = "cart_${item.id}"
        val effectivePrice = item.getEffectivePrice()
        val total = effectivePrice * qty
        val cartItem = CartItemEntity(
            id = cartItemId,
            menuItemId = item.id,
            catererId = item.catererId,
            catererName = item.catererName,
            name = item.name,
            imageUrl = item.imageUrl,
            foodType = item.foodType,
            unitType = item.unitType,
            pricePerUnit = effectivePrice,
            originalPricePerUnit = item.pricePerUnit,
            quantity = qty,
            totalPrice = total,
            catererDiscountType = item.catererDiscountType,
            catererDiscountValue = item.catererDiscountValue,
            adminDiscountType = item.adminDiscountType,
            adminDiscountValue = item.adminDiscountValue
        )
        dao.insertCartItem(cartItem)
    }

    suspend fun addCustomAddOnToCart(
        catererId: String,
        catererName: String,
        addOnId: String,
        name: String,
        price: Double,
        qty: Double,
        foodType: FoodType = FoodType.VEG
    ) {
        val cartItemId = "cart_addon_${addOnId}"
        val total = price * qty
        val cartItem = CartItemEntity(
            id = cartItemId,
            menuItemId = addOnId,
            catererId = catererId,
            catererName = catererName,
            name = name,
            imageUrl = "",
            foodType = foodType,
            unitType = UnitType.PORTION,
            pricePerUnit = price,
            originalPricePerUnit = price,
            quantity = qty,
            totalPrice = total
        )
        dao.insertCartItem(cartItem)
    }

    suspend fun removeCartItem(id: String) {
        dao.deleteCartItem(id)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    suspend fun createOrder(
        customerName: String,
        customerMobile: String,
        deliveryAddress: String,
        catererId: String,
        catererName: String,
        itemsSummary: String,
        totalAmount: Double,
        is30PercentAdvance: Boolean,
        paymentMethod: PaymentMethod,
        deliveryDate: String,
        deliveryTimeSlot: String,
        catererDiscountAmount: Double = 0.0,
        adminDiscountAmount: Double = 0.0,
        loyaltyDiscountAmount: Double = 0.0,
        redeemedLoyaltyPoints: Int = 0,
        earnedLoyaltyPoints: Int = 0,
        isOfflineBooking: Boolean = false,
        customAdvanceAmount: Double? = null
    ): String {
        val orderNum = (10000..99999).random()
        val orderId = "CW-$orderNum"
        // 50% Advance Booking Token or Custom/COD Advance
        val advancePaid = if (customAdvanceAmount != null) {
            customAdvanceAmount.coerceIn(0.0, totalAmount)
        } else if (is30PercentAdvance) {
            totalAmount * 0.50
        } else {
            totalAmount
        }
        val balance = (totalAmount - advancePaid).coerceAtLeast(0.0)
        val otp = (1000..9999).random().toString()

        val newOrder = OrderEntity(
            orderId = orderId,
            customerName = customerName,
            customerMobile = customerMobile,
            deliveryAddress = deliveryAddress,
            catererId = catererId,
            catererName = catererName,
            itemsSummary = itemsSummary,
            totalAmount = totalAmount,
            advancePaidAmount = advancePaid,
            balanceAmount = balance,
            paymentMethod = paymentMethod,
            paymentStatus = when {
                balance <= 0.0 -> PaymentStatus.FULL_PAID
                advancePaid <= 0.0 -> PaymentStatus.BALANCE_PENDING
                else -> PaymentStatus.ADVANCE_PAID_30
            },
            orderStatus = OrderStatus.CONFIRMED,
            deliveryDate = deliveryDate,
            deliveryTimeSlot = deliveryTimeSlot,
            deliveryOtp = otp,
            catererOrderDiscountAmount = catererDiscountAmount,
            adminOrderDiscountAmount = adminDiscountAmount,
            loyaltyDiscountAmount = loyaltyDiscountAmount,
            redeemedLoyaltyPoints = redeemedLoyaltyPoints,
            earnedLoyaltyPoints = earnedLoyaltyPoints,
            isOfflineBooking = isOfflineBooking,
            cashCollectedByDeliveryBoy = if (paymentMethod == PaymentMethod.CASH_ON_DELIVERY) balance else 0.0,
            isCashSubmittedToKitchen = if (paymentMethod == PaymentMethod.CASH_ON_DELIVERY && advancePaid == 0.0) false else isOfflineBooking
        )

        dao.insertOrder(newOrder)
        dao.clearCart()

        val custMsg = "Order $orderId placed with $catererName for $deliveryDate ($deliveryTimeSlot)."
        val kitMsg = "New order $orderId received for ₹${totalAmount.toInt()} from $customerName."

        // Create Notifications
        dao.insertNotification(
            NotificationEntity(
                id = "n_${System.currentTimeMillis()}_cust",
                title = "Order Placed Successfully! 🎉",
                message = custMsg,
                targetRole = UserRole.CUSTOMER
            )
        )
        dao.insertNotification(
            NotificationEntity(
                id = "n_${System.currentTimeMillis()}_kit",
                title = "New Order Received! 🛎️",
                message = kitMsg,
                targetRole = UserRole.KITCHEN
            )
        )

        return orderId
    }

    suspend fun updateOrder(order: OrderEntity) {
        dao.updateOrder(order)
    }

    suspend fun insertNotification(notification: NotificationEntity) {
        dao.insertNotification(notification)
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus, context: Context? = null) {
        dao.updateOrderStatus(orderId, status)
        val order = dao.getOrderById(orderId) ?: return

        val driverName = order.deliveryBoyName ?: "our delivery driver"

        val (title, message) = when (status) {
            OrderStatus.NEW -> Pair("New Order Received 🛎️", "Order #${order.orderId} received.")
            OrderStatus.ACCEPTED, OrderStatus.CONFIRMED -> Pair("Order Confirmed! 🟢", "Kitchen ${order.catererName} accepted your order #${order.orderId}.")
            OrderStatus.PREPARING -> Pair("Food Preparation Started! 🍳", "Kitchen ${order.catererName} has started preparing your fresh meals for order #${order.orderId}.")
            OrderStatus.READY -> Pair("Order Ready for Dispatch 📦", "Kitchen ${order.catererName} has finished cooking order #${order.orderId}.")
            OrderStatus.ASSIGNED_DELIVERY -> Pair("Delivery Partner Assigned 🚴", "Delivery partner $driverName assigned for order #${order.orderId}.")
            OrderStatus.OUT_FOR_DELIVERY -> Pair("Out For Delivery! 🚚", "Your order #${order.orderId} is out for delivery with $driverName.")
            OrderStatus.DELIVERED -> Pair("Order Delivered! 🎉", "Order #${order.orderId} delivered successfully. Bon Appétit!")
            OrderStatus.CANCELLED -> Pair("Order Cancelled ❌", "Order #${order.orderId} was cancelled.")
        }

        val custNotif = NotificationEntity(
            id = "n_${System.currentTimeMillis()}_status",
            title = title,
            message = message,
            targetRole = UserRole.CUSTOMER
        )
        dao.insertNotification(custNotif)

        if (context != null) {
            NotificationHelper.showSystemNotification(context, title, message)
        }
    }

    suspend fun assignDeliveryBoy(
        orderId: String,
        boy: DeliveryBoyEntity,
        bartanDescription: String? = null,
        handiCount: Int = 0,
        spoonsCount: Int = 0,
        boxesCount: Int = 0,
        context: Context? = null
    ) {
        val order = dao.getOrderById(orderId)
        val finalBartanDesc = bartanDescription ?: order?.bartanDescription ?: "Handi & Serving Spoons"
        val totalContainers = handiCount + spoonsCount + boxesCount
        val isDisposable = finalBartanDesc.contains("Disposable", ignoreCase = true) || finalBartanDesc.contains("डिस्पोजेबल", ignoreCase = true) || finalBartanDesc.contains("No Bartan", ignoreCase = true)
        val hasContainers = !isDisposable && (totalContainers > 0 || bartanDescription != null)

        dao.assignDeliveryBoyWithBartan(
            orderId = orderId,
            boyId = boy.id,
            boyName = boy.name,
            boyMobile = boy.mobile,
            status = OrderStatus.ASSIGNED_DELIVERY,
            bartanDescription = finalBartanDesc,
            isBartanPending = hasContainers
        )

        if (hasContainers) {
            dao.incrementDeliveryBoyBartanCount(boy.id, if (totalContainers > 0) totalContainers else 1)

            val existingRecord = dao.getBartanRecordByOrderId(orderId)
            val record = existingRecord?.copy(
                deliveryBoyId = boy.id,
                deliveryBoyName = boy.name,
                deliveryBoyMobile = boy.mobile,
                itemsDescription = finalBartanDesc,
                handiCount = if (handiCount > 0) handiCount else existingRecord.handiCount,
                spoonsCount = if (spoonsCount > 0) spoonsCount else existingRecord.spoonsCount,
                boxesCount = if (boxesCount > 0) boxesCount else existingRecord.boxesCount,
                returnStatus = "WITH_DELIVERY_BOY",
                isCollected = false
            ) ?: BartanRecordEntity(
                id = "b_${System.currentTimeMillis()}_${orderId}",
                orderId = orderId,
                customerName = order?.customerName ?: "Customer",
                customerMobile = order?.customerMobile ?: "",
                customerAddress = order?.deliveryAddress ?: "",
                catererId = order?.catererId ?: boy.kitchenId,
                catererName = order?.catererName ?: "Kitchen",
                itemsDescription = finalBartanDesc,
                deliveryDate = order?.deliveryDate ?: "",
                deliveryBoyId = boy.id,
                deliveryBoyName = boy.name,
                deliveryBoyMobile = boy.mobile,
                isCollected = false,
                returnStatus = "WITH_DELIVERY_BOY",
                handiCount = if (handiCount > 0) handiCount else 2,
                spoonsCount = if (spoonsCount > 0) spoonsCount else 2,
                boxesCount = if (boxesCount > 0) boxesCount else 1
            )
            dao.insertBartanRecord(record)
        }

        val containerNote = if (hasContainers) " 🍲 Containers: $finalBartanDesc" else " (Disposable Packaging)"
        val title = "Delivery Partner Assigned 🚴"
        val message = "Driver ${boy.name} (${boy.mobile}) assigned to order #${orderId}.$containerNote"

        dao.insertNotification(
            NotificationEntity(
                id = "n_${System.currentTimeMillis()}_assign",
                title = title,
                message = message,
                targetRole = UserRole.CUSTOMER
            )
        )
        dao.insertNotification(
            NotificationEntity(
                id = "n_${System.currentTimeMillis()}_delboy",
                title = "New Delivery Task Assigned 📦",
                message = "Order #${orderId} assigned for ${order?.customerName ?: "Customer"}.$containerNote. Please verify kitchen handover.",
                targetRole = UserRole.DELIVERY_BOY
            )
        )

        if (context != null) {
            NotificationHelper.showSystemNotification(context, title, message)
        }
    }

    suspend fun verifyOtpAndCompleteDelivery(orderId: String, enteredOtp: String, context: Context? = null): Boolean {
        val order = dao.getOrderById(orderId) ?: return false
        if (order.deliveryOtp == enteredOtp) {
            val balanceToCollect = order.balanceAmount
            if (balanceToCollect > 0.0) {
                // Payment stays with Delivery Boy until Kitchen verifies and marks it received!
                dao.updateOrderStatusAndPayment(
                    orderId = orderId,
                    status = OrderStatus.DELIVERED,
                    paymentStatus = PaymentStatus.BALANCE_PENDING
                )
                dao.updateOrderCashCollected(orderId = orderId, cashAmount = balanceToCollect, submitted = false)
            } else {
                dao.updateOrderStatusAndPayment(
                    orderId = orderId,
                    status = OrderStatus.DELIVERED,
                    paymentStatus = PaymentStatus.FULLY_SETTLED
                )
                dao.updateOrderCashSubmitted(orderId = orderId, submitted = true, paymentStatus = PaymentStatus.FULLY_SETTLED)
            }

            val title = "Order Delivered! 🎉"
            val message = if (balanceToCollect > 0.0) {
                "Order #${orderId} delivered. Balance ₹${balanceToCollect.toInt()} cash collected by Delivery Boy."
            } else {
                "Order #${orderId} delivered successfully with verified OTP. Thank you!"
            }
            dao.insertNotification(
                NotificationEntity(
                    id = "n_${System.currentTimeMillis()}_del",
                    title = title,
                    message = message,
                    targetRole = UserRole.CUSTOMER
                )
            )

            // Notify Kitchen that delivery is done and delivery boy is carrying cash
            if (balanceToCollect > 0.0) {
                dao.insertNotification(
                    NotificationEntity(
                        id = "n_${System.currentTimeMillis()}_cash_held",
                        title = "💵 ₹${balanceToCollect.toInt()} Cash with Delivery Partner",
                        message = "Delivery boy ${order.deliveryBoyName ?: "Partner"} collected balance 50% cash (₹${balanceToCollect.toInt()}) for #${orderId}. Pending deposit to kitchen cashier.",
                        targetRole = UserRole.KITCHEN
                    )
                )
            }

            if (context != null) {
                NotificationHelper.showSystemNotification(context, title, message)
            }
            // Add Bartan Record for delivery container return (food container to be returned, not for rent)
            dao.insertBartanRecord(
                BartanRecordEntity(
                    id = "b_${System.currentTimeMillis()}",
                    orderId = orderId,
                    customerName = order.customerName,
                    customerMobile = order.customerMobile,
                    customerAddress = order.deliveryAddress,
                    catererId = order.catererId,
                    catererName = order.catererName,
                    itemsDescription = order.bartanDescription,
                    deliveryDate = order.deliveryDate,
                    deliveryBoyId = order.deliveryBoyId ?: "",
                    deliveryBoyName = order.deliveryBoyName ?: "Delivery Partner",
                    deliveryBoyMobile = order.deliveryBoyMobile ?: "",
                    isCollected = false,
                    returnStatus = "PENDING"
                )
            )
            return true
        }
        return false
    }

    suspend fun markCashReceivedByKitchen(orderId: String, context: Context? = null) {
        val order = dao.getOrderById(orderId) ?: return
        dao.updateOrderCashSubmitted(orderId = orderId, submitted = true, paymentStatus = PaymentStatus.FULLY_SETTLED)

        val title = "💵 Cash Received & Verified!"
        val message = "Kitchen has confirmed receiving ₹${order.balanceAmount.toInt()} cash from Delivery Boy ${order.deliveryBoyName ?: "Partner"} for Order #${orderId}."
        dao.insertNotification(
            NotificationEntity(
                id = "n_cash_recv_${orderId}_${System.currentTimeMillis()}",
                title = title,
                message = message,
                targetRole = UserRole.KITCHEN
            )
        )
        dao.insertNotification(
            NotificationEntity(
                id = "n_cash_db_${orderId}_${System.currentTimeMillis()}",
                title = "💵 Cash Handover Accepted!",
                message = "Kitchen cashier confirmed receiving ₹${order.balanceAmount.toInt()} for Order #${orderId}. Your liability is settled ✅",
                targetRole = UserRole.DELIVERY_BOY
            )
        )
        if (context != null) {
            NotificationHelper.showSystemNotification(context, title, message)
        }
    }

    suspend fun deliveryBoyNotifyCashHandover(orderId: String, context: Context? = null) {
        val order = dao.getOrderById(orderId) ?: return
        val title = "🛵 Cash Handover Arrived at Kitchen"
        val message = "${order.deliveryBoyName ?: "Delivery Boy"} is at the kitchen with ₹${order.balanceAmount.toInt()} cash for Order #${orderId}. Please accept & mark received."
        dao.insertNotification(
            NotificationEntity(
                id = "n_cash_arrived_${orderId}_${System.currentTimeMillis()}",
                title = title,
                message = message,
                targetRole = UserRole.KITCHEN
            )
        )
        if (context != null) {
            NotificationHelper.showSystemNotification(context, title, message)
        }
    }

    suspend fun updateCatererBookingStatus(catererId: String, isOpen: Boolean) {
        val caterer = dao.getCatererById(catererId)
        if (caterer != null) {
            dao.updateCaterer(caterer.copy(isOpenForBooking = isOpen))
        }
    }

    suspend fun updateCatererCommissionAndOfflineSettings(
        catererId: String,
        onlineCommission: Double,
        offlineCommission: Double,
        isOnlineEnabled: Boolean,
        isOfflineEnabled: Boolean
    ) {
        val caterer = dao.getCatererById(catererId)
        if (caterer != null) {
            dao.updateCaterer(
                caterer.copy(
                    onlineCommissionPercentage = onlineCommission,
                    offlineCommissionPercentage = offlineCommission,
                    isOpenForBooking = isOnlineEnabled,
                    isOfflineBookingEnabled = isOfflineEnabled
                )
            )
        }
    }

    suspend fun addMenuItem(item: MenuItemEntity) {
        dao.insertMenuItem(item)
    }

    suspend fun updateMenuItem(item: MenuItemEntity) {
        dao.insertMenuItem(item)
    }

    suspend fun deleteMenuItem(id: String) {
        dao.deleteMenuItem(id)
    }

    suspend fun updateCatererKycStatus(catererId: String, status: KycStatus, notes: String) {
        val caterer = dao.getCatererById(catererId)
        if (caterer != null) {
            dao.updateCaterer(
                caterer.copy(
                    kycStatus = status,
                    kycNotes = notes,
                    isFssaiVerified = status == KycStatus.APPROVED
                )
            )
        }
    }

    suspend fun updateCatererDocuments(
        catererId: String,
        fssaiDoc: String,
        aadhaarDoc: String,
        panDoc: String,
        bankChequeDoc: String,
        kitchenPhotoDoc: String
    ) {
        val caterer = dao.getCatererById(catererId)
        if (caterer != null) {
            dao.updateCaterer(
                caterer.copy(
                    fssaiDocUrl = fssaiDoc.ifBlank { caterer.fssaiDocUrl },
                    aadhaarDocUrl = aadhaarDoc.ifBlank { caterer.aadhaarDocUrl },
                    panDocUrl = panDoc.ifBlank { caterer.panDocUrl },
                    bankChequeDocUrl = bankChequeDoc.ifBlank { caterer.bankChequeDocUrl },
                    kitchenPhotoUrl = kitchenPhotoDoc.ifBlank { caterer.kitchenPhotoUrl },
                    kycStatus = KycStatus.PENDING,
                    kycNotes = "Re-submitted documents. Awaiting Super Admin review."
                )
            )
        }
    }

    suspend fun updateCatererDetails(caterer: CatererEntity) {
        dao.updateCaterer(caterer)
    }

    suspend fun addCaterer(caterer: CatererEntity) {
        dao.insertCaterer(caterer)
    }

    suspend fun addDeliveryBoy(boy: DeliveryBoyEntity) {
        dao.insertDeliveryBoy(boy)
    }

    suspend fun updateDeliveryBoy(boy: DeliveryBoyEntity) {
        dao.updateDeliveryBoy(boy)
    }

    suspend fun deleteDeliveryBoy(boyId: String, kitchenId: String) {
        dao.deleteDeliveryBoy(boyId, kitchenId)
    }

    fun getDeliveryBoysByKitchen(kitchenId: String): Flow<List<DeliveryBoyEntity>> {
        return dao.getDeliveryBoysByKitchen(kitchenId)
    }

    suspend fun updateBartanCollected(id: String, isCollected: Boolean, date: String) {
        dao.updateBartanCollectedFull(id, isCollected, date, if (isCollected) "RETURNED_TO_KITCHEN" else "PENDING")
        val bartans = dao.getAllBartanRecords().first()
        val target = bartans.find { it.id == id }
        if (target != null) {
            dao.updateBartanStatus(target.orderId, isCollected)
        }
    }

    suspend fun updateBartanCollectedWithStatus(id: String, isCollected: Boolean, date: String, status: String) {
        dao.updateBartanCollectedFull(id, isCollected, date, status)
    }

    fun getBartanRecordsByDeliveryBoy(deliveryBoyId: String): Flow<List<BartanRecordEntity>> {
        return dao.getBartanRecordsByDeliveryBoy(deliveryBoyId)
    }

    suspend fun reassignBartanPickupBoy(
        recordId: String,
        newBoyId: String,
        newBoyName: String,
        newBoyMobile: String,
        context: Context? = null
    ) {
        dao.updateBartanPickupBoy(
            id = recordId,
            pickupBoyId = newBoyId,
            pickupBoyName = newBoyName,
            pickupBoyMobile = newBoyMobile,
            status = "PICKUP_SCHEDULED"
        )
        val notif = NotificationEntity(
            id = "n_pickup_${recordId}_${System.currentTimeMillis()}",
            title = "🔄 Bartan Pickup Re-assigned",
            message = "Pickup assigned to $newBoyName ($newBoyMobile) for empty containers return to kitchen.",
            targetRole = UserRole.DELIVERY_BOY
        )
        dao.insertNotification(notif)
    }

    // Kitchen Utensils Master (Stock Hisaab)
    fun getUtensilsByKitchen(kitchenId: String = "caterer_1"): Flow<List<KitchenUtensilEntity>> {
        return dao.getUtensilsByKitchen(kitchenId)
    }

    suspend fun saveUtensil(utensil: KitchenUtensilEntity) {
        dao.insertUtensil(utensil)
    }

    suspend fun updateUtensil(utensil: KitchenUtensilEntity) {
        dao.updateUtensil(utensil)
    }

    suspend fun deleteUtensil(id: String) {
        dao.deleteUtensil(id)
    }

    /**
     * 9:30 AM Daily Morning Container Return Notification Trigger
     * Sends notification to the Kitchen whose containers are pending,
     * AND to the specific Delivery Boy who delivered the order.
     */
    suspend fun trigger930AmBartanMorningAlert(context: Context?): Int {
        val pendingList = dao.getPendingBartanRecordsList()
        if (pendingList.isEmpty()) return 0

        val todayDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

        // 1. Group by Caterer / Kitchen
        val byCaterer = pendingList.groupBy { it.catererId }
        byCaterer.forEach { (catererId, records) ->
            val kitchenName = records.firstOrNull()?.catererName.takeIf { !it.isNullOrBlank() } ?: "Catering Kitchen"
            val totalPending = records.size
            val summary = records.joinToString("\n") { 
                "• Order #${it.orderId}: ${it.itemsDescription} at ${it.customerName} (${it.customerMobile}) [Delivered by: ${it.deliveryBoyName}]" 
            }

            val title = "⏰ 9:30 AM Deg/Bartan Alert: $totalPending Return Pending!"
            val message = "Kitchen: $kitchenName • Food delivery degs/containers are pending collection. Assigned delivery boys have been notified."

            dao.insertNotification(
                NotificationEntity(
                    id = "n_930_kit_${catererId}_${System.currentTimeMillis()}",
                    title = title,
                    message = "$message\n\n$summary",
                    targetRole = UserRole.KITCHEN
                )
            )

            if (context != null) {
                NotificationHelper.show930AmBartanAlertForKitchen(
                    context = context,
                    kitchenName = kitchenName,
                    pendingCount = totalPending,
                    detailSummary = summary
                )
            }
        }

        // 2. Group by Delivery Boy who delivered the food
        val byDeliveryBoy = pendingList.groupBy { it.deliveryBoyId }
        byDeliveryBoy.forEach { (boyId, records) ->
            val boyName = records.firstOrNull()?.deliveryBoyName.takeIf { !it.isNullOrBlank() } ?: "Delivery Partner"
            val ordersListText = records.joinToString(", ") { "#${it.orderId} (${it.customerName})" }

            val title = "🛵 9:30 AM Pickup Task: Collect ${records.size} Deg/Bartan"
            val message = "$boyName, you delivered containers for orders: $ordersListText. Please collect them and return to the respective kitchen(s)."

            dao.insertNotification(
                NotificationEntity(
                    id = "n_930_boy_${boyId}_${System.currentTimeMillis()}",
                    title = title,
                    message = message,
                    targetRole = UserRole.DELIVERY_BOY
                )
            )

            records.forEach { rec ->
                if (context != null) {
                    NotificationHelper.show930AmBartanAlertForDeliveryBoy(
                        context = context,
                        deliveryBoyName = boyName,
                        customerName = rec.customerName,
                        utensilDescription = rec.itemsDescription,
                        address = rec.customerAddress,
                        catererName = rec.catererName
                    )
                }
                dao.updateBartanMorningAlert(rec.id, true, todayDate)
            }
        }

        return pendingList.size
    }

    suspend fun sendManualContainerReminderToDeliveryBoy(
        bartanRecordId: String?,
        orderId: String,
        deliveryBoyId: String,
        deliveryBoyName: String,
        deliveryBoyMobile: String,
        customerName: String,
        customerMobile: String,
        customerAddress: String,
        containerDescription: String,
        catererName: String = "A1 Huma Caterers",
        daysOverdue: Int = 1,
        context: Context? = null
    ): Boolean {
        val boyDisplayName = deliveryBoyName.ifBlank { "Delivery Partner" }
        val title = "🚨 URGENT: Overdue Deg/Bartan Alert ($daysOverdue Days Late)"
        val message = "Hey $boyDisplayName! Order #$orderId containers ($containerDescription) at $customerName ($customerMobile, $customerAddress) are OVERDUE for return to $catererName. Please collect and return immediately."

        // 1. Insert notification for Delivery Boy
        dao.insertNotification(
            NotificationEntity(
                id = "n_remind_boy_${deliveryBoyId.ifBlank { orderId }}_${System.currentTimeMillis()}",
                title = title,
                message = message,
                targetRole = UserRole.DELIVERY_BOY
            )
        )

        // 2. Insert notification log for Kitchen
        dao.insertNotification(
            NotificationEntity(
                id = "n_remind_kit_${orderId}_${System.currentTimeMillis()}",
                title = "🔔 Overdue Reminder Sent to $boyDisplayName",
                message = "Manual overdue alert sent to $boyDisplayName for Order #$orderId ($containerDescription). Customer: $customerName ($customerMobile).",
                targetRole = UserRole.KITCHEN
            )
        )

        // 3. Android System Notification
        if (context != null) {
            NotificationHelper.showOverdueContainerReminderForDeliveryBoy(
                context = context,
                deliveryBoyName = boyDisplayName,
                customerName = customerName,
                customerMobile = customerMobile,
                customerAddress = customerAddress,
                utensilDescription = containerDescription,
                orderId = orderId,
                catererName = catererName,
                daysOverdue = daysOverdue
            )
        }

        // 4. Update alert timestamp in bartan record if it exists
        if (!bartanRecordId.isNullOrBlank()) {
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            dao.updateBartanMorningAlert(bartanRecordId, true, todayDate)
        }

        return true
    }

    suspend fun submitOrderReview(orderId: String, rating: Float, review: String) {
        dao.updateOrderReview(orderId, rating, review)
    }

    val allReviews: Flow<List<PartnerReviewEntity>> = dao.getAllReviews()

    fun getReviewsByCaterer(catererId: String): Flow<List<PartnerReviewEntity>> {
        return dao.getReviewsByCaterer(catererId)
    }

    suspend fun getReviewForOrder(orderId: String): PartnerReviewEntity? {
        return dao.getReviewByOrderId(orderId)
    }

    suspend fun submitDetailedReview(review: PartnerReviewEntity, context: Context? = null) {
        // 1. Insert review into database
        dao.insertReview(review)

        // 2. Update Order Entity with user rating & review
        dao.updateOrderReview(review.orderId, review.overallRating, review.comment)

        // 3. Recompute kitchen partner's rating & review count
        val caterer = dao.getCatererById(review.catererId)
        if (caterer != null) {
            val allCatererReviews = dao.getReviewsListForCaterer(review.catererId)
            val avgRating = if (allCatererReviews.isNotEmpty()) {
                val sum = allCatererReviews.map { it.overallRating }.sum()
                val calc = (sum / allCatererReviews.size)
                (Math.round(calc * 10.0) / 10.0).toFloat()
            } else {
                review.overallRating
            }
            dao.updateCaterer(
                caterer.copy(
                    rating = avgRating,
                    reviewCount = allCatererReviews.size
                )
            )
        }

        // 4. Send In-App Notification to Kitchen Partner
        val starsEmoji = "⭐".repeat(review.overallRating.toInt().coerceIn(1, 5))
        val notifTitle = "New $starsEmoji Review Received!"
        val notifMessage = "${if (review.isAnonymous) "A verified customer" else review.customerName} rated ${review.catererName} ${review.overallRating}★ for Order #${review.orderId}: \"${review.comment.take(60)}\""
        
        dao.insertNotification(
            NotificationEntity(
                id = "n_${System.currentTimeMillis()}_rev",
                title = notifTitle,
                message = notifMessage,
                targetRole = UserRole.KITCHEN
            )
        )

        // 5. Send Reward notification to Customer
        dao.insertNotification(
            NotificationEntity(
                id = "n_${System.currentTimeMillis()}_cust_reward",
                title = "🎁 +50 Loyalty Points Earned!",
                message = "Thank you for rating ${review.catererName}! 50 Foodie Coins have been added to your CaterersWale wallet.",
                targetRole = UserRole.CUSTOMER
            )
        )

        if (context != null) {
            NotificationHelper.showSystemNotification(context, notifTitle, notifMessage)
        }
    }

    // User Favorites
    fun getFavoriteCatererIds(userId: String): Flow<List<String>> =
        dao.getFavoriteCatererIds(userId)

    fun getFavoritesByUser(userId: String): Flow<List<FavoriteKitchenEntity>> =
        dao.getFavoritesByUser(userId)

    suspend fun isCatererFavorite(userId: String, catererId: String): Boolean =
        dao.isFavorite(userId, catererId)

    suspend fun addFavorite(userId: String, caterer: CatererEntity) {
        dao.insertFavorite(
            FavoriteKitchenEntity(
                userId = userId,
                catererId = caterer.id,
                catererName = caterer.name,
                kitchenName = caterer.kitchenName,
                rating = caterer.rating,
                address = caterer.address,
                favoritedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeFavorite(userId: String, catererId: String) {
        dao.deleteFavorite(userId, catererId)
    }
}

