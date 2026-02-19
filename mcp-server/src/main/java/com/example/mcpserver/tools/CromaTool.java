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
public class CromaTool {

    private static final Logger log = LoggerFactory.getLogger(CromaTool.class);
    private final MockDataProvider mockDataProvider;

    public CromaTool(MockDataProvider mockDataProvider) {
        this.mockDataProvider = mockDataProvider;
    }

    @Tool(description = "Search for Samsung Galaxy S series phones on Croma (Tata). Returns full specs, price with MRP/discount, HDFC Bank + Croma Rewards offers, Bajaj Finserv EMI, delivery via Croma/BlueDart, 7-day DOA replacement, and in-store exchange value.")
    public String searchCroma(
            @ToolParam(description = "The Samsung phone to search for, e.g., 'S24 Ultra', 'Galaxy S24', 'S23 FE', 'samsung'") String productName) {

        log.info("🏬 [MCP TOOL] searchCroma called with: '{}'", productName);
        long start = System.currentTimeMillis();

        List<Product> products = mockDataProvider.searchCroma(productName);

        log.info("   → Found {} products on Croma in {}ms",
                products.size(), System.currentTimeMillis() - start);
        products.forEach(p -> log.debug("     · {} ({}, {}) — {}", p.getId(), p.getColor(), p.getStorage(),
                p.getFormattedPrice()));

        if (products.isEmpty())
            return "No Samsung Galaxy phones found on Croma for: " + productName;
        return ToolOutputHelper.formatResults("🏬 CROMA (Tata Digital) — Samsung Galaxy S Series", products);
    }
}
