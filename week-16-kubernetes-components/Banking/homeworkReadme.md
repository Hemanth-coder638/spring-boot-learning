# Homework 1 — Deploy a Simple Web Application using Kubernetes and Minikube
## Homework 1 ScreenRecord

https://github.com/user-attachments/assets/c48db050-e9de-4459-b8d2-2119d29675d7

## 1. Objective

The objective of this homework is to deploy a simple web application using **Nginx** on a local Kubernetes cluster created with **Minikube**.

In this practical, we will:

* Create a Kubernetes Deployment for Nginx.
* Understand how a Deployment creates and manages a ReplicaSet.
* Understand how a ReplicaSet creates and maintains Pods.
* Expose the Nginx application using a Kubernetes Service.
* Access the Nginx application from the host machine.
* Scale the application from 1 replica to 3 replicas.
* Verify that the Service can route traffic to all 3 replicas.
* Demonstrate Kubernetes self-healing by deleting a Pod and observing Kubernetes automatically create a replacement.

---

# 2. Technologies Used

| Technology | Purpose                                                |
| ---------- | ------------------------------------------------------ |
| Kubernetes | Container orchestration platform                       |
| Minikube   | Runs a local Kubernetes cluster                        |
| Docker     | Container runtime used by Minikube                     |
| kubectl    | Command-line tool used to communicate with Kubernetes  |
| Nginx      | Sample web application                                 |
| NodePort   | Exposes the application outside the Kubernetes cluster |

---

# 3. What is Kubernetes?

**Kubernetes** is an open-source container orchestration platform used to deploy, manage, scale, and maintain containerized applications.

Instead of manually managing containers, Kubernetes allows us to define the desired state of an application.

For example, if we tell Kubernetes:

```text
I need 3 Nginx Pods
```

Kubernetes continuously tries to maintain:

```text
Desired state = 3 Pods
Actual state  = 3 Pods
```

If one Pod crashes or is deleted:

```text
Desired = 3
Actual  = 2
```

Kubernetes detects the difference and creates another Pod.

This is one of the most important concepts of Kubernetes:

> **Kubernetes continuously works to make the actual state match the desired state.**

---

# 4. What is Minikube?

**Minikube** is a tool that allows us to run a Kubernetes cluster locally on a computer.

Normally, a Kubernetes cluster may contain multiple physical or virtual machines. For learning and development, creating such infrastructure would be unnecessary.

Minikube allows us to run a Kubernetes cluster on our local machine.

In this project, the architecture is:

```text
Windows Computer
       |
       v
Docker Desktop
       |
       v
Minikube
       |
       v
Kubernetes Cluster
       |
       v
Nginx Application
```

In our environment, Minikube is running Kubernetes using the Docker driver.

---

# 5. What is a Kubernetes Node?

A **Node** is a machine that runs Kubernetes workloads.

A Node can be:

* A physical machine
* A virtual machine
* A local machine running a Kubernetes environment

In our Minikube setup, we have one Kubernetes Node:

```text
minikube
```

We verified it using:

```bash
kubectl get nodes
```

The Node was shown as:

```text
NAME       STATUS   ROLES           VERSION
minikube   Ready    control-plane   v1.35.1
```

The important status is:

```text
Ready
```

This means the Node is available to run workloads.

---

# 6. Kubernetes Cluster Architecture

Our local Kubernetes environment can be represented as:

```text
+------------------------------------------------------+
|                Windows Computer                      |
|                                                      |
|  +------------------------------------------------+  |
|  |              Docker Desktop                    |  |
|  |                                                |  |
|  |  +--------------------------------------------+|  |
|  |  |             Minikube Cluster               ||  |
|  |  |                                            ||  |
|  |  |  +--------------------------------------+  ||  |
|  |  |  |          Kubernetes Node            |  ||  |
|  |  |  |            "minikube"               |  ||  |
|  |  |  |                                    |  ||  |
|  |  |  |   Kubernetes Components            |  ||  |
|  |  |  |                                    |  ||  |
|  |  |  |   Deployment                       |  ||  |
|  |  |  |       |                            |  ||  |
|  |  |  |       v                            |  ||  |
|  |  |  |   ReplicaSet                       |  ||  |
|  |  |  |       |                            |  ||  |
|  |  |  |       +---- Pod 1                  |  ||  |
|  |  |  |       +---- Pod 2                  |  ||  |
|  |  |  |       +---- Pod 3                  |  ||  |
|  |  |  |                                    |  ||  |
|  |  |  +--------------------------------------+  ||  |
|  |  +--------------------------------------------+|  |
|  +------------------------------------------------+  |
+------------------------------------------------------+
```

---

# 7. What is a Pod?

A **Pod** is the smallest deployable unit in Kubernetes.

A Pod usually contains one or more containers.

In our application, each Pod contains one Nginx container.

For example:

```text
Pod
+-----------------------------+
|                             |
|      Nginx Container        |
|                             |
|      Nginx Web Server       |
|                             |
+-----------------------------+
```

We created our first Pod indirectly through the Deployment.

We did not manually create the Pod.

Instead, we created:

```text
Deployment
    |
    v
ReplicaSet
    |
    v
Pod
    |
    v
Nginx Container
```

This is an important Kubernetes principle:

> Normally, applications are managed through higher-level Kubernetes objects such as Deployments rather than manually creating individual Pods.

---

# 8. Why do we need Pods?

Containers need an environment in which Kubernetes can manage them.

Pods provide Kubernetes with a unit for:

* Running containers
* Networking
* Storage
* Resource management
* Health management

In our example, each Nginx Pod runs the Nginx web server.

If we have three Pods:

```text
Pod 1 → Nginx
Pod 2 → Nginx
Pod 3 → Nginx
```

we have three instances of our Nginx application.

---

# 9. What is a ReplicaSet?

A **ReplicaSet** ensures that a specified number of identical Pods are running.

For example:

```text
Desired replicas = 3
```

The ReplicaSet continuously tries to maintain:

```text
3 Running Pods
```

If one Pod is deleted:

```text
Before:

Pod 1
Pod 2
Pod 3

        ↓

Pod 2 deleted

        ↓

Pod 1
Pod 3
```

The ReplicaSet detects that only two Pods are running.

It then creates another Pod:

```text
Pod 1
Pod 3
Pod 4
```

Therefore:

```text
Desired = 3
Actual  = 3
```

---

# 10. Why don't we normally create ReplicaSets manually?

A Deployment automatically creates and manages a ReplicaSet.

Therefore, our architecture is:

```text
Deployment
     |
     v
ReplicaSet
     |
     +-------- Pod
     |
     +-------- Pod
     |
     +-------- Pod
```

