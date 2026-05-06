# Assignment 3 - Annotations, MVVM and Jetpack Compose

**Course:** Mobile Application Development (DAM)  
**Degree:** LEIM (Bachelor in Computer Science and Multimedia Engineering)  
**Student:** Carlos Simões, A51696, 61D  
**Teacher:** Pedro Fazenda  
**Date:** April 30, 2026  
**Repository URL:** https://github.com/carlossimoes04/Desenvolvimento-de-Aplicacoes-Moveis-LEIM-SV25-26

---

## 1. Introduction
The objective of this assignment is to explore compile-time code generation using Kotlin annotation processors and to implement modern Android architectures using MVVM and Jetpack Compose. Finally, we apply these concepts autonomously by refactoring a previous application into a multi-module architecture. The assignment is structured in three phases:
1. **Annotation Processors:** Creating custom Kotlin annotations (`@Greeting` and `@Extract`) to automate boilerplate code generation at compile-time using KotlinPoet.
2. **Cool Jetpack Weather App:** Rebuilding the Weather App (from Tutorial 2) entirely with Jetpack Compose and MVVM architecture, including the integration of Google Maps for location picking.
3. **Assisted Code Generation (MIP-3):** Refactoring the "Picsum Gallery" app into a multi-module architecture (`:core`, `:app-xml`, and `:app-compose`) with the help of AntiGravity IDE.

## 2. System Overview
The project is divided into three distinct modules:
1. **GreetingProcessorProject:** A pure Kotlin JVM project containing annotation processors to handle basic string printing (`@Greeting`) and regex-based string extraction (`@Extract`).
2. **CoolJetpackWeatherApp:** A native Android application that fetches data from Open-Meteo REST API, built using declarative UI components (Jetpack Compose). Includes an interactive Google Maps activity (`LocationPickerActivity`).
3. **DAM_TP3_Antigravity (MIP-3):** The evolution of the Picsum Gallery app, now split into a shared `:core` library (Data, Networking, and Room DB) and two separate presentation layers: an XML-based app (`:app-xml`) and a Compose-based app (`:app-compose`).

## 3. Architecture and Design
* **Annotation Processors:** Uses `AbstractProcessor` and KotlinPoet to hook into the compilation process, resolving annotated elements and generating wrapper classes dynamically.
* **CoolJetpackWeatherApp:** Adopts the **MVVM (Model-View-ViewModel)** design pattern utilizing Kotlin `StateFlow`. UI state is hoisted to the `WeatherViewModel`, decoupling the Jetpack Compose UI from data retrieval logic (Ktor client).
* **DAM_TP3_Antigravity (MIP-3):** Follows a strict multi-module design:
  * `:core`: Encapsulates all domain and data logic (Retrofit interfaces, Room Database, Repositories).
  * `:app-xml` / `:app-compose`: Presentation layers that depend solely on `:core`, ensuring pure decoupling and logic reuse.

## 4. Implementation

### 4.1. Annotation Processors
* **@Greeting Processor:** Scans for methods annotated with `@Greeting` and generates a wrapper class using composition that prints the greeting message before executing the original method.
* **Regex Processor (Optional Task):** Implemented an advanced processor that reads the `@Extract` annotation, pulling its regex pattern to automatically generate extraction logic using Kotlin's `Regex` class against a base input string.

### 4.2. Cool Jetpack Weather App
* **Declarative UI:** Translated the previous XML layouts into modular Jetpack Compose functions (`WeatherScreen`, `CoordinatesCard`, `WeatherCard`), managing states reactively.
* **Multilanguage Support:** Integrated English and Portuguese string resources natively mapped into the Compose components.
* **Location Picker (Optional Task):** Added a `LocationPickerActivity` integrated with the Google Maps SDK, allowing users to interactively drop a pin and return the precise latitude and longitude back to the Compose UI.

### 4.3. Assisted Generation (MIP-3 - Multi-module Refactor)
* **Core Module:** Extracted the data layer, including the Room Database (`AppDatabase`, `FavoriteDao`) and Retrofit endpoints, into a clean Android Library (`:core`).
* **UI Modules:** Configured gradle dependencies so that both `:app-xml` and the newly designed `:app-compose` seamlessly share the same instance of the `ImageRepository` and database.
* **Compose Exclusive Features:** Developed specific Compose layouts (`GalleryScreen.kt`) utilizing `LazyVerticalGrid` and interactive animations, proving the flexibility of declarative UI against the legacy XML adapter implementations.

