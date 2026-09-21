package com.example.util

import com.example.data.models.FoodType
import com.example.data.models.Language
import com.example.data.models.UnitType
import com.example.data.models.UserRole

object LocalizationManager {

    // ==================== ROLE & TOP BAR TRANSLATIONS ====================
    fun getRoleLabel(role: UserRole, language: Language): String {
        return when (language) {
            Language.HINDI -> when (role) {
                UserRole.CUSTOMER -> "ग्राहक"
                UserRole.KITCHEN -> "रसोई"
                UserRole.DELIVERY_BOY -> "डिलीवरी"
                UserRole.SUPER_ADMIN -> "एडमिन"
            }
            Language.HINGLISH -> when (role) {
                UserRole.CUSTOMER -> "Customer"
                UserRole.KITCHEN -> "Kitchen"
                UserRole.DELIVERY_BOY -> "Delivery"
                UserRole.SUPER_ADMIN -> "Admin"
            }
            Language.ENGLISH -> when (role) {
                UserRole.CUSTOMER -> "Customer"
                UserRole.KITCHEN -> "Kitchen"
                UserRole.DELIVERY_BOY -> "Delivery"
                UserRole.SUPER_ADMIN -> "Admin"
            }
        }
    }

    fun getRoleBadge(role: UserRole, language: Language): String {
        return when (language) {
            Language.HINDI -> when (role) {
                UserRole.CUSTOMER -> "कस्टमर"
                UserRole.KITCHEN -> "किचन"
                UserRole.DELIVERY_BOY -> "डिलीवरी"
                UserRole.SUPER_ADMIN -> "एडमिन"
            }
            else -> role.name
        }
    }

    fun getLogoutText(language: Language): String {
        return when (language) {
            Language.HINDI -> "लॉग आउट"
            Language.HINGLISH -> "Logout"
            Language.ENGLISH -> "Logout"
        }
    }

    // ==================== CATERER HEADER TRANSLATIONS ====================
    fun getFssaiVerified(licenseNo: String, language: Language): String {
        return when (language) {
            Language.HINDI -> "FSSAI प्रमाणित #$licenseNo"
            Language.HINGLISH -> "FSSAI Verified #$licenseNo"
            Language.ENGLISH -> "FSSAI Verified #$licenseNo"
        }
    }

    fun getReviewsButtonText(count: Int, language: Language): String {
        return when (language) {
            Language.HINDI -> "★ $count+ समीक्षाएं पढ़ें ›"
            Language.HINGLISH -> "★ $count+ Reviews dekhein ›"
            Language.ENGLISH -> "★ Read $count+ Reviews ›"
        }
    }

    fun getDeliveryTime(mins: Int, language: Language): String {
        return when (language) {
            Language.HINDI -> "$mins मिनट"
            Language.HINGLISH -> "$mins mins"
            Language.ENGLISH -> "$mins mins"
        }
    }

    // ==================== EVENT FOOD CALCULATOR ====================
    fun getCalculatorTitle(language: Language): String {
        return when (language) {
            Language.HINDI -> "⚡ इवेंट भोजन कैलकुलेटर (10 - 200 मेहमान)"
            Language.HINGLISH -> "⚡ Event Food Calculator (10 - 200 Mehman)"
            Language.ENGLISH -> "⚡ Event Food Calculator (10 - 200 Guests)"
        }
    }

    fun getCalculatorSubtitle(language: Language): String {
        return when (language) {
            Language.HINDI -> "शादी/पार्टी/समारोह के लिए सही मात्रा की सलाह"
            Language.HINGLISH -> "Birthday/Functions ke liye auto-suggest quantity"
            Language.ENGLISH -> "Auto-suggest quantity for functions & gatherings"
        }
    }

    fun getCalculateBtnText(isActive: Boolean, language: Language): String {
        return when (language) {
            Language.HINDI -> if (isActive) "सक्रिय ✓" else "गणना करें"
            Language.HINGLISH -> if (isActive) "Active ✓" else "Calculate"
            Language.ENGLISH -> if (isActive) "Active ✓" else "Calculate"
        }
    }

