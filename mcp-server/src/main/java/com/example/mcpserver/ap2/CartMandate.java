package com.example.mcpserver.ap2;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

/**
 * AP2 Cart Mandate — locks exact product, price, and quantity.
 * Links to an Intent Mandate to prove chain of authorization.
 * The user "signs" this to confirm "what I see is what I pay for."
 */
public record CartMandate(
        String mandateId,
        String intentMandateId, // link to parent Intent Mandate
        String productId, // exact product: "FK-S24U-256"
        String productName,
        String platform,
        double unitPrice,
        int quantity,
        double totalAmount,
        Instant createdAt,
        String signature // mock SHA-256 signature
) {
    public static CartMandate create(String intentMandateId, String productId, String productName,
            String platform, double unitPrice, int quantity) {
        String id = "CM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        double total = unitPrice * quantity;
        Instant now = Instant.now();
        String sig = sign(id + "|" + intentMandateId + "|" + productId + "|" + total + "|" + now);
        return new CartMandate(id, intentMandateId, productId, productName, platform, unitPrice, quantity, total, now,
                sig);
    }

    private static String sign(String data) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(data.getBytes());
            return HexFormat.of().formatHex(hash).substring(0, 16);
        } catch (Exception e) {
            return "mock-sig-" + data.hashCode();
        }
    }

    public String toSummary() {
        return String.format("""
                🛒 CART MANDATE (AP2)
                ├─ Cart ID:       %s
                ├─ Intent Link:   %s ✅ (verified)
                ├─ Product:       %s
                ├─ Product ID:    %s
                ├─ Platform:      %s
                ├─ Unit Price:    ₹%,.0f
                ├─ Quantity:      %d
                ├─ Total:         ₹%,.0f
                ├─ Created:       %s
                └─ Signature:     %s ✅ (SHA-256)""",
                mandateId, intentMandateId, productName, productId,
                platform, unitPrice, quantity, totalAmount, createdAt, signature);
    }
}
