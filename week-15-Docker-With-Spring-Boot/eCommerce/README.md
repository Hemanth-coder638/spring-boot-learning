# 🛒 eCommerce Microservices Platform – Dockerized Spring Boot Architecture

> Containerized microservices system (Orders/Inventory/Shipping) with centralized config (Config Server), service discovery (Eureka), API Gateway, databases, Zipkin tracing, and ELK logging.

---

## 📌 Project Summary

This repository provides a **production-grade Docker** deployment of an eCommerce microservices ecosystem. All components – Spring Boot services (Order, Inventory, Shipping), Spring Cloud Config Server, Eureka Discovery, API Gateway, PostgreSQL databases, Zipkin, and the ELK stack – run inside Docker containers. Everything is orchestrated by Docker Compose, so a single command brings up the entire distributed system.

**Key features:** Centralized configuration, service registry, API routing, containerized databases, distributed tracing, and centralized logging. This demonstrates modern cloud-native microservices practices.

---

## 🛠️ Technology Stack

| Technology                         | Purpose                                              |
|------------------------------------|------------------------------------------------------|
| **Java 21, Spring Boot 3**         | Backend microservices (Order, Inventory, Shipping)   |
| **Spring Cloud (Config, Eureka)**  | Centralized configuration & service discovery        |
| **Spring Cloud Gateway**           | API Gateway (routing external requests)              |
| **PostgreSQL (Docker containers)** | Order and Inventory databases                         |
| **Docker & Docker Compose**        | Containerization and multi-container orchestration    |
| **Zipkin**                         | Distributed tracing (visualize request flow)          |
| **Elasticsearch / Logstash / Kibana (ELK)** | Centralized logging and log analysis        |

---

## 📂 Services & Components

| Container / Service        | Role / Description                                            |
|----------------------------|---------------------------------------------------------------|
| **config-server**          | Spring Cloud Config Server (centralized configuration)         |
| **discovery-service** (Eureka) | Service registry for all microservices (dynamic discovery) |
| **api-gateway**            | Spring Cloud Gateway (routes API calls to backend services)    |
| **order-service**          | Order processing (creates orders, interacts with inventory)    |
| **inventory-service**      | Inventory management (manages product stock levels)            |
| **shipping-service**       | Shipping workflow service                                      |
| **postgres-order**         | PostgreSQL container (stores the `orderDB` database)           |
| **postgres-inventory**     | PostgreSQL container (stores the `inventoryDB` database)       |
| **zipkin**                 | Zipkin server (distributed tracing UI and storage)             |
| **elasticsearch**          | Elasticsearch (stores aggregated application logs)            |
| **logstash**               | Logstash (collects and processes logs from services)          |
| **kibana**                 | Kibana (visualizes logs and metrics from Elasticsearch)       |

---

## 🔗 Architecture

> 📷 **Architecture Diagram:** [Insert diagram of the microservices architecture here]  
> *(Shows how API Gateway, Discovery, Config, services, DBs, Zipkin, and ELK connect.)*

---

## 🚀 Getting Started

**Prerequisites:** Java 21, Maven, and Docker Desktop (with WSL2 on Windows) installed. Verify Docker is running (`docker --version`).