The Deployment provides higher-level management such as:

* Scaling
* Rolling updates
* Rollbacks
* Managing ReplicaSets

The ReplicaSet focuses primarily on maintaining the required number of Pods.

---

# 11. What is a Deployment?

A **Deployment** is a Kubernetes object used to manage applications that usually run as stateless Pods.

Instead of manually creating Pods, we tell Kubernetes what application we want and how many replicas we need.

For this homework, we created:

```text
nginx-deployment
```

using:

```bash
kubectl create deployment nginx-deployment --image=nginx
```

This command tells Kubernetes:

```text
Application name = nginx-deployment
Container image  = nginx
```

Kubernetes then creates the required resources.

---

# 12. Deployment → ReplicaSet → Pod

The relationship can be represented as:

```text
                  Deployment
                       |
                       |
                       v
                 ReplicaSet
                       |
          +------------+------------+
          |            |            |
          v            v            v
        Pod 1        Pod 2        Pod 3
          |            |            |
          v            v            v
        Nginx        Nginx        Nginx
       Container    Container    Container
```

This is one of the most important diagrams to understand for Kubernetes.

---

# 13. Creating the Nginx Deployment

We created the Deployment using:

```bash
kubectl create deployment nginx-deployment --image=nginx
```

The `--image=nginx` option tells Kubernetes to use the official Nginx container image.

After creation, we verified the Deployment:

```bash
kubectl get deployments
```

We also checked the Pods:

```bash
kubectl get pods
```

And the ReplicaSet:

```bash
kubectl get rs
```

The initial architecture was:

```text
Deployment
     |
     v
ReplicaSet
     |
     v
1 Nginx Pod
```

---

# 14. What is a Kubernetes Service?

Pods are temporary.

A Pod can be deleted and recreated, and a replacement Pod can receive a different IP address.

Therefore, applications should not depend directly on individual Pod IP addresses.

A **Service** provides a stable network endpoint for accessing a group of Pods.

Our architecture is:

```text
Client
  |
  v
Service
  |
  +-------- Pod 1
  |
  +-------- Pod 2
  |
  +-------- Pod 3
```

The Service selects the appropriate Pods using labels.

---

# 15. Why do we need a Service for Nginx?

Our Nginx Pods run inside the Kubernetes cluster.

If we only have Pods:

```text
Nginx Pod
10.244.x.x
```

that does not provide a convenient stable way for our Windows browser to access the application.

We therefore created a Service:

```bash
kubectl expose deployment nginx-deployment --type=NodePort --port=80
```

The Service provides a stable access point for the Nginx application.

---

# 16. What is NodePort?

A **NodePort** Service exposes an application through a port on the Kubernetes Node.

Our Service was created with:

```text
Service port = 80
NodePort     = 30279
```

We verified this using:

```bash
kubectl get services
```

The result contained:

```text
80:30279/TCP
```

This means:

```text
Port 80
    |
    v
Nginx Service
    |
    v
NodePort 30279
```

---

# 17. Accessing Nginx from the Host Machine

We used:

```bash
minikube service nginx-deployment
```

Minikube provided a URL and opened the application in the browser.

The browser displayed:

```text
Welcome to nginx!
```

Therefore, the complete traffic flow was:

```text
Windows Browser
       |
       v
Minikube Node
       |
       v
NodePort :30279
       |
       v
Nginx Service
       |
       v
Nginx Pod
       |
       v
Nginx Container
       |
       v
Nginx Web Page
```

---

# 18. Scaling the Deployment

Initially, our Deployment had one replica.

We scaled it to three replicas using:

```bash
kubectl scale deployment nginx-deployment --replicas=3
```

After scaling, we verified:

```bash
kubectl get deployments
```

The Deployment showed:

```text
READY   UP-TO-DATE   AVAILABLE
3/3     3            3
```

This means all three desired replicas were running and available.

---

# 19. Three Nginx Pods

After scaling, Kubernetes created three Pods.

Conceptually:

```text
                    nginx-deployment
                           |
                           v
                      ReplicaSet
                           |
             +-------------+-------------+
             |             |             |
             v             v             v
          Pod 1         Pod 2         Pod 3
          Nginx         Nginx         Nginx
```

We verified the Pods using:

```bash
kubectl get pods
```

All three Pods showed:

```text
1/1 Running
```

---

# 20. How Kubernetes maintains 3 replicas

Suppose our desired number of replicas is:

```text
3
```

Kubernetes continuously compares:

```text
Desired State
     vs
Actual State
```

For example:

```text
Desired = 3
Actual  = 3
```

Everything is healthy.

If one Pod is deleted:

```text
Desired = 3
Actual  = 2
```

Kubernetes detects the difference.

The ReplicaSet creates a replacement:

```text
Desired = 3
Actual  = 3
```

This is called **self-healing**.

---

# 21. Self-Healing Demonstration

We tested Kubernetes self-healing by deleting one Nginx Pod.

For example:

```bash
kubectl delete pod <pod-name>
```

After deleting the Pod, Kubernetes automatically created a replacement Pod.

We verified this using:

```bash
kubectl get pods
```

Eventually, the number of running Pods returned to:

```text
3
```

This demonstrates that Kubernetes does not simply run containers.

It continuously manages the desired state of the application.

---

# 22. Service Connecting to Three Pods

After scaling, the Service had three endpoints.

We verified this using:

```bash
kubectl get endpoints nginx-deployment
```

The result showed three Pod IP addresses:

```text
10.244.0.3:80
10.244.0.4:80
10.244.0.5:80
```

The exact IP addresses can differ between environments.

The important point is that the Service had three backend endpoints.

The architecture therefore became:

```text
                    Nginx Service
                         |
          +--------------+--------------+
          |              |              |
          v              v              v
      Pod 1           Pod 2           Pod 3
   10.244.0.3      10.244.0.4      10.244.0.5
       :80             :80             :80
```

---

# 23. Complete Homework Architecture

The complete application architecture is:

```text
                         USER
                          |
                          |
                    Windows Browser
                          |
                          v
                  Minikube Service
                          |
                    NodePort :30279
                          |
                          v
              +-----------------------+
              |   Nginx Service       |
              |   ClusterIP           |
              +-----------+-----------+
                          |
             Selects Pods using labels
                          |
          +---------------+---------------+
          |               |               |
          v               v               v
      +-------+       +-------+       +-------+
      | Pod 1 |       | Pod 2 |       | Pod 3 |
      | Nginx |       | Nginx |       | Nginx |
      +-------+       +-------+       +-------+
          ^               ^               ^
          |               |               |
          +---------------+---------------+
                          |
                     ReplicaSet
                          ^
                          |
                     Deployment
                          ^
                          |
                    Kubernetes
                          ^
                          |
                       Minikube
                          ^
                          |
                     Docker Desktop
                          ^
                          |
                    Windows Machine
```

