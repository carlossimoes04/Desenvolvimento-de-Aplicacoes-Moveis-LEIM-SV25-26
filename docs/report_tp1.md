# Assignment 1 - Hello Kotlin. Hello Android World!

**Course:** Mobile Application Development (DAM)  
**Degree:** LEIM (Bachelor in Computer Science and Multimedia Engineering)  
**Student:** Carlos Simões, A51696, 61D  
**Teacher:** Pedro Fazenda  
**Date:** [Submission Date, e.g., March 8, 2026]  
**Repository URL:** https://github.com/carlossimoes04/Desenvolvimento-de-Aplicacoes-Moveis-LEIM-SV25-26

---

## 1. Introduction
The objective of this assignment is to establish a strong foundation in Kotlin programming and native Android development. The project follows a structured, step-by-step learning path divided into distinct phases:
1. **Getting Started with Kotlin:** Solving foundational console exercises to understand Kotlin's basic types, control flow, exceptions, and sequences (e.g., building a calculator and simulating a bouncing ball).
2. **First Android Steps:** Configuring the Android Studio environment and virtual devices, followed by developing the first "Hello World" application and a "System Info" app using XML Views.
3. **Exploring Kotlin Again (Virtual Library):** Returning to console programming to master Object-Oriented Programming (OOP) principles. This phase involves building a Virtual Library management system using classes, inheritance, custom getters/setters, and companion objects.
4. **Android Enhancements:** Improving the Android experience by exploring the Android Debug Bridge (ADB) and implementing landscape layouts for the "Hello World" app.
5. **AI-Assisted Development (MIP):** Finally, utilizing an AI coding assistant (GitHub Copilot) to autonomously generate an Android mobile application. This phase demonstrates the integration of intelligent agents in modern software engineering workflows to accelerate development.

## 2. System Overview
The complete system is a progressive portfolio divided into five distinct modules, reflecting the assignment's chronological learning path:
1. **Kotlin Fundamentals (Console):** Initial console-based programs designed to practice Kotlin's syntax, control flow, exceptions, and collections. This includes foundational algorithms such as a basic calculator and a bouncing ball simulation using sequences.
2. **First Android Applications (Hello World & System Info):** Native Android applications introducing XML Views and the Activity lifecycle. This module includes a responsive "Hello World" interface and a "System Info" application that programmatically retrieves and displays device-specific hardware and OS data.
3. **Virtual Library System (Console):** A comprehensive Object-Oriented Programming (OOP) module. It manages an inheritance-based architecture (abstract `Book` extending to `DigitalBook` and `PhysicalBook`), library members, and borrowing/returning logic using Kotlin collections, custom accessors, and companion objects.
4. **Android Experience Enhancements:** Advanced Android configurations applied to the initial apps. This includes handling safe window insets, implementing dedicated landscape layouts (`layout-land`), and practical exploration of the device through the Android Debug Bridge (ADB).
5. **Recipe Buddy (MIP - Assisted Code Generation):** An autonomous, AI-generated Android application consuming the public *TheMealDB* API. Built following the MVVM architecture, it features a search bar, a `RecyclerView` for displaying a list of meals, and a detailed screen showing specific ingredients and cooking instructions.

