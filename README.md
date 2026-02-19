# 📱 Samsung Galaxy S Series — AI Shopping Assistant

> **Compare Samsung Galaxy phones across Amazon, Flipkart, Samsung.com & Croma** — powered by Spring AI + MCP Protocol + Ollama LLM

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen) ![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-blue) ![Ollama](https://img.shields.io/badge/Ollama-llama3.2-purple)

---

## 🎯 What is this?

A demo application that shows how **Spring AI** and the **Model Context Protocol (MCP)** can power an intelligent shopping assistant. Ask natural language questions like *"Compare S24 Ultra prices"* and the LLM automatically searches 4 e-commerce platforms, compares prices, highlights bank offers & freebies, and recommends where to buy.

### Key Features

- 🔍 **4-Platform Search** — Amazon India, Flipkart, Samsung.com India, Croma
- 📊 **Side-by-Side Comparison** — Price, MRP, discount%, bank offers, EMI, freebies, delivery, warranty, returns
- 🛒 **Order Placement** — Buy directly through the chat (mock)
- 💬 **Conversational Memory** — Multi-turn conversations with context retention
- 🎨 **Premium Dark UI** — Chat interface with typing indicators, suggestion chips, and responsive design

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│  Browser (index.html)                                           │
│  Dark-themed chat UI with suggestion cards                      │
└─────────────────┬───────────────────────────────────────────────┘
                  │ POST /api/shop {message, conversationId}
                  ▼
┌─────────────────────────────────────────────────────────────────┐
│  MCP Client (port 8080)                                         │
│  ┌─────────────┐  ┌──────────────────┐  ┌───────────────────┐  │
│  │ REST API     │→ │ ShoppingAssistant │→ │ Ollama LLM        │  │
│  │ /api/shop    │  │ ChatClient +      │  │ llama3.2:1b       │  │
│  │ /api/clear   │  │ Memory + Tools    │  │ localhost:11434    │  │
│  │ /api/health  │  └──────────────────┘  └───────┬───────────┘  │
│  └─────────────┘                                 │ tool_call     │
└──────────────────────────────────────────────────┼──────────────┘
                                                   │ MCP over HTTP
                                                   ▼
┌─────────────────────────────────────────────────────────────────┐
│  MCP Server (port 8081)                                         │
│  ┌──────────────┬──────────────┬─────────────┬──────────────┐  │
│  │ AmazonTool   │ FlipkartTool │ SamsungStore│ CromaTool    │  │
│  │              │              │ Tool        │              │  │
│  └──────┬───────┴──────┬───────┴──────┬──────┴──────┬───────┘  │
│  ┌──────┴──────────────┴──────────────┴─────────────┴───────┐  │
│  │ PriceComparator │ OrderTool    │ MockDataProvider          │  │
│  │ (4-platform     │ (place order,│ (28 Samsung phones,       │  │
│  │  comparison)    │  get details)│  7 per platform)          │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 How a Request Flows

1. **User types** "Compare S24 Ultra prices" in the chat UI
2. **Browser** sends `POST /api/shop` to the MCP Client
3. **ShoppingAssistant** builds a prompt with system instructions + conversation history + user message + available tool definitions
4. **Ollama LLM** receives the prompt and decides to call `comparePrices("S24 Ultra")` 
5. **Spring AI** intercepts the tool call and sends a **JSON-RPC request** over MCP to the server
6. **MCP Server** routes to `PriceComparator`, which calls `MockDataProvider.searchAllPlatforms()`
7. **MockDataProvider** returns matching products from all 4 platform maps
8. **PriceComparator** groups by model+storage, sorts by price, formats a rich comparison
9. **Tool result** flows back through MCP → Spring AI → LLM
10. **LLM generates** a natural language summary with recommendations → sent back to the browser

---

## 📊 Product Data

### Samsung Galaxy S Series Lineup

| Model | Storage Options | Price Range (across platforms) |
|---|---|---|
| Galaxy S24 Ultra | 256GB, 512GB | ₹1,27,999 – ₹1,46,999 |
| Galaxy S24+ | 256GB | ₹77,999 – ₹81,999 |
| Galaxy S24 | 128GB, 256GB | ₹57,999 – ₹66,999 |
| Galaxy S23 FE | 128GB, 256GB | ₹27,999 – ₹35,999 |

### Per-Product Data (27 fields)

| Category | Fields |
|---|---|
| **Specs** | Processor, Display, Camera, Battery, RAM, OS |
| **Pricing** | Price, MRP, Auto-computed Discount % |
| **Offers** | Bank discounts, EMI options, Exchange value, Freebies |
| **Delivery** | Speed, Expected date, Charge (FREE/paid), COD, Delivery partner |
| **Trust** | Seller name, Rating, Review count, Warranty, Return policy, Stock |

### Platform Differentiators

| Platform | Unique Advantages |
|---|---|
| **Amazon India** | HDFC/SBI/ICICI card offers, Amazon Logistics, COD ✅ |
| **Flipkart** | Fastest delivery (Tomorrow), Axis Bank + SuperCoins, Extended warranty, Ekart, COD ✅ |
| **Samsung.com India** | Exclusive colors (Titanium Yellow/Green), Free Galaxy Buds2 Pro, Samsung Care+, 15-day returns |
| **Croma** | HDFC + Croma Rewards combo, Bajaj Finserv EMI, In-store exchange |

---

## 🛠️ MCP Protocol

**MCP (Model Context Protocol)** is a standardized JSON-RPC 2.0 protocol for LLM ↔ Tool communication.

- **Transport:** Streamable HTTP (`POST /mcp`)
- **Tool Discovery:** Client calls `tools/list` at startup; server reflects all `@Tool`-annotated methods
- **Tool Execution:** Client sends `tools/call` with method name + args; server executes and returns result

### Registered MCP Tools (8 total)

| Tool | Purpose |
|---|---|
| `searchAmazon` | Search Amazon India for Samsung phones |
| `searchFlipkart` | Search Flipkart for Samsung phones |
| `searchSamsungStore` | Search Samsung.com India (official store) |
| `searchCroma` | Search Croma for Samsung phones |
| `comparePrices` | 4-platform side-by-side price comparison |
| `getProductDetails` | Get full details for a product by ID |
| `placeOrder` | Place an order (product ID, qty, name, address) |
| `checkOrderStatus` | Check status of an existing order |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+**
- **Ollama** installed with `llama3.2:1b` model
  ```bash
  # Install Ollama: https://ollama.com
  ollama pull llama3.2:1b
  ```

### Run

```bash
# 1. Start Ollama (if not already running)
ollama serve

# 2. Start MCP Server (port 8081)
./mvnw spring-boot:run -pl mcp-server

# 3. Start MCP Client (port 8080) — in a new terminal
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

## 📁 Project Structure

```
spring-ai-mcp-demo/
├── pom.xml                              # Parent POM (multi-module)
│
├── mcp-server/                          # PORT 8081 — MCP Tool Server
│   ├── pom.xml
│   └── src/main/java/.../mcpserver/
│       ├── model/Product.java           # 27-field product model
│       ├── mock/MockDataProvider.java   # 28 products, search, orders
│       └── tools/
│           ├── AmazonTool.java          # searchAmazon
│           ├── FlipkartTool.java        # searchFlipkart
│           ├── SamsungStoreTool.java     # searchSamsungStore
│           ├── CromaTool.java           # searchCroma
│           ├── PriceComparator.java     # comparePrices (4-platform)
│           ├── OrderTool.java           # placeOrder, getProductDetails, checkOrderStatus
│           └── ToolOutputHelper.java    # Shared output formatting
│
└── mcp-client/                          # PORT 8080 — User-facing App
    ├── pom.xml
    └── src/main/
        ├── resources/
        │   ├── application.yml          # Ollama + MCP client config
        │   └── static/index.html        # Chat UI (dark theme)
        └── java/.../mcpclient/
            ├── controller/ShoppingController.java  # REST endpoints
            └── service/ShoppingAssistant.java      # ChatClient + Memory
```

---

## 💬 Conversation Memory

Uses `MessageWindowChatMemory` (sliding window of last 20 messages) to support multi-turn interactions:

```
User: "Compare S24 Ultra prices"
Bot:  [shows 4-platform comparison]
User: "Buy the Flipkart one"        ← remembers the comparison
Bot:  "Which storage — 256GB or 512GB?"
User: "256GB, my name is Harsh, deliver to Mumbai 400001"
Bot:  ✅ ORDER CONFIRMED — FK-S24U-256
```

Each browser session gets a unique `conversationId`, so different users don't share memory.

---

## 🛡️ Tech Stack

| Component | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.4.1 |
| **AI Framework** | Spring AI 1.0.0 |
| **LLM** | Ollama (llama3.2:1b, local) |
| **Protocol** | MCP (Model Context Protocol) over Streamable HTTP |
| **Frontend** | Vanilla HTML/CSS/JS (single file, dark theme) |
| **Build** | Maven (multi-module) |

---

## 📄 License

This project is for demonstration and educational purposes.
