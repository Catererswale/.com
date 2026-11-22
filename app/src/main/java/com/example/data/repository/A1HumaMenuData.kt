package com.example.data.repository

import com.example.data.models.CatererEntity
import com.example.data.models.FoodType
import com.example.data.models.KycStatus
import com.example.data.models.MenuItemEntity
import com.example.data.models.UnitType

object A1HumaMenuData {

    val caterer = CatererEntity(
        id = "caterer_1",
        name = "A1 Huma Caterers",
        kitchenName = "A1 Huma Central Kitchen",
        logoUrl = "android.resource://com.example/drawable/ic_catererswale_mark",
        bannerUrl = "android.resource://com.example/drawable/img_hero_banner_1784989627411",
        rating = 4.9f,
        reviewCount = 582,
        deliveryTimeMinutes = 45,
        minOrderAmount = 1000.0,
        deliveryCharge = 150.0,
        distanceKm = 2.4,
        fssaiLicense = "11521004000123",
        isFssaiVerified = true,
        isOpenForBooking = true,
        address = "Shop No.2, Dagdi Chawl, Sakharam Lanjekar Marg, Opp. Fatima High School, Sewri (W)",
        city = "Mumbai - 400015",
        ownerMobile = "+91 9867867155",
        kycStatus = KycStatus.APPROVED,
        aadhaarNumber = "9988 7766 5544",
        panNumber = "ABCDE1234F",
        bankAccount = "918273645012",
        bankIfsc = "HDFC0001234",
        dietaryType = "BOTH",
        kycNotes = "Veg. & Non-Veg. Biryani, Moglai, Chinese, Barbeque & Sweets | Mob: 9867867155 / 9082610992"
    )

