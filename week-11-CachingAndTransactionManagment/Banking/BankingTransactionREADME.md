# Week 11 Homework — Banking Application Transaction Management (Part 1)

## Overview
This project is part of my Week 11 Spring Boot learning journey. The goal of this homework is to understand **database transactions**, **ACID properties**, and **transaction isolation levels** in a real banking application.

The application is built to show how banking operations behave when multiple requests access the same account at the same time. It demonstrates how Spring Boot manages transactions using `@Transactional` and how isolation levels affect concurrent read and write operations.

This README covers only **Part 1: Transaction Management**. The optimistic and pessimistic locking part will be documented separately in Part 2.

---

## What is a Database Transaction?

### Short definition
A **transaction** is a group of database operations that are treated as a single unit of work. Either all operations succeed, or all of them fail together.

### Interview-style definition
A database transaction is a logical sequence of operations that must be executed atomically to keep the data safe, consistent, and reliable even when multiple users access the same data concurrently.

### Simple banking meaning
In banking, a transaction ensures that money is not lost during operations like:
- deposit
- withdrawal
- transfer
- concurrent balance updates

If one step fails, the entire operation should rollback so the account does not end up in an invalid state.

---

## ACID Properties

Transactions are built on the four ACID properties.

### 1. Atomicity
Atomicity means **all or nothing**.

If a transfer involves deducting money from one account and adding it to another, both steps must complete together. If one step fails, both changes are rolled back.

### 2. Consistency
Consistency means the database must always move from one valid state to another valid state.

For example, the bank balance should never become negative unless the business rule allows it.

### 3. Isolation
Isolation means one transaction should not interfere with another transaction while both are running.

This is the main concept demonstrated in this project using isolation levels.

### 4. Durability
Durability means once a transaction is committed, the data must remain saved even if the application crashes or the system restarts.

---

## Why Transactions Matter in Banking

Banking systems are one of the best examples of transaction usage because money-related data must always remain accurate.

Transactions help ensure:
- balance updates are safe
- transfer operations are reliable
- concurrent requests do not corrupt data
- rollback happens if something goes wrong

Without transactions, two users updating the same account at the same time could cause incorrect balances.

---

## Transaction Isolation Levels

Isolation levels define how much one transaction can see or be affected by another transaction.

This project demonstrates three important isolation levels:
- `READ_COMMITTED`
- `REPEATABLE_READ`
- `SERIALIZABLE`

---

## Standard Definitions of Isolation Levels

### 1. READ_COMMITTED
A transaction can only read data that has already been committed by another transaction.

This level prevents dirty reads, but the value may change if another transaction commits in between two reads.

### 2. REPEATABLE_READ
A transaction sees a stable snapshot of the data for the full duration of the transaction.

If the same row is read multiple times inside the same transaction, it returns the same value.

### 3. SERIALIZABLE
This is the strongest isolation level.

Transactions behave as if they were executed one after another in sequence. It provides the highest safety, but may reduce concurrency.

---

## Internal Working of a Transaction in Spring Boot

### How Spring handles it internally
When a service method is annotated with `@Transactional`, Spring creates a transaction boundary around that method.

### Basic working flow
1. A request enters the controller.
2. Controller calls the service method.
3. Spring opens a transaction before the method starts.
4. Database operations happen inside that transaction.
5. If everything succeeds, Spring commits the transaction.
6. If an exception occurs, Spring rolls back the transaction.

### Why this is important
This prevents partial updates and protects data integrity in banking operations.

---

## Internal Transaction Flow Diagram

```text
Client Request
     |
     v
Controller
     |
     v
Service Method (@Transactional)
     |
     v
Spring Transaction Manager
     |
     v
Database Operation
     |
     +-------------------------+
     |                         |
     | Success                 | Failure
     |                         |
     v                         v
   COMMIT                    ROLLBACK
     |                         |
     v                         v
Database Updated         No Partial Change
```

---

## Banking Example of Isolation Levels

### Example 1: READ_COMMITTED
Suppose the account balance is `1000`.

- Transaction A reads the balance as `1000`
- Transaction B deposits `500` and commits
- Transaction A reads again and now sees `1500`

This shows that the data can change during the transaction.

### Example 2: REPEATABLE_READ
Suppose the account balance is `1000`.

- Transaction A reads the balance as `1000`
- Transaction B deposits `500` and commits
- Transaction A reads again and still sees `1000`

This shows a stable snapshot inside the transaction.

### Example 3: SERIALIZABLE
Suppose two transactions try to update the same balance.

