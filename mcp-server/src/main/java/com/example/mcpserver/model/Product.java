package com.example.mcpserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Samsung Galaxy phone listing from an e-commerce platform.
 * Contains specs, pricing, offers, and delivery details for purchase decisions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    // ── Identity ──
    private String id;
    private String name;
    private String platform;
    private String category;

    // ── Variant ──
    private String color;
    private String storage;
    private String ram;

    // ── Specs ──
    private String processor;
    private String display;
    private String camera;
    private String battery;
    private String os;

    // ── Pricing ──
    private double price; // Selling price
    private double mrp; // Original MRP
    @Builder.Default
    private String currency = "INR";

    // ── Ratings ──
    private double rating;
    private int reviewCount;

    // ── Seller ──
    private String seller;
    private boolean inStock;
    private int stockCount;

    // ── Offers ──
    private String warranty;
    private String offers;
    private String emiOption;
    private String returnPolicy;
    private String exchangeValue;
    private String freebie; // "Galaxy Buds2 Pro", "None"

    // ── Delivery ──
    private String deliverySpeed; // "1 day", "2-3 days"
    private String deliveryDate; // "22 Feb by 9 PM"
    private double deliveryCharge; // 0.0 = free
    private boolean codAvailable; // Cash on delivery
    private String deliveryPartner; // "Amazon Logistics", "Ekart", "BlueDart"

    public String getFormattedPrice() {
        return String.format("₹%,.0f", price);
    }

    public String getFormattedMrp() {
        return String.format("₹%,.0f", mrp);
    }

    public int getDiscountPercent() {
        if (mrp <= 0)
            return 0;
        return (int) Math.round(((mrp - price) / mrp) * 100);
    }

    public String toSummary() {
        return String.format("%s (%s, %s, %s) — %s (%d%% off) on %s | ⭐%.1f | 🚚%s | %s",
                name, color, storage, ram, getFormattedPrice(), getDiscountPercent(),
                platform, rating, deliverySpeed, inStock ? "In Stock" : "Out of Stock");
    }

    public String toDetailedCard() {
        StringBuilder sb = new StringBuilder();
        sb.append("┌───────────────────────────────────────────────────────────\n");
        sb.append("│ 🏷️ ID: ").append(id).append("\n");
        sb.append("│ 📱 ").append(name).append(" (").append(color).append(", ").append(storage).append(")\n");
        sb.append("│\n");

        // Specs
        sb.append("│ ⚙️ SPECS:\n");
        sb.append("│    Processor: ").append(processor).append("\n");
        sb.append("│    RAM: ").append(ram).append(" | Storage: ").append(storage).append("\n");
        sb.append("│    Display: ").append(display).append("\n");
        sb.append("│    Camera: ").append(camera).append("\n");
        sb.append("│    Battery: ").append(battery).append("\n");
        sb.append("│    OS: ").append(os).append("\n");
        sb.append("│\n");

        // Pricing
        sb.append("│ 💰 PRICING:\n");
        sb.append("│    Price: ").append(getFormattedPrice()).append("  (MRP: ").append(getFormattedMrp())
                .append(", ").append(getDiscountPercent()).append("% off)\n");
        if (offers != null)
            sb.append("│    Offers: ").append(offers).append("\n");
        if (emiOption != null)
            sb.append("│    EMI: ").append(emiOption).append("\n");
        if (exchangeValue != null)
            sb.append("│    Exchange: ").append(exchangeValue).append("\n");
        if (freebie != null && !freebie.equalsIgnoreCase("None"))
            sb.append("│    🎁 Freebie: ").append(freebie).append("\n");
        sb.append("│\n");

        // Delivery
        sb.append("│ 🚚 DELIVERY:\n");
        sb.append("│    Speed: ").append(deliverySpeed).append("\n");
        sb.append("│    Expected: ").append(deliveryDate).append("\n");
        sb.append("│    Charge: ").append(deliveryCharge == 0 ? "FREE" : String.format("₹%.0f", deliveryCharge))
                .append("\n");
        sb.append("│    COD: ").append(codAvailable ? "Available" : "Not Available").append("\n");
        sb.append("│    Partner: ").append(deliveryPartner).append("\n");
        sb.append("│\n");

        // Trust
        sb.append("│ 🏪 SELLER & TRUST:\n");
        sb.append("│    Platform: ").append(platform).append("\n");
        sb.append("│    Seller: ").append(seller).append("\n");
        sb.append("│    ⭐ ").append(rating).append("★ (").append(reviewCount).append(" reviews)\n");
        sb.append("│    Stock: ").append(inStock ? stockCount + " units" : "Out of Stock").append("\n");
        sb.append("│    Warranty: ").append(warranty).append("\n");
        sb.append("│    Returns: ").append(returnPolicy).append("\n");
        sb.append("└───────────────────────────────────────────────────────────\n");
        return sb.toString();
    }
}
