# Local Events Explorer

A native Android application built with **Kotlin, Jetpack Compose, Clean Architecture, MVVM, Room, and Hilt**. The application allows users to explore nearby events, view event details, save their favourite events, and navigate to event locations using Google Maps.

## Features

### 1. Nearby Events
- Browse a list of available events.
- View event images, titles, locations, and date/time information.
- Open an event to see its detailed information.
- Display event information in a card-based UI.

### 2. Saved Events
- Bookmark events to save them for later.
- Remove bookmarks by tapping the favourite icon again.
- View all bookmarked events in a dedicated Saved Events screen.
- Persist bookmark status locally using Room.

### 3. Event Details
- View event images and complete event information.
- Display formatted event dates and times.
- Request location permission when opening the event details screen.
- Calculate the distance between the user's current location and the event location.
- Handle location permission denial and provide an option to grant permission through app settings.
- Open the event location in an external Maps application.

### 4. Offline Support
- Store event information locally using Room Database.
- Load initial event data from a bundled JSON asset.
- Display locally stored events without requiring a network connection.
- Preserve existing bookmark status when refreshing event data.

### 5. UI State Management
The application uses a reusable sealed UI state to handle different screen states:

- **Loading:** Displays a progress indicator while data is being loaded.
- **Success:** Displays the requested event information.
- **Error:** Displays an error message and retry option.
- **Empty:** Displays an appropriate message when no events are available.

## Technology Stack

| Category | Technologies |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Design System | Material 3 |
| Architecture | Clean Architecture, MVVM |
| Dependency Injection | Hilt |
| Local Database | Room |
| Asynchronous Programming | Kotlin Coroutines, Flow, StateFlow |
| Navigation | Navigation Compose |
| Image Loading | Coil |
| Location | Google Play Services Location |
| Testing | JUnit, AndroidX Test, Room Testing, Coroutines Test |

## Architecture

The project follows Clean Architecture with separate Gradle modules for the application, domain, and data layers.

```text
LocalEventsExplorer/
│
├── app/
│   └── src/main/java/com/android/events/
│       ├── details/
│       ├── events/
│       ├── navigation/
│       ├── ui/
│       │   └── state/
│       ├── utils/
│       └── MainActivity.kt
│
├── domain/
│   └── src/main/java/com/android/events/domain/
│       ├── model/
│       ├── repository/
│       └── usecase/
│
└── data/
    └── src/main/
        ├── assets/
        │   └── events.json
        └── java/com/android/events/data/
            ├── di/
            ├── local/
            │   ├── dao/
            │   ├── database/
            │   └── entity/
            ├── location/
            ├── mapper/
            ├── remote/
            │   └── datasource/
            └── repository/
```

### Presentation Layer (app)

Responsible for the user interface and presentation logic.

- Jetpack Compose screens.
- ViewModels for managing UI state.
- Navigation between Nearby Events, Saved Events, and Event Details.
- Reusable `UiState` for Loading, Success, and Error handling.

### Domain Layer (domain)

Contains the application's core business logic and is independent of Android framework and data-source implementations.

**Models**
- `Event`
- `UserLocation`

**Repository Contracts**
- `EventRepository`
- `LocationRepository`

**Use Cases**
- `GetEventsUseCase`
- `GetBookmarkedEventsUseCase`
- `GetEventByIdUseCase`
- `ToggleBookmarkUseCase`
- `RefreshEventsUseCase`
- `GetCurrentLocationUseCase`

### Data Layer (data)

Responsible for providing and managing application data.

- Room Database for local persistence.
- DAO interfaces for database operations.
- Entity-to-domain and domain-to-entity mapping.
- Local JSON data source for initial event data.
- Repository implementations.
- Location data source using Google Play Services Location.
- Hilt modules for dependency injection.

## Data Flow

The application follows a unidirectional data flow between the UI, ViewModel, domain, and data layers.

