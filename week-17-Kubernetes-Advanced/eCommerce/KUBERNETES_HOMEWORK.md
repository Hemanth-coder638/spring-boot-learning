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

## 17.5 Secure Kubernetes Secrets Using Doppler and External Secrets Operator

https://github.com/user-attachments/assets/a8a48de6-511e-47a7-807a-b0c42737ede8

### 1. Workflow — What I Did in This Recording

In this recording, I worked on securely managing database credentials and application secrets in Kubernetes. Instead of keeping sensitive values directly inside Kubernetes application manifests, I configured Doppler and External Secrets Operator to synchronize secrets into Kubernetes.

**Step 1: Configure Database Credentials in Kubernetes Manifests**

First, I inspected the PostgreSQL StatefulSet YAML files, including `order-db.yml` and `inventory-db.yml`.

These manifests configure the database containers, environment variables, database ports, and persistent storage. The databases require credentials such as usernames and passwords to initialize and operate.

Hardcoding real passwords directly into YAML files is not a good security practice because these files may be committed to GitHub.

**Step 2: Configure Doppler**

Next, I opened the Doppler dashboard and accessed my eCommerce project's secret configuration.

Doppler is a secrets management platform that stores sensitive configuration values, such as database passwords, API keys, and application credentials.

I configured the secrets required by my application in Doppler so that Kubernetes could obtain them through the external-secrets integration.

**Step 3: Configure External Secrets Operator**

I worked with the Kubernetes resources used by External Secrets Operator.

External Secrets Operator connects Kubernetes to an external secret management system. In my setup, it retrieves secrets from Doppler and creates or updates Kubernetes Secrets.

The important resources are:

- **SecretStore:** Defines how External Secrets Operator connects to a secret provider.
- **ExternalSecret:** Defines which external secrets to retrieve and how to create the corresponding Kubernetes Secret.

**Step 4: Configure the ExternalSecret Manifest**

I inspected the `order-external-secret.yml` manifest.

This manifest defines an `ExternalSecret` named `order-external-secret`. It references the configured secret store and maps external secret values into a Kubernetes Secret.

For example, database configuration values can include:

- `ORDER_DB_NAME`
- `ORDER_DB_USERNAME`
- `ORDER_DB_PASSWORD`

The external secret configuration specifies which values to retrieve and how they should appear in the resulting Kubernetes Secret.

**Step 5: Apply the ExternalSecret Configuration**

I used `kubectl` to apply the ExternalSecret manifest:

```bash
kubectl apply -f order-external-secret.yml
```

I then checked the external-secret resources and their synchronization status.

The recording shows that I encountered synchronization or reconciliation errors while configuring the secret integration. These errors indicate that applying the YAML file alone does not guarantee that the external secret has been retrieved successfully.

The provider connection, secret names, permissions, and manifest configuration must all be correct for synchronization to succeed.

**Step 6: Inspect the ExternalSecret Status**

I used commands such as:

```bash
kubectl get externalsecret
kubectl describe externalsecret order-external-secret
```

The first command lists ExternalSecret resources. The second displays detailed information, including conditions and events that help identify synchronization problems.

I inspected the status and event messages to troubleshoot the integration.

**Step 7: Verify the Resulting Kubernetes Secret**

Once the configuration is correct and synchronization succeeds, External Secrets Operator creates the Kubernetes Secret referenced by the application.

I can verify the Secret resource using:

```bash
kubectl get secrets
```

To inspect only the names of the keys in a Secret without printing their values, I can use:

```bash
kubectl get secret order-external-secret -o json
```

The JSON output contains encoded Secret data, so I should avoid sharing it publicly. Kubernetes Secret values are commonly Base64-encoded; Base64 is encoding, not encryption.

**Step 8: Verify the Application Resources**

Finally, I checked the Kubernetes Pods and related resources to see whether the eCommerce workloads were running.

This helped me understand that secret management is part of the deployment process: the application must receive the required configuration before it can connect successfully to its database or other services.

### 2. Quick Technical Definitions

