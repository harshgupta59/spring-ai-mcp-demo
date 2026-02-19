package com.example.mcpclient.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shopping Assistant service that uses LLM with MCP tools
 * to answer shopping queries and find best deals.
 */
@Service
public class ShoppingAssistant {

        private static final Logger log = LoggerFactory.getLogger(ShoppingAssistant.class);

        private final ChatClient chatClient;
        private final ChatMemory chatMemory;

        public ShoppingAssistant(ChatClient.Builder chatClientBuilder,
                        SyncMcpToolCallbackProvider mcpToolProvider) {

                log.info("🚀 [SERVICE] Initializing ShoppingAssistant...");

                // Create chat memory with a sliding window (keeps last 20 messages)
                this.chatMemory = MessageWindowChatMemory.builder()
                                .maxMessages(20)
                                .build();
                log.info("🧠 [SERVICE] Chat memory created (sliding window: 20 messages)");

                // Discover MCP tools
                ToolCallback[] tools = mcpToolProvider.getToolCallbacks();
                log.info("🔧 [SERVICE] Discovered {} MCP tools via Streamable HTTP:", tools.length);
                for (ToolCallback tool : tools) {
                        log.info("   🔨 Tool: {} — {}", tool.getToolDefinition().name(),
                                        tool.getToolDefinition().description().substring(0,
                                                        Math.min(80, tool.getToolDefinition().description().length()))
                                                        + "...");
                }

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
                                .defaultTools(tools)
                                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                                .build();

                log.info("✅ [SERVICE] ShoppingAssistant ready! System prompt loaded, {} tools registered",
                                tools.length);
        }

        /**
         * Process a shopping query using LLM + MCP tools
         */
        public String chat(String userMessage) {
                log.info("💬 [SERVICE] Processing message: \"{}\"", userMessage);
                log.info("   → Sending to LLM (Ollama) with system prompt + {} MCP tools + memory", "8");

                long start = System.currentTimeMillis();
                String response = chatClient.prompt()
                                .user(userMessage)
                                .call()
                                .content();

                long elapsed = System.currentTimeMillis() - start;
                log.info("✅ [SERVICE] LLM responded in {}ms ({} chars)", elapsed,
                                response != null ? response.length() : 0);
                return response;
        }

        /**
         * Process a shopping query with a specific conversation ID
         */
        public String chat(String conversationId, String userMessage) {
                log.info("💬 [SERVICE] Processing message for conversation '{}'", conversationId);
                log.info("   → Message: \"{}\"", userMessage);
                log.info("   → Loading conversation history from memory...");
                log.info("   → Sending to LLM (Ollama) with system prompt + MCP tools + memory");

                long start = System.currentTimeMillis();
                String response = chatClient.prompt()
                                .user(userMessage)
                                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                                .call()
                                .content();

                long elapsed = System.currentTimeMillis() - start;
                log.info("✅ [SERVICE] LLM responded in {}ms ({} chars) for conversation '{}'",
                                elapsed, response != null ? response.length() : 0, conversationId);
                return response;
        }

        public void clearMemory(String conversationId) {
                log.info("🧹 [SERVICE] Clearing memory for conversation: {}", conversationId);
                chatMemory.clear(conversationId);
        }

        public void clearMemory() {
                log.info("🧹 [SERVICE] Clearing default conversation memory");
                chatMemory.clear("default");
        }
}
