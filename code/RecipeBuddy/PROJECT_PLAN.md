# Recipe Buddy - Project Plan
## Production-Quality Android Application with MVVM Architecture

---

## 1. Project Overview

**Application Name:** Recipe Buddy  
**Target SDK:** Android 14 (API 34)  
**Min SDK:** Android 8 (API 26)  
**Architecture:** MVVM (Model-View-ViewModel)  
**UI Framework:** XML-based Views with Material Design 3  
**Language:** Kotlin  

### Core Features
- **Search Screen:** Search meals by name with a search bar and RecyclerView displaying results
- **Meal Details Screen:** Display full meal information including photo, ingredients, and instructions
- **Navigation:** Fragment-based navigation using Navigation Component

---

## 2. MVVM Architecture Overview

### Layer Responsibilities

#### **Data Layer**
- **API Service:** Retrofit-based service for TheMealDB API communications
- **Repository:** Single source of truth for data; handles API calls and caching logic
- **Models/DTOs:** Data classes representing API responses and local entities

#### **Domain Layer**
- **Use Cases:** Business logic encapsulation (future-proofing)
- **Mappers:** Convert between DTOs and UI models

#### **Presentation Layer**
- **ViewModels:** Manage UI state and handle user interactions
- **Views (Fragments & Activities):** Display UI and forward user interactions to ViewModels
- **Adapters:** RecyclerView adapters for displaying meal lists

#### **State Management**
- **StateFlow:** Reactive state management for UI observations
- **LiveData (alternative):** For compatibility and preference

---

## 3. Detailed Folder Structure

```
app/src/main/
├── java/dam_a51696/recipebuddy/
│   ├── MainActivity.kt                    # Host Activity
│   │
│   ├── di/                               # Dependency Injection (Hilt)
│   │   └── NetworkModule.kt              # Retrofit & HTTP client configuration
│   │
│   ├── data/
│   │   ├── api/
│   │   │   └── MealDbApiService.kt       # Retrofit Service interface
│   │   ├── models/                       # Data class models (DTO)
│   │   │   ├── MealResponse.kt
│   │   │   ├── MealDetailResponse.kt
│   │   │   └── IngredientModel.kt
│   │   ├── repository/
│   │   │   ├── MealRepository.kt         # Repository interface
│   │   │   └── MealRepositoryImpl.kt     # Repository implementation
│   │   └── datasource/
│   │       └── RemoteDataSource.kt       # Remote API calls
│   │
│   ├── domain/
│   │   ├── model/                        # UI-layer models
│   │   │   ├── Meal.kt
│   │   │   └── MealDetail.kt
│   │   └── usecase/
│   │       ├── SearchMealsUseCase.kt
│   │       └── GetMealDetailUseCase.kt
│   │
│   ├── presentation/
│   │   ├── viewmodel/
│   │   │   ├── SearchViewModel.kt
│   │   │   └── DetailViewModel.kt
│   │   ├── ui/
│   │   │   ├── search/
│   │   │   │   ├── SearchFragment.kt
│   │   │   │   ├── SearchAdapter.kt     # RecyclerView adapter
│   │   │   │   └── MealViewHolder.kt
│   │   │   └── detail/
│   │   │       └── DetailFragment.kt
│   │   ├── state/
│   │   │   ├── SearchUiState.kt         # State classes
│   │   │   └── DetailUiState.kt
│   │   └── navigation/
│   │       └── NavigationController.kt  # Navigation orchestration
│   │
│   └── util/
│       ├── Constants.kt
│       ├── Extensions.kt                 # Extension functions
│       └── ErrorHandler.kt
│
├── res/
│   ├── layout/
│   │   ├── activity_main.xml            # Host activity layout
│   │   ├── fragment_search.xml          # Search screen layout
│   │   ├── item_meal.xml                # RecyclerView item layout
│   │   └── fragment_detail.xml          # Detail screen layout
│   ├── values/
│   │   ├── colors.xml                   # Material Design 3 color palette
│   │   ├── dimens.xml                   # Spacing & sizing constants
│   │   ├── strings.xml                  # String resources
│   │   └── themes.xml                   # Material Design 3 theme
│   ├── values-night/
│   │   └── themes.xml                   # Dark theme
│   └── drawable/
│       ├── ic_search.xml                # Search icon
│       ├── ic_error.xml                 # Error placeholder
│       └── loading_animation.xml        # Loading animation
│
└── AndroidManifest.xml
```