| Term | Simple Definition |
|---|---|
| Secret | Sensitive information such as a password, token, or API key. |
| Secret Management | The process of storing, controlling access to, and distributing sensitive information securely. |
| Doppler | A secrets management platform that stores and provides application configuration and sensitive values. |
| External Secrets Operator (ESO) | A Kubernetes operator that retrieves secrets from external providers and creates Kubernetes Secrets. |
| Secret Provider | An external system that stores and supplies secrets, such as Doppler. |
| SecretStore | A Kubernetes resource that configures access to a secret provider within a namespace. |
| ClusterSecretStore | A SecretStore-like resource that can be referenced across namespaces, subject to its configuration and permissions. |
| ExternalSecret | A Kubernetes custom resource that describes which external secret values to retrieve and how to create a Kubernetes Secret. |
| Kubernetes Secret | A Kubernetes resource intended to store sensitive configuration data. |
| Secret Synchronization | The process of retrieving external secret values and updating the corresponding Kubernetes Secret. |
| Reconciliation | The operator's process of repeatedly checking whether the actual resources match the desired configuration and correcting differences. |
| Secret Key | The name associated with a particular value inside a secret, such as `ORDER_DB_PASSWORD`. |
| Environment Variable | A named configuration value that an application can read at runtime. |
| Credentials | Information used to authenticate, such as a username and password. |
| Base64 Encoding | A method of representing binary or text data as characters. It does not provide encryption or confidentiality. |
| Least Privilege | Giving an application or user only the permissions needed to perform its task. |
| RBAC | Role-Based Access Control; Kubernetes authorization based on roles and role bindings. |
| Custom Resource Definition (CRD) | A Kubernetes extension that introduces a new resource type, such as `ExternalSecret`. |
| Operator | Software that automates the management of an application or resource by observing its state and reconciling it. |
| `kubectl apply -f` | Creates or updates Kubernetes resources using a manifest file. |
| `kubectl get secrets` | Lists Kubernetes Secret resources in the selected namespace. |
| `kubectl describe` | Displays detailed resource information, conditions, and events useful for troubleshooting. |

### Important Note

This recording demonstrates the Doppler and External Secrets Operator integration in the course environment. My final eCommerce deployment also uses **Doppler and External Secrets Operator** to manage application secrets in Kubernetes.

For production security, I should avoid committing actual passwords or access tokens to GitHub, restrict access to secrets using RBAC, protect the external provider credentials, and enable appropriate encryption and access controls for Kubernetes Secrets.

Also, an `ExternalSecret` resource showing up in `kubectl get externalsecret` does not prove that synchronization succeeded. I must verify its readiness condition, inspect events when errors occur, and confirm that the expected Kubernetes Secret was created successfully.

# 17.6 Deploy Spring Boot eCommerce Microservices to Civo Kubernetes Using GitHub Actions

https://github.com/user-attachments/assets/841b6bd4-78b7-4fb3-97b5-c7da54ecf1ab

## 1. Workflow — What I Did in This Recording

In Week 17.6, I continued working on my advanced Spring Boot eCommerce microservices project. The main objective was to deploy the application to a Civo Kubernetes cluster through GitHub Actions, troubleshoot a deployment issue, and verify that the application was running correctly and accepting API requests.

My application uses Spring Boot microservices, PostgreSQL databases, Kubernetes service discovery through Kubernetes DNS, Config Server, Docker Hub, and GitHub Actions for automated CI/CD.

During this stage, I encountered a Kubernetes rollout timeout while deploying the API Gateway. I investigated the deployment state, examined application logs and pod health, checked the node's memory usage, and verified the API Gateway health endpoint.

The deployment subsequently completed successfully, and I verified that API requests were working.

### Step 1: Review the Git Repository and Project Structure

First, I worked in my local Git repository on Windows and inspected the Week 17 Kubernetes Advanced project.

My repository details are:

- **GitHub repository:** `Hemanth-coder638/spring-boot-learning`
- **Local repository:** `C:\MySpringProjects\spring-boot-learning`
- **Project directory:** `week-17-Kubernetes-Advanced/eCommerce`
- **Git branch:** `main`

The eCommerce project contains the following microservices and supporting components:

- Discovery Service
- Config Server
- API Gateway
- Inventory Service
- Order Service
- Shipping Service
- PostgreSQL database for Inventory
- PostgreSQL database for Orders
- Zipkin for distributed tracing

The application images are published to Docker Hub under the `hemanths5454` namespace.

I also inspected the GitHub repository to verify that the project files were present remotely.

**Important Git lesson:** Generated Maven `target` directories and Jib cache files appeared in the Git changes shown in my recording. These are build artifacts and normally should not be committed to the repository. I also noticed a file named `token.json`, i have ensured it does not contain credentials or access tokens.

### Step 2: Configure the GitHub Actions CI/CD Workflow

I used the GitHub Actions workflow:

`.github/workflows/ecommerce-cicd.yml`

The workflow automates the build, test, image publishing, and Kubernetes deployment process.

The workflow builds and tests six services:

