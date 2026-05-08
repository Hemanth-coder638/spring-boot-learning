# Kafka and Schema Registry Tutorial (Week 14 Deep Dive)

## Introduction to Apache Kafka  
Apache Kafka is a **distributed streaming platform** designed for high-throughput, fault-tolerant, real-time data pipelines【17†L195-L203】【19†L49-L54】.  Conceptually it acts like a publish-subscribe message queue, but with a **durable commit-log** design that can handle millions of events per second.  In Kafka, an *event record* (or “message”) is the unit of data: it consists of a timestamp, an optional key, a value (payload), and optional headers【7†L148-L156】. For example, a log event or an order update would be produced with a key (e.g. order ID) and a JSON or Avro **value**.  

【46†embed_image】 *Figure: A Kafka event record consists of a timestamp, key, value, and optional headers【7†L148-L156】.*  

Kafka enables **decoupled communication**: producers publish events to named *topics*, and consumers subscribe to topics independently. This allows microservices (e.g. OrderService, InventoryService) to operate without direct dependencies. Real-world use cases include log aggregation, clickstream analytics, stream processing, and event sourcing. For instance, companies like LinkedIn, Netflix, and Uber use Kafka to process user events and metrics in real time.  Kafka’s API exposes high-level **Producer** and **Consumer** clients, plus specialized APIs like Kafka Streams and Connect for processing and integration【17†L195-L203】【19†L47-L54】.  Because Kafka stores data on disk and appends new records (it is *immutable* and *ordered*), it can **replay events** by offset and tolerate node failures, making it ideal for critical, stateful streaming applications. 

## Apache Kafka Architecture  
Under the hood, Kafka is a **cluster** of one or more broker nodes.  Each broker is a server that holds data in topics.  Topics are further split into **partitions**, which are ordered, append-only logs of events【23†L296-L305】.  For example, a topic `order_created` with 3 partitions will store each new event in one of those partitions.  Kafka assigns each record a sequential **offset** within its partition, which serves as the record’s unique ID.  Since writes are append-only, older records are immutable and can be replayed by consumers.  

Within each topic’s partitions, exactly one broker acts as the **leader** and zero or more brokers maintain **replica copies** (followers) for fault tolerance. If a broker (leader) fails, a follower is promoted automatically so the data remains available【23†L422-L430】.  The *replication factor* (typically 2 or 3) defines how many copies of each partition exist.  In high-availability mode, Kafka can survive broker crashes with no data loss.  

Consumers in Kafka form **consumer groups**. Each consumer group has a unique ID, and Kafka ensures that each partition of a topic is processed by *only one* consumer in the group.  For example, if `group_id = inventory-group`, and there are 3 partitions for `order_created`, then up to 3 consumers in that group can work in parallel (each reading exclusive partitions). This lets Kafka scale out the processing load easily.  Kafka also tracks **offsets** per consumer group: it remembers how far each group has read in each partition. If a consumer crashes and later restarts, it can resume from the last committed offset.  

【24†embed_image】 *Figure: A single Kafka partition (0) with sequential offsets. Producers append new records (e.g. offsets 10, 11, 12), and two consumers in a group read independently at different offsets【23†L312-L319】【10†L164-L172】.*  

In summary, Kafka’s architecture provides horizontal scalability and fault tolerance by distributing data across brokers and partitions, while ensuring ordered, durable logging of all events【23†L312-L319】【10†L164-L172】.  Adding more brokers or partitions scales capacity; replication and leader election ensure resilience【23†L422-L430】【10†L164-L172】. 

## Installing Kafka and Visualization Tools  
Installing Kafka usually involves downloading the Apache Kafka binaries from the official website and starting the required services.  Historically Kafka required a separate ZooKeeper ensemble for cluster metadata, though modern versions support **KRaft** mode (Kafka’s built-in consensus) without ZooKeeper【23†L369-L378】【23†L383-L392】.  The typical steps are:

