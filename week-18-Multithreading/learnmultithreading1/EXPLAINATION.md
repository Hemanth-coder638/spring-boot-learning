# Week 18 - Multithreading in Java and Spring Boot

These notes explain the concepts covered in Week 18 with short examples and diagrams.

## Learning roadmap

- 18.1 Introduction to Threads in Java
- 18.2 Java Executor Framework
- 18.3 Future and CompletableFuture
- 18.4 Task Scheduling in Spring Boot
- 18.5 Asynchronous Task Scheduling in Spring Boot
- 18.6 Tomcat Server Threading Model
- 18.7 Homework for Async and Blocking Task Scheduling

---

# 18.1 Introduction to Threads in Java

## Program, process, and thread

A **program** is a set of instructions stored on disk. A **process** is a running instance of a program. A **thread** is an execution path inside a process. A process can contain multiple threads.

Threads in one process share resources such as the heap and loaded classes, but each thread has its own stack and execution state.

```text
Program on disk
     |
     | Start
     v
   Process
     |
     +------------------------------+
     | Shared memory and resources  |
     +------------------------------+
       |            |           |
    Thread 1     Thread 2    Thread 3
    Stack 1      Stack 2     Stack 3
```

## Single-threaded and multithreaded execution

A single-threaded application performs work on one thread. If that thread waits for a slow operation, it cannot perform its next operation until the wait finishes.

A multithreaded application can make progress on multiple tasks using multiple threads.

```text
Single thread:  Task A -------- Task B -------- Task C

Multiple:
Thread 1:       Task A ------------------------>
Thread 2:             Task B ----------------->
Thread 3:                   Task C ----------->
```

Multithreading does not mean all threads run at precisely the same instant. The operating system schedules runnable threads on available CPU cores. More threads can exist than the number of CPU cores; some may wait for CPU time.

## Creating a thread

A common approach is to provide a `Runnable` task to a `Thread`.

```java
Runnable task = () -> {
    System.out.println(Thread.currentThread().getName());
};

Thread worker = new Thread(task);
worker.start();
```

`start()` asks the JVM to start a new thread, which then executes `run()`. Calling `run()` directly is just a normal method call and does not create a new thread.

## Thread states

Java defines six thread states:

| State | Meaning |
|---|---|
| `NEW` | Thread object created; `start()` not called. |
| `RUNNABLE` | Eligible to run or currently running. |
| `BLOCKED` | Waiting to acquire a monitor lock. |
| `WAITING` | Waiting indefinitely for another action. |
| `TIMED_WAITING` | Waiting for a specified time, such as during `sleep()`. |
| `TERMINATED` | Execution has finished. |

Simplified lifecycle:

```text
 NEW --start()--> RUNNABLE ------run() ends------> TERMINATED
                      |
          +-----------+------------+
          |                        |
     waits for lock            sleep(timeout)
          |                        |
        BLOCKED               TIMED_WAITING
          |                        |
     lock acquired             timeout ends
          +-----------+------------+
                      |
                   RUNNABLE

RUNNABLE --wait()/join()--> WAITING --notification/completion--> RUNNABLE
```

The diagram is simplified. For example, `sleep()` does not release locks already held by the sleeping thread.

## `sleep()` and `join()`

`Thread.sleep(2000)` pauses the current thread for about two seconds. `join()` makes the calling thread wait for another thread to finish.

```java
Thread worker = new Thread(() -> {
    System.out.println("Worker started");
});

worker.start();
worker.join();
System.out.println("Worker finished");
```

## Runnable and Callable

Both interfaces describe work that can be executed by another thread.

| Runnable | Callable |
|---|---|
| Has `run()` | Has `call()` |
| Does not return a result | Returns a result |
| Cannot declare checked exceptions | Can declare checked exceptions |
| Often used with `Thread` and executors | Usually submitted to an `ExecutorService` |

```java
Runnable runnable = () -> System.out.println("Working");

Callable<String> callable = () -> "Task result";
```

A submitted `Callable<V>` can produce a `Future<V>`.

## Platform threads and virtual threads