---

# 24. Complete Kubernetes Flow

The complete process can be summarized as:

```text
1. Start Minikube
       |
       v
2. Kubernetes Cluster starts
       |
       v
3. Create Deployment
       |
       v
4. Deployment creates ReplicaSet
       |
       v
5. ReplicaSet creates Pod
       |
       v
6. Pod runs Nginx container
       |
       v
7. Create NodePort Service
       |
       v
8. Service provides stable access
       |
       v
9. Access Nginx from browser
       |
       v
10. Scale Deployment to 3 replicas
       |
       v
11. ReplicaSet maintains 3 Pods
       |
       v
12. Delete one Pod
       |
       v
13. ReplicaSet creates replacement Pod
       |
       v
14. Application returns to 3 replicas
```

---

# 25. Commands Used

## Check Minikube

```bash
minikube status
```

## Check Kubernetes Nodes

```bash
kubectl get nodes
```

## Check all Pods

```bash
kubectl get pods -A
```

## Create Deployment

```bash
kubectl create deployment nginx-deployment --image=nginx
```

## Check Deployment

```bash
kubectl get deployments
```

## Check Pods

```bash
kubectl get pods
```

## Check ReplicaSets

```bash
kubectl get rs
```

## Create Service

```bash
kubectl expose deployment nginx-deployment --type=NodePort --port=80
```

## Check Services

```bash
kubectl get services
```

## Access Nginx

```bash
minikube service nginx-deployment
```

## Scale to 3 replicas

```bash
kubectl scale deployment nginx-deployment --replicas=3
```

## Check Service endpoints

```bash
kubectl get endpoints nginx-deployment
```

## Check modern EndpointSlices

```bash
kubectl get endpointslices -l kubernetes.io/service-name=nginx-deployment
```

## Describe Service

```bash
kubectl describe service nginx-deployment
```

## Delete a Pod to test self-healing

```bash
kubectl delete pod <pod-name>
```

---

# 26. Verification

The following checks were performed successfully.

### Kubernetes Cluster

```text
minikube   Ready
```

### Deployment

```text
nginx-deployment   3/3   3   3
```

### Pods

```text
3 Nginx Pods
3/3 Running
```

### ReplicaSet

```text
DESIRED   CURRENT   READY
3         3         3
```

### Service

```text
Type: NodePort
Port: 80
NodePort: 30279
```

### Browser

```text
Welcome to nginx!
```

### Service Endpoints

Three Nginx Pod endpoints were available behind the Service.

### Self-Healing

After deleting one Pod, Kubernetes automatically created a replacement Pod and restored the desired replica count to three.

---

# 27. What I Learned

Through this homework, I learned how Kubernetes manages a containerized application from deployment to external access.

The major concepts demonstrated were:

1. **Minikube** — provides a local Kubernetes cluster for development and learning.
2. **Node** — provides the environment where Kubernetes workloads run.
3. **Pod** — provides the smallest deployable unit containing the Nginx container.
4. **Deployment** — manages the desired state of the Nginx application.
5. **ReplicaSet** — maintains the required number of Nginx Pods.
6. **Service** — provides stable networking and routes traffic to the Pods.
7. **NodePort** — exposes the application outside the Kubernetes cluster.
8. **Scaling** — increases the number of application replicas.
9. **Self-healing** — Kubernetes automatically replaces deleted Pods.

---

# 28. Key Kubernetes Concept

The most important concept demonstrated in this homework is:

```text
                 DESIRED STATE
                      |
                      v
                 Deployment
                      |
                      v
                  ReplicaSet
                      |
                      v
               Actual Pod State
                      |
                      v
              Kubernetes continuously
              reconciles the difference
```

For example:

```text
Desired: 3 Pods
Actual:  3 Pods
       ↓
     Healthy
```

If a Pod is deleted:

```text
Desired: 3 Pods
Actual: 2 Pods
       ↓
ReplicaSet detects difference
       ↓
Creates replacement Pod
       ↓
Desired: 3 Pods
Actual: 3 Pods
       ↓
     Healthy
```

This **desired-state + reconciliation + self-healing model** is one of the fundamental principles of Kubernetes.

---

# 29. Final Result

The Nginx web application was successfully deployed on a local Kubernetes cluster using Minikube.

The application was:

* Deployed using a Kubernetes Deployment.
* Managed by a ReplicaSet.
* Running with three replicas.
* Exposed through a NodePort Service.
* Accessible from the host machine.
* Verified through three Service endpoints.
* Tested for Kubernetes self-healing by deleting a Pod and observing automatic replacement.

Therefore, **Homework 1 — Deploy a Simple Web Application** was successfully completed.
---

# Homework 2 — Namespace Isolation in Kubernetes
## Homework 2 ScreenRecord

https://github.com/user-attachments/assets/af704949-5149-4954-ac1d-1d92e01f3ffb

## 1. Objective

The objective of this homework is to understand and demonstrate **Namespace Isolation** in Kubernetes.

In this practical, we create two separate namespaces:

- `dev` — Development environment 
- `prod` — Production environment 

We then deploy one Pod in each namespace and assign a different environment variable to each Pod:
```
dev namespace  → ENV=dev
prod namespace → ENV=prod
```

Finally, we verify that the Pods belong to their respective namespaces and can be independently accessed using Kubernetes namespace commands.

---

# 2. What is a Kubernetes Namespace?

A **Namespace** is a logical partition inside a Kubernetes cluster.

It allows us to organize Kubernetes resources into separate environments or teams while using the same Kubernetes cluster.

For example, a real company could have:
```
Kubernetes Cluster
│
├── development
│   ├── Backend
│   ├── Frontend
│   └── Database
│
├── testing
│   ├── Backend
│   └── Database
│
└── production
    ├── Backend
    ├── Frontend
    └── Database
```

In our homework, we use only two namespaces:
```
Kubernetes Cluster
│
├── dev
│   └── dev-pod
│
└── prod
    └── prod-pod
```

---

# 3. Why Do We Need Namespaces?

Imagine a company has a development environment and a production environment.

Developers may frequently create, update, and delete resources in development.

Production resources, however, need to be managed carefully.

Without namespaces, a cluster could become difficult to organize:
```
Kubernetes Cluster
│
├── backend
├── frontend
├── database
├── backend
├── frontend
├── database
└── ...
```

It becomes difficult to understand which resource belongs to which environment.