- Transaction A begins first
- Transaction B tries to update at the same time
- Database forces a strict ordering or may reject one transaction

This gives the safest result, especially for banking-style logic.

---

## How Transaction Concepts Are Implemented in This Banking Application

This project uses a single `account` table in PostgreSQL and applies transaction management in the service layer.

### Main table
The `account` table stores:
- id
- account number
- account holder name
- balance
- version
- timestamps

### How normal banking operations work
The `AccountService` class handles:
- create account
- get account
- deposit
- withdraw
- transfer

These methods use `@Transactional` so the database operations are executed safely as a unit.

### How transaction isolation is tested
The `TransactionDemoService` class contains special methods to demonstrate isolation behavior:
- `readBalanceWithReadCommitted()`
- `readBalanceWithRepeatableRead()`
- `readBalanceWithSerializable()`
- `simulateConcurrentUpdate()`

These methods are designed to show how concurrent transactions behave when multiple requests access the same account.

### Why this is a good banking design
This design is simple but powerful because a single `Account` entity is enough to demonstrate:
- transactional rollback
- consistent balance changes
- concurrent access behavior
- isolation-level differences

---

## Service Layer Explanation

### `AccountService`
This service contains normal banking operations.

It is responsible for:
- creating accounts
- reading account details
- depositing money
- withdrawing money
- transferring money

Each method uses transaction management to keep balance updates safe.

### `TransactionDemoService`
This service exists only for demonstration purposes.

It is responsible for proving:
- how `READ_COMMITTED` behaves
- how `REPEATABLE_READ` behaves
- how `SERIALIZABLE` behaves
- how concurrent read/write operations affect the same data

---

## Controller Layer Explanation

### `AccountController`
Used for normal banking API operations.

Endpoints:
- `POST /accounts`
- `GET /accounts/{accountNumber}`
- `POST /accounts/{accountNumber}/deposit`
- `POST /accounts/{accountNumber}/withdraw`
- `POST /accounts/transfer`

### `TransactionDemoController`
Used only to demonstrate transaction behavior.

Endpoints:
- `GET /demo/isolation/read-committed/{accountNumber}`
- `GET /demo/isolation/repeatable-read/{accountNumber}`
- `GET /demo/isolation/serializable/{accountNumber}`
- `POST /demo/isolation/simulate-concurrent-updates/{accountNumber}`

---

## Repository Layer Role

The repository layer communicates directly with PostgreSQL.

It provides:
- normal account lookup
- pessimistic lock lookup for later part of the homework
- data persistence for account updates

For Part 1, the repository helps the services read and update the account data safely during transaction execution.

---

## What I Learned from This Part

This transaction homework helped me understand:
- what a database transaction really means
- why ACID properties are critical in banking
- how Spring Boot manages transactions using `@Transactional`
- how isolation levels affect concurrent access
- how the same account can behave differently under different isolation levels
- why concurrency testing is important in backend systems

---

## Summary

This banking application demonstrates the foundation of transaction management in Spring Boot. It shows how to keep balance updates reliable, how to protect data with `@Transactional`, and how different isolation levels affect concurrent requests.

This is the base of the banking project. Part 2 will cover:
- optimistic locking
- pessimistic locking
- retry handling
- conflict simulation
- lock-based concurrency control






# Week 11 Homework — Banking Application Locking Mechanisms (Part 2)

## Overview

This section of the project focuses on **concurrency control using locking mechanisms** in a banking system. While Part 1 explained transactions and isolation levels, this part demonstrates how to handle **concurrent updates on the same data** using:

- Optimistic Locking
- Pessimistic Locking

The goal is to prevent **data inconsistency issues like lost updates**, which are critical in financial systems.

---

## What is Locking?

### Short definition

Locking is a mechanism used to control concurrent access to data so that multiple transactions do not corrupt it.

### Interview-style definition

Locking is a concurrency control technique that ensures data integrity by restricting how multiple transactions can read and modify shared resources simultaneously.

### Banking meaning

In banking, locking ensures that when multiple users try to update the same account balance, the final result remains correct and consistent.

---

## Why Locking is Required

Without locking, concurrent updates can lead to:

- Lost updates
- Incorrect balances
- Data inconsistency
- Race conditions

Example:

Initial balance = 100 Two users deposit 100 at the same time

Expected balance = 300 Actual (without locking) = 200 ❌

---

## Types of Locking

This project demonstrates two major types:

1. Optimistic Locking
2. Pessimistic Locking

---

