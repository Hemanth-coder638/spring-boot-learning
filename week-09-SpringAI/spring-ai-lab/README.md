Spring AI Week-09 Homework Project

A hands-on implementation of six AI backend patterns using *Spring Boot 3.5, **Spring AI 1.1, and **PGVector*. This project demonstrates the integration of LLMs with local databases and real-world tools.

---

## 📖 Core AI Terminology
To understand this project, you must be familiar with these five pillars:

* *Embeddings:* Converting text into mathematical vectors (arrays of numbers) so the computer can understand context and meaning.
* *Semantic Search:* Finding data based on "intent" and "meaning" rather than exact keyword matches.
* *RAG (Retrieval-Augmented Generation):* Enhancing LLM accuracy by retrieving facts from a private database and providing them as "context" to the model.
* *Tool Calling:* Enabling an LLM to execute actual Java methods (e.g., restarting a router or fetching a stock price) when it detects a user needs an action performed.
* *Advisors:* Specialized components that wrap the chat logic to handle cross-cutting concerns like *Chat Memory, **Logging, and **Safety Filtering*.

---

## 📂 Project Implementations

### 📘 Homework 1 — Sarcastic Poet
* *Concept:* *Structured Output.* This ensures the LLM doesn't just "chat" but returns a valid JSON object that maps directly to a Java Record.
* *Internal Flow:* User Request → PoetService → ChatClient with BeanOutputConverter → Ollama → Java Record (Title, Poem, Rhyme).
* *Explanation:* By defining a schema, we treat the AI like a structured API rather than a text generator.

---

### 🎵 Homework 2 — Vibe Playlist Matcher
* *Concept:* *Vector Similarity Search.* Using the distance between vectors to find "vibes."
* *Internal Flow:* User Feeling → EmbeddingModel → PGVector (similarity search) → Filtered Results → Playlist.
* *Explanation:* Instead of searching for the word "sad," the system finds vectors mathematically close to the "feeling" of sadness in the vector_store table.

---

### 📄 Homework 3 — Employee Handbook Bot (RAG)
* *Concept:* *The RAG Pipeline.* Connecting the LLM to a static PDF file.
* *Internal Flow:* PDF Document → Token Splitter → Vector Store Ingestion → User Query → Retrieve Context → Augmented Prompt → LLM Answer.
* *Explanation:* This allows the AI to answer questions about specific company policies that were never part of its original training data.

---

### 🧠 Homework 4 — Safe & Forgetful Assistant
* *Concept:* *Memory & Security Advisors.*
* *Internal Flow:* User Input → LoggingAdvisor → SafetyAdvisor (filter) → ChatMemoryAdvisor (context window) → LLM.
* *Explanation:* This homework demonstrates how to manage conversational state (memory) while ensuring the AI stays within safe boundaries using Interceptors.

---

### 📈 Homework 5 — Stock Trader Agent
* *Concept:* *Autonomous Tool Calling.*
* *Internal Flow:* User Prompt → ChatClient → Function Callback → Java Method (buyStock) → Database Update → Success Message.
* *Explanation:* The AI is given a "toolbox." If a user says "Buy 5 shares," the AI recognizes it has a buyStock tool and calls it automatically.
  
---

### 🌐 Homework 6 — Customer Support Agent
* *Concept:* *Hybrid AI Agent (RAG + Tools).*
* *Internal Flow:* Customer Query → RouterAgent → Check Vector Store (Manual) → Decide Action → Call Reboot Tool → Final Response.
* *Explanation:* The most complex pattern; the AI uses RAG to understand the problem and Tool Calling to solve it.

---

## 🛠 Setup & Requirements
* *Java 21* & *Spring Boot 3.5*
* *Docker:* Used to run the pgvector-schemefinderai container.
* *PostgreSQL:* Database with vector extension enabled.
* *Ollama:* Running local models (e.g., llama3 or mistral).
