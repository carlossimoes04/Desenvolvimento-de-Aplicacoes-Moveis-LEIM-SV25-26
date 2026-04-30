# Data Model

All data models are defined in the `:core` module to ensure consistency across the project.

## ImageItem
Represents an image fetched from the Picsum Photos API.

* `id: String` - The unique identifier of the image.
* `author: String` - The name of the photographer.
* `width: Int` - Original image width.
* `height: Int` - Original image height.
* `download_url: String` - The direct URL to display the image.

## UiState (Sealed Interface)
Used in ViewModels to represent the UI status:
* `Loading`: Fetching data.
* `Success`: List of `ImageItem` available.
* `Error`: Contains error message and retry logic.
