package com.example.mcpclient.controller;

import com.example.mcpclient.service.ShoppingAssistant;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for shopping assistant chat.
 * Supports both single-turn and multi-turn conversations.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ShoppingController {

    private final ShoppingAssistant shoppingAssistant;

    public ShoppingController(ShoppingAssistant shoppingAssistant) {
        this.shoppingAssistant = shoppingAssistant;
    }

    /**
     * Chat endpoint for shopping queries
     * 
     * Example requests:
     * - "Buy me an iPhone 15"
     * - "Find the best price for MacBook Air"
     * - "I need milk and bread quickly"
     * - "Compare prices for Sony headphones"
     * 
     * Follow-up examples (after seeing products):
     * - "I'll take the Flipkart one"
     * - "Order the cheapest iPhone"
     * - "Buy product FK-IP15-128"
     */
    @PostMapping("/shop")
    public ResponseEntity<ShopResponse> shop(@RequestBody ShopRequest request) {
        String response;

        if (request.getConversationId() != null && !request.getConversationId().isEmpty()) {
            // Use conversation-specific memory
            response = shoppingAssistant.chat(request.getConversationId(), request.getMessage());
        } else {
            // Use default shared memory
            response = shoppingAssistant.chat(request.getMessage());
        }

        return ResponseEntity.ok(new ShopResponse(response));
    }

    /**
     * Clear conversation memory - start fresh
     */
    @PostMapping("/clear")
    public ResponseEntity<Map<String, String>> clearMemory() {
        shoppingAssistant.clearMemory();
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Conversation memory cleared. Ready for new conversation."));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "MCP Shopping Client",
                "features", "search, compare, order, conversation-memory"));
    }

    @Data
    public static class ShopRequest {
        private String message;
        private String conversationId; // Optional: for user-specific conversations
    }

    @Data
    public static class ShopResponse {
        private final String response;

        public ShopResponse(String response) {
            this.response = response;
        }
    }
}
