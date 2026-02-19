package com.example.mcpserver.tools;

import com.example.mcpserver.mock.MockDataProvider;
import com.example.mcpserver.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderTool {

    private static final Logger log = LoggerFactory.getLogger(OrderTool.class);
    private final MockDataProvider mockDataProvider;

    public OrderTool(MockDataProvider mockDataProvider) {
        this.mockDataProvider = mockDataProvider;
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

    @Tool(description = "Place an order for a product. Use this when the user confirms they want to buy a specific product. Requires product ID, quantity, customer name, and delivery address.")
    public String placeOrder(
            @ToolParam(description = "The product ID to order, e.g., 'FK-S24U-256', 'AMZ-S24U-512'") String productId,
            @ToolParam(description = "Quantity to order, e.g., 1, 2, 3") int quantity,
            @ToolParam(description = "Customer's full name") String customerName,
            @ToolParam(description = "Full delivery address including city and pincode") String deliveryAddress) {

        log.info("🛒 [MCP TOOL] placeOrder called:");
        log.info("   Product ID: {}", productId);
        log.info("   Quantity: {}", quantity);
        log.info("   Customer: {}", customerName);
        log.info("   Address: {}", deliveryAddress);

        Optional<Product> productOpt = mockDataProvider.getProductById(productId);
        if (productOpt.isEmpty()) {
            log.error("   → ❌ ORDER FAILED: Product '{}' not found", productId);
            return "❌ Order failed: Product not found with ID: " + productId;
        }

        Product product = productOpt.get();

        if (!product.isInStock()) {
            log.warn("   → ❌ ORDER FAILED: {} is out of stock on {}", product.getName(), product.getPlatform());
            return "❌ Order failed: " + product.getName() + " is currently out of stock on " + product.getPlatform();
        }

        if (product.getStockCount() < quantity) {
            log.warn("   → ❌ ORDER FAILED: Only {} units available, requested {}", product.getStockCount(), quantity);
            return "❌ Order failed: Only " + product.getStockCount() + " units available. You requested " + quantity
                    + " units.";
        }

        MockDataProvider.Order order = mockDataProvider.placeOrder(productId, quantity, customerName, deliveryAddress);

        if (order == null) {
            log.error("   → ❌ ORDER FAILED: Unknown error during order placement");
            return "❌ Order failed: Unable to process order. Please try again.";
        }

        log.info("   → ✅ ORDER CONFIRMED!");
        log.info("     Order ID: {}", order.orderId());
        log.info("     Product: {} on {}", product.getName(), product.getPlatform());
        log.info("     Total: ₹{}", String.format("%,.0f", order.totalAmount()));
        log.info("     Delivery: {} via {}", order.expectedDelivery(), order.deliveryPartner());

        return order.toSummary();
    }

    @Tool(description = "Check the status of an existing order by order ID.")
    public String checkOrderStatus(
            @ToolParam(description = "The order ID, e.g., 'ORD-1234567890'") String orderId) {

        log.info("📋 [MCP TOOL] checkOrderStatus called with: '{}'", orderId);

        return mockDataProvider.getOrderById(orderId)
                .map(order -> {
                    log.info("   → ✅ Order found: {} — Status: {}", orderId, order.status());
                    return order.toSummary();
                })
                .orElseGet(() -> {
                    log.warn("   → ❌ Order NOT FOUND: '{}'", orderId);
                    return "❌ Order not found with ID: " + orderId;
                });
    }
}
