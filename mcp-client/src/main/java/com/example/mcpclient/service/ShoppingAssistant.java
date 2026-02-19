package com.example.mcpclient.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Service;

/**
 * Shopping Assistant service that uses LLM with MCP tools
 * to answer shopping queries and find best deals.
 * 
 * Features:
 * - Conversation memory for multi-turn interactions
 * - Product search across Amazon, Flipkart, Swiggy, Blinkit
 * - Price comparison
 * - Order placement
 */
@Service
public class ShoppingAssistant {

        private final ChatClient chatClient;
        private final ChatMemory chatMemory;

        public ShoppingAssistant(ChatClient.Builder chatClientBuilder,
                        SyncMcpToolCallbackProvider mcpToolProvider) {

                // Create chat memory with a sliding window (keeps last 20 messages)
                this.chatMemory = MessageWindowChatMemory.builder()
                                .maxMessages(20)
                                .build();

                // Build ChatClient with MCP tools and memory
                this.chatClient = chatClientBuilder
                                .defaultSystem("""
                                                You are a Samsung Galaxy S Series shopping expert for Indian buyers.
                                                You help users compare and buy Samsung Galaxy phones across 4 platforms:
                                                - Amazon India (HDFC/SBI/ICICI card offers, 2-3 day delivery)
                                                - Flipkart (Axis Bank offers, SuperCoins, 1-2 day delivery, extended warranty)
                                                - Samsung.com India (exclusive colors, free Galaxy Buds/Fit, Samsung Care+, 15-day returns)
                                                - Croma (HDFC + Croma Rewards combo, Bajaj Finserv EMI, in-store exchange)

                                                AVAILABLE PHONES:
                                                - Galaxy S24 Ultra (256GB / 512GB) — flagship, S Pen, 200MP camera
                                                - Galaxy S24+ (256GB) — premium, large display
                                                - Galaxy S24 (128GB / 256GB) — balanced flagship
                                                - Galaxy S23 FE (128GB / 256GB) — budget flagship

                                                IMPORTANT GUIDELINES:

                                                1. SEARCHING:
                                                   - ALWAYS search ALL 4 platforms (Amazon, Flipkart, Samsung.com, Croma)
                                                   - Then use comparePrices for side-by-side comparison
                                                   - Show differences in: price, bank offers, EMI, exchange value,
                                                     delivery speed, warranty, freebies, and return policy

                                                2. HELP USER DECIDE WHERE TO BUY:
                                                   - Highlight which platform has: best price, best freebies (Samsung.com),
                                                     fastest delivery (Flipkart), best bank card deal, best warranty
                                                   - Mention bank-specific discounts (user might have that card)
                                                   - Note Samsung.com exclusive: free Galaxy Buds, Samsung Care+
                                                   - Note Croma exclusive: in-store exchange, walk-in service
                                                   - Always show Product IDs prominently for easy ordering

                                                3. WHEN USER WANTS TO BUY:
                                                   - Confirm product ID, color, storage, platform
                                                   - Ask for name and delivery address if not provided
                                                   - Use placeOrder tool to complete the purchase

                                                4. TONE:
                                                   - Friendly, knowledgeable Samsung expert
                                                   - Always give a clear recommendation with reasoning
                                                """)
                                .defaultTools(mcpToolProvider.getToolCallbacks())
                                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                                .build();
        }

        /**
         * Process a shopping query using LLM + MCP tools
         * Maintains conversation memory for multi-turn interactions
         */
        public String chat(String userMessage) {
                return chatClient.prompt()
                                .user(userMessage)
                                .call()
                                .content();
        }

        /**
         * Process a shopping query with a specific conversation ID
         * Useful for maintaining separate conversations for different users
         */
        public String chat(String conversationId, String userMessage) {
                return chatClient.prompt()
                                .user(userMessage)
                                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                                .call()
                                .content();
        }

        /**
         * Clear conversation memory for a specific conversation
         */
        public void clearMemory(String conversationId) {
                chatMemory.clear(conversationId);
        }

        /**
         * Clear default conversation memory (start fresh)
         */
        public void clearMemory() {
                chatMemory.clear("default");
        }
}