    fun getGuestsLabel(language: Language): String {
        return when (language) {
            Language.HINDI -> "मेहमान संख्या (Guests):"
            Language.HINGLISH -> "Select Guests (मेहमान):"
            Language.ENGLISH -> "Select Guests:"
        }
    }

    // ==================== DIETARY FILTER TRANSLATIONS ====================
    fun getDietaryFilterTitle(language: Language): String {
        return when (language) {
            Language.HINDI -> "खान-पान फ़िल्टर (Dietary Filter)"
            Language.HINGLISH -> "Dietary Filter (खान-पान फ़िल्टर)"
            Language.ENGLISH -> "Dietary Filter"
        }
    }

    fun getAllDishesFilter(language: Language): String {
        return when (language) {
            Language.HINDI -> "सभी व्यंजन"
            Language.HINGLISH -> "All Dishes"
            Language.ENGLISH -> "All Dishes"
        }
    }

    fun getPureVegFilter(language: Language): String {
        return when (language) {
            Language.HINDI -> "शुद्ध शाकाहारी"
            Language.HINGLISH -> "Pure Veg"
            Language.ENGLISH -> "Pure Veg"
        }
    }

    fun getNonVegFilter(language: Language): String {
        return when (language) {
            Language.HINDI -> "मांसाहारी"
            Language.HINGLISH -> "Non-Veg"
            Language.ENGLISH -> "Non-Veg"
        }
    }

    fun getResetText(language: Language): String {
        return when (language) {
            Language.HINDI -> "रीसेट करें"
            Language.HINGLISH -> "Reset"
            Language.ENGLISH -> "Reset"
        }
    }

    // ==================== CATEGORY TRANSLATIONS ====================
    fun getCategoryLabel(category: String, language: Language): String {
        if (language == Language.ENGLISH) return category
        return when (category.trim()) {
            "All" -> if (language == Language.HINDI) "सभी" else "All"
            "Chicken Biryani" -> if (language == Language.HINDI) "चिकन बिरयानी" else "Chicken Biryani"
            "Mutton Biryani" -> if (language == Language.HINDI) "मटन बिरयानी" else "Mutton Biryani"
            "Veg Biryani" -> if (language == Language.HINDI) "वेज बिरयानी" else "Veg Biryani"
            "Chicken Gravy" -> if (language == Language.HINDI) "चिकन ग्रेवी" else "Chicken Gravy"
            "Veg Gravy" -> if (language == Language.HINDI) "वेज ग्रेवी" else "Veg Gravy"
            "Chinese" -> if (language == Language.HINDI) "चाइनीज़" else "Chinese"
            "Desserts", "Desserts & Sweets" -> if (language == Language.HINDI) "मीठा / डेज़र्ट" else "Desserts / Meetha"
            "Beverages" -> if (language == Language.HINDI) "पेय पदार्थ / लस्सी" else "Beverages / Lassi"
            "Biryani & Rice" -> if (language == Language.HINDI) "बिरयानी और चावल" else "Biryani & Rice"
            "Breads / Roti" -> if (language == Language.HINDI) "रोटी / नान" else "Breads / Roti"
            "Starters" -> if (language == Language.HINDI) "स्टार्टर्स" else "Starters"
            else -> category
        }
    }

    // ==================== DISH NAMES & DESCRIPTIONS ====================
    data class DishTranslation(val nameHindi: String, val descHindi: String)