1. **Clone the repository:**
   ```bash
   git clone <your-github-repo-url>
   cd eCommerce
   

2. **Build application artifacts (optional):**
   ```bash
   mvn clean package
   ```
   (This produces JARs for each service, though Dockerfiles can build directly.)

3. **Start the system with Docker Compose:**
   ```bash
   docker compose up --build
   ```
   This command builds images (if needed) and starts all containers. To run in the background, add `-d`.

4. **Verify containers:**
   ```bash
   docker ps
   ```
   (You should see all microservices and infrastructure containers up.) For example, `docker ps` shows running containers.

5. **Monitor logs (optional):**
   ```bash
   docker logs -f <container_name>
   ```
   (Shows live logs for the given container.)

6. **Stop the services:**
   ```bash
   docker compose down
   ```

---

## 📍 Service Endpoints

| Service              | URL                                  |
|----------------------|--------------------------------------|
| Eureka Dashboard     | http://localhost:8761                |
| Config Server        | http://localhost:8888                |
| API Gateway          | http://localhost:5124                |
| Zipkin (Tracing UI)  | http://localhost:9411                |
| Kibana (Logging UI)  | http://localhost:5601                |
| Elasticsearch API    | http://localhost:9200                |
| Order API (via Gateway)     | http://localhost:5124/orders          |

*(After startup, Eureka will list all services on port 8761, and the gateway forwards calls to microservices.)*

---

## 📚 Topics Covered

This project implements the Week-15 Docker & Docker Compose material:

- **15.1 Introduction to Docker:** Learned OS-level virtualization basics. Containers share the host kernel and are lightweight (no guest OS).
- **15.2 Docker Installation & Commands:** Used core Docker commands. _Example:_ `docker run -d -p 8080:80 nginx` publishes container port 80 on host port 8080.
- **15.3 Building Docker Images:** Wrote `Dockerfile` for Spring Boot apps. Example:
  ```dockerfile
  FROM eclipse-temurin:21-jdk
  COPY target/app.jar app.jar
  ENTRYPOINT ["java","-jar","/app.jar"]
  ```
  (As per Spring guide, this simple Dockerfile can run a Spring Boot JAR.)
- **15.4 Docker Compose:** Defined services in `docker-compose.yml` with networks, ports, and volumes. Running `docker compose up` builds and starts all containers.
  - Example Compose snippet:
    ```yaml
    services:
      app:
        image: myapp-image
        ports:
          - "8080:80"
    ```
- **15.5 Containerizing the Architecture:** Each microservice and support component (DBs, Zipkin, ELK) runs in a container. They communicate via Docker network (service hostnames).
- **15.6 Homework:** Practical demos (below) of building images, running services, and using Docker Compose to deploy the full system.

---

## 📝 Homework Implementations

# Homework 1: Build Docker Images of Java Apps
- **Task:** Create Docker images for Java (Spring Boot) applications.
- **Approach:** Wrote a `Dockerfile` (as above) for each app. Ran `docker build -t myapp .` in the project directory.
- **Verification:** Used `docker images` to list built images and `docker run myapp` to test.
- **Commands:**
  ```bash
  mvn clean package
  docker build -t my-java-app ./my-java-app
  docker images
  ```
🎥 *[SCREEN RECORDING: Building Docker Images of Sample Java Apps]

https://github.com/user-attachments/assets/17ca0a91-4419-4f2a-9abc-7c3d0191d201
## Steps Performed

### 1. Built the Spring Boot JAR

``` bash
mvn clean package -DskipTests
```

This generated the executable Spring Boot JAR file.

### 2. Created Docker Image

``` bash
docker build -t banking-app .
```

A Docker image named `banking-app` was created from the project's
Dockerfile.

### 3. Created Docker Network

``` bash
docker network create banking-network
```

This network allows containers to communicate using container names.

### 4. Started PostgreSQL Container

``` bash
docker run -d \
--name postgres \
--network banking-network \
-e POSTGRES_DB=bankingdatabase \
-e POSTGRES_USER=postgres \
-e POSTGRES_PASSWORD=7878 \
-p 5433:5432 \
postgres:16
```

### PostgreSQL Port Mapping

Host Machine: 5433\
Container: 5432

The Banking application connects to PostgreSQL through the Docker
network using the container name `postgres`.

### 5. Started Banking Application Container

``` bash
docker run -d \
--name banking-container \
--network banking-network \
-p 1212:1212 \
-e DB_URL=jdbc:postgresql://postgres:5432/bankingdatabase \
-e DB_USERNAME=postgres \
-e DB_PASSWORD=7878 \
banking-app
```

### Banking Container Port Mapping

Host Machine: 1212\
Container: 1212

The application becomes accessible through:

``` text
http://localhost:1212
```

## Container Communication

``` text
Banking Container
        |
        |
        v
