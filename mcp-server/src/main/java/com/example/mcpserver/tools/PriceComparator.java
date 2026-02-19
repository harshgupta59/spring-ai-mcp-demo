package com.example.mcpserver.tools;

import com.example.mcpserver.mock.MockDataProvider;
import com.example.mcpserver.model.Product;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PriceComparator {

    private final MockDataProvider mockDataProvider;

    public PriceComparator(MockDataProvider mockDataProvider) {
        this.mockDataProvider = mockDataProvider;
    }

    @Tool(description = "Compare Samsung Galaxy phone prices across Amazon India, Flipkart, Samsung.com India, and Croma. Shows side-by-side comparison of price, MRP, discount%, offers, EMI, freebies, delivery speed/date/COD/partner, warranty, exchange value, and return policy for each platform.")
    public String comparePrices(
            @ToolParam(description = "The Samsung phone to compare, e.g., 'S24 Ultra', 'Galaxy S24', 'S23 FE'") String productName) {

        Map<String, List<Product>> allResults = mockDataProvider.searchAllPlatforms(productName);
        List<Product> allProducts = new ArrayList<>();
        allResults.values().forEach(allProducts::addAll);

        if (allProducts.isEmpty())
            return "No Samsung Galaxy phones found for '" + productName + "'.";

        // Group by model + storage
        Map<String, List<Product>> grouped = new LinkedHashMap<>();
        for (Product p : allProducts) {
            String key = p.getName() + " " + p.getStorage();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📊 4-PLATFORM PRICE COMPARISON: '").append(productName).append("'\n");
        sb.append("════════════════════════════════════════════════════════════════\n\n");

        for (var entry : grouped.entrySet()) {
            List<Product> variants = entry.getValue();
            variants.sort(Comparator.comparingDouble(Product::getPrice));
            Product best = variants.get(0);

            sb.append("📱 ").append(entry.getKey())
                    .append(" (").append(best.getRam()).append(" RAM)\n");
            sb.append("   ").append(best.getProcessor()).append("\n");
            sb.append("────────────────────────────────────────────────────────────────\n");

            for (Product p : variants) {
                String badge = p == best ? " 👑 BEST PRICE" : "";
                sb.append("\n  🏪 ").append(p.getPlatform()).append(badge).append("\n");
                sb.append("     💰 ").append(p.getFormattedPrice())
                        .append("  (MRP: ").append(p.getFormattedMrp())
                        .append(", ").append(p.getDiscountPercent()).append("% off)\n");
                sb.append("     🎨 Color:    ").append(p.getColor()).append("\n");
                sb.append("     🎁 Offers:   ").append(p.getOffers()).append("\n");
                sb.append("     💳 EMI:      ").append(p.getEmiOption()).append("\n");
                sb.append("     🔄 Exchange: ").append(p.getExchangeValue()).append("\n");
                if (p.getFreebie() != null && !p.getFreebie().equalsIgnoreCase("None")) {
                    sb.append("     🎁 FREE:    ").append(p.getFreebie()).append("\n");
                }
                sb.append("     🚚 Delivery: ").append(p.getDeliverySpeed())
                        .append(" → ").append(p.getDeliveryDate()).append("\n");
                sb.append("        Charge: ")
                        .append(p.getDeliveryCharge() == 0 ? "FREE" : String.format("₹%.0f", p.getDeliveryCharge()))
                        .append(" | COD: ").append(p.isCodAvailable() ? "Yes" : "No")
                        .append(" | Via: ").append(p.getDeliveryPartner()).append("\n");
                sb.append("     🛡️ Warranty: ").append(p.getWarranty()).append("\n");
                sb.append("     ↩️ Returns:  ").append(p.getReturnPolicy()).append("\n");
                sb.append("     ⭐ Rating:   ").append(p.getRating()).append("★ (")
                        .append(p.getReviewCount()).append(" reviews)\n");
            }

            if (variants.size() > 1) {
                double spread = variants.get(variants.size() - 1).getPrice() - best.getPrice();
                if (spread > 0) {
                    sb.append("\n  💡 Savings: ₹").append(String.format("%,.0f", spread))
                            .append(" cheaper on ").append(best.getPlatform()).append("\n");
                }
            }

            sb.append("\n════════════════════════════════════════════════════════════════\n\n");
        }

        sb.append("💡 To order, tell me the product ID and I'll complete the purchase.\n");
        return sb.toString();
    }
}
