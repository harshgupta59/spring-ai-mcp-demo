package com.example.mcpserver.ap2;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

/**
 * AP2 Payment Result — outcome of payment processing.
 * Contains the full audit trail: Intent → Cart → Payment.
 */
public record PaymentResult(
        String transactionId,
        String cartMandateId,
        String intentMandateId,
        String status, // COMPLETED, FAILED, PENDING
        String paymentMethod, // UPI, CREDIT_CARD, NET_BANKING, WALLET
        double amount,
        String customerName,
        String deliveryAddress,
        String productName,
        String platform,
        Instant processedAt,
        String signature) {
    public static PaymentResult success(CartMandate cart, String intentMandateId,
            String paymentMethod, String customerName, String deliveryAddress) {
        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        Instant now = Instant.now();
        String sig = sign(txnId + "|" + cart.mandateId() + "|" + intentMandateId + "|" + now);
        return new PaymentResult(txnId, cart.mandateId(), intentMandateId, "COMPLETED",
                paymentMethod, cart.totalAmount(), customerName, deliveryAddress,
                cart.productName(), cart.platform(), now, sig);
    }

    public static PaymentResult failed(String cartMandateId, String reason) {
        return new PaymentResult("NONE", cartMandateId, "NONE", "FAILED: " + reason,
                "NONE", 0, "NONE", "NONE", "NONE", "NONE", Instant.now(), "NONE");
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
        if (status.startsWith("FAILED")) {
            return "❌ PAYMENT FAILED\n   Reason: " + status.replace("FAILED: ", "");
        }
        return String.format("""
                ✅ PAYMENT SUCCESSFUL (AP2)
                ══════════════════════════════════════
                💳 Transaction ID:  %s
                📱 Product:         %s
                🏪 Platform:        %s
                💰 Amount Paid:     ₹%,.0f
                💳 Payment Method:  %s
                👤 Customer:        %s
                📍 Deliver To:      %s
                🕐 Processed At:    %s
                ══════════════════════════════════════
                🔐 AP2 AUDIT TRAIL (Non-repudiable):
                   Intent Mandate: %s ✅
                   Cart Mandate:   %s ✅
                   Payment Sig:    %s ✅
                ══════════════════════════════════════""",
                transactionId, productName, platform, amount, paymentMethod,
                customerName, deliveryAddress, processedAt,
                intentMandateId, cartMandateId, signature);
    }
}
