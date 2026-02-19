package com.example.mcpserver.mock;

import com.example.mcpserver.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Mock product data for Samsung Galaxy S series phones across 4 platforms.
 * Each product has full specs, pricing with MRP/discount, and delivery details.
 */
@Component
public class MockDataProvider {

        private static final Logger log = LoggerFactory.getLogger(MockDataProvider.class);

        private final Map<String, List<Product>> amazonProducts = new HashMap<>();
        private final Map<String, List<Product>> flipkartProducts = new HashMap<>();
        private final Map<String, List<Product>> samsungStoreProducts = new HashMap<>();
        private final Map<String, List<Product>> cromaProducts = new HashMap<>();
        private final Map<String, Product> allProductsById = new HashMap<>();
        private final Map<String, Order> orders = new HashMap<>();

        // ── Shared specs (same phone, same hardware) ──
        private static final String S24U_PROC = "Snapdragon 8 Gen 3 for Galaxy";
        private static final String S24U_DISP = "6.8\" QHD+ Dynamic AMOLED 2X, 120Hz, 2600 nits peak";
        private static final String S24U_CAM = "200MP Wide + 12MP Ultra Wide + 50MP 5x Tele + 10MP 3x Tele";
        private static final String S24U_BAT = "5000mAh, 45W Wired + 15W Wireless + 4.5W Reverse";
        private static final String S24U_OS = "Android 14, One UI 6.1 (7 years of updates)";

        private static final String S24P_PROC = "Exynos 2400";
        private static final String S24P_DISP = "6.7\" FHD+ Dynamic AMOLED 2X, 120Hz, 2600 nits peak";
        private static final String S24P_CAM = "50MP Wide + 12MP Ultra Wide + 10MP 3x Telephoto";
        private static final String S24P_BAT = "4900mAh, 45W Wired + 15W Wireless";
        private static final String S24P_OS = "Android 14, One UI 6.1 (7 years of updates)";

        private static final String S24_PROC = "Exynos 2400";
        private static final String S24_DISP = "6.2\" FHD+ Dynamic AMOLED 2X, 120Hz, 2600 nits peak";
        private static final String S24_CAM = "50MP Wide + 12MP Ultra Wide + 10MP 3x Telephoto";
        private static final String S24_BAT = "4000mAh, 25W Wired + 15W Wireless";
        private static final String S24_OS = "Android 14, One UI 6.1 (7 years of updates)";

        private static final String S23FE_PROC = "Exynos 2200";
        private static final String S23FE_DISP = "6.4\" FHD+ Dynamic AMOLED 2X, 120Hz, 1450 nits peak";
        private static final String S23FE_CAM = "50MP Wide + 12MP Ultra Wide + 8MP 3x Telephoto";
        private static final String S23FE_BAT = "4500mAh, 25W Wired + 15W Wireless";
        private static final String S23FE_OS = "Android 14, One UI 6.1 (4 years of updates)";

        public MockDataProvider() {
                log.info("📦 [DATA] Initializing MockDataProvider...");
                initializeAmazonData();
                log.info("   ✅ Amazon India — {} products loaded",
                                amazonProducts.getOrDefault("samsung", List.of()).size());
                initializeFlipkartData();
                log.info("   ✅ Flipkart — {} products loaded",
                                flipkartProducts.getOrDefault("samsung", List.of()).size());
                initializeSamsungStoreData();
                log.info("   ✅ Samsung.com India — {} products loaded",
                                samsungStoreProducts.getOrDefault("samsung", List.of()).size());
                initializeCromaData();
                log.info("   ✅ Croma — {} products loaded", cromaProducts.getOrDefault("samsung", List.of()).size());
                log.info("📦 [DATA] Total: {} unique products indexed across 4 platforms", allProductsById.size());
        }

