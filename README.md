# Core Java Concurrent Banking System

![Java Version](https://img.shields.io/badge/Java-17%2B-blue?logo=java&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green)

A multi-threaded financial transaction system built in pure Java to demonstrate advanced concurrency principles, thread-safe design, and deadlock prevention strategies.

This repository contains two distinct versions of the project:
1.  A **High-Throughput Simulation** to validate system integrity under heavy load.
2.  An **Interactive Command-Line Application** that provides a user-facing banking interface.

---

## Core Concepts Demonstrated

This project is a practical implementation of fundamental and advanced concurrency concepts in Java:

* **Concurrency & Thread Safety:** The core architecture is designed to handle multiple simultaneous operations without data corruption or race conditions.
* **Mutual Exclusion:** Utilizes `java.util.concurrent.locks.ReentrantLock` to ensure that critical sections of code (like modifying an account balance) are atomic and executed by only one thread at a time.
* **Deadlock Prevention:** Implements a **lock-ordering** mechanism to prevent deadlocks during fund transfers. By acquiring locks on account objects in a consistent, deterministic order (based on their `System.identityHashCode`), the circular wait condition required for a deadlock is made impossible.
* **Producer-Consumer Pattern:** The simulation version uses a `BlockingQueue` to decouple transaction generation (the producer) from transaction processing (the consumers), a classic and efficient concurrency pattern.
* **Thread-Safe Collections:** The interactive version uses `ConcurrentHashMap` for storing users and accounts, providing superior performance and safety in a multi-threaded context compared to synchronized standard collections.
* **Separation of Concerns (SoC):** The interactive application is architected into three distinct layers (View, Service, Model) to create a maintainable and decoupled codebase.

---

## Project Versions

### Version 1: High-Throughput Simulation

This version stress-tests the concurrent logic by simulating a high volume of random transactions processed by a pool of worker threads.

#### **Purpose**
To validate that the system's core logic is robust, deadlock-free, and maintains data integrity (i.e., no money is created or destroyed) under heavy concurrent load.

#### **How it Works**
1.  A set of bank accounts are initialized with a starting balance.
2.  A fixed-size thread pool (`ExecutorService`) of `TransactionProcessor` threads is created.
3.  The main thread acts as a **producer**, creating thousands of random `TRANSFER` transactions and placing them onto a shared `BlockingQueue`.
4.  The worker threads act as **consumers**, taking transactions from the queue and processing them concurrently.
5.  After all transactions are processed, a final **audit** is performed to compare the initial total balance with the final total balance, verifying system integrity.

#### **How to Run**
1.  Ensure all source files (`BankingSimulation.java`, `TransactionProcessor.java`, `Account.java`, etc.) are in the same directory.
2.  Compile the source code:
    ```bash
    javac *.java
    ```
3.  Run the simulation:
    ```bash
    java BankingSimulation
    ```

### Version 2: Interactive Command-Line Application

This version provides a user-facing interface to interact with the thread-safe banking service.

#### **Purpose**
To demonstrate how the concurrent-safe core logic can be encapsulated within a service layer and exposed to a user, showcasing clean architecture and state management.

#### **Features**
* User Registration and Login
* Create multiple bank accounts per user
* Deposit funds into an account
* Withdraw funds from an account
* Transfer funds between any two accounts in the system (safely and concurrently)
* View account balances and transaction history

#### **How to Run**
1.  Ensure all source files (`InteractiveBank.java`, `BankService.java`, `User.java`, `Account.java`) are in the same directory.
2.  Compile the source code:
    ```bash
    javac *.java
    ```
3.  Run the interactive application:
    ```bash
    java InteractiveBank
    ```

---

## Architectural Overview (Interactive Version)

The interactive application follows a classic three-layer architecture:

* **View Layer (`InteractiveBank.java`):** Responsible for all console input and output. It acts as the user interface and knows nothing about the underlying business logic or concurrency controls.
* **Service Layer (`BankService.java`):** Contains the core business logic. It manages users and accounts, orchestrates transactions, and, most importantly, encapsulates all the complex concurrency logic (locking and deadlock prevention).
* **Model Layer (`Account.java`, `User.java`):** Simple Java objects (POJOs) that represent the data. The `Account` class is responsible for its own state and contains the `ReentrantLock` to protect its balance.

---

## License

This project is licensed under the MIT License.
