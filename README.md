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

# 1. Download & Extract

> **Security Note:** To comply with security best practices and prevent the exposure of database credentials on public repositories, the `firebase-config.json` key has been excluded from the GitHub repository via `.gitignore`. 
>
> Therefore, **please do not `git clone` this project.** Instead, download the provided `.zip` file from the submission portal, which already includes the necessary secure configurations.

1. Download the provided `COMP1020-Music-Library-Management.zip` submission file.
2. Extract the `.zip` file to your preferred local directory.
3. Open your terminal or IDE and navigate to the extracted folder.

*(The required `firebase-config.json` file is already pre-configured and located inside the `src/main/resources/` directory).*
---

# 2.1. Run via IDE (Recommended)

## Eclipse

### Step 1: Import as a Maven Project

1. Open Eclipse.
2. Go to **File → Import...**
3. Search for **Maven**.
4. Select **Existing Maven Projects** → **Next**.
5. Click **Browse...** and choose the cloned `-COMP1020-Music-Library-Management` folder.
6. Ensure that `pom.xml` is checked in the project list.
7. Click **Finish**.
8. Wait until Eclipse finishes downloading all Maven dependencies.

### Step 2: Run the Application

1. Right-click the project root (it should display a small **M** icon).
2. Select **Run As → Maven Build...**
3. In the **Goals** field, enter:

```text
clean javafx:run
```

4. Click **Run**.

---

## IntelliJ IDEA

1. Open the project.
2. Open the **Maven Tool Window**.
3. Navigate to:

```text
Plugins → javafx
```

4. Double-click:

```text
javafx:run
```

---

# 2.2. Run via Terminal

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

**Note 1:** If the Maven Wrapper (`mvnw`) is missing, you can either generate it:

```bash
mvn wrapper:wrapper
```

or run the project using a globally installed Maven:

```bash
mvn clean javafx:run
```


**Note 2:** If you encounter a **"Failed to delete target"** error during execution (commonly caused by IDEs, file indexing services, or OneDrive locking files on Windows), you can safely skip the `clean` phase and run the application directly:

### macOS / Linux / Git Bash

```bash
./mvnw javafx:run
```

### Windows CMD

```cmd
mvnw javafx:run
```

### Windows PowerShell

```powershell
.\mvnw javafx:run
```

This issue does not affect the application source code. It only prevents Maven from deleting and recreating the `target/` directory before launching the application.

---

# 3. Test Credentials

Once the application is running, you can use the following pre-configured accounts to test the Role-Based Access Control (RBAC) features.

## Admin Account

```text
Email: admin1@musicapp.com
Password: admin
```

## Listener (User) Account

```text
Email: tester02@gmail.com
Password: tester
```

## Alternative Option

You can also use the **Sign Up** feature on the login screen to create a new account.

# 🚀 Test Cases
## C.3.1 TC-01: Account Registration

```text
System correctly catches duplicate credentials and displays a warning popup.
```
![Fig B.1.1](screenshots/B.1.1.jpg)

```text
Successful registration confirmation popup routing the user to the Login view.
```
![Fig B.1.2](screenshots/B.1.2.jpg)

```text
Real-time Firebase Database state showing the newly created user with a deterministic Double-Hashed ID (U00000256).
```
![Fig B.1.3](screenshots/B.1.3.jpg)

---

## C.3.2 TC-02: Authentication (Login)

```text
Login interface capturing the registered user's credentials.
```
![Fig B.2.1](screenshots/B.2.1.jpg)

```text
System console verifying Firebase credential validation, successful Dashboard redirection, and the initialization of the AVL Tree search index (104 songs loaded).
```
![Fig B.2.2](screenshots/B.2.2.jpg)

---

## C.3.3 TC-03: Role-Based Access Control (RBAC)

```text
Admin interface displaying elevated privileges with visible 'ADD' and 'DELETE' mutation controls.
```
![Fig B.3.1](screenshots/B.3.1.jpg)

```text
Standard Listener interface showing restricted access, dynamically hiding all administrative mutation controls to prevent unauthorized actions.
```
![Fig B.3.2](screenshots/B.3.2.jpg)


## 4. Playback Engine (Play/Next/Prev)
```text
UI player actively streaming audio, correctly highlighting the current track and updating the progress bar.
```
![Playback View](screenshots/B4.1.jpg)

```text
System console logging the underlying data structure behaviors: sequential traversal through the LinkedList queue and LIFO retrieval from the History stack.
```
![Playback Console](screenshots/B4.2.png)

## 5. User: Favorites
```text
UI highlighting the heart icon to visually confirm the track's favorited state.
```
![Favorite Heart Icon](screenshots/B5.1.jpg)

```text
The favorited track is successfully aggregated into the user's personal 'Your Favorite Songs' library view.
```
![Favorite Song View](screenshots/B5.2.png)

```text
Real-time Firebase synchronization appending the song ID (S1071) to the user's distinct favorites node (fav_U00000256).
```
![Favorite Firebase Sync](screenshots/B5.3.png)

## 6. User: Playlist Management
```text
UI form for creating a new custom playlist.
```
![Create Playlist](screenshots/B6.1.png)

```text
Firebase syncing the generated Playlist ID directly to the User's document.
```
![Playlist Firebase User](screenshots/B6.2.png)

```text
The isolated Playlist node storing its metadata and associated track IDs (S1007).
```
![Playlist Firebase Node](screenshots/B6.3.png)

```text
Modal interface allowing users to append selected tracks to their custom playlists.
```
![Add To Playlist Modal](screenshots/B6.4.png)

```text
The playlist view dynamically rendering the newly aggregated track.
```
![Playlist Track View](screenshots/B6.5.png)

## 7. Account View & Update
```text
User Account interface displaying current session metadata and allowing profile modifications.
```
![Account View](screenshots/B.7.1.png)

```text
Real-time Firebase synchronization confirming the updated user profile data (e.g, fullname: "Ali Ali")
```
![Account Database](screenshots/B.7.2.png)

## 8. User: Search Engine 
```text
Search engine accurately resolving a partial title query (”leave”) to fetch the exact track.
```
![Searching example 1](screenshots/B.8.1.png)

```text
Search engine demonstrating multi-attribute prefix matching, successfully retrieving all tracks where the artist’s name begins with ”bru” (Bruno Mars).
```
![Search example 2](screenshots/B.8.2.png)

## 9. Admin: Global Library Mutation
```text
Aministrative modal for inputting new track metadata and Google Drive media links.
```
![Admin Library View 1](screenshots/B.9.1.png)

```text
Firebase database confirming the automatic conversion of input URL to the direct exportable streaming format.
```
![Admin Library View 2](screenshots/B.9.2.png)

```text
The ’All Songs’ library view immediately reflecting the newly added track, ready for immediate playback.
```
![Admin Library View 3](screenshots/B.9.3.png)

## 10. Admin: Album management 
```text
Administrative interface for defining a new Album with global metadata.
```
![Admin Album View 1](screenshots/B.10.1.png)

```text
Firebase root node for Albums, serving as the parent entity.
```
![Admin Album View 2](screenshots/B.10.2.png)

```text
Nested structure: Tracks inheriting shared attributes from the parent Album node.
```
![Admin Album View 3](screenshots/B.10.3.png)

```text
Album detail view rendering tracks that successfully inherited parent metadata, ensuring consistency across the UI.
```
![Admin Album View 4](screenshots/B.10.4.png)

```text
Global library view automatically synchronizing the new track from the Album, ensuring the song is accessible via both Album and All Songs views.
```
![Admin Album View 5](screenshots/B.10.5.png)

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