1. Discovery Service
2. API Gateway
3. Config Server
4. Inventory Service
5. Order Service
6. Shipping Service

The workflow uses Java 21 and PostgreSQL 16 for CI testing. It creates the `orderDB` and `inventoryDB` databases and activates the `local` Spring profile for the relevant tests.

After the build-and-test stage succeeds, the workflow publishes the required Docker images to Docker Hub and updates the corresponding deployments on Civo Kubernetes.

The images are tagged using the Git commit associated with the workflow run. This helps connect a deployed application version to its source-code revision.

### Step 3: Configure the Kubernetes Deployment Architecture

For the Civo environment, I use the Spring profile `k8s`.

The application uses Kubernetes DNS for service-to-service communication rather than relying on Eureka for runtime discovery.

For example, the Config Server URL is:

```text
http://config-server:8888
```

The Discovery Service remains part of the GitHub Actions build-and-test matrix, but its image is not built and published for the Civo deployment because the Kubernetes environment uses native service discovery.

This architecture allows the application services to communicate through Kubernetes Services and their DNS names.

I retained the existing architecture rather than redesigning the application during deployment troubleshooting.

### Step 4: Investigate the GitHub Actions API Gateway Deployment Failure

During a GitHub Actions deployment, the API Gateway deployment step timed out.

The important error message was:

```text
Waiting for deployment "api-gateway" rollout to finish:
1 old replicas are pending termination...
```

The command eventually returned:

```text
error: timed out waiting for the condition
```

The rollout command was using a timeout of 180 seconds.

This indicated that Kubernetes was waiting for an old API Gateway replica to terminate while completing the rollout. It did not automatically mean that the new application version had failed to start.

A Kubernetes Deployment performs a rolling update by creating replacement Pods and gradually terminating old Pods, subject to the deployment strategy and available resources.

When an old Pod takes too long to terminate, the rollout can exceed its configured timeout even when the new application instance has started successfully.

I therefore needed to inspect the actual Kubernetes state instead of assuming that the entire application was down.

### Step 5: Inspect API Gateway Logs and Application Startup

Next, I examined the API Gateway logs to determine whether the new application version had started correctly.

The logs showed the following:

- The `k8s` Spring profile was active.
- The API Gateway successfully fetched configuration from Config Server.
- The Spring Boot application started.
- Netty was listening on port `8080`.
- A Zipkin connection-reset warning was also present.

The successful configuration retrieval and application startup were important signs that the API Gateway itself was functioning.

The Zipkin warning indicated a tracing connectivity issue at that time. It was not sufficient evidence to conclude that Zipkin caused the Kubernetes rollout timeout.

This investigation helped separate application startup problems from Kubernetes rollout and infrastructure problems.

### Step 6: Investigate Pod Eviction and Node Memory Usage

I then examined the Kubernetes Pods and node resource usage.

The observed node metrics were:

| Resource | Observed usage |
|---|---:|
| CPU | 314 millicores (16%) |
| Memory | 2,540 MiB (110%) |

The reported memory usage was particularly important because it was above the displayed memory capacity reference.

The pod memory usage at that time was approximately:

| Pod | Memory usage |
|---|---:|
| API Gateway | 250 MiB |
| Config Server | 205 MiB |
| Inventory Database | 34 MiB |
| Inventory Service | 263 MiB |
| Order Database | 24 MiB |
| Order Service | 148 MiB |
| Shipping Service | 241 MiB |
| Zipkin | 230 MiB |

One API Gateway Pod was reported as evicted because the node was low on memory. The recorded exit code was `137`.

Another API Gateway Pod was `Running` and had `Ready: True`, with zero restarts.

These observations suggested that node memory pressure was a significant issue to investigate. However, the memory figures alone did not establish the exact cause of the eviction or prove that memory was the only reason for the rollout timeout.

This is why I needed to inspect the node conditions, allocated resources, pod status, and Kubernetes events before choosing an infrastructure change.

### Step 7: Verify the API Gateway Deployment State

I checked the API Gateway Deployment and its related Kubernetes resources.

The relevant commands for this investigation were:

```powershell
kubectl get deployment api-gateway -o wide
kubectl get pods -o wide
kubectl get rs
kubectl describe deployment api-gateway
```

These commands help determine:

- How many replicas the Deployment requests.
- How many replicas have been updated and are ready.
- Whether an old ReplicaSet still has Pods terminating.
- Whether a Pod has been evicted or is failing.
- Whether the Deployment is available despite an earlier rollout timeout.

The Deployment description previously showed one available replica and zero unavailable replicas.

