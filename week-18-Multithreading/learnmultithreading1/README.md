# Homework 1 - Implementing ThreadPoolExecutor with 20 Tasks

https://github.com/user-attachments/assets/efb8f729-9072-4e55-90e1-81993dd9c01f

## Problem Statement

Write a Java program using `ThreadPoolExecutor` with the following configuration:

- Core pool size: 2
- Maximum pool size: 5
- Keep-alive time: 30 seconds
- Bounded queue size: 10
- Submit 20 tasks, with each task taking 2 seconds to complete.
- Observe how threads are utilized and what happens to excess tasks.

## Implementation

### 1. Creating the Homework1 class

First, I created a separate `Homework1` class inside my Spring Boot application's main Java package. I created an `executeHomework1()` method to keep the executor implementation separate from the main application class.

### 2. Configuring ThreadPoolExecutor

Inside `executeHomework1()`, I created a `ThreadPoolExecutor` with the required configuration.

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
        2,
        5,
        30,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(10)
);
```

Here is what each configuration means:

- **Core pool size – 2:** The executor initially uses two core worker threads.
- **Maximum pool size – 5:** The executor can increase its worker count up to five when the queue becomes full.
- **Keep-alive time – 30 seconds:** Extra idle worker threads can remain available for up to 30 seconds before being removed.
- **ArrayBlockingQueue – 10:** The executor can hold up to ten waiting tasks in its bounded queue.

### 3. Submitting 20 tasks

Next, I used a `for` loop to submit 20 tasks to the executor.

```java
for (int i = 1; i <= 20; i++) {
    final int taskId = i;

    try {
        executor.execute(() -> {
            // Task implementation
        });
    } catch (RejectedExecutionException e) {
        System.out.println("Task " + taskId + " REJECTED");
    }
}
```

I used `executor.execute()` to submit each task. The executor decides whether to assign the task to a worker thread, place it in the queue, create another worker, or reject it.

I also used `final int taskId = i` so that each submitted lambda can access its own task ID.

### 4. Simulating task execution

Inside the lambda expression, I printed the task ID and the name of the thread executing it.

```java
System.out.println(
    "Task " + taskId + " started on "
    + Thread.currentThread().getName()
);
```

To simulate a task that takes two seconds, I used:

```java
Thread.sleep(2000);
```

After the delay, I printed a completion message.

```java
System.out.println("Task " + taskId + " completed");
```

This helps me observe which worker threads execute the tasks and when each task finishes.

### 5. Handling rejected tasks

I wrapped `executor.execute()` inside a `try-catch` block.

```java
catch (RejectedExecutionException e) {
    System.out.println("Task " + taskId + " REJECTED");
}
```

The executor uses `AbortPolicy` by default. When all five worker threads are occupied and the queue is full, additional submissions are rejected with `RejectedExecutionException`.

This is how I identify tasks that cannot be accepted by the executor.

### 6. Monitoring the executor

After submitting the tasks, I used two methods to observe the executor's current state.

```java
System.out.println(
    "Active threads: " + executor.getActiveCount()
);

System.out.println(
    "Queued tasks: " + executor.getQueue().size()
);
```

- `getActiveCount()` gives an approximate count of worker threads currently executing tasks.
- `getQueue().size()` gives the current number of tasks waiting in the queue.

These values are snapshots and can change while the worker threads are executing.

### 7. Shutting down the executor

Finally, I used `shutdown()` to stop accepting new tasks while allowing accepted tasks to finish.

```java
executor.shutdown();

if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
    executor.shutdownNow();
}
```

`awaitTermination()` waits for the executor to finish its accepted tasks for up to one minute. If the timeout is reached, `shutdownNow()` attempts to stop the remaining work.

## Understanding how the executor handles 20 tasks

For a rapid submission of all 20 tasks, the executor follows this process:

```text
              20 Tasks Submitted
                      |
                      v
             ThreadPoolExecutor
                      |
           +----------+----------+
           |                     |
           v                     v
     2 Core Threads         Queue Capacity
       Running                  10
           |
           v
    Queue becomes full
           |
           v
    Pool grows to 5 threads
           |
           v
    5 Tasks Running
    10 Tasks Queued
           |
           v
    Remaining tasks
           |
           v
       Rejected