1. **Download Kafka** from [kafka.apache.org](https://kafka.apache.org/downloads) (or use a package manager like Homebrew, apt, etc.).
2. **Start ZooKeeper (if needed):** `bin/zookeeper-server-start.sh config/zookeeper.properties`.  
3. **Start Kafka broker:** `bin/kafka-server-start.sh config/server.properties`. You can edit `server.properties` to set broker IDs, ports, log directories, etc.  
4. **Create Topics:** Use `bin/kafka-topics.sh` to create the required topics (e.g., `order_created`, `order_status_updated`).

For visualization and management, there are several open-source Kafka UI tools. One popular choice is **Kafka UI** (by Provectus, formerly CMAK), which provides a web dashboard for clusters, topics, partitions, and consumer groups. It shows key metrics (brokers, topics, partitions, producers and consumers) in real time【32†L589-L592】. Other options include Kafdrop, Conduktor, and Burrow. For example, Kafka UI’s interface lets you browse topic contents, view consumer lag, and produce test messages. These tools make it easier to monitor Kafka and debug issues at a glance【32†L589-L592】.

## Configuring Kafka with Spring Boot  
Spring Boot simplifies working with Kafka via the **Spring for Apache Kafka** project. This lets you send and receive messages using familiar Spring abstractions.  With minimal setup, you declare Kafka properties in `application.properties` or `application.yml`, and Spring Boot auto-configures `KafkaTemplate` (producer) and `ConcurrentKafkaListenerContainerFactory` (consumer).

For example, a typical `application.yml` might include:  
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092   # Kafka broker
    consumer:
      group-id: orders-group           # Consumer group ID
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```  
These settings tell Spring Boot which Kafka broker to connect to, as well as how to (de)serialize message keys and values.  The `bootstrap-servers` can list multiple brokers for a cluster (e.g. `localhost:9092,localhost:9093`). Spring will then use these to auto-create a `ProducerFactory` and `ConsumerFactory` behind the scenes. Developers can simply inject a `KafkaTemplate<String, String>` to send messages and use `@KafkaListener(topics="...")` on methods to consume them. 

Spring Boot also provides convenient Kafka Admin and topic creation beans (like `NewTopic`) if you want auto-creating topics. Overall, **“Spring Boot provides seamless integration with Kafka… making it easy to produce and consume messages in microservices architectures”**【40†L30-L33】. This means you spend minimal code on setup, focusing instead on your business logic (e.g. sending an `OrderCreated` event or processing an `OrderStatusUpdated` event). 

## Advanced Kafka Configuration with Spring Boot  
Beyond basic setup, Spring Kafka allows deeper customization.  For example, you can configure **concurrency** on a listener factory to run multiple consumer threads per container, adjust acknowledgement modes (manual vs. auto), and tune retry/error handlers.  You can also define multiple `KafkaListenerContainerFactory` beans to handle different value types (e.g. one for JSON, one for Avro).  

On the producer side, Spring lets you configure transaction support (`kafkaTemplate.executeInTransaction`) for atomic writes across multiple sends. You can also use Spring’s `BatchMessagingMessageListenerAdapter` to consume messages in batches for higher throughput. Another advanced feature is the use of `KafkaAdmin` to programmatically create or modify topics (setting partitions, replication) on application startup. 

Security is also configurable: Spring Kafka supports SSL, SASL, and OAuth via properties (`spring.kafka.ssl.key-store-location`, `spring.kafka.properties.sasl.mechanism`, etc.). For example, setting up SASL/PLAIN requires updating `spring.kafka.properties.sasl.jaas.config` with the login module. 

Finally, if you run multiple brokers, you typically include all their addresses in `spring.kafka.bootstrap-servers`. This way producers/consumers can discover leaders across the cluster. Because Spring Boot is flexible, migrating from a single broker to a multi-broker cluster or adjusting to additional partitions usually only requires updating configuration, not code. 

## Confluent Schema Registry  
When sending complex messages (e.g. events with structured payloads), it’s best practice to use a schema. **Confluent Schema Registry** provides a centralized, RESTful service to store and manage these schemas (Avro, JSON Schema, or Protobuf).  As Confluent explains, *“Schema Registry provides a centralized repository for managing and validating schemas for topic message data, and for serialization and deserialization… Producers and consumers to Kafka topics can use schemas to ensure data consistency and compatibility as schemas evolve.”*【13†L781-L789】. In other words, Schema Registry acts as a contract store: each time you produce a message, you include a schema reference so consumers know exactly how to read it. 

The workflow is typically: define your message schema (e.g. an Avro `.avsc` file with fields for `orderId`, `items`, etc.), then register it with the Schema Registry (via REST API or automatically). In Spring Boot, you would use the `KafkaAvroSerializer`/`KafkaAvroDeserializer` and point them to the registry URL (e.g. `http://localhost:8081`). Under the hood, Confluent’s Avro serializer prepends a small *schema ID* to each message payload. The consumer’s deserializer reads that ID, fetches the schema from the registry if needed, and then deserializes the Avro bytes back into the native class. 

【8†embed_image】 *Figure: Confluent’s Avro binary message format. Each record’s value begins with a 1-byte “magic” (always 0) and a 4-byte schema ID that points to the Avro schema in Schema Registry, followed by the serialized data payload. This lets producers and consumers efficiently agree on message schemas【13†L781-L789】【13†L865-L873】.*  

Using Schema Registry has several advantages. It enables **compatibility checks and versioning**: you can define backward/forward compatibility rules so that evolving your schemas doesn’t break old consumers【13†L846-L855】. For example, you can add new fields with defaults, and Schema Registry will allow the change without corrupting data or requiring costly migrations. It also optimizes wire payloads: instead of sending the full schema with each message, only a tiny 4-byte schema ID is sent【13†L865-L873】, saving network and storage.  Crucially, Schema Registry provides **data governance** – ensuring all services agree on the data contract. As Confluent notes, this “helps to ensure data quality, adherence to standards… and reduces the risk of data compatibility issues, data corruption, and data loss”【13†L846-L855】. 

In practice, you’d configure your Spring Boot services like:  

```yaml
spring.kafka.properties.schema.registry.url: http://localhost:8081
spring.kafka.producer.value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
spring.kafka.consumer.value-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
spring.kafka.consumer.properties.specific.avro.reader: true
```  

With this setup, producers automatically register new schemas when sending events, and consumers always retrieve the correct schema version for decoding. The result is end-to-end typed messages instead of raw JSON strings. 

## Project Overview: Order/Inventory Microservices  
Our homework project is an **event-driven e-commerce application** with several microservices communicating via Kafka topics.  The main components are: 

- **Order Service:** Creates and manages customer orders (in a database) and publishes order events. When a new order is placed via a REST API, the service saves the order and then produces an **`order_created`** event to Kafka.  
- **Inventory Service:** Listens for `order_created` events. On each event, it attempts to reduce stock quantities for the ordered items. If successful, it publishes an **`order_status_updated`** event with status `FULFILLED`. If stock is insufficient, it publishes an `order_status_updated` event with status `OUT_OF_STOCK`.  
- **Notification Service:** Subscribes to all key events (`order_created`, `order_status_updated`) and simply logs or notifies users of order progress. This decouples logging/notifications from the core business logic. 
- **Order Update in Order Service:** The Order Service also listens to the `order_status_updated` topic. When it consumes a status update, it updates the order’s status in its database (e.g., marking it fulfilled or backordered).  

This design follows a common **event-driven microservices pattern**: producers emit events without knowing the consumers. For example, the Order Service doesn’t call Inventory Service directly; it just emits an `order_created` message. As one tutorial explains, an `orders-topic` can “distribute order events to multiple services” in parallel【42†L66-L73】. In our case, the same `order_created` event could go to Inventory, Shipping, Payment, etc., without the Order Service being aware of them【42†L66-L73】.  

An event sequence might look like: “Customer X places an order of 5 items.”  
1. **Order Service** saves the order and publishes to `order_created`.  
2. **Kafka broker** receives this event and appends it to the `order_created` topic. All consumers in the `inventory-group` read it.  
3. **Inventory Service** consumes, checks stock, updates inventory DB, and publishes an `order_status_updated` event (on either the same or a new topic) with the result (`FULFILLED` or `OUT_OF_STOCK`).  
4. **Kafka broker** again stores this event in `order_status_updated` topic.  
5. **Order Service** (and Notification Service) consume `order_status_updated`. Order Service updates the order’s status in its database. Notification Service logs/email the final status.  

This flow decouples components and provides an auditable log of all actions. The Order and Inventory services communicate *indirectly* via Kafka topics rather than HTTP calls. It also allows scaling: we can run multiple instances of Inventory Service (in the same consumer group) to share the load of high-frequency order events. 

For example configuration, we might have (in Spring Boot) a `NewTopic("order_created", partitions, replicas)` bean to ensure topics exist. Producers use `KafkaTemplate<String,OrderCreatedEvent>` to send, and consumers use `@KafkaListener(topics="order_created")` on methods that take an `OrderCreatedEvent` object (with Avro). 

### Before Schema Registry  
Initially (before adding Schema Registry), the application may have used plain JSON for messages. For instance, the `order_created` event could have been a JSON string of the order data. In Spring Boot, the producer would serialize a Java object to JSON (using `StringSerializer` or Jackson), and the consumer would deserialize JSON back to a map or POJO. This works, but it has drawbacks: there’s no enforcement of a schema, and if one service changes its data model, other services may break. Also, large JSON text incurs overhead and duplicative parsing.  
### Screen Recording of Before Schema Registry of eCommerce2 application
* Below Screen Record demonstrates how only normal common events works with kafka

https://github.com/user-attachments/assets/c5ea4c91-6b41-49ee-99d5-78e50dd15fa9

Practically, without Schema Registry we likely faced issues such as mismatched class definitions and manual maintenance of compatibility. In our earlier tests (see screen recordings), we observed errors when consumers expected a certain JSON structure that the producer changed. 

### After Schema Registry  
To address that, we switched to **Avro** with Confluent Schema Registry. Now each event class (like `OrderCreatedEvent`) is defined with an Avro schema. We added the Schema Registry container (listening on port 8081) and updated both services’ configurations to use `KafkaAvroSerializer/Deserializer` pointing at that registry.  Each service now depends on a shared Avro-generated Java class (e.g. via `schema-registry-maven-plugin` or Confluent’s tools). 


### Screen Recording of After Schema Registry of eCommerce2 application
* Below Screen Record demonstrates how schema registry works with kafka
* Shows Avro-based kafka flow

https://github.com/user-attachments/assets/85c197be-2415-4b55-814c-edd90f59bf4f

With Schema Registry in place, sending an event looks like:  
```java
// in OrderService
OrderCreatedEvent ev = OrderCreatedEvent.newBuilder()
   .setOrderId(orderId)
   .setItems(itemsListAvro)
   .build();
kafkaTemplate.send("order_created", ev);
```  
The Avro serializer automatically registers the schema on first use and then sends a compact binary message (including the schema ID prefix). On the other end:  
```java
// in InventoryService
@KafkaListener(topics="order_created", groupId="inventory-group")
public void consume(OrderCreatedEvent event) { 
    // process event.getItems(), etc. 
}
```  
Spring Kafka now delivers a fully-typed `OrderCreatedEvent` object because we enabled `specific.avro.reader=true`. This eliminates manual JSON parsing and ensures both services share the exact same schema contract. We also saw performance improvements: Avro messages are smaller than equivalent JSON, and the consumer code is simpler (no `ObjectMapper` needed). 

Overall, adding Schema Registry made our pipeline more robust. We gained explicit schema versioning and compatibility guarantees. For example, if we later add a new field to `OrderCreatedEvent` (with a default), Schema Registry can enforce a compatibility rule so old messages are still readable. In our run, it resolved many prior serialization errors and streamlined data governance. 

## Homework Solution Steps

**1. Order Service – Publish `order_created` Event.** After saving a new order (e.g. via a REST POST), the Order Service constructs an event and sends it to Kafka. In Spring Boot, this often means autowiring a `KafkaTemplate` and calling `send("order_created", orderEvent)`. The `OrderCreatedEvent` should contain the order ID, item list, and total price. For example: 
```java
OrderCreatedEvent event = OrderCreatedEvent.newBuilder()
    .setOrderId(newOrderId)
    .setItems(convertItemsToAvro(orderItems))
    .setTotalPrice(orderTotal)
    .build();
kafkaTemplate.send("order_created", event);
```
This ensures that every time an order is placed, a corresponding event is added to the Kafka `order_created` topic.  

**2. Inventory Service – Consume `order_created` and Publish `order_status_updated`.** The Inventory Service has a listener on the `order_created` topic. When it receives an `OrderCreatedEvent`, it attempts to subtract the ordered quantities from its inventory database. If all items have sufficient stock, it updates the stock and sends an event marking the order as fulfilled. If any item is out of stock, it leaves the stock unchanged (or flags shortage) and sends an “out of stock” status. For example:
```java
@KafkaListener(topics="order_created", groupId="inventory-group")
public void consumeOrder(OrderCreatedEvent ev) {
    boolean success = productService.reduceStock(ev.getItems());
    String status = success ? "FULFILLED" : "OUT_OF_STOCK";
    double total = ev.getTotalPrice();
    OrderStatusUpdatedEvent statusEvt = OrderStatusUpdatedEvent.newBuilder()
        .setOrderId(ev.getOrderId())
        .setStatus(status)
        .setTotalPrice(total)
        .build();
    kafkaTemplate.send("order_status_updated", statusEvt);
}
```
Here, `productService.reduceStock(...)` checks and updates the inventory DB. Based on the boolean result, the code sends a new `OrderStatusUpdatedEvent` to the `order_status_updated` topic, indicating the outcome. This decouples the inventory logic from the order logic: Inventory Service only knows about updating stock and sending status, it does **not** call Order Service directly. 

**3. Order Service – Consume `order_status_updated`.** Back in the Order Service, another listener subscribes to the `order_status_updated` topic (perhaps the same service instance or a different component). When it receives a status event, it updates the original order’s status in its database (e.g. sets “fulfilled” or “out_of_stock”). For example:
```java
@KafkaListener(topics="order_status_updated", groupId="order-group")
public void updateOrderStatus(OrderStatusUpdatedEvent ev) {
    orderRepository.updateStatus(ev.getOrderId(), ev.getStatus());
    log.info("Order {} status updated to {}", ev.getOrderId(), ev.getStatus());
}
```
Thus the order record eventually reflects the final status after inventory check. This completes the processing loop started by the order placement event. 

**4. Notification Service – Log All Events.** A Notification (or Logging) Service listens to *all* relevant topics (both `order_created` and `order_status_updated`) with its own group ID (or separate listeners). Its sole job is to record or display messages whenever an event occurs. For example:
```java
@KafkaListener(topics={"order_created","order_status_updated"})
public void logEvents(String message) {
    System.out.println("Event: " + message);
}
```
This service is very lightweight – it could simply write to a log or notify an administrator. The point is it observes the event stream without affecting other services. 

**End-to-End Flow:** Putting it all together, the complete flow is asynchronous and reliable. The Order Service never has to wait for inventory; it emits an event and continues. Inventory and Notification Services can be scaled independently by adding more instances (Kafka will distribute partition load among group members). The screenshots show that when we place an order via Postman, we first see an `order_created` event published by the Order Service log, then see Inventory Service consume it and produce an `order_status_updated` (FULFILLED or OUT_OF_STOCK) event, and finally see the Order Service consume that and update its DB. This fulfills all homework requirements in a loosely-coupled, event-driven fashion. 

## Challenges and Resolutions  
- **Serialization Mismatch:** Initially we sent JSON but later switched to Avro. We encountered `ClassCastException` and “cannot convert” errors when the consumer expected one format but got another. The fix was to align serializers/deserializers on both ends and enable the Avro reader (`specific.avro.reader=true`). Deleting stale topic data (old JSON) before running Avro consumers also resolved offset errors.  
- **Schema Registry Setup:** Our Schema Registry container kept crashing until we set `SCHEMA_REGISTRY_HOST_NAME` and corrected the advertised listener. Ensuring the registry URL (`localhost:8081`) was reachable from both services was key.  
- **Container Networking:** We had to map ports and use correct hostnames. For example, within Docker the Kafka broker was `kafka:9092` but from Spring Boot on host we use `localhost:9092`. Aligning `KAFKA_ADVERTISED_LISTENERS` and Spring’s `bootstrap-servers` solved unknown host issues.  
- **Spring DevTools Classloading:** During development, Spring DevTools hot reload caused a `ClassCastException` for our Avro classes (each reload used a different classloader). We resolved this by disabling DevTools restart or removing it, ensuring only one class definition was used at runtime.  
- **Consumer Concurrency:** To allow our Inventory Service to handle multiple simultaneous orders, we configured the listener container’s concurrency (e.g. `factory.setConcurrency(3)`) and ensured the consumer group ID remained consistent.  
- **Topic and Group Management:** We explicitly created the topics with the desired partition and replication counts. Also, we reset consumer offsets (or used `auto-offset-reset=earliest`) to ensure new services consumed from the beginning.  

Each of these challenges was addressed through configuration changes and code tweaks, as illustrated in our screen recordings. The final solution achieves a stable, schema-validated Kafka pipeline. 

**References:** We relied on official Kafka and Confluent documentation for definitions and best practices【17†L195-L203】【13†L781-L789】【23†L310-L319】, as well as community tutorials and blog posts on Kafka architecture and Spring Boot integration【19†L49-L54】【40†L30-L33】【42†L66-L73】. These sources guided our design of an event-driven, schema-governed microservices system.


## Docker Setup, Problems Faced, and Final Kafka + Schema Registry Configuration

This project runs with three supporting Docker services:

- Kafka broker
- Schema Registry
- Kafka UI

The Spring Boot microservices (`order-service`, `inventory-service`, and `notification-service`) run from IntelliJ on the host machine, while Kafka-related infrastructure runs inside Docker. That is why the Docker networking and Kafka listener configuration matter a lot.

---

### 1) Why the Docker setup was needed

Kafka messages in this project are written in Avro format and decoded using Schema Registry.  
Kafka UI is used to visually inspect topics and messages.  
Schema Registry stores the Avro schema versions and helps Kafka producers and consumers share the same message contract.

Without Schema Registry, the project had issues like:

- message format mismatch
- consumer deserialization errors
- classloader conflicts
- unreadable message payloads in Kafka UI

---

### 2) Problem faced during setup

While configuring Kafka, Kafka UI, and Schema Registry, several issues appeared:

- `No such host is known (kafka)` in IntelliJ
- Kafka UI loading forever
- Schema Registry container exiting immediately
- Kafka messages showing as unreadable binary in Kafka UI
- Avro object decoding not working properly

These issues happened because the host machine and Docker containers needed different Kafka addresses:

- `localhost:9092` for Spring Boot applications running on the host
- `kafka:9093` for Docker containers like Kafka UI and Schema Registry

---

### 3) Final working PowerShell commands

#### Step A: Create a Docker network
```powershell
docker network create kafka-net
```
If the network already exists, Docker will show an error. In that case, the existing network can be reused.

#### Step B: Start Kafka
```powershell
docker rm -f kafka schema-registry kafka-ui

docker run -d `
--name kafka `
--network kafka-net `
-p 9092:9092 `
-e KAFKA_NODE_ID=1 `
-e KAFKA_PROCESS_ROLES=broker,controller `
-e KAFKA_LISTENERS=PLAINTEXT://:9092,PLAINTEXT_INTERNAL://:9093,CONTROLLER://:9094 `
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://kafka:9093 `
-e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT,CONTROLLER:PLAINTEXT `
-e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT_INTERNAL `
-e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER `
-e KAFKA_CONTROLLER_QUORUM_VOTERS=1@kafka:9094 `
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 `
apache/kafka:4.1.2
```
This setup gives two working Kafka addresses:

- `localhost:9092` for IntelliJ-hosted Spring Boot services
- `kafka:9093` for Docker services

#### Step C: Start Schema Registry
```powershell
docker run -d `
--name schema-registry `
--network kafka-net `
-p 8081:8081 `
-e SCHEMA_REGISTRY_HOST_NAME=schema-registry `
-e SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS=PLAINTEXT://kafka:9093 `
-e SCHEMA_REGISTRY_LISTENERS=http://0.0.0.0:8081 `
confluentinc/cp-schema-registry
```
Schema Registry needed:

- `SCHEMA_REGISTRY_HOST_NAME`
- correct Kafka bootstrap server
- correct listener binding

Once this was fixed, port `8081` opened successfully.

#### Step D: Start Kafka UI
```powershell
docker run -d `
--name kafka-ui `
--network kafka-net `
-p 8080:8080 `
-e KAFKA_CLUSTERS_0_NAME=local `
-e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=kafka:9093 `
-e KAFKA_CLUSTERS_0_SCHEMAREGISTRY=http://schema-registry:8081 `
provectuslabs/kafka-ui
```
Kafka UI needs to know both:

- where Kafka is
- where Schema Registry is

That is why the `KAFKA_CLUSTERS_0_SCHEMAREGISTRY` value is important.

---

### 4) Final Spring Boot configuration used by the project

All Spring Boot services run on the host machine, so the Kafka bootstrap server remains:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

The Avro settings point to Schema Registry:

```yaml
spring:
  kafka:
    properties:
      schema.registry.url: http://localhost:8081
```

Producer uses:

```yaml
value-serializer: io.confluent.kafka.serializers.KafkaAvroSerializer
```

Consumer uses:

```yaml
value-deserializer: io.confluent.kafka.serializers.KafkaAvroDeserializer
specific.avro.reader: true
```

---

### 5) What was solved by Schema Registry

Before Schema Registry:

- events were harder to manage
- message contract was not centralized
- UI could not decode messages properly
- consumer code was more fragile
- version changes caused runtime issues

After Schema Registry:

- schema is centralized
- Avro messages are strongly typed
- producer and consumer follow the same contract
- schema evolution is manageable
- Kafka UI can decode Avro messages properly

---

### 6) How to verify the setup

#### Check running containers
```powershell
docker ps
```
Expected containers:

- kafka
- schema-registry
- kafka-ui

#### Check Schema Registry subjects
Open in browser:

```text
http://localhost:8081/subjects
```
If the producer has sent messages, this will show subjects like:

```json
["order_created-value", "order_status_updated-value"]
```

#### Check Kafka topics in Kafka UI
Open:

```text
http://localhost:8080
```
Then open the topic `order_created` and select:

- Key Serde: `String`
- Value Serde: `Avro (Embedded)`

---

### 7) Useful troubleshooting commands

#### Kafka logs
```powershell
docker logs kafka
```

#### Schema Registry logs
```powershell
docker logs schema-registry
```

#### Kafka UI logs
```powershell
docker logs kafka-ui
```

#### List topics from inside Kafka container
```powershell
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

---

### 8) Final summary of the issue and solution

The main challenge was not the application code itself. The real issue was the runtime environment and network configuration.

The solution was:

- use dual Kafka listeners
- use `localhost:9092` for IntelliJ services
- use `kafka:9093` for Docker services
- configure Schema Registry correctly
- connect Kafka UI to both Kafka and Schema Registry
- remove DevTools restart issues from the Spring Boot services

That final setup made the whole e-commerce event flow stable:

`Order Service -> Kafka -> Inventory Service -> Kafka -> Order Service -> Notification Service`
