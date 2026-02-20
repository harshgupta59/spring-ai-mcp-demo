package com.example.mcpserver.tools;

import com.example.mcpserver.ap2.PaymentService;
import com.example.mcpserver.mock.MockDataProvider;
import com.example.mcpserver.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * MCP Tool for product details and order status.
 * Purchase flow is now handled by AP2PaymentTool.
 */
@Component
public class OrderTool {

    private static final Logger log = LoggerFactory.getLogger(OrderTool.class);
    private final MockDataProvider mockDataProvider;
    private final PaymentService paymentService;

    public OrderTool(MockDataProvider mockDataProvider, PaymentService paymentService) {
        this.mockDataProvider = mockDataProvider;
        this.paymentService = paymentService;
    }

    @Tool(description = "Get detailed information about a specific product by its ID. Use this when user wants to know more about a product or is considering buying it.")
    public String getProductDetails(
            @ToolParam(description = "The product ID, e.g., 'AMZ-S24U-256', 'FK-S24U-512', 'SS-S24-128'") String productId) {

        log.info("🔍 [MCP TOOL] getProductDetails called with ID: '{}'", productId);
        Optional<Product> productOpt = mockDataProvider.getProductById(productId);

        if (productOpt.isEmpty()) {
            log.warn("   → ❌ Product NOT FOUND: '{}'", productId);
            return "Product not found with ID: " + productId + ". Please check the product ID and try again.";
        }

        Product product = productOpt.get();
        log.info("   → ✅ Found: {} ({}, {}) on {} — {}",
                product.getName(), product.getColor(), product.getStorage(),
                product.getPlatform(), product.getFormattedPrice());
        return product.toDetailedCard();
    }

    @Tool(description = "Check the status of an existing order by order ID, or look up an AP2 transaction by transaction ID.")
    public String checkOrderStatus(
            @ToolParam(description = "The order ID (e.g., 'ORD-1234567890') or AP2 transaction ID (e.g., 'TXN-A1B2C3D4E5F6')") String orderId) {

        log.info("📋 [MCP TOOL] checkOrderStatus called with: '{}'", orderId);

        // Check AP2 transactions first
        if (orderId.startsWith("TXN-")) {
            return paymentService.getTransaction(orderId)
                    .map(txn -> {
                        log.info("   → ✅ AP2 Transaction found: {}", orderId);
                        return txn.toSummary();
                    })
                    .orElseGet(() -> {
                        log.warn("   → ❌ Transaction NOT FOUND: '{}'", orderId);
                        return "❌ Transaction not found: " + orderId;
                    });
        }

        // Fall back to legacy orders
        return mockDataProvider.getOrderById(orderId)
                .map(order -> {
                    log.info("   → ✅ Order found: {}", orderId);
                    return order.toSummary();
                })
                .orElseGet(() -> {
                    log.warn("   → ❌ Order NOT FOUND: '{}'", orderId);
                    return "❌ Order not found: " + orderId;
                });
    }
}