---

## 4. Key Dependencies (build.gradle.kts)

### Core Android
- `androidx.core:core-ktx` - Kotlin extensions
- `androidx.appcompat:appcompat` - Backward compatibility
- `androidx.activity:activity-ktx` - Activity extensions

### Material Design 3
- `com.google.android.material:material` - Material Design 3 components

### Fragment & Navigation
- `androidx.fragment:fragment-ktx` - Fragment extensions
- `androidx.navigation:navigation-fragment-ktx` - Navigation Component
- `androidx.navigation:navigation-ui-ktx` - Navigation UI integration

### Networking
- `com.squareup.retrofit2:retrofit` - HTTP client
- `com.squareup.retrofit2:converter-gson` - JSON conversion
- `com.squareup.okhttp3:okhttp` - Interceptors and logging
- `com.squareup.okhttp3:logging-interceptor` - Network request logging

### Image Loading
- `io.coil-kt:coil` - Image loading and caching (modern, Kotlin-first alternative)

### Reactive Programming
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle-aware coroutines
- `androidx.lifecycle:lifecycle-viewmodel-ktx` - ViewModel integration
- `org.jetbrains.kotlinx:kotlinx-coroutines-android` - Android coroutines

### Dependency Injection (Optional)
- `com.google.dagger:hilt-android` - Simplified DI with Hilt
- `com.google.dagger:hilt-compiler` - Hilt annotation processor

### JSON Parsing
- `com.google.code.gson:gson` - JSON serialization/deserialization

### Testing
- `junit:junit` - Unit testing
- `androidx.test.ext:junit` - AndroidX test extensions
- `androidx.test.espresso:espresso-core` - UI testing

---

## 5. TheMealDB API Details

### Key Endpoints

#### **Search by Meal Name**
```
GET /api/json/v1/1/search.php?s={meal_name}
```
Response includes:
- `meals[].idMeal` - Meal ID
- `meals[].strMeal` - Meal name
- `meals[].strMealThumb` - Meal thumbnail image URL

#### **Get Meal Details**
```
GET /api/json/v1/1/lookup.php?i={meal_id}
```
Response includes:
- `meals[0].strMeal` - Meal name
- `meals[0].strMealThumb` - Meal image URL
- `meals[0].strInstructions` - Cooking instructions
- `meals[0].strIngredient1-20` - Ingredient names
- `meals[0].strMeasure1-20` - Ingredient measurements

### Base URL
```
https://www.themealdb.com
```

---

## 6. Navigation Strategy

### Navigation Flow
```
MainActivity
    ├── SearchFragment (Default)
    │   ├── User enters meal name in SearchView
    │   ├── RecyclerView displays search results
    │   └── User taps on meal item
    │       └── Navigate to DetailFragment (with meal ID)
    │
    └── DetailFragment
        ├── Displays full meal information
        ├── Back button/Up navigation returns to SearchFragment
        └── Search state is preserved (retained in ViewModel)
```

### Implementation Method
- **Navigation Component:** Use Safe Args for type-safe argument passing
- **Graph:** Define nav_graph.xml with two destinations (search, detail)
- **Host Activity:** Single-activity architecture with NavHostFragment

---

## 7. State Management

### Search Screen State
```kotlin
sealed class SearchUiState {
    data object Idle : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(val meals: List<Meal>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
    data object NoResults : SearchUiState()
}
```

### Detail Screen State
```kotlin
sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val meal: MealDetail) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}
```

### ViewModel State Management
- **SearchViewModel:**
  - Exposes `searchQuery: StateFlow<String>` for search input
  - Exposes `uiState: StateFlow<SearchUiState>` for UI state
  - Function: `searchMeals(query: String)`

- **DetailViewModel:**
  - Receives meal ID via arguments
  - Exposes `mealDetail: StateFlow<DetailUiState>` for UI state
  - Automatically loads meal details on initialization

---

## 8. UI/UX Design Specifications

### Material Design 3 Components