With namespaces:
```
Kubernetes Cluster
│
├── dev
│   ├── backend
│   ├── frontend
│   └── database
│
└── prod
    ├── backend
    ├── frontend
    └── database
```

This provides a much cleaner organizational structure.

---

# 4. Namespace Isolation Architecture

Our homework uses the following architecture:
```
                    Kubernetes Cluster
                           │
             ┌─────────────┴─────────────┐
             │                           │
             ▼                           ▼
      ┌──────────────┐           ┌──────────────┐
      │     dev      │           │     prod     │
      │   Namespace  │           │   Namespace  │
      └──────┬───────┘           └──────┬───────┘
             │                           │
             ▼                           ▼
       ┌───────────┐               ┌───────────┐
       │  dev-pod  │               │ prod-pod  │
       └─────┬─────┘               └─────┬─────┘
             │                           │
             ▼                           ▼
          ENV=dev                     ENV=prod
```

Both Pods run inside the same Kubernetes cluster, but they belong to different namespaces.

---

# 5. Minikube and Kubernetes Cluster

For this homework, we used **Minikube** to run Kubernetes locally.

The overall environment is:
```
Windows Machine
       │
       ▼
Docker Desktop
       │
       ▼
Minikube
       │
       ▼
Kubernetes Cluster
       │
       ├── dev Namespace
       │
       └── prod Namespace
```

Before starting the homework, we verified that Minikube was running:
```
minikube status
```

The cluster showed:
```
host: Running
kubelet: Running
apiserver: Running
kubeconfig: Configured
```

We also verified the Kubernetes Node:
```
kubectl get nodes
```

The Minikube Node was in:
```
Ready
```

state.

---

# 6. Checking Existing Namespaces

We first checked the namespaces available in the Kubernetes cluster:
```
kubectl get namespaces
```

A new Minikube cluster already contains several system namespaces.

Typical namespaces include:
```
default
kube-system
kube-public
kube-node-lease
```

These are created and used by Kubernetes itself.

We then created our own application namespaces:
```
dev
prod
```

---

# 7. Creating the Development Namespace

We created the development namespace using:
```
kubectl create namespace dev
```

Kubernetes created:
```
namespace/dev created
```

We verified it using:
```
kubectl get namespaces
```

The namespace appeared as:
```
dev    Active
```

---

# 8. Creating the Production Namespace

We then created the production namespace:
```
kubectl create namespace prod
```

After creation, we verified:
```
kubectl get namespaces
```

The cluster now contained:
```
default
dev
prod
kube-node-lease
kube-public
kube-system
```

Our application environments were now logically separated:
```
Kubernetes Cluster
│
├── dev
│
└── prod
```

---

# 9. Creating the Development Pod

For this homework, we don't need a web application.

We only need a lightweight container to demonstrate namespaces and environment variables.

Therefore, we used the **BusyBox** image.

We created the development Pod using:
```
kubectl run dev-pod --image=busybox --restart=Never --env="ENV=dev" --namespace=dev -- sleep 3600
```

Let's understand the command.

### `kubectl run`

Creates a Pod.

### `dev-pod`

The name of our Pod.

### `--image=busybox`

Uses the BusyBox container image.

### `--restart=Never`

Creates a standalone Pod rather than a Deployment.

### `--env="ENV=dev"`

Creates the environment variable:
```
ENV=dev
```

### `--namespace=dev`

Places the Pod inside the `dev` namespace.

### `-- sleep 3600`

Keeps the BusyBox container running for enough time so that we can inspect it.

---

# 10. Development Pod Architecture

The resulting structure is:
```
dev Namespace
     │
     ▼
  dev-pod
     │
     ▼
BusyBox Container
     │
     └── ENV=dev
```

We verified the Pod using:
```
kubectl get pods -n dev
```

The Pod reached:
```
1/1 Running
```

---

# 11. Verifying the Development Environment Variable

To verify that the environment variable was actually present inside the container, we used:
```
kubectl exec -n dev dev-pod -- printenv ENV
```

The output was:
```
dev
```

This proves that:
```
dev-pod
   ↓
ENV=dev
```

was successfully configured.

---

# 12. Creating the Production Pod

We then created a second Pod in the `prod` namespace.

Command:
```
kubectl run prod-pod --image=busybox --restart=Never --env="ENV=prod" --namespace=prod -- sleep 3600
```

The important differences are:
```
Pod name:
prod-pod

Namespace:
prod

Environment variable:
ENV=prod
```

---

# 13. Production Pod Architecture

The resulting structure is:
```
prod Namespace
      │
      ▼
   prod-pod
      │
      ▼
BusyBox Container
      │
      └── ENV=prod
```

We verified the Pod using:
```
kubectl get pods -n prod
```

The Pod reached:
```
1/1 Running
```

---

# 14. Verifying the Production Environment Variable

We used:
```
kubectl exec -n prod prod-pod -- printenv ENV
```

The output was:
```
prod
```

This proves that:
```
prod-pod
   ↓
ENV=prod
```

was successfully configured.

---

# 15. Development vs Production

Our final configuration is:

| PropertyDevelopmentProduction |           |            |
| ----------------------------- | --------- | ---------- |
| Namespace                     | `dev`     | `prod`     |
| Pod                           | `dev-pod` | `prod-pod` |
| Image                         | BusyBox   | BusyBox    |
| Environment Variable          | `ENV=dev` | `ENV=prod` |
| Status                        | Running   | Running    |

The important distinction is:
```
dev namespace
    ↓
dev-pod
    ↓
ENV=dev
```

while:
```
prod namespace
    ↓
prod-pod
    ↓
ENV=prod
```

---

# 16. Verifying Pods Separately

Kubernetes commands can be executed against a specific namespace using:
```
-n <namespace>
```

For development:
```
kubectl get pods -n dev
```

For production:
```
kubectl get pods -n prod
```

This allows us to inspect each environment independently.

---

# 17. What Happens Without `-n`?

When we run:
```
kubectl get pods
```

Kubernetes checks the **current/default namespace**.

It does not automatically show Pods from every namespace.

Therefore:
```
kubectl get pods
```

and:
```
kubectl get pods -n dev
```

are different commands.

The second explicitly tells Kubernetes:

> Show me Pods belonging to the `dev` namespace.

Similarly:
```
kubectl get pods -n prod
```

means:

> Show me Pods belonging to the `prod` namespace.

---

# 18. Viewing Pods Across All Namespaces

To view Pods from every namespace, we can use:
```
kubectl get pods -A
```

`-A` means:
```
--all-namespaces
```

The output allows us to clearly see:
```
NAMESPACE     NAME
default       ...
dev           dev-pod
prod          prod-pod
kube-system   ...
```