    private val dishTranslations = mapOf(
        "Special Dum Chicken Biryani" to DishTranslation(
            nameHindi = "स्पेशल दम चिकन बिरयानी",
            descHindi = "खुशबूदार बासमती चावल, रसीले चिकन और शाही मसालों से दम पर पकाई गई लज़ीज़ बिरयानी।"
        ),
        "Royal Hyderabadi Mutton Biryani" to DishTranslation(
            nameHindi = "रॉयल हैदराबादी मटन बिरयानी",
            descHindi = "केसरिया चावल, तली प्याज और मुलायम मटन के साथ पारंपरिक दम बिरयानी।"
        ),
        "Shahi Paneer Dum Biryani" to DishTranslation(
            nameHindi = "शाही पनीर दम बिरयानी",
            descHindi = "दही और खड़े मसालों में मैरीनेट ताज़ा पनीर और महकते बासमती चावल की बिरयानी।"
        ),
        "Butter Chicken (Shahi Style)" to DishTranslation(
            nameHindi = "बटर चिकन (शाही स्टाइल)",
            descHindi = "तंदूरी चिकन टिक्का और मखमली मलाईदार टमाटर-मक्खन ग्रेवी का शाही संगम।"
        ),
        "Kadhai Paneer Gravy" to DishTranslation(
            nameHindi = "कड़ाही पनीर ग्रेवी",
            descHindi = "शिमला मिर्च, प्याज और ताज़ा कुटे कड़ाही मसालों के साथ भुना हुआ पनीर।"
        ),
        "Veg Hakka Noodles" to DishTranslation(
            nameHindi = "वेज हक्का नूडल्स",
            descHindi = "क्रंची ताज़ी सब्जियों और ओरिएंटल सॉस के साथ कड़ाही में टॉस किए गए नूडल्स।"
        ),
        "Shahi Zafrani Kheer" to DishTranslation(
            nameHindi = "शाही ज़ाफ़रानी खीर",
            descHindi = "केसर, छोटी इलायची और मेवों से भरपूर गाढ़ी मलाईदार पारंपरिक चावल की खीर।"
        ),
        "Soft Gulab Jamun" to DishTranslation(
            nameHindi = "नरम शाही गुलाब जामुन",
            descHindi = "गुलाब जल की चाशनी में डूबे मुंह में घुल जाने वाले खोया के गरमा-गरम जामुन।"
        ),
        "Fresh Mint Jaljeera Lassi" to DishTranslation(
            nameHindi = "पुदीना जलजीरा लस्सी",
            descHindi = "ताज़ा पुदीना और भुने जीरे के स्वाद वाली ताजगी भरी गाढ़ी ठंडी लस्सी।"
        ),
        "Awadhi Dum Chicken Biryani" to DishTranslation(
            nameHindi = "अवधी दम चिकन बिरयानी",
            descHindi = "केवड़ा जल, केसर और धीमे दम पर पकाई गई लखनऊ की पारंपरिक शाही बिरयानी।"
        ),
        "Navratan Saffron Veg Biryani" to DishTranslation(
            nameHindi = "नवरत्न केसरिया वेज बिरयानी",
            descHindi = "मौसमी सब्जियों, मेवों, पनीर और सुनहरे केसरिया बासमती चावल की बिरयानी।"
        ),
        "Mutton Nihari (Dilli 6 Style)" to DishTranslation(
            nameHindi = "मटन निहारी (दिल्ली 6 स्टाइल)",
            descHindi = "नली और साबुत मसालों के साथ 6 घंटे धीमी आंच पर पकी दिल्ली 6 की गाढ़ी निहारी।"
        ),
        "Shahi Mutton Korma" to DishTranslation(
            nameHindi = "शाही मटन कोरमा",
            descHindi = "काजू, खसखस और जाफरान की रिच मखमली ग्रेवी में पका हुआ रसीला मटन।"
        ),
        "Dal Makhani Handi" to DishTranslation(
            nameHindi = "दाल मखनी हांडी",
            descHindi = "मक्खन और ताज़ी मलाई के साथ कोयले के दम पर रात भर पकी उड़द दाल।"
        ),
        "Khamiri Roti (Tandoor)" to DishTranslation(
            nameHindi = "खमीरी रोटी (तंदूर)",
            descHindi = "तंदूर से ताज़ा निकली फूली, नरम और गरमा-गरम मुग़लई खमीरी रोटी।"
        ),
        "Rumali Roti" to DishTranslation(
            nameHindi = "रूमाली रोटी",
            descHindi = "उलटे तवे पर बनी एकदम पतली और मुलायम पारंपरिक रूमाली रोटी।"
        ),
        "Butter Garlic Naan" to DishTranslation(
            nameHindi = "बटर गार्लिक नान",
            descHindi = "लहसुन और मक्खन की परत वाला गरमा-गरम क्रिस्पी तंदूरी नान।"
        ),
        "Shahi Tukda (Desi Ghee)" to DishTranslation(
            nameHindi = "शाही टुकड़ा (देसी घी)",
            descHindi = "गाढ़ी रबड़ी और पिस्ता-बादाम से सजा देसी घी का पारंपरिक अवधी मीठा।"
        ),
        "Angoori Rasmalai" to DishTranslation(
            nameHindi = "अंगूरी रसमलाई",
            descHindi = "केसरिया पिस्ता दूध में भीगे हुए नरम, रसीले छेने के छोटे रसगुल्ले।"
        ),
        "Moong Dal Halwa" to DishTranslation(
            nameHindi = "मूंग दाल हलवा",
            descHindi = "शुद्ध देसी घी और सूखे मेवों में धीमी आंच पर भुना हुआ मूंग दाल का हलवा।"
        )
    )

