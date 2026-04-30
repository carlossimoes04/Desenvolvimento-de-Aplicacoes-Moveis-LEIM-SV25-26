# Navigation

The project implements navigation in two different ways depending on the module.

## app-xml Navigation
Uses the traditional Intent-based system:
- `MainActivity` -> `ImageDetailsActivity` via `startActivity(intent)`.

## app-compose Navigation
Uses the Jetpack Compose Navigation Component:
- `GalleryScreen` -> `DetailScreen` via `NavController.navigate("route")`.
- Arguments are passed as part of the navigation route.
