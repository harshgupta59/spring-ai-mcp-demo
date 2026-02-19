package com.example.mcpserver.tools;

import com.example.mcpserver.model.Product;

import java.util.List;

/**
 * Shared output formatting for all platform tools.
 */
public class ToolOutputHelper {

    public static String formatResults(String platformHeader, List<Product> products) {
        if (products.isEmpty())
            return "No Samsung Galaxy phones found.";

        StringBuilder sb = new StringBuilder();
        sb.append(platformHeader).append("\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        int i = 1;
        for (Product p : products) {
            sb.append(i++).append(". ").append(p.getName())
                    .append(" (").append(p.getColor()).append(", ").append(p.getStorage())
                    .append(", ").append(p.getRam()).append(" RAM)\n");
            sb.append("   🏷️ ID: ").append(p.getId()).append("\n\n");

            // Specs
            sb.append("   ⚙️ SPECS:\n");
            sb.append("      Processor: ").append(p.getProcessor()).append("\n");
            sb.append("      Display:   ").append(p.getDisplay()).append("\n");
            sb.append("      Camera:    ").append(p.getCamera()).append("\n");
            sb.append("      Battery:   ").append(p.getBattery()).append("\n");
            sb.append("      OS:        ").append(p.getOs()).append("\n\n");

            // Pricing
            sb.append("   💰 PRICING:\n");
            sb.append("      Price: ").append(p.getFormattedPrice())
                    .append("  (MRP: ").append(p.getFormattedMrp())
                    .append(", ").append(p.getDiscountPercent()).append("% off)\n");
            sb.append("      Offers:   ").append(p.getOffers()).append("\n");
            sb.append("      EMI:      ").append(p.getEmiOption()).append("\n");
            sb.append("      Exchange: ").append(p.getExchangeValue()).append("\n");
            if (p.getFreebie() != null && !p.getFreebie().equalsIgnoreCase("None")) {
                sb.append("      🎁 FREE:  ").append(p.getFreebie()).append("\n");
            }
            sb.append("\n");

            // Delivery
            sb.append("   🚚 DELIVERY:\n");
            sb.append("      Speed:    ").append(p.getDeliverySpeed()).append("\n");
            sb.append("      Expected: ").append(p.getDeliveryDate()).append("\n");
            sb.append("      Charge:   ")
                    .append(p.getDeliveryCharge() == 0 ? "FREE" : String.format("₹%.0f", p.getDeliveryCharge()))
                    .append("\n");
            sb.append("      COD:      ").append(p.isCodAvailable() ? "✅ Available" : "❌ Not Available").append("\n");
            sb.append("      Partner:  ").append(p.getDeliveryPartner()).append("\n\n");

            // Trust
            sb.append("   🏪 SELLER & TRUST:\n");
            sb.append("      Seller:   ").append(p.getSeller()).append("\n");
            sb.append("      Rating:   ").append(p.getRating()).append("★ (").append(p.getReviewCount())
                    .append(" reviews)\n");
            sb.append("      Stock:    ").append(p.isInStock() ? p.getStockCount() + " units" : "Out of Stock")
                    .append("\n");
            sb.append("      Warranty: ").append(p.getWarranty()).append("\n");
            sb.append("      Returns:  ").append(p.getReturnPolicy()).append("\n");
            sb.append("\n   ──────────────────────────────────────────────────────────\n\n");
        }

        sb.append("💡 To buy, tell me the product ID (e.g., '").append(products.get(0).getId()).append("')\n");
        return sb.toString();
    }
}