#### **Search Screen**
- `AppBarLayout` with integrated search field using `SearchView` or custom `EditText`
- `RecyclerView` with `LinearLayoutManager`
- Item card layout with:
  - Meal image (square, 120dp)
  - Meal name text
  - Ripple effect on tap
- Empty state with placeholder message
- Error state with retry button
- Loading indicator (ProgressBar or Shimmer)

#### **Detail Screen**
- `AppBarLayout` with back navigation
- Full-width meal image at top
- Scrollable content below with:
  - Meal title
  - "Ingredients" section (bulleted list)
  - "Instructions" section (scrollable text)
- Utilize `TextAppearance` for Material Design 3 typography

### Theming
- Material Design 3 dynamic color support (API 31+)
- Fallback color palette for older devices
- Light and dark theme variants in `themes.xml`

---

## 9. Error Handling & Edge Cases

### Network Errors
- **Connection Timeout:** Show user-friendly timeout message with retry button
- **Server Error (5xx):** Display generic error message with retry option
- **Client Error (4xx):** Handle empty results gracefully

### Data Validation
- Validate ingredient/measure arrays alignment
- Handle missing optional fields (some meals may lack certain ingredients)
- Parse null/empty API responses

### UI State Transitions
- Prevent multiple simultaneous requests
- Cancel in-flight requests when Fragment is destroyed
- Maintain search state during configuration changes

---

## 10. Code Quality & Best Practices

### Architecture Adherence
- ✅ Clear separation of concerns (Data, Domain, Presentation layers)
- ✅ Repository pattern for consistent data access
- ✅ ViewModel for state management and lifecycle awareness
- ✅ Sealed classes for exhaustive type safety

### Kotlin Best Practices
- ✅ Extension functions for utility operations
- ✅ Data classes for models
- ✅ Scope functions (let, apply, run) appropriately
- ✅ Null safety with `?:` and `?.let` operators

### Android Best Practices
- ✅ Fragment-based UI with Navigation Component
- ✅ Lifecycle-aware coroutines (ViewModelScope)
- ✅ Resource configuration handling
- ✅ Memory leak prevention (no context leaks in ViewModel)

### Resource Management
- ✅ Proper image caching with Coil
- ✅ Coroutine cancellation on lifecycle events
- ✅ No context references in ViewModels

---

## 11. Testability & Scalability

### Testing Architecture
- **Unit Tests:** ViewModels, Repositories, UseCases
- **Integration Tests:** API client with mock server
- **UI Tests:** Fragment navigation and user interactions

### Future Expansion Points
- Database caching (Room) for offline support
- Favorites/bookmarks feature (database + new UI screen)
- Meal filters/categories (new API endpoint)
- Share meal functionality
- User authentication (if needed)

---

## 12. Build Configuration Summary

- **buildTools & SDK:** Modern stable versions (API 34+)
- **Kotlin Version:** 1.9.x
- **Gradle Version:** 8.x
- **Proguard:** Minimal config for third-party libraries
- **Build Variants:** Debug and Release with appropriate optimization

---

## 13. Dependency Injection Strategy (Hilt)

### Modules
- **NetworkModule:** Provides Retrofit instance, OkHttpClient, MealDbApiService
- **RepositoryModule:** Provides repository implementations
- **ViewModelFactory:** Automatic ViewModel injection with Hilt

### Benefits
- Testability through easy mocking
- Centralized configuration management
- Automatic lifecycle management

---

## Approval Checklist

Before implementation, please confirm:

- [ ] Architecture (MVVM with Repository pattern) is acceptable
- [ ] Folder structure aligns with project standards
- [ ] Selected dependencies (Retrofit + Coil + Hilt) are approved
- [ ] Navigation strategy (single activity + fragments) is appropriate
- [ ] TheMealDB API endpoints are correct for intended functionality
- [ ] Material Design 3 approach matches design expectations
- [ ] State management approach (StateFlow) suits requirements
- [ ] Error handling strategy is comprehensive
- [ ] Development timeline and complexity expectations are clear

---

**Status:** Awaiting approval to proceed with implementation  
**Next Steps:** Upon approval, I will generate all Kotlin source files, XML layouts, and configuration files following this plan precisely.