This was evidence that the API Gateway was available at that time, even though the earlier GitHub Actions rollout command had timed out.

I used this information to distinguish a deployment command timeout from an application that was completely unavailable.

### Step 8: Verify the API Gateway Health Endpoint

After inspecting the deployment, I verified the API Gateway health endpoint through Kubernetes port-forwarding.

First, I ran the following command in one PowerShell window:

```powershell
kubectl port-forward deployment/api-gateway 18080:8080
```

This forwarded local port `18080` to port `8080` on the API Gateway Deployment.

Then, in another PowerShell window, I executed:

```powershell
Invoke-WebRequest http://localhost:18080/actuator/health
```

The response was:

```text
StatusCode: 200
StatusDescription: OK
```

The health endpoint returned:

```json
{
  "status": "UP",
  "groups": [
    "liveness",
    "readiness"
  ]
}
```

This confirmed that the health endpoint responded successfully through port-forwarding during the verification.

It was an important step because it tested the running application rather than relying only on the GitHub Actions job result.

A successful health check alone does not prove that every microservice, database, and external integration is healthy, so I also needed to verify the application API requests.

### Step 9: Complete the Deployment and Verify API Requests

After the deployment troubleshooting, the eCommerce application deployment completed successfully.

I verified that the application was running and that API requests were working.

This was the final functional validation of the deployment: the goal was not simply to make the GitHub Actions workflow green, but to ensure that the deployed application could actually respond to requests.

The troubleshooting process demonstrated why CI/CD status, Kubernetes rollout status, Pod readiness, application health, and successful API requests should be checked together.

A successful image build does not guarantee a successful rollout. Likewise, a rollout timeout does not automatically prove that the new application is unusable.

The Kubernetes resource state and application responses provide the evidence needed to understand the real deployment status.

### Step 10: Review Git Repository Hygiene

During the recording, I also inspected Git changes and pushed project changes to the remote repository.

The terminal showed a large number of files associated with generated Maven `target` directories and Jib cache artifacts. The output also showed a file named `token.json`.

These observations highlighted an important repository-maintenance task.

Build outputs and temporary container-image cache files should generally be excluded from source control because they increase repository size and can introduce unnecessary generated files into commits.

A token file must also be checked before it is committed. If it contains a real credential that has already been pushed to GitHub, removing it in a later commit is not sufficient to invalidate the credential. The credential should be revoked or rotated.

The recording does not establish whether `token.json` contained a live credential, so its contents and security impact must be verified separately.

### Step 11: Consider Resource Improvements for Future Rollouts

The investigation also identified an opportunity to improve the reliability of Kubernetes rolling updates.

The node had reported high memory usage, and an API Gateway Pod had been evicted. Therefore, future deployment reliability depends on understanding the cluster's actual resource capacity.

The appropriate checks include:

```powershell
kubectl top nodes
kubectl top pods
kubectl describe node k3s-ecommerce-cluster-f78f-1873c8-node-pool-92bb-b7j2h
kubectl get events --sort-by='.lastTimestamp'
```

Depending on the evidence, possible improvements include:

- Reducing unnecessary application memory consumption.
- Reviewing existing Kubernetes memory requests and limits.
- Checking the node's allocatable memory and the total resources requested by Pods.
- Increasing Civo node capacity if the workload genuinely exceeds available resources.
- Adjusting the rollout timeout if the application needs more time to complete an otherwise healthy update.

Increasing the timeout alone does not solve memory exhaustion. Similarly, changing memory limits without understanding the workload can increase the risk of node pressure or container termination.

These improvements should be based on actual resource measurements rather than assumptions.

## 2. Quick Technical Definitions

