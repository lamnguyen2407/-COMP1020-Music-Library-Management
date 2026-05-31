# 🎵 Music Library Management System

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square\&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=flat-square)
![Firebase](https://img.shields.io/badge/Firebase-Realtime_DB-FFCA28?style=flat-square\&logo=firebase)
![Architecture](https://img.shields.io/badge/Architecture-MVC_%7C_SPA-success?style=flat-square)

> A desktop music streaming platform built with custom Data Structures and Algorithms using JavaFX and Firebase. Developed as the Final Project for the **COMP1020 Object-Oriented Programming & Data Structures** course at VinUniversity (Spring 2026).

---

# 📖 Project Overview

This project provides a centralized and ad-free music management experience with:

* A responsive **Single-Page Application (SPA)** interface
* **Role-Based Access Control (RBAC)**
* Asynchronous synchronization with Firebase Realtime Database
* Optimized in-memory caching and search mechanisms
* Custom implementations of core Data Structures and Algorithms

The application focuses on combining practical software engineering with efficient algorithmic design.

---

# 🧠 Object-Oriented Programming Concepts

The system architecture applies several core OOP principles:

## Encapsulation

Data integrity is maintained within the `Song`, `Album`, and `User` models by keeping fields private and exposing controlled access through public getters/setters.

## Inheritance

A hierarchical user system is implemented where the abstract `User` class is extended by `Admin` and `ListenerUser`, allowing shared core attributes while supporting specialized behaviors.

## Abstraction

Controllers interact with simplified service methods such as `getTrendingSongs()`, while the service layer abstracts away caching and Firebase synchronization logic.

## Polymorphism

The UI dynamically adapts based on user roles. For example, administrative controls are conditionally rendered depending on the active session type.

---

# 🧱 Core Data Structures

## LinkedList & Stack (Navigation & Playback)

### Stack (LIFO)

Used for navigation history management. Each page transition pushes a UI node onto the stack, enabling efficient back-navigation in $\mathcal{O}(1)$ time.

### LinkedList (Queue)

Used to manage the playback queue for efficient next/previous track operations.

---

## Priority Queue (Top-K Selection)

A Min-Heap structure is used to maintain Top-K collections such as latest songs and albums.

* Time Complexity: $\mathcal{O}(N \log K)$
* Automatically removes older releases to maintain list size constraints

---

## Hash Map (In-Memory Caching)

A HashMap-based cache inside `LibraryManager` enables constant-time song retrieval and reduces repeated Firebase calls during frequent UI refreshes.

* Average Lookup Complexity: $\mathcal{O}(1)$

---

## AVL Tree & HashSet (Search Engine)

### AVL Tree

Acts as an inverted index storing searchable tokens extracted from song titles and artist names.

### HashSet

Each AVL node stores associated songs inside a HashSet to support fast intersection during multi-keyword searches.

---

# ⚙️ Algorithms

## Deterministic User ID Generation

### Polynomial Rolling Hash

Converts email strings into integer keys in $\mathcal{O}(L)$ time, where $L$ is the string length.

### Double Hashing & Multiplication Method

Used for collision handling and dynamic scaling.

Features:

* Amortized $\mathcal{O}(1)$ lookup complexity
* Reduced collision clustering
* Odd-step probing to prevent infinite loops

---

## Prefix Search Traversal

### Workflow

1. Split search queries into lowercase tokens
2. Traverse the AVL tree to retrieve candidate songs
3. Intersect candidate sets using `removeIf`
4. Filter songs that do not contain remaining tokens

### Complexity

* Prefix matching complexity: $\mathcal{O}(\log N + r)$

This approach performs significantly better than naive linear search for large music collections.

---

# 🚀 Installation & Execution

# Prerequisites

* Java Development Kit (JDK) 21 or higher
* Maven
* Eclipse or IntelliJ IDEA

---

# 1. Clone the Repository

```bash
git clone https://github.com/lamnguyen2407/-COMP1020-Music-Library-Management.git
```

Navigate to the project directory:

```bash
cd -COMP1020-Music-Library-Management
```

---

# 2. Firebase Configuration

Place your `firebase-config.json` service account key inside:

```text
src/main/resources/
```

This is required to establish a connection with Firebase Realtime Database.

---

# 3. Run via IDE (Recommended)

## Eclipse

1. Open the project as a Maven Project
2. Right-click project root → `Run As`
3. Select `Maven Build...`
4. Enter the following goal:

```text
clean javafx:run
```

5. Click **Run**

---

## IntelliJ IDEA

1. Open the Maven Tool Window
2. Navigate to:

```text
Plugins → javafx
```

3. Double-click:

```text
javafx:run
```

---

# 4. Run via Terminal

## macOS / Linux / Git Bash

```bash
./mvnw clean javafx:run
```

---

## Windows CMD

```cmd
mvnw clean javafx:run
```

---

## Windows PowerShell

```powershell
.\mvnw clean javafx:run
```

---

## Notes

If the `mvnw` wrapper is missing, you can either:

* Generate it using:

```bash
mvn wrapper:wrapper
```

* Or run directly with a global Maven installation:

```bash
mvn clean javafx:run
```

---

# 👥 Team Members (Team 2)

| Name                  | Student ID |
| --------------------- | ---------- |
| Nguyen Tuan Lam       | V202502497 |
| Nguyen Chu Hung Anh   | V202502224 |
| Nguyen Thi Hong Nhung | V202502876 |
| Hoang Thanh An        | V202502760 |
| Nguyen Nhat Thanh     | V202502691 |

---

# 🏫 Institution

**VinUniversity – College of Engineering and Computer Science**