This is an important verification because it shows the namespace associated with each Pod.

---

# 19. Namespace Isolation Demonstration

The final structure is:
```
                     Kubernetes Cluster
                            │
          ┌─────────────────┴─────────────────┐
          │                                   │
          ▼                                   ▼
   ┌───────────────┐                   ┌───────────────┐
   │ dev Namespace │                   │ prod Namespace│
   └───────┬───────┘                   └───────┬───────┘
           │                                   │
           ▼                                   ▼
      ┌─────────┐                         ┌─────────┐
      │ dev-pod │                         │prod-pod │
      └────┬────┘                         └────┬────┘
           │                                   │
           ▼                                   ▼
       ENV=dev                             ENV=prod
```

This demonstrates the basic concept of **logical namespace isolation**.

---

# 20. Important Clarification — What Does "Isolation" Mean?

Namespace isolation should be understood correctly.

Namespaces provide **logical and administrative separation** of Kubernetes resources.

They help with:

-  Resource organization 
-  Resource naming 
-  Access-control scope 
-  Resource management 
-  Environment separation 

However, namespaces by themselves do **not automatically prevent network communication** between Pods in different namespaces.

For example:
```
dev Pod  ────────────> prod Pod
```

may still be possible depending on the Kubernetes networking configuration.

If we want to control network traffic between namespaces, we can use:
```
NetworkPolicy
```

If we want to control who can perform Kubernetes operations, we can use:
```
RBAC
```

Therefore:
```
Namespace
    ↓
Logical organization / isolation

RBAC
    ↓
Permission isolation

NetworkPolicy
    ↓
Network isolation
```

This distinction is important in real-world Kubernetes deployments.

---

# 21. Why Environment Variables Are Used

The homework requires us to distinguish the environments.

We use:
```
ENV=dev
```

for development and:
```
ENV=prod
```

for production.

In real applications, environment variables are commonly used to configure application behavior.

For example:
```
Development:

ENV=dev
DATABASE_HOST=dev-db
LOG_LEVEL=DEBUG
```

Production:
```
ENV=prod
DATABASE_HOST=prod-db
LOG_LEVEL=INFO
```

The application can read these values and behave differently depending on the environment.

In our homework, we use only:
```
ENV
```

to keep the demonstration simple.

---

# 22. Why Did We Use BusyBox?

We used BusyBox because this homework is focused on **Namespaces**, not application deployment.

We don't need:

-  Nginx 
-  Service 
-  Deployment 
-  ReplicaSet 
-  Multiple replicas 

We only need a simple running container where we can verify the environment variable.

Therefore:
```
BusyBox
   ↓
Lightweight container
   ↓
Easy to create
   ↓
Easy to inspect
```

This keeps the Kubernetes demonstration focused on the actual learning objective.

---

# 23. Namespace vs Pod

A common beginner confusion is thinking that a namespace is a container for a single Pod.

It isn't.

A namespace can contain many different Kubernetes resources:
```
dev Namespace
│
├── Deployment
├── ReplicaSet
├── Pods
├── Services
├── ConfigMaps
├── Secrets
└── PersistentVolumeClaims
```

Similarly:
```
prod Namespace
│
├── Deployment
├── ReplicaSet
├── Pods
├── Services
├── ConfigMaps
├── Secrets
└── PersistentVolumeClaims
```

Therefore, namespaces are useful for organizing entire application environments.

---

# 24. Namespace Resource Organization

A real Spring Boot application could eventually look like:
```
Kubernetes Cluster
│
├── dev
│   ├── Spring Boot Deployment
│   ├── Spring Boot Pods
│   ├── MySQL
│   └── Services
│
└── prod
    ├── Spring Boot Deployment
    ├── Spring Boot Pods
    ├── MySQL
    └── Services
```

This is one reason namespaces are important when moving from local Kubernetes practice to real production environments.

---

# 25. Commands Used

## Check Minikube
```
minikube status
```

## Check Kubernetes Nodes
```
kubectl get nodes
```

## List Namespaces
```
kubectl get namespaces
```

## Create Development Namespace
```
kubectl create namespace dev
```

## Create Production Namespace
```
kubectl create namespace prod
```

## Create Development Pod
```
kubectl run dev-pod --image=busybox --restart=Never --env="ENV=dev" --namespace=dev -- sleep 3600
```

## Create Production Pod
```
kubectl run prod-pod --image=busybox --restart=Never --env="ENV=prod" --namespace=prod -- sleep 3600
```

## View Development Pod
```
kubectl get pods -n dev
```

## View Production Pod
```
kubectl get pods -n prod
```

## View Pods Across All Namespaces
```
kubectl get pods -A
```

## Verify Development Environment Variable
```
kubectl exec -n dev dev-pod -- printenv ENV
```

Output:
```
dev
```

## Verify Production Environment Variable
```
kubectl exec -n prod prod-pod -- printenv ENV
```

Output:
```
prod
```

## Describe Development Pod
```
kubectl describe pod dev-pod -n dev
```

## Describe Production Pod
```
kubectl describe pod prod-pod -n prod
```

---

# Homework 3: Persistent Volumes with StatefulSet
## Homework3 ScreenRecord

https://github.com/user-attachments/assets/e3ecfc5a-f63e-4689-9107-5dbb0ef8d7f8

## Objective

Set up a **PostgreSQL database using a Kubernetes StatefulSet** with persistent storage. The Spring Boot Banking application is connected to this PostgreSQL database running inside Kubernetes.
The main objective is to verify that the database data **does not get lost when the PostgreSQL Pod is deleted and recreated**.
For this implementation, I used **PostgreSQL instead of MySQL** because my Banking Spring Boot application was already developed using PostgreSQL.

---

# 1. Banking Spring Boot Application

I used my existing **Banking Spring Boot backend application** for this homework.
The application uses:

-  Spring Boot 3.5.11 
-  Java 21 
-  Spring Data JPA 
-  PostgreSQL 
-  Hibernate 
-  Maven 

The database configuration uses environment variables instead of hardcoding the database connection details.
```
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
```

This allows the same application to work with different database environments.

---

# 2. Build the Spring Boot Application

Before deploying the application into Kubernetes, I created the executable JAR file using Maven.
```
mvn clean package
```

The build completed successfully.

The generated JAR file was:
```
target/Banking-0.0.1-SNAPSHOT.jar
```

This JAR was then used to create the Docker image for the Banking application.

---

# 3. Create Docker Image

