package com.example.mcpserver.ap2;

import com.example.mcpserver.mock.MockDataProvider;
import com.example.mcpserver.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock Payment Service Provider (PSP) implementing AP2 protocol.
 * Manages the mandate chain: Intent → Cart → Payment.
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final MockDataProvider mockDataProvider;
    private final Map<String, IntentMandate> intentMandates = new ConcurrentHashMap<>();
    private final Map<String, CartMandate> cartMandates = new ConcurrentHashMap<>();
    private final Map<String, PaymentResult> transactions = new ConcurrentHashMap<>();

    public PaymentService(MockDataProvider mockDataProvider) {
        this.mockDataProvider = mockDataProvider;
        log.info("💳 [AP2] PaymentService initialized (mock PSP)");
    }

    /**
     * Step 1: Create Intent Mandate — captures user's shopping intent.
     */
    public IntentMandate createIntentMandate(String userIntent, double maxBudget, String preferredPlatform) {
        IntentMandate mandate = IntentMandate.create(userIntent, maxBudget, preferredPlatform);
        intentMandates.put(mandate.mandateId(), mandate);

        log.info("🔒 [AP2] Intent Mandate created: {}", mandate.mandateId());
        log.info("   Intent: '{}', Budget: ₹{}, Platform: {}",
                userIntent, String.format("%,.0f", maxBudget),
                preferredPlatform != null ? preferredPlatform : "Any");
        log.info("   Signature: {} ✅", mandate.signature());

        return mandate;
    }

    /**
     * Step 2: Create Cart Mandate — locks exact product, price, quantity.
     * Validates against Intent Mandate (budget check).
     */
    public CartMandate createCartMandate(String intentMandateId, String productId, int quantity) {
        // Validate intent mandate exists
        IntentMandate intent = intentMandates.get(intentMandateId);
        if (intent == null) {
            log.error("❌ [AP2] Cart Mandate failed: Intent Mandate '{}' not found", intentMandateId);
            return null;
        }

        // Validate product exists
        Optional<Product> productOpt = mockDataProvider.getProductById(productId);
        if (productOpt.isEmpty()) {
            log.error("❌ [AP2] Cart Mandate failed: Product '{}' not found", productId);
            return null;
        }

        Product product = productOpt.get();

        // Validate budget
        double total = product.getPrice() * quantity;
        if (total > intent.maxBudget()) {
            log.warn("⚠️ [AP2] Cart total ₹{} exceeds intent budget ₹{}",
                    String.format("%,.0f", total), String.format("%,.0f", intent.maxBudget()));
        }

        // Validate stock
        if (!product.isInStock() || product.getStockCount() < quantity) {
            log.error("❌ [AP2] Cart Mandate failed: Insufficient stock for '{}'", productId);
            return null;
        }

        CartMandate cart = CartMandate.create(intentMandateId, productId,
                product.getName() + " (" + product.getColor() + ", " + product.getStorage() + ")",
                product.getPlatform(), product.getPrice(), quantity);
        cartMandates.put(cart.mandateId(), cart);

        log.info("🛒 [AP2] Cart Mandate created: {}", cart.mandateId());
        log.info("   Product: {} on {}", product.getName(), product.getPlatform());
        log.info("   Price: {} × {} = ₹{}", product.getFormattedPrice(), quantity, String.format("%,.0f", total));
        log.info("   Intent Link: {} ✅", intentMandateId);
        log.info("   Signature: {} ✅", cart.signature());

        return cart;
    }

    /**
     * Step 3: Process Payment — validates mandate chain and executes payment.
     */
    public PaymentResult processPayment(String cartMandateId, String paymentMethod,
            String customerName, String deliveryAddress) {
        // Validate cart mandate
        CartMandate cart = cartMandates.get(cartMandateId);
        if (cart == null) {
            log.error("❌ [AP2] Payment failed: Cart Mandate '{}' not found", cartMandateId);
            return PaymentResult.failed(cartMandateId, "Cart Mandate not found");
        }

        // Validate intent mandate chain
        IntentMandate intent = intentMandates.get(cart.intentMandateId());
        if (intent == null) {
            log.error("❌ [AP2] Payment failed: Intent Mandate chain broken for cart '{}'", cartMandateId);
            return PaymentResult.failed(cartMandateId, "Intent Mandate chain broken — authorization invalid");
        }

        // Validate payment method
        if (!isValidPaymentMethod(paymentMethod)) {
            log.error("❌ [AP2] Payment failed: Invalid payment method '{}'", paymentMethod);
            return PaymentResult.failed(cartMandateId, "Invalid payment method: " + paymentMethod);
        }

        log.info("💳 [AP2] Processing payment...");
        log.info("   Cart: {} → Intent: {} (chain valid ✅)", cartMandateId, cart.intentMandateId());
        log.info("   Amount: ₹{} via {}", String.format("%,.0f", cart.totalAmount()), paymentMethod);
        log.info("   Customer: {} → {}", customerName, deliveryAddress);

        // Mock payment processing (simulate gateway)
        PaymentResult result = PaymentResult.success(cart, cart.intentMandateId(),
                paymentMethod, customerName, deliveryAddress);
        transactions.put(result.transactionId(), result);

        // Also record as an order in MockDataProvider for checkOrderStatus
        mockDataProvider.placeOrder(cart.productId(), cart.quantity(), customerName, deliveryAddress);

        log.info("✅ [AP2] Payment COMPLETED!");
        log.info("   Transaction: {}", result.transactionId());
        log.info("   Audit: {} → {} → {} ✅", intent.mandateId(), cart.mandateId(), result.transactionId());

        return result;
    }

    public Optional<PaymentResult> getTransaction(String transactionId) {
        return Optional.ofNullable(transactions.get(transactionId));
    }

    private boolean isValidPaymentMethod(String method) {
        return method != null && (method.equalsIgnoreCase("UPI") ||
                method.equalsIgnoreCase("CREDIT_CARD") ||
                method.equalsIgnoreCase("DEBIT_CARD") ||
                method.equalsIgnoreCase("NET_BANKING") ||
                method.equalsIgnoreCase("WALLET"));
    }
}