    fun getDishName(originalName: String, language: Language): String {
        if (language != Language.HINDI) return originalName
        return dishTranslations[originalName]?.nameHindi ?: originalName
    }

    fun getDishDescription(originalName: String, originalDesc: String, language: Language): String {
        if (language != Language.HINDI) return originalDesc
        return dishTranslations[originalName]?.descHindi ?: originalDesc
    }

    fun getAppetizingTag(originalTag: String?, language: Language): String? {
        if (originalTag == null) return null
        if (language != Language.HINDI) return originalTag
        return when {
            originalTag.contains("Dum Pukht Handi", ignoreCase = true) -> "🔥 दम पुख्त हांडी"
            originalTag.contains("Zafrani Mughlai Gravy", ignoreCase = true) -> "🥘 ज़ाफ़रानी मुग़लई ग्रेवी"
            originalTag.contains("Desi Ghee Shahi Sweet", ignoreCase = true) -> "🍨 देसी घी शाही मीठा"
            originalTag.contains("Clay Tandoor Fresh", ignoreCase = true) -> "🫓 तंदूर से ताज़ा"
            else -> originalTag
        }
    }

    fun getUnitLabel(unitType: UnitType, language: Language): String {
        if (language != Language.HINDI) {
            return when (unitType) {
                UnitType.KG -> "/ Kg"
                UnitType.DOZEN -> "/ Dozen"
                UnitType.LITRE -> "/ Litre"
                UnitType.PORTION -> "/ Portion"
            }
        }
        return when (unitType) {
            UnitType.KG -> "/ किग्रा"
            UnitType.DOZEN -> "/ दर्जन"
            UnitType.LITRE -> "/ लीटर"
            UnitType.PORTION -> "/ प्लेट"
        }
    }

    fun getAddToCartText(language: Language): String {
        return when (language) {
            Language.HINDI -> "कार्ट में जोड़ें"
            Language.HINGLISH -> "Add karein"
            Language.ENGLISH -> "Add to Cart"
        }
    }

    fun getViewCartText(language: Language): String {
        return when (language) {
            Language.HINDI -> "कार्ट देखें"
            Language.HINGLISH -> "View Cart"
            Language.ENGLISH -> "View Bulk Cart"
        }
    }

    fun getItemsInCartText(count: Int, language: Language): String {
        return when (language) {
            Language.HINDI -> "कार्ट में $count व्यंजन"
            Language.HINGLISH -> "Cart me $count items"
            Language.ENGLISH -> "$count Items in Deg / Box"
        }
    }

    fun getPopularCategoriesTitle(language: Language): String {
        return when (language) {
            Language.HINDI -> "श्रेणियां देखें"
            Language.HINGLISH -> "Categories dekhein"
            Language.ENGLISH -> "Explore Categories"
        }
    }

    fun getBottomNavLabel(key: String, language: Language): String {
        if (language != Language.HINDI) return key
        return when (key) {
            "Home" -> "होम"
            "Favorites" -> "पसंदीदा"
            "Cart" -> "कार्ट"
            "Orders" -> "ऑर्डर्स"
            "Profile" -> "प्रोफ़ाइल"
            else -> key
        }
    }
}