I created a Dockerfile for the Spring Boot application.
```
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/Banking-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 1212

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Then I built the Docker image:
```
docker build -t banking-app:1.0 .
```

The image was successfully created.

Since I was using Minikube, I loaded the image into the Minikube environment:
```
minikube image load banking-app:1.0
```

I verified that Minikube could see the image:
```
minikube image ls
```

The following image was available:
```
docker.io/library/banking-app:1.0
```

---

# 4. Create PostgreSQL StatefulSet

Next, I created a Kubernetes StatefulSet for PostgreSQL.

The StatefulSet manages the PostgreSQL Pod:
```
StatefulSet
    ↓
postgres-0
```

The important reason for using a **StatefulSet** is that databases require stable Pod identity and persistent storage.

After applying the StatefulSet configuration:
```
kubectl apply -f postgres-statefulset.yaml
```

Kubernetes created:
```
statefulset.apps/postgres created
```

I then checked the resources:
```
kubectl get statefulsets,pods,pvc,pv
```

The PostgreSQL Pod was created as:
```
postgres-0
```

Initially, it took some time to start:
```
ContainerCreating
```

After the database started successfully:
```
postgres-0    1/1    Running
```

---

# 5. PersistentVolumeClaim and PersistentVolume

The StatefulSet automatically created a PersistentVolumeClaim for the PostgreSQL Pod.

I verified it using:
```
kubectl get pvc
```

The PVC was:
```
postgres-storage-postgres-0
```

and its status was:
```
Bound
```

with:
```
Capacity: 1Gi
Access Mode: RWO
StorageClass: standard
```

Kubernetes also created the corresponding PersistentVolume.

The storage relationship is:
```
PostgreSQL StatefulSet
        |
        ↓
    postgres-0
        |
        ↓
      PVC
        |
        ↓
       PV
        |
        ↓
 PostgreSQL Data
```

The important point is that the PostgreSQL data is stored in persistent storage rather than only inside the Pod's temporary filesystem.

---

# 6. Verify PostgreSQL Database

After the PostgreSQL Pod became ready, I connected directly to PostgreSQL using:
```
kubectl exec -it postgres-0 -- psql -U postgres -d bankingdatabase
```

Inside PostgreSQL, I checked the database version:
```
SELECT version();
```

PostgreSQL was running successfully.

I also checked the available databases:
```
\l
```

The `bankingdatabase` database was available.

At this stage, the database was successfully running inside Kubernetes.

---

# 7. Create PostgreSQL Service

Next, I created a Kubernetes Service for PostgreSQL:
```
kubectl apply -f postgres-service.yaml
```

The service was created as:
```
postgres
```

I verified it using:
```
kubectl get service
```

The PostgreSQL Service uses port:
```
5432
```

The Service provides a stable DNS name for the PostgreSQL database.

Therefore, instead of the Banking application connecting to a changing Pod IP, it connects to:
```
postgres:5432
```

The communication is:
```
Banking Application
       |
       ↓
postgres Service
       |
       ↓
postgres-0
       |
       ↓
PostgreSQL
```

---

# 8. Verify Persistent Storage Before Testing

To prove that the persistent volume is actually working, I created a small test table inside PostgreSQL:
```
CREATE TABLE persistence_test (
    id SERIAL PRIMARY KEY,
    message VARCHAR(100)
);
```

Then I inserted test data:
```
INSERT INTO persistence_test (message)
VALUES ('Data survives pod restart');
```

I verified the data:
```
SELECT * FROM persistence_test;
```

The result was:
```
 id |          message
----+---------------------------
  1 | Data survives pod restart
```

This gave me an initial piece of data that I could check after deleting the Pod.

---

# 9. Deploy Banking Application

After PostgreSQL was running successfully, I deployed my Spring Boot Banking application.
```
kubectl apply -f banking-deployment.yaml
```

The Deployment was created:
```
deployment.apps/banking created
```

I also created the Banking Service:
```
kubectl apply -f banking-service.yaml
```

Then I verified the Pods:
```
kubectl get pods
```

The result showed both applications running:
```
banking-xxxxxxxxxx    1/1    Running
postgres-0             1/1    Running
```

So now Kubernetes was running both:
```
Spring Boot Banking
        +
PostgreSQL
```

---

# 10. Verify Banking Application Connection

I checked the Banking application logs:
```
kubectl logs deployment/banking
```

The application successfully started on:
```
Tomcat started on port 1212
```

More importantly, Hibernate successfully connected to PostgreSQL.

The logs showed:
```
HikariPool-1 - Added connection
```

and:
```
Database version: 16.15
```

This confirmed that the Spring Boot application was successfully communicating with the PostgreSQL database running inside Kubernetes.

---

# 11. Test Banking Application Using Postman

I then used Postman to send requests to the Banking application.

The Banking application created the required `account` table automatically through Hibernate because:
```
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

I inserted banking account data through the API.

The records were successfully stored in PostgreSQL.

I verified the records directly from the PostgreSQL database:
```
SELECT * FROM account;
```

The database contained the account records inserted through the Banking application.

This demonstrated the complete application flow:
```
Postman
   |
   ↓
Banking Service
   |
   ↓
Banking Pod
   |
   ↓
PostgreSQL Service
   |
   ↓
postgres-0
   |
   ↓
PersistentVolume
```

---

# 12. Test Pod Deletion and Data Persistence

This was the most important part of the homework.

First, I checked the account data:
```
SELECT * FROM account;
```

The records were present.

Then I exited PostgreSQL:
```
\q
```

I deleted the PostgreSQL Pod:
```
kubectl delete pod postgres-0
```

Kubernetes reported:
```
pod "postgres-0" deleted
```

At this point, the PostgreSQL Pod was intentionally deleted.

---

# 13. StatefulSet Automatically Recreates the Pod

I checked the Pods:
```
kubectl get pods
```

A new Pod with the same StatefulSet identity appeared:
```
postgres-0    1/1    Running
```

This demonstrates an important StatefulSet feature:

> Even though the PostgreSQL Pod was deleted, the StatefulSet automatically recreated `postgres-0`.

The Pod was recreated, but the persistent storage remained available.

---

# 14. Verify Data After Pod Recreation

I connected to PostgreSQL again:
```
kubectl exec -it postgres-0 -- psql -U postgres -d bankingdatabase
```

Then I executed:
```
SELECT * FROM account;
```

The same account records were still present.

I also verified the persistence test:
```
SELECT * FROM persistence_test;
```

The previous record was still available:
```
id | message
---+---------------------------
1  | Data survives pod restart
```

Therefore, deleting and recreating the PostgreSQL Pod **did not delete the database data**.

---

# 15. Final Kubernetes Architecture