Platform threads are traditional Java threads backed by operating-system threads. Java 21 also supports virtual threads, which are lightweight threads managed by the JVM. Virtual threads can help applications that perform many blocking operations. They do not make CPU-intensive work faster and do not remove limits on database connections or external services.

---

# 18.2 Java Executor Framework

## Why use the Executor Framework?

Creating and managing a new thread manually for every task becomes difficult. An application must manage thread creation, reuse, task submission, resource limits, and shutdown.

The Java Executor Framework (`java.util.concurrent`) separates task submission from the details of task execution. It can reuse worker threads through a thread pool.

## Main interfaces and classes

```text
             Executor
                |
                v
          ExecutorService
             /       \
            v         v
 ThreadPoolExecutor  ScheduledExecutorService
```

- **Executor:** Basic abstraction with `execute(Runnable)`.
- **ExecutorService:** Extends Executor and adds `submit`, shutdown, and lifecycle methods.
- **ThreadPoolExecutor:** Configurable implementation that manages worker threads and a task queue.
- **ScheduledExecutorService:** Supports delayed and periodic execution.

## `execute()` versus `submit()`

`execute()` accepts a `Runnable` and returns no result.

```java
executor.execute(() -> System.out.println("Task running"));
```

`submit()` accepts a `Runnable` or `Callable` and returns a `Future`.

```java
Future<String> future = executor.submit(() -> "Hello");
```

Use `execute()` when you only need to run work. Use `submit()` when you need a Future to inspect completion, retrieve a result, or observe task failure.

## Thread pool

A thread pool is a group of reusable worker threads. Tasks are submitted to the executor. Available workers run tasks; tasks that cannot start immediately may wait in a queue.

```text
Submitted tasks
 T1  T2  T3  T4  T5
          |
          v
     +-----------+
     | Task queue|
     +-----------+
       |   |   |
       v   v   v
     Worker threads
```

Reusing threads avoids repeatedly creating and destroying threads. A bounded pool and queue can also limit resource consumption.

## Creating common executors

```java
ExecutorService fixed = Executors.newFixedThreadPool(5);
ExecutorService single = Executors.newSingleThreadExecutor();
ScheduledExecutorService scheduled =
        Executors.newScheduledThreadPool(2);
```

For detailed control over pool size, queue capacity, and rejection behaviour, use `ThreadPoolExecutor`.

## ThreadPoolExecutor configuration

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
        2,                         // core pool size
        5,                         // maximum pool size
        30,                        // keep-alive time
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(10)
);
```

| Setting | Meaning |
|---|---|
| Core pool size | Number of core workers the executor tries to maintain. |
| Maximum pool size | Maximum number of worker threads. |
| Keep-alive | How long excess idle workers may remain alive. |
| Work queue | Holds tasks waiting for a worker. |
| Rejection handler | Defines what happens when new work cannot be accepted. |

By default, core threads do not time out. Keep-alive time normally applies to workers above the core size unless core-thread timeout is enabled.

### Task admission sequence

For a typical `ThreadPoolExecutor`:

1. If fewer than the core number of workers exist, create a worker for the task.
2. Otherwise, try to put the task in the work queue.
3. If the queue is full and the pool is below maximum size, create another worker.
4. If the queue is full and maximum size has been reached, invoke the rejection handler.

With core size 2, maximum size 5, and queue capacity 10, a rapid burst can be accepted as 2 running + 10 queued + 3 additional running = 15 tasks. Further submissions are rejected while the executor is saturated.

## Rejection policies

A rejection policy handles work that cannot be accepted.

- `AbortPolicy`: throws `RejectedExecutionException`.
- `CallerRunsPolicy`: runs the task on the submitting thread, unless the executor is shut down.
- `DiscardPolicy`: silently discards the rejected task.
- `DiscardOldestPolicy`: removes the oldest queued task and retries submission, subject to executor state.

A custom handler can log or retry. Retrying needs careful shutdown handling and protection against endless retry loops.

## Executor shutdown

```java
executor.shutdown();

if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
    executor.shutdownNow();
}
```

`shutdown()` stops accepting new tasks but allows accepted tasks to finish. `shutdownNow()` attempts to interrupt running tasks and returns tasks that never started. `awaitTermination()` waits for termination for a specified time.

Always manage executor lifecycle so application threads do not remain alive unnecessarily.

---

# 18.3 Future and CompletableFuture

## Future

A `Future` is a handle to a result that may become available later. Submitting a `Callable` to an `ExecutorService` can return a Future.

```java
ExecutorService executor = Executors.newSingleThreadExecutor();

