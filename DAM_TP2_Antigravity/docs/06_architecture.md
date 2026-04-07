# Architecture

The application follows the **MVVM (Model-View-ViewModel)** design pattern to ensure a clean separation of concerns, scalability, and testability.

## Layers
The architecture is organized into the following layers:

* **View (UI):** Consists of `MainActivity` and XML layouts. It is responsible for displaying data and capturing user interactions.
* **ViewModel:** Acts as a bridge between the Model and the View. It holds the UI state and communicates with the Repository using `LiveData`.
* **Repository:** Manages data operations. It decides whether to fetch data from the network (API) or a local cache.
* **Model (API Service):** Handles the communication with the Picsum Photos API using Retrofit or a similar library to retrieve JSON data.

## Data Flow
1. The **View** observes **LiveData** from the **ViewModel**.
2. The **ViewModel** requests data from the **Repository**.
3. The **Repository** fetches data from the **API Service**.
4. Once the data is retrieved, it flows back to the **ViewModel**, which updates the **LiveData**, automatically notifying the **View**.