```

The executor can initially accept 15 tasks under these conditions:

- 2 tasks running on core threads.
- 10 tasks waiting in the queue.
- 3 additional tasks running on newly created worker threads.

Therefore, if all 20 tasks are submitted before any capacity becomes available, the remaining five tasks are rejected by the default rejection policy.

## Result

Through this implementation, I demonstrated how `ThreadPoolExecutor` manages worker threads, queues waiting tasks, increases its pool size when required, and rejects additional tasks when its capacity is exhausted.

# Homework 2 - Fetching User Report Using @Async and CompletableFuture.allOf()

https://github.com/user-attachments/assets/d570f79d-fd31-4b1f-ad2f-3cff2a55f323

## Problem Statement

Write a Spring Boot application with three asynchronous methods:

- `fetchUserData()` – Simulate fetching user details with a 2-second delay.
- `fetchOrderData()` – Simulate fetching order details with a 3-second delay.
- `fetchPaymentData()` – Simulate fetching payment history with a 4-second delay.

Use these methods inside a `getUserReport()` method to fetch all data concurrently and return the combined result.

## Implementation

### 1. Enabling asynchronous execution

First, I enabled asynchronous execution in my Spring Boot application by adding the `@EnableAsync` annotation.

```java
@SpringBootApplication
@EnableAsync
public class Learnmultithreading1Application {

    public static void main(String[] args) {
        SpringApplication.run(
            Learnmultithreading1Application.class, args
        );
    }
}
```

`@EnableAsync` tells Spring to enable asynchronous method execution.

This allows Spring-managed methods annotated with `@Async` to execute using an asynchronous executor instead of making the calling thread wait for their execution to finish.

### 2. Creating Homework2DataFetchService

Next, I created a separate service class named `Homework2DataFetchService`.

Inside this class, I implemented three methods:

- `fetchUserData()`
- `fetchOrderData()`
- `fetchPaymentData()`

I annotated each method with `@Async` so that Spring can execute them asynchronously.

```java
@Service
public class Homework2DataFetchService {

    @Async
    public CompletableFuture<String> fetchUserData() {
        // Simulate fetching user data
    }

    @Async
    public CompletableFuture<String> fetchOrderData() {
        // Simulate fetching order data
    }

    @Async
    public CompletableFuture<String> fetchPaymentData() {
        // Simulate fetching payment data
    }
}
```

I used `CompletableFuture<String>` as the return type because each method produces a result that becomes available after its asynchronous execution.

### 3. Simulating three independent operations

Inside each method, I used `Thread.sleep()` to simulate the time required to fetch data.

The delays are different for each operation:

```java
// User data
Thread.sleep(2000);

// Order data
Thread.sleep(3000);

// Payment history
Thread.sleep(4000);
```

After the delay, each method returns its simulated result using `CompletableFuture.completedFuture()`.

For example:

```java
return CompletableFuture.completedFuture(
    "User details fetched successfully"
);
```

I used this approach for all three methods, returning user details, order details, and payment history.

The purpose of these delays is to simulate real-world operations where different services or database queries may take different amounts of time.

### 4. Creating Homework2UserReportService

After implementing the three asynchronous methods, I created another service class named `Homework2UserReportService`.

This service is responsible for calling all three methods and combining their results into a single user report.

I injected `Homework2DataFetchService` through the constructor so that I could access its asynchronous methods.

```java
@Service
public class Homework2UserReportService {

    private final Homework2DataFetchService dataFetchService;

    public Homework2UserReportService(
            Homework2DataFetchService dataFetchService) {
        this.dataFetchService = dataFetchService;
    }
}
```

### 5. Calling all three asynchronous methods

Inside `getUserReport()`, I called the three methods and stored their returned futures in separate variables.

```java
CompletableFuture<String> userFuture =
        dataFetchService.fetchUserData();

CompletableFuture<String> orderFuture =
        dataFetchService.fetchOrderData();

CompletableFuture<String> paymentFuture =
        dataFetchService.fetchPaymentData();
```

At this point, all three asynchronous operations are started through Spring's async execution mechanism.

Because they are independent operations, they can execute concurrently instead of waiting for one another.

### 6. Using CompletableFuture.allOf()

This is the most important part of this homework.

After starting all three operations, I used `CompletableFuture.allOf()` to coordinate their completion.

```java
return CompletableFuture.allOf(
        userFuture,
        orderFuture,
        paymentFuture
).thenApply(ignored -> {

    String user = userFuture.join();
    String orders = orderFuture.join();
    String payments = paymentFuture.join();

    return """
            ===== USER REPORT =====
            %s
            %s
            %s
            =======================
            """.formatted(user, orders, payments);
});
```

Here is why I used these methods:

**`CompletableFuture.allOf()`**

It waits asynchronously until all three supplied futures have completed. It does not block the calling thread while waiting.

**`thenApply()`**

It executes the next operation after all three futures complete successfully. I used it to combine the individual results.

**`join()`**

I used `join()` to retrieve the result from each future. Since `allOf()` has already completed successfully, these individual futures are already completed, so these calls do not need to wait for their original operations.

Finally, I combined all three results into a single user report and returned it.

### 7. Creating Homework2UserReportController

After completing the service implementation, I created a REST controller to expose the report functionality through an API.

```java
@RestController
public class Homework2UserReportController {