```text
Compose UI
    |
    v
ViewModel
    |
    v
Use Case
    |
    v
Repository Interface
    |
    v
Repository Implementation
    |
    +------------------+
    |                  |
    v                  v
Room Database      JSON Data Source
    |
    v
Domain Model
    |
    v
StateFlow / UiState
    |
    v
Compose UI
```

For location-related functionality:

```text
Event Detail Screen
        |
        v
Location Permission
        |
        v
EventDetailViewModel
        |
        v
GetCurrentLocationUseCase
        |
        v
LocationRepository
        |
        v
LocationDataSource
        |
        v
Google Play Services Location
```

## Database Design

The application uses Room with an `EventEntity` table.

| Field | Type | Description |
|---|---|---|
| id | String | Unique event identifier |
| title | String | Event title |
| location | String | Event location |
| time | String | Event date and time |
| imageUrl | String | Event image URL |
| latitude | Double | Event latitude |
| longitude | Double | Event longitude |
| isBookmarked | Boolean | Bookmark status |

### Bookmark Persistence

Bookmark status is stored in the database.

When an event is bookmarked, its `isBookmarked` value is updated. The Saved Events screen observes bookmarked events through a Room Flow.

### Refresh Strategy

When event data is refreshed:

1. Existing database events are retrieved.
2. Existing bookmark statuses are indexed by event ID.
3. Updated event information is mapped to database entities.
4. Existing bookmark statuses are preserved for matching event IDs.
5. Existing records are replaced inside a Room transaction.

This ensures that refreshing event information does not unintentionally remove saved events.

## Location and Navigation

The application uses Google Play Services Location to retrieve the user's current location.

- Requests fine and coarse location permissions at runtime.
- Supports approximate location when precise location is unavailable.
- Calculates the distance between the current location and an event using geographic coordinates.
- Uses an Android geo URI to launch an external Maps application.

Location access is optional. Users can still browse events without granting location permission.

## UI State Management

A generic sealed interface is used to represent screen states:

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>

    data class Success<T>(
        val data: T
    ) : UiState<T>

    data class Error(
        val message: String
    ) : UiState<Nothing>
}
```

This approach provides predictable state handling and separates UI rendering from business logic.

## Dependency Injection

Hilt is used for dependency injection.

- Repository implementations are bound to domain repository interfaces.
- Room database and DAO dependencies are provided through Hilt modules.
- ViewModels receive use cases through constructor injection.
- Application-scoped dependencies are managed through Hilt components.

## Getting Started

### Prerequisites

- Android Studio
- Android SDK
- JDK compatible with the project's Gradle configuration
- Android device or emulator
- Location services enabled for testing location-based features

### Clone the Repository

```bash
git clone https://github.com/jyomin26/Events
```

### Open the Project

1. Open the project in Android Studio.
2. Allow Gradle to sync.
3. Build the project.
4. Run the `app` configuration on an emulator or Android device.

### Permissions

The application requires the following location permissions:

```xml
<uses-permission
    android:name="android.permission.ACCESS_FINE_LOCATION" />

<uses-permission
    android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

Location permission is requested at runtime when accessing event details.

## Testing

The project includes a testing approach for validating core application logic.

### Repository Tests

Repository tests use an in-memory Room database to verify database and repository behavior.

Key test scenarios include:

- Retrieving all events.
- Retrieving bookmarked events.
- Retrieving an event by ID.
- Handling events that do not exist.
- Toggling bookmark status.
- Refreshing event data while preserving existing bookmarks.

### Running Instrumentation Tests

```bash
./gradlew :data:connectedDebugAndroidTest
```

Run the tests on an emulator or connected Android device.

## Design Principles

The application follows these principles:

- Separation of concerns through Clean Architecture.
- Single responsibility for individual components.
- Dependency inversion through repository interfaces.
- Constructor injection using Hilt.
- Reactive data observation using Kotlin Flow.
- Lifecycle-aware state management.
- Transactional database updates.
- Reusable UI state handling.
- Testable business logic.
