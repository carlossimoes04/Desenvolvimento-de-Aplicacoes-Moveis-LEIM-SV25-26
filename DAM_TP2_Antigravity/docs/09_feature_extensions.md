# Feature Extensions

This document outlines the new features to be added to the Picsum Gallery application, including their descriptions, tasks, expected UI changes, and implementation plans.

## Extension 1: Image Details Screen
**Description:** A new screen dedicated to showing more detailed information about a selected image (e.g., high-resolution image, author, dimensions, and source URL).
* **Implementation Tasks:**
    * Create a new Activity or Fragment (`ImageDetailsActivity`).
    * Implement click listeners in the existing `ImageAdapter` to pass data (via Intent/Bundle) to the details screen.
    * Load the high-quality version of the image using Glide.
* **Expected UI Changes:** * New full-screen layout with a large `ImageView`.
    * `TextView` elements for metadata (Author, ID, original dimensions).
    * A back navigation button in the Toolbar.
* **Implementation Plan:**
    * Step 14: Design `activity_image_details.xml`.
    * Step 15: Create `ImageDetailsActivity.kt` and configure intent data extraction.
    * Step 16: Update `ImageAdapter.kt` in `MainActivity` to handle item clicks and navigate to the details screen.

## Extension 2: Favorite Items System (FIFO)
**Description:** A feature allowing users to favorite images. The system must use a FIFO (First-In-First-Out) queue with a strict maximum of 5 items. These items must be directly accessible from any screen.
* **Implementation Tasks:**
    * Implement a local persistence layer (Room Database or SharedPreferences) to store favorite `ImageItem` objects.
    * Implement the FIFO logic in the Repository: when adding a 6th item, the oldest item must be automatically removed.
    * Create a persistent UI component (e.g., a BottomSheet or a custom Floating Action Button menu) to show the 5 favorite images globally.
* **Expected UI Changes:**
    * A 'Heart' (favorite) icon button on each item in the `RecyclerView` and in the Details screen.
    * A persistent mini-gallery (floating or bottom bar) visible across all screens to access the 5 favorite images.
* **Implementation Plan:**
    * Step 17: Set up local storage (Room/Prefs) and implement FIFO queue logic in `ImageRepository`.
    * Step 18: Add favorite toggle buttons to UI layouts and wire them to the `MainViewModel`.
    * Step 19: Design and implement the global persistent view for the 5 favorite images.

## Extension 3: Advanced Caching & Offline Access
**Description:** Maintain a robust local cache of up to 50 items (excluding favorites). During navigation, the app must keep a sliding window of at least 10 items ahead and 10 behind the current position. Users must be able to view cached/favorite items offline.
* **Implementation Tasks:**
    * Configure Room Database to act as a Single Source of Truth (SSOT).
    * Implement a pagination/sliding window algorithm in the Repository to manage the 50-item limit and the +/- 10 items pre-fetching.
    * Configure Glide to aggressively cache downloaded images for offline rendering.
    * Intercept network state to switch seamlessly to local data.
* **Expected UI Changes:**
    * Contextual Loading Indicators (e.g., inline spinners at the bottom/top of the list) relative *only* to the specific items being fetched dynamically, rather than blocking the whole screen.
    * A Snackbar or visual cue indicating "Offline Mode" when network is lost.
* **Implementation Plan:**
    * Step 20: Create Room Entities and DAOs for the 50-item cache.
    * Step 21: Implement the sliding window logic (10 ahead/behind) and local/remote mediation in `ImageRepository`.
    * Step 22: Update UI to show contextual loading indicators specifically for the item chunks being loaded.
    * Step 23: Implement offline state detection and offline UI feedback.

## Extension 4: Robust Architecture & Graceful Error Handling
**Description:** Ensure strict adherence to the MVVM pattern across all new features and handle all API/Network errors gracefully without crashing or trapping the user.
* **Implementation Tasks:**
    * Refactor any direct UI-to-Data logic to strictly pass through the ViewModel using `LiveData` or `StateFlow`.
    * Implement a sealed class wrapper (e.g., `Result<T>` or `UiState`) to explicitly model `Success`, `Error`, and `Loading` states.
    * Catch Retrofit HTTP errors and network timeouts.
* **Expected UI Changes:**
    * Display friendly Error states (e.g., a "Retry" button layout or SnackBar) instead of blank screens when an API call fails.
* **Implementation Plan:**
    * Step 24: Create the `UiState` sealed class and update `MainViewModel` to emit these states.
    * Step 25: Update `MainActivity` to react to `UiState.Error` by showing a Retry dialog or Snackbar.