PostgreSQL Container
```

The Banking container communicates with PostgreSQL using:

``` text
postgres:5432
```

where `postgres` is the PostgreSQL container name inside the Docker
network.

## Verification

Commands used:

``` bash
docker ps
docker logs banking-container
docker logs postgres
```

Verification confirmed:

-   PostgreSQL container running successfully.
-   Banking application container running successfully.
-   Application connected to PostgreSQL database.
-   REST APIs tested successfully using Postman.


# Homework 2: Run Open-Source Docker Projects
- **Task:** Pull and run existing Docker images from Docker Hub.
- **Approach:** Used `docker pull` and `docker run`. For example, ran Nginx and PostgreSQL containers.
- **Example:**
  ```bash
  docker pull nginx
  docker run -d -p 8080:80 nginx
  docker pull postgres
  docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=secret postgres
  ```
  Then browsed to `http://localhost:8080` to confirm Nginx, and connected to Postgres to verify.
- **Outcome:** Images ran successfully from public repositories.
🎥 *[SCREEN RECORDING: Pulling and Running Docker Hub Images]*

https://github.com/user-attachments/assets/3d5aa7c1-745a-4d81-8fb5-f6331cf966ec
## Objective

Pull and run existing open-source Docker images from Docker Hub.

## Steps Performed

### 1. Pulled Nginx Image

```bash
docker pull nginx
```

Downloaded the official Nginx image from Docker Hub.

### 2. Created Nginx Container
```bash
docker run -d --name nginx-container -p 8080:80 nginx
```

Port Mapping:

Host Machine: 8080
Container: 80

Verified using:

http://localhost:8080

Nginx welcome page was displayed successfully.

# Homework 3: Dockerize Microservices Architecture
- **Task:** Containerize all Spring Boot microservices together.
- **Approach:** Created a unified `docker-compose.yml` including:
  - discovery-service (Eureka), config-server, api-gateway, order-service, inventory-service, shipping-service
  - postgres-order, postgres-inventory
- **Execution:**
  ```bash
  docker compose up -d
  ```
  All services start and register with Eureka automatically.
- **Verification:** Checked Eureka UI (`http://localhost:8761`) to see all services listed. Used `docker ps` to ensure containers are running.

🎥 *[SCREEN RECORDING: Running Microservices via Docker Compose]*

https://github.com/user-attachments/assets/ede61cc5-9da5-4533-9b0f-1da293bc456d

## Task
Containerize multiple Spring Boot microservices together using Docker Compose.

### Microservices Included

- Discovery Service (Eureka)
- Config Server
- API Gateway
- Order Service
- Inventory Service
- Shipping Service
- PostgreSQL databases

### Docker Compose Execution

Created a unified docker-compose.yml file containing all services.

Command used:

```bash
docker compose up -d
```

-Docker Compose automatically created containers, networks, and started all microservices.

## 1.Container Communication

- All containers communicate through:
ecommerce-network
Services communicate using container names.
Example:

order-service
      |
      |
postgres-order:5432
## 2.Verification

Checked running containers:

docker ps

Checked service logs:

- docker logs order-service
- docker logs discovery-service

Verified Eureka Dashboard:

http://localhost:8761

All microservices were registered successfully.

## 3.Result

Successfully containerized a complete Spring Boot microservices architecture using Docker Compose with service discovery, configuration management, database containers, and inter-container communication.

# Homework 4: Dockerize Kafka & ELK Stack
- **Task:** Integrate a Kafka broker and the ELK logging stack into the Docker setup.
- **Approach:** Added Kafka (broker + Zookeeper) and ELK services to `docker-compose.yml`. Configured services to send logs to Logstash and traces to Zipkin.
- **Result:** `docker compose up` now brings up Kafka, Elasticsearch, Logstash, Kibana alongside the microservices. Verified in Kibana that logs from the services are collected.
🎥 *[SCREEN RECORDING: Starting ELK (and Kafka) Stack and Demonstrating Kibana]*

# Homework 5: Orchestrate with Docker Compose
- **Task:** Deploy the entire application ecosystem with one command.
- **Approach:** The existing `docker-compose.yml` defines all components. Running:
  ```bash
  docker compose up -d
  ```
  spins up the complete system.
- **Result:** The full platform starts automatically. Demonstrated with a Postman API call:
  - Sent a POST to `http://localhost:5124/orders` to create an order (through the API Gateway).
  - Verified the order was created (e.g., via database or subsequent GET).
