# Features

## Shared Features (via `:core`)
1. **API Integration:** Fetch random images from the Picsum Photos API.
2. **Data Caching:** Local persistence of image metadata (Room/DataStore).
3. **Repository Pattern:** Centralized data access for both UI modules.

## app-xml Features
1. **Legacy Gallery:** Display images in a standard scrollable list using `RecyclerView`.
2. **XML Layouts:** UI defined in traditional XML resource files.
3. **Swipe-to-Refresh:** Traditional implementation of list refreshing.

## app-compose Features (Exclusive)
1. **Adaptive Staggered Grid:** Display images in a dynamic grid with varying aspect ratios (`LazyVerticalStaggeredGrid`).
2. **Smooth Animations:** Interactive entry and size animations using `animateContentSize`.
3. **Declarative UI:** Modern state-driven interface built with Jetpack Compose.
