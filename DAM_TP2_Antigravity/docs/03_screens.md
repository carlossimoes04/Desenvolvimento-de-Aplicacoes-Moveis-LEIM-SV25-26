# Screens

## Screen: Main Screen (MainActivity)
**Components:**
* **Toolbar:** Displays the application title.
* **RecyclerView:** A scrollable list or grid displaying the images fetched from the Picsum Photos API.
* **Refresh Action:** A SwipeRefreshLayout wrapping the RecyclerView (or a dedicated refresh button) to reload the image feed.
* **Loading Indicator:** A ProgressBar that becomes visible while data is being fetched from the API and is hidden once the images are loaded.