🎥 *[SCREEN RECORDING: Full System Startup and Order Creation Demo]*

---

## 📸 Demonstration Screenshots

> 📷 *Docker Desktop (containers view)*: All microservice and infrastructure containers running.  
> 📷 *Eureka Dashboard* at `http://localhost:8761`: Services registered and healthy.  
> 📷 *Zipkin Tracing UI*: Sample distributed trace for a request.  
> 📷 *Kibana Dashboard*: Logs from services (filter/search for specific log entries).  
> 📷 *Postman – Order API*: Creating a new order via the API Gateway (HTTP POST).  

*(Add actual screenshots in place of these placeholders.)*

---

## 🛠️ Troubleshooting & Tips

- **WSL2 Memory (Windows):** If Docker seems constrained, edit `%UserProfile%\.wslconfig` to allocate more RAM. Example:  
  ```
  [wsl2]
  memory=8GB
  ```  
  Save and restart WSL (`wsl --shutdown`). (This matches Microsoft guidance on WSL2 configuration.)

- **DBeaver TimeZone Error:** If you see `FATAL: invalid value for parameter "TimeZone": "Asia/Calcutta"`, use `Asia/Kolkata` instead or enable *Replace legacy time zone* in DBeaver’s Advanced settings.

- **Port Conflicts:** Ensure required ports (e.g., 8080, 8761, 5432) are not in use. If a port is busy, change the mapping in `docker-compose.yml` or stop the conflicting service. For example, mapping PostgreSQL:  
  ```yaml
  ports:
    - "5433:5432"
  ```  
  (This publishes container port 5432 on host port 5433.)

- **Useful Docker Commands:**  
  - `docker ps` – list running containers  
  - `docker logs -f <container>` – stream logs  
  - `docker compose up` – start all services  
  - `docker compose down` – stop and remove containers  

---

## 💡 Key Concepts Demonstrated

- **Containerization:** Building Docker images for microservices (via Dockerfile or Maven plugin).  
- **Docker Compose:** Defining and running a multi-container stack (services, networks, volumes).  
- **Service Discovery:** Eureka registers and tracks service instances.  
- **API Gateway:** Spring Cloud Gateway routes external calls to backend services.  
- **Distributed Tracing:** Zipkin visualizes request flows across services.  
- **Centralized Logging:** ELK stack collects and displays logs from all services.  
- **Persistence:** Docker volumes (or named volumes) ensure database data survives container restarts.  
- **DevOps Practices:** Infrastructure-as-Code, environment consistency, portability.

```

| Recording                  | Content / Steps                                                                                        | Duration | Resolution | Narration Focus                                            |
|----------------------------|--------------------------------------------------------------------------------------------------------|----------|------------|------------------------------------------------------------|
| **HW1:** Build Docker Images      | Create a Spring Boot app Dockerfile; run `docker build -t myapp .`; show `docker images`.           | ~2 min   | 1080p      | Explain Dockerfile (FROM, COPY, ENTRYPOINT) and building an image. |
| **HW2:** Open-Source Images       | Pull and run official images (e.g., `nginx`, `postgres`); show accessing the services (web, DB).    | ~2 min   | 1080p      | Highlight `docker pull` and `docker run -d -p` usage.      |
| **HW3:** Microservices Compose    | Run `docker compose up -d` for all services; check Eureka (http://localhost:8761) and `docker ps`.  | ~2 min   | 1080p      | Narrate container startup and service registration in Eureka. |
| **HW4:** ELK (Kafka) Stack       | Start Elasticsearch, Logstash, Kibana (and Kafka); open Kibana to view logs from the services.      | ~2 min   | 1080p      | Demonstrate log indexing in Kibana and Kafka topic usage. |
| **HW5:** Full System Startup      | Use `docker compose up`; execute a sample API call (via Postman) to create an order.                | ~2 min   | 1080p      | One-command deployment; show end-to-end order creation.    |

**Download Instructions:** Copy the **README.md** content above (including all markdown formatting) into your repository’s `README.md` file. Alternatively, [download this content as a Markdown file](sandbox:/mnt/data/README_Week15_Docker.md). Ensure to replace placeholder text and screenshots before publishing.
