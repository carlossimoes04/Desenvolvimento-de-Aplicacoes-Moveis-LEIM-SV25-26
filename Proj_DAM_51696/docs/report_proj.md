# Final Project - Pantry Chef

**Course:** Mobile Application Development (DAM)  
**Degree:** LEIM (Bachelor in Computer Science and Multimedia Engineering)  
**Student:** Carlos Simões, A51696, 61D  
**Teacher:** Pedro Fazenda  
**Date:** June 12, 2026  
**Repository URL:** https://github.com/carlossimoes04/Desenvolvimento-de-Aplicacoes-Moveis-LEIM-SV25-26

---

## 1. Introduction
The final project for the Mobile Application Development (DAM) course is **Pantry Chef**, a comprehensive native Android application designed to help users manage their home pantry, track ingredient expiration dates, and discover recipes based on what they already possess. 

This project represents the culmination of all knowledge acquired throughout the semester. It abandons simple local memory storage and legacy XML in favor of a full modern Android tech stack: Jetpack Compose for declarative UI, cloud synchronization via Firebase, REST API consumption, and robust Clean Architecture patterns.

## 2. System Overview
Pantry Chef is built to be a responsive, edge-to-edge Android application with the following core modules:
1. **User Authentication:** Secure registration and login systems.
2. **Pantry Management:** Real-time tracking of ingredients, categorized automatically by urgency (Expired, Expiring Soon, Good to Go).
3. **Smart Recipe Discovery:** Integration with *TheMealDB* API to suggest recipes that prioritize ingredients nearing their expiration dates, reducing food waste.
4. **Shopping List:** A reactive checklist where users can manually add items or automatically transfer missing ingredients from a recipe directly to the cart.
5. **Favorites:** A persistent collection of the user's preferred recipes.

## 3. Architecture and Package Organization
The project strictly adheres to **Clean Architecture** principles and the **MVVM (Model-View-ViewModel)** design pattern. The source code is systematically organized to ensure high decoupling, testability, and scalability.

Inside `dam_A51696.pantrychef`, the codebase is divided into three main layers:

### 3.1. `domain/` (The Core)
Contains the fundamental business rules. It is entirely independent of any Android framework, UI, or database.
* **`model/`**: Plain Kotlin data classes representing core entities (`Ingredient`, `Recipe`, `ShoppingItem`).
* **`repository/`**: Interfaces defining the required data operations (Contracts). It dictates *what* data is needed, but not *how* to get it.
* **`usecase/`**: Classes containing specific business logic actions (e.g., `GetExpiringIngredientsUseCase`, `AddMissingIngredientsUseCase`).

### 3.2. `data/` (The Infrastructure)
The implementation of the data layer. It decides *how* and *where* data is fetched or saved.
* **`repository/`**: Concrete implementations of the interfaces defined in the domain (e.g., `ShoppingRepositoryImpl`).
* **`remote/`**: Code related to external APIs and Firebase. Includes Data Transfer Objects (`dto/`) and mapping functions (`toDomain()`) to translate network data into safe domain models.

### 3.3. `presentation/` (The UI Layer)
Contains everything the user interacts with (Jetpack Compose functions and ViewModels).
* **`auth/`, `pantry/`, `recipes/`, `shopping/`, `favorites/`, `search/`**: Feature-based folders. Each contains its respective `ViewModel` and Compose `Screen`. Grouping by feature rather than by layer (e.g., all screens together) makes navigation highly intuitive.
* **`navigation/`**: Centralized routing logic (`AppNavigation`).
* **`theme/`**: Global styling, color palettes, and typography (`Color.kt`, `Theme.kt`).

**Why this organization?** 
This architecture forces a one-way dependency rule. The UI (Presentation) depends on Domain, and the Data depends on Domain. By separating concerns, the UI never handles heavy logic. If the data source needs to be swapped in the future (e.g., migrating from Firebase to a local Room Database), changes will only occur in the `data/` package, without breaking a single line of code in the `domain/` or `presentation/` packages.

## 4. Implementation and Dependencies
To achieve a professional-grade application, several industry-standard external libraries were utilized in the `build.gradle.kts`. Each plays a critical role in the system:

* **Jetpack Compose (`androidx.compose.*`)**
  * **Where:** Used universally in the `presentation` layer.
  * **Why:** Replaces legacy XML layouts. It allows the UI to be built dynamically and react instantly to state changes using a declarative Kotlin syntax.