The final setup looks like this:
```
                       Kubernetes Cluster
                              |
                           Minikube
                              |
                       ┌──────┴──────┐
                       |             |
                       ↓             ↓
                 Banking Pod      postgres-0
                 Spring Boot      PostgreSQL
                    :1212             :5432
                       |                |
                       ↓                ↓
                Banking Service   postgres Service
                                        |
                                        ↓
                                       PVC
                                        |
                                        ↓
                                       PV
                                        |
                                        ↓
                                Persistent Data
```

The complete request flow is:
```
Postman
   |
   ↓
Banking Service
   |
   ↓
Banking Pod
   |
   ↓
PostgreSQL Service
   |
   ↓
postgres-0
   |
   ↓
PVC
   |
   ↓
PV
```

---

# Result

The Homework 3 requirement was successfully completed.

### What I implemented

-  Created a PostgreSQL **StatefulSet** 
-  Created a PostgreSQL **Pod** 
-  Created a **PersistentVolumeClaim** 
-  Kubernetes provisioned a **PersistentVolume** 
-  Created a PostgreSQL **Service** 
-  Dockerized my existing Spring Boot Banking application 
-  Deployed the Banking application to Kubernetes 
-  Connected Spring Boot to PostgreSQL through the Kubernetes Service 
-  Inserted data through the Banking API using Postman 
-  Verified the data directly inside PostgreSQL 
-  Deleted the PostgreSQL Pod 
-  Allowed the StatefulSet to recreate `postgres-0` 
-  Verified that the previously stored data was still available 

### Final verification
```
Pod deleted
     ↓
StatefulSet recreates postgres-0
     ↓
PVC remains Bound
     ↓
PV remains available
     ↓
PostgreSQL starts again
     ↓
Previous banking data is still present
```

**Therefore, the PersistentVolume successfully preserved the PostgreSQL data across Pod deletion and recreation.**

---

## Key Kubernetes concepts learned

Through this homework, I learned how a real backend application can be deployed together with its database in Kubernetes:

| ComponentPurpose |                                             |
| ---------------- | ------------------------------------------- |
| **Deployment**   | Manages the Spring Boot Banking application |
| **StatefulSet**  | Manages the PostgreSQL database Pod         |
| **Pod**          | Runs the application containers             |
| **Service**      | Provides stable network access between Pods |
| **PVC**          | Requests persistent storage                 |
| **PV**           | Provides the actual persistent storage      |
| **PostgreSQL**   | Stores the Banking application's data       |
| **Minikube**     | Provides the local Kubernetes cluster       |
| **Postman**      | Tests the Banking REST APIs                 |

**This is the main learning outcome of the homework: instead of treating the database as something running separately on my laptop, I deployed the backend and database as Kubernetes workloads and used persistent storage to protect the database data from Pod restarts/recreation.**
---


# Homework 4: StatefulSet with Headless Service
## Homework 4 ScreenRecord

https://github.com/user-attachments/assets/5aa256c0-eb86-42be-a508-c44fe466c6ad

## Homework Statement

**StatefulSet with Headless Service**

-  Deploy a StatefulSet for a simple application, such as Redis or MySQL, that requires stable, unique network IDs. 
-  Create a headless service to enable direct pod-to-pod communication. 
-  Verify that each pod has a stable hostname and consistent storage. 

---

# Objective

In this homework, I implemented a **PostgreSQL StatefulSet with 3 replicas** and a **Headless Service**.
I used PostgreSQL instead of Redis/MySQL because PostgreSQL was already used in my previous homework.
I also deployed my **Spring Boot Banking application** in the same Minikube cluster.
The main things I demonstrated were:

-  StatefulSet with multiple replicas 
-  Stable and unique pod names 
-  Stable pod hostnames 
-  Headless Service 
-  Individual PVC for each StatefulSet pod 
-  Pod endpoint discovery 
-  Kubernetes networking between application and database 

---

# Architecture
```
                         Minikube Cluster
                                │
                ┌───────────────┴────────────────┐
                │                                │
        Banking Application              PostgreSQL StatefulSet
                │                         3 replicas
                │                                │
                │                  ┌─────────────┼─────────────┐
                │                  │             │             │
                │            postgres-hw4-0  postgres-hw4-1  postgres-hw4-2
                │                  │             │             │
                │                 PVC           PVC           PVC
                │                  │             │             │
                │                 PV            PV            PV
                │
        banking-hw4 Service
             NodePort
                │
             Postman
```

The important networking component for this homework is:
```
              postgres-hw4-headless
                       │
             ┌─────────┼─────────┐
             │         │         │
             ▼         ▼         ▼
        postgres-0  postgres-1  postgres-2
```

---

# 1. Create Headless Service

First, I created the Headless Service using the Kubernetes YAML file.

### Command
```
kubectl apply -f postgres-hw4-headless-service.yaml
```

### Output
```
service/postgres-hw4-headless created
```

Then I checked the services.

### Command
```
kubectl get service
```

### Important Output
```
postgres-hw4-headless   ClusterIP   None   5432/TCP
```

The important point here is:
```
ClusterIP: None
```

This confirms that `postgres-hw4-headless` is a **Headless Service**.

A Headless Service does not provide a normal virtual ClusterIP. Instead, it allows Kubernetes DNS to discover the individual pods behind the service.

---

# 2. Create PostgreSQL StatefulSet

Next, I created the PostgreSQL StatefulSet.

### Command
```
kubectl apply -f postgres-hw4-statefulset.yaml
```

### Output
```
statefulset.apps/postgres-hw4 created
```

The StatefulSet was configured with **3 replicas**.

I then checked the StatefulSets.

### Command
```
kubectl get statefulsets
```

### Result
```
postgres-hw4   1/3
```

After the pods finished starting, all 3 replicas became ready.

---

# 3. Verify PostgreSQL Pods

I checked all the pods running inside the cluster.

### Command
```
kubectl get pods
```

### Result
```
postgres-hw4-0
postgres-hw4-1
postgres-hw4-2
```

All three PostgreSQL pods were running.

The important difference compared with a normal Deployment is that StatefulSet pods have **predictable names**.

The naming pattern is:
```
postgres-hw4-0
postgres-hw4-1
postgres-hw4-2
```

The ordinal numbers identify each StatefulSet replica.

---

# 4. Verify Individual Persistent Storage

Next, I checked the PersistentVolumeClaims created for the StatefulSet.

### Command
```
kubectl get pvc
```

The StatefulSet created three separate PVCs:
```
postgres-storage-postgres-hw4-0
postgres-storage-postgres-hw4-1
postgres-storage-postgres-hw4-2
```

Each PVC showed:
```
STATUS: Bound
CAPACITY: 1Gi
ACCESS MODE: RWO
```