Future<String> future = executor.submit(() -> {
    Thread.sleep(2000);
    return "User data";
});

String result = future.get(); // Blocks if the result is not ready
System.out.println(result);

executor.shutdown();
```

Important methods:

- `get()`: waits for completion and returns the result.
- `isDone()`: checks whether execution has completed.
- `cancel(true)`: attempts cancellation and may interrupt the task.
- `isCancelled()`: checks whether cancellation succeeded.

## Limitation of Future

`Future.get()` is blocking. If the result is not ready, the calling thread waits. Traditional Future does not provide a fluent callback pipeline for “when this result is ready, transform it and then run another operation.” The caller often blocks or polls.

## CompletableFuture

`CompletableFuture` implements `Future` and `CompletionStage`. It supports asynchronous pipelines, callbacks, combining independent work, and exception handling.

```java
CompletableFuture<String> future =
        CompletableFuture.supplyAsync(() -> "Hemanth");

future.thenApply(String::toUpperCase)
      .thenAccept(System.out::println);
```

```text
supplyAsync()
     |
  "Hemanth"
     |
 thenApply()
     |
  "HEMANTH"
     |
 thenAccept()
     |
   print
```

## Important methods

| Method | Purpose |
|---|---|
| `supplyAsync()` | Runs a supplier asynchronously and produces a value. |
| `runAsync()` | Runs an asynchronous task without a result. |
| `thenApply()` | Transforms a result and returns a new stage. |
| `thenAccept()` | Consumes a result without returning a value. |
| `thenRun()` | Runs an action after completion without using the result. |
| `thenApplyAsync()` | Performs a transformation asynchronously. |
| `thenCombine()` | Combines results from two independent futures. |
| `allOf()` | Completes when all supplied futures complete. |
| `anyOf()` | Completes when any supplied future completes. |
| `exceptionally()` | Recovers from exceptional completion. |
| `handle()` | Handles either a result or an exception. |
| `join()` | Waits for completion and returns the result; failures are unchecked completion exceptions. |

## thenApply, thenAccept, and thenRun

```java
CompletableFuture<String> future =
        CompletableFuture.supplyAsync(() -> "Hemanth");

future.thenApply(String::toUpperCase);  // transforms and returns
future.thenAccept(System.out::println); // consumes the result
future.thenRun(() -> System.out.println("Finished")); // no input
```

The `Async` variants request asynchronous execution. Without an explicit executor, they commonly use the default asynchronous facility. A Spring application may use a configured executor for better control.

## Combining futures

`thenCombine()` combines two independent results.

```java
CompletableFuture<String> first =
        CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> second =
        CompletableFuture.supplyAsync(() -> "World");

CompletableFuture<String> combined =
        first.thenCombine(second, (a, b) -> a + " " + b);
```

`allOf()` is useful when several independent tasks must all finish before the next stage runs.

```java
CompletableFuture<String> user =
        CompletableFuture.supplyAsync(() -> "User");
CompletableFuture<String> orders =
        CompletableFuture.supplyAsync(() -> "Orders");
CompletableFuture<String> payments =
        CompletableFuture.supplyAsync(() -> "Payments");

CompletableFuture<Void> all =
        CompletableFuture.allOf(user, orders, payments);

CompletableFuture<String> report = all.thenApply(ignored ->
        user.join() + ", " + orders.join() + ", " + payments.join()
);
```

`allOf()` returns `CompletableFuture<Void>`; it does not collect the individual results. Once it completes successfully, the individual futures can be read without waiting again.

`allOf()` does not start tasks. The tasks start when their futures are created, for example through `supplyAsync()` or asynchronous service methods.

## Exception handling

```java
CompletableFuture<String> result =
        CompletableFuture.supplyAsync(() -> {
            throw new IllegalStateException("Service unavailable");
        }).exceptionally(ex -> "Fallback value");
