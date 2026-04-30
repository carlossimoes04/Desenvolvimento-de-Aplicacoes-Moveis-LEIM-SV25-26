package dam_a51696.recipebuddy

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for the RecipeBuddy app.
 *
 * This class is annotated with @HiltAndroidApp to trigger Hilt's code generation,
 * which serves as the application-level dependency container.
 */
@HiltAndroidApp
class RecipeBuddyApp : Application()