    private final Homework2UserReportService reportService;

    public Homework2UserReportController(
            Homework2UserReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/user-report")
    public CompletableFuture<String> getUserReport() {
        return reportService.getUserReport();
    }
}
```

I created a GET endpoint named `/user-report`.

When a client calls this endpoint, the controller invokes `getUserReport()` and returns its `CompletableFuture`.

Spring MVC can handle this asynchronous return type and complete the HTTP response when the report becomes available.

### 8. Understanding sequential and concurrent execution

One important objective of this homework is to understand the difference between sequential and concurrent execution.

If all three operations execute one after another:

```text
User data       = 2 seconds
Order data      = 3 seconds
Payment history = 4 seconds

Total = 2 + 3 + 4 = 9 seconds
```

However, with `@Async`, all three operations can execute concurrently.

```text
Time ------------------------------------------------>

User data:       [------ 2 seconds ------]

Order data:      [--------- 3 seconds ---------]

Payment history: [------------ 4 seconds ------------]

                                                     |
                                                     v
                                              Combined report
```

The total execution time is approximately four seconds because the longest operation takes four seconds.

The three operations overlap instead of waiting for one another.

This is why `CompletableFuture.allOf()` is important: it allows me to continue with report generation only after all three independent operations have completed.

## Execution Flow

```text
              Client / Postman
                     |
                     | GET /user-report
                     v
       Homework2UserReportController
                     |
                     v
       Homework2UserReportService
                     |
          +----------+----------+
          |          |          |
          v          v          v
      fetchUser  fetchOrder  fetchPayment
       Data        Data        Data
          |          |          |
        2 sec      3 sec       4 sec
          |          |          |
          +----------+----------+
                     |
                     v
        CompletableFuture.allOf()
                     |
                     v
                  thenApply()
                     |
                     v
             Combine all results
                     |
                     v
               User Report
                     |
                     v
              HTTP Response
```

## Testing the Application

After completing the implementation, I started my Spring Boot application and tested the API.

**HTTP Method:** GET

**Endpoint:**

```text
http://localhost:8081/user-report
```

I called the endpoint using Postman.

The application starts all three data-fetching operations asynchronously. After all three operations finish, the combined user report is returned in the HTTP response.

The expected execution time for the simulated operations is approximately four seconds, excluding application startup, request processing, and other overhead.

## Result

Through this implementation, I demonstrated how Spring Boot's `@Async` annotation and Java's `CompletableFuture` can be used together to execute multiple independent operations concurrently.

Instead of fetching user details, order details, and payment history sequentially, I executed them asynchronously and used `CompletableFuture.allOf()` to wait for all results before generating the final report.

**Concepts demonstrated:** `@EnableAsync`, `@Async`, `CompletableFuture`, `CompletableFuture.allOf()`, `thenApply()`, `join()`, constructor injection, and asynchronous REST API responses.

I also demonstrated how to monitor active threads and queued tasks and how to shut down the executor properly.

**Concepts demonstrated:** `ThreadPoolExecutor`, `ArrayBlockingQueue`, `execute()`, `RejectedExecutionException`, `getActiveCount()`, `getQueue()`, `shutdown()`, and `awaitTermination()`.

# Homework 3 - Implementing a Custom Rejection Policy in ThreadPoolExecutor

https://github.com/user-attachments/assets/f24485b2-b4db-43bb-90e7-acebe6fe214b

## Problem Statement

Implement a custom rejection policy in a `ThreadPoolExecutor` that logs a warning message and retries rejected tasks after one second.

## Implementation

### 1. Creating the Homework3 class

First, I created a separate Java class named `Homework3` inside my Spring Boot application's main package.

I created an `executeHomework3()` method to implement the executor logic and submit 20 tasks.

### 2. Configuring ThreadPoolExecutor

Inside the `executeHomework3()` method, I created a `ThreadPoolExecutor` with the following configuration:

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
        2,
        5,
        30,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(10),
        new RetryRejectedExecutionHandler()
);
```

Here is what each configuration means:

- **Core pool size – 2:** The executor initially uses two core worker threads.
- **Maximum pool size – 5:** The executor can create up to five worker threads.
- **Keep-alive time – 30 seconds:** Extra idle worker threads can remain alive for 30 seconds.
- **ArrayBlockingQueue – 10:** The queue can store up to ten waiting tasks.
- **RetryRejectedExecutionHandler:** This is my custom rejection handler, which handles tasks that cannot be accepted immediately.

The sixth constructor argument is important because it registers my custom rejection policy with the executor.

### 3. Submitting 20 tasks

Next, I used a `for` loop to submit 20 tasks.

```java
for (int i = 1; i <= 20; i++) {
    final int taskId = i;

    executor.execute(() -> {
        System.out.println(
            "Task " + taskId + " started on "
            + Thread.currentThread().getName()
        );

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        System.out.println("Task " + taskId + " completed");
    });
}
```

Each task prints its ID and the name of the worker thread executing it.

I used `Thread.sleep(2000)` to simulate a task that takes approximately two seconds to complete.

When the executor becomes full, it invokes the custom rejection handler instead of immediately rejecting the task.

### 4. Creating a custom RetryRejectedExecutionHandler

Next, I created a static inner class named `RetryRejectedExecutionHandler` that implements Java's `RejectedExecutionHandler` interface.

```java
static class RetryRejectedExecutionHandler
        implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(
            Runnable task,
            ThreadPoolExecutor executor) {

        // Retry logic
    }
}
```

The `RejectedExecutionHandler` interface provides the `rejectedExecution()` method.

This method is automatically invoked by the executor whenever it cannot accept a newly submitted task.

It provides two parameters:

- `Runnable task`: The task that was rejected.
- `ThreadPoolExecutor executor`: The executor that rejected the task.

I used these parameters to implement my retry mechanism.

### 5. Logging a warning message

Inside `rejectedExecution()`, I first printed a warning message.

```java
System.out.println(
    "Warning: Task rejected! Retrying after 1 second..."
);
```

This helps me identify when the executor becomes full and a task needs to be retried.

### 6. Retrying rejected tasks after one second

Next, I used a `while` loop to continue retrying as long as the executor is not shut down.

```java
while (!executor.isShutdown()) {
    try {
        Thread.sleep(1000);

        if (executor.isShutdown()) {
            throw new IllegalStateException(
                "Executor is shutting down"
            );
        }

        if (executor.getQueue().offer(
                task, 1, TimeUnit.SECONDS)) {

            System.out.println(
                "Task successfully added to queue after retry"
            );
            return;
        }

        System.out.println(
            "WARNING: Queue still full. Retrying again..."
        );

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Retry interrupted", e);
    }
}
```

Here is how this retry mechanism works:

1. First, the handler waits for one second using `Thread.sleep(1000)`.
2. It checks whether the executor has been shut down.
3. It uses `executor.getQueue().offer()` to attempt to add the rejected task back into the queue.
4. If the task is successfully added, it prints a success message and returns.
5. If the queue is still full, it prints another warning and repeats the process.

The `offer()` method attempts to add the task to the queue. In this implementation, I used its timed version, which can wait for up to one second for queue capacity.

I used direct queue insertion instead of calling `executor.execute(task)` again. This avoids recursively invoking the same rejection handler.

### 7. Handling executor shutdown

After the retry loop, I added a final exception:

```java
throw new IllegalStateException(
    "Executor shut down before task could be retried"
);
```

This prevents the handler from retrying indefinitely when the executor has already been shut down.

I also handled `InterruptedException` by restoring the thread's interrupt status using:

```java
Thread.currentThread().interrupt();
```

This is important because interruption should not be silently ignored.

### 8. Shutting down the executor

After submitting all 20 tasks, I used:

```java
System.out.println("All 20 tasks submitted");

executor.shutdown();

if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
    executor.shutdown();
}

System.out.println("All tasks finished");
```

`shutdown()` stops accepting new tasks but allows accepted tasks to finish.

`awaitTermination()` waits for the executor to complete its work.

## Understanding the execution flow

```text
             20 Tasks Submitted
                     |
                     v
             ThreadPoolExecutor
                     |
          +----------+----------+
          |                     |
          v                     v
     2 Core Threads       Queue Capacity 10
          |                     |
          +----------+----------+
                     |
                     v
            Queue becomes full
                     |
                     v
            Pool grows to 5
                     |
                     v
            Executor is full
                     |
                     v
          Custom Rejection Handler
                     |
                     v
           Print Warning Message
                     |
                     v
              Wait 1 Second
                     |
                     v
             Try Queue.offer()
                     |
              +------+------+
              |             |
          Successful      Still Full
              |             |
              v             v
        Task accepted   Print Warning
              |             |
              v             |
          Return        Retry Again
```

## Result

Through this implementation, I demonstrated how to customize the default rejection behaviour of `ThreadPoolExecutor`.

Instead of immediately throwing `RejectedExecutionException` when the executor becomes full, my custom handler logs a warning, waits for one second, and retries adding the rejected task to the queue.

After the worker threads complete their existing tasks and queue capacity becomes available, the rejected tasks can also be accepted and executed.

In my demonstration, all 20 tasks completed successfully after implementing the custom retry policy.

**Concepts demonstrated:** `ThreadPoolExecutor`, `RejectedExecutionHandler`, `rejectedExecution()`, `ArrayBlockingQueue`, `offer()`, `Thread.sleep()`, `isShutdown()`, `shutdown()`, and `awaitTermination()`.

---

# Homework 4 - Implementing Dynamic Task Scheduling Using Spring TaskScheduler

https://github.com/user-attachments/assets/fcd2599d-d725-4b6a-b4b9-d9889e3c943d

## Problem Statement

Implement a dynamic task scheduler using Spring's `TaskScheduler` that allows a user to call an API that adds a task to send an email after 10 seconds.

## Implementation

### 1. Understanding dynamic task scheduling

In this homework, I implemented a mechanism where the user can schedule an email task by calling a REST API.

Instead of executing the email operation immediately, the application calculates a future execution time and registers the task with Spring's `TaskScheduler`.

The email task is executed automatically after approximately ten seconds.

### 2. Creating TaskSchedulerConfig

First, I created a configuration class named `TaskSchedulerConfig`.

Inside this class, I configured a `ThreadPoolTaskScheduler` bean.

```java
@Configuration
public class TaskSchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler =
                new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("email-scheduler-");

        return scheduler;
    }
}
```

Here is what these annotations and methods mean:

- `@Configuration`: Marks the class as a Spring configuration class.
- `@Bean`: Registers the scheduler as a Spring-managed bean.
- `ThreadPoolTaskScheduler`: Spring implementation of the `TaskScheduler` abstraction.
- `setPoolSize(2)`: Configures two scheduler threads.
- `setThreadNamePrefix()`: Sets a readable prefix for scheduler thread names.

I used this configuration so that Spring manages the scheduler and its worker threads.

### 3. Creating EmailService

Next, I created a service class named `EmailService`.

This class contains the `sendEmail()` method, which simulates sending an email.

```java
@Service
public class EmailService {

    public void sendEmail(
            String to, String subject, String body) {

        System.out.println("EMAIL SENDING STARTED!");

        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + body);

        System.out.println(
            "Executed by: "
            + Thread.currentThread().getName()
        );

        System.out.println("EMAIL SENT SUCCESSFULLY!");
    }
}
```

I created this method with three parameters:

- `to`: Email recipient.
- `subject`: Email subject.
- `body`: Email message.

I also printed the current thread name to identify which scheduler thread executes the email task.

For this homework, email sending is simulated using console output. Actual email delivery would require an SMTP or email-provider integration.

### 4. Creating DynamicEmailSchedulerService

After creating the email service, I created another service class named `DynamicEmailSchedulerService`.

This service is responsible for scheduling the email task.

I injected two dependencies through constructor injection:

- `TaskScheduler`
- `EmailService`

```java
@Service
public class DynamicEmailSchedulerService {

    private final TaskScheduler taskScheduler;
    private final EmailService emailService;

    public DynamicEmailSchedulerService(
            TaskScheduler taskScheduler,
            EmailService emailService) {

        this.taskScheduler = taskScheduler;
        this.emailService = emailService;
    }
}
```

The `TaskScheduler` is used to register the task for future execution, while `EmailService` performs the email operation.

### 5. Scheduling the email after 10 seconds

Inside `DynamicEmailSchedulerService`, I created a method named `scheduleEmail()`.

```java
public ScheduledFuture<?> scheduleEmail(
        String to, String subject, String body) {

    Instant scheduledTime = Instant.now().plusSeconds(10);

    return taskScheduler.schedule(
            () -> emailService.sendEmail(to, subject, body),
            scheduledTime
    );
}
```

This is the main implementation of the homework.

Here is how it works:

**`Instant.now()`**

Returns the current date and time as an instant.

**`plusSeconds(10)`**

Adds ten seconds to the current time and calculates the target execution time.

**`taskScheduler.schedule()`**

Registers the task with Spring's scheduler.

It accepts two important arguments:

1. A `Runnable` containing the operation to execute.
2. The time at which the operation should execute.

When the scheduled time arrives, the scheduler executes:

```java
emailService.sendEmail(to, subject, body);
```

The method returns a `ScheduledFuture<?>`, which represents the scheduled task and can be used to inspect or cancel it.

I did not use `Thread.sleep(10000)` to delay the execution. Instead, I allowed Spring's scheduler to manage the execution time.

### 6. Creating DynamicEmailSchedulerController

Next, I created a REST controller named `DynamicEmailSchedulerController`.

This controller exposes the API through which users can schedule email tasks.

```java
@RestController
@RequestMapping("/emails")
public class DynamicEmailSchedulerController {

    private final DynamicEmailSchedulerService schedulerService;

    public DynamicEmailSchedulerController(
            DynamicEmailSchedulerService schedulerService) {

        this.schedulerService = schedulerService;
    }

    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleEmail(
            @RequestBody EmailRequest request) {

        schedulerService.scheduleEmail(
                request.to(),
                request.subject(),
                request.body()
        );

        return ResponseEntity.ok(
            "Email scheduled successfully. It will be sent after 10 seconds."
        );
    }

    public record EmailRequest(
            String to,
            String subject,
            String body
    ) {}
}
```

I created a POST API with the endpoint `/emails/schedule`.

The controller accepts the email details through `@RequestBody` and passes them to `DynamicEmailSchedulerService`.

I used a Java record named `EmailRequest` to represent the incoming request data.

After registering the scheduled task, the controller immediately returns a success response instead of waiting for ten seconds.

### 7. Testing the API using Postman

After completing the implementation, I started my Spring Boot application and opened Postman to test the endpoint.

**HTTP Method:** POST

**Endpoint:**

```text
http://localhost:8081/emails/schedule
```

I selected:

- Body
- Raw
- JSON

Then I provided the following request:

```json
{
    "to": "hemanth@gmail.com",
    "subject": "Spring Boot Task Scheduler",
    "body": "Your scheduled email is ready."
}
```

When I send this request, the controller passes the email details to the scheduling service.

The API immediately returns:

```text
Email scheduled successfully.
It will be sent after 10 seconds.
```

After approximately ten seconds, the scheduler executes the email task and prints the email details in the application console.

Expected console output:

```text
EMAIL SENDING STARTED!
To: hemanth@gmail.com
Subject: Spring Boot Task Scheduler
Message: Your scheduled email is ready.
Executed by: email-scheduler-1
EMAIL SENT SUCCESSFULLY!
```

The exact execution time and thread name can vary.

## Understanding the execution flow

```text
                Client / Postman
                       |
                       |
                       v
          POST /emails/schedule
                       |
                       v
     DynamicEmailSchedulerController
                       |
                       v
      DynamicEmailSchedulerService
                       |
                       v
       Instant.now().plusSeconds(10)
                       |
                       v
          Spring TaskScheduler
                       |
                       v
             Wait until target time
                       |
                       v
                EmailService
                       |
                       v
               sendEmail()
                       |
                       v
             Console Output
```

## Result

Through this implementation, I demonstrated how to schedule a task dynamically using Spring's `TaskScheduler`.

Instead of defining a fixed schedule using `@Scheduled`, I created a REST API that allows a user to register an email task at runtime.

When the user calls the API, the application calculates a time ten seconds in the future and registers the email operation with the scheduler.

The API responds immediately, and the email task is executed after the scheduled delay.

**Concepts demonstrated:** `TaskScheduler`, `ThreadPoolTaskScheduler`, `@Configuration`, `@Bean`, `Instant`, `ScheduledFuture`, `schedule()`, REST API, `@RequestBody`, and dynamic task scheduling.

## Important Note

The scheduled tasks are stored in application memory. If the application stops or restarts before a task executes, that scheduled task will be lost.

For this homework, the objective is to understand dynamic scheduling with Spring's `TaskScheduler`. In production applications that require reliable scheduled jobs across restarts, persistent job scheduling or a durable messaging system may be needed.