* **Dagger-Hilt (`com.google.dagger:hilt-android`)**
  * **Where:** Used across all layers (e.g., `@HiltViewModel` in presentation, `@Inject` in data/domain, and `@HiltAndroidApp` in the Application class).
  * **Why:** Manages **Dependency Injection (DI)**. It automatically provides instances of Repositories and UseCases directly to the ViewModels, eliminating manual class instantiations, reducing boilerplate, and making the app modular.
* **Firebase (`com.google.firebase:firebase-bom`)**
  * **Where:** Implemented in the `data/remote/` package.
  * **Why:** Serves as the remote backend. **Firebase Auth** securely manages user sessions. The **Realtime Database** synchronizes user data (pantry stock, shopping lists) across devices instantly, utilizing `ValueEventListeners` converted to Kotlin `Flow` for real-time reactivity.
* **Retrofit2 & Gson (`com.squareup.retrofit2:retrofit`)**
  * **Where:** Implemented in the `data/remote/` package.
  * **Why:** Handles HTTP REST requests to the public *TheMealDB* API. Retrofit provides a clean interface for API calls, while Gson automatically deserializes complex JSON responses into usable Kotlin DTOs.
* **Coil (`io.coil-kt:coil-compose`)**
  * **Where:** Used in the UI layer (e.g., `RecipeGridCard.kt`, `RecipeDetailScreen.kt`).
  * **Why:** An image-loading library explicitly built for Kotlin and Compose. It fetches, caches, and displays high-resolution food images from URLs asynchronously without blocking the main UI thread.
* **Jetpack Navigation Compose (`androidx.navigation:navigation-compose`)**
  * **Where:** Implemented in the `presentation/navigation/` package.
  * **Why:** Manages the screen flow, deep linking, and the bottom navigation bar seamlessly within the Jetpack Compose ecosystem.

## 5. Autonomous Software Engineering & UI Design
This project strongly embraced AI-assisted engineering to bridge the gap between design and implementation.

* **High-Fidelity UI Translation:** An AI coding assistant (Antigravity) was strictly utilized to construct the Jetpack Compose interfaces so they would perfectly match the initial mockup designs provided in the PDF (`Project Proposal - A51696 - Pantry Chef.pdf`). By feeding the AI with detailed visual constraints from the PDF, it successfully generated complex structural elements—such as the edge-to-edge scaffolding, custom filtering chips, the specific cream background, and dynamic color-coded expiration tags—ensuring the final product was visually identical to the proposed UI/UX concept.

## 6. Testing and Validation
* **State Management Validity:** Tested the MVVM architecture across multiple configuration changes (device rotation, backgrounding). Thanks to `StateFlow`, no data or loading states are lost during recomposition.
* **Data Reactivity:** Verified that adding, editing, or deleting an ingredient in the `PantryScreen` updates the visual list immediately without requiring a manual refresh, confirming the efficiency of the Firebase-to-Flow pipeline.

## 7. Usage Instructions
1. Clone the repository and open it in Android Studio.
2. Ensure you have an active internet connection, as the app requires Firebase connectivity and API access.
3. Select the `app` module and Run it on an emulator or physical device (API 24+).
4. Create a new account on the initial screen to gain access to the secure database and start managing your digital pantry.

## 8. Difficulties and Lessons Learned
* **Firebase Data Serialization:** A notable difficulty was resolving serialization conflicts between Kotlin and Firebase (e.g., Firebase truncating the Kotlin variable `isBought` to simply `bought` in the database). This taught me the importance of creating distinct `FirebaseDto` classes and mapping them securely to Domain models using explicit `toDomain()` mapping functions.
* **UI Event Absorption:** Addressed issues in Jetpack Compose where touch events were being absorbed by parent `Card` containers instead of specific action buttons (like the toggle checkmark in the Shopping List), requiring a deeper understanding of Modifier stacking and `IconButton` boundaries.

## 9. Future Improvements
* Implement a Barcode/QR Scanner (via Google ML Kit) to quickly add new grocery items to the pantry.
* Implement background workers using `WorkManager` (already included in dependencies) to trigger Push Notifications proactively a few days before an ingredient is set to expire.

## 10. AI Usage Disclosure
Antigravity was actively used during the development of this project. Antigravity was specifically utilized to assist with implementation in situations where I had more doubts or faced complex challenges. It was highly instrumental in helping to build the Jetpack Compose UI so that it would be identical to the visual identity outlined in the PDF project proposal. Furthermore, Antigravity was used to generate the comprehensive KDocs for the classes and functions. However, the line-by-line comments explaining the core logic throughout the codebase were written entirely by me. All generated code was thoroughly reviewed, adapted to respect Clean Architecture principles, and manually tested to ensure absolute control and understanding of the application's behavior.
