package com.example.mcpserver.tools;

import com.example.mcpserver.mock.MockDataProvider;
import com.example.mcpserver.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlipkartTool {

    private static final Logger log = LoggerFactory.getLogger(FlipkartTool.class);
    private final MockDataProvider mockDataProvider;

    public FlipkartTool(MockDataProvider mockDataProvider) {
        this.mockDataProvider = mockDataProvider;
    }

    @Tool(description = "Search for Samsung Galaxy S series phones on Flipkart. Returns full specs, price with MRP/discount, Axis Bank/SuperCoins offers, EMI, delivery speed/date/partner/COD, warranty with Flipkart extended, exchange value, and seller trust info.")
    public String searchFlipkart(
            @ToolParam(description = "The Samsung phone to search for, e.g., 'S24 Ultra', 'Galaxy S24', 'S23 FE', 'samsung'") String productName) {

        log.info("🏪 [MCP TOOL] searchFlipkart called with: '{}'", productName);
        long start = System.currentTimeMillis();

        List<Product> products = mockDataProvider.searchFlipkart(productName);

        log.info("   → Found {} products on Flipkart in {}ms",
                products.size(), System.currentTimeMillis() - start);
        products.forEach(p -> log.debug("     · {} ({}, {}) — {}", p.getId(), p.getColor(), p.getStorage(),
                p.getFormattedPrice()));

        if (products.isEmpty())
            return "No Samsung Galaxy phones found on Flipkart for: " + productName;
        return ToolOutputHelper.formatResults("🏪 FLIPKART — Samsung Galaxy S Series", products);
    }
}
