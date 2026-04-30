# Screens

## Screen: Main Gallery (Common Goal)
Both `:app-xml` and `:app-compose` feature a main gallery screen, but with different implementations.

### app-xml Implementation:
* **RecyclerView:** Fixed-column grid or list.
* **Layouts:** `activity_main.xml` and `item_image.xml`.
* **Refresh:** `SwipeRefreshLayout`.

### app-compose Implementation:
* **Staggered Grid:** Images have different heights, creating a masonry-style layout.
* **Animations:** Items fade in or expand when the state updates.
* **State Management:** Uses `ComposeViewModel` and `collectAsState`.

## Screen: Image Details
Displays full-screen image preview and author metadata.
- **app-xml:** `ImageDetailsActivity`.
- **app-compose:** `ImageDetailScreen` (Composable).
