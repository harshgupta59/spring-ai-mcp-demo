package com.example.mcpserver.ap2;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

/**
 * AP2 Intent Mandate — captures the user's shopping intent.
 * Cryptographically signed to prove the user authorized this search/purchase
 * intent.
 */
public record IntentMandate(
        String mandateId,
        String userIntent, // "Buy Galaxy S24 Ultra from Flipkart"
        double maxBudget, // price ceiling the user is willing to pay
        String preferredPlatform, // optional: "Flipkart", "Amazon", etc.
        Instant createdAt,
        String signature // mock SHA-256 signature
) {
    public static IntentMandate create(String userIntent, double maxBudget, String preferredPlatform) {
        String id = "IM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Instant now = Instant.now();
        String sig = sign(id + "|" + userIntent + "|" + maxBudget + "|" + now);
        return new IntentMandate(id, userIntent, maxBudget, preferredPlatform, now, sig);
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
                🔒 INTENT MANDATE (AP2)
                ├─ Mandate ID:  %s
                ├─ Intent:      %s
                ├─ Max Budget:  ₹%,.0f
                ├─ Platform:    %s
                ├─ Created:     %s
                └─ Signature:   %s ✅ (SHA-256)""",
                mandateId, userIntent, maxBudget,
                preferredPlatform != null ? preferredPlatform : "Any",
                createdAt, signature);
    }
}