```

If one input future completes exceptionally, `allOf()` also completes exceptionally. Decide whether the entire report should fail or whether partial results and fallback values are acceptable.

## Executor selection

No-executor overloads such as `supplyAsync()` commonly use the common ForkJoinPool. In production, an explicitly configured executor can provide clearer limits, thread names, monitoring, and resource isolation.

---

# 18.4 Task Scheduling in Spring Boot

## What is task scheduling?

Task scheduling means running work at a specified time or repeatedly at an interval without a user manually starting each execution.

Examples include cleaning expired records, sending reminders, archiving logs, fetching external data, and performing health checks.

Spring supports scheduling with `@Scheduled` and the `TaskScheduler` abstraction.

## Enable scheduling

```java
@SpringBootApplication
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## `@Scheduled`

A scheduled method is normally a no-argument method with a `void` return type.

```java
@Component
public class ReportJob {
    @Scheduled(fixedRate = 5000)
    public void runReport() {
        System.out.println("Report job executed");
    }
}
```

## Scheduling parameters

| Option | Meaning |
|---|---|
| `fixedRate` | Interval between the start times of consecutive executions. |
| `fixedDelay` | Delay between the end of one execution and the start of the next. |
| `initialDelay` | Delay before the first execution. |
| `cron` | Calendar-based schedule expressed as a cron expression. |

```java
@Scheduled(fixedRate = 5000)
public void fixedRateJob() { }

@Scheduled(fixedDelay = 5000, initialDelay = 2000)
public void fixedDelayJob() { }

@Scheduled(cron = "0 0 9 * * MON-FRI")
public void weekdayMorningJob() { }
```

Fixed rate is based on start times. Fixed delay is measured after the previous execution finishes. Actual timing depends on scheduler configuration and available threads.

## Cron expressions

Spring cron expressions use six fields:

```text
second minute hour day-of-month month day-of-week
```

Example:

```text
0 0 9 * * MON-FRI
```

This represents 9:00 AM every Monday through Friday in the scheduler's configured time zone.

Common symbols include `*` (every value), `,` (list), `-` (range), `/` (step), and `?` (no specific day value in supported day fields). Confirm the exact syntax and time zone for the Spring version and configuration in use.

## How scheduling works

```text
Application starts
       |
Spring finds @Scheduled methods
       |
Registers scheduled tasks
       |
TaskScheduler triggers a task
       |
Scheduler thread executes the method
```

When no custom scheduler is configured, Spring scheduling commonly uses a single scheduler thread. A long-running job can delay other scheduled jobs. Configure a suitable `ThreadPoolTaskScheduler` when concurrent scheduled execution is needed.

## TaskScheduler

`TaskScheduler` is a Spring interface for scheduling tasks at specific times or periodically. It supports programmatic scheduling and is useful when the task or schedule is decided at runtime. `ThreadPoolTaskScheduler` is a common implementation backed by scheduled executor functionality.

---

# 18.5 Asynchronous Task Scheduling in Spring Boot

## Blocking scheduled work

If a scheduled task takes a long time and the scheduler has only one thread, other scheduled tasks can be delayed.

```text
Single scheduler thread:
Task A: [--------- 10 seconds ---------]
Task B:                                [runs]
Task C:                                      [runs]
```

## Using @Async

`@Async` lets a Spring-managed method run through an asynchronous executor.

Enable it:

```java
@Configuration
@EnableAsync
public class AsyncConfig {
}
```

Example:

```java
@Service
public class NotificationService {
    @Async
    public void sendNotification() {
        System.out.println(Thread.currentThread().getName());
    }
}
```

The call must normally pass through the Spring proxy. In the usual proxy-based setup, calling an `@Async` method from another method in the same object does not activate asynchronous execution.

`@EnableAsync` enables annotation-driven async execution. `@Async` and `@Scheduled` solve different problems: one controls how work is executed, the other controls when work is triggered.

## Combining @Scheduled and @Async

```java
@Component
public class BackgroundJobs {

    @Async
    @Scheduled(fixedRate = 5000)
    public void runLongJob() throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        Thread.sleep(8000);
    }
}
```

The scheduler triggers the method; the async executor performs the work. Repeated triggers may overlap if executor capacity allows it. Ensure the job is safe to run concurrently.

