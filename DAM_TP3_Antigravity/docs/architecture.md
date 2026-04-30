# Architecture

The application follows the **MVVM (Model-View-ViewModel)** design pattern, strictly enforced through a **Multi-Module Project Structure**.

## Modules Overview

1. **`:core` (Shared Data & Domain Layer)**
   - **Responsibility:** Contains all business logic, data models, API clients (Retrofit), and Repositories.
   - **Dependencies:** Pure Kotlin, Networking libraries, Room Persistence. NO Android UI imports.
   
2. **`:app-xml` (Legacy UI Layer)**
   - **Responsibility:** Legacy application refactored. Uses XML Layouts and `RecyclerView`.
   - **Dependencies:** Consumes the `:core` module.

3. **`:app-compose` (Modern UI Layer)**
   - **Responsibility:** New UI built with Jetpack Compose.
   - **Dependencies:** Consumes the `:core` module.
   - **Exclusive Feature:** `LazyVerticalStaggeredGrid` with masonry layout and content animations.

## Data Flow
1. ViewModels in UI modules observe the `ImageRepository` from `:core`.
2. The Repository fetches data from the API and manages the local cache.
3. State is propagated back to the UI via `LiveData` (XML) or `StateFlow` (Compose).
