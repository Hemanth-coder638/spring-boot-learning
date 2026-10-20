## 17.1 Google Kubernetes Engine Setup with gCloud CLI

https://github.com/user-attachments/assets/79ef9c0e-0daa-49f6-8cf9-b96fdad1b722

### 1. Workflow — What I Did in This Recording

In this recording, I set up the Google Cloud environment and configured my local Windows machine to communicate with a Kubernetes cluster.

**Step 1: Create a Google Cloud Project**

First, I opened the Google Cloud Console and created a project for my Kubernetes work. A Google Cloud project is used to organize cloud resources, APIs, permissions, and billing.

**Step 2: Open Kubernetes Engine**

After creating the project, I opened Kubernetes Engine in the Google Cloud Console and enabled the required service. This allows me to work with Google Kubernetes Engine (GKE).

**Step 3: Install Google Cloud CLI**

Next, I opened the official Google Cloud CLI installation documentation and downloaded the installer for Windows.

Google Cloud CLI provides commands that allow me to manage Google Cloud resources directly from the terminal instead of performing every operation through the browser.

**Step 4: Initialize Google Cloud CLI**

After installation, I opened the terminal and ran:

```bash
gcloud init
```

This command starts the initial configuration process. I signed in to my Google account, selected the Google Cloud project, and configured the required default settings.

**Step 5: Install the GKE Authentication Plugin**

I installed the GKE authentication plugin using:

```bash
gcloud components install gke-gcloud-auth-plugin
```

This plugin allows Kubernetes client tools to authenticate with GKE clusters using Google Cloud credentials.

**Step 6: Connect kubectl to the GKE Cluster**

I configured my local Kubernetes client to connect to the `ecommerce-cluster` in the `asia-south1` region:

```bash
gcloud container clusters get-credentials ecommerce-cluster --region=asia-south1
```

This command retrieves the cluster connection details and updates my local kubeconfig file.

**Step 7: Verify Kubernetes Access**

Finally, I ran:

```bash
kubectl get ns
```

The command displayed the Kubernetes namespaces, including `default` and `kube-system`, along with GKE-managed namespaces. This confirmed that `kubectl` could communicate with the cluster.

### 2. Quick Technical Definitions

| Term | Simple Definition |
|---|---|
| Google Cloud | Google's cloud platform for running applications and managing cloud resources. |
| Google Cloud Project | A container used to organize cloud resources, APIs, permissions, and billing. |
| GKE | Google Kubernetes Engine is Google's managed service for running Kubernetes clusters. |
| Google Cloud CLI | A command-line tool used to manage Google Cloud resources through commands. |
| `gcloud init` | Initializes the CLI by configuring account access, project selection, and other settings. |
| GKE Authentication Plugin | Helps `kubectl` authenticate with a GKE cluster using Google Cloud credentials. |
| Kubernetes | A platform for deploying and managing containerized applications. |
| Kubernetes Cluster | A group of control-plane and worker-node resources used to run Kubernetes workloads. |
| `kubectl` | The command-line tool used to communicate with and manage Kubernetes clusters. |
| kubeconfig | A configuration file containing Kubernetes cluster connection details and authentication information. |
| Namespace | A way to organize Kubernetes resources within a cluster. |
| `kube-system` | A Kubernetes namespace that contains system components and resources. |
| Region | A geographical location where cloud resources can run. |
| GKE Credentials | The connection and authentication configuration that allows local tools to access a GKE cluster. |

### Important Note

This recording demonstrates the GKE setup covered in the course lesson. For my final Week 17 eCommerce deployment, I used **Civo Kubernetes** instead of GKE. The underlying Kubernetes concepts remain applicable, although the cluster setup and authentication commands differ between cloud providers.

## 17.2 PostgreSQL StatefulSets and Zipkin Deployment

https://github.com/user-attachments/assets/08cc26a7-35d3-44e2-bd19-a8808b70b7e8

### 1. Workflow — What I Did in This Recording

In this recording, I created Kubernetes YAML manifests for the PostgreSQL databases used by my eCommerce application. I also deployed Zipkin and verified that the resources were running in my GKE cluster.

**Step 1: Create the Kubernetes YAML Manifests**

First, I opened my `k8s` folder in Visual Studio Code and created YAML configuration files for the database resources.

The files shown in the recording were:

- `order-db.yml`
- `inventory-db.yml`
- `zipkin.yml`

These YAML files describe the resources I want Kubernetes to create.

**Step 2: Configure the Order Database StatefulSet**