## 5. Testing and Validation
* **Processor Compilation:** Validated that the annotation processor correctly triggers during the `kapt` phase, properly analyzing the Abstract Syntax Tree (AST) and outputting valid `.kt` wrapper files into the `build/generated` directory.
* **UI and MVVM Validity:** Tested `StateFlow` updates across device rotations in the Compose Weather App to ensure no state loss. The Google Maps integration was verified for correct intent resolution.
* **Multi-module Sync:** Confirmed through Gradle builds that both the XML and Compose apps successfully share the Core module without conflicting dependencies.

## 6. Usage Instructions
Software Details:
- IntelliJ Idea 2025.3.2
- Android Studio Panda 1

To run this project:
1. Clone the repository to your local machine.
2. Select the specific project to evaluate (`GreetingProcessorProject`, `code/CoolJetpackWeatherApp`, or `DAM_TP3_Antigravity`).
3. For the **GreetingProcessorProject**, build the project to view the generated classes inside `app/build/generated/source/kaptKotlin`.
4. For the **CoolJetpackWeatherApp**, ensure you have set up a valid Google Maps API Key in `local.properties` or `AndroidManifest.xml` to test the Location Picker.
5. For the **DAM_TP3_Antigravity**, you can run either the `app-xml` or `app-compose` configurations directly from Android Studio.

---

# Autonomous Software Engineering Sections
*(Note: Sections 7 to 11 apply exclusively to the Section 4 MIP Application "DAM_TP3_Antigravity", where the use of AI tools was explicitly guided by a planning-first approach.)*

## 7. Prompting Strategy
The transition into a multi-module architecture was orchestrated using AntiGravity. By feeding explicit instructions and Markdown specifications, the AI was guided to systematically decouple the previous MVP-2 codebase. The strategy relied on splitting the refactoring into distinct phases (Module Creation, Data Extraction, UI Re-linking) rather than requesting a bulk rewrite.

## 8. Autonomous Agent Workflow
1. The AI was first instructed to define the `settings.gradle.kts` and isolate the `:core` module.
2. It moved the Retrofit, Room, and Repository components into `:core`, resolving all import path conflicts.
3. The legacy `app` module was renamed to `:app-xml` and configured to implement `:core`.
4. Finally, the `:app-compose` module was spun up, utilizing AI to map the exact same ViewModel architecture into Compose UI patterns.

## 9. Verification of AI-Generated Artifacts
Following each module migration, gradle syncs and test builds were executed. Adjustments were mostly required around Gradle configuration scopes (e.g., ensuring `implementation(project(":core"))` was correctly applied) and fixing minor package import renaming issues across the legacy XML files.

## 10. Human vs AI Contribution
* **Human Contribution:** Handled the `GreetingProcessorProject` logic and the complete development of the Jetpack Compose `CoolJetpackWeatherApp`, including the Google Maps integration. Additionally, provided the high-level architectural constraints for the multi-module refactor.
* **AI Contribution:** Acted as the heavy-lifter for the boilerplate-heavy multi-module extraction in `DAM_TP3_Antigravity`. It effectively managed moving files, rewriting imports, and quickly prototyping the Jetpack Compose UI for the gallery app.

## 11. Ethical and Responsible Use
The AI was utilized purely as an architectural assistant. The design of the multi-module system was defined by human rules to guarantee no anti-patterns (such as circular dependencies) were introduced. All generated Compose layouts were reviewed to ensure they met the course's requirements for state management.

---

# Development Process

## 12. Version Control and Commit History
Git was used to track independent features. Work on the annotation processors, the Weather App rewrite, and the MIP-3 multi-module refactoring were isolated in specific project folders, allowing independent build processes and tracking.

## 13. Difficulties and Lessons Learned
* Implementing custom Kotlin Annotation Processors using `kapt` and KotlinPoet was challenging, particularly in understanding how to traverse the AST using `TypeElement` and `ExecutableElement`.
* Transitioning from XML to Jetpack Compose required a significant shift in mindset—from mutating views imperatively to observing `StateFlows` and letting the UI recompose naturally.
* Setting up Google Maps SDK required careful handling of lifecycles and API key restrictions.

## 14. Future Improvements
* Migrate the annotation processor from `kapt` to KSP (Kotlin Symbol Processing) for faster compile times and better Kotlin integration.
* Expand the `CoolJetpackWeatherApp` to display a 7-day forecast using Compose's `LazyColumn`.

## 15. AI Usage Disclosure
AntiGravity IDE was used to assist in the architectural refactoring of `DAM_TP3_Antigravity`. The AI generated the multi-module gradle setup and refactored existing code to depend on the shared core. I have reviewed, understood, and take full responsibility for the architectural choices and the resulting compiled applications.
