# Implementation Plan

## Phase 1: Modular Re-structuring
* **Step 1:** Create `:core` (Library) and `:app-compose` (App) modules. Rename original module to `:app-xml`.
* **Step 2:** Configure module dependencies in `settings.gradle.kts`.

## Phase 2: Core Module Extraction
* **Step 3:** Move `ImageItem`, `ApiService`, and `ImageRepository` to `:core`.
* **Step 4:** Ensure `:core` has no UI dependencies and compiles independently.

## Phase 3: XML App Refactoring
* **Step 5:** Update `:app-xml` to use `:core` components.
* **Step 6:** Verify legacy app functionality.

## Phase 4: Compose App Development
* **Step 7:** Implement `ComposeViewModel` in `:app-compose`.
* **Step 8:** Build the masonry-style `LazyVerticalStaggeredGrid`.
* **Step 9:** Add `animateContentSize` for interactive image expansions.
* **Step 10:** Final integration and testing of both apps.