I configured the `order-db.yml` manifest using `kind: StatefulSet`.

The configuration defines the database Pod, its labels, the Service associated with it, the PostgreSQL container, and persistent storage.

A StatefulSet is useful for databases because it provides stable Pod identity and works with persistent storage.

**Step 3: Configure the Inventory Database StatefulSet**

Next, I configured `inventory-db.yml` for the inventory database.

I used the PostgreSQL 16 container image:

```yaml
image: postgres:16
```

I configured PostgreSQL to use port `5432` and mounted the database storage at:

```yaml
mountPath: /var/lib/postgresql/data
```

This is the directory where PostgreSQL stores its database files inside the container.

**Step 4: Configure Persistent Storage**

I added a `volumeClaimTemplates` section to the StatefulSet configuration.

The manifest requested `1Gi` of storage and used the `ReadWriteOnce` access mode.

This allows Kubernetes to provision persistent storage for the database Pod, depending on the cluster's configured storage provisioner.

**Step 5: Configure the Zipkin Deployment**

I also created `zipkin.yml` to deploy Zipkin, which is used for distributed tracing.

The configuration creates a Zipkin Deployment and a Service exposing port `9411`. Other application components can send tracing information to Zipkin so I can investigate requests across microservices.

**Step 6: Apply the YAML Manifests**

After preparing the manifests, I opened Google Cloud SDK Shell and moved to my Kubernetes configuration directory:

```bash
cd C:\MySpringProjects\eCommerce\k8s
```

I applied the database and Zipkin manifests to the cluster using `kubectl apply -f` with the manifest files.

Kubernetes then created the following resources:

- `order-db` StatefulSet
- `order-db-service` Service
- `inventory-db` StatefulSet
- `inventory-db-service` Service
- Zipkin Deployment
- Zipkin Service

**Step 7: Verify the Pods**

I ran:

```bash
kubectl get pods
```

The output showed these Pods in the `Running` state:

- `inventory-db-0`
- `order-db-0`
- A Zipkin Pod

Each displayed `1/1` under the READY column, meaning the single container in each Pod was ready.

**Step 8: Verify the Services**

Finally, I ran:

```bash
kubectl get services
```

This displayed the database Services and Zipkin Service. The database Services used port `5432`, while Zipkin used port `9411`.

This confirmed that Kubernetes had created the resources and exposed the expected ports inside the cluster.

### 2. Quick Technical Definitions

| Term | Simple Definition |
|---|---|
| YAML | A human-readable format used to write Kubernetes configuration files. |
| Kubernetes Manifest | A YAML or JSON file that describes the Kubernetes resources I want to create or manage. |
| StatefulSet | A Kubernetes workload controller designed for applications that need stable Pod identities and persistent storage. |
| Deployment | A Kubernetes controller that manages stateless application Pods and their updates. |
| Pod | The smallest deployable unit in Kubernetes. It contains one or more containers. |
| PostgreSQL | An open-source relational database used to store structured application data. |
| Container Image | A packaged application template used to create containers. |
| `postgres:16` | The PostgreSQL container image using the PostgreSQL 16 release. |
| Service | A Kubernetes resource that provides a stable network endpoint for accessing selected Pods. |
| ClusterIP | The default Service type, which exposes a Service through an internal cluster IP. |
| Port `5432` | PostgreSQL's default network port. |
| Port `9411` | The port commonly used by Zipkin's web interface and API. |
| Persistent Storage | Storage that can retain data beyond the lifetime of an individual container. |
| PersistentVolume (PV) | A Kubernetes resource representing storage available to the cluster. |
| PersistentVolumeClaim (PVC) | A request for storage made by a Kubernetes workload. |
| `volumeClaimTemplates` | A StatefulSet field that lets Kubernetes create a separate PVC for each Pod. |
| `ReadWriteOnce` | A storage access mode that allows a volume to be mounted read-write by a single node at a time. It does not necessarily mean only one Pod can use it. |
| `mountPath` | The path inside a container where a volume is mounted. |
| `kubectl apply -f` | Creates or updates Kubernetes resources based on a manifest file. |
| `kubectl get pods` | Lists Pods and their current status in the selected namespace. |
| `kubectl get services` | Lists Services in the selected namespace. |
| Distributed Tracing | A way to follow a request as it travels through multiple services. |
| Zipkin | A distributed tracing system used to inspect request flows and timing across microservices. |

### Important Note

This recording demonstrates the database and Zipkin setup on GKE as part of the course lesson. My final Week 17 eCommerce deployment uses **Civo Kubernetes**. The same StatefulSet, Service, storage, and tracing concepts apply, but the actual storage configuration depends on the cloud provider and its StorageClass.