This demonstrates that each StatefulSet pod gets its **own persistent storage**.

The relationship is:
```
postgres-hw4-0
      ↓
postgres-storage-postgres-hw4-0

postgres-hw4-1
      ↓
postgres-storage-postgres-hw4-1

postgres-hw4-2
      ↓
postgres-storage-postgres-hw4-2
```

Therefore, each PostgreSQL replica has its own storage.

---

# 5. Verify Stable Hostname of postgres-hw4-0

Next, I verified the hostname of the first PostgreSQL pod.

### Command
```
kubectl exec postgres-hw4-0 -- hostname
```

### Output
```
postgres-hw4-0
```

This confirms that the hostname is the same as the StatefulSet pod identity.

---

# 6. Verify Stable Hostname of postgres-hw4-1

### Command
```
kubectl exec postgres-hw4-1 -- hostname
```

### Output
```
postgres-hw4-1
```

---

# 7. Verify Stable Hostname of postgres-hw4-2

### Command
```
kubectl exec postgres-hw4-2 -- hostname
```

### Output
```
postgres-hw4-2
```

Therefore, the three pods have stable and unique hostnames:
```
postgres-hw4-0
postgres-hw4-1
postgres-hw4-2
```

This satisfies the **stable hostname** requirement of the homework.

---

# 8. Verify Headless Service Endpoints

Next, I checked the EndpointSlices.

### Command
```
kubectl get endpointslice
```

The Headless Service showed three PostgreSQL pod IP addresses:
```
postgres-hw4-headless
10.244.0.28
10.244.0.29
10.244.0.30
```

This confirms that the Headless Service is associated with the three PostgreSQL pods.

Instead of pointing to one virtual ClusterIP, the Headless Service exposes the individual pod endpoints.

---

# 9. Deploy Banking Application

After verifying the PostgreSQL StatefulSet and Headless Service, I deployed my Spring Boot Banking application.

### Command
```
kubectl apply -f banking-hw4-deployment.yaml
```

### Output

```
deployment.apps/banking-hw4 created
```

Then I created the Banking Service.

### Command
```
kubectl apply -f banking-hw4-service.yaml
```

### Output
```
service/banking-hw4 created
```

---

# 10. Verify Banking Application Pod

I checked the pods again.

### Command
```
kubectl get pods
```

The Banking application pod was running:
```
banking-hw4-795977b987-l6mkx   1/1   Running
```

Along with the PostgreSQL StatefulSet pods:
```
postgres-hw4-0
postgres-hw4-1
postgres-hw4-2
```

So both the Banking application and PostgreSQL StatefulSet were running inside the same Minikube cluster.

---

# 11. Verify Banking Application Logs

I checked the Banking application logs.

### Command
```
kubectl logs deployment/banking-hw4
```

Initially, the application showed:
```
Connection to localhost:5432 refused
```

The reason was that the Banking application was trying to connect to:
```
localhost:5432
```

Inside the Banking pod, `localhost` refers to the **Banking pod itself**, not the PostgreSQL pod.

Therefore, the application cannot use:
```
jdbc:postgresql://localhost:5432/...
```

to connect to PostgreSQL running in another Kubernetes pod.
The application must use the appropriate Kubernetes Service/DNS name to communicate with PostgreSQL.

---

# 12. How Pod-to-Pod Communication Works

This is an important part of this homework.

The Headless Service was created as:
```
postgres-hw4-headless
```

with:
```
ClusterIP: None
```

It points to the PostgreSQL StatefulSet pods:
```
postgres-hw4-headless
        │
        ├── postgres-hw4-0
        ├── postgres-hw4-1
        └── postgres-hw4-2
```

The EndpointSlice confirmed the individual pod IP addresses:
```
10.244.0.28
10.244.0.29
10.244.0.30
```

The Headless Service therefore provides **DNS-based discovery of the individual PostgreSQL pods**.

The StatefulSet + Headless Service combination gives stable network identities for the pods.

The expected DNS naming pattern is:
```
postgres-hw4-0.postgres-hw4-headless
postgres-hw4-1.postgres-hw4-headless
postgres-hw4-2.postgres-hw4-headless
```

So another pod inside the Kubernetes cluster can discover a specific PostgreSQL pod using its stable DNS identity.

> **Note:** In this homework, I verified the Headless Service and its individual pod endpoints. I did not perform a separate manual DNS/network connection test between two PostgreSQL pods. Therefore, the practical evidence recorded here is **pod discovery through the Headless Service**, which enables direct pod-to-pod communication.

---

# 13. Final Verification

At the end, I verified the important StatefulSet components.

### Check StatefulSet
```
kubectl get statefulsets
```

### Check Pods
```
kubectl get pods
```

### Check Persistent Storage
```
kubectl get pvc
```

### Check Services
```
kubectl get service
```

### Check Pod Endpoints
```
kubectl get endpointslice
```

### Check Individual Hostnames
```
kubectl exec postgres-hw4-0 -- hostname
kubectl exec postgres-hw4-1 -- hostname
kubectl exec postgres-hw4-2 -- hostname
```

---

# 📊 Final Kubernetes Structure
```
Minikube Cluster
│
├── Banking Application
│   └── banking-hw4-xxxxx
│
├── Banking Service
│   └── banking-hw4
│
├── PostgreSQL StatefulSet
│   ├── postgres-hw4-0
│   │   └── PVC → postgres-storage-postgres-hw4-0
│   │
│   ├── postgres-hw4-1
│   │   └── PVC → postgres-storage-postgres-hw4-1
│   │
│   └── postgres-hw4-2
│       └── PVC → postgres-storage-postgres-hw4-2
│
└── Headless Service
    └── postgres-hw4-headless
        ├── postgres-hw4-0
        ├── postgres-hw4-1
        └── postgres-hw4-2
```

---

# 🎯 Conclusion

In this homework, I deployed a **PostgreSQL StatefulSet with 3 replicas** and created a **Headless Service**.

I verified that StatefulSet created predictable pod identities:
```
postgres-hw4-0
postgres-hw4-1
postgres-hw4-2
```

I also verified that each pod received its own persistent storage through separate PVCs.

The Headless Service had:
```
ClusterIP: None
```

and its EndpointSlice contained the IP addresses of all three PostgreSQL pods.
This demonstrates how **StatefulSet + Headless Service** provides stable pod identities, stable hostnames, persistent storage, and DNS-based discovery for direct communication between pods.
The Spring Boot Banking application was also deployed into the same Minikube cluster, demonstrating how an application can communicate with services running inside Kubernetes rather than using `localhost`.