## 3. Architecture and Design
The project's architecture evolves from simple procedural scripts to a complex Android architectural pattern across its different modules:
* **Kotlin Fundamentals:** Built using simple procedural programming and functional constructs to solve algorithmic challenges efficiently.
* **First Android Apps (Hello World & System Info):** Utilizes a standard Activity-based architecture combined with XML `ConstraintLayout` for responsive UI design. It relies on Android system services (e.g., `Build` class) to fetch hardware and OS data programmatically.
* **Virtual Library System:** Designed with strict Object-Oriented Programming (OOP) principles. It implements an inheritance hierarchy where `DigitalBook` and `PhysicalBook` extend a base abstract `Book` class. The `Library` class acts as the central manager, utilizing encapsulation (custom getters/setters) and a `companion object` to maintain a global state of total books created across all instances.
* **Android Enhancements:** Employs Android's resource qualification system (specifically the `res/layout-land` directory) to handle dynamic configuration changes (screen rotation) natively. It also integrates window insets to manage edge-to-edge displays (camera cutouts and system bars).
* **Recipe Buddy App (MIP):** Follows the modern **MVVM (Model-View-ViewModel)** architectural pattern to separate concerns:
  * **Model:** Kotlin data classes representing the API JSON responses.
  * **View:** XML Layouts and Activities responsible strictly for UI rendering and user interactions.
  * **ViewModel:** Manages the UI state and business logic, ensuring the UI is decoupled from the data-fetching processes.
  * **Networking & Media:** Handled by Retrofit2 and Gson for REST API consumption, while asynchronous image loading is managed by Glide (or Coil).

## 4. Implementation

The implementation was carried out in progressive stages, leveraging specific Kotlin features and Android APIs:

### 4.1. Kotlin Fundamentals & Algorithms
* **Simple Exercise:** Implemented an integer array with the first 50 perfect squares using an `IntArray` constructor; using a `range` and `map()`; and using an `Array` with constructor.
* **Control Flow & Error Handling:** Implemented a robust console calculator using `when` expressions for operation routing and `try-catch` blocks to safely handle `NumberFormatException` during user input.
* **Sequences:** Developed the bouncing ball simulation using Kotlin's `Sequence` (`generateSequence`). This demonstrated an understanding of lazy evaluation to efficiently calculate the ball's height rebounds until it stopped.

### 4.2. Object-Oriented Programming (Virtual Library)
* **Encapsulation & Custom Accessors:** In the abstract `Book` class, the `availableCopies` property uses a custom `set(value)` method. This actively prevents negative stock allocations and automatically triggers a warning when copies reach zero, keeping the business logic safe. This class also has a `publicationYear` property that uses a custom `get()` to determine the book era. From `Book`, it was created two other classes to determine if a book is digital or physical, with methods to print their informations. They are, respectively, `DigitalBook` and `PhysicalBook`.
* **Global State Management:** The `Library` class utilizes a `companion object` to track `globalTotalBooksCreated`. This ensures that the counter is statically shared across all library instances in the system.
* **String Templates:** Extensive use of Kotlin's raw strings (`"""...""".trimIndent()`) and `buildString { }` for efficient and readable console formatting of the library catalog.

### 4.3. Android UI & System API (Hello World & System Info)
* **System Information Retrieval:** Programmatically accessed hardware and OS details using Android's `android.os.Build` (e.g., `Build.MANUFACTURER`, `Build.MODEL`).
* **UI Insets Management:** Successfully integrated the `enableEdgeToEdge()` method, applying `WindowInsetsCompat` to ensure UI components (like `TextView` and `Button`) respect the system status bar and the Pixel 9 Pro's camera cutout without overlapping.
* **Layout Qualifiers:** Implemented a `res/layout-land/activity_main.xml` file, demonstrating native Android resource resolution for landscape orientation changes without hardcoding dimensions.

### 4.4. Assisted Generation (Recipe Buddy MVP)
* **Networking Integration:** Configured asynchronous API calls to *TheMealDB* using `Retrofit.Builder()` combined with `GsonConverterFactory` to automatically parse JSON responses into Kotlin Data Classes.
* **Dynamic UI:** Implemented a `RecyclerView` paired with a custom `RecyclerView.Adapter` to efficiently display large lists of recipes. 
* **Media Handling:** Utilized third-party libraries (such as Glide or Coil) to fetch and cache high-resolution meal images from the internet directly into `ImageView` components.

## 5. Testing and Validation
* **Logic Testing:** The Virtual Library logic was validated by instantiating multiple books and attempting to borrow/return them, checking the console (or Android Logcat) to ensure correct stock calculation and error handling.
* **UI Testing:** The Android applications were tested on the Pixel 9 Pro emulator (API 35). Tests included rotating the device to ensure the `layout-land` variation was loaded correctly and verifying that the `RecyclerView` scrolled smoothly while loading internet images.