## Configure a bounded async executor

```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("async-");
        executor.initialize();

        return executor;
    }
}
```

Use the named executor:

```java
@Async("taskExecutor")
public void sendNotification() {
    // asynchronous work
}
```

A real application should select pool and queue sizes based on workload and resource limits, and define rejection and error-handling behaviour. Avoid uncontrolled thread creation.

---

# 18.6 Tomcat Server Threading Model

## What is Tomcat?

Apache Tomcat is a servlet container commonly used by Spring Boot web applications. It accepts HTTP requests and provides the servlet environment in which Spring MVC controllers execute.

## Normal request flow

```text
Client / Browser
       |
   HTTP request
       |
Tomcat connector
       |
Tomcat worker thread
       |
Spring DispatcherServlet
       |
Controller -> Service -> Repository
       |
HTTP response
       |
Worker thread becomes available
```

Tomcat uses request-processing worker threads. A worker handles a request and becomes available again when processing finishes.

## Blocking request flow

If controller logic waits for a slow database call, external API, `Thread.sleep()`, or other blocking operation, the request thread remains occupied.

```text
Request 1 -> Tomcat thread 1 -> waits
Request 2 -> Tomcat thread 2 -> processes
Request 3 -> Tomcat thread 3 -> processes
```

If enough request threads are occupied, new requests may wait or be rejected after configured limits are reached.

## Asynchronous request processing

Spring MVC supports asynchronous return types such as `Callable`, `DeferredResult`, and `CompletableFuture` in supported controller scenarios.

```text
HTTP request
     |
Tomcat request thread
     |
Hands off asynchronous work
     |
Releases request-processing thread
     |
Worker executor performs operation
     |
Result becomes ready
     |
HTTP response is completed
```

This can free the original request-processing thread while work continues elsewhere. It does not eliminate the work or make a blocking operation itself non-blocking. The operation still consumes a worker thread unless it uses a genuinely non-blocking API.

## Tomcat configuration

Common Spring Boot properties include:

```properties
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10
server.tomcat.accept-count=100
```

- `threads.max`: maximum request-processing threads.
- `threads.min-spare`: minimum spare threads.
- `accept-count`: connection queue capacity when request-processing threads are busy.

These values are not universal performance recommendations. Verify defaults and behaviour for the application's Spring Boot and Tomcat versions.

## Spring bean thread safety

Spring controllers, services, and repositories are singleton-scoped by default. The same bean instance may be called by multiple request threads at the same time.

Avoid request-specific mutable fields in singleton beans.

Unsafe:

```java
@Service
public class ReportService {
    private String currentUser; // Shared mutable state
}
```

Safer:

```java
@Service
public class ReportService {
    public String createReport(String username) {
        return "Report for " + username;
    }
}
```

Keep services stateless where possible. Shared configuration such as a database URL is different from mutable state belonging to one request.

---

# Quick revision

| Concept | Main idea |
|---|---|
| Thread | Execution path inside a process. |
| Runnable | Task without a return value. |
| Callable | Task that returns a value and can throw checked exceptions. |
| Executor | Separates task submission from execution. |
| Thread pool | Reuses a controlled group of worker threads. |
| Future | Handle for a result that may become available later. |
| CompletableFuture | Supports asynchronous pipelines and composition. |
| `allOf()` | Completes after all supplied futures complete. |
| `@Scheduled` | Declares a task that runs at a configured time or interval. |
| TaskScheduler | Spring abstraction for programmatic scheduling. |
| `@Async` | Executes a Spring bean method through an async executor. |
| Tomcat worker | Processes an HTTP request. |
| Thread safety | Shared singleton beans must be safe for concurrent calls. |

## Design reminders

1. Prefer managed executors and schedulers over unmanaged threads.
2. Plan executor shutdown and task cancellation.
3. Use bounded pools and queues when workload limits matter.
4. Handle asynchronous failures.
5. `CompletableFuture.allOf()` coordinates futures; it does not start tasks.
6. Asynchronous execution does not automatically make blocking work non-blocking.
7. Keep singleton Spring services stateless or protect shared mutable state.
8. Measure under realistic load rather than guessing thread counts.
