# Assignment 2 - WeatherAPP

**Course:** Mobile Application Development (DAM)  
**Degree:** LEIM (Bachelor in Computer Science and Multimedia Engineering)  
**Student:** Carlos Simões, A51696, 61D  
**Teacher:** Pedro Fazenda  
**Date:** April 7, 2026  
**Repository URL:** https://github.com/carlossimoes04/Desenvolvimento-de-Aplicacoes-Moveis-LEIM-SV25-26

---

## 1. Introduction
The objective of this assignment is to expand our knowledge of Kotlin's advanced features and implement robust Android applications adhering to modern architectural standards such as MVVM. The assignment is structured in three phases:
1. **Advanced Kotlin Exercises:** Enhancing algorithmic skills by working with Extension Functions, Higher-Order Functions, Multi-parameter Generics (Cache), Functional Pipelines, and Operator Overloading (2D Vectors).
2. **Cool Weather App:** Building an Android application that queries the Open-Meteo REST API. This phase challenges us to integrate Location Services (GPS coordinates), adapt layouts to varied formats, use dynamic XML resources, and implement the Model-View-ViewModel (MVVM) design pattern.
3. **Assisted Code Generation (MIP-2):** Creating the "Picsum Gallery" app using AntiGravity IDE. This module enforces a planning-first approach where code generation is preceded by a strict series of Markdown structural plans.

## 2. System Overview
The system consists of three distinct modules reflecting the assignment's structure:
1. **Kotlin Advanced Algorithms:** A collection of console applications in `DAM_TP2` addressing complex data structures. This includes event log pipelines, an in-memory generic cache system, and mathematical 2D vector operations.
2. **Cool Weather App:** A robust native Android app that requests real-time weather metrics (pressure, wind speed, temperature) based on the device's geographical coordinates (using the `LocationManager` API) and applies adaptive themes based on local time and weather conditions.
3. **Picsum Gallery (MIP-2 - Assisted Code Generation):** An AI-generated, fully functional gallery app that pulls data from the Picsum API. The app is guided entirely by markdown descriptions under the `docs/` folder, ensuring a solid architecture (MVVM, Retrofit, Room Database).

## 3. Architecture and Design
* **Kotlin Exercises:** Showcases pure Object-Oriented and Functional capabilities of Kotlin, focusing on code reusability using Generics and functional programming with Higher-Order functions. 
* **Cool Weather App:** Uses the **MVVM (Model-View-ViewModel)** pattern. The `WeatherViewModel` abstracts the API fetching logic and maintains the `LiveData` streams. The `MainActivity` dynamically adjusts themes (Day/Night) and reacts to the orientation.
* **Picsum Gallery (MIP-2):** Follows a strict MVVM setup combined with the Repository pattern. It uses Retrofit for networking, Room for persistence (Favorites), and is bound to `UiState` mappings to safely manage loading, success, and error states across the UI.

## 4. Implementation

### 4.1. Advanced Kotlin Exercises
* **Functional Programming:** Used `filterIsInstance` and custom Higher-Order functions to route event models efficiently. 
* **Generics:** Built a type-safe `Cache<K, V>` using mutable structures and features like `getOrPut` to handle dynamic memory indexing.
* **Pipelines:** Designed configurable string manipulation pipelines, showing an understanding of lazy transformations. 
* **Operator Overloading:** Developed the `Vec2` data class integrating `operator fun plus()`, `times()`, and `compareTo()` methods to treat 2D coordinate objects as native numerics.

### 4.2. Android Weather Application (CoolWeatherAPP)
* **Location and Permissions:** Used the native Android `LocationManager` to extract accurate Coordinates via `GPS_PROVIDER` and `NETWORK_PROVIDER`, with integrated requests for `ACCESS_FINE_LOCATION` Runtime Permissions.
* **Networking and JSON Parsing:** Configured API calls toward Open-Meteo, using background threads to prevent UI freezes. JSON responses are safely parsed using the Gson library directly into predefined Data Classes (`WeatherData`).
* **UI Adaptability & Theming:** Adopted responsive dimensions combining `WindowInsetsCompat` to adapt to screen edges, alongside completely separated `styles` handling Light and Dark themes natively triggered when comparing sunrise times with the current local time.

### 4.3. Assisted Generation (MIP-2 - Picsum Gallery)
* **Design Pattern and Networking:** Achieved a well-structured MVVM architecture employing `Retrofit2` interfaces for efficient network requests, alongside the `Glide` library to securely buffer visual image components to prevent stutter.
* **Database Caching:** Created a dynamic Room DB with `AppDatabase`, `FavoriteDao` and `CacheDao` to securely maintain cached versions of images and save long-term persistent settings across multiple app cycles.