    fun getMenuItems(): List<MenuItemEntity> {
        val list = mutableListOf<MenuItemEntity>()
        var count = 101

        fun add(
            category: String,
            name: String,
            desc: String,
            foodType: FoodType,
            unitType: UnitType,
            price: Double,
            minQty: Double = 1.0,
            isPopular: Boolean = false,
            isRecommended: Boolean = false
        ) {
            list.add(
                MenuItemEntity(
                    id = "huma_${count++}",
                    catererId = "caterer_1",
                    catererName = "A1 Huma Caterers",
                    category = category,
                    name = name,
                    description = desc,
                    imageUrl = "",
                    foodType = foodType,
                    unitType = unitType,
                    pricePerUnit = price,
                    minQuantity = minQty,
                    maxQuantity = 100.0,
                    stepQuantity = if (unitType == UnitType.KG) 0.5 else 1.0,
                    isPopular = isPopular,
                    isRecommended = isRecommended
                )
            )
        }

        // ==========================================
        // 1. DUM BIRYANI (Per kg)
        // ==========================================
        add("Dum Biryani", "Mutton Biryani", "Aromatic long-grain basmati with tender succulent mutton & authentic spices.", FoodType.NON_VEG, UnitType.KG, 1700.0, isPopular = true)
        add("Dum Biryani", "Mutton Hyderabadi Biryani", "Rich Hyderabadi kachhi dum biryani infused with saffron and fried onion.", FoodType.NON_VEG, UnitType.KG, 1800.0, isRecommended = true)
        add("Dum Biryani", "Mutton White Biryani", "Mildly spiced royal white masala mutton biryani with creamy finish.", FoodType.NON_VEG, UnitType.KG, 1800.0)
        add("Dum Biryani", "Mutton Green Biryani", "Fragrant mint and coriander haryali masala layered with tender mutton.", FoodType.NON_VEG, UnitType.KG, 1800.0)
        add("Dum Biryani", "Mutton Tandoori Biryani", "Smoky char-grilled tandoori mutton pieces layered in spiced saffron rice.", FoodType.NON_VEG, UnitType.KG, 1800.0)
        add("Dum Biryani", "Mutton Chinese Biryani", "Fusion style wok-tossed oriental masala biryani with tender mutton.", FoodType.NON_VEG, UnitType.KG, 1800.0)
        add("Dum Biryani", "Mutton Zam Zam Biryani", "Grand wedding specialty layered with mutton, boiled eggs, and rich gravy.", FoodType.NON_VEG, UnitType.KG, 1900.0, isRecommended = true)
        add("Dum Biryani", "Mutton Kepsa Biryani", "Arabian inspired mandi-style aromatic spiced rice loaded with mutton.", FoodType.NON_VEG, UnitType.KG, 2000.0)
        add("Dum Biryani", "Mutton Kashmiri Biryani", "Rich Kashmiri saffron biryani garnished with dry fruits and nuts.", FoodType.NON_VEG, UnitType.KG, 2000.0)
        add("Dum Biryani", "Mutton Boneless Biryani", "100% boneless melt-in-mouth mutton pieces slow dum cooked.", FoodType.NON_VEG, UnitType.KG, 2400.0, isPopular = true)
        add("Dum Biryani", "Mutton Tikka Biryani", "Tandoor roasted boneless mutton tikka tossed in rich dum rice.", FoodType.NON_VEG, UnitType.KG, 2500.0)

        add("Dum Biryani", "Chicken Biryani", "Mumbai style authentic fragrant basmati dum chicken biryani.", FoodType.NON_VEG, UnitType.KG, 1000.0, isPopular = true)
        add("Dum Biryani", "Chicken Kepsa Biryani", "Flavorful Arabian spiced platter rice with juicy roast chicken.", FoodType.NON_VEG, UnitType.KG, 1200.0, isPopular = true)
        add("Dum Biryani", "Chicken Seekh Biryani (Red Masala)", "Spiced tandoori chicken seekh kababs layered with aromatic rice.", FoodType.NON_VEG, UnitType.KG, 1200.0)
        add("Dum Biryani", "Chicken Malai Seekh Biryani (White Masala)", "Creamy white gravy marinated chicken seekh in rich dum rice.", FoodType.NON_VEG, UnitType.KG, 1300.0)
        add("Dum Biryani", "Chi. Cheese Malai Seekh Biryani", "Rich cheese and cream infused seekh kababs in royal dum rice.", FoodType.NON_VEG, UnitType.KG, 1500.0, isRecommended = true)
        add("Dum Biryani", "Chicken Murg Musallam Biryani", "Lavish royal preparation with chicken musallam gravy and rice.", FoodType.NON_VEG, UnitType.KG, 1300.0)
        add("Dum Biryani", "Chicken Malai Biryani", "Mild, velvet cream & cashew marinated chicken layered with basmati.", FoodType.NON_VEG, UnitType.KG, 1150.0)
        add("Dum Biryani", "Chicken Malai Tandoori Biryani", "Smoky malai tandoori chicken cooked in rich saffron dum.", FoodType.NON_VEG, UnitType.KG, 1200.0)
        add("Dum Biryani", "Chicken Malai Tikka Biryani", "Tender malai tikka chunks layered with fragrant dum rice.", FoodType.NON_VEG, UnitType.KG, 1300.0)
        add("Dum Biryani", "Chicken Kashmiri Biryani", "Sweet and aromatic spices, roasted dry fruits, and succulent chicken.", FoodType.NON_VEG, UnitType.KG, 1200.0)
        add("Dum Biryani", "Chicken Hyderabadi Biryani", "Traditional spicy kachhi dum biryani with brown onions & fresh mint.", FoodType.NON_VEG, UnitType.KG, 1100.0, isPopular = true)
        add("Dum Biryani", "Chicken Boneless Biryani", "Easy-to-serve boneless chicken cubes cooked in dum basmati rice.", FoodType.NON_VEG, UnitType.KG, 1100.0)
        add("Dum Biryani", "Chicken Tikka Biryani", "Char-grilled spicy red chicken tikka tossed with masala rice.", FoodType.NON_VEG, UnitType.KG, 1200.0)
        add("Dum Biryani", "Chicken Tandoori Biryani", "Charcoal roasted tandoori chicken with rich biryani masala.", FoodType.NON_VEG, UnitType.KG, 1100.0)
        add("Dum Biryani", "Chicken White Biryani", "Mughlai style white cream and curd based non-spicy aromatic biryani.", FoodType.NON_VEG, UnitType.KG, 1100.0)
        add("Dum Biryani", "Chicken Green Biryani", "Fresh mint, green chilly & coriander masala chicken biryani.", FoodType.NON_VEG, UnitType.KG, 1100.0)
        add("Dum Biryani", "Chicken Chinese Biryani", "Oriental spice infused chicken rice prepared in bulk catering cauldron.", FoodType.NON_VEG, UnitType.KG, 1200.0)
        add("Dum Biryani", "Chicken Zam Zam Biryani", "Loaded feast biryani with chicken, meatballs, eggs and garnishes.", FoodType.NON_VEG, UnitType.KG, 1200.0, isRecommended = true)
        add("Dum Biryani", "Chicken & Mutton Zam Zam Biryani", "Grand royal combination of chicken and mutton in one rich deg.", FoodType.NON_VEG, UnitType.KG, 1700.0, isRecommended = true)
        add("Dum Biryani", "Fish / Prawns Biryani", "Fresh sea fish or succulent prawns slow-cooked in coastal spices.", FoodType.NON_VEG, UnitType.KG, 2000.0)

        add("Dum Biryani", "Veg. Biryani", "Garden fresh vegetables dum cooked with long basmati & shahi masala.", FoodType.VEG, UnitType.KG, 700.0, isPopular = true)
        add("Dum Biryani", "Veg. Soya Bean Mix Biryani", "High protein soya chunks and seasonal veggies in dum spices.", FoodType.VEG, UnitType.KG, 750.0)
        add("Dum Biryani", "Veg. Paneer Mix Biryani", "Soft fresh paneer cubes and vegetables cooked in saffron rice.", FoodType.VEG, UnitType.KG, 800.0, isRecommended = true)
        add("Dum Biryani", "Veg. Paneer Tikka Biryani", "Tandoor roasted paneer tikka cubes layered with spiced biryani rice.", FoodType.VEG, UnitType.KG, 1000.0)
        add("Dum Biryani", "Veg. Kepsa Biryani", "Middle eastern platter rice loaded with sautéed vegetables & dry fruits.", FoodType.VEG, UnitType.KG, 1200.0)
        add("Dum Biryani", "Veg. Hyderabadi Biryani", "Authentic spicy Hyderabadi style vegetable dum biryani.", FoodType.VEG, UnitType.KG, 800.0)
        add("Dum Biryani", "Veg. Paneer Mix Hyderabadi Biryani", "Paneer and fresh vegetables in spicy Hyderabadi green herbs gravy.", FoodType.VEG, UnitType.KG, 1000.0)

        // ==========================================
        // 2. MUTTON CURRY (Per kg)
        // ==========================================
        add("Mutton Curry", "Mutton Kofta Curry", "Handcrafted seasoned minced mutton meatballs in royal rich gravy.", FoodType.NON_VEG, UnitType.KG, 2000.0, isRecommended = true)
        add("Mutton Curry", "Mutton Kheema Masala", "Finely minced mutton slow-simmered with onions, tomatoes and garam masala.", FoodType.NON_VEG, UnitType.KG, 1700.0, isPopular = true)
        add("Mutton Curry", "Mutton Green Masala", "Coriander, mint, and green chilli based rich herbal mutton gravy.", FoodType.NON_VEG, UnitType.KG, 1500.0)
        add("Mutton Curry", "Mutton White Masala", "Rich white cashew and cream based non-spicy Mughlai mutton gravy.", FoodType.NON_VEG, UnitType.KG, 1500.0)
        add("Mutton Curry", "Mutton Tandoori Masala", "Tandoori grilled mutton finished in a spicy thick tomato-onion gravy.", FoodType.NON_VEG, UnitType.KG, 1500.0)
        add("Mutton Curry", "Mutton Angara", "Smoky charcoal infused spicy red mutton gravy.", FoodType.NON_VEG, UnitType.KG, 1500.0)
        add("Mutton Curry", "Mutton Chops Masala", "Juicy mutton chops cooked in aromatic roasted onion masala.", FoodType.NON_VEG, UnitType.KG, 1500.0)
        add("Mutton Curry", "Mutton Korma", "Classic wedding shahi mutton korma with fragrant kewra essence.", FoodType.NON_VEG, UnitType.KG, 1400.0, isPopular = true)
        add("Mutton Curry", "Mutton Bhuna", "Slow-roasted tender mutton cooked down in its own rich thick juices.", FoodType.NON_VEG, UnitType.KG, 1400.0)
        add("Mutton Curry", "Mutton Dal Gosht with Rice", "Traditional Mumbai wedding dal gosht served with flavored baghara rice.", FoodType.NON_VEG, UnitType.KG, 1500.0, isRecommended = true)
        add("Mutton Curry", "Mutton Rogan Josh", "Kashmiri style aromatic curry with vibrant red gravy and spices.", FoodType.NON_VEG, UnitType.KG, 1500.0)
        add("Mutton Curry", "Mutton Kadai", "Tossed with crushed whole spices, capsicum and diced onions in iron wok.", FoodType.NON_VEG, UnitType.KG, 1500.0)
        add("Mutton Curry", "Mutton Dubba Gosht", "Baked style boneless mutton curry enriched with eggs and cashew paste.", FoodType.NON_VEG, UnitType.KG, 1700.0)
        add("Mutton Curry", "Mutton Khichda", "Slow-cooked wheat, lentils and shredded mutton porridge with pure ghee.", FoodType.NON_VEG, UnitType.KG, 2000.0, isPopular = true)
        add("Mutton Curry", "Mutton Whole Raan Tandoori", "Whole mutton leg marinated in secret spices and roasted to perfection.", FoodType.NON_VEG, UnitType.KG, 2200.0, isRecommended = true)
        add("Mutton Curry", "Mutton Whole Raan Tandoori Masala", "Whole roasted raan smothered in rich spicy masala gravy.", FoodType.NON_VEG, UnitType.KG, 2400.0)

        // ==========================================
        // 3. CHICKEN CURRY (Per kg)
        // ==========================================
        add("Chicken Curry", "Chicken Korma", "Traditional shahi chicken korma with browned onion and yogurt gravy.", FoodType.NON_VEG, UnitType.KG, 700.0, isPopular = true)
        add("Chicken Curry", "Chicken White Masala", "Delicate white Mughlai gravy made with melon seeds, cashews & cream.", FoodType.NON_VEG, UnitType.KG, 800.0)
        add("Chicken Curry", "Chicken Green Masala", "Fresh coriander and green chilli infused rich chicken gravy.", FoodType.NON_VEG, UnitType.KG, 800.0)
        add("Chicken Curry", "Chicken Angara", "Fiery red smoky chicken gravy infused with charcoal aroma.", FoodType.NON_VEG, UnitType.KG, 800.0)
        add("Chicken Curry", "Chicken Chilly (Boneless)", "Indo-Chinese style boneless spicy stir-fried chicken.", FoodType.NON_VEG, UnitType.KG, 900.0)
        add("Chicken Curry", "Chicken Makhani", "Velvety smooth butter and tomato sauce with shredded chicken.", FoodType.NON_VEG, UnitType.KG, 900.0)
        add("Chicken Curry", "Chicken Tikka Masala", "Char-grilled boneless chicken tikka simmered in spicy masala gravy.", FoodType.NON_VEG, UnitType.KG, 900.0, isPopular = true)
        add("Chicken Curry", "Butter Chicken", "All-time favorite creamy rich butter chicken in sweet & tangy gravy.", FoodType.NON_VEG, UnitType.KG, 1000.0, isPopular = true)
        add("Chicken Curry", "Chicken Hyderabadi", "Spicy coastal Hyderabadi chicken with coconut and curry leaves.", FoodType.NON_VEG, UnitType.KG, 800.0)
        add("Chicken Curry", "Chicken Kaju Masala", "Rich roasted cashew nut paste blended with chicken gravy.", FoodType.NON_VEG, UnitType.KG, 800.0)
        add("Chicken Curry", "Chicken Tandoori Masala", "Tandoori chicken pieces cooked in thick onion-tomato gravy.", FoodType.NON_VEG, UnitType.KG, 800.0)
        add("Chicken Curry", "Chicken Kadhai", "Fresh bell peppers, tomatoes and freshly pounded kadhai spices.", FoodType.NON_VEG, UnitType.KG, 800.0)
        add("Chicken Curry", "Chicken Ellaichi", "Green cardamom infused royal aromatic chicken curry.", FoodType.NON_VEG, UnitType.KG, 1000.0)
        add("Chicken Curry", "Chicken Mughlai", "Rich egg and almond enriched imperial Mughlai gravy.", FoodType.NON_VEG, UnitType.KG, 900.0)
        add("Chicken Curry", "Chicken Do Piyaza", "Prepared with double quantity of caramelized and crunchy diced onions.", FoodType.NON_VEG, UnitType.KG, 800.0)
        add("Chicken Curry", "Chicken Kheema Masala", "Finely minced fresh chicken simmered in flavorful spicy gravy.", FoodType.NON_VEG, UnitType.KG, 900.0)
        add("Chicken Curry", "Chicken Kofta Curry", "Soft minced chicken meatballs cooked in royal spiced gravy.", FoodType.NON_VEG, UnitType.KG, 1100.0)
        add("Chicken Curry", "Chicken Murg Musallam", "Whole roasted chicken served in a thick rich dry-fruit gravy.", FoodType.NON_VEG, UnitType.KG, 1000.0, isRecommended = true)

        // ==========================================
        // 4. BARBEQUE & TANDOOR
        // ==========================================
        add("Barbeque", "Chicken Seekh Kabab (20pcs)", "Min order 20 pcs. Charcoal grilled seasoned minced chicken skewers.", FoodType.NON_VEG, UnitType.PORTION, 40.0, minQty = 20.0, isPopular = true)
        add("Barbeque", "Chicken Chutney Kabab (20pcs)", "Min order 20 pcs. Stuffed with spicy mint-coriander chutney.", FoodType.NON_VEG, UnitType.PORTION, 35.0, minQty = 20.0)
        add("Barbeque", "Chicken Lucknowi Seekh (20pcs)", "Min order 20 pcs. Melt in mouth Awadhi style seasoned chicken seekh.", FoodType.NON_VEG, UnitType.PORTION, 50.0, minQty = 20.0)
        add("Barbeque", "Chicken Tandoori Leg (10Pcs)", "Min order 10 pcs. Full chicken leg quarter marinated in tandoori spices.", FoodType.NON_VEG, UnitType.PORTION, 150.0, minQty = 10.0, isPopular = true)
        add("Barbeque", "Chicken Tandoori (Full)", "Full whole chicken marinated and roasted in clay oven.", FoodType.NON_VEG, UnitType.PORTION, 600.0, minQty = 1.0, isRecommended = true)
        add("Barbeque", "Mutton Seekh Kabab (20pcs)", "Min order 20 pcs. Pure minced mutton grilled on iron skewers.", FoodType.NON_VEG, UnitType.PORTION, 120.0, minQty = 20.0, isPopular = true)
        add("Barbeque", "Mutton Lucknowi Seekh (20pcs)", "Min order 20 pcs. Authentic Galawati style soft mutton seekh.", FoodType.NON_VEG, UnitType.PORTION, 150.0, minQty = 20.0, isRecommended = true)
        add("Barbeque", "Chicken Tandoori Tikka (Per kg)", "Boneless chicken cubes in classic red tandoori marinade.", FoodType.NON_VEG, UnitType.KG, 700.0)
        add("Barbeque", "Chicken Pahadi Tikka (Per kg)", "Mountain herb, spinach and green mint marinated chicken tikka.", FoodType.NON_VEG, UnitType.KG, 800.0)
        add("Barbeque", "Chicken Reshmi Tikka (Per kg)", "Silky smooth egg and cream coated tender chicken cubes.", FoodType.NON_VEG, UnitType.KG, 800.0)
        add("Barbeque", "Chicken Malai Tikka (Per kg)", "Mild and creamy cashew cheese marinated boneless tikka.", FoodType.NON_VEG, UnitType.KG, 800.0, isPopular = true)
        add("Barbeque", "Chicken Kalimiri Tikka (Per kg)", "Crushed black pepper and hung curd marinated boneless tikka.", FoodType.NON_VEG, UnitType.KG, 800.0)
        add("Barbeque", "Paneer Tikka (Per kg)", "Fresh malai paneer cubes, capsicum and onions in tandoori masala.", FoodType.VEG, UnitType.KG, 700.0, isPopular = true)
        add("Barbeque", "Paneer Mushroom Tikka (Per kg)", "Button mushrooms and paneer skewers roasted over charcoal.", FoodType.VEG, UnitType.KG, 800.0)
        add("Barbeque", "Fish Snacks / Fish Fry (Per kg)", "Crisp spiced batter fried sea fish pieces.", FoodType.NON_VEG, UnitType.KG, 1500.0)
        add("Barbeque", "Fish Tikka (Per kg)", "Boneless fish fillets char-grilled with ajwain and lemon.", FoodType.NON_VEG, UnitType.KG, 1500.0)
        add("Barbeque", "Prawns Tandoori (Per kg)", "Jumbo fresh prawns roasted in tandoor with coastal masala.", FoodType.NON_VEG, UnitType.KG, 2000.0, isRecommended = true)

        // ==========================================
        // 5. CHICKEN & MUTTON SNACKS
        // ==========================================
        add("Chicken Snacks", "Chicken Russian Kabab (20pcs)", "Min order 20 pcs. Shredded chicken, potato & vermicelli crisp crust.", FoodType.NON_VEG, UnitType.PORTION, 30.0, minQty = 20.0, isPopular = true)
        add("Chicken Snacks", "Chicken Cheese Corn Kabab (20pcs)", "Min order 20 pcs. Melted cheese, sweet corn and tender chicken.", FoodType.NON_VEG, UnitType.PORTION, 35.0, minQty = 20.0)
        add("Chicken Snacks", "Chicken Cutlets (20pcs)", "Min order 20 pcs. Spiced minced chicken patties crumb fried crisp.", FoodType.NON_VEG, UnitType.PORTION, 35.0, minQty = 20.0)
        add("Chicken Snacks", "Chicken Lolli Pop (20pcs)", "Min order 20 pcs. Crispy chicken winglets tossed with schezwan dip.", FoodType.NON_VEG, UnitType.PORTION, 30.0, minQty = 20.0)
        add("Chicken Snacks", "Chicken Samosa (20pcs)", "Min order 20 pcs. Golden triangle patti samosa stuffed with spicy chicken.", FoodType.NON_VEG, UnitType.PORTION, 25.0, minQty = 20.0, isPopular = true)
        add("Chicken Snacks", "Chicken Leg Fry (10pcs)", "Min order 10 pcs. Deep-fried spiced crunchy chicken leg pieces.", FoodType.NON_VEG, UnitType.PORTION, 130.0, minQty = 10.0)
        add("Chicken Snacks", "Chicken Hungama Kabab (20pcs)", "Min order 20 pcs. Chef's special spicy and colorful cocktail kabab.", FoodType.NON_VEG, UnitType.PORTION, 35.0, minQty = 20.0)
        add("Chicken Snacks", "Chicken Spring Roll (20pcs)", "Min order 20 pcs. Thin crispy rolls loaded with chicken and vegetables.", FoodType.NON_VEG, UnitType.PORTION, 50.0, minQty = 20.0)
        add("Chicken Snacks", "Chicken Paper Roll (20pcs)", "Min order 20 pcs. Ultra thin delicate pastry wrapped chicken starter.", FoodType.NON_VEG, UnitType.PORTION, 50.0, minQty = 20.0)
        add("Chicken Snacks", "Chicken Cheese Shahi Roll (20pcs)", "Min order 20 pcs. Royal cheese stuffed crunchy chicken appetizer.", FoodType.NON_VEG, UnitType.PORTION, 70.0, minQty = 20.0, isRecommended = true)
        add("Chicken Snacks", "Chicken Crispy (20pcs)", "Min order 20 pcs. Golden shredded crispy chicken strips.", FoodType.NON_VEG, UnitType.PORTION, 50.0, minQty = 20.0)
        add("Chicken Snacks", "Chicken Stick (20pcs)", "Min order 20 pcs. Boneless chicken skewer starter.", FoodType.NON_VEG, UnitType.PORTION, 35.0, minQty = 20.0)
        add("Chicken Snacks", "Chicken Butter Stick (20pcs)", "Min order 20 pcs. Glazed with melted butter and garlic seasoning.", FoodType.NON_VEG, UnitType.PORTION, 40.0, minQty = 20.0)
        add("Chicken Snacks", "Chicken Sathe (20pcs)", "Min order 20 pcs. Oriental chicken satay with spicy dip.", FoodType.NON_VEG, UnitType.PORTION, 30.0, minQty = 20.0)
        add("Chicken Snacks", "Chicken Fry (Full)", "Full whole chicken seasoned and deep fried crispy.", FoodType.NON_VEG, UnitType.PORTION, 500.0, minQty = 1.0)
        add("Chicken Snacks", "Chicken Sixty Five (Per kg)", "Classic spicy curry-leaf and red chilli tempered chicken 65.", FoodType.NON_VEG, UnitType.KG, 600.0, isPopular = true)

        add("Mutton Snacks", "Mutton Cutlets (20pcs)", "Min order 20 pcs. Rich minced mutton patties coated in crispy breadcrumbs.", FoodType.NON_VEG, UnitType.PORTION, 70.0, minQty = 20.0)
        add("Mutton Snacks", "Mutton Shami Kabab (20pcs)", "Min order 20 pcs. Traditional chana dal and mutton silken shami kababs.", FoodType.NON_VEG, UnitType.PORTION, 70.0, minQty = 20.0, isRecommended = true)
        add("Mutton Snacks", "Mutton Samosa (20pcs)", "Min order 20 pcs. Crisp patti samosa stuffed with aromatic mutton kheema.", FoodType.NON_VEG, UnitType.PORTION, 60.0, minQty = 20.0, isPopular = true)
        add("Mutton Snacks", "Mutton Hungama Kabab (20pcs)", "Min order 20 pcs. Spiced mutton party appetizer bite.", FoodType.NON_VEG, UnitType.PORTION, 70.0, minQty = 20.0)
        add("Mutton Snacks", "Mutton Roll (20pcs)", "Min order 20 pcs. Flaky pastry rolls filled with minced mutton.", FoodType.NON_VEG, UnitType.PORTION, 120.0, minQty = 20.0)
        add("Mutton Snacks", "Mutton Chops Fry (1kg)", "Prime mutton chops marinated in secret masala and shallow fried.", FoodType.NON_VEG, UnitType.KG, 1600.0, isRecommended = true)

        // ==========================================
        // 6. RICE & PULAO (Per kg)
        // ==========================================
        add("Rice & Pulao", "Jeera Rice", "Cumin tempered fragrant basmati rice cooked with desi ghee.", FoodType.VEG, UnitType.KG, 300.0)
        add("Rice & Pulao", "Basmati Rice", "Steamed premium extra-long grain aromatic basmati rice.", FoodType.VEG, UnitType.KG, 400.0)
        add("Rice & Pulao", "Veg. Pulao", "Assorted vegetables and whole garam spices simmered with basmati.", FoodType.VEG, UnitType.KG, 600.0)
        add("Rice & Pulao", "Veg. Kashmiri Pulao", "Delicate fragrant rice loaded with dry fruits, saffron and fruits.", FoodType.VEG, UnitType.KG, 700.0)
        add("Rice & Pulao", "Veg. Hyderabadi Pulao", "Spicy green chilli and fresh mint flavored vegetable pulao.", FoodType.VEG, UnitType.KG, 700.0)
        add("Rice & Pulao", "Veg. Navratan Pulao", "Nine royal ingredients including paneer, nuts, raisins and veggies.", FoodType.VEG, UnitType.KG, 1000.0)
        add("Rice & Pulao", "Veg. Fried Rice", "Wok-fried rice with fine shredded vegetables and spring onions.", FoodType.VEG, UnitType.KG, 700.0)
        add("Rice & Pulao", "Chicken Pulao", "Lightly spiced chicken broth and tender pieces with long basmati.", FoodType.NON_VEG, UnitType.KG, 800.0, isPopular = true)
        add("Rice & Pulao", "Chicken Masoor Pulao", "Wholesome brown masoor dal and chicken pulao preparation.", FoodType.NON_VEG, UnitType.KG, 900.0)
        add("Rice & Pulao", "Chicken Akhni Pulao", "Rich chicken yakhni broth infused aromatic wedding pulao.", FoodType.NON_VEG, UnitType.KG, 900.0, isRecommended = true)
        add("Rice & Pulao", "Chicken Kheema Pulao", "Basmati rice cooked directly with seasoned minced chicken.", FoodType.NON_VEG, UnitType.KG, 1100.0)
        add("Rice & Pulao", "Chicken Chinese Pulao", "Fusion oriental spiced rice tossed with chicken chunks.", FoodType.NON_VEG, UnitType.KG, 1100.0)
        add("Rice & Pulao", "Chicken Corn & Capsicum Pulao", "Sweet corn and crunchy bell pepper seasoned chicken pulao.", FoodType.NON_VEG, UnitType.KG, 1100.0)
        add("Rice & Pulao", "Chicken Kashmiri Pulao", "Sweet aroma of Kashmiri spices and dry fruits with tender chicken.", FoodType.NON_VEG, UnitType.KG, 1200.0)
        add("Rice & Pulao", "Chicken Hyderabadi Pulao", "Spicy coastal Hyderabadi seasoned chicken rice.", FoodType.NON_VEG, UnitType.KG, 1000.0)
        add("Rice & Pulao", "Chicken Fried Rice", "Indo-Chinese style chicken and egg fried rice.", FoodType.NON_VEG, UnitType.KG, 1000.0)
        add("Rice & Pulao", "Mutton Pulao", "Rich mutton broth infused authentic traditional yakhni pulao.", FoodType.NON_VEG, UnitType.KG, 1500.0, isPopular = true)
        add("Rice & Pulao", "Mutton Masoor Pulao", "Traditional Mumbai Muslim wedding masoor gosht pulao.", FoodType.NON_VEG, UnitType.KG, 1600.0)
        add("Rice & Pulao", "Mutton Akhni Pulao", "Deep seasoned meat stock simmered with tender mutton and basmati.", FoodType.NON_VEG, UnitType.KG, 1600.0, isRecommended = true)
        add("Rice & Pulao", "Mutton Kheema Pulao", "Rich minced mutton cooked with fragrant basmati rice.", FoodType.NON_VEG, UnitType.KG, 1800.0)
        add("Rice & Pulao", "Mutton Chinese Pulao", "Fusion oriental seasoned mutton fried pulao.", FoodType.NON_VEG, UnitType.KG, 1600.0)
        add("Rice & Pulao", "Mutton Kashmiri Pulao", "Royal saffron dry fruit garnished celebration mutton pulao.", FoodType.NON_VEG, UnitType.KG, 2000.0)
        add("Rice & Pulao", "Mutton Hyderabadi Pulao", "Spicy green herb and mutton stock layered rice.", FoodType.NON_VEG, UnitType.KG, 1600.0)
        add("Rice & Pulao", "Mutton Corn & Capsicum Pulao", "Juicy mutton paired with golden sweet corn and capsicum.", FoodType.NON_VEG, UnitType.KG, 1700.0)

        // ==========================================
        // 7. VEGETABLE CURRY (Per kg)
        // ==========================================
        add("Vegetable Curry", "Aloo Mutter", "Potatoes and green peas simmered in homely spiced tomato gravy.", FoodType.VEG, UnitType.KG, 550.0)
        add("Vegetable Curry", "Aloo Gobi", "Classic cauliflower and potato stir fry with ginger and turmeric.", FoodType.VEG, UnitType.KG, 500.0)
        add("Vegetable Curry", "Veg. Korma", "Assorted vegetables cooked in coconut and cashew creamy sauce.", FoodType.VEG, UnitType.KG, 600.0)
        add("Vegetable Curry", "Veg. Kofta Curry", "Grated vegetable dumplings in a luscious spiced onion-tomato curry.", FoodType.VEG, UnitType.KG, 700.0)
        add("Vegetable Curry", "Veg. Makkhanwala", "Mixed vegetables in rich, sweet buttery tomato gravy.", FoodType.VEG, UnitType.KG, 800.0)
        add("Vegetable Curry", "Veg. Kolhapuri", "Fiery Kolhapuri masala vegetable curry with roasted red chillies.", FoodType.VEG, UnitType.KG, 700.0)
        add("Vegetable Curry", "Paneer Kadhai", "Fresh cottage cheese, capsicum and onions in aromatic kadhai gravy.", FoodType.VEG, UnitType.KG, 900.0, isPopular = true)
        add("Vegetable Curry", "Paneer Mutter", "Tender paneer cubes and sweet green peas in rich gravy.", FoodType.VEG, UnitType.KG, 800.0)
        add("Vegetable Curry", "Palak Paneer", "Fresh spinach puree tempered with garlic and soft paneer cubes.", FoodType.VEG, UnitType.KG, 900.0)
        add("Vegetable Curry", "Bhindi Masala", "Tender okra sautéed with caramelized onions and dry spices.", FoodType.VEG, UnitType.KG, 600.0)
        add("Vegetable Curry", "Chana Masala", "Slow-cooked chickpeas in zesty Punjabi spiced onion gravy.", FoodType.VEG, UnitType.KG, 600.0)
        add("Vegetable Curry", "Masala Dal / Dal Fry", "Yellow lentils tempered with ghee, cumin, garlic and dry chillies.", FoodType.VEG, UnitType.KG, 400.0)
        add("Vegetable Curry", "Dal Makhani / Dal Tadka", "Slow-cooked black lentils simmered overnight with butter and cream.", FoodType.VEG, UnitType.KG, 500.0, isRecommended = true)
        add("Vegetable Curry", "Chole Masala", "Authentic spiced kabuli chana preparation in thick gravy.", FoodType.VEG, UnitType.KG, 600.0)

        // ==========================================
        // 8. VEGETABLE SNACKS
        // ==========================================
        add("Vegetable Snacks", "Veg. Cutlets (20pcs)", "Min order 20 pcs. Crumb-fried vegetable and potato patties.", FoodType.VEG, UnitType.PORTION, 25.0, minQty = 20.0)
        add("Vegetable Snacks", "Veg. Cheese & Corn Kabab (20pcs)", "Min order 20 pcs. Sweet corn and melted cheese golden kababs.", FoodType.VEG, UnitType.PORTION, 40.0, minQty = 20.0, isPopular = true)
        add("Vegetable Snacks", "Veg. Pattice (20pcs)", "Min order 20 pcs. Golden flaky potato patties with spicy filling.", FoodType.VEG, UnitType.PORTION, 40.0, minQty = 20.0)
        add("Vegetable Snacks", "Veg. Samosa (20pcs)", "Min order 20 pcs. Classic crispy triangular potato samosa.", FoodType.VEG, UnitType.PORTION, 20.0, minQty = 20.0, isPopular = true)
        add("Vegetable Snacks", "Hara Bhara Kabab (20pcs)", "Min order 20 pcs. Spinach, green peas and cottage cheese pan-fried patties.", FoodType.VEG, UnitType.PORTION, 30.0, minQty = 20.0)
        add("Vegetable Snacks", "Paneer Pakoda (Per kg)", "Batter fried soft paneer fritters with mint seasoning.", FoodType.VEG, UnitType.KG, 700.0)
        add("Vegetable Snacks", "Hyderabadi Dum Aloo (Per kg)", "Baby potatoes cooked in rich Hyderabadi spicy gravy.", FoodType.VEG, UnitType.KG, 400.0)

        // ==========================================
        // 9. ROTI & BREADS
        // ==========================================
        add("Roti & Breads", "Chapatti (20pcs)", "Min order 20 pcs. Soft wheat phulkas cooked on tava.", FoodType.VEG, UnitType.PORTION, 10.0, minQty = 20.0)
        add("Roti & Breads", "Tandoori Roti (20pcs)", "Min order 20 pcs. Crisp clay oven baked whole wheat roti.", FoodType.VEG, UnitType.PORTION, 15.0, minQty = 20.0, isPopular = true)
        add("Roti & Breads", "Tava Paratha (20pcs)", "Min order 20 pcs. Layered shallow fried tava paratha.", FoodType.VEG, UnitType.PORTION, 20.0, minQty = 20.0)
        add("Roti & Breads", "Rumali Roti (20pcs)", "Min order 20 pcs. Paper-thin soft handkerchief bread cooked on inverted kadhai.", FoodType.VEG, UnitType.PORTION, 50.0, minQty = 20.0, isRecommended = true)
        add("Roti & Breads", "Lachha Paratha", "Flaky multi-layered tandoori paratha smeared with butter.", FoodType.VEG, UnitType.PORTION, 30.0, minQty = 10.0)

        // ==========================================
        // 10. SWEETS & DESSERTS
        // ==========================================
        add("Sweets", "Shahi Tukda (20pcs)", "Min order 20 pcs. Crisp fried bread soaked in saffron rabdi and dry fruits.", FoodType.VEG, UnitType.PORTION, 60.0, minQty = 20.0, isPopular = true)
        add("Sweets", "Kheer (Per ltr.)", "Traditional thick rice and milk pudding with cardamom & pistachios.", FoodType.VEG, UnitType.LITRE, 500.0, minQty = 1.0, isPopular = true)
        add("Sweets", "Sheer Khurma (Per ltr.)", "Eid celebration vermicelli pudding loaded with dates, nuts and milk.", FoodType.VEG, UnitType.LITRE, 500.0, minQty = 1.0, isRecommended = true)
        add("Sweets", "Zarda Rice (Per kg)", "Sweet golden yellow aromatic basmati with khoya, cherries and tutti frutti.", FoodType.VEG, UnitType.KG, 500.0)
        add("Sweets", "Seva Zarda (Per kg)", "Sweet roasted vermicelli cooked in pure ghee and sugar syrup with nuts.", FoodType.VEG, UnitType.KG, 600.0)

        // ==========================================
        // 11. HALWA (Per kg)
        // ==========================================
        add("Halwa", "Rava Halwa (Per kg)", "Golden semolina roasted in desi ghee with cashews and raisins.", FoodType.VEG, UnitType.KG, 700.0)
        add("Halwa", "Gajar Halwa (Per kg)", "Fresh red carrots simmered with khoya, milk and pure desi ghee.", FoodType.VEG, UnitType.KG, 600.0, isPopular = true)
        add("Halwa", "Pineapple Halwa (Per kg)", "Tangy sweet real pineapple semolina halwa with rich nuts.", FoodType.VEG, UnitType.KG, 700.0)
        add("Halwa", "Dudhi Halwa (Per kg)", "Delicate bottle gourd halwa slow-cooked in whole milk and khoya.", FoodType.VEG, UnitType.KG, 600.0)
        add("Halwa", "Pav Halwa (Per kg)", "Traditional Mumbai wedding special bread halwa with rich mawa.", FoodType.VEG, UnitType.KG, 600.0)
        add("Halwa", "Moong Dal Halwa (Per kg)", "Rich roasted yellow moong lentils simmered in generous pure ghee.", FoodType.VEG, UnitType.KG, 1400.0, isRecommended = true)

        return list
    }
}