A StatefulSet and persistent volume help preserve database data, but they do not replace database backups or a proper recovery strategy.

## 17.3 Microservices Setup with Kubernetes

https://github.com/user-attachments/assets/ba56d4e0-a51a-4912-9dff-839b506cf850

### 1. Workflow — What I Did in This Recording

In this recording, I worked on deploying my eCommerce microservices to Kubernetes and verifying that the services could communicate with one another. I also checked the application through Postman and inspected the database from inside Kubernetes.

**Step 1: Prepare the Microservices Configuration**

First, I worked with the eCommerce project in IntelliJ IDEA. The project contains separate services for different responsibilities:

- API Gateway
- Config Server
- Inventory Service
- Order Service
- Shipping Service

I worked with the Kubernetes configuration files in the `k8s` folder. These YAML manifests describe how Kubernetes should run the microservices and expose them through Services.

**Step 2: Build and Push the Microservice Docker Image**

I built and pushed the Order Service image using Maven and Jib.

Jib packages a Java application into a container image and can push the image directly to a container registry without requiring me to write a Dockerfile for every service.

The image shown in the recording was:

```text
hemanths5454/ecommerce-order-service
```

After a successful build, the image was available in Docker Hub for Kubernetes to pull and run.

**Step 3: Configure the Kubernetes Manifests**

I worked with the Kubernetes YAML files for the microservices, including configurations for the API Gateway, Config Server, Inventory Service, Order Service, and Shipping Service.

These manifests define resources such as Deployments and Services. A Deployment manages the application Pods, while a Service provides a stable network endpoint through which other components can reach them.

**Step 4: Deploy the Microservices**

I used `kubectl` to inspect and manage the resources in my Kubernetes cluster.

For example, the following command lists the Pods:

```bash
kubectl get pods
```

In the recording, the output showed the Config Server, Inventory Service, Order Service, Shipping Service, Zipkin, and database Pods in the `Running` state, with their containers ready.

**Step 5: Check the Kubernetes Scheduling Status**

I also inspected Kubernetes events when a Pod was having difficulty starting.

The output showed an `Insufficient memory` scheduling warning for a workload. This means Kubernetes could not find a suitable node with enough available memory to satisfy the Pod's resource requirements at that time.

This helped me understand that deploying an application successfully requires not only correct YAML configuration but also sufficient cluster resources.

**Step 6: Configure Communication Between Microservices**

I inspected the Feign client configuration used by the Order Service to communicate with other services.

For example, the `InventoryOpenFeignClient` interface defines methods for reducing and restoring inventory stock through the Inventory Service.

The client configuration shown in the recording includes:

```java
@FeignClient(
    name = "inventory-service",
    path = "/inventory",
    url = "${...}"
)
```

The URL property is supplied through configuration. The exact value depends on the active application profile and configuration source.

Feign allows the Order Service to call the Inventory Service through Java interfaces instead of manually constructing HTTP requests for every operation.

**Step 7: Test the API Through Postman**

Next, I opened Postman and sent a request to the API Gateway to obtain an access token.

The request returned **HTTP 200 OK** with a token in the response. This demonstrated that the token endpoint was reachable and responding successfully at the time of the test.

An access token can then be used to authorize requests to protected application endpoints.

**Step 8: Verify the Database**

Finally, I connected to PostgreSQL from inside the Order Database Pod using `kubectl exec` and the PostgreSQL command-line client.

The command shown was:

```bash
kubectl exec -it order-db-0 -- psql -U user -d orderDB
```

Inside PostgreSQL, I ran:

```sql
\dt
```

The output listed the `order_item` and `orders` tables in the `orderDB` database.

This demonstrated how I can access a database running inside Kubernetes and inspect its tables for troubleshooting and verification.

### 2. Quick Technical Definitions

