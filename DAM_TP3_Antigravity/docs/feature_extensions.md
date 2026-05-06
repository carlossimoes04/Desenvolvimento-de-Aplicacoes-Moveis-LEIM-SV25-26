# Feature Extensions (MIP-3 Evolution)

## Multi-Module Integration
The previous extensions (Details Screen, FIFO Favorites, Offline Cache) have been migrated into the multi-module structure.

* **Offline Cache:** Now lives in `:core` using Room, benefiting both UI modules.
* **FIFO Favorites:** Logic moved to `:core` repository, ensuring the same 5 favorites are synced between the XML and Compose versions.
* **Compose Adaptive Layout:** The primary new extension, providing a staggered grid that responds to screen orientation and size.
