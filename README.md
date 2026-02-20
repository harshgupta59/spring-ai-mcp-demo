# 📱 Samsung Galaxy S Series — AI Shopping Assistant

> **Compare Samsung Galaxy phones across Amazon, Flipkart, Samsung.com & Croma — then buy securely via AP2 Protocol**
>
> Powered by **Spring AI** + **MCP (Model Context Protocol)** + **AP2 (Agent Payments Protocol)** + **Ollama LLM**

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen) ![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-blue) ![Ollama](https://img.shields.io/badge/Ollama-llama3.2-purple) ![AP2](https://img.shields.io/badge/AP2-Agent%20Payments-red)

---

## 🎯 What is this?

An AI-powered shopping assistant that lets you **search, compare, and purchase** Samsung Galaxy phones through natural conversation. Ask questions like *"Compare S24 Ultra prices across all platforms"* — the LLM automatically searches 4 e-commerce platforms, compares prices, recommends where to buy, and processes payment through Google's **AP2 (Agent Payments Protocol)** with cryptographic mandate verification.

### Key Features

- 🔍 **4-Platform Search** — Amazon India, Flipkart, Samsung.com India, Croma
- 📊 **Side-by-Side Comparison** — Price, MRP, discount%, bank offers, EMI, freebies, delivery, warranty
- 🔒 **AP2 Secure Payments** — 3-step cryptographic mandate chain (Intent → Cart → Payment)
- 💬 **Conversational Memory** — Multi-turn conversations with context retention
- 🎨 **Premium Dark Chat UI** — Typing indicators, suggestion chips, responsive design

---

## 🏗️ Architecture Overview

The application runs as **3 separate processes** that communicate with each other:

```
┌────────────────────────────────────────────────────────────────────┐
│  BROWSER (index.html)                                              │
│  Dark-themed chat UI with suggestion cards                         │
└─────────────────┬──────────────────────────────────────────────────┘
                  │ POST /api/shop {message, conversationId}
                  │ (Standard REST/JSON)
                  ▼
┌────────────────────────────────────────────────────────────────────┐
│  MCP CLIENT (port 8080) — The Orchestrator                         │
│                                                                    │
│  ShoppingController ─→ ShoppingAssistant ─→ Ollama LLM            │
│  (REST API)             (ChatClient +        (llama3.2:1b          │
│                          Memory + Tools)      localhost:11434)      │
│                                                                    │
│  What it does:                                                     │
│  • Receives user messages via REST API                             │
│  • Injects system prompt (Samsung expert persona)                  │
│  • Loads conversation history (last 20 messages)                   │
│  • Sends prompt + tool definitions to LLM                          │
│  • When LLM calls a tool → forwards to MCP Server                 │
│  • Returns LLM's final response to browser                        │
└─────────────────┬──────────────────────────────────────────────────┘
                  │ MCP (Streamable HTTP)
                  │ JSON-RPC 2.0 over POST /mcp
                  ▼
┌────────────────────────────────────────────────────────────────────┐
│  MCP SERVER (port 8081) — The Tool Provider                        │
│                                                                    │
│  ┌─────────────┬──────────────┬──────────────┬──────────────┐     │
│  │ AmazonTool  │ FlipkartTool │ SamsungStore │ CromaTool    │     │
│  │             │              │ Tool         │              │     │
│  ├─────────────┼──────────────┼──────────────┴──────────────┤     │
│  │ PriceCompar │ OrderTool    │ AP2PaymentTool              │     │
│  │ ator        │ (details,    │ (intent, cart, payment)     │     │
│  │             │  status)     │                             │     │
│  ├─────────────┴──────────────┴─────────────────────────────┤     │
│  │ MockDataProvider (28 products) │ PaymentService (AP2 PSP)│     │
│  └──────────────────────────────────────────────────────────┘     │
│                                                                    │
│  What it does:                                                     │
│  • Exposes @Tool methods over MCP (Streamable HTTP)                │
│  • Searches mock product data across 4 platforms                   │
│  • Compares prices with grouping and sorting                       │
│  • Manages AP2 mandate chain for secure payments                   │
│  • Returns formatted text results to the LLM                      │
└────────────────────────────────────────────────────────────────────┘
```

| Process | Port | Role |
|---|---|---|
| **Ollama** | 11434 | Runs `llama3.2:1b` LLM locally. Decides which tools to call. Generates responses. |
| **MCP Server** | 8081 | Exposes product data + AP2 payments as MCP tools. No LLM here. |
| **MCP Client** | 8080 | Bridges user ↔ LLM ↔ MCP tools. Orchestrates everything. |

---

## 🔄 Complete Request Flow (Step-by-Step)

### Flow 1: Product Search & Comparison

When a user types **"Compare S24 Ultra prices"**:

**Step 1 — Browser → Controller**
```
POST http://localhost:8080/api/shop
{"message": "Compare S24 Ultra prices", "conversationId": "session-abc123"}
```
`ShoppingController` receives this, extracts `message` and `conversationId`, calls `shoppingAssistant.chat()`.

**Step 2 — ShoppingAssistant builds the LLM prompt**

Three things are assembled:
1. **System Prompt** — "You are a Samsung Galaxy S Series shopping expert..." (defines persona, available phones, AP2 rules)
2. **Conversation History** — Last 20 messages from `MessageWindowChatMemory` (for multi-turn context)
3. **User Message** — "Compare S24 Ultra prices"

Plus: **10 tool definitions** (names, descriptions, parameter schemas) are attached so the LLM knows what it can call.

**Step 3 — Prompt sent to Ollama LLM**

The full prompt goes to `http://localhost:11434/api/chat` (Ollama's API). The LLM reads the user's intent and decides to call a tool:

```json
{
  "tool_calls": [{
    "function": {
      "name": "comparePrices",
      "arguments": {"productName": "S24 Ultra"}
    }
  }]
}
```

> The LLM doesn't execute anything — it just decides WHICH tool to call and WITH WHAT arguments.

**Step 4 — Spring AI sends MCP request**

Spring AI intercepts the `tool_calls`, looks up `comparePrices` in the registered MCP callbacks, and sends a JSON-RPC request:

```
POST http://localhost:8081/mcp
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "method": "tools/call",
  "params": {
    "name": "comparePrices",
    "arguments": {"productName": "S24 Ultra"}
  }
}
```

**Step 5 — MCP Server executes the tool**

`PriceComparator.comparePrices("S24 Ultra")` runs:
1. Calls `mockDataProvider.searchAllPlatforms("S24 Ultra")`
2. Gets 2 products from each platform (256GB + 512GB) = 8 total
3. Groups by model + storage for fair comparison
4. Sorts each group by price (cheapest first)
5. Formats a rich comparison showing price, MRP, discount%, offers, delivery, warranty per platform
6. Returns the formatted string

**Step 6 — Tool result goes back to LLM**

Spring AI sends the tool output back to Ollama as a `tool_response` message. The LLM now has the data and generates a natural language summary with recommendations.

**Step 7 — Response flows back to user**

LLM response → `ChatClient` saves to memory → `ShoppingAssistant` returns string → `ShoppingController` wraps in JSON → Browser renders it.

---

### Flow 2: AP2 Secure Payment

When a user decides to buy, the **AP2 (Agent Payments Protocol)** kicks in. This is Google's open protocol for secure agent-led payments using cryptographic mandates.

#### Why AP2?
Traditional payments assume a human clicks "buy." With AI agents making purchases, we need:
- **Authorization** — Prove the user gave the agent permission
- **Authenticity** — Ensure the request matches the user's true intent
- **Accountability** — Maintain an audit trail if something goes wrong

#### The 3-Step Mandate Chain

```
User says: "Buy the Flipkart S24 Ultra 256GB"

  ┌─────────────────────────────────────────────────────────────┐
  │ STEP 1: createIntentMandate                                  │
  │                                                              │
  │ What:  Captures user's shopping intent + budget              │
  │ Input: intent="Buy Galaxy S24 Ultra from Flipkart"           │
  │        maxBudget=150000, platform="Flipkart"                 │
  │ Output: IM-A1B2C3D4 (SHA-256 signed, timestamped)           │
  │                                                              │
  │ Why:   Creates verifiable proof that the user                │
  │        authorized this purchase with these constraints       │
  └──────────────────────────┬──────────────────────────────────┘
                             │ links to
  ┌──────────────────────────▼──────────────────────────────────┐
  │ STEP 2: createCartMandate                                    │
  │                                                              │
  │ What:  Locks EXACT product, price, quantity                  │
  │ Input: intentMandateId="IM-A1B2C3D4"                        │
  │        productId="FK-S24U-256", quantity=1                   │
  │ Validates:                                                   │
  │   ✅ Intent mandate exists and is valid                      │
  │   ✅ Product exists and is in stock                          │
  │   ✅ Total (₹1,27,999) doesn't exceed budget (₹1,50,000)   │
  │ Output: CM-E5F6G7H8 (SHA-256 signed, linked to IM)          │
  │                                                              │
  │ Why:   "What you see is what you pay for"                   │
  │        Price is locked — can't be changed                   │
  │        Agent shows cart to user for confirmation             │
  └──────────────────────────┬──────────────────────────────────┘
                             │ links to
  ┌──────────────────────────▼──────────────────────────────────┐
  │ STEP 3: processPayment                                       │
  │                                                              │
  │ What:  Validates full chain + processes payment              │
  │ Input: cartMandateId="CM-E5F6G7H8"                          │
  │        paymentMethod="UPI", name, address                    │
  │ Validates:                                                   │
  │   ✅ Cart mandate exists                                     │
  │   ✅ Intent mandate chain is unbroken                        │
  │   ✅ Payment method is valid (UPI/Card/Wallet/NetBanking)   │
  │ Output: TXN-X1Y2Z3A4B5C6 (COMPLETED, full audit trail)     │
  │                                                              │
  │ Audit Trail:                                                 │
  │   IM-A1B2C3D4 → CM-E5F6G7H8 → TXN-X1Y2Z3A4B5C6            │
  │   (intent)       (cart)         (payment)                    │
  │   All cryptographically signed ✅                            │
  └─────────────────────────────────────────────────────────────┘
```

#### AP2 Conversation Example
```
User: "I want to buy the S24 Ultra from Flipkart, 256GB"

Bot:  🔒 Intent Mandate created: IM-A1B2C3D4
      Intent: "Buy Galaxy S24 Ultra 256GB from Flipkart"
      Budget: ₹1,50,000 | Signature: 4f8a2b1c... ✅

      🛒 Cart locked: CM-E5F6G7H8
      Product: Samsung Galaxy S24 Ultra (Titanium Gray, 256GB)
      Platform: Flipkart | Price: ₹1,27,999
      Intent Link: IM-A1B2C3D4 ✅

      Please confirm and tell me:
      • Payment method: UPI / Credit Card / Debit Card / Net Banking / Wallet
      • Your full name
      • Delivery address with pincode

User: "Confirm. UPI, Harsh Gupta, Mumbai 400001"

Bot:  ✅ PAYMENT SUCCESSFUL (AP2)
      Transaction ID: TXN-X1Y2Z3A4B5C6
      Product: Samsung Galaxy S24 Ultra (Titanium Gray, 256GB)
      Platform: Flipkart | Amount: ₹1,27,999 | Via: UPI

      🔐 AP2 Audit Trail:
         Intent: IM-A1B2C3D4 ✅
         Cart:   CM-E5F6G7H8 ✅
         Payment: 7c3d9e... ✅
```

---

## 🔗 MCP Protocol Details

**MCP (Model Context Protocol)** is a standardized JSON-RPC 2.0 protocol for LLM ↔ Tool communication. Think of it as *"USB for AI tools."*

### How MCP Works Here

**Transport:** Streamable HTTP (`POST /mcp`)

**Tool Discovery (at startup):**
1. MCP Client connects to `http://localhost:8081/mcp`
2. Sends `tools/list` request
3. Server reflects all `@Tool`-annotated methods with names, descriptions, and JSON Schema for parameters
4. Client registers them as callable functions for the LLM

**Tool Execution (during chat):**
1. LLM decides to call `searchFlipkart("S24 Ultra")`
2. Spring AI sends `tools/call` JSON-RPC to MCP Server
3. Server executes `FlipkartTool.searchFlipkart("S24 Ultra")`
4. Returns formatted result string via JSON-RPC response
5. Spring AI passes result back to LLM

### All 10 Registered Tools

| # | Tool | Class | Parameters | Purpose |
|---|---|---|---|---|
| 1 | `searchAmazon` | `AmazonTool` | `productName` | Search Amazon India |
| 2 | `searchFlipkart` | `FlipkartTool` | `productName` | Search Flipkart |
| 3 | `searchSamsungStore` | `SamsungStoreTool` | `productName` | Search Samsung.com India |
| 4 | `searchCroma` | `CromaTool` | `productName` | Search Croma |
| 5 | `comparePrices` | `PriceComparator` | `productName` | 4-platform side-by-side comparison |
| 6 | `getProductDetails` | `OrderTool` | `productId` | Full detail for single product |
| 7 | `checkOrderStatus` | `OrderTool` | `orderId` | Order/transaction status lookup |
| 8 | `createIntentMandate` | `AP2PaymentTool` | `intent, budget, platform` | AP2 Step 1: capture user intent |
| 9 | `createCartMandate` | `AP2PaymentTool` | `intentId, productId, qty` | AP2 Step 2: lock cart |
| 10 | `processPayment` | `AP2PaymentTool` | `cartId, method, name, addr` | AP2 Step 3: process payment |

---

## 🧠 Chat Memory System

```java
// Sliding window — keeps the last 20 messages per conversation
MessageWindowChatMemory.builder().maxMessages(20).build();
```

**How it works:**
1. Before each LLM call, `MessageChatMemoryAdvisor` loads the last 20 messages for the `conversationId`
2. These are injected into the prompt as conversation history
3. After the LLM responds, both user message and response are saved
4. This enables multi-turn conversations:

```
Turn 1: "Compare S24 Ultra prices"     → gets comparison
Turn 2: "Buy the Flipkart one"         → knows which product, starts AP2
Turn 3: "UPI, Harsh, Mumbai 400001"    → completes payment
```

Each browser session gets a unique `conversationId`, so different users don't share memory.

---

## 📊 Product Data

### 28 Products (7 phones × 4 platforms)

| Model | Storage | Key Spec |
|---|---|---|
| Galaxy S24 Ultra | 256GB, 512GB | Snapdragon 8 Gen 3, 200MP, S Pen, 6.8" QHD+ |
| Galaxy S24+ | 256GB | Exynos 2400, 50MP, 6.7" FHD+ |
| Galaxy S24 | 128GB, 256GB | Exynos 2400, 50MP, 6.2" FHD+ |
| Galaxy S23 FE | 128GB, 256GB | Exynos 2200, 50MP, 6.4" FHD+ |

### Per-Product Fields (27)

| Category | Fields |
|---|---|
| **Identity** | `id`, `name`, `platform`, `category` |
| **Variant** | `color`, `storage`, `ram` |
| **Specs** | `processor`, `display`, `camera`, `battery`, `os` |
| **Pricing** | `price`, `mrp`, auto-computed `discountPercent()` |
| **Offers** | `offers`, `emiOption`, `exchangeValue`, `freebie` |
| **Delivery** | `deliverySpeed`, `deliveryDate`, `deliveryCharge`, `codAvailable`, `deliveryPartner` |
| **Trust** | `seller`, `rating`, `reviewCount`, `warranty`, `returnPolicy`, `inStock`, `stockCount` |

### Platform Differences

| Platform | Unique Value | Delivery | COD | Partner |
|---|---|---|---|---|
| **Amazon** | HDFC/SBI/ICICI offers | 1-3 days | ✅ | Amazon Logistics |
| **Flipkart** | Axis Bank + SuperCoins, extended warranty | Tomorrow! | ✅ | Ekart |
| **Samsung.com** | Free Galaxy Buds2 Pro, Samsung Care+ | 3-5 days | ❌ | BlueDart |
| **Croma** | HDFC + Croma Rewards, in-store exchange | 2-3 days | ❌ | Croma/BlueDart |

---

## 📁 Project Structure

```
spring-ai-mcp-demo/
├── pom.xml                              # Parent POM (multi-module Maven)
│
├── mcp-server/                          # PORT 8081 — MCP Tool Server
│   ├── pom.xml
│   └── src/main/java/.../mcpserver/
│       ├── McpServerApplication.java    # Spring Boot entry point
│       ├── model/
│       │   └── Product.java             # 27-field product model (@Builder)
│       ├── mock/
│       │   └── MockDataProvider.java    # 28 products, search logic, orders
│       ├── ap2/                         # ★ AP2 Protocol Implementation
│       │   ├── IntentMandate.java       # Step 1: signed user intent
│       │   ├── CartMandate.java         # Step 2: signed locked cart
│       │   ├── PaymentResult.java       # Step 3: transaction receipt + audit
│       │   └── PaymentService.java      # Mock PSP with chain validation
│       └── tools/
│           ├── AmazonTool.java          # @Tool: searchAmazon
│           ├── FlipkartTool.java        # @Tool: searchFlipkart
│           ├── SamsungStoreTool.java     # @Tool: searchSamsungStore
│           ├── CromaTool.java           # @Tool: searchCroma
│           ├── PriceComparator.java     # @Tool: comparePrices
│           ├── OrderTool.java           # @Tool: getProductDetails, checkOrderStatus
│           ├── AP2PaymentTool.java      # @Tool: createIntentMandate, createCartMandate, processPayment
│           └── ToolOutputHelper.java    # Shared formatting utility
│
└── mcp-client/                          # PORT 8080 — User-facing App
    ├── pom.xml
    └── src/main/
        ├── resources/
        │   ├── application.yml          # Ollama + MCP client config
        │   └── static/index.html        # Chat UI (dark theme)
        └── java/.../mcpclient/
            ├── McpClientApplication.java
            ├── controller/
            │   └── ShoppingController.java  # REST: /api/shop, /api/clear, /api/health
            └── service/
                └── ShoppingAssistant.java   # ChatClient + Memory + System Prompt
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+**
- **Ollama** with `llama3.2:1b` model
  ```bash
  # Install: https://ollama.com
  ollama pull llama3.2:1b
  ```

### Run

```bash
# 1. Start Ollama
ollama serve

# 2. Start MCP Server (port 8081)
./mvnw spring-boot:run -pl mcp-server

# 3. Start MCP Client (port 8080) — new terminal
./mvnw spring-boot:run -pl mcp-client

# 4. Open browser
open http://localhost:8080
```

### API Endpoints

```bash
# Chat with the assistant
curl -X POST http://localhost:8080/api/shop \
  -H "Content-Type: application/json" \
  -d '{"message": "Compare S24 Ultra prices", "conversationId": "test-1"}'

# Health check
curl http://localhost:8080/api/health

# Clear conversation memory
curl -X POST http://localhost:8080/api/clear
```

---

## 📋 What Each File Does

### MCP Client (Port 8080)

| File | What it does |
|---|---|
| `ShoppingController.java` | REST API — receives `/api/shop` POST requests, delegates to `ShoppingAssistant`, returns JSON response. Logs request timing. |
| `ShoppingAssistant.java` | Core service — builds `ChatClient` with system prompt (Samsung expert persona), registers MCP tools via `SyncMcpToolCallbackProvider`, manages `MessageWindowChatMemory` (20-message sliding window). Sends prompts to Ollama and returns responses. |
| `application.yml` | Config — Ollama URL (`localhost:11434`), model (`llama3.2:1b`), MCP client connection (`localhost:8081/mcp`), logging levels. |
| `index.html` | Chat UI — dark theme, typing indicators, suggestion chips, markdown rendering. Sends fetch requests to `/api/shop`. |

### MCP Server (Port 8081)

| File | What it does |
|---|---|
| `Product.java` | Data model — 27 fields covering identity, variant, specs, pricing, offers, delivery, trust. Uses Lombok `@Builder`. Has `toDetailedCard()` for rich formatting. |
| `MockDataProvider.java` | In-memory database — 28 products (7 phones × 4 platforms) indexed by search terms. Fuzzy search matching: exact → substring → product name. Also manages order records. |
| `ToolOutputHelper.java` | Shared formatter — generates consistent rich-text output for product listings used by all 4 platform tools. Shows specs, pricing with MRP/discount, offers, delivery details. |
| `AmazonTool.java` | MCP tool — `searchAmazon(productName)` → searches Amazon data → returns formatted results via `ToolOutputHelper`. |
| `FlipkartTool.java` | MCP tool — `searchFlipkart(productName)` → same pattern for Flipkart. |
| `SamsungStoreTool.java` | MCP tool — `searchSamsungStore(productName)` → Samsung.com India, highlights freebies. |
| `CromaTool.java` | MCP tool — `searchCroma(productName)` → Croma (Tata). |
| `PriceComparator.java` | MCP tool — `comparePrices(productName)` → searches ALL 4 platforms, groups by model+storage, sorts by price, shows price spread and best deal per group. |
| `OrderTool.java` | MCP tool — `getProductDetails(productId)` returns full product card. `checkOrderStatus(orderId)` looks up orders or AP2 transactions. |
| `AP2PaymentTool.java` | MCP tool — 3 AP2 tools: `createIntentMandate`, `createCartMandate`, `processPayment`. Delegates to `PaymentService`. |
| `IntentMandate.java` | AP2 record — captures user intent (query, budget, platform). Generates mock SHA-256 signature. Immutable Java record. |
| `CartMandate.java` | AP2 record — locks product/price/qty, links to Intent Mandate. SHA-256 signed. Validates budget against intent. |
| `PaymentResult.java` | AP2 record — transaction receipt with full audit trail (Intent → Cart → Payment signatures). |
| `PaymentService.java` | Mock PSP — manages mandate stores (`ConcurrentHashMap`), validates chain integrity at each step, processes payments. Logs every step of the AP2 flow. |

---

## 🛡️ Tech Stack

| Component | Technology | Why |
|---|---|---|
| **Language** | Java 17 | Spring ecosystem, record types |
| **Framework** | Spring Boot 3.4.1 | Auto-config, dependency injection |
| **AI Framework** | Spring AI 1.0.0 | ChatClient, MCP integration, tool callbacks |
| **LLM** | Ollama (llama3.2:1b) | Local, free, private — no API keys |
| **MCP Transport** | Streamable HTTP | JSON-RPC 2.0 over HTTP POST |
| **Payment Protocol** | AP2 (Agent Payments Protocol) | Secure agent-led payments with mandates |
| **Frontend** | Vanilla HTML/CSS/JS | Single file, no build step |
| **Build** | Maven (multi-module) | Server + Client as separate modules |

---

## 📄 License

This project is for demonstration and educational purposes.
