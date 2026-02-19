package com.example.mcpserver.tools;

import com.example.mcpserver.mock.MockDataProvider;
import com.example.mcpserver.model.Product;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * MCP Tool for placing orders on e-commerce platforms
 */
@Component
public class OrderTool {

    private final MockDataProvider mockDataProvider;

    public OrderTool(MockDataProvider mockDataProvider) {
        this.mockDataProvider = mockDataProvider;
    }

    @Tool(description = "Get detailed information about a specific product by its ID. Use this when user wants to know more about a product or is considering buying it.")
    public String getProductDetails(
            @ToolParam(description = "The product ID, e.g., 'AMZ-IP15-128', 'FK-IP15-128', 'BL-AMUL-FC-1L'") String productId) {

        Optional<Product> productOpt = mockDataProvider.getProductById(productId);

        if (productOpt.isEmpty()) {
            return "Product not found with ID: " + productId + ". Please check the product ID and try again.";
        }

        Product product = productOpt.get();
        return product.toDetailedCard();
    }

    @Tool(description = "Place an order for a product. Use this when the user confirms they want to buy a specific product. Requires product ID, quantity, customer name, and delivery address.")
    public String placeOrder(
            @ToolParam(description = "The product ID to order, e.g., 'AMZ-IP15-128', 'FK-IP15-128'") String productId,
            @ToolParam(description = "Quantity to order, e.g., 1, 2, 3") int quantity,
            @ToolParam(description = "Customer's full name") String customerName,
            @ToolParam(description = "Full delivery address including city and pincode") String deliveryAddress) {

        // Validate product exists
        Optional<Product> productOpt = mockDataProvider.getProductById(productId);
        if (productOpt.isEmpty()) {
            return "❌ Order failed: Product not found with ID: " + productId;
        }

        Product product = productOpt.get();

        // Check stock
        if (!product.isInStock()) {
            return "❌ Order failed: " + product.getName() + " is currently out of stock on " + product.getPlatform();
        }

        if (product.getStockCount() < quantity) {
            return "❌ Order failed: Only " + product.getStockCount() + " units available. You requested " + quantity
                    + " units.";
        }

        // Place order
        MockDataProvider.Order order = mockDataProvider.placeOrder(productId, quantity, customerName, deliveryAddress);

        if (order == null) {
            return "❌ Order failed: Unable to process order. Please try again.";
        }

        return order.toSummary();
    }

    @Tool(description = "Check the status of an existing order by order ID.")
    public String checkOrderStatus(
            @ToolParam(description = "The order ID, e.g., 'ORD-1234567890'") String orderId) {

        return mockDataProvider.getOrderById(orderId)
                .map(MockDataProvider.Order::toSummary)
                .orElse("❌ Order not found with ID: " + orderId);
    }
}
