package com.example.mcpclient.controller;

import com.example.mcpclient.service.ShoppingAssistant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for shopping assistant chat.
 * Supports both single-turn and multi-turn conversations.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ShoppingController {

    private final ShoppingAssistant shoppingAssistant;

    public ShoppingController(ShoppingAssistant shoppingAssistant) {
        this.shoppingAssistant = shoppingAssistant;
    }

    @PostMapping("/shop")
    public ResponseEntity<ShopResponse> shop(@RequestBody ShopRequest request) {
        String convId = request.getConversationId();
        log.info("════════════════════════════════════════════════════════════");
        log.info("📥 [CONTROLLER] Incoming request");
        log.info("   Message: \"{}\"", request.getMessage());
        log.info("   ConversationId: {}", convId != null ? convId : "(default)");
        log.info("════════════════════════════════════════════════════════════");

        long startTime = System.currentTimeMillis();

        String response;
        if (convId != null && !convId.isEmpty()) {
            response = shoppingAssistant.chat(convId, request.getMessage());
        } else {
            response = shoppingAssistant.chat(request.getMessage());
        }

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("════════════════════════════════════════════════════════════");
        log.info("📤 [CONTROLLER] Response ready in {}ms ({} chars)",
                elapsed, response != null ? response.length() : 0);
        log.info("════════════════════════════════════════════════════════════");

        return ResponseEntity.ok(new ShopResponse(response));
    }

    @PostMapping("/clear")
    public ResponseEntity<Map<String, String>> clearMemory() {
        log.info("🧹 [CONTROLLER] Clearing conversation memory");
        shoppingAssistant.clearMemory();
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Conversation memory cleared. Ready for new conversation."));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.debug("💓 [CONTROLLER] Health check");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "MCP Shopping Client",
                "features", "search, compare, order, conversation-memory"));
    }

    @Data
    public static class ShopRequest {
        private String message;
        private String conversationId;
    }

    @Data
    public static class ShopResponse {
        private final String response;

        public ShopResponse(String response) {
            this.response = response;
        }
    }
}