| Term | Simple Definition |
|---|---|
| Microservices | An architecture in which an application is divided into smaller services with separate responsibilities. |
| API Gateway | An entry point that routes client requests to the appropriate backend services. |
| Config Server | A central service that provides configuration to application services. |
| Inventory Service | The service responsible for inventory and stock-related operations. |
| Order Service | The service responsible for order-related operations. |
| Shipping Service | The service responsible for shipping-related operations. |
| Docker Image | A packaged application template from which a container can be created. |
| Docker Hub | A container registry where images can be stored and downloaded. |
| Jib | A tool for building Java application container images and pushing them to a registry. |
| Kubernetes Manifest | A YAML or JSON file describing the resources Kubernetes should create or manage. |
| Deployment | A Kubernetes resource that manages application Pods and their updates. |
| Service | A Kubernetes resource that provides a stable network endpoint for accessing Pods. |
| Pod | The smallest deployable unit in Kubernetes, containing one or more containers. |
| `kubectl get pods` | Lists Pods and their status in the selected namespace. |
| `kubectl apply -f` | Creates or updates Kubernetes resources from a manifest file. |
| Pod Scheduling | The process by which Kubernetes selects a suitable node to run a Pod. |
| Resource Requests | The CPU and memory amounts Kubernetes uses when deciding where a Pod can be scheduled. |
| Resource Limits | The maximum CPU and memory resources a container is allowed to use, subject to Kubernetes enforcement. |
| Insufficient Memory | A scheduling condition in which available node memory cannot satisfy a Pod's memory request. |
| OpenFeign | A declarative HTTP client that lets Java applications call remote services through interfaces. |
| Feign Client | An interface configured to make HTTP requests to another service. |
| Service-to-Service Communication | Communication between backend services using a network protocol such as HTTP. |
| Postman | A tool for sending API requests and inspecting responses. |
| HTTP 200 OK | An HTTP status indicating that a request succeeded. |
| Access Token | A credential used to authorize access to protected APIs. |
| `kubectl exec` | Executes a command inside a running container in a Kubernetes Pod. |
| `psql` | The command-line client for connecting to and working with PostgreSQL. |
| `\dt` | A PostgreSQL `psql` command that lists tables visible in the current database and schema search path. |
| Zipkin | A distributed tracing tool for investigating requests across multiple microservices. |

### Important Note

This recording demonstrates microservice deployment and troubleshooting concepts using the GKE environment shown in the lesson. My final Week 17 eCommerce deployment uses **Civo Kubernetes**.

The core concepts remain the same across Kubernetes providers. However, the image names, configuration values, resource requirements, storage configuration, and cluster access settings must match the actual deployment environment.

A Pod showing `Running` does not automatically prove that the entire application is working correctly. I must also check container readiness, service connectivity, application logs, API responses, and database operations.

## 17.4 Ingress Endpoints in Kubernetes And Deploying Microservices on Civo Kubernetes

https://github.com/user-attachments/assets/d563bcb7-55d6-4af8-a0ad-557a375b4f80

### 1. Workflow — What I Did in This Recording

In this recording, I switched my deployment environment from Google Kubernetes Engine (GKE) to Civo Kubernetes because I wanted to continue the project without depending on my previous Google Cloud credits. I then configured local Kubernetes access, deployed the required resources, and verified the eCommerce microservices.

**Step 1: Open the Civo Kubernetes Cluster**

First, I opened the Civo dashboard and accessed my Kubernetes cluster named `ecommerce-cluster`.

I checked the cluster information, including its Kubernetes version, node pool, API endpoint, and kubeconfig download option.

I downloaded the kubeconfig file so that my Windows machine could communicate with the Civo cluster using `kubectl`.

**Step 2: Configure the kubeconfig File**

Next, I opened PowerShell and configured the `KUBECONFIG` environment variable to point to my downloaded Civo configuration file.

```powershell
$env:KUBECONFIG="C:\Users\HP\Downloads\civo-ecommerce-cluster-kubeconfig"
```

I initially encountered a PowerShell variable syntax error and corrected the command. After that, I checked the environment variable value.

This configuration tells `kubectl` which Kubernetes connection configuration to use in the current PowerShell session.

**Step 3: Verify the Kubernetes Node**

I ran:

```bash
kubectl get nodes
```

The output showed my Civo Kubernetes node in the `Ready` state.

This confirmed that my local Kubernetes client could communicate with the cluster and that the node was available to run workloads.

**Step 4: Open the Kubernetes Configuration Directory**

I navigated to my project's Kubernetes manifest directory:

```powershell
cd C:\MySpringProjects\eCommerce\k8s
```

Then I ran:

```powershell
dir
```

This displayed my Kubernetes YAML files, including the configurations for the API Gateway, Config Server, Inventory Service, Order Service, Shipping Service, database StatefulSets, Ingress, and Zipkin.

**Step 5: Apply the Order Database Configuration**

I applied the Order Database manifest:

```bash
kubectl apply -f order-db.yml
```

Kubernetes created the `order-db` StatefulSet and `order-db-service` Service.

I then checked the Pods and Services:

```bash
kubectl get pods
kubectl get svc
```

