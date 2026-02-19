package com.example.mcpserver.tools;

import com.example.mcpserver.mock.MockDataProvider;
import com.example.mcpserver.model.Product;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AmazonTool {

    private final MockDataProvider mockDataProvider;

    public AmazonTool(MockDataProvider mockDataProvider) {
        this.mockDataProvider = mockDataProvider;
    }

    @Tool(description = "Search for Samsung Galaxy S series phones on Amazon India. Returns full specs, price with MRP/discount, bank offers, EMI, delivery speed/date/partner/COD, warranty, exchange value, and seller trust info.")
    public String searchAmazon(
            @ToolParam(description = "The Samsung phone to search for, e.g., 'S24 Ultra', 'Galaxy S24', 'S23 FE', 'samsung'") String productName) {

        List<Product> products = mockDataProvider.searchAmazon(productName);
        if (products.isEmpty())
            return "No Samsung Galaxy phones found on Amazon India for: " + productName;
        return ToolOutputHelper.formatResults("🛒 AMAZON INDIA — Samsung Galaxy S Series", products);
    }
}
