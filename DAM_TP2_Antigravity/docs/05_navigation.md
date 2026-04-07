# Navigation

The application follows a simple hierarchical navigation flow:

1. **MainActivity**
   - The entry point of the app, displaying the image grid/list.
   - Navigation target: `ImageDetailsActivity`.
   
2. **ImageDetailsActivity** (Feature Extension)
   - Accessed by clicking an item in the `RecyclerView`.
   - Allows the user to return to the `MainActivity` via the system back button or the toolbar up button.