## 6. Usage Instructions
Used Software:
- IntelliJ Idea 2025.3.2
- Android Studio Panda 1

To run this project:
1. Clone the repository to your local machine.
2. Open the project in Android Studio or IntelliJ Idea, depending on what you're trying to test.
3. Allow Gradle to sync and download all dependencies (Retrofit, Gson, Glide).
4. Select the `app` module and click **Run** using an emulator with a minimum of API Level 24.
5. *Note:* To test the Virtual Library backend, check the Logcat outputs or press the configured UI buttons on the main screen.

---

# Autonomous Software Engineering Sections
*(Note: Sections 7 to 11 apply exclusively to the Section 8 MIP Application "Recipe Buddy", where the use of AI tools was explicitly permitted.)*

## 7. Prompting Strategy
I used GitHub Copilot Chat integrated into VSCode. I designed a structured prompt based on the assignment's guidelines, which included: **Context** (Expert Android Developer), **Goal** (Build "Recipe Buddy" with TheMealDB), **Constraints** (Kotlin, XML Views, Retrofit, No Jetpack Compose), **Plan** (Requesting an architectural plan before code), and **Deliverables**. 

## 8. Autonomous Agent Workflow
Instead of an automated agent like Antigravity, I used an interactive workflow with Copilot. First, the AI generated a detailed project plan proposing the MVVM structure. After I approved the plan, I prompted it sequentially to generate:
1. Gradle dependencies.
2. Data classes and Retrofit networking interfaces.
3. XML layout files.
4. `RecyclerView` Adapters and the main `Activity` logic.

## 9. Verification of AI-Generated Artifacts
Every code snippet provided by the AI was manually reviewed before being pasted into Android Studio. I verified that the API endpoints were correct (`/api/json/v1/1/search.php?s=`), ensured no deprecated methods were used, and checked the AndroidManifest.xml for the required `INTERNET` permission.

## 10. Human vs AI Contribution
* **Human Contribution:** Sections 2 through 6 (Kotlin basics, OOP logic, Virtual Library, and the initial Hello World UI configuration) were coded entirely by me. I also managed the project structure, debugging, and final integration.
* **AI Contribution:** GitHub Copilot generated the boilerplate networking code, the data classes for the API, and the base structure for the `RecyclerView` adapter in Section 8.

## 11. Ethical and Responsible Use
I ensured responsible use by critically evaluating the AI's output rather than blindly copying it. I was vigilant against "hallucinations" (e.g., the AI suggesting libraries or API endpoints that do not exist) and took care to understand the MVVM architecture it proposed so I could maintain and explain the code.

---

# Development Process

## 12. Version Control and Commit History
Git and GitHub were used extensively throughout the development process. I adhered to best practices by making small, atomic commits with descriptive messages (e.g., `feat: implement borrowBook logic`, `fix: landscape layout constraints`). This ensures a clear and trackable history of the project's evolution.

## 13. Difficulties and Lessons Learned
The main difficulties encountered were:
* Adapting to Android's modern `enableEdgeToEdge()` feature, which caused UI elements to overlap with the system status bar. I learned how to use window insets and `tools:layout_marginTop` to handle this.
* Transitioning console `println()` logic to Android UI outputs, which taught me the importance of returning `String` values from backend classes to display them in a `TextView`.

## 14. Future Improvements
For future improvements, I would like to implement a local database to save favorite recipes offline in the "Recipe Buddy" app. Additionally, adding unit tests for the Virtual Library logic would further improve the system's robustness.

## 15. AI Usage Disclosure
I used GitHub Copilot (via VSCode) to assist in generating the code for the MIP application in Section 8. I also used AI models (like Gemini) to help structure this markdown report, generate KDoc documentation, and troubleshoot Android Studio layout constraints. I confirm that I understand all the submitted code and take full responsibility for its functionality and integrity.