| Term | Simple Definition |
|---|---|
| Kubernetes | A platform for deploying, scaling, and managing containerized applications. |
| Civo Kubernetes | A managed Kubernetes environment used to run the eCommerce application. |
| CI/CD | Continuous Integration and Continuous Delivery or Deployment; automates testing, building, and releasing software. |
| GitHub Actions | A GitHub automation platform that executes workflows for testing, image publishing, and deployment. |
| Git Commit | A recorded snapshot of changes in a Git repository. |
| Git Push | Sends local commits to a remote repository. |
| Docker Hub | A container registry used to store and distribute Docker images. |
| Jib | A tool for building container images for Java applications without requiring a traditional Dockerfile workflow. |
| Spring Profile | A mechanism for selecting environment-specific Spring Boot configuration. |
| Kubernetes DNS | A naming and discovery mechanism that allows Kubernetes workloads to communicate through Service names. |
| Config Server | A service that provides external configuration to Spring Boot applications. |
| API Gateway | An entry point that routes client requests to backend microservices. |
| Deployment | A Kubernetes resource that manages application replicas and rolling updates. |
| ReplicaSet | A Kubernetes resource that maintains the desired number of matching Pods. |
| Pod | The smallest deployable Kubernetes workload unit, which can contain one or more containers. |
| Rolling Update | A deployment strategy that gradually replaces old Pods with new ones. |
| Rollout Timeout | The maximum time allowed for a rollout-status command to wait for completion. |
| Pod Termination | The process through which Kubernetes shuts down a Pod. |
| Pod Eviction | The removal of a Pod from a node, which can happen when the node experiences resource pressure. |
| Exit Code 137 | Commonly indicates that a process was terminated by `SIGKILL`; the code alone does not prove the exact cause. |
| MemoryPressure | A Kubernetes node condition indicating that memory availability has reached a pressure threshold. |
| Memory Request | The amount of memory Kubernetes uses when making scheduling decisions for a container. |
| Memory Limit | The configured upper memory boundary for a container, enforced by the container runtime and kernel. |
| Allocatable Memory | The portion of node resources available for Kubernetes workloads after system reservations. |
| `kubectl top` | Displays resource usage metrics for nodes and Pods when metrics are available. |
| `kubectl describe` | Shows detailed resource information, status, conditions, and events. |
| `kubectl rollout status` | Reports whether a Deployment rollout has completed. |
| Port-Forwarding | Creates a connection from a local port to a port on a Kubernetes resource. |
| Actuator Health Endpoint | A Spring Boot endpoint used to inspect application health. |
| Readiness Probe | Determines whether a container is ready to receive traffic. |
| Liveness Probe | Helps Kubernetes determine whether a container should be restarted. |
| Zipkin | A distributed tracing system used to observe requests across microservices. |
| Maven `target` Directory | A generated directory containing build outputs and other Maven-generated files. |
| `.gitignore` | A Git configuration file that excludes specified untracked files and directories from normal Git tracking. |
| Secret | Sensitive configuration data, such as a password, API key, or access token. |

## 3. Important Lessons Learned

### A. A GitHub Actions timeout is not always an application failure

The API Gateway rollout command timed out while an old replica was pending termination. The new application had started, and a gateway Pod was reported as ready.

Therefore, I investigated the live Kubernetes state instead of assuming that the application was entirely down.

### B. Node memory must be investigated before changing rollout settings

The observed memory usage was 2,540 MiB, reported as 110%, and an API Gateway Pod had been evicted because the node was low on memory.

This made memory pressure an important investigation area. A longer rollout timeout could provide more time for a healthy update, but it would not resolve insufficient memory capacity.

### C. Health checks and API requests provide functional evidence

The `/actuator/health` endpoint returned HTTP 200 with `status: UP` during port-forwarding verification.

The deployment subsequently completed, and API requests worked. Together, these checks helped validate the deployed application beyond the CI/CD build results.

### D. Keep generated build artifacts out of Git

Maven `target` directories and Jib cache artifacts should normally remain outside source control.

A suitable `.gitignore` should exclude generated build output and local cache files while retaining source code, Maven build configuration, Kubernetes manifests, and GitHub Actions workflows.

### E. Protect credentials before pushing to GitHub

A file named `token.json` appeared in the recorded Git changes. Its contents must be verified without exposing any credentials.

Real tokens should not be committed. If a credential has already been exposed in a remote repository, revoke or rotate it rather than relying only on deleting the file.

### F. Verify the application before triggering another deployment

A reliable deployment verification process checks the Deployment, ReplicaSets, Pods, node resources, health endpoint, and actual API responses.

These checks help distinguish build failures, application startup failures, rollout delays, and infrastructure resource problems.

## 4. Final Project Status

At the end of this stage, my Week 17 advanced Kubernetes eCommerce project had completed its deployment successfully, and API requests were working.

The project uses GitHub Actions to automate the build, test, image-publishing, and deployment workflow. The application runs on Civo Kubernetes, uses Kubernetes DNS for service discovery, retrieves configuration through Config Server, and uses PostgreSQL databases for Inventory and Orders.

The API Gateway rollout timeout was investigated through Kubernetes deployment status, application logs, Pod readiness, memory usage, and health-endpoint verification.

The main lesson from Week 17.6 is that **successful deployment requires more than a green CI/CD workflow: the Kubernetes workloads must be healthy, the infrastructure must have sufficient resources, and the deployed application must respond correctly to requests.**






