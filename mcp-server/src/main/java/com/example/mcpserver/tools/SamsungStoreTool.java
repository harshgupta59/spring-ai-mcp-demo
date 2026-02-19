package com.example.mcpserver.tools;

import com.example.mcpserver.mock.MockDataProvider;
import com.example.mcpserver.model.Product;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SamsungStoreTool {

    private final MockDataProvider mockDataProvider;

    public SamsungStoreTool(MockDataProvider mockDataProvider) {
        this.mockDataProvider = mockDataProvider;
    }

    @Tool(description = "Search for Samsung Galaxy S series phones on Samsung.com India (official Samsung store). Returns full specs, price with MRP/discount, exclusive colors, free Galaxy Buds/Fit/Care+ freebies, Samsung Finance+ EMI, delivery via BlueDart, 15-day return policy, and SmartSwitch trade-in value.")
    public String searchSamsungStore(
            @ToolParam(description = "The Samsung phone to search for, e.g., 'S24 Ultra', 'Galaxy S24', 'S23 FE', 'samsung'") String productName) {

        List<Product> products = mockDataProvider.searchSamsungStore(productName);
        if (products.isEmpty())
            return "No Samsung Galaxy phones found on Samsung.com India for: " + productName;
        return ToolOutputHelper.formatResults("🏢 SAMSUNG.COM INDIA (Official Store) — Samsung Galaxy S Series",
                products);
    }
}