## Optimistic Locking

### Concept

Optimistic locking assumes that conflicts are rare. Instead of locking data, it allows multiple transactions to proceed and checks for conflicts at the time of update.

### How it works

- Each row has a version field
- When a transaction updates a row, the version is checked
- If another transaction already modified the row, the update fails

### Key Annotation

```java
@Version
private Long version;
```

---

## Internal Working of Optimistic Locking

```text
Thread 1 reads version = 1
Thread 2 reads version = 1

Thread 1 updates → version becomes 2
Thread 2 tries to update → fails (version mismatch)
```

---

## Banking Example (Optimistic Locking)

Initial balance = 100

- User A reads balance = 100
- User B reads balance = 100

User A deposits 100 → balance = 200 (version updated) User B deposits 100 → fails due to version mismatch

System retries → reads latest balance = 200 → updates to 300

Final balance = 300 ✅

---

## Implementation in This Project (Optimistic)

### Entity Layer

```java
@Version
private Long version;
```

### Service Layer

```java
@Transactional
@Retryable(
    retryFor = { ObjectOptimisticLockingFailureException.class },
    maxAttempts = 3,
    backoff = @Backoff(delay = 100, multiplier = 2)
)
public Account updateWithOptimisticLock(String accountNumber, BigDecimal amount) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found"));

    account.setBalance(account.getBalance().add(amount));

    return accountRepository.save(account);
}
```

### Key Points

- `@Version` enables version tracking
- Hibernate checks version before update
- If conflict occurs → exception is thrown
- `@Retryable` retries the transaction automatically

---

## Pessimistic Locking

### Concept

Pessimistic locking assumes that conflicts are likely, so it locks the data immediately when it is read.

### How it works

- When a transaction reads a row, it locks it
- Other transactions must wait until the lock is released

### Key Annotation

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

---

## Internal Working of Pessimistic Locking

```text
Thread 1 reads row → LOCK ACQUIRED
Thread 2 tries to read → WAITS

Thread 1 updates and commits → LOCK RELEASED
Thread 2 proceeds
```

---

## Banking Example (Pessimistic Locking)

Initial balance = 100

- User A starts transaction and locks account
- User B tries to update → waits

User A deposits 100 → balance = 200 Lock released

User B deposits 100 → balance = 300

Final balance = 300 ✅

---

## Implementation in This Project (Pessimistic)

### Repository Layer

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select a from Account a where a.accountNumber = :accountNumber")
Optional<Account> findByAccountNumberForPessimistic(String accountNumber);
```

### Service Layer

```java
@Transactional
public Account updateWithPessimisticLock(String accountNumber, BigDecimal amount) {
    Account account = accountRepository.findByAccountNumberForPessimistic(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found"));

    account.setBalance(account.getBalance().add(amount));

    return accountRepository.save(account);
}
```

---

## Optimistic vs Pessimistic Locking

| Feature           | Optimistic Locking   | Pessimistic Locking   |
| ----------------- | -------------------- | --------------------- |
| Locking           | No immediate lock    | Locks row immediately |
| Performance       | High                 | Lower due to waiting  |
| Conflict Handling | Retry mechanism      | Blocking              |
| Use Case          | Low conflict systems | High conflict systems |

---

## How Locking is Tested in This Project

### Optimistic Lock Testing

- Send multiple concurrent requests
- Example: 5 deposits of 100
- Observe:
  - retries happening
  - no lost updates
  - correct final balance

### Pessimistic Lock Testing

- Send multiple concurrent requests
- Observe:
  - requests waiting
  - sequential execution
  - correct final balance

---

## Real-World Usage in Banking Systems

### Optimistic Locking

Used when:

- reads are more frequent than writes
- conflicts are rare
- performance is important

Example:

- balance viewing systems
- profile updates

### Pessimistic Locking

Used when:

- high chance of conflicts
- critical financial operations

Example:

- fund transfer
- high-frequency trading systems

---

## What I Learned from This Part

- difference between optimistic and pessimistic locking
- how `@Version` prevents lost updates
- how retries handle conflicts
- how database locks block concurrent access
- real-world trade-offs between performance and consistency

---

## Summary

Locking is essential in any banking system to prevent data corruption during concurrent updates. This project demonstrates both optimistic and pessimistic locking strategies using Spring Boot and PostgreSQL.

Optimistic locking improves performance with retry logic, while pessimistic locking ensures strict data safety by blocking concurrent access.

Together, these mechanisms make the banking application robust, consistent, and production-ready.