        // ═══════════════════════════════════════════════════════
        // AMAZON INDIA — Prime delivery, HDFC/SBI/ICICI offers
        // ═══════════════════════════════════════════════════════
        private void initializeAmazonData() {
                List<Product> phones = List.of(
                                Product.builder().id("AMZ-S24U-256").name("Samsung Galaxy S24 Ultra")
                                                .platform("Amazon India").category("smartphones")
                                                .color("Titanium Gray").storage("256GB").ram("12GB")
                                                .processor(S24U_PROC).display(S24U_DISP).camera(S24U_CAM)
                                                .battery(S24U_BAT).os(S24U_OS)
                                                .price(129999).mrp(149999)
                                                .rating(4.5).reviewCount(12840).seller("Samsung Official Store")
                                                .inStock(true).stockCount(45)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹5,000 Instant Discount on HDFC Bank Cards")
                                                .emiOption("No-cost EMI from ₹10,834/month x 12")
                                                .returnPolicy("7-day replacement only")
                                                .exchangeValue("Up to ₹15,000 off on exchange").freebie("None")
                                                .deliverySpeed("2-3 days").deliveryDate(dd(2) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Amazon Logistics").build(),

                                Product.builder().id("AMZ-S24U-512").name("Samsung Galaxy S24 Ultra")
                                                .platform("Amazon India").category("smartphones")
                                                .color("Titanium Violet").storage("512GB").ram("12GB")
                                                .processor(S24U_PROC).display(S24U_DISP).camera(S24U_CAM)
                                                .battery(S24U_BAT).os(S24U_OS)
                                                .price(144999).mrp(164999)
                                                .rating(4.5).reviewCount(8920).seller("Samsung Official Store")
                                                .inStock(true).stockCount(18)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹7,000 Instant Discount on SBI Credit Cards")
                                                .emiOption("No-cost EMI from ₹12,084/month x 12")
                                                .returnPolicy("7-day replacement only")
                                                .exchangeValue("Up to ₹15,000 off on exchange").freebie("None")
                                                .deliverySpeed("3-4 days").deliveryDate(dd(3) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Amazon Logistics").build(),

                                Product.builder().id("AMZ-S24P-256").name("Samsung Galaxy S24+")
                                                .platform("Amazon India").category("smartphones")
                                                .color("Cobalt Violet").storage("256GB").ram("12GB")
                                                .processor(S24P_PROC).display(S24P_DISP).camera(S24P_CAM)
                                                .battery(S24P_BAT).os(S24P_OS)
                                                .price(79999).mrp(99999)
                                                .rating(4.4).reviewCount(6540).seller("Appario Retail").inStock(true)
                                                .stockCount(62)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹3,000 Instant Discount on ICICI Bank Cards")
                                                .emiOption("No-cost EMI from ₹6,667/month x 12")
                                                .returnPolicy("7-day replacement only")
                                                .exchangeValue("Up to ₹12,000 off on exchange").freebie("None")
                                                .deliverySpeed("2 days").deliveryDate(dd(2) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Amazon Logistics").build(),

                                Product.builder().id("AMZ-S24-256").name("Samsung Galaxy S24").platform("Amazon India")
                                                .category("smartphones")
                                                .color("Amber Yellow").storage("256GB").ram("8GB")
                                                .processor(S24_PROC).display(S24_DISP).camera(S24_CAM).battery(S24_BAT)
                                                .os(S24_OS)
                                                .price(64999).mrp(79999)
                                                .rating(4.3).reviewCount(9210).seller("Samsung Official Store")
                                                .inStock(true).stockCount(88)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹2,000 Instant Discount on Amazon Pay ICICI Card")
                                                .emiOption("No-cost EMI from ₹5,417/month x 12")
                                                .returnPolicy("7-day replacement only")
                                                .exchangeValue("Up to ₹10,000 off on exchange").freebie("None")
                                                .deliverySpeed("2 days").deliveryDate(dd(2) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Amazon Logistics").build(),

                                Product.builder().id("AMZ-S24-128").name("Samsung Galaxy S24").platform("Amazon India")
                                                .category("smartphones")
                                                .color("Onyx Black").storage("128GB").ram("8GB")
                                                .processor(S24_PROC).display(S24_DISP).camera(S24_CAM).battery(S24_BAT)
                                                .os(S24_OS)
                                                .price(59999).mrp(74999)
                                                .rating(4.3).reviewCount(11300).seller("Appario Retail").inStock(true)
                                                .stockCount(120)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹2,000 Instant Discount on Amazon Pay ICICI Card")
                                                .emiOption("No-cost EMI from ₹5,000/month x 12")
                                                .returnPolicy("7-day replacement only")
                                                .exchangeValue("Up to ₹10,000 off on exchange").freebie("None")
                                                .deliverySpeed("1-2 days").deliveryDate(dd(1) + " by 11 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Amazon Logistics").build(),

                                Product.builder().id("AMZ-S23FE-128").name("Samsung Galaxy S23 FE")
                                                .platform("Amazon India").category("smartphones")
                                                .color("Mint").storage("128GB").ram("8GB")
                                                .processor(S23FE_PROC).display(S23FE_DISP).camera(S23FE_CAM)
                                                .battery(S23FE_BAT).os(S23FE_OS)
                                                .price(29999).mrp(49999)
                                                .rating(4.2).reviewCount(15600).seller("Samsung Official Store")
                                                .inStock(true).stockCount(200)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹2,000 Instant Discount on HDFC Bank Cards")
                                                .emiOption("No-cost EMI from ₹2,500/month x 12")
                                                .returnPolicy("7-day replacement only")
                                                .exchangeValue("Up to ₹8,000 off on exchange").freebie("None")
                                                .deliverySpeed("1-2 days").deliveryDate(dd(1) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Amazon Logistics").build(),

                                Product.builder().id("AMZ-S23FE-256").name("Samsung Galaxy S23 FE")
                                                .platform("Amazon India").category("smartphones")
                                                .color("Cream").storage("256GB").ram("8GB")
                                                .processor(S23FE_PROC).display(S23FE_DISP).camera(S23FE_CAM)
                                                .battery(S23FE_BAT).os(S23FE_OS)
                                                .price(34999).mrp(54999)
                                                .rating(4.2).reviewCount(10200).seller("Appario Retail").inStock(true)
                                                .stockCount(75)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹2,000 Instant Discount on HDFC Bank Cards")
                                                .emiOption("No-cost EMI from ₹2,917/month x 12")
                                                .returnPolicy("7-day replacement only")
                                                .exchangeValue("Up to ₹8,000 off on exchange").freebie("None")
                                                .deliverySpeed("2-3 days").deliveryDate(dd(2) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Amazon Logistics").build());
                indexProducts(amazonProducts, phones);
        }

        // ═══════════════════════════════════════════════════════
        // FLIPKART — SuperCoins, Axis Bank, Ekart logistics
        // ═══════════════════════════════════════════════════════
        private void initializeFlipkartData() {
                List<Product> phones = List.of(
                                Product.builder().id("FK-S24U-256").name("Samsung Galaxy S24 Ultra")
                                                .platform("Flipkart").category("smartphones")
                                                .color("Titanium Gray").storage("256GB").ram("12GB")
                                                .processor(S24U_PROC).display(S24U_DISP).camera(S24U_CAM)
                                                .battery(S24U_BAT).os(S24U_OS)
                                                .price(127999).mrp(149999)
                                                .rating(4.6).reviewCount(18450)
                                                .seller("Samsung India (Flipkart Assured ✓)").inStock(true)
                                                .stockCount(55)
                                                .warranty("1 Year Samsung India + 1 Year Extended by Flipkart")
                                                .offers("₹8,000 off on Flipkart Axis Bank Card + ₹1,000 off with SuperCoins")
                                                .emiOption("No-cost EMI from ₹10,667/month x 12")
                                                .returnPolicy("10-day replacement policy")
                                                .exchangeValue("Up to ₹17,000 off on exchange").freebie("None")
                                                .deliverySpeed("1-2 days").deliveryDate(dd(1) + " by 11 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Ekart Logistics").build(),

                                Product.builder().id("FK-S24U-512").name("Samsung Galaxy S24 Ultra")
                                                .platform("Flipkart").category("smartphones")
                                                .color("Titanium Black").storage("512GB").ram("12GB")
                                                .processor(S24U_PROC).display(S24U_DISP).camera(S24U_CAM)
                                                .battery(S24U_BAT).os(S24U_OS)
                                                .price(141999).mrp(164999)
                                                .rating(4.6).reviewCount(12100)
                                                .seller("Samsung India (Flipkart Assured ✓)").inStock(true)
                                                .stockCount(22)
                                                .warranty("1 Year Samsung India + 1 Year Extended by Flipkart")
                                                .offers("₹10,000 off on Flipkart Axis Bank Card")
                                                .emiOption("No-cost EMI from ₹11,834/month x 12")
                                                .returnPolicy("10-day replacement policy")
                                                .exchangeValue("Up to ₹17,000 off on exchange").freebie("None")
                                                .deliverySpeed("1-2 days").deliveryDate(dd(2) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Ekart Logistics").build(),

                                Product.builder().id("FK-S24P-256").name("Samsung Galaxy S24+").platform("Flipkart")
                                                .category("smartphones")
                                                .color("Onyx Black").storage("256GB").ram("12GB")
                                                .processor(S24P_PROC).display(S24P_DISP).camera(S24P_CAM)
                                                .battery(S24P_BAT).os(S24P_OS)
                                                .price(77999).mrp(99999)
                                                .rating(4.5).reviewCount(9800).seller("RetailNet (Flipkart Assured ✓)")
                                                .inStock(true).stockCount(78)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹5,000 off on Flipkart Axis Bank Card + ₹1,500 off with SuperCoins")
                                                .emiOption("No-cost EMI from ₹6,500/month x 12")
                                                .returnPolicy("10-day replacement policy")
                                                .exchangeValue("Up to ₹14,000 off on exchange").freebie("None")
                                                .deliverySpeed("1 day").deliveryDate(dd(1) + " by 7 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Ekart Logistics").build(),

                                Product.builder().id("FK-S24-256").name("Samsung Galaxy S24").platform("Flipkart")
                                                .category("smartphones")
                                                .color("Cobalt Violet").storage("256GB").ram("8GB")
                                                .processor(S24_PROC).display(S24_DISP).camera(S24_CAM).battery(S24_BAT)
                                                .os(S24_OS)
                                                .price(62999).mrp(79999)
                                                .rating(4.4).reviewCount(14200)
                                                .seller("Samsung India (Flipkart Assured ✓)").inStock(true)
                                                .stockCount(130)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹3,000 off on Flipkart Axis Bank Card")
                                                .emiOption("No-cost EMI from ₹5,250/month x 12")
                                                .returnPolicy("10-day replacement policy")
                                                .exchangeValue("Up to ₹12,000 off on exchange").freebie("None")
                                                .deliverySpeed("Tomorrow").deliveryDate(dd(1) + " by 7 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Ekart Logistics").build(),

                                Product.builder().id("FK-S24-128").name("Samsung Galaxy S24").platform("Flipkart")
                                                .category("smartphones")
                                                .color("Marble Gray").storage("128GB").ram("8GB")
                                                .processor(S24_PROC).display(S24_DISP).camera(S24_CAM).battery(S24_BAT)
                                                .os(S24_OS)
                                                .price(57999).mrp(74999)
                                                .rating(4.4).reviewCount(16500).seller("RetailNet (Flipkart Assured ✓)")
                                                .inStock(true).stockCount(200)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹3,000 off on Flipkart Axis Bank Card")
                                                .emiOption("No-cost EMI from ₹4,834/month x 12")
                                                .returnPolicy("10-day replacement policy")
                                                .exchangeValue("Up to ₹12,000 off on exchange").freebie("None")
                                                .deliverySpeed("Tomorrow").deliveryDate(dd(1) + " by 7 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Ekart Logistics").build(),

                                Product.builder().id("FK-S23FE-128").name("Samsung Galaxy S23 FE").platform("Flipkart")
                                                .category("smartphones")
                                                .color("Cream").storage("128GB").ram("8GB")
                                                .processor(S23FE_PROC).display(S23FE_DISP).camera(S23FE_CAM)
                                                .battery(S23FE_BAT).os(S23FE_OS)
                                                .price(27999).mrp(49999)
                                                .rating(4.3).reviewCount(22300)
                                                .seller("Samsung India (Flipkart Assured ✓)").inStock(true)
                                                .stockCount(300)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹2,500 off on Flipkart Axis Bank Card + 5% Cashback on Flipkart Pay")
                                                .emiOption("No-cost EMI from ₹2,334/month x 12")
                                                .returnPolicy("10-day replacement policy")
                                                .exchangeValue("Up to ₹10,000 off on exchange").freebie("None")
                                                .deliverySpeed("Tomorrow").deliveryDate(dd(1) + " by 7 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Ekart Logistics").build(),

                                Product.builder().id("FK-S23FE-256").name("Samsung Galaxy S23 FE").platform("Flipkart")
                                                .category("smartphones")
                                                .color("Mint").storage("256GB").ram("8GB")
                                                .processor(S23FE_PROC).display(S23FE_DISP).camera(S23FE_CAM)
                                                .battery(S23FE_BAT).os(S23FE_OS)
                                                .price(32999).mrp(54999)
                                                .rating(4.3).reviewCount(14800).seller("RetailNet (Flipkart Assured ✓)")
                                                .inStock(true).stockCount(110)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹2,500 off on Flipkart Axis Bank Card")
                                                .emiOption("No-cost EMI from ₹2,750/month x 12")
                                                .returnPolicy("10-day replacement policy")
                                                .exchangeValue("Up to ₹10,000 off on exchange").freebie("None")
                                                .deliverySpeed("1-2 days").deliveryDate(dd(2) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(true).deliveryPartner("Ekart Logistics").build());
                indexProducts(flipkartProducts, phones);
        }

        // ═══════════════════════════════════════════════════════
        // SAMSUNG.COM INDIA — Exclusive colors, freebies, Samsung Care+
        // ═══════════════════════════════════════════════════════
        private void initializeSamsungStoreData() {
                List<Product> phones = List.of(
                                Product.builder().id("SS-S24U-256").name("Samsung Galaxy S24 Ultra")
                                                .platform("Samsung.com India").category("smartphones")
                                                .color("Titanium Yellow (Exclusive)").storage("256GB").ram("12GB")
                                                .processor(S24U_PROC).display(S24U_DISP).camera(S24U_CAM)
                                                .battery(S24U_BAT).os(S24U_OS)
                                                .price(131999).mrp(149999)
                                                .rating(4.7).reviewCount(5200).seller("Samsung India Direct")
                                                .inStock(true).stockCount(30)
                                                .warranty("1 Year Samsung India Warranty + Free Samsung Care+ (1 Year)")
                                                .offers("₹5,000 Samsung Upgrade Bonus")
                                                .emiOption("Samsung Finance+ EMI from ₹11,000/month x 12")
                                                .returnPolicy("15-day return with full refund")
                                                .exchangeValue("Up to ₹12,000 via Samsung SmartSwitch trade-in")
                                                .freebie("Galaxy Buds2 Pro (worth ₹17,999)")
                                                .deliverySpeed("3-4 days").deliveryDate(dd(3) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("BlueDart").build(),

                                Product.builder().id("SS-S24U-512").name("Samsung Galaxy S24 Ultra")
                                                .platform("Samsung.com India").category("smartphones")
                                                .color("Titanium Green (Exclusive)").storage("512GB").ram("12GB")
                                                .processor(S24U_PROC).display(S24U_DISP).camera(S24U_CAM)
                                                .battery(S24U_BAT).os(S24U_OS)
                                                .price(146999).mrp(164999)
                                                .rating(4.7).reviewCount(3800).seller("Samsung India Direct")
                                                .inStock(true).stockCount(12)
                                                .warranty("1 Year Samsung India Warranty + Free Samsung Care+ (1 Year)")
                                                .offers("₹7,000 Samsung Upgrade Bonus")
                                                .emiOption("Samsung Finance+ EMI from ₹12,250/month x 12")
                                                .returnPolicy("15-day return with full refund")
                                                .exchangeValue("Up to ₹12,000 via Samsung SmartSwitch trade-in")
                                                .freebie("Galaxy Buds2 Pro (worth ₹17,999)")
                                                .deliverySpeed("3-5 days").deliveryDate(dd(4) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("BlueDart").build(),

                                Product.builder().id("SS-S24P-256").name("Samsung Galaxy S24+")
                                                .platform("Samsung.com India").category("smartphones")
                                                .color("Sandstone Orange (Exclusive)").storage("256GB").ram("12GB")
                                                .processor(S24P_PROC).display(S24P_DISP).camera(S24P_CAM)
                                                .battery(S24P_BAT).os(S24P_OS)
                                                .price(81999).mrp(99999)
                                                .rating(4.6).reviewCount(3100).seller("Samsung India Direct")
                                                .inStock(true).stockCount(25)
                                                .warranty("1 Year Samsung India Warranty + Free Samsung Care+ (1 Year)")
                                                .offers("₹3,000 Samsung Upgrade Bonus")
                                                .emiOption("Samsung Finance+ EMI from ₹6,834/month x 12")
                                                .returnPolicy("15-day return with full refund")
                                                .exchangeValue("Up to ₹10,000 via Samsung SmartSwitch trade-in")
                                                .freebie("Galaxy Buds FE (worth ₹6,999)")
                                                .deliverySpeed("3-4 days").deliveryDate(dd(3) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("BlueDart").build(),

                                Product.builder().id("SS-S24-256").name("Samsung Galaxy S24")
                                                .platform("Samsung.com India").category("smartphones")
                                                .color("Amber Yellow").storage("256GB").ram("8GB")
                                                .processor(S24_PROC).display(S24_DISP).camera(S24_CAM).battery(S24_BAT)
                                                .os(S24_OS)
                                                .price(66999).mrp(79999)
                                                .rating(4.5).reviewCount(4200).seller("Samsung India Direct")
                                                .inStock(true).stockCount(40)
                                                .warranty("1 Year Samsung India Warranty + Free Samsung Care+ (1 Year)")
                                                .offers("₹2,000 Samsung Upgrade Bonus")
                                                .emiOption("Samsung Finance+ EMI from ₹5,584/month x 12")
                                                .returnPolicy("15-day return with full refund")
                                                .exchangeValue("Up to ₹8,000 via Samsung SmartSwitch trade-in")
                                                .freebie("Galaxy Fit3 (worth ₹3,999)")
                                                .deliverySpeed("3 days").deliveryDate(dd(3) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("BlueDart").build(),

                                Product.builder().id("SS-S24-128").name("Samsung Galaxy S24")
                                                .platform("Samsung.com India").category("smartphones")
                                                .color("Onyx Black").storage("128GB").ram("8GB")
                                                .processor(S24_PROC).display(S24_DISP).camera(S24_CAM).battery(S24_BAT)
                                                .os(S24_OS)
                                                .price(61999).mrp(74999)
                                                .rating(4.5).reviewCount(5600).seller("Samsung India Direct")
                                                .inStock(true).stockCount(60)
                                                .warranty("1 Year Samsung India Warranty + Free Samsung Care+ (1 Year)")
                                                .offers("₹2,000 Samsung Upgrade Bonus")
                                                .emiOption("Samsung Finance+ EMI from ₹5,167/month x 12")
                                                .returnPolicy("15-day return with full refund")
                                                .exchangeValue("Up to ₹8,000 via Samsung SmartSwitch trade-in")
                                                .freebie("Galaxy Fit3 (worth ₹3,999)")
                                                .deliverySpeed("3 days").deliveryDate(dd(3) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("BlueDart").build(),

                                Product.builder().id("SS-S23FE-128").name("Samsung Galaxy S23 FE")
                                                .platform("Samsung.com India").category("smartphones")
                                                .color("Graphite").storage("128GB").ram("8GB")
                                                .processor(S23FE_PROC).display(S23FE_DISP).camera(S23FE_CAM)
                                                .battery(S23FE_BAT).os(S23FE_OS)
                                                .price(30999).mrp(49999)
                                                .rating(4.4).reviewCount(6800).seller("Samsung India Direct")
                                                .inStock(true).stockCount(100)
                                                .warranty("1 Year Samsung India Warranty + Free Samsung Care+ (1 Year)")
                                                .offers("₹2,000 Samsung Upgrade Bonus")
                                                .emiOption("Samsung Finance+ EMI from ₹2,584/month x 12")
                                                .returnPolicy("15-day return with full refund")
                                                .exchangeValue("Up to ₹6,000 via Samsung SmartSwitch trade-in")
                                                .freebie("None")
                                                .deliverySpeed("2-3 days").deliveryDate(dd(2) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("BlueDart").build(),

                                Product.builder().id("SS-S23FE-256").name("Samsung Galaxy S23 FE")
                                                .platform("Samsung.com India").category("smartphones")
                                                .color("Mint").storage("256GB").ram("8GB")
                                                .processor(S23FE_PROC).display(S23FE_DISP).camera(S23FE_CAM)
                                                .battery(S23FE_BAT).os(S23FE_OS)
                                                .price(35999).mrp(54999)
                                                .rating(4.4).reviewCount(4100).seller("Samsung India Direct")
                                                .inStock(true).stockCount(50)
                                                .warranty("1 Year Samsung India Warranty + Free Samsung Care+ (1 Year)")
                                                .offers("₹2,000 Samsung Upgrade Bonus")
                                                .emiOption("Samsung Finance+ EMI from ₹3,000/month x 12")
                                                .returnPolicy("15-day return with full refund")
                                                .exchangeValue("Up to ₹6,000 via Samsung SmartSwitch trade-in")
                                                .freebie("None")
                                                .deliverySpeed("3-4 days").deliveryDate(dd(4) + " by 9 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("BlueDart").build());
                indexProducts(samsungStoreProducts, phones);
        }

        // ═══════════════════════════════════════════════════════
        // CROMA — HDFC + Croma Rewards, Bajaj Finserv, in-store
        // ═══════════════════════════════════════════════════════
        private void initializeCromaData() {
                List<Product> phones = List.of(
                                Product.builder().id("CR-S24U-256").name("Samsung Galaxy S24 Ultra").platform("Croma")
                                                .category("smartphones")
                                                .color("Titanium Gray").storage("256GB").ram("12GB")
                                                .processor(S24U_PROC).display(S24U_DISP).camera(S24U_CAM)
                                                .battery(S24U_BAT).os(S24U_OS)
                                                .price(129999).mrp(149999)
                                                .rating(4.4).reviewCount(3200).seller("Croma (Tata Digital)")
                                                .inStock(true).stockCount(20)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹6,000 Instant Discount on HDFC Bank Cards + Extra 5% off with Croma Rewards")
                                                .emiOption("No-cost EMI from ₹10,834/month x 12 via Bajaj Finserv")
                                                .returnPolicy("7-day DOA replacement")
                                                .exchangeValue("Up to ₹13,000 off (in-store exchange only)")
                                                .freebie("None")
                                                .deliverySpeed("2-3 days").deliveryDate(dd(2) + " by 8 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("Croma Delivery / BlueDart")
                                                .build(),

                                Product.builder().id("CR-S24U-512").name("Samsung Galaxy S24 Ultra").platform("Croma")
                                                .category("smartphones")
                                                .color("Titanium Violet").storage("512GB").ram("12GB")
                                                .processor(S24U_PROC).display(S24U_DISP).camera(S24U_CAM)
                                                .battery(S24U_BAT).os(S24U_OS)
                                                .price(145999).mrp(164999)
                                                .rating(4.4).reviewCount(1800).seller("Croma (Tata Digital)")
                                                .inStock(true).stockCount(8)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹8,000 Instant Discount on HDFC Bank Cards + Extra 5% off with Croma Rewards")
                                                .emiOption("No-cost EMI from ₹12,167/month x 12 via Bajaj Finserv")
                                                .returnPolicy("7-day DOA replacement")
                                                .exchangeValue("Up to ₹13,000 off (in-store exchange only)")
                                                .freebie("None")
                                                .deliverySpeed("3-4 days").deliveryDate(dd(3) + " by 8 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("Croma Delivery / BlueDart")
                                                .build(),

                                Product.builder().id("CR-S24P-256").name("Samsung Galaxy S24+").platform("Croma")
                                                .category("smartphones")
                                                .color("Cobalt Violet").storage("256GB").ram("12GB")
                                                .processor(S24P_PROC).display(S24P_DISP).camera(S24P_CAM)
                                                .battery(S24P_BAT).os(S24P_OS)
                                                .price(79999).mrp(99999)
                                                .rating(4.3).reviewCount(2100).seller("Croma (Tata Digital)")
                                                .inStock(true).stockCount(15)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹4,000 Instant Discount on HDFC Bank Cards + 5% off with Croma Rewards")
                                                .emiOption("No-cost EMI from ₹6,667/month x 12 via Bajaj Finserv")
                                                .returnPolicy("7-day DOA replacement")
                                                .exchangeValue("Up to ₹10,000 off (in-store exchange only)")
                                                .freebie("None")
                                                .deliverySpeed("2-3 days").deliveryDate(dd(2) + " by 8 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("Croma Delivery / BlueDart")
                                                .build(),

                                Product.builder().id("CR-S24-256").name("Samsung Galaxy S24").platform("Croma")
                                                .category("smartphones")
                                                .color("Amber Yellow").storage("256GB").ram("8GB")
                                                .processor(S24_PROC).display(S24_DISP).camera(S24_CAM).battery(S24_BAT)
                                                .os(S24_OS)
                                                .price(64999).mrp(79999)
                                                .rating(4.3).reviewCount(3500).seller("Croma (Tata Digital)")
                                                .inStock(true).stockCount(35)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹3,000 Instant Discount on HDFC Bank Cards + 5% off with Croma Rewards")
                                                .emiOption("No-cost EMI from ₹5,417/month x 12 via Bajaj Finserv")
                                                .returnPolicy("7-day DOA replacement")
                                                .exchangeValue("Up to ₹8,000 off (in-store exchange only)")
                                                .freebie("None")
                                                .deliverySpeed("2 days").deliveryDate(dd(2) + " by 8 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("Croma Delivery / BlueDart")
                                                .build(),

                                Product.builder().id("CR-S24-128").name("Samsung Galaxy S24").platform("Croma")
                                                .category("smartphones")
                                                .color("Onyx Black").storage("128GB").ram("8GB")
                                                .processor(S24_PROC).display(S24_DISP).camera(S24_CAM).battery(S24_BAT)
                                                .os(S24_OS)
                                                .price(59999).mrp(74999)
                                                .rating(4.3).reviewCount(4200).seller("Croma (Tata Digital)")
                                                .inStock(true).stockCount(50)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹2,500 Instant Discount on HDFC Bank Cards + 5% off with Croma Rewards")
                                                .emiOption("No-cost EMI from ₹5,000/month x 12 via Bajaj Finserv")
                                                .returnPolicy("7-day DOA replacement")
                                                .exchangeValue("Up to ₹8,000 off (in-store exchange only)")
                                                .freebie("None")
                                                .deliverySpeed("2 days").deliveryDate(dd(2) + " by 8 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("Croma Delivery / BlueDart")
                                                .build(),

                                Product.builder().id("CR-S23FE-128").name("Samsung Galaxy S23 FE").platform("Croma")
                                                .category("smartphones")
                                                .color("Mint").storage("128GB").ram("8GB")
                                                .processor(S23FE_PROC).display(S23FE_DISP).camera(S23FE_CAM)
                                                .battery(S23FE_BAT).os(S23FE_OS)
                                                .price(28999).mrp(49999)
                                                .rating(4.2).reviewCount(5400).seller("Croma (Tata Digital)")
                                                .inStock(true).stockCount(80)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹2,000 Instant Discount on HDFC Bank Cards + 5% off with Croma Rewards")
                                                .emiOption("No-cost EMI from ₹2,417/month x 12 via Bajaj Finserv")
                                                .returnPolicy("7-day DOA replacement")
                                                .exchangeValue("Up to ₹6,000 off (in-store exchange only)")
                                                .freebie("None")
                                                .deliverySpeed("2 days").deliveryDate(dd(2) + " by 8 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("Croma Delivery / BlueDart")
                                                .build(),

                                Product.builder().id("CR-S23FE-256").name("Samsung Galaxy S23 FE").platform("Croma")
                                                .category("smartphones")
                                                .color("Cream").storage("256GB").ram("8GB")
                                                .processor(S23FE_PROC).display(S23FE_DISP).camera(S23FE_CAM)
                                                .battery(S23FE_BAT).os(S23FE_OS)
                                                .price(33999).mrp(54999)
                                                .rating(4.2).reviewCount(2900).seller("Croma (Tata Digital)")
                                                .inStock(true).stockCount(40)
                                                .warranty("1 Year Samsung India Warranty")
                                                .offers("₹2,000 Instant Discount on HDFC Bank Cards + 5% off with Croma Rewards")
                                                .emiOption("No-cost EMI from ₹2,834/month x 12 via Bajaj Finserv")
                                                .returnPolicy("7-day DOA replacement")
                                                .exchangeValue("Up to ₹6,000 off (in-store exchange only)")
                                                .freebie("None")
                                                .deliverySpeed("2-3 days").deliveryDate(dd(3) + " by 8 PM")
                                                .deliveryCharge(0)
                                                .codAvailable(false).deliveryPartner("Croma Delivery / BlueDart")
                                                .build());
                indexProducts(cromaProducts, phones);
        }

        // ═══════════════════════════════════════════════════════
        // Index & Search Helpers
        // ═══════════════════════════════════════════════════════
        private void indexProducts(Map<String, List<Product>> platform, List<Product> phones) {
                platform.put("samsung", phones);
                platform.put("galaxy", phones);
                platform.put("phone", phones);
                platform.put("s24", phones.subList(0, 5));
                platform.put("s24 ultra", phones.subList(0, 2));
                platform.put("s24+", List.of(phones.get(2)));
                platform.put("s24 plus", List.of(phones.get(2)));
                platform.put("s23", phones.subList(5, 7));
                platform.put("s23 fe", phones.subList(5, 7));
                phones.forEach(p -> allProductsById.put(p.getId(), p));
        }

        public List<Product> searchAmazon(String q) {
                return searchProducts(amazonProducts, q);
        }

        public List<Product> searchFlipkart(String q) {
                return searchProducts(flipkartProducts, q);
        }

        public List<Product> searchSamsungStore(String q) {
                return searchProducts(samsungStoreProducts, q);
        }

        public List<Product> searchCroma(String q) {
                return searchProducts(cromaProducts, q);
        }

        public Map<String, List<Product>> searchAllPlatforms(String query) {
                Map<String, List<Product>> r = new LinkedHashMap<>();
                var a = searchAmazon(query);
                var f = searchFlipkart(query);
                var s = searchSamsungStore(query);
                var c = searchCroma(query);
                if (!a.isEmpty())
                        r.put("Amazon India", a);
                if (!f.isEmpty())
                        r.put("Flipkart", f);
                if (!s.isEmpty())
                        r.put("Samsung.com India", s);
                if (!c.isEmpty())
                        r.put("Croma", c);
                return r;
        }

        private List<Product> searchProducts(Map<String, List<Product>> map, String query) {
                String q = query.toLowerCase().trim();
                if (map.containsKey(q))
                        return map.get(q);
                Set<Product> matched = new LinkedHashSet<>();
                for (var e : map.entrySet()) {
                        if (e.getKey().contains(q) || q.contains(e.getKey()))
                                matched.addAll(e.getValue());
                }
                if (matched.isEmpty()) {
                        for (var products : map.values()) {
                                for (var p : products) {
                                        if (p.getName().toLowerCase().contains(q)
                                                        || p.getProcessor().toLowerCase().contains(q))
                                                matched.add(p);
                                }
                        }
                }
                return new ArrayList<>(matched);
        }

        // ── Product & Order ──
        public Optional<Product> getProductById(String id) {
                return Optional.ofNullable(allProductsById.get(id));
        }

        public Order placeOrder(String productId, int quantity, String customerName, String address) {
                Product p = allProductsById.get(productId);
                if (p == null || !p.isInStock())
                        return null;
                String orderId = "ORD-" + System.currentTimeMillis();
                Order order = new Order(orderId, productId,
                                p.getName() + " (" + p.getColor() + ", " + p.getStorage() + ", " + p.getRam() + " RAM)",
                                p.getPlatform(), quantity, p.getPrice(), p.getPrice() * quantity,
                                customerName, address, p.getDeliveryDate(), p.getDeliveryPartner(), "CONFIRMED",
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
                orders.put(orderId, order);
                return order;
        }

        public Optional<Order> getOrderById(String orderId) {
                return Optional.ofNullable(orders.get(orderId));
        }

        private String dd(int d) {
                return LocalDateTime.now().plusDays(d).format(DateTimeFormatter.ofPattern("dd MMM"));
        }

        public record Order(String orderId, String productId, String productName, String platform,
                        int quantity, double unitPrice, double totalAmount,
                        String customerName, String deliveryAddress,
                        String expectedDelivery, String deliveryPartner, String status, String orderTime) {
                public String toSummary() {
                        return String.format("""
                                        ✅ ORDER CONFIRMED
                                        ┌──────────────────────────────────────
                                        │ 🆔 Order ID: %s
                                        │ 📱 %s
                                        │ 🏪 Platform: %s
                                        │ 📦 Qty: %d × ₹%,.0f = ₹%,.0f
                                        │ 👤 Customer: %s
                                        │ 📍 Address: %s
                                        │ 📅 Expected: %s
                                        │ 🚚 Via: %s
                                        │ ⏰ Ordered: %s
                                        │ 📋 Status: %s
                                        └──────────────────────────────────────
                                        """, orderId, productName, platform,
                                        quantity, unitPrice, totalAmount,
                                        customerName, deliveryAddress,
                                        expectedDelivery, deliveryPartner, orderTime, status);
                }
        }
}