The Order Database Pod initially showed `Pending`. This demonstrated that creating a Kubernetes resource does not guarantee that its Pod becomes ready immediately. Kubernetes still needs to schedule the Pod and make its required storage and resources available.

**Step 6: Verify the Application Resources**

I continued checking the Kubernetes resources using:

```bash
kubectl get pods
kubectl get svc
kubectl get ingress
kubectl get nodes
```

These commands allowed me to verify the status of the Pods, internal Services, Ingress configuration, and cluster node.

In the later output, the API Gateway, Config Server, Inventory Service, Order Service, Shipping Service, database Pods, and Zipkin Pod were shown as running and ready.

I also checked the Kubernetes Services and confirmed that the microservices had internal cluster IP addresses and the expected ports.

**Step 7: Verify the Ingress Configuration**

I checked the Ingress resource:

```bash
kubectl get ingress
```

The output showed the `myingress` resource using Traefik and a Civo Kubernetes hostname.

Ingress provides an entry point for external HTTP traffic. The configured Ingress rules determine how requests are routed to the appropriate Kubernetes Services.

**Step 8: Check the Application Logs and Resource Status**

I inspected the terminal output while checking the application resources and service startup logs.

This helped me understand how to troubleshoot a Kubernetes deployment by checking Pod status, readiness, restarts, and application startup messages rather than relying only on whether a Deployment was created.

**Step 9: Confirm the Deployment in the Civo Dashboard**

Finally, I returned to the Civo dashboard and checked the cluster and node-pool information.

This completed the transition to using Civo Kubernetes for my project and confirmed that I could manage the cluster through both the Civo dashboard and local Kubernetes command-line tools.

### 2. Quick Technical Definitions

| Term | Simple Definition |
|---|---|
| Civo Kubernetes | A managed Kubernetes service that provides a cluster for running containerized applications. |
| Cloud Provider | A company that provides cloud infrastructure and services, such as compute, storage, and managed Kubernetes. |
| Kubernetes Cluster | A group of Kubernetes control-plane and worker resources used to run applications. |
| Node | A machine in a Kubernetes cluster that runs Pods. |
| `Ready` Node | A node that Kubernetes considers healthy and available to run workloads. |
| kubeconfig | A configuration file containing cluster connection details, credentials, and contexts. |
| `KUBECONFIG` | An environment variable that can tell Kubernetes client tools which configuration file to use. |
| PowerShell | A command-line shell and scripting environment available on Windows. |
| `kubectl` | The command-line tool used to communicate with Kubernetes clusters. |
| `kubectl get nodes` | Lists the nodes in the currently selected Kubernetes cluster. |
| `kubectl apply -f` | Creates or updates Kubernetes resources from a manifest file. |
| StatefulSet | A Kubernetes workload controller for applications that need stable Pod identities and persistent storage. |
| Deployment | A Kubernetes resource that manages application Pods and their updates. |
| Service | A stable network endpoint used to access selected Pods. |
| ClusterIP | The default Service type, providing an internal cluster network endpoint. |
| Pod | The smallest deployable Kubernetes unit, containing one or more containers. |
| `Pending` Pod | A Pod that has not yet been scheduled successfully or has not completed the steps required to start. |
| `Running` Pod | A Pod whose containers have been created and are running, though readiness should still be checked separately. |
| Readiness | Indicates whether a container is ready to serve requests according to its readiness checks. |
| Restart Count | The number of times a container in a Pod has restarted. |
| Ingress | A Kubernetes resource that defines rules for routing external HTTP or HTTPS traffic to Services. |
| Traefik | An ingress controller and reverse proxy that can route incoming traffic to Kubernetes Services. |
| Ingress Hostname | A hostname used to reach an application through the configured Ingress. |
| Internal Service IP | A cluster network address used to access a Kubernetes Service internally. |
| Port Mapping | The configuration that connects traffic on a particular network port to an application or Service port. |
| Resource Scheduling | Kubernetes' process of choosing a suitable node for a Pod based on its requirements and cluster availability. |

### Important Note

This recording demonstrates the move to Civo Kubernetes and the deployment checks performed on the cluster. My final Week 17 eCommerce project uses **Civo Kubernetes**, not GKE.

The exact cluster name, kubeconfig path, hostname, storage configuration, and node details are specific to my environment. The Kubernetes concepts remain applicable to other cloud providers.

Also, a Pod being `Running` does not necessarily mean the application is fully functional. I must verify readiness, application logs, service connectivity, API responses, and database operations.






