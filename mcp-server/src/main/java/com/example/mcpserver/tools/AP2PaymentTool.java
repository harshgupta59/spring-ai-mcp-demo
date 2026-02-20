package com.example.mcpserver.tools;

import com.example.mcpserver.ap2.CartMandate;
import com.example.mcpserver.ap2.IntentMandate;
import com.example.mcpserver.ap2.PaymentResult;
import com.example.mcpserver.ap2.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * MCP Tools implementing the AP2 (Agent Payments Protocol).
 * 3-step mandate chain: Intent → Cart → Payment.
 */
@Component
public class AP2PaymentTool {

    private static final Logger log = LoggerFactory.getLogger(AP2PaymentTool.class);
    private final PaymentService paymentService;

    public AP2PaymentTool(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Tool(description = "AP2 Step 1: Create an Intent Mandate. Call this when the user decides to buy a product. Captures user's shopping intent, budget, and preferred platform. Returns a signed mandate ID needed for the next step.")
    public String createIntentMandate(
            @ToolParam(description = "What the user wants to buy, e.g. 'Buy Samsung Galaxy S24 Ultra 256GB from Flipkart'") String userIntent,
            @ToolParam(description = "Maximum budget in INR the user is willing to pay, e.g. 150000") double maxBudget,
            @ToolParam(description = "Preferred platform: 'Amazon India', 'Flipkart', 'Samsung.com India', or 'Croma'. Use 'Any' if no preference.") String preferredPlatform) {

        log.info("🔒 [AP2 TOOL] createIntentMandate called");
        IntentMandate mandate = paymentService.createIntentMandate(userIntent, maxBudget, preferredPlatform);
        return mandate.toSummary() + "\n\n💡 Next: Use this Intent Mandate ID (" + mandate.mandateId()
                + ") to create a Cart Mandate with createCartMandate.";
    }

    @Tool(description = "AP2 Step 2: Create a Cart Mandate. Call this after createIntentMandate. Locks the exact product, price, and quantity. The cart is cryptographically signed and linked to the Intent Mandate. Show the cart details to the user and ask them to confirm before proceeding to payment.")
    public String createCartMandate(
            @ToolParam(description = "The Intent Mandate ID from step 1, e.g. 'IM-A1B2C3D4'") String intentMandateId,
            @ToolParam(description = "The product ID to add to cart, e.g. 'FK-S24U-256'") String productId,
            @ToolParam(description = "Quantity to purchase, e.g. 1") int quantity) {

        log.info("🛒 [AP2 TOOL] createCartMandate called — intent: {}, product: {}", intentMandateId, productId);
        CartMandate cart = paymentService.createCartMandate(intentMandateId, productId, quantity);

        if (cart == null) {
            return "❌ Cart Mandate creation failed. Check that the Intent Mandate ID and Product ID are valid.";
        }

        return cart.toSummary() + "\n\n💡 Ask the user to confirm this cart. Then use Cart Mandate ID ("
                + cart.mandateId() + ") with processPayment to complete the purchase.";
    }

    @Tool(description = "AP2 Step 3: Process payment. Call this after the user confirms the Cart Mandate. Validates the full mandate chain (Intent → Cart → Payment), processes payment, and returns a transaction receipt with audit trail. Supported payment methods: UPI, CREDIT_CARD, DEBIT_CARD, NET_BANKING, WALLET.")
    public String processPayment(
            @ToolParam(description = "The Cart Mandate ID from step 2, e.g. 'CM-A1B2C3D4'") String cartMandateId,
            @ToolParam(description = "Payment method: UPI, CREDIT_CARD, DEBIT_CARD, NET_BANKING, or WALLET") String paymentMethod,
            @ToolParam(description = "Customer's full name") String customerName,
            @ToolParam(description = "Full delivery address including city and pincode") String deliveryAddress) {

        log.info("💳 [AP2 TOOL] processPayment called — cart: {}, method: {}", cartMandateId, paymentMethod);
        PaymentResult result = paymentService.processPayment(cartMandateId, paymentMethod, customerName,
                deliveryAddress);
        return result.toSummary();
    }
}