## 5. Testing and Validation
* **Logic Checking:** Executed deep test variations mapping string and numerical values over the Cache interface. Output formatting was evaluated directly inside the compiler logcat.
* **UI and MVVM Validity:** Validated `LiveData` responses rotating the layout constantly (triggering configuration changes) ensuring that there are no data losses or overlapping elements on rotating the android emulator device (Tested on API 35). Simulated GPS overrides in the device extended controls verified the location pipeline.

## 6. Usage Instructions
Software Details:
- IntelliJ Idea 2025.3.2
- Android Studio Panda 1

To run this project:
1. Clone the repository to your local machine.
2. Select the particular module to evaluate (`DAM_TP2`, `code/CoolWeatherAPP` or `DAM_TP2_Antigravity`).
3. If running `CoolWeatherAPP`, make sure you provide the Location permissions when prompted by the app to test the GPS fetching capabilities. For `Picsum Gallery`, press the refresh icon to pull live images.

---

# Autonomous Software Engineering Sections
*(Note: Sections 7 to 11 apply exclusively to the Section 3 MIP Application "Picsum Gallery", where the use of AI tools was explicitly guided by a planning-first approach.)*

## 7. Prompting Strategy
The project was driven strictly by a planning-first strategy leveraging Markdown Structural Files (`docs/01_overview.md` to `docs/08_implementation_plan.md`). By specifying the exact behavior expected statically rather than conversing loosely in ad-hoc prompting, the AI was given rigid constraints defining MVVM usage, Room configurations, Retrofit patterns and RecyclerViews implementation scopes. 

## 8. Autonomous Agent Workflow
Through AntiGravity, the workflow was structured step-by-step:
1. We formalized the app requirements defining precise endpoints (`https://picsum.photos/v2/list`).
2. AntiGravity generated the structured architecture and parsed the expected implementation roadmap in `docs/08_implementation_plan.md`. 
3. Code blocks (Repositories, the Main View, Item views) were produced only following approval of the design constraints.

## 9. Verification of AI-Generated Artifacts
Following code execution and deployment by AntiGravity, local testing confirmed integration. Minor adjustments and code verifications were required mainly to ensure the API serialization mapped the raw JSON fields accurately to the internal Kotlin data classes and ensuring async coroutines operated efficiently within the `viewModelScope`.

## 10. Human vs AI Contribution
* **Human Contribution:** Addressed Sections 2 through 6 which mapped out complex functional algorithm structures (Cache, Pipelines, Vectors) alongside building the core framework logic for the `CoolWeatherApp` to interact explicitly with the `LocationManager`.
* **AI Contribution:** Handled the generation of boilerplate and logical connections inside the MVP-2 "Picsum Gallery". It successfully created the `Room` database initialization, MVVM logic (`LiveData` & State management) and `Retrofit` JSON abstractions based solely on the provided Markdown guidelines.

## 11. Ethical and Responsible Use
Maintaining full authority over the process meant no black-box code was accepted blindly. Deep code inspections were conducted on database definitions and error-handling paths. AI output served reliably strictly as an implementation compiler rather than a creative substitution, proving the value of having the engineer securely in control of the high-level specifications.

---

# Development Process

## 12. Version Control and Commit History
Git and GitHub were leveraged as source control strategies, involving pushing specific features and assignments individually into branches preventing logical overlap. Modular separations divided core Kotlin training from the heavier UI application workloads ensuring clear historical tracking.

## 13. Difficulties and Lessons Learned
A significant learning curve existed within managing standard GPS integrations (`ACCESS_FINE_LOCATION`) alongside runtime requests cleanly without locking the UI Thread in the `CoolWeatherApp`. 
Additionally, transitioning the architecture logically into ViewModel mapping proved valuable in guaranteeing states remained secure and resilient through configuration changes (device rotation), solidifying comprehension on application lifecycles.

## 14. Future Improvements
* The implementation of proper generic error wrappers in the advanced Kotlin code could increase modular robustness.
* Enhancing "Picsum Gallery" to include continuous scrolling paging (Infinite Scrolling) by updating the network endpoints combined with a `Paging3` library implementation.

## 15. AI Usage Disclosure
AntiGravity IDE was formally used to create a markdown-guided auto-generation pipeline for the `DAM_TP2_Antigravity` module. The AI successfully generated structural layouts, local databasing, and networking logic adhering strictly to the pre-approved functional constraints described via Markdown tasks. I confirm complete understanding and comprehension of the structures across the entirety of this ecosystem and take full responsibility for its